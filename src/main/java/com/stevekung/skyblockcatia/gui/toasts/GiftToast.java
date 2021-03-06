package com.stevekung.skyblockcatia.gui.toasts;

import java.nio.FloatBuffer;
import java.util.Random;

import com.stevekung.skyblockcatia.renderer.EquipmentOverlay;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GiftToast implements IToast
{
    private final Random rand = new Random();
    private final ResourceLocation texture;
    private final ToastUtils.ItemDrop itemDrop;
    private final long drawTime;
    private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

    public GiftToast(ItemStack itemStack, ToastUtils.DropType rarity, boolean santaGift)
    {
        this.itemDrop = new ToastUtils.ItemDrop(itemStack, rarity);
        this.drawTime = this.itemDrop.getType() == ToastUtils.DropType.SANTA_TIER ? 20000L : 10000L;
        this.texture = new ResourceLocation("skyblockcatia:textures/gui/gift_toasts_" + (santaGift ? 1 : Integer.valueOf(1 + this.rand.nextInt(2))) + ".png");
    }

    @Override
    public IToast.Visibility draw(GuiToast toastGui, long delta)
    {
        ToastUtils.ItemDrop drop = this.itemDrop;
        boolean isSanta = this.itemDrop.getType() == ToastUtils.DropType.SANTA_TIER;
        ItemStack itemStack = drop.getItemStack();
        String itemName = itemStack.getDisplayName();

        if (itemStack.getItem() == Items.enchanted_book)
        {
            itemName = itemStack.getTooltip(null, false).get(1);
        }

        toastGui.mc.getTextureManager().bindTexture(this.texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.mc.fontRendererObj.drawString(drop.getType().getColor() + JsonUtils.create(drop.getType().getName()).setChatStyle(JsonUtils.style().setBold(true)).getFormattedText(), 30, 7, 16777215);
        GuiToast.drawLongItemName(toastGui, delta, 0L, this.buffer, itemName, isSanta ? 2000L : 500L, this.drawTime, 5000L, 8000L, false);
        EquipmentOverlay.renderItem(itemStack, 8, 8);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(toastGui.mc.fontRendererObj, itemStack, 8, 8, null);
        GlStateManager.disableLighting();
        return delta >= this.drawTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }
}