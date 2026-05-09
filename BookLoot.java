package com.proceduraldialectics.minerlore;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Set;

/**
 * Modernized 1.20.1-oriented loot injector.
 *
 * Major upgrades:
 * - per-book rarity & seasonal weighting
 * - category/tags metadata embedded in NBT
 * - codex hooks for UI and progression systems
 */
@Mod(BookLoot.MOD_ID)
@Mod.EventBusSubscriber(modid = BookLoot.MOD_ID)
public class BookLoot {
    public static final String MOD_ID = "minerlore";

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation WRITTEN_BOOK_ID = new ResourceLocation("minecraft", "written_book");
    private static final Set<ResourceLocation> TARGET_LOOT_TABLES = Set.of(
            new ResourceLocation("minecraft", "chests/igloo_chest"),
            new ResourceLocation("minecraft", "chests/simple_dungeon"),
            new ResourceLocation("minecraft", "chests/woodland_mansion"),
            new ResourceLocation("minecraft", "chests/village/village_toolsmith"),
            new ResourceLocation("minecraft", "chests/stronghold_library"),
            new ResourceLocation("minecraft", "chests/stronghold_corridor"),
            new ResourceLocation("minecraft", "chests/stronghold_crossing"),
            new ResourceLocation("minecraft", "chests/abandoned_mineshaft")
    );

    public BookLoot() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Miner Lore (1.20.1 refactor) loaded with {} books.", LoreBookRegistry.allBooks().size());
        LOGGER.info("Features: codexUI={}, discoveryXp={}, seasonalRotation={}, tagSearch={}, pinning={}",
                LoreFeatureFlags.ENABLE_CODEX_SCREEN,
                LoreFeatureFlags.ENABLE_DISCOVERY_XP,
                LoreFeatureFlags.ENABLE_SEASONAL_ROTATION,
                LoreFeatureFlags.ENABLE_TAG_SEARCH,
                LoreFeatureFlags.ENABLE_BOOK_PINNING);
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        if (!TARGET_LOOT_TABLES.contains(tableId)) {
            return;
        }

        Item writtenBook = ForgeRegistries.ITEMS.getValue(WRITTEN_BOOK_ID);
        if (writtenBook == null) {
            LOGGER.warn("Could not find minecraft:written_book; skipping loot injection.");
            return;
        }

        LootPool.Builder poolBuilder = LootPool.lootPool().name("minerlore_books");

        for (BookData book : LoreBookRegistry.allBooks()) {
            int weight = book.rarityWeight() + LoreBookRegistry.seasonalWeightBonus(book);
            poolBuilder.add(LootItem.lootTableItem(writtenBook)
                    .setWeight(Math.max(1, weight))
                    .apply(SetNbtFunction.setTag(toBookTag(book)).build()));
        }

        if (tableId.equals(new ResourceLocation("minecraft", "chests/stronghold_library"))) {
            poolBuilder.setRolls(ConstantValue.exactly(4));
        } else {
            poolBuilder.setRolls(ConstantValue.exactly(1));
        }

        LootTable table = event.getTable();
        if (table != null) {
            table.addPool(poolBuilder.build());
        }
    }

    private static CompoundTag toBookTag(BookData book) {
        CompoundTag tag = new CompoundTag();
        tag.putString("author", book.author());
        tag.putString("title", book.title());
        tag.putString("minerlore_book_id", book.id());
        tag.putString("minerlore_category", book.category().name());
        tag.putInt("minerlore_discovery_xp", book.discoveryXp());

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf("{\"text\":\"" + safe(book.previewLine()) + "\"}"));
        for (String p : book.pages()) {
            pages.add(StringTag.valueOf("{\"text\":\"" + safe(p) + "\"}"));
        }
        tag.put("pages", pages);

        ListTag tags = new ListTag();
        for (String value : book.tags()) {
            tags.add(StringTag.valueOf(value));
        }
        tag.put("minerlore_tags", tags);
        return tag;
    }

    private static String safe(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
