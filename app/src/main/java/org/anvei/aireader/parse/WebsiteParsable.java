package org.anvei.aireader.parse;


import org.anvei.aireader.bean.WebsiteChapter;
import org.anvei.aireader.bean.WebsiteIdentifier;
import org.anvei.aireader.bean.WebsiteNovel;

import java.util.List;

public interface WebsiteParsable {

    WebsiteIdentifier getWebsiteIdentifier();

    List<WebsiteNovel> search(String keyWord);

    List<WebsiteChapter> loadNovel(WebsiteNovel novelInfo);

    List<WebsiteChapter> loadNovel(String novelUrl);

    WebsiteChapter loadChapter(WebsiteChapter chapterInfo);

}
