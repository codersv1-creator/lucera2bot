/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.items.ItemInstance
 *  l2.gameserver.templates.item.ItemTemplate
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import java.io.Serializable;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;

public abstract class Action<SelfT extends Action>
implements Serializable {
    private transient long duration;

    public abstract SelfT fromLegacy(int[] var1);

    public boolean doIt(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        return this.doItImpl(altRecBot, actionPlaybackContext);
    }

    public abstract boolean doItImpl(AltRecBot var1, ActionPlaybackContext var2);

    public long getDuration() {
        return this.duration;
    }

    public SelfT setDuration(long l) {
        this.duration = l;
        return (SelfT)this;
    }

    public long getDuration(ActionPlaybackContext actionPlaybackContext) {
        return this.duration;
    }

    public abstract ActionType getActionType();

    protected void useShots(Player player) {
        for (ItemInstance itemInstance : player.getInventory().getItems()) {
            ItemTemplate itemTemplate;
            if (itemInstance == null || !(itemTemplate = itemInstance.getTemplate()).isShotItem()) continue;
            player.addAutoSoulShot(Integer.valueOf(itemTemplate.getItemId()));
        }
        player.autoShot();
    }
}

