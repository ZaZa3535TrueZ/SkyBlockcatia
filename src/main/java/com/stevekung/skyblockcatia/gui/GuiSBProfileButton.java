package com.stevekung.skyblockcatia.gui;

import java.util.List;

import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData;
import com.stevekung.skyblockcatia.gui.api.ProfileDataCallback;
import com.stevekung.skyblockcatia.utils.CommonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiSBProfileButton extends GuiButton
{
    private List<ProfileDataCallback> profiles;
    private final ProfileDataCallback callback;
    private final Minecraft mc;

    public GuiSBProfileButton(int id, int x, int y, int width, int height, ProfileDataCallback callback)
    {
        super(id, x, y, width, height, callback.getProfileName());
        this.mc = Minecraft.getMinecraft();
        this.callback = callback;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height)
        {
            this.mc.displayGuiScreen(new GuiSkyBlockData(this.profiles, this.callback));
            return true;
        }
        return false;
    }

    public long getLastSave()
    {
        return this.callback.getLastSave();
    }

    public void setProfileList(List<ProfileDataCallback> profiles)
    {
        this.profiles = profiles;
    }

    public String getLastActive()
    {
        String time = "Invalid data!";

        if (this.callback.getLastSave() > 0)
        {
            time = CommonUtils.getRelativeTime(this.callback.getLastSave());
        }
        return "Last active: " + time;
    }
}