/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.Config
 *  l2.gameserver.model.items.ItemInstance
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import java.io.Serializable;
import l2.gameserver.model.items.ItemInstance;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ItemSetEnchant
extends Action<ItemSetEnchant>
implements Serializable {
    private int itemId;
    private int enchant;

    public ItemSetEnchant() {
    }

    public ItemSetEnchant(int n, int n2) {
        this.itemId = n;
        this.enchant = n2;
    }

    public int getItemId() {
        return this.itemId;
    }

    public ItemSetEnchant setItemId(int n) {
        this.itemId = n;
        return this;
    }

    public int getEnchant() {
        return this.enchant;
    }

    public ItemSetEnchant setEnchant(int n) {
        this.enchant = n;
        return this;
    }

    @Override
    public ItemSetEnchant fromLegacy(int[] nArray) {
        return this.setItemId(nArray[0]).setEnchant(Math.min(nArray[1], l2.gameserver.Config.ENCHANT_MAX));
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        if (ArrayUtils.contains(Config.PLAYBACK_IGNORED_ITEM_IDS, this.getItemId())) {
            return true;
        }
        ItemInstance itemInstance = altRecBot.getInventory().getItemByItemId(this.getItemId());
        if (itemInstance == null) {
            return true;
        }
        itemInstance.setEnchantLevel(Math.min(Config.BOT_ITEM_ENCHANT_ANIMATE_LIMIT, this.getEnchant()));
        this.useShots(altRecBot);
        altRecBot.broadcastCharInfo();
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ITEM_SET_ENCHANT;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ItemSetEnchant itemSetEnchant = (ItemSetEnchant)object;
        return new EqualsBuilder().append(this.itemId, itemSetEnchant.itemId).append(this.enchant, itemSetEnchant.enchant).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.itemId).append(this.enchant).toHashCode();
    }

    public String toString() {
        return "ItemSetEnchantParams{itemId=" + this.itemId + ", enchant=" + this.enchant + "}";
    }
}

