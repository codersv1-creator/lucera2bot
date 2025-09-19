/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.listener.zone.OnZoneEnterLeaveListener
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.Zone
 */
package com.lucera2.scripts.altrecbots.model.listeners;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.ActionRecordingContext;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.Optional;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;

public class ZoneListener
implements OnZoneEnterLeaveListener {
    private static final ZoneListener instance = new ZoneListener();

    public static ZoneListener getInstance() {
        return instance;
    }

    public void onZoneEnter(Zone zone, Creature creature) {
        if (!Config.AUTO_RECORD_PLAYER_ACTIONS || creature == null || !creature.isPlayer() || BotUtils.isBot(creature)) {
            return;
        }
        Player player = creature.getPlayer();
        if (!BotUtils.testRecordingCondition(player)) {
            Optional<ActionRecordingContext> optional = ActionRecordingContext.getRecordingContext(player);
            if (optional.isPresent()) {
                optional.get().close(player);
                return;
            }
            return;
        }
        Optional<ActionRecordingContext> optional = ActionRecordingContext.getRecordingContext(player);
        if (!optional.isPresent()) {
            ActionRecordingContext.openContext(player);
        }
    }

    public void onZoneLeave(Zone zone, Creature creature) {
        Optional<ActionRecordingContext> optional;
        if (!Config.AUTO_RECORD_PLAYER_ACTIONS || creature == null || !creature.isPlayer() || BotUtils.isBot(creature)) {
            return;
        }
        Player player = creature.getPlayer();
        if (!BotUtils.testRecordingCondition(player) && (optional = ActionRecordingContext.getRecordingContext(player)).isPresent()) {
            optional.get().close(player);
        }
    }
}

