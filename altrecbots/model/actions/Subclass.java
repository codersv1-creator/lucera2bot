/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.lang.reference.HardReference
 *  l2.commons.threading.RunnableImpl
 *  l2.gameserver.Config
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.GameObject
 *  l2.gameserver.model.GameObjectsStorage
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.base.ClassId
 *  l2.gameserver.model.base.Experience
 *  l2.gameserver.model.instances.NpcInstance
 *  l2.gameserver.network.l2.s2c.L2GameServerPacket
 *  l2.gameserver.network.l2.s2c.MagicSkillUse
 *  l2.gameserver.network.l2.s2c.SocialAction
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Subclass
extends Action<Subclass>
implements Serializable {
    private SubclassActionType subclassActionType;
    private Integer targetNpcId;
    private Location location;
    private ClassId classId;
    private ClassId newClassId;
    private Long exp;

    public Subclass() {
    }

    public Subclass(SubclassActionType subclassActionType, Integer n, ClassId classId, ClassId classId2) {
        this.subclassActionType = subclassActionType;
        this.targetNpcId = n;
        this.classId = classId;
        this.newClassId = classId2;
    }

    private static final Location getLegacyLocation(int n, int n2, Integer n3) {
        if (n3 != null && n3 > 0) {
            NpcInstance npcInstance = null;
            long l = Long.MAX_VALUE;
            for (NpcInstance npcInstance2 : GameObjectsStorage.getAllByNpcId((int)n3, (boolean)true)) {
                long l2 = npcInstance2.getSqDistance(n, n2);
                if (l2 >= l) continue;
                npcInstance = npcInstance2;
                l = l2;
            }
            if (npcInstance != null) {
                return new Location(n, n2, npcInstance.getZ() + Config.CLIENT_Z_SHIFT).correctGeoZ();
            }
        }
        return new Location(n, n2, Short.MAX_VALUE).correctGeoZ();
    }

    public SubclassActionType getSubclassActionType() {
        return this.subclassActionType;
    }

    public Subclass setSubclassActionType(SubclassActionType subclassActionType) {
        this.subclassActionType = subclassActionType;
        return this;
    }

    public Integer getTargetNpcId() {
        return this.targetNpcId;
    }

    public Subclass setTargetNpcId(Integer n) {
        this.targetNpcId = n;
        return this;
    }

    public ClassId getClassId() {
        return this.classId;
    }

    public Subclass setClassId(ClassId classId) {
        this.classId = Objects.requireNonNull(classId);
        return this;
    }

    public ClassId getNewClassId() {
        return this.newClassId;
    }

    public Subclass setNewClassId(ClassId classId) {
        this.newClassId = Objects.requireNonNull(classId);
        return this;
    }

    public Long getExp() {
        return this.exp;
    }

    public Subclass setExp(Long l) {
        this.exp = l;
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public Subclass setLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public Subclass fromLegacy(int[] nArray) {
        switch (nArray[0]) {
            case 0: {
                return this.setSubclassActionType(SubclassActionType.SetNew).setClassId(BotUtils.toClassId(nArray[1]).get()).setTargetNpcId(nArray[3] != 0 ? Integer.valueOf(nArray[3]) : null).setLocation(Subclass.getLegacyLocation(nArray[4], nArray[5], nArray[3]));
            }
            case 1: {
                return this.setSubclassActionType(SubclassActionType.Change).setClassId(BotUtils.toClassId(nArray[1]).get()).setExp(Experience.getExpForLevel((int)nArray[2])).setTargetNpcId(nArray[3] != 0 ? Integer.valueOf(nArray[3]) : null).setLocation(Subclass.getLegacyLocation(nArray[4], nArray[5], nArray[3]));
            }
            case 2: {
                return this.setSubclassActionType(SubclassActionType.AddNew).setClassId(BotUtils.toClassId(nArray[1]).get()).setTargetNpcId(nArray[3] != 0 ? Integer.valueOf(nArray[3]) : null).setLocation(Subclass.getLegacyLocation(nArray[4], nArray[5], nArray[3]));
            }
            case 3: {
                return this.setSubclassActionType(SubclassActionType.Replace).setClassId(BotUtils.toClassId(nArray[1]).get()).setNewClassId(BotUtils.toClassId(nArray[2]).get()).setTargetNpcId(nArray[3] != 0 ? Integer.valueOf(nArray[3]) : null).setLocation(Subclass.getLegacyLocation(nArray[4], nArray[5], nArray[3]));
            }
        }
        return null;
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        final HardReference hardReference = altRecBot.getRef();
        RunnableImpl runnableImpl = new RunnableImpl(){

            public void runImpl() throws Exception {
                Player player = (Player)hardReference.get();
                if (player == null || !(player instanceof AltRecBot)) {
                    return;
                }
                AltRecBot altRecBot = (AltRecBot)player;
                NpcInstance npcInstance = null;
                if (Subclass.this.getTargetNpcId() != null) {
                    npcInstance = BotUtils.setMyTargetByNpcId(altRecBot, Subclass.this.getTargetNpcId());
                }
                switch (Subclass.this.getSubclassActionType()) {
                    case SetNew: {
                        player.setClassId(Subclass.this.classId.getId(), true, false);
                        if (npcInstance == null || !(altRecBot.getDistance((GameObject)npcInstance) < (double)(npcInstance.getActingRange() + 128))) break;
                        altRecBot.broadcastPacket(new L2GameServerPacket[]{new SocialAction(altRecBot.getObjectId(), 16)});
                        altRecBot.broadcastPacket(new L2GameServerPacket[]{new SocialAction(altRecBot.getObjectId(), 3)});
                        break;
                    }
                    case Change: {
                        altRecBot.setActiveSubClass(Subclass.this.getClassId().getId(), false);
                        if (Subclass.this.getExp() == null) break;
                        altRecBot.addExpAndSp(Subclass.this.getExp() - altRecBot.getExp(), 0L);
                        break;
                    }
                    case AddNew: {
                        altRecBot.setActiveSubClass(Subclass.this.getClassId().getId(), false);
                        if (npcInstance == null || !(altRecBot.getDistance((GameObject)npcInstance) < (double)npcInstance.getActingRange()) || Subclass.this.getExp() == null) break;
                        altRecBot.addExpAndSp(Subclass.this.getExp() - altRecBot.getExp(), 0L);
                        break;
                    }
                    case Replace: {
                        altRecBot.setActiveSubClass(Subclass.this.getClassId().getId(), false);
                        if (npcInstance == null || !(altRecBot.getDistance((GameObject)npcInstance) < (double)npcInstance.getActingRange())) break;
                        player.broadcastPacket(new L2GameServerPacket[]{new SocialAction(player.getObjectId(), 3)});
                        player.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse((Creature)player, (Creature)player, 4339, 1, 0, 0L)});
                    }
                }
                int n = altRecBot.getActiveClassId();
                ClassId classId2 = Stream.of(ClassId.values()).filter(classId -> classId.getId() == n).findAny().get();
                int n2 = Math.max(altRecBot.getLevel(), Player.EXPERTISE_LEVELS[classId2.getLevel()]);
                altRecBot.addExpAndSp(Experience.getExpForLevel((int)n2) - altRecBot.getExp(), 0L);
                player.broadcastCharInfo();
            }
        };
        if (altRecBot.isSitting()) {
            altRecBot.standUp();
        }
        NpcInstance npcInstance = null;
        if (this.getTargetNpcId() != null && (npcInstance = BotUtils.setMyTargetByNpcId(altRecBot, this.getTargetNpcId())) != null) {
            double d;
            double d2 = 0.0;
            d2 = altRecBot.getDistance((GameObject)npcInstance);
            if (d > (double)npcInstance.getActingRange()) {
                altRecBot.moveToLocation(npcInstance.getLoc(), Math.max(48, npcInstance.getActingRange() / 2), true);
                ThreadPoolManager.getInstance().schedule(runnableImpl, (long)(d2 / (double)altRecBot.getRunSpeed() * 1000.0));
                return true;
            }
        }
        runnableImpl.run();
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
                return (long)(d2 / (double)altRecBot.getRunSpeed() * 1000.0) + super.getDuration();
            }
        }
        return super.getDuration(actionPlaybackContext);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SUBCLASS;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Subclass subclass = (Subclass)object;
        return new EqualsBuilder().append((Object)this.subclassActionType, (Object)subclass.subclassActionType).append(this.targetNpcId, subclass.targetNpcId).append(this.classId, subclass.classId).append(this.newClassId, subclass.newClassId).append(this.exp, subclass.exp).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.subclassActionType).append(this.targetNpcId).append(this.classId).append(this.newClassId).append(this.exp).toHashCode();
    }

    public String toString() {
        return "SubclassParams{subclassActionType=" + this.subclassActionType + ", targetNpcId=" + this.targetNpcId + ", classId=" + this.classId + ", newClassId=" + this.newClassId + ", exp=" + this.exp + "}";
    }

    public static enum SubclassActionType {
        SetNew,
        Change,
        AddNew,
        Replace;

    }
}

