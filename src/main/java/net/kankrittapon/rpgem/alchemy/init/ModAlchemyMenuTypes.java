package net.kankrittapon.rpgem.alchemy.init;

import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.kankrittapon.rpgem.alchemy.menu.AlchemyTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModAlchemyMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU,
            RPGEMAlchemy.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AlchemyTableMenu>> ALCHEMY_TABLE_MENU = MENUS.register(
            "alchemy_table_menu",
            () -> IMenuTypeExtension.create(AlchemyTableMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
