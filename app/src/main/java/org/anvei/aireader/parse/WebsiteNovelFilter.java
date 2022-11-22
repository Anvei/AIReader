package org.anvei.aireader.parse;

import org.anvei.aireader.bean.WebsiteNovel;

import java.util.List;

public interface WebsiteNovelFilter {
    List<WebsiteNovel> filter(List<WebsiteNovel> novels);
}
