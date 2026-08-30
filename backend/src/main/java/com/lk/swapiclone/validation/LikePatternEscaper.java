package com.lk.swapiclone.validation;

public final class LikePatternEscaper {

    private LikePatternEscaper() {
    }

    public static String escape(String term) {
        return term
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    }
}
