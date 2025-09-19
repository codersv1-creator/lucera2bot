/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.lang.reference.HardReference
 *  l2.commons.threading.RunnableImpl
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.GameObject
 *  l2.gameserver.model.Skill
 *  l2.gameserver.model.instances.NpcInstance
 *  l2.gameserver.tables.SkillTable
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.ThreadPoolManager;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.io.Serializable;
import java.util.Optional;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SkillCast
extends Action<SkillCast>
implements Serializable {
    private int skillId;
    private Integer targetNpcId;
    private Location targetLoc;
    private boolean forceUse;

    public SkillCast() {
    }

    public SkillCast(int n) {
        this.skillId = n;
    }

    public SkillCast(int n, int n2, Location location, boolean bl) {
        this.skillId = n;
        this.targetNpcId = n2;
        this.targetLoc = location;
        this.forceUse = bl;
    }

    public int getSkillId() {
        return this.skillId;
    }

    public SkillCast setSkillId(int n) {
        this.skillId = n;
        return this;
    }

    public Integer getTargetNpcId() {
        return this.targetNpcId;
    }

    public SkillCast setTargetNpcId(Integer n) {
        this.targetNpcId = n;
        return this;
    }

    public Location getTargetLoc() {
        return this.targetLoc;
    }

    public SkillCast setTargetLoc(Location location) {
        this.targetLoc = location;
        return this;
    }

    public boolean isForceUse() {
        return this.forceUse;
    }

    public SkillCast setForceUse(boolean bl) {
        this.forceUse = bl;
        return this;
    }

    @Override
    public SkillCast fromLegacy(int[] nArray) {
        return this.setSkillId(nArray[0]).setTargetNpcId(nArray[1] > 0 ? Integer.valueOf(nArray[1]) : null).setForceUse(nArray[2] != 0).setTargetLoc(nArray[3] != 0 && nArray[4] != 0 ? new Location(nArray[3], nArray[4], Short.MAX_VALUE) : null);
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        NpcInstance npcInstance;
        int n;
        if (altRecBot.isSitting()) {
            altRecBot.standUp();
        }
        if ((n = SkillTable.getInstance().getMaxLevel(this.getSkillId())) < 1) {
            return true;
        }
        final Skill skill = SkillTable.getInstance().getInfo(this.getSkillId(), n);
        if (skill == null) {
            return true;
        }
        if (!skill.altUse() && altRecBot.isMoving()) {
            altRecBot.stopMove();
        }
        final HardReference hardReference = altRecBot.getRef();
        RunnableImpl runnableImpl = new RunnableImpl(){

            public void runImpl() throws Exception {
                AltRecBot altRecBot = (AltRecBot)((Object)hardReference.get());
                if (altRecBot == null) {
                    return;
                }
                try {
                    if (!skill.altUse() && altRecBot.isMoving()) {
                        altRecBot.stopMove();
                    }
                    Creature creature = null;
                    if (SkillCast.this.getTargetNpcId() != null) {
                        NpcInstance npcInstance = BotUtils.setMyTargetByNpcId(altRecBot, SkillCast.this.getTargetNpcId());
                        if (npcInstance != null) {
                            creature = skill.getAimingTarget((Creature)altRecBot, (GameObject)npcInstance);
                        }
                    } else {
                        altRecBot.setTarget((GameObject)altRecBot);
                        creature = skill.getAimingTarget((Creature)altRecBot, altRecBot.getTarget());
                    }
                    if (skill.altUse()) {
                        altRecBot.altUseSkill(skill, creature);
                    } else if (skill.checkCondition((Creature)altRecBot, creature, SkillCast.this.isForceUse(), false, true)) {
                        if (creature.getEffectList().containEffectFromSkills(new int[]{skill.getId()})) {
                            creature.getEffectList().stopEffect(skill);
                        }
                        altRecBot.doCast(skill, creature, SkillCast.this.isForceUse());
                    }
                } catch (Exception exception) {
                    // empty catch block
                }
            }
        };
        boolean bl = true;
        if (this.getTargetNpcId() != null && !skill.altUse() && this.getTargetNpcId() != null && (npcInstance = BotUtils.setMyTargetByNpcId(altRecBot, this.getTargetNpcId())) != null) {
            double d;
            double d2 = 0.0;
            d2 = altRecBot.getDistance((GameObject)npcInstance);
            if (d > (double)npcInstance.getActingRange()) {
                long l = (long)(d2 / (double)altRecBot.getRunSpeed() * 1000.0);
                ThreadPoolManager.getInstance().schedule(runnableImpl, l + 333L);
                bl = false;
            }
        }
        if (bl) {
            ThreadPoolManager.getInstance().execute(runnableImpl);
        }
        return true;
    }

    @Override
    public long getDuration(ActionPlaybackContext actionPlaybackContext) {
        Optional optional = actionPlaybackContext.getPlayer();
        if (!optional.isPresent()) {
            return super.getDuration(actionPlaybackContext);
        }
        AltRecBot altRecBot = (AltRecBot)((Object)optional.get());
        NpcInstance npcInstance = null;
        if (this.getTargetNpcId() != null && (npcInstance = BotUtils.setMyTargetByNpcId(altRecBot, this.getTargetNpcId())) != null) {
            double d;
            double d2 = 0.0;
            d2 = altRecBot.getDistance((GameObject)npcInstance);
            if (d > (double)npcInstance.getActingRange()) {
                return (long)(d2 / (double)altRecBot.getRunSpeed() * 1000.0) + super.getDuration() + 500L;
            }
        }
        return super.getDuration(actionPlaybackContext);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SKILL_CAST;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        SkillCast skillCast = (SkillCast)object;
        return new EqualsBuilder().append(this.skillId, skillCast.skillId).append(this.targetNpcId, skillCast.targetNpcId).append(this.forceUse, skillCast.forceUse).append(this.targetLoc, skillCast.targetLoc).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.skillId).append(this.targetNpcId).append(this.targetLoc).append(this.forceUse).toHashCode();
    }

    public String toString() {
        return "SkillCastParams{skillId=" + this.skillId + ", targetNpcId=" + this.targetNpcId + ", targetLoc=" + this.targetLoc + ", forceUse=" + this.forceUse + "}";
    }
}

