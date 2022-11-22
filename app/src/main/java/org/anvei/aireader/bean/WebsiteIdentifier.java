package org.anvei.aireader.bean;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;

public enum WebsiteIdentifier {
    UNKNOWN,
    BIQUMU,
    SFACG,
    W147XS;

    public static @Nullable WebsiteIdentifier getIdentifier(String identifierStr) {
        identifierStr = identifierStr.toUpperCase();
        WebsiteIdentifier identifier = null;
        Class<WebsiteIdentifier> clazz = WebsiteIdentifier.class;
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            String name = field.getName().toUpperCase();
            if (name.equals(identifierStr)) {
                try {
                    // 传null获取字段值
                    identifier = (WebsiteIdentifier)field.get(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return identifier;
    }

}
