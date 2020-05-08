package com.stevekung.skyblockcatia.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.stevekungslib.utils.JsonUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;

public class SignSelectionList extends ExtendedList<SignSelectionList.Entry>
{
    public static final List<Entry> AUCTION_PRICES = new ArrayList<>();
    public static final List<Entry> AUCTION_QUERIES = new ArrayList<>();
    public static final List<Entry> BANK_WITHDRAW = new ArrayList<>();
    public static final List<Entry> BANK_DEPOSIT = new ArrayList<>();
    public static final List<Entry> BAZAAR_ORDER = new ArrayList<>();
    private final String title;
    private final EditSignScreen parent;

    public SignSelectionList(EditSignScreen parent, int width, int height, int top, int bottom, List<SignSelectionList.Entry> list, String title)
    {
        super(parent.getMinecraft(), width, height, top, bottom, 16);
        this.title = title;
        this.parent = parent;
        list = list.stream().distinct().collect(Collectors.toList());

        /*List<Entry> test = this.list.stream().distinct().collect(Collectors.toList());TODO

        if (this.getItemCount() > 5)
        {
            test.remove(test.size() - 1);
            this.list.remove(test.size() - 1);
        }*/
        for (int i = 0; i < list.size(); ++i)
        {
            this.addEntry(new Entry(list.get(i).getValue(), parent));
        }
        Collections.reverse(this.children());
    }

    @Override
    protected boolean isFocused()
    {
        return this.parent.getFocused() == this;
    }

    @Override
    public int getRowWidth()
    {
        return 100;
    }

    @Override
    protected int getMaxPosition()
    {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        int k = this.getRowLeft();
        int l = this.y0 + 4 - (int)this.getScrollAmount();
        this.renderList(k, l, mouseX, mouseY, partialTicks);
        this.minecraft.fontRenderer.drawString(this.title + ":", k, this.y0 - 12, 16777215);
    }

    public void add(List<Entry> toAdd, String value, EditSignScreen parent)
    {
        toAdd.add(new Entry(value, parent));
    }

    public static void clearAll()
    {
        SignSelectionList.AUCTION_PRICES.clear();
        SignSelectionList.AUCTION_QUERIES.clear();
        SignSelectionList.BANK_WITHDRAW.clear();
        SignSelectionList.BANK_DEPOSIT.clear();
        SignSelectionList.BAZAAR_ORDER.clear();
    }

    class Entry extends ExtendedList.AbstractListEntry<SignSelectionList.Entry>
    {
        private final Minecraft mc;
        private final EditSignScreen parent;
        private final String value;
        private long lastClicked;

        public Entry(String value, EditSignScreen parent)
        {
            this.mc = Minecraft.getInstance();
            this.value = value;
            this.parent = parent;
        }

        @Override
        public void render(int index, int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            this.mc.fontRenderer.drawString(this.value, rowLeft + 2, rowTop + 2, 16777215);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseEvent)
        {
            SignTileEntity sign = ((EditSignScreen)this.mc.currentScreen).tileSign;

            if (mouseEvent == 0)
            {
                sign.markDirty();

                if (Util.milliTime() - this.lastClicked < 250L)
                {
                    if (SBExtendedConfig.INSTANCE.auctionBidConfirm && NumberUtils.isNumeric(this.value))
                    {
                        int price = Integer.valueOf(this.value);

                        if (price >= SBExtendedConfig.INSTANCE.auctionBidConfirmValue)
                        {
                            this.mc.displayGuiScreen(new ConfirmScreen(confirm ->
                            {
                                if (confirm)
                                {
                                    this.confirmSign(sign);
                                }
                                else
                                {
                                    this.mc.displayGuiScreen(this.parent);
                                }
                            }
                            , LangUtils.translateComponent("message.bid_confirm_title"), LangUtils.translateComponent("message.bid_confirm")));
                        }
                        else
                        {
                            this.confirmSign(sign);
                        }
                    }
                    else
                    {
                        this.confirmSign(sign);
                    }
                    return true;
                }
                SignSelectionList.this.setSelected(this);
                sign.setText(0, JsonUtils.create(this.value));
                ((EditSignScreen)this.mc.currentScreen).textInputUtil.func_216899_b();
                this.lastClicked = Util.milliTime();
                return true;
            }
            else
            {
                return false;
            }
        }

        private void confirmSign(SignTileEntity sign)
        {
            SignSelectionList.processSignData(sign);
            this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.mc.displayGuiScreen(null);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Entry))
            {
                return false;
            }
            if (obj == this)
            {
                return true;
            }
            Entry other = (Entry) obj;
            return new EqualsBuilder().append(this.value, other.value).isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(this.value).toHashCode();
        }

        public String getValue()
        {
            return this.value;
        }
    }

    public static void processSignData(SignTileEntity sign)
    {
        ClientPlayNetHandler clientplaynethandler = Minecraft.getInstance().getConnection();

        if (clientplaynethandler != null)
        {
            clientplaynethandler.sendPacket(new CUpdateSignPacket(sign.getPos(), sign.getText(0), sign.getText(1), sign.getText(2), sign.getText(3)));
        }
        sign.setEditable(true);
    }
}