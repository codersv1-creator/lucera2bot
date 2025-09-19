/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.listener.actor.player.OnPlayerEnterListener
 *  l2.gameserver.model.Player
 */
package com.lucera2.scripts.altrecbots.model.listeners;

import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Player;

public class PlayerEnterListener
implements OnPlayerEnterListener {
    private static final PlayerEnterListener instance = new PlayerEnterListener();

    public static PlayerEnterListener getInstance() {
        return instance;
    }

    public void onPlayerEnter(Player player) {
    }
}

