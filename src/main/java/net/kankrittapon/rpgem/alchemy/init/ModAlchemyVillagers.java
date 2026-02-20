package net.kankrittapon.rpgem.alchemy.init;

import com.google.common.collect.ImmutableSet;
import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAlchemyVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE,
            RPGEMAlchemy.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister
            .create(Registries.VILLAGER_PROFESSION, RPGEMAlchemy.MODID);

    public static final DeferredHolder<PoiType, PoiType> ALCHEMIST_POI = POI_TYPES.register("alchemist_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(ModAlchemyBlocks.ALCHEMY_TABLE.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> ALCHEMIST = VILLAGER_PROFESSIONS
            .register("alchemist",
                    () -> new VillagerProfession("alchemist", x -> x.value() == ALCHEMIST_POI.get(),
                            x -> x.value() == ALCHEMIST_POI.get(), ImmutableSet.of(), ImmutableSet.of(),
                            SoundEvents.VILLAGER_WORK_CLERIC));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
