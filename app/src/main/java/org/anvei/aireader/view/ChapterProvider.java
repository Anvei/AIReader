package org.anvei.aireader.view;

import androidx.annotation.IntRange;

public abstract class ChapterProvider {

    public abstract void initProvider() throws ChapterInitException;

    public abstract String nextPage();

    public abstract String prePage();

    public abstract String curPage();

    public abstract boolean hasPrePage();

    public abstract boolean hasNextPage();

    public abstract boolean hasPage();

    public abstract boolean moveToPre();

    public abstract boolean moveToNext();

    public abstract void chapterIndex(@IntRange(from = 1) int index);

    public abstract void pageIndex(@IntRange(from = 1) int index);

}
