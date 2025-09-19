/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.model.base.Experience
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import java.io.Serializable;
import l2.gameserver.model.base.Experience;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GainExp
extends Action<GainExp>
implements Serializable {
    private Long exp;

    public GainExp() {
    }

    public GainExp(Long l) {
        this.exp = l;
    }

    public Long getExp() {
        return this.exp;
    }

    public GainExp setExp(Long l) {
        this.exp = l;
        return this;
    }

    @Override
    public GainExp fromLegacy(int[] nArray) {
        return this.setExp(Experience.getExpForLevel((int)nArray[0]));
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        int n = altRecBot.getLevel();
        altRecBot.addExpAndSp(this.getExp() - altRecBot.getExp(), 0L);
        return true;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        GainExp gainExp = (GainExp)object;
        return new EqualsBuilder().append(this.exp, gainExp.exp).isEquals();
    }

    public String toString() {
        return "GainExp{exp=" + this.exp + "}";
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.exp).toHashCode();
    }

    @Override
    public ActionType getActionType() {
        return ActionType.GAIN_EXP;
    }
}

