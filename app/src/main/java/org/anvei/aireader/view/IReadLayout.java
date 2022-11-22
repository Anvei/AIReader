package org.anvei.aireader.view;

import java.io.File;
import java.io.FileNotFoundException;

public interface IReadLayout {

    /**
     * 从文件中加载小说内容
     */
    void loadFile(File file) throws FileNotFoundException;

    void loadText(String string);

    void loadFromInternet(InternetNovelLoader novelLoader);

}
