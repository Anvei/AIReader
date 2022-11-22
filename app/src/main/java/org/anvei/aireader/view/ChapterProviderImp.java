package org.anvei.aireader.view;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;


import org.anvei.aireader.view.splitter.ChapterPageSplitter;
import org.anvei.aireader.view.splitter.ChapterSplitter;

import java.io.IOException;
import java.util.List;

public class ChapterProviderImp extends ChapterProvider {

    private ChapterSplitter chapterSplitter;

    private ChapterPageSplitter chapterPageSplitter;

    private int curChapterIndex;
    private int curPageIndex;

    private int pointer;
    private final ChapterContent[] chapterContents = new ChapterContent[3];

    public ChapterProviderImp(@IntRange(from = 1) int curChapterIndex, @IntRange(from = 1) int curPageIndex) {
        this.curChapterIndex = curChapterIndex;
        this.curPageIndex = curPageIndex;
    }

    private @Nullable List<String> splitPage(int index) {
        try {
            return chapterPageSplitter.startSplit(
                    chapterSplitter.getChapterContent(index));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 初始化ChapterProvider，负责约束curChapterIndex，
     * 初始化指针pointer，负责当前章节、上一章节、下一章节分页的初始化
     * @throws ChapterInitException 初始化异常
     */
    @Override
    public void initProvider() throws ChapterInitException {
        for (int i = 0; i < chapterContents.length; i++) {
            if (chapterContents[i] == null) {
                chapterContents[i] = new ChapterContent();
            } else {
                chapterContents[i].init(null);
            }
        }
        pointer =  1;
        int count = chapterSplitter.getChapterSplitResultCount();
        if (count == 0)
            throw new ChapterInitException();
        if (count < curChapterIndex) {
            curChapterIndex = count;
        }
        if (curChapterIndex < 1) {
            curChapterIndex = 1;
        }
        List<String> curSplits = splitPage(curChapterIndex);
        chapterContents[pointer].init(curSplits);
        if (!chapterContents[pointer].moveToPosition(curPageIndex - 1)) {
            throw new ChapterInitException();
        }
        // 有下一章
        if (curChapterIndex < count) {
            List<String> nextSplits = splitPage(curChapterIndex + 1);
            chapterContents[pointer + 1].init(nextSplits);
        }
        // 有上一章
        if (curChapterIndex > 1) {
            List<String> preSplits = splitPage(curChapterIndex - 1);
            chapterContents[pointer - 1].init(preSplits);
        }
    }

    @Override
    public @Nullable String nextPage() {
        ChapterContent chapterContent = chapterContents[pointer];
        String res = null;
        if (chapterContent.moveToNext()) {
            res = chapterContent.get();
        } else {
            // 该章节已经到了最后一页，尝试切换下一章
            pointer = (pointer + 1) % 3;
            curChapterIndex++;
            curPageIndex = 1;
            ChapterContent cur = chapterContents[pointer];
            if (cur.moveToFist()) {
                res = cur.get();
            }
            new Thread(() -> {
                // 记录需要更新内容的章节序号，该章节需要更新为切换之后的下一章
                int nextPointer = (pointer + 1) % 3;
                ChapterContent next = chapterContents[nextPointer];
                List<String> splits = splitPage(curChapterIndex + 1);
                next.init(splits);
            }).start();
        }
        return res;
    }

    @Override
    public @Nullable String prePage() {
        ChapterContent chapterContent = chapterContents[pointer];
        String res = null;
        if (chapterContent.moveToPre()) {
            res = chapterContent.get();
        } else {
            pointer = (pointer - 1) % 3;
            curPageIndex--;
            ChapterContent cur = chapterContents[pointer];
            curPageIndex = cur.getCount();
            if (cur.moveToLast()) {
                res = cur.get();
            }
            new Thread(() -> {
                int prePointer = (pointer - 1) % 3;
                ChapterContent pre = chapterContents[prePointer];
                List<String> splits = splitPage(curChapterIndex - 1);
                pre.init(splits);
            }).start();
        }
        return res;
    }

    @Override
    public String curPage() {
        ChapterContent chapterContent = chapterContents[pointer];
        return chapterContent.get();
    }

    @Override
    public boolean hasPrePage() {
        // 不是第一章、自然有前一页
        if (curChapterIndex != 1) {
            return true;
        }
        return curPageIndex != 1;
    }

    @Override
    public boolean hasNextPage() {
        ChapterContent chapterContent = chapterContents[curChapterIndex];
        if (curChapterIndex != chapterSplitter.getChapterSplitResultCount()) {
            return true;
        }
        return chapterContent.getCount() != curPageIndex;
    }

    @Override
    public boolean hasPage() {
        ChapterContent chapterContent = chapterContents[curChapterIndex];
        return  (curChapterIndex > 0 && chapterSplitter.getChapterSplitResultCount() >= curChapterIndex)
                && (curPageIndex > 0 && chapterContent.getCount() >= curPageIndex);
    }

    @Override
    public void chapterIndex(int index) {
        curChapterIndex = index;
    }

    @Override
    public void pageIndex(int index) {
        curPageIndex = index;
    }

    @Override
    public boolean moveToPre() {
        return false;
    }

    @Override
    public boolean moveToNext() {
        return false;
    }

    public void setChapterSplitter(ChapterSplitter chapterSplitter) {
        this.chapterSplitter = chapterSplitter;
    }

    public void setChapterPageSplitter(ChapterPageSplitter chapterPageSplitter) {
        this.chapterPageSplitter = chapterPageSplitter;
    }

    public void setSplitDemi(int lineCount, int charCount) throws ChapterInitException {
        chapterPageSplitter.setMaxLines(lineCount);
        chapterPageSplitter.setMaxCharWidthPerLine(charCount);
        initProvider();
    }
}
