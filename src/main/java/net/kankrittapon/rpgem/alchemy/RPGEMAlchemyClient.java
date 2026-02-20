package net.kankrittapon.rpgem.alchemy;

import net.kankrittapon.rpgem.alchemy.init.ModAlchemyMenuTypes;
import net.kankrittapon.rpgem.alchemy.screen.AlchemyTableScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = RPGEMAlchemy.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RPGEMAlchemyClient {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModAlchemyMenuTypes.ALCHEMY_TABLE_MENU.get(), AlchemyTableScreen::new);
    }
}
