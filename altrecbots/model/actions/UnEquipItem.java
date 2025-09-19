/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.model.items.Inventory
 *  l2.gameserver.model.items.ItemInstance
 *  l2.gameserver.model.items.PcInventory
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import java.io.Serializable;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UnEquipItem
extends Action<UnEquipItem>
implements Serializable {
    private int slot;

    public UnEquipItem() {
    }

    public UnEquipItem(int n) {
        this.slot = n;
    }

    public int getSlot() {
        return this.slot;
    }

    public UnEquipItem setSlot(int n) {
        this.slot = n;
        return this;
    }

    @Override
    public UnEquipItem fromLegacy(int[] nArray) {
        int n = PcInventory.getPaperdollIndex((int)nArray[0]);
        if (n >= 0) {
            return this.setSlot(n);
        }
        return this;
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        AltRecBot.AltRecBotInventory altRecBotInventory = altRecBot.getInventory();
        ItemInstance itemInstance = altRecBotInventory.getPaperdollItem(Math.max(0, Math.min(this.getSlot(), Inventory.PAPERDOLL_MAX - 1)));
        if (itemInstance == null) {
            return true;
        }
        altRecBot.getInventory().unEquipItem(itemInstance);
        altRecBot.broadcastCharInfo();
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.UNEQUIP_SLOT;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        UnEquipItem unEquipItem = (UnEquipItem)object;
        return new EqualsBuilder().append(this.slot, unEquipItem.slot).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.slot).toHashCode();
    }

    public String toString() {
        return "UnEquipItemParams{slot=" + this.slot + "}";
    }
}

