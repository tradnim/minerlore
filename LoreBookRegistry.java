package com.proceduraldialectics.minerlore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class LoreBookRegistry {
    private static final List<BookData> BOOKS = new ArrayList<>();

    static {
        registerDefaultBooks();
    }

    private LoreBookRegistry() {}

    public static List<BookData> allBooks() {
        return BookData.immutableCopy(BOOKS);
    }

    public static List<BookData> featuredBooks() {
        return BOOKS.stream().filter(BookData::featured).collect(Collectors.toList());
    }

    public static List<BookData> byCategory(LoreCategory category) {
        return BOOKS.stream().filter(book -> book.category() == category).collect(Collectors.toList());
    }

    public static List<BookData> search(String query, LoreCategory category) {
        return BOOKS.stream()
                .filter(book -> category == null || book.category() == category)
                .filter(book -> query == null || query.isBlank() || book.matchesSearch(query))
                .sorted(Comparator.comparing(BookData::title, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public static int seasonalWeightBonus(BookData book) {
        if (!LoreFeatureFlags.ENABLE_SEASONAL_ROTATION) {
            return 0;
        }

        LocalDate now = LocalDate.now();
        if (now.getMonthValue() == 10 && book.tags().stream().anyMatch(t -> t.equalsIgnoreCase("horror"))) {
            return 40;
        }
        if (now.getMonthValue() == 12 && book.tags().stream().anyMatch(t -> t.equalsIgnoreCase("winter"))) {
            return 35;
        }
        return 0;
    }

    public static String categoryDisplayName(LoreCategory category) {
        return category.name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }

    private static void registerDefaultBooks() {
        BOOKS.add(BookData.builder("blue_diamond_mines", "Jean Ritchie", "Blue Diamond Mines")
                .category(LoreCategory.SONG)
                .rarityWeight(90)
                .discoveryXp(5)
                .featured(true)
                .tag("labor")
                .tag("song")
                .addPage("I remember the ways in the bygone days, when we was all in our prime.")
                .addPage("In the mines, in the mines... I have worked my life away.")
                .build());

        BOOKS.add(BookData.builder("germinal_excerpt", "Emile Zola", "Germinal")
                .category(LoreCategory.HISTORY)
                .rarityWeight(60)
                .discoveryXp(8)
                .tag("history")
                .tag("classic")
                .addPage("In the fertile earth, life was leaping out; and beneath, the miners struck the deep.")
                .addPage("The plain seemed full of that sound, an army germinating in the furrows.")
                .build());

        BOOKS.add(BookData.builder("patience_kershaw", "Frank Higgins", "The Testimony of Patience Kershaw")
                .category(LoreCategory.CULTURE)
                .rarityWeight(45)
                .discoveryXp(12)
                .featured(true)
                .tag("workers")
                .tag("history")
                .addPage("I hurry corves to earn my pay; it isn't lady-like, but bread must be won.")
                .addPage("A hundred years and more will pass before we're standing side by side.")
                .build());

        BOOKS.add(BookData.builder("field_report_spitter", "Bertolt Brecht", "Field Report: Spitter")
                .category(LoreCategory.FIELD_REPORT)
                .rarityWeight(35)
                .discoveryXp(16)
                .tag("horror")
                .tag("infected")
                .addPage("The creature lashed wildly before expelling a stream of burning acid.")
                .addPage("Distance and terrain are now mandatory survival tools.")
                .build());

        BOOKS.add(BookData.builder("survivor_diary_sarah", "Sarah Earle", "Survivor Log: Day 30")
                .category(LoreCategory.SURVIVOR_LOG)
                .rarityWeight(25)
                .discoveryXp(10)
                .tag("survivor")
                .tag("journal")
                .addPage("We found old lab notes today. The virus may have been engineered.")
                .addPage("Every new week, infected variants become more adaptive and less predictable.")
                .build());
    }
}
