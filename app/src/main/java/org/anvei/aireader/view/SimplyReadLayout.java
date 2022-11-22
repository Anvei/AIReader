package org.anvei.aireader.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.NonNull;

import org.anvei.aireader.R;
import org.anvei.aireader.view.splitter.ChapterPageSplitter;
import org.anvei.aireader.view.splitter.ChapterSplitter;
import org.anvei.aireader.view.splitter.FileEncodingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimplyReadLayout extends ReadLayout {

    public static final String TAG = "SimplyReadLayout";

    public static final int CHILD_COUNT = 3;

    public interface LoadListener {

        // 加载成功回调
        void onSuccess(int chapterCount);

        // 加载失败回调
        void onFailed();

    }

    private final ChapterProviderImp chapterProvider;

    private LoadListener loadListener;

    // 内部保存了上一章、当前章节、下一章节的分页信息
    private final List<String> pages = new ArrayList<>();

    private int chapterCount;

    private GestureDetector gestureDetector;
    private Scroller scroller;
    private static final int minScrollDistance = 40;

    public SimplyReadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        chapterProvider = new ChapterProviderImp(1, 1);
        init();
    }

    private final View[] views = new View[CHILD_COUNT];
    private final SimpleReadPage[] readPages = new SimpleReadPage[CHILD_COUNT];
    private int curViewPointer;

    private static final int SELECTED_PRE = 0x01;
    private static final int SELECTED_NEXT = 0x02;
    private static final int SELECTED_NONE = 0x03;
    private int selectedView = SELECTED_NONE;

    private float start = -1;

    private void init() {
        // 最先addView()的在最底层、即next view在最底层
        for (int i = 0; i < views.length; i++) {
            views[i] = generatePage();
            readPages[i] = views[i].findViewById(R.id.view_simple_read_page);
            addView(views[i]);
        }
        curViewPointer = 1;             // 采用循环队列的方法进行设置，该指针指向当前显示的View
        scroller = new Scroller(getContext());
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // 选中一个view，在一组滑动操作中，只能操作同一个View
                Log.d(TAG, "onScroll: onScroll hasPre: " + hasPrePage());
                if (selectedView == SELECTED_NONE) {
                    if (distanceX > 0 && hasNextPage()) {
                        selectedView = SELECTED_NEXT;
                        start = e2.getX();
                    } else if (distanceX < 0 && hasPrePage()) {
                        selectedView = SELECTED_PRE;
                        start = e2.getX();
                    }
                }
                switch (selectedView) {
                    case SELECTED_NONE:
                        return false;
                    case SELECTED_PRE: {
                        getPreView().scrollBy((int) distanceX, 0);
                        break;
                    }
                    case SELECTED_NEXT: {
                        getCurView().scrollBy((int) distanceX, 0);
                        break;
                    }
                }
                return true;
            }

        });
    }

    private void nextPagePointer() {
        curViewPointer = (curViewPointer - 1 + CHILD_COUNT) % CHILD_COUNT;
    }

    private void prePagePointer() {
        curViewPointer = (curViewPointer + 1) % CHILD_COUNT;
    }

    private View getCurView() {
        return views[curViewPointer];
    }

    private View getPreView() {
        return views[(curViewPointer + 1) % CHILD_COUNT];
    }

    private View getNextView() {
        return views[(curViewPointer - 1 + CHILD_COUNT) % CHILD_COUNT];
    }

    private static final int PRE_READ_PAGE = 0x01;
    private static final int CUR_READ_PAGE = 0x02;
    private static final int NEXT_READ_PAGE = 0x03;

    private SimpleReadPage getReadPage(int id) {
        int index;
        switch (id) {
            case PRE_READ_PAGE:
               index = (curViewPointer + 1) % CHILD_COUNT;
                break;
            case NEXT_READ_PAGE:
                index = (curViewPointer - 1) % CHILD_COUNT;
                break;
            default:
                index = curViewPointer;
        }
        return readPages[index];
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            View view;
            if (scrollingView == SELECTED_NEXT) {
                view = getCurView();
            } else if (scrollingView == SELECTED_PRE) {
                view = getPreView();
            } else {
                return;
            }
            view.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        } else {
            switch (scrollingView) {
                case SELECTED_PRE:
                    prePagePointer();
                    invalidate();
                    break;
                case SELECTED_NEXT:
                    nextPagePointer();
                    invalidate();
                    break;
            }
            scrollingView = SELECTED_NONE;
        }
    }

    private int scrollingView;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean res = gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (start == -1)
                    break;
                scrollingView = selectedView;
                selectedView = SELECTED_NONE;
                float distance = start - event.getX();
                start = -1;
                // 右滑状态，控制页面完成完整页面的滑动
                Log.d(TAG, "onTouchEvent: distance = " + distance);
                if (distance > 0) {
                    // 如果没有达到触发翻页的最小宽度，就退回当前页面未移动的状态
                    if (distance < minScrollDistance) {
                        scroller.startScroll(getCurView().getScrollX(), getCurView().getScrollY(),
                                -getCurView().getScrollX(), getCurView().getScrollY());
                    } else {        // 翻页
                        scroller.startScroll(getCurView().getScrollX(), getCurView().getScrollY(),
                                getCurView().getWidth() - getCurView().getScrollX(),
                                getCurView().getScrollY());
                    }
                    invalidate();
                } else {
                    Log.d(TAG, "onTouchEvent: pre");
                    if (distance < minScrollDistance) {
                        scroller.startScroll(getPreView().getScrollX(), getPreView().getScrollY(),
                                -getPreView().getScrollX(), getPreView().getScrollY());
                    } else {
                        scroller.startScroll(getPreView().getScrollX(), getPreView().getScrollY(),
                                getPreView().getWidth() - getPreView().getScrollX(),
                                getPreView().getScrollY());
                    }
                }
                break;
        }
        return res;
    }

    public boolean hasPrePage() {
        return chapterProvider.hasPrePage();
    }

    public boolean hasNextPage() {
        return chapterProvider.hasNextPage();
    }

    private View generatePage() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_read_page, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // 设置view的测量大小
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            // 子view的MeasureSpec和容器相同
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount != CHILD_COUNT) {
            throw new IllegalStateException("childCount必须为3!");
        }
        // 依次叠放、先layout()的在下面
        getNextView().layout(0, 0, getNextView().getMeasuredWidth(), getNextView().getMeasuredHeight());
        getCurView().layout(0, 0, getCurView().getMeasuredWidth(), getCurView().getMeasuredHeight());
        getPreView().layout(0, 0, getPreView().getMeasuredWidth(), getPreView().getMeasuredHeight());
        // 让preView显示在屏幕之外
        getPreView().scrollTo(getWidth(), 0);
    }


    /**
     * 进行一次实验性测量，以初始化maxDisplayLines，charCountPerLine大小
     */
    private void initDemiInfo() {
        getReadPage(NEXT_READ_PAGE).startOnceDemiListen((maxDisplayLines, charCountPerLine) -> {
            chapterProvider.setChapterPageSplitter(new ChapterPageSplitter(maxDisplayLines, charCountPerLine));
            initProvider();
        });
    }

    /**
     * 初始化其他页面的文字显示，该函数会在准确的显示信息测量以后调用 <br/
     * 该函数会完成chapterProvider的初始化操作 <br/>
     * chapterProvider必须在设置完chapterSplitter、chapterPageSplitter之后 <br/>
     * 再调用initProvider之后才能正常工作 <br/>
     */
    private void initProvider() {
        new Thread(() -> {
            try {
                chapterProvider.initProvider();
                sendMsg(INIT_PAGE_TEXT);
            } catch (ChapterInitException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 在该函数内初始化进入该页面时需要显示的内容（上一页、下一页、当前页）
    private void initPageText() {
        getReadPage(CUR_READ_PAGE).setText(chapterProvider.curPage());
        if (hasPrePage()) {
            getReadPage(PRE_READ_PAGE).setText(chapterProvider.prePage());
            chapterProvider.nextPage();
        }
        if (hasNextPage()) {
            getReadPage(NEXT_READ_PAGE).setText(chapterProvider.nextPage());
            chapterProvider.prePage();
        }
    }

    // Handler消息标识
    public static final int INIT_DEMI_INFO = 0x01;
    public static final int LOAD_LISTENER_SUCCESS = 0x02;
    public static final int LOAD_LISTENER_FAILED = 0x03;
    public static final int INIT_PAGE_TEXT = 0x04;

    private final Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case INIT_DEMI_INFO:
                    initDemiInfo();
                    return true;
                case LOAD_LISTENER_SUCCESS:
                    if (loadListener != null) {
                        loadListener.onSuccess(chapterCount);
                    }
                    return true;
                case LOAD_LISTENER_FAILED:
                    if (loadListener != null) {
                        loadListener.onFailed();
                    }
                    return true;
                case INIT_PAGE_TEXT:
                    initPageText();
                    return true;
            }
            return false;
        }
    });

    // 发送一个消息
    private void sendMsg(int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    /**
     * 可以在onCreate()中调用
     */
    @Override
    public void loadFile(File file) throws FileNotFoundException {
        ChapterSplitter chapterSplitter = new ChapterSplitter(file);
        new Thread(() -> {
            try {
                chapterProvider.setChapterSplitter(chapterSplitter);
                // 切割章节
                chapterSplitter.startSplit();
                chapterCount = chapterSplitter.getChapterSplitResultCount();
                sendMsg(INIT_DEMI_INFO);
                sendMsg(LOAD_LISTENER_SUCCESS);
            } catch (FileEncodingException | IOException e) {
                e.printStackTrace();
                sendMsg(LOAD_LISTENER_FAILED);
            }
        }).start();
    }

    @Override
    public void loadText(String string) {

    }

    @Override
    public void loadFromInternet(InternetNovelLoader novelLoader) {

    }

    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }
}
