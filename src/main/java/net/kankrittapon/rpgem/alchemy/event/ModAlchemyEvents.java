package net.kankrittapon.rpgem.alchemy.event;

import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.kankrittapon.rpgem.alchemy.init.ModAlchemyItems;
import net.kankrittapon.rpgem.alchemy.init.ModAlchemyVillagers;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent; // Will use later
import net.neoforged.neoforge.common.BasicItemListing;

import java.util.List;

@EventBusSubscriber(modid = RPGEMAlchemy.MODID)
public class ModAlchemyEvents {

        @SubscribeEvent
        public static void addCustomTrades(VillagerTradesEvent event) {
                if (event.getType() == ModAlchemyVillagers.ALCHEMIST.get()) {
                        // Level 1: Novice
                        // Buy Emerald x15 -> Sell Potion of Healing I
                        List<VillagerTrades.ItemListing> level1 = event.getTrades().get(1);
                        ItemStack healing1 = new ItemStack(Items.POTION);
                        healing1.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                                        new net.minecraft.world.item.alchemy.PotionContents(
                                                        net.minecraft.world.item.alchemy.Potions.HEALING));
                        level1.add(new BasicItemListing(
                                        new ItemStack(Items.EMERALD, 15),
                                        healing1,
                                        10, 5, 0.05f));

                        // Level 2: Apprentice
                        // Buy Emerald x30 -> Sell Potion of Healing II (Strong Healing)
                        List<VillagerTrades.ItemListing> level2 = event.getTrades().get(2);
                        ItemStack healing2 = new ItemStack(Items.POTION);
                        healing2.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                                        new net.minecraft.world.item.alchemy.PotionContents(
                                                        net.minecraft.world.item.alchemy.Potions.STRONG_HEALING));
                        level2.add(new BasicItemListing(
                                        new ItemStack(Items.EMERALD, 30),
                                        healing2,
                                        5, 10, 0.05f));

                        // Level 3: Journeyman
                        // Buy Netherite Block x32 + Diamond Block x32 -> Sell Ethernal Bottle
                        List<VillagerTrades.ItemListing> level3 = event.getTrades().get(3);
                        level3.add(new BasicItemListing(
                                        new ItemStack(Items.NETHERITE_BLOCK, 32),
                                        new ItemStack(Items.DIAMOND_BLOCK, 32),
                                        new ItemStack(ModAlchemyItems.ETHERNAL_BOTTLE.get()),
                                        3, 20, 0.05f));

                        // Level 4: Expert
                        // Buy Emerald x64 + Gold Ingot x32 -> Sell Pieces (Selectable)
                        List<VillagerTrades.ItemListing> level4 = event.getTrades().get(4);
                        // Option A: Piece of Heart
                        level4.add(new BasicItemListing(
                                        new ItemStack(Items.EMERALD, 64),
                                        new ItemStack(Items.GOLD_INGOT, 32),
                                        new ItemStack(ModAlchemyItems.PIECE_OF_HEART.get()),
                                        5, 30, 0.05f));
                        // Option B: Piece of Bone
                        level4.add(new BasicItemListing(
                                        new ItemStack(Items.EMERALD, 64),
                                        new ItemStack(Items.GOLD_INGOT, 32),
                                        new ItemStack(ModAlchemyItems.PIECE_OF_BONE.get()),
                                        5, 30, 0.05f));
                        // Option C: Piece of Cosmic Emerald
                        level4.add(new BasicItemListing(
                                        new ItemStack(Items.EMERALD, 64),
                                        new ItemStack(Items.GOLD_INGOT, 32),
                                        new ItemStack(ModAlchemyItems.PIECE_OF_COSMIC_EMERALD.get()),
                                        5, 30, 0.05f));

                        // Level 5: Master
                        // Buy Ethernal Bottle + Nether Star -> Sell Potion of Healing III +
                        // Regeneration III
                        List<VillagerTrades.ItemListing> level5 = event.getTrades().get(5);

