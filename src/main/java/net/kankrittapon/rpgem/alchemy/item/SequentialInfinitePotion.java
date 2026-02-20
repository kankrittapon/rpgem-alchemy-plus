package net.kankrittapon.rpgem.alchemy.item;

import net.kankrittapon.rpgem.alchemy.init.ModAlchemyEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;

public class SequentialInfinitePotion extends Item {

    public SequentialInfinitePotion(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        Player player = livingEntity instanceof Player ? (Player) livingEntity : null;
        if (player == null)
            return stack;

        if (!level.isClientSide) {
            List<String> history = getIngredientHistory(stack);
            int tier = history.size();

            // 1. Always Heal (Instant Action)
            performHealing(player, tier);

            // 2. Check Buff Cooldown (Separate from Healing)
            // Fix: Use getOrDefault inside checking or initialize cleanly
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            long lastBuffTime = customData.copyTag().getLong("LastBuffTime");

            long currentTime = level.getGameTime();
            long cooldownTicks = switch (tier) {
                case 1 -> 160; // 8s
                case 2 -> 120; // 6s
                case 3 -> 40; // 2s
                default -> 160;
            };

            if (currentTime >= lastBuffTime + cooldownTicks) {
                // Perform Cleanse & Apply Buffs
                if (tier >= 2)
                    performCleanse(player, tier);
                applyEffects(player, history);

                // Update Last Buff Time
                CompoundTag tag = customData.copyTag();
                tag.putLong("LastBuffTime", currentTime);
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

                // Short Item Cooldown
                player.getCooldowns().addCooldown(this, 20); // 1s universal CD
            } else {
                player.displayClientMessage(Component.literal("§7[ Potion Buffs on Cooldown... ]"), true);
            }
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }

    private void performHealing(Player player, int tier) {
        if (tier == 1)
            player.heal(8.0F); // 4 Hearts
        else if (tier == 2)
            player.heal(16.0F); // 8 Hearts
        else if (tier == 3)
            player.setHealth(player.getMaxHealth()); // 100%
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32; // Standard drink time
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getIngredientHistory(stack).size() >= 3;
    }

    @Override
    public Component getName(ItemStack stack) {
        List<String> history = getIngredientHistory(stack);
        int tier = history.size();

        if (tier == 1 && !history.isEmpty()) {
            return switch (history.get(0)) {
                case "H" -> Component.literal("Potion of Undying Vitality");
                case "B" -> Component.literal("Potion of Unyielding Structure");
                case "C" -> Component.literal("Potion of Cosmic Clarity");
                default -> super.getName(stack);
            };
        } else if (tier == 2 && history.size() >= 2) {
            String combo = history.get(0) + history.get(1);
            return switch (combo) {
                case "HB" -> Component.literal("Potion of Armored Vitality");
                case "HC" -> Component.literal("Potion of Enlightened Pulse");
                case "BH" -> Component.literal("Potion of Living Structure");
                case "BC" -> Component.literal("Potion of Astral Spine");
                case "CH" -> Component.literal("Potion of Cosmic Flesh");
                case "CB" -> Component.literal("Potion of Solidified Void");
                default -> super.getName(stack);
            };
        } else if (tier >= 3) {
            return Component.literal("The Elixir of Boundless Eternity").withStyle(ChatFormatting.GOLD);
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        List<String> history = getIngredientHistory(stack);
        int tier = history.size();

        if (tier > 0) {
            tooltipComponents.add(Component.literal("Tier: " + tier).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.literal("Ingredients: " + String.join(", ", history))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        if (tier == 3) {
            tooltipComponents.add(Component.literal("The Savior's Grace").withStyle(ChatFormatting.GOLD));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private void performCleanse(Player player, int tier) {
        List<MobEffectInstance> activeEffects = new ArrayList<>(player.getActiveEffects());

        for (MobEffectInstance instance : activeEffects) {
            if (instance.getEffect().value().getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL) {
                boolean isBlacklisted = instance.getEffect().value() == MobEffects.WITHER ||
                        instance.getEffect().value() == MobEffects.LEVITATION ||
                        instance.getEffect().value() == MobEffects.DARKNESS;

                if (tier == 2) {
                    if (!isBlacklisted) {
                        player.removeEffect(instance.getEffect());
                    }
                } else if (tier == 3) {
                    player.removeEffect(instance.getEffect());
                }
            }
        }
    }

    private void applyEffects(Player player, List<String> history) {
        int tier = history.size();

        // Note: performHealing calls setHealth/heal separately.
        // This method applies status effects.

        if (tier == 1 && !history.isEmpty()) {
            String primary = history.get(0);
            switch (primary) {
                case "H" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 1, 0));
                }
                case "B" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
                }
                case "C" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
                }
            }

        } else if (tier == 2 && history.size() >= 2) {
            String combo = history.get(0) + history.get(1);
            switch (combo) {
                case "HB" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 600, 4));
                    player.addEffect(new MobEffectInstance(ModAlchemyEffects.JUGGERNAUT, 600, 0));
                    player.setHealth(player.getMaxHealth()); // Reset health to max after boost
                }
                case "HC" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
                }
                case "BH" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1));
                    player.addEffect(new MobEffectInstance(ModAlchemyEffects.IRON_THORNS, 600, 0));
                }
                case "BC" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1));
                    player.addEffect(new MobEffectInstance(ModAlchemyEffects.UNSTOPPABLE, 600, 0));
                }
                case "CH" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0));
                    player.addEffect(new MobEffectInstance(ModAlchemyEffects.EVASION, 600, 0));
                }
                case "CB" -> {
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
                }
            }

        } else if (tier == 3) {
            player.setHealth(player.getMaxHealth());
            player.addEffect(new MobEffectInstance(ModAlchemyEffects.BOUNDLESS_GRACE, 1200, 0));

            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
        }
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player,
            net.minecraft.world.InteractionHand hand) {
        return net.minecraft.world.item.ItemUtils.startUsingInstantly(level, player, hand);
    }

    private List<String> getIngredientHistory(ItemStack stack) {
        List<String> history = new ArrayList<>();
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag = customData.copyTag();
            if (tag.contains("IngredientHistory")) {
                ListTag list = tag.getList("IngredientHistory", 8); // 8 = String
                for (Tag t : list) {
                    history.add(t.getAsString());
                }
            }
        }
        return history;
    }
}
