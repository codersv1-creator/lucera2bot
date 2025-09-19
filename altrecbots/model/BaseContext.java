/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.lang.reference.HardReference
 *  l2.gameserver.model.Player
 */
package com.lucera2.scripts.altrecbots.model;

import java.util.Optional;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.model.Player;

public abstract class BaseContext<P extends Player> {
    private final HardReference<P> playerRef;

    protected BaseContext(HardReference<P> hardReference) {
        this.playerRef = hardReference;
    }

    public Optional<P> getPlayer() {
        return Optional.ofNullable((Player)this.playerRef.get());
    }
}

