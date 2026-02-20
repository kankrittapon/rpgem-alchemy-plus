package net.kankrittapon.rpgem.alchemy.init;

import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.kankrittapon.rpgem.alchemy.item.SequentialInfinitePotion;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAlchemyItems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RPGEMAlchemy.MODID);

        // Ingredients (Migrated from neoforged-bokkchoy if not in Core)
        public static final DeferredItem<Item> BONE_OF_MAZE = ITEMS.registerSimpleItem("bone_of_maze",
                        new Item.Properties());
        public static final DeferredItem<Item> COSMIC_EMERALD = ITEMS.registerSimpleItem("cosmic_emerald",
                        new Item.Properties());
        public static final DeferredItem<Item> ETHERNAL_BOTTLE = ITEMS.registerSimpleItem("ethernal_bottle",
                        new Item.Properties());
        public static final DeferredItem<Item> ZOMBIE_HEART = ITEMS.registerSimpleItem("zombie_heart",
                        new Item.Properties());

        // Potions
        public static final DeferredItem<Item> INFINITE_POTION_TIER_1 = ITEMS.register("infinite_potion_tier_1",
                        () -> new SequentialInfinitePotion(new Item.Properties()));
        public static final DeferredItem<Item> INFINITE_POTION_TIER_2 = ITEMS.register("infinite_potion_tier_2",
                        () -> new SequentialInfinitePotion(new Item.Properties()));
        public static final DeferredItem<Item> INFINITE_POTION_TIER_3 = ITEMS.register("infinite_potion_tier_3",
                        () -> new SequentialInfinitePotion(new Item.Properties()));

        // Block Items — required so the block can appear in Creative Tab
        public static final DeferredItem<BlockItem> ALCHEMY_TABLE_ITEM = ITEMS.registerSimpleBlockItem("alchemy_table",
                        ModAlchemyBlocks.ALCHEMY_TABLE, new Item.Properties());

        // Quest Fragments (ย้ายมาจาก rpgem-fairy-plus)
        public static final DeferredItem<Item> PIECE_OF_HEART = ITEMS.registerSimpleItem("piece_of_heart",
                        new Item.Properties());
        public static final DeferredItem<Item> PIECE_OF_BONE = ITEMS.registerSimpleItem("piece_of_bone",
                        new Item.Properties());
        public static final DeferredItem<Item> PIECE_OF_COSMIC_EMERALD = ITEMS.registerSimpleItem(
                        "piece_of_cosmic_emerald",
                        new Item.Properties());

        public static void register(IEventBus eventBus) {
                ITEMS.register(eventBus);
        }
}
