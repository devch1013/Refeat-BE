package com.audrey.refeat.domain.chat.entity.enums;

public enum Language {
    EN,
    KO;

    public static Language fromString(String language) {
        if (language == null) {
            return null;
        }
        for (Language lang : Language.values()) {
            if (lang.name().equalsIgnoreCase(language)) {
                return lang;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return name();
    }
}
