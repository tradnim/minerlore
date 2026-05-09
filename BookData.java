package com.proceduraldialectics.minerlore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a lore book that can appear in loot and in the in-game codex UI.
 *
 * Updated for the 1.20.1 refactor:
 * - immutable model
 * - categories/tags
 * - XP reward and rarity metadata
 * - UI-friendly preview helpers
 */
public final class BookData {
    private final String id;
    private final String author;
    private final String title;
    private final List<String> pages;
    private final LoreCategory category;
    private final int rarityWeight;
    private final int discoveryXp;
    private final boolean featured;
    private final List<String> tags;

    private BookData(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.author = Objects.requireNonNull(builder.author, "author");
        this.title = Objects.requireNonNull(builder.title, "title");
        this.pages = List.copyOf(builder.pages);
        this.category = Objects.requireNonNull(builder.category, "category");
        this.rarityWeight = Math.max(1, builder.rarityWeight);
        this.discoveryXp = Math.max(0, builder.discoveryXp);
        this.featured = builder.featured;
        this.tags = List.copyOf(builder.tags);
    }

    public String id() { return id; }
    public String author() { return author; }
    public String title() { return title; }
    public List<String> pages() { return pages; }
    public LoreCategory category() { return category; }
    public int rarityWeight() { return rarityWeight; }
    public int discoveryXp() { return discoveryXp; }
    public boolean featured() { return featured; }
    public List<String> tags() { return tags; }

    public String previewLine() {
        return title + " — " + author;
    }

    public boolean matchesSearch(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        return title.toLowerCase(Locale.ROOT).contains(q)
                || author.toLowerCase(Locale.ROOT).contains(q)
                || tags.stream().anyMatch(t -> t.toLowerCase(Locale.ROOT).contains(q));
    }

    public static Builder builder(String id, String author, String title) {
        return new Builder(id, author, title);
    }

    public static final class Builder {
        private final String id;
        private final String author;
        private final String title;
        private final List<String> pages = new ArrayList<>();
        private LoreCategory category = LoreCategory.HISTORY;
        private int rarityWeight = 100;
        private int discoveryXp = 4;
        private boolean featured;
        private final List<String> tags = new ArrayList<>();

        private Builder(String id, String author, String title) {
            this.id = id;
            this.author = author;
            this.title = title;
        }

        public Builder addPage(String page) {
            this.pages.add(page);
            return this;
        }

        public Builder pages(List<String> pages) {
            this.pages.clear();
            this.pages.addAll(pages);
            return this;
        }

        public Builder category(LoreCategory category) {
            this.category = category;
            return this;
        }

        public Builder rarityWeight(int rarityWeight) {
            this.rarityWeight = rarityWeight;
            return this;
        }

        public Builder discoveryXp(int discoveryXp) {
            this.discoveryXp = discoveryXp;
            return this;
        }

        public Builder featured(boolean featured) {
            this.featured = featured;
            return this;
        }

        public Builder tag(String tag) {
            this.tags.add(tag);
            return this;
        }

        public BookData build() {
            if (pages.isEmpty()) {
                pages.add("This volume is empty. (Placeholder entry)");
            }
            return new BookData(this);
        }
    }

    public static List<BookData> immutableCopy(List<BookData> books) {
        return Collections.unmodifiableList(new ArrayList<>(books));
    }
}
