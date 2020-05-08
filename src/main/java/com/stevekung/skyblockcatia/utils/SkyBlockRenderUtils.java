package com.stevekung.skyblockcatia.utils;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.ColorUtils.RGB;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class SkyBlockRenderUtils
{
    private static final ResourceLocation RARITY = new ResourceLocation("skyblockcatia:textures/gui/rarity.png");

    public static ItemStack getSkullItemStack(String skullId, String skullValue)
    {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        CompoundNBT compound = new CompoundNBT();
        CompoundNBT properties = new CompoundNBT();
        properties.putString("Id", skullId);
        CompoundNBT texture = new CompoundNBT();
        ListNBT list = new ListNBT();
        CompoundNBT value = new CompoundNBT();
        value.putString("Value", skullValue);
        list.add(value);
        texture.put("textures", list);
        properties.put("Properties", texture);
        compound.put("SkullOwner", properties);
        itemStack.setTag(compound);
        return itemStack;
    }

    public static void renderRarity(Slot slot, String title)
    {
        if (title != null)
        {
            if (title.equals("Auctions Browser") && slot.slotNumber >= 0 && slot.slotNumber <= 53)
            {
                return;
            }
            else if ((title.equals("Manage Auctions") || title.equals("Your Bids")) && slot.slotNumber >= 0 && slot.slotNumber <= 26)
            {
                return;
            }
            else if (title.endsWith("'s Auctions") && slot.slotNumber >= 0 && slot.slotNumber <= 35)
            {
                return;
            }
        }
        SkyBlockRenderUtils.renderRarity(slot.getStack(), slot.xPos, slot.yPos);
    }

    public static void renderRarity(ItemStack itemStack, int xPos, int yPos)
    {
        if (!itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundNBT compound = itemStack.getTag().getCompound("display");

            if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
            {
                ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                for (int j1 = 0; j1 < list.size(); ++j1)
                {
                    String lore = ITextComponent.Serializer.fromJson(list.getString(j1)).getFormattedText();
                    RGB common = ColorUtils.stringToRGB("255,255,255");
                    RGB uncommon = ColorUtils.stringToRGB("85,255,85");
                    RGB rare = ColorUtils.stringToRGB("85,85,255");
                    RGB epic = ColorUtils.stringToRGB("170,0,170");
                    RGB legendary = ColorUtils.stringToRGB("255,170,0");
                    RGB special = ColorUtils.stringToRGB("255,85,255");

                    if (lore.startsWith(TextFormatting.WHITE + "" + TextFormatting.BOLD + "COMMON"))
                    {
                        SkyBlockRenderUtils.renderRarity(xPos, yPos, common);
                    }
                    else if (lore.startsWith(TextFormatting.GREEN + "" + TextFormatting.BOLD + "UNCOMMON"))
                    {
                        SkyBlockRenderUtils.renderRarity(xPos, yPos, uncommon);
                    }
                    else if (lore.startsWith(TextFormatting.BLUE + "" + TextFormatting.BOLD + "RARE"))
                    {
                        SkyBlockRenderUtils.renderRarity(xPos, yPos, rare);
                    }
                    else if (lore.startsWith(TextFormatting.DARK_PURPLE + "" + TextFormatting.BOLD + "EPIC"))
                    {
                        SkyBlockRenderUtils.renderRarity(xPos, yPos, epic);
                    }
                    else if (lore.startsWith(TextFormatting.GOLD + "" + TextFormatting.BOLD + "LEGENDARY"))
                    {
                        SkyBlockRenderUtils.renderRarity(xPos, yPos, legendary);
                    }
                    else if (lore.startsWith(TextFormatting.LIGHT_PURPLE + "" + TextFormatting.BOLD + "SPECIAL"))
                    {
                        SkyBlockRenderUtils.renderRarity(xPos, yPos, special);
                    }
                }
            }
        }
    }

    private static void renderRarity(int xPos, int yPos, RGB color)
    {
        float alpha = ExtendedConfig.INSTANCE.itemRarityOpacity / 100.0F;
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderUtils.bindTexture(RARITY);
        RenderSystem.color4f(color.floatRed(), color.floatGreen(), color.floatBlue(), alpha);
        RenderSystem.blendFunc(770, 771);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
        AbstractGui.blit(xPos, yPos, 0, 0, 16, 16, 16, 16);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.disableAlphaTest();
    }
}