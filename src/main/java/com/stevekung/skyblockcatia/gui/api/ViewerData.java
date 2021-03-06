package com.stevekung.skyblockcatia.gui.api;

public class ViewerData
{
    private boolean hasKills = true;
    private boolean hasDeaths = true;
    private boolean hasOthers = true;
    private boolean hasSkills = true;
    private boolean hasSlayers = true;
    private boolean hasCollections = true;
    private boolean hasMinions = true;
    private boolean hasOthersTab = true;

    public void setHasKills(boolean hasKills)
    {
        this.hasKills = hasKills;
    }

    public void setHasDeaths(boolean hasDeaths)
    {
        this.hasDeaths = hasDeaths;
    }

    public void setHasOthers(boolean hasOthers)
    {
        this.hasOthers = hasOthers;
    }

    public void setHasSkills(boolean hasSkills)
    {
        this.hasSkills = hasSkills;
    }

    public void setHasSlayers(boolean hasSlayers)
    {
        this.hasSlayers = hasSlayers;
    }

    public void setHasCollections(boolean hasCollections)
    {
        this.hasCollections = hasCollections;
    }

    public void setHasMinions(boolean hasMinions)
    {
        this.hasMinions = hasMinions;
    }

    public void setHasOthersTab(boolean hasOthersTab)
    {
        this.hasOthersTab = hasOthersTab;
    }

    public boolean hasKills()
    {
        return this.hasKills;
    }

    public boolean hasDeaths()
    {
        return this.hasDeaths;
    }

    public boolean hasOthers()
    {
        return this.hasOthers;
    }

    public boolean hasSkills()
    {
        return this.hasSkills;
    }

    public boolean hasSlayers()
    {
        return this.hasSlayers;
    }

    public boolean hasCollections()
    {
        return this.hasCollections;
    }

    public boolean hasMinions()
    {
        return this.hasMinions;
    }

    public boolean hasOthersTab()
    {
        return this.hasOthersTab;
    }
}