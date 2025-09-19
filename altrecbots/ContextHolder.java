/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.model.Player
 */
package com.lucera2.scripts.altrecbots;

import com.lucera2.scripts.altrecbots.model.BaseContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.gameserver.model.Player;

public class ContextHolder<CTX extends BaseContext> {
    private final ReadWriteLock a = new ReentrantReadWriteLock();
    private final Lock b = this.a.readLock();
    private final Lock c = this.a.writeLock();
    private final Map<Integer, CTX> d = new HashMap<Integer, CTX>();

    public Optional<CTX> getContext(Player player) {
        this.b.lock();
        try {
            Optional<BaseContext> optional = Optional.ofNullable((BaseContext)this.d.get(player.getObjectId()));
            return optional;
        } finally {
            this.b.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CTX addContext(Player player, CTX CTX) {
        this.c.lock();
        try {
            this.d.put(player.getObjectId(), (BaseContext)Objects.requireNonNull(CTX));
            CTX CTX2 = CTX;
            return CTX2;
        } finally {
            this.c.unlock();
        }
    }

    public CTX removeContext(int n) {
        this.c.lock();
        try {
            BaseContext baseContext = (BaseContext)this.d.remove(n);
            return (CTX)baseContext;
        } finally {
            this.c.unlock();
        }
    }

    public CTX removeContext(Player player) {
        return this.removeContext(player.getObjectId());
    }
}

