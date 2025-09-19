/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.lang.reference.HardReference
 *  l2.gameserver.model.Player
 */
package com.lucera2.scripts.altrecbots;

import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.ActionRecordingContext;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Contexts {
    private static final Logger a = LoggerFactory.getLogger(Contexts.class);
    private static final Contexts b = new Contexts();
    private final Map<HardReference<Player>, ActionPlaybackContext> c;
    private final Map<HardReference<Player>, ActionRecordingContext> d;

    protected Contexts(Map<HardReference<Player>, ActionPlaybackContext> map, Map<HardReference<Player>, ActionRecordingContext> map2) {
        this.c = map;
        this.d = map2;
    }

    private Contexts() {
        this(new ConcurrentHashMap<HardReference<Player>, ActionPlaybackContext>(), new ConcurrentHashMap<HardReference<Player>, ActionRecordingContext>());
    }

    public static Contexts getInstance() {
        return b;
    }

    public Optional<ActionPlaybackContext> getPlaybackContext(Player player) {
        return Optional.ofNullable(this.c.get(player.getRef()));
    }

    public Optional<ActionRecordingContext> getRecordingContext(Player player) {
        return Optional.ofNullable(this.d.get(player.getRef()));
    }

    public void putContext(Player player, ActionPlaybackContext actionPlaybackContext) {
        this.c.put((HardReference<Player>)player.getRef(), actionPlaybackContext);
    }

    public void putContext(Player player, ActionRecordingContext actionRecordingContext) {
        this.d.put((HardReference<Player>)player.getRef(), actionRecordingContext);
    }
}

