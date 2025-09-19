/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.listener.inventory.OnEquipListener
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.Playable
 *  l2.gameserver.model.items.ItemInstance
 */
package com.lucera2.scripts.altrecbots.model.listeners;

import com.lucera2.scripts.altrecbots.model.ActionRecordingContext;
import com.lucera2.scripts.altrecbots.model.listeners.BasicPlayerListener;
import java.util.Optional;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.items.ItemInstance;

public class InventoryListener
extends BasicPlayerListener
implements OnEquipListener {
    private static final InventoryListener instance = new InventoryListener();

    public static InventoryListener getInstance() {
        return instance;
    }

    public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        Optional<ActionRecordingContext> optional = this.getRecordingContext((Creature)playable);
        if (!optional.isPresent() || !playable.isPlayer()) {
            return;
        }
        ActionRecordingContext actionRecordingContext = optional.get();
        actionRecordingContext.onEquip(itemInstance, playable.getPlayer());
    }

    public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
        Optional<ActionRecordingContext> optional = this.getRecordingContext((Creature)playable);
        if (!optional.isPresent() || !playable.isPlayer()) {
            return;
        }
        ActionRecordingContext actionRecordingContext = optional.get();
        actionRecordingContext.onUnequip(n, playable.getPlayer());
    }
}

