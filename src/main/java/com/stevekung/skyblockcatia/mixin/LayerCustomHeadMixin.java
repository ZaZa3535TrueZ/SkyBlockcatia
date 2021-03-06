package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.renderer.TileEntityEnchantedSkullRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

@Mixin(LayerCustomHead.class)
public abstract class LayerCustomHeadMixin
{
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
    private final ModelPlayer playerModel = new ModelPlayer(0.0F, false);

    @Shadow
    @Final
    @Mutable
    private ModelRenderer field_177209_a;

    @Redirect(method = "doRenderLayer(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/tileentity/TileEntitySkullRenderer.renderSkull(FFFLnet/minecraft/util/EnumFacing;FILcom/mojang/authlib/GameProfile;I)V"))
    private void renderEnchantedSkull(TileEntitySkullRenderer renderer, float x, float y, float z, EnumFacing facing, float rotation, int meta, GameProfile profile, int p_180543_8_, EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack = entity.getCurrentArmor(3);

        if (itemstack != null && itemstack.getItem() != null)
        {
            TileEntityEnchantedSkullRenderer.INSTANCE.renderSkull(x, y, z, facing, rotation, meta, profile, partialTicks, itemstack.hasEffect(), entity);
        }
    }

    @Inject(method = "doRenderLayer(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V", at = @At("RETURN"))
    private void renderGlowingLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo info)
    {
        this.renderGlowingArmor(entity, partialTicks);
    }

    private void renderGlowingArmor(EntityLivingBase entity, float partialTicks)
    {
        if (!ExtendedConfig.instance.glowingDragonArmor)
        {
            return;
        }

        ItemStack itemstack = entity.getCurrentArmor(3);

        if (itemstack != null && itemstack.getItem() == Items.skull)
        {
            String texture = "";

            if (!itemstack.hasTagCompound())
            {
                return;
            }

            String id = itemstack.getTagCompound().getCompoundTag("ExtraAttributes").getString("id");

            if (id.equals("SUPERIOR_DRAGON_HELMET"))
            {
                texture = "superior";
            }
            else if (id.equals("WISE_DRAGON_HELMET"))
            {
                texture = "wise";
            }
            else if (id.equals("YOUNG_DRAGON_HELMET"))
            {
                texture = "young";
            }
            else if (id.equals("OLD_DRAGON_HELMET") || id.equals("PROTECTOR_DRAGON_HELMET"))
            {
                texture = "white_eye";
            }
            else if (id.equals("UNSTABLE_DRAGON_HELMET"))
            {
                texture = "unstable";
            }
            else if (id.equals("STRONG_DRAGON_HELMET"))
            {
                texture = "strong";
            }
            else if (id.equals("HOLY_DRAGON_HELMET"))
            {
                texture = "holy";
            }
            else if (id.equals("DIVER_HELMET"))
            {
                texture = "diver_head";
            }

            if (texture.isEmpty())
            {
                return;
            }

            ResourceLocation resource = new ResourceLocation("skyblockcatia:textures/entity/" + texture + ".png");

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.blendFunc(1, 1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(!entity.isInvisible());
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 128.0F, 128.0F);
            GlStateManager.enableLighting();

            if (entity.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.field_177209_a.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            float f3 = 1.1875F;
            GlStateManager.scale(f3, -f3, -f3);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            GlStateManager.enableAlpha();
            Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
            this.humanoidHead.render(null, 0.0F, 0.0F, 0.0F, 180.0F, 0.0F, 0.0625F);

            int i = entity.getBrightnessForRender(partialTicks);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.popMatrix();
        }
    }
}