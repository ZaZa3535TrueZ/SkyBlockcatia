package com.stevekung.skyblockcatia.gui.api;

import java.text.DecimalFormat;

import net.minecraft.util.text.TextFormatting;

public class SkyBlockStats
{
    private String name;
    private final float value;
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###,###.#");

    public SkyBlockStats(String name, float value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public float getValue()
    {
        return this.value;
    }

    public String getValueByString()
    {
        if (this.name == null || this.name.startsWith(TextFormatting.YELLOW.toString()))
        {
            return "";
        }
        else if (this.name.contains("Race"))
        {
            float seconds = this.value / 1000.0F;
            return FORMAT.format(seconds) + " seconds";
        }
        return FORMAT.format(this.value);
    }

    public enum Type
    {
        KILLS, DEATHS, OTHERS;
    }
}