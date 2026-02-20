package net.kankrittapon.rpgem.alchemy.init;

import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.kankrittapon.rpgem.alchemy.block.AlchemyTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAlchemyBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RPGEMAlchemy.MODID);

    public static final DeferredBlock<AlchemyTableBlock> ALCHEMY_TABLE = BLOCKS.register("alchemy_table",
            () -> new AlchemyTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5F)
                    .requiresCorrectToolForDrops().noOcclusion()));

    public static final DeferredBlock<Block> TOME_OF_FORGOTTEN_TABLE = BLOCKS.register("tome_of_forgotten_table",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).noOcclusion()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
