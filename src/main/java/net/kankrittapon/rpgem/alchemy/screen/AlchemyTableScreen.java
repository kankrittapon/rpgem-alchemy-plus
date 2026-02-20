package net.kankrittapon.rpgem.alchemy.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kankrittapon.rpgem.alchemy.menu.AlchemyTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlchemyTableScreen extends AbstractContainerScreen<AlchemyTableMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft",
            "textures/gui/container/dispenser.png");

    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 166;

    private static final int ARROW_X = 85;
    private static final int ARROW_Y = 30;
    private static final int ARROW_WIDTH = 8;
    // private static final int ARROW_HEIGHT = 26;

    public AlchemyTableScreen(AlchemyTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = BG_WIDTH;
        this.imageHeight = BG_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            // Logic from original: blit from 176, 0.
            // Vanilla dispenser doesn't have arrow there, but keeping logic as is.
            guiGraphics.blit(TEXTURE, x + ARROW_X, y + ARROW_Y, 176, 0, ARROW_WIDTH, menu.getScaledProgress());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
