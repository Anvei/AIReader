package org.anvei.aireader.parse.parserImp;

import org.anvei.aireader.bean.WebsiteChapter;
import org.anvei.aireader.bean.WebsiteIdentifier;
import org.anvei.aireader.bean.WebsiteNovel;
import org.anvei.aireader.parse.WebsiteNovelParser;

import java.util.List;

/**
 * TODO: 待实现
 */
public class YunxiParser extends WebsiteNovelParser {

    private static final String homeUrl = "https://www.yunxibook.com";

    private static final String searchApi = "https://www.yunxibook.com/modules/article/search.php";

    @Override
    public WebsiteIdentifier getWebsiteIdentifier() {
        return WebsiteIdentifier.UNKNOWN;
    }

    @Override
    public List<WebsiteNovel> search(String keyWord) {
        return null;
    }

    @Override
    public List<WebsiteChapter> loadNovel(WebsiteNovel novelInfo) {
        return null;
    }

    @Override
    public List<WebsiteChapter> loadNovel(String novelUrl) {
        return null;
    }

    @Override
    public WebsiteChapter loadChapter(WebsiteChapter chapterInfo) {
        return null;
    }
}
