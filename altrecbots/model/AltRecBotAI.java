/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.threading.RunnableImpl
 *  l2.gameserver.ai.PlayerAI
 *  l2.gameserver.model.Player
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.ThreadPoolManager;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import java.util.Objects;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ai.PlayerAI;
import l2.gameserver.model.Player;
import org.apache.commons.lang3.tuple.Pair;

public class AltRecBotAI
extends PlayerAI {
    private volatile Pair<RunnableImpl, Long> arriveRunnableAndDelay;

    public AltRecBotAI(AltRecBot altRecBot) {
        super((Player)altRecBot);
    }

    public AltRecBot getActor() {
        return (AltRecBot)super.getActor();
    }

    public AltRecBotAI setArriveRunnable(RunnableImpl runnableImpl, long l) {
        this.arriveRunnableAndDelay = Pair.of(runnableImpl, l);
        return this;
    }

    protected void onEvtArrived() {
        super.onEvtArrived();
        Pair<RunnableImpl, Long> pair = this.arriveRunnableAndDelay;
        if (pair != null) {
            this.arriveRunnableAndDelay = null;
            if (Objects.equals(pair.getRight(), 0L)) {
                ThreadPoolManager.getInstance().execute(pair.getLeft());
            } else {
                ThreadPoolManager.getInstance().schedule(pair.getLeft(), pair.getRight());
            }
        }
    }
}

