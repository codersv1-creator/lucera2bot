/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.listener.actor.OnAttackListener
 *  l2.gameserver.listener.actor.OnMagicUseListener
 *  l2.gameserver.listener.actor.player.OnSetClassListener
 *  l2.gameserver.listener.actor.player.OnTeleportListener
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.Skill
 *  l2.gameserver.model.entity.Reflection
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model.listeners;

import com.lucera2.scripts.altrecbots.model.ActionRecordingContext;
import com.lucera2.scripts.altrecbots.model.listeners.BasicPlayerListener;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.Optional;
import l2.gameserver.listener.actor.OnAttackListener;
import l2.gameserver.listener.actor.OnMagicUseListener;
import l2.gameserver.listener.actor.player.OnSetClassListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.utils.Location;

public class PlayerListeners
extends BasicPlayerListener
implements OnAttackListener,
OnMagicUseListener,
OnSetClassListener,
OnTeleportListener {
    private static final PlayerListeners instance = new PlayerListeners();

    public static PlayerListeners getInstance() {
        return instance;
    }

    public void onSetClass(Player player, int n) {
        Optional<ActionRecordingContext> optional = this.getRecordingContext((Creature)player);
        if (!optional.isPresent()) {
            return;
        }
        ActionRecordingContext actionRecordingContext = optional.get();
        actionRecordingContext.onSetClass(player, n);
    }

    public void onTeleport(Player player, int n, int n2, int n3, Reflection reflection) {
        Optional<ActionRecordingContext> optional = this.getRecordingContext((Creature)player);
        if (!optional.isPresent()) {
            return;
        }
        ActionRecordingContext actionRecordingContext = optional.get();
        if (!BotUtils.testRecordingCondition(player)) {
            actionRecordingContext.close(player);
            return;
        }
        actionRecordingContext.onTeleported(new Location(n, n2, n3), player);
    }

    public void onMagicUse(Creature creature, Skill skill, Creature creature2, boolean bl) {
        Optional<ActionRecordingContext> optional = this.getRecordingContext(creature);
        if (!optional.isPresent() || !creature.isPlayer()) {
            return;
        }
        ActionRecordingContext actionRecordingContext = optional.get();
        actionRecordingContext.onSkillCast(creature.getPlayer(), skill, creature2);
    }

    public void onAttack(Creature creature, Creature creature2) {
        Optional<ActionRecordingContext> optional = this.getRecordingContext(creature);
        if (!optional.isPresent() || !creature.isPlayer()) {
            return;
        }
        ActionRecordingContext actionRecordingContext = optional.get();
        actionRecordingContext.onAttack(creature.getPlayer(), creature2);
    }
}

