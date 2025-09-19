/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.instances.NpcInstance
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.io.Serializable;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Attack
extends Action<Attack>
implements Serializable {
    private int targetNpcId;
    private Location targetLoc;

    public Attack() {
    }

    public Attack(int n, Location location) {
        this.targetNpcId = n;
        this.targetLoc = location;
    }

    public int getTargetNpcId() {
        return this.targetNpcId;
    }

    public Attack setTargetNpcId(int n) {
        this.targetNpcId = n;
        return this;
    }

    public Location getTargetLoc() {
        return this.targetLoc;
    }

    public Attack setTargetLoc(Location location) {
        this.targetLoc = location;
        return this;
    }

    @Override
    public Attack fromLegacy(int[] nArray) {
        return this.setTargetNpcId(nArray[0]).setTargetLoc(new Location(nArray[1], nArray[2], Short.MAX_VALUE).correctGeoZ());
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        NpcInstance npcInstance;
        if (altRecBot.isMoving()) {
            altRecBot.stopMove();
        }
        if (this.getTargetNpcId() > 0 && (npcInstance = BotUtils.setMyTargetByNpcId(altRecBot, this.getTargetNpcId())) != null) {
            altRecBot.doAttack((Creature)npcInstance);
        }
        return true;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ATTACK;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Attack attack = (Attack)object;
        return new EqualsBuilder().append(this.targetNpcId, attack.targetNpcId).append(this.targetLoc, attack.targetLoc).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.targetNpcId).append(this.targetLoc).toHashCode();
    }

    public String toString() {
        return "AttackParams{targetNpcId=" + this.targetNpcId + ", targetLoc=" + this.targetLoc + "}";
    }
}

