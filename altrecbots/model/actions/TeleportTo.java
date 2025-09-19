/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.threading.RunnableImpl
 *  l2.gameserver.Config
 *  l2.gameserver.instancemanager.ReflectionManager
 *  l2.gameserver.model.World
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.ThreadPoolManager;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import java.io.Serializable;
import java.util.Optional;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.World;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TeleportTo
extends Action<TeleportTo>
implements Serializable {
    private Location location;

    public TeleportTo() {
    }

    public TeleportTo(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public TeleportTo setLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public TeleportTo fromLegacy(int[] nArray) {
        return this.setLocation(new Location(nArray[0], nArray[1], nArray[2] + Config.CLIENT_Z_SHIFT));
    }

    @Override
    public boolean doItImpl(AltRecBot altRecBot, final ActionPlaybackContext actionPlaybackContext) {
        int n = World.validCoordX((int)this.getLocation().getX());
        int n2 = World.validCoordY((int)this.getLocation().getY());
        int n3 = World.validCoordZ((int)this.getLocation().getZ());
        altRecBot.teleToLocation(n, n2, n3, ReflectionManager.DEFAULT);
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            public void runImpl() throws Exception {
                Optional optional = actionPlaybackContext.getPlayer();
                if (!optional.isPresent()) {
                    return;
                }
                ((AltRecBot)((Object)optional.get())).onTeleported();
            }
        }, 1000L);
        return true;
    }

    @Override
    public long getDuration(ActionPlaybackContext actionPlaybackContext) {
        return super.getDuration(actionPlaybackContext) + 1000L;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TELEPORT_TO;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TeleportTo teleportTo = (TeleportTo)object;
        return new EqualsBuilder().append(this.location, teleportTo.location).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.location).toHashCode();
    }

    public String toString() {
        return "TeleportToParams{location=" + this.location + "}";
    }
}

