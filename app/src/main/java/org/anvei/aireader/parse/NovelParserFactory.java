package org.anvei.aireader.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.anvei.aireader.bean.WebsiteIdentifier;

/**
 * 为了降低耦合度，采用了反射机制进行加载NovelParser
 */
public class NovelParserFactory {

    public @Nullable WebsiteNovelParser getWebsiteNovelParser(@NonNull String identifier) {
        WebsiteIdentifier i = WebsiteIdentifier.getIdentifier(identifier);
        if (i == null || i == WebsiteIdentifier.UNKNOWN)
            return null;
        return getWebsiteNovelParser(i);
    }

    public @Nullable WebsiteNovelParser getWebsiteNovelParser(WebsiteIdentifier identifier) {

        return null;
    }
}
