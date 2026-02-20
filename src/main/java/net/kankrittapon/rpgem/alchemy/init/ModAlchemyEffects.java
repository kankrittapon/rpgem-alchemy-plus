package net.kankrittapon.rpgem.alchemy.init;

import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.kankrittapon.rpgem.core.init.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAlchemyEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT,
            RPGEMAlchemy.MODID);

    public static final Holder<MobEffect> BOUNDLESS_GRACE = MOB_EFFECTS.register("boundless_grace",
            () -> new BoundlessGraceEffect(MobEffectCategory.BENEFICIAL, 0xFFD700)
                    .addAttributeModifier(ModAttributes.REFLECT_RESIST,
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                    RPGEMAlchemy.MODID,
                                    "grace_reflect_resist"),
                            1.0,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(ModAttributes.SEAL_RESIST,
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                    RPGEMAlchemy.MODID,
                                    "grace_seal_resist"),
                            1.0,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(ModAttributes.EVASION,
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                    RPGEMAlchemy.MODID,
                                    "grace_evasion"),
                            0.8,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(ModAttributes.REFLECT_CHANCE,
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                    RPGEMAlchemy.MODID,
                                    "grace_reflect_chance"),
                            0.8,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE)); // Gold

    public static final Holder<MobEffect> JUGGERNAUT = MOB_EFFECTS.register("juggernaut",
            () -> new BoundlessGraceEffect(MobEffectCategory.BENEFICIAL, 0x8B0000)
                    .addAttributeModifier(
                            net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH,
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                    RPGEMAlchemy.MODID, "juggernaut_health"),
                            4.0,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE)); // +4 HP

    public static final Holder<MobEffect> IRON_THORNS = MOB_EFFECTS.register("iron_thorns",
            () -> new BoundlessGraceEffect(MobEffectCategory.BENEFICIAL, 0x808080)); // Grey

    public static final Holder<MobEffect> EVASION = MOB_EFFECTS.register("evasion",
            () -> new BoundlessGraceEffect(MobEffectCategory.BENEFICIAL, 0x00FFFF)); // Cyan

    public static final Holder<MobEffect> UNSTOPPABLE = MOB_EFFECTS.register("unstoppable",
            () -> new BoundlessGraceEffect(MobEffectCategory.BENEFICIAL, 0xFFFA500)
                    .addAttributeModifier(
                            net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE,
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                    RPGEMAlchemy.MODID, "unstoppable_kb"),
                            1.0,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE)); // 100% KB

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }

    // Simple Effect Class (Inner)
    public static class BoundlessGraceEffect extends MobEffect {
        protected BoundlessGraceEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
