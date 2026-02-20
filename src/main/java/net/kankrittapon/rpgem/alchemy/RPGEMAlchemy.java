package net.kankrittapon.rpgem.alchemy;

import com.mojang.logging.LogUtils;
import net.kankrittapon.rpgem.core.init.ModCreativeModeTabs;
import net.kankrittapon.rpgem.alchemy.init.ModAlchemyItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RPGEMAlchemy.MODID)
public class RPGEMAlchemy {
    public static final String MODID = "rpgem_alchemy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RPGEMAlchemy(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing RPGEM Alchemy+");

        net.kankrittapon.rpgem.alchemy.init.ModAlchemyBlocks.register(modEventBus);
        net.kankrittapon.rpgem.alchemy.init.ModAlchemyItems.register(modEventBus);
        net.kankrittapon.rpgem.alchemy.init.ModAlchemyBlockEntities.register(modEventBus);
        net.kankrittapon.rpgem.alchemy.init.ModAlchemyMenuTypes.register(modEventBus);
        net.kankrittapon.rpgem.alchemy.init.ModAlchemyEffects.register(modEventBus);
        net.kankrittapon.rpgem.alchemy.init.ModAlchemyVillagers.register(modEventBus);

        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModCreativeModeTabs.RPGEM_TAB_KEY) {
            // Ingredients
            event.accept(ModAlchemyItems.BONE_OF_MAZE);
            event.accept(ModAlchemyItems.COSMIC_EMERALD);
            event.accept(ModAlchemyItems.ETHERNAL_BOTTLE);
            event.accept(ModAlchemyItems.ZOMBIE_HEART);
            // Quest Fragments (ย้ายมาจาก rpgem-fairy-plus)
            event.accept(ModAlchemyItems.PIECE_OF_HEART);
            event.accept(ModAlchemyItems.PIECE_OF_BONE);
            event.accept(ModAlchemyItems.PIECE_OF_COSMIC_EMERALD);
            // Potions
            event.accept(ModAlchemyItems.INFINITE_POTION_TIER_1);
            event.accept(ModAlchemyItems.INFINITE_POTION_TIER_2);
            event.accept(ModAlchemyItems.INFINITE_POTION_TIER_3);
            // Block (Alchemy Table)
            event.accept(ModAlchemyItems.ALCHEMY_TABLE_ITEM);
        }
    }
}
