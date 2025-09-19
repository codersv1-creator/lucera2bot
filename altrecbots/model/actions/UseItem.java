/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import java.io.Serializable;
import org.apache.commons.lang3.ArrayUtils;

public class UseItem
extends Action<UseItem>
implements Serializable {
    private int itemId;

    public UseItem() {
    }

    public UseItem(int n) {
        this.itemId = n;
    }

    public int getItemId() {
        return this.itemId;
    }

    public UseItem setItemId(int n) {
        this.itemId = n;
        return this;
    }

    @Override
    public UseItem fromLegacy(int[] nArray) {
        this.itemId = nArray[1];
        return this;
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        if (ArrayUtils.contains(Config.PLAYBACK_IGNORED_ITEM_IDS, this.getItemId())) {
            return true;
        }
        this.useShots(altRecBot);
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.USE_ITEM;
    }
}

