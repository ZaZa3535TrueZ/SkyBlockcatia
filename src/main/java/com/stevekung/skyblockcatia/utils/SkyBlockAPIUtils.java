package com.stevekung.skyblockcatia.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

public class SkyBlockAPIUtils
{
    private static final Gson GSON = new Gson();
    public static int MAX_FAIRY_SOULS;
    public static SupportedPack PACKS;
    private static String API_KEY;
    public static String PLAYER_NAME;
    public static String PLAYER_UUID;
    public static String SKYBLOCK_PROFILE;
    public static String SKYBLOCK_PROFILES;
    public static String SKYBLOCK_AUCTION;
    public static String BAZAAR;
    public static String GUILD;

    public static void setApiKey()
    {
        LoggerIN.info("Setting an API Key");
        SkyBlockAPIUtils.API_KEY = ConfigManagerIN.hypixelApiKey;
        PLAYER_NAME = "https://api.hypixel.net/player?key=" + API_KEY + "&name=";
        PLAYER_UUID = "https://api.hypixel.net/player?key=" + API_KEY + "&uuid=";
        SKYBLOCK_PROFILE = "https://api.hypixel.net/skyblock/profile?key=" + API_KEY + "&profile=";
        SKYBLOCK_PROFILES = "https://api.hypixel.net/skyblock/profiles?key=" + API_KEY + "&uuid=";
        SKYBLOCK_AUCTION = "https://api.hypixel.net/skyblock/auction?key=" + API_KEY + "&profile=";
        BAZAAR = "https://api.hypixel.net/skyblock/bazaar?key=" + API_KEY;
        GUILD = "https://api.hypixel.net/guild?key=" + API_KEY + "&player=";
    }

    public static void setApiKeyFromServer(String uuid)
    {
        ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Hypixel API Key", ConfigManagerIN.hypixelApiKey).set(uuid);
        ConfigManagerIN.hypixelApiKey = uuid;
        ConfigManagerIN.getConfig().save();
        SkyBlockAPIUtils.setApiKey();
    }

    public static void getFairySouls()
    {
        try
        {
            MAX_FAIRY_SOULS = GSON.fromJson(DataGetter.get("api/stats_bonuses/misc/max_fairy_souls.json"), MaxFairySouls.class).getMaxFairySouls();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MAX_FAIRY_SOULS = 209;
        }
    }

    public static void getSupportedPackNames()
    {
        try
        {
            PACKS = GSON.fromJson(DataGetter.get("pack_name.json"), SupportedPack.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static List<ItemStack> decodeItem(JsonObject currentProfile, SkyBlockInventoryType type)
    {
        if (currentProfile.has(type.getApiName()))
        {
            List<ItemStack> itemStack = new ArrayList<>();
            byte[] decode = Base64.getDecoder().decode(currentProfile.get(type.getApiName()).getAsJsonObject().get("data").getAsString().replace("\\u003d", "="));

            try
            {
                NBTTagCompound compound = CompressedStreamTools.readCompressed(new ByteArrayInputStream(decode));
                NBTTagList list = compound.getTagList("i", 10);
                int dummyIndex = -1;
                boolean hasDummy = type == SkyBlockInventoryType.ACCESSORY_BAG || type == SkyBlockInventoryType.POTION_BAG || type == SkyBlockInventoryType.FISHING_BAG || type == SkyBlockInventoryType.QUIVER;

                for (int i = 0; hasDummy && i < list.tagCount(); ++i)
                {
                    if (list.getCompoundTagAt(i).toString().contains("Go Back"))
                    {
                        dummyIndex = i;
                        break;
                    }
                }
                for (int i = type == SkyBlockInventoryType.INVENTORY ? 9 : 0; i < list.tagCount(); ++i)
                {
                    if (hasDummy && i >= dummyIndex - 3)
                    {
                        break;
                    }
                    itemStack.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
                }
                if (type == SkyBlockInventoryType.INVENTORY)
                {
                    for (int i = 0; i < 9; ++i)
                    {
                        itemStack.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return itemStack;
        }
        else
        {
            List<ItemStack> itemStack = new ArrayList<>();
            ItemStack barrier = new ItemStack(Blocks.barrier);
            barrier.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.RED + type.getName() + " is not available!");

            for (int i = 0; i < 36; ++i)
            {
                itemStack.add(barrier);
            }
            return itemStack;
        }
    }
}