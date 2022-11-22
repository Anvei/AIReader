package org.anvei.aireader.view;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChapterContent {

    private final List<String> chapterPages = new ArrayList<>();

    private int pointer = -1;

    public ChapterContent() {
    }

    public boolean moveToNext() {
        if (pointer < chapterPages.size() - 1) {
            pointer++;
            return true;
        }
        return false;
    }

    public boolean moveToPre() {
        if (pointer > 0) {
            pointer--;
            return true;
        }
        return false;
    }

    public boolean moveToFist() {
        if (chapterPages.size() != 0) {
            pointer = 0;
            return true;
        }
        return false;
    }

    public boolean moveToLast() {
        if (chapterPages.size() != 0) {
            pointer = chapterPages.size() - 1;
        }
        return false;
    }

    public boolean moveToPosition(int position) {
        if (chapterPages.size() == 0) {
            return false;
        }
        if (position < 0 || position >= chapterPages.size()) {
            return false;
        }
        pointer = position;
        return true;
    }

    public String get() {
        return chapterPages.get(pointer);
    }

    public void init(@Nullable List<String> chapterPages) {
        if (this.chapterPages.size() != 0) {
            this.chapterPages.clear();
        }
        pointer = -1;
        if (chapterPages != null) {
            this.chapterPages.addAll(chapterPages);
        }
    }

    public int getCount() {
        return chapterPages.size();
    }
}
