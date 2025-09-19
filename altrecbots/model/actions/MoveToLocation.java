/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.lang.reference.HardReference
 *  l2.commons.threading.RunnableImpl
 *  l2.gameserver.Config
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.instances.NpcInstance
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
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MoveToLocation
extends Action<MoveToLocation>
implements Serializable {
    private Location location;
    private int offset;
    private boolean pathfinding;
    private Integer targetNpc;
    private Location fromLocation;

    public MoveToLocation() {
    }

    public MoveToLocation(Location location, int n, boolean bl) {
        this.location = location;
        this.offset = n;
        this.pathfinding = bl;
    }

    public Location getLocation() {
        return this.location;
    }

    public MoveToLocation setLocation(Location location) {
        this.location = location;
        return this;
    }

    public int getOffset() {
        return this.offset;
    }

    public MoveToLocation setOffset(int n) {
        this.offset = n;
        return this;
    }

    public Integer getTargetNpc() {
        return this.targetNpc;
    }

    public MoveToLocation setTargetNpc(Integer n) {
        this.targetNpc = n;
        return this;
    }

    public Location getFromLocation() {
        return this.fromLocation;
    }

    public MoveToLocation setFromLocation(Location location) {
        this.fromLocation = location;
        return this;
    }

    public boolean isPathfinding() {
        return this.pathfinding;
    }

    public MoveToLocation setPathfinding(boolean bl) {
        this.pathfinding = bl;
        return this;
    }

    @Override
    public MoveToLocation fromLegacy(int[] nArray) {
        return this.setLocation(new Location(nArray[0], nArray[1], nArray[2] + Config.CLIENT_Z_SHIFT).correctGeoZ()).setOffset(Math.min(nArray[3], 150)).setPathfinding(nArray[4] != 0);
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, ActionPlaybackContext actionPlaybackContext) {
        NpcInstance npcInstance;
        NpcInstance npcInstance2 = npcInstance = this.getTargetNpc() != null ? BotUtils.setMyTargetByNpcId(altRecBot, this.getTargetNpc()) : null;
        if (altRecBot.isSitting()) {
            altRecBot.standUp();
        }
        final HardReference hardReference = altRecBot.getRef();
        RunnableImpl runnableImpl = new RunnableImpl(){

            public void runImpl() throws Exception {
                Player player = (Player)hardReference.get();
                if (player == null) {
                    return;
                }
                Location location = MoveToLocation.this.getLocation().clone();
                int n = MoveToLocation.this.getOffset();
                if (npcInstance != null) {
                    location = npcInstance.getLoc().clone();
                    n = Math.max(32, npcInstance.getActingRange() - 16);
                }
                Location location2 = player.getFinalDestination();
                if (player.isMoving() && location2.equalsGeo((Object)location)) {
                    return;
                }
                player.moveToLocation(location, n, true);
            }
        };
        if (!altRecBot.isInPeaceZone()) {
            actionPlaybackContext.finish();
            return false;
        }
        if (altRecBot.isAttackingNow() || altRecBot.isCastingNow()) {
            long l = altRecBot.getAnimationEndTime();
            if (l > 0L) {
                ThreadPoolManager.getInstance().schedule(runnableImpl, Math.max(333L, l - System.currentTimeMillis()));
            }
            return false;
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
        if (altRecBot.isCastingNow() && altRecBot.getAnimationEndTime() > 0L) {
            return super.getDuration(actionPlaybackContext) + Math.max(333L, altRecBot.getAnimationEndTime() - System.currentTimeMillis());
        }
        return super.getDuration(actionPlaybackContext);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.MOVE_TO_LOCATION;
    }

    public String toString() {
        return "MoveToLocationParams{location=" + this.location + ", offset=" + this.offset + ", pathfinding=" + this.pathfinding + "}";
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MoveToLocation moveToLocation = (MoveToLocation)object;
        return new EqualsBuilder().append(this.offset, moveToLocation.offset).append(this.pathfinding, moveToLocation.pathfinding).append(this.location, moveToLocation.location).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.location).append(this.offset).append(this.pathfinding).toHashCode();
    }
}

