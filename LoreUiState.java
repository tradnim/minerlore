package com.proceduraldialectics.minerlore;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-side state container to support a richer codex UI for 1.20.1.
 *
 * Features provided:
 * - search query binding
 * - category filtering
 * - pinned books
 * - page/font scaling for accessibility
 */
public final class LoreUiState {
    private String searchQuery = "";
    private LoreCategory activeCategory = LoreCategory.HISTORY;
    private final Set<String> pinnedBookIds = new HashSet<>();
    private float fontScale = 1.0F;

    public String searchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery == null ? "" : searchQuery.trim();
    }

    public LoreCategory activeCategory() {
        return activeCategory;
    }

    public void setActiveCategory(LoreCategory activeCategory) {
        if (activeCategory != null) {
            this.activeCategory = activeCategory;
        }
    }

    public boolean isPinned(String bookId) {
        return pinnedBookIds.contains(bookId);
    }

    public void togglePinned(String bookId) {
        if (bookId == null || bookId.isBlank()) {
            return;
        }

        if (!pinnedBookIds.add(bookId)) {
            pinnedBookIds.remove(bookId);
        }
    }

    public float fontScale() {
        return fontScale;
    }

    public void setFontScale(float fontScale) {
        this.fontScale = Math.max(0.75F, Math.min(1.75F, fontScale));
    }
}
