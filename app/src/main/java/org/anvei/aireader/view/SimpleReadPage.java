package org.anvei.aireader.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import org.anvei.aireader.R;

public class SimpleReadPage extends AppCompatTextView implements IReadPage {

    public SimpleReadPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 临时监听，该监听会触发重绘
     */
    public void startOnceDemiListen(DemiListener demiListener) {
        String s = getContext().getResources().getString(R.string.read_page_measure_str);
        setText(s);
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                demiListener.onDemi(getDisplayLineCount(), getCharCountPerLine());
                setText("");
                vto.removeOnGlobalLayoutListener(this);
            }
        });
    }

    public interface DemiListener {

        void onDemi(int maxDisplayLines, int charCountPerLine);

    }

    @Override
    public int getDisplayLineCount() {
        Layout layout = getLayout();
        if (layout == null)
            return 0;
        int topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        return layout.getLineForVertical(topOfLastLine);
    }

    @Override
    public int getDisplayCharCount() {
        Layout layout = getLayout();
        if (layout == null)
            return 0;
        return layout.getLineEnd(getDisplayLineCount());
    }

    public int getCharCountPerLine() {
        Layout layout = getLayout();
        if (layout == null) {
            return 0;
        }
        return layout.getLineEnd(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

}
