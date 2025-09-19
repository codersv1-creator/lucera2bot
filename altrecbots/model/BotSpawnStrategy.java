/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.model.GameObjectsStorage
 *  l2.gameserver.model.Player
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.model.BotSpawnManager;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum BotSpawnStrategy {
    Constant{

        @Override
        public int getSpawnNeeded(String[] stringArray) {
            return Math.max(0, Integer.parseInt(stringArray[0]) - BotSpawnManager.getInstance().getSpawnCounter().get());
        }
    }
    ,
    OnlinePercent{

        @Override
        public int getSpawnNeeded(String[] stringArray) {
            int n = 0;
            int n2 = 0;
            double d = Double.parseDouble(stringArray[0]) / 100.0;
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                if (player == null) continue;
                if (BotUtils.isBot(player)) {
                    ++n2;
                    continue;
                }
                ++n;
            }
            return (int)Math.max(0.0, (double)n * d - (double)n2);
        }
    };


    public abstract int getSpawnNeeded(String[] var1);
}