                        ItemStack superPotion = new ItemStack(Items.POTION);
                        // Create Custom Potion Effects
                        // Healing III = Amplifier 2, Instant
                        // Regeneration III = Amplifier 2, Duration 45s (900 ticks)
                        superPotion.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                                        new net.minecraft.world.item.alchemy.PotionContents(java.util.Optional.empty(),
                                                        java.util.Optional.of(0xFA5252), java.util.List.of(
                                                                        new net.minecraft.world.effect.MobEffectInstance(
                                                                                        net.minecraft.world.effect.MobEffects.HEAL,
                                                                                        1, 2),
                                                                        new net.minecraft.world.effect.MobEffectInstance(
                                                                                        net.minecraft.world.effect.MobEffects.REGENERATION,
                                                                                        900, 2))));
                        superPotion.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                                        net.minecraft.network.chat.Component.literal("§6Elixir of Vitality"));

                        level5.add(new BasicItemListing(
                                        new ItemStack(ModAlchemyItems.ETHERNAL_BOTTLE.get()),
                                        new ItemStack(Items.NETHER_STAR, 1),
                                        superPotion,
                                        3, 30, 0.05f));
                }
        }

        @SubscribeEvent
        public static void onEntityInteract(
                        net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
                if (event.getLevel().isClientSide)
                        return;

                if (event.getTarget() instanceof net.minecraft.world.entity.npc.Villager villager) {
                        if (villager.getVillagerData().getProfession() == ModAlchemyVillagers.ALCHEMIST.get()) {
                                net.minecraft.world.entity.player.Player player = event.getEntity();
                                ItemStack heldItem = player.getMainHandItem();

                                // === 1. FRAGMENT EXCHANGE (100 -> 1 Big Item) ===
                                if (heldItem.is(ModAlchemyItems.PIECE_OF_HEART.get())) {
                                        if (getInventoryItemCount(player,
                                                        ModAlchemyItems.PIECE_OF_HEART.get()) >= 100) {
                                                consumeItemFromInventory(player, ModAlchemyItems.PIECE_OF_HEART.get(),
                                                                100);
                                                player.addItem(new ItemStack(ModAlchemyItems.ZOMBIE_HEART.get()));
                                                playExchangeSound(player);
                                                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                                                "§6[Alchemist]§e A Zombie Heart! Pure vitality condensed."));
                                                event.setCanceled(true);
                                                event.setCancellationResult(
                                                                net.minecraft.world.InteractionResult.SUCCESS);
                                                return;
                                        }
                                } else if (heldItem.is(ModAlchemyItems.PIECE_OF_BONE.get())) {
                                        if (getInventoryItemCount(player, ModAlchemyItems.PIECE_OF_BONE.get()) >= 100) {
                                                consumeItemFromInventory(player, ModAlchemyItems.PIECE_OF_BONE.get(),
                                                                100);
                                                player.addItem(new ItemStack(ModAlchemyItems.BONE_OF_MAZE.get()));
                                                playExchangeSound(player);
                                                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                                                "§6[Alchemist]§e The Bone of Maze... structure and resilience."));
                                                event.setCanceled(true);
                                                event.setCancellationResult(
                                                                net.minecraft.world.InteractionResult.SUCCESS);
                                                return;
                                        }
                                } else if (heldItem.is(ModAlchemyItems.PIECE_OF_COSMIC_EMERALD.get())) {
                                        if (getInventoryItemCount(player,
                                                        ModAlchemyItems.PIECE_OF_COSMIC_EMERALD.get()) >= 100) {
                                                consumeItemFromInventory(player,
                                                                ModAlchemyItems.PIECE_OF_COSMIC_EMERALD.get(), 100);
                                                player.addItem(new ItemStack(ModAlchemyItems.COSMIC_EMERALD.get()));
                                                playExchangeSound(player);
                                                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                                                "§6[Alchemist]§e A Cosmic Emerald! The energy of the universe."));
                                                event.setCanceled(true);
                                                event.setCancellationResult(
                                                                net.minecraft.world.InteractionResult.SUCCESS);
                                                return;
                                        }
                                }

                                // === 2. POTION INFUSION (Dynamic Upgrade) ===
                                if (heldItem.is(ModAlchemyItems.ETHERNAL_BOTTLE.get()) ||
                                                heldItem.getItem() instanceof net.kankrittapon.rpgem.alchemy.item.SequentialInfinitePotion) {

                                        List<String> history = getIngredientHistory(heldItem);
                                        ItemStack nextPotion = ItemStack.EMPTY;

                                        // Determine Next Tier
                                        if (heldItem.is(ModAlchemyItems.ETHERNAL_BOTTLE.get())) {
                                                nextPotion = new ItemStack(
                                                                ModAlchemyItems.INFINITE_POTION_TIER_1.get());
                                        } else if (heldItem.is(ModAlchemyItems.INFINITE_POTION_TIER_1.get())) {
                                                nextPotion = new ItemStack(
                                                                ModAlchemyItems.INFINITE_POTION_TIER_2.get());
                                        } else if (heldItem.is(ModAlchemyItems.INFINITE_POTION_TIER_2.get())) {
                                                nextPotion = new ItemStack(
                                                                ModAlchemyItems.INFINITE_POTION_TIER_3.get());
                                        } else {
                                                // Already Max Tier (Tier 3) -> Cannot upgrade further via this method
                                                return;
                                        }

                                        // Scan Inventory for Compatible Big Items
                                        String ingredientCode = null;
                                        net.minecraft.world.item.Item ingredientItem = null;

                                        if (!history.contains("H") && hasItemInInventory(player,
                                                        ModAlchemyItems.ZOMBIE_HEART.get())) {
                                                ingredientCode = "H";
                                                ingredientItem = ModAlchemyItems.ZOMBIE_HEART.get();
                                        } else if (!history.contains("B") && hasItemInInventory(player,
                                                        ModAlchemyItems.BONE_OF_MAZE.get())) {
                                                ingredientCode = "B";
                                                ingredientItem = ModAlchemyItems.BONE_OF_MAZE.get();
                                        } else if (!history.contains("C") && hasItemInInventory(player,
                                                        ModAlchemyItems.COSMIC_EMERALD.get())) {
                                                ingredientCode = "C";
                                                ingredientItem = ModAlchemyItems.COSMIC_EMERALD.get();
                                        }

                                        // Execute Infusion
                                        if (ingredientCode != null && ingredientItem != null) {
                                                // 1. Consume Big Item
                                                consumeItemFromInventory(player, ingredientItem, 1);

                                                // 2. Update NBT History
                                                history.add(ingredientCode);
                                                applyIngredientHistory(nextPotion, history);

                                                // 3. Replace Item
                                                heldItem.shrink(1);
                                                player.addItem(nextPotion);

                                                // 4. Feedback
                                                player.level().playSound(null, player.blockPosition(),
                                                                net.minecraft.sounds.SoundEvents.BREWING_STAND_BREW,
                                                                net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
                                                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                                                "§6[Alchemist]§a Infusion Successful! The potion evolves..."));
                                                event.setCanceled(true);
                                                event.setCancellationResult(
                                                                net.minecraft.world.InteractionResult.SUCCESS);
                                        } else {
                                                if (heldItem.is(ModAlchemyItems.ETHERNAL_BOTTLE.get())) {
                                                        player.sendSystemMessage(net.minecraft.network.chat.Component
                                                                        .literal("§6[Alchemist]§c I need a powerful catalyst (Heart, Bone, or Cosmic Emerald) to activate this bottle."));
                                                } else {
                                                        player.sendSystemMessage(net.minecraft.network.chat.Component
                                                                        .literal("§6[Alchemist]§c You need a new catalyst (Heart, Bone, or Cosmic Emerald) that hasn't been used yet!"));
                                                }
                                        }
                                }
                        }
                }
        }

        private static void playExchangeSound(net.minecraft.world.entity.player.Player player) {
                player.level().playSound(null, player.blockPosition(),
                                net.minecraft.sounds.SoundEvents.VILLAGER_WORK_CLERIC,
                                net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
        }

        // === NBT HELPERS ===
        private static List<String> getIngredientHistory(ItemStack stack) {
                List<String> history = new java.util.ArrayList<>();
                if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
                        net.minecraft.world.item.component.CustomData customData = stack
                                        .get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
                        net.minecraft.nbt.CompoundTag tag = customData.copyTag();
                        if (tag.contains("IngredientHistory")) {
                                net.minecraft.nbt.ListTag list = tag.getList("IngredientHistory", 8); // 8 = String
                                for (int i = 0; i < list.size(); i++) {
                                        history.add(list.getString(i));
                                }
                        }
                }
                return history;
        }

        private static void applyIngredientHistory(ItemStack stack, List<String> history) {
                net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
                net.minecraft.nbt.ListTag listTag = new net.minecraft.nbt.ListTag();
                for (String s : history) {
                        listTag.add(net.minecraft.nbt.StringTag.valueOf(s));
                }
                tag.put("IngredientHistory", listTag);
                stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                                net.minecraft.world.item.component.CustomData.of(tag));
        }

        // === INVENTORY HELPERS ===
        private static int getInventoryItemCount(net.minecraft.world.entity.player.Player player,
                        net.minecraft.world.item.Item item) {
                int count = 0;
                for (ItemStack stack : player.getInventory().items) {
                        if (stack.is(item)) {
                                count += stack.getCount();
                        }
                }
                return count;
        }

        private static boolean hasItemInInventory(net.minecraft.world.entity.player.Player player,
                        net.minecraft.world.item.Item item) {
                for (ItemStack stack : player.getInventory().items) {
                        if (stack.is(item))
                                return true;
                }
                return false;
        }

        private static void consumeItemFromInventory(net.minecraft.world.entity.player.Player player,
                        net.minecraft.world.item.Item item, int count) {
                int remaining = count;
                for (ItemStack stack : player.getInventory().items) {
                        if (stack.is(item)) {
                                int take = Math.min(stack.getCount(), remaining);
                                stack.shrink(take);
                                remaining -= take;
                                if (remaining <= 0)
                                        break;
                        }
                }
        }
}
