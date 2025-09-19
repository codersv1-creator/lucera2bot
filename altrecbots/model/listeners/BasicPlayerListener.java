/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.Player
 */
package com.lucera2.scripts.altrecbots.model.listeners;

import com.lucera2.scripts.altrecbots.model.ActionRecordingContext;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.Optional;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;

public class BasicPlayerListener {
    protected Optional<ActionRecordingContext> getRecordingContext(Creature creature) {
        if (creature == null || !creature.isPlayer() || BotUtils.isBot(creature)) {
            return Optional.empty();
        }
        return ActionRecordingContext.getRecordingContext((Player)creature);
    }
}

