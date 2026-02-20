package net.kankrittapon.rpgem.alchemy.init;

import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.kankrittapon.rpgem.alchemy.block.entity.AlchemyTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAlchemyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, RPGEMAlchemy.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyTableBlockEntity>> ALCHEMY_TABLE_BE = BLOCK_ENTITIES
            .register("alchemy_table_be",
                    () -> BlockEntityType.Builder.of(AlchemyTableBlockEntity::new, ModAlchemyBlocks.ALCHEMY_TABLE.get())
                            .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
