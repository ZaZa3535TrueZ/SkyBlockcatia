package com.stevekung.skyblockcatia.gui;

import java.lang.reflect.Field;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiButtonItem extends GuiButton
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/blank.png");
    private final int originalX;
    private final int potionX;
    private ItemStack itemStack;
    private final Minecraft mc;
    private String customName;

    // Patcher Compatibility
    private static Class<?> patcherConfig;
    private boolean patcherInventoryPosition;

    // Vanilla Enhancements Compatibility
    private static Class<?> vanillaEnConfig;
    private boolean vanillaEnFixInventory;

    public GuiButtonItem(int buttonID, int xPos, int yPos, ItemStack itemStack)
    {
        this(buttonID, xPos, yPos, xPos, itemStack, itemStack.getDisplayName());
    }

    public GuiButtonItem(int buttonID, int xPos, int yPos, int potionX, ItemStack itemStack)
    {
        this(buttonID, xPos, yPos, potionX, itemStack, itemStack.getDisplayName());
    }

    public GuiButtonItem(int buttonID, int xPos, int yPos, ItemStack itemStack, String customName)
    {
        this(buttonID, xPos, yPos, xPos, itemStack, customName);
    }

    public GuiButtonItem(int buttonID, int xPos, int yPos, int potionX, ItemStack itemStack, String customName)
    {
        super(buttonID, xPos, yPos, 18, 18, "");
        this.originalX = xPos;
        this.potionX = potionX;
        this.itemStack = itemStack;
        this.mc = Minecraft.getMinecraft();
        this.customName = customName;

        if (SkyBlockcatiaMod.isPatcherLoaded)
        {
            try
            {
                patcherConfig = Class.forName("club.sk1er.patcher.config.PatcherConfig");
                Field inventoryPosition = patcherConfig.getDeclaredField("inventoryPosition");
                this.patcherInventoryPosition = inventoryPosition.getBoolean(patcherConfig);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (SkyBlockcatiaMod.isVanillaEnhancementsLoaded)
        {
            try
            {
                vanillaEnConfig = Class.forName("com.orangemarshall.enhancements.config.Config");
                Object instance = vanillaEnConfig.getDeclaredMethod("instance").invoke(vanillaEnConfig);
                Field fixInventory = instance.getClass().getDeclaredField("fixInventory");
                this.vanillaEnFixInventory = fixInventory.getBoolean(instance);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (!(this.vanillaEnFixInventory || this.patcherInventoryPosition || this.mc.currentScreen instanceof GuiSkyBlockData))
        {
            boolean hasVisibleEffect = false;

            for (PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects())
            {
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];

                if (potion.shouldRender(potioneffect))
                {
                    hasVisibleEffect = true;
                    break;
                }
            }

            if (!this.mc.thePlayer.getActivePotionEffects().isEmpty() && hasVisibleEffect)
            {
                this.xPosition = this.potionX;
            }
            else
            {
                this.xPosition = this.originalX;
            }
        }

        boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        if (this.visible)
        {
            if (this.itemStack.getItem() == Items.nether_star)
            {
                ItemStack skyBlockMenu = this.itemStack.copy();
                NBTTagList list = new NBTTagList();
                skyBlockMenu.setStackDisplayName("SkyBlock Menu");
                list.appendTag(new NBTTagString(EnumChatFormatting.GRAY + "View all of your SkyBlock"));
                skyBlockMenu.getTagCompound().getCompoundTag("display").setTag("Lore", list);
                this.itemStack = skyBlockMenu;
            }

            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, flag ? 18 : 0, 0, this.width, this.height, 36, 18);

            GlStateManager.enableDepth();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(this.itemStack, this.xPosition + 1, this.yPosition + 1);
        }
    }

    public String getName()
    {
        return this.customName;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    public void setName(String name)
    {
        this.customName = name;
    }

    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }
}