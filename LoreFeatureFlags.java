package com.proceduraldialectics.minerlore;

/**
 * Central place for feature toggles. This gives servers the ability to
 * quickly disable heavy/experimental systems without removing code.
 */
public final class LoreFeatureFlags {
    public static final boolean ENABLE_CODEX_SCREEN = true;
    public static final boolean ENABLE_DISCOVERY_XP = true;
    public static final boolean ENABLE_SEASONAL_ROTATION = true;
    public static final boolean ENABLE_TAG_SEARCH = true;
    public static final boolean ENABLE_BOOK_PINNING = true;

    private LoreFeatureFlags() {
    }
}
