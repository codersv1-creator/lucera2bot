/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.Config
 *  l2.gameserver.model.items.ItemInstance
 *  l2.gameserver.templates.item.ItemTemplate
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.io.Serializable;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EquipItem
extends Action<EquipItem>
implements Serializable {
    private int itemId;
    private Integer enchant;

    public EquipItem() {
    }

    public EquipItem(int n) {
        this.itemId = n;
    }

    public int getItemId() {
        return this.itemId;
    }

    public EquipItem setItemId(int n) {
        this.itemId = n;
        return this;
    }

    public Integer getEnchant() {
        return this.enchant;
    }

    public EquipItem setEnchant(Integer n) {
        this.enchant = n;
        return this;
    }

    @Override
    public EquipItem fromLegacy(int[] nArray) {
        return this.setItemId(nArray[0]).setEnchant(Math.min(nArray[1], l2.gameserver.Config.ENCHANT_MAX));
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        ItemTemplate itemTemplate = BotUtils.getItemTemplate(this.getItemId()).orElse(null);
        if (itemTemplate == null || ArrayUtils.contains(Config.PLAYBACK_IGNORED_ITEM_IDS, itemTemplate.getItemId())) {
            return true;
        }
        ItemInstance itemInstance = altRecBot.getInventory().getItemByItemId(itemTemplate.getItemId());
        if (itemInstance != null && itemInstance.isEquipable()) {
            altRecBot.getInventory().equipItem(itemInstance);
            this.useShots(altRecBot);
        }
        altRecBot.broadcastUserInfo(true);
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.EQUIP_ITEM;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        EquipItem equipItem = (EquipItem)object;
        return new EqualsBuilder().append(this.itemId, equipItem.itemId).append(this.enchant, equipItem.enchant).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.itemId).append(this.enchant).toHashCode();
    }

    public String toString() {
        return "EquipItemParams{itemId=" + this.itemId + ", enchant=" + this.enchant + "}";
    }
}

