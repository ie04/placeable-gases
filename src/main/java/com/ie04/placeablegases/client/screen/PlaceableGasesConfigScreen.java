package com.ie04.placeablegases.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PlaceableGasesConfigScreen extends Screen
{
    private static final Component TITLE = Component.literal("Placeable Gases Config");
    private final Screen parent;

    public PlaceableGasesConfigScreen(Screen parent)
    {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init()
    {
        addRenderableWidget(Button.builder(Component.literal("Back"), button -> onClose())
                .bounds(width / 2 - 100, height - 32, 200, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        graphics.drawString(font, Component.literal("Gas properties are live Forge common config values."), width / 2 - 150, 55, 0xD0D0D0);
        graphics.drawString(font, Component.literal("Edit:"), width / 2 - 150, 75, 0xD0D0D0);
        graphics.drawString(font, Component.literal("config/placeablegases-common.toml"), width / 2 - 150, 88, 0xA8D8FF);
        graphics.drawString(font, Component.literal("Sections:"), width / 2 - 150, 115, 0xD0D0D0);
        graphics.drawString(font, Component.literal("gases.hydrogen"), width / 2 - 150, 128, 0xA8D8FF);
        graphics.drawString(font, Component.literal("gases.oxygen"), width / 2 - 150, 141, 0xA8D8FF);
        graphics.drawString(font, Component.literal("gases.chlorine"), width / 2 - 150, 154, 0xA8D8FF);
        graphics.drawString(font, Component.literal("Save the file to reload values while the game is running."), width / 2 - 150, 181, 0xD0D0D0);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose()
    {
        minecraft.setScreen(parent);
    }
}
