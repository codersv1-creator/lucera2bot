/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.listener.actor.OnMoveListener
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.Player
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model.listeners;

import com.lucera2.scripts.altrecbots.model.ActionRecordingContext;
import com.lucera2.scripts.altrecbots.model.listeners.BasicPlayerListener;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.Optional;
import l2.gameserver.listener.actor.OnMoveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Location;

public class MoveListener
extends BasicPlayerListener
implements OnMoveListener {
    private static final MoveListener instance = new MoveListener();

    public static MoveListener getInstance() {
        return instance;
    }

    public void onMove(Creature creature, Location location) {
        if (creature == null || !creature.isPlayer()) {
            return;
        }
        Player player = creature.getPlayer();
        Optional<ActionRecordingContext> optional = this.getRecordingContext(creature);
        if (!optional.isPresent()) {
            return;
        }
        ActionRecordingContext actionRecordingContext = optional.get();
        if (!BotUtils.testRecordingCondition(player)) {
            actionRecordingContext.close(player);
        }
        actionRecordingContext.onMove(location);
    }
}

