/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.threading.RunnableImpl
 *  l2.gameserver.ThreadPoolManager
 *  l2.gameserver.handler.admincommands.IAdminCommandHandler
 *  l2.gameserver.model.GameObjectsStorage
 *  l2.gameserver.model.Player
 *  l2.gameserver.network.l2.components.IStaticPacket
 *  l2.gameserver.network.l2.s2c.NpcHtmlMessage
 */
package com.lucera2.scripts.altrecbots.model.handler.admincommands;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.ActionRecord;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.BotSpawnManager;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.Objects;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class AdminAltRecBots
implements IAdminCommandHandler {
    private static final AdminAltRecBots instance = new AdminAltRecBots();

    public static AdminAltRecBots getInstance() {
        return instance;
    }

    private boolean deleteBotRecord(Player player, String[] stringArray) {
        if (stringArray.length < 1) {
            player.sendMessage("Usage: admin_delete_bot_record <botId>");
            return false;
        }
        int n = Integer.parseInt(stringArray[1]);
        boolean bl = false;
        for (Player player2 : GameObjectsStorage.getAllPlayersForIterate()) {
            ActionRecord actionRecord;
            AltRecBot altRecBot;
            ActionPlaybackContext actionPlaybackContext;
            if (player2 == null || !BotUtils.isBot(player2) || (actionPlaybackContext = (altRecBot = (AltRecBot)player2).getPlaybackContext()) == null || (actionRecord = actionPlaybackContext.getActionRecord()) == null || !actionRecord.getId().isPresent() || !Objects.equals(actionRecord.getId().get(), n) || !BotSpawnManager.getInstance().deleteRecord(actionRecord)) continue;
            bl = true;
            actionPlaybackContext.finish(0L);
            player.sendMessage("'" + altRecBot.getName() + "'[" + n + "] deleted and kicked.");
        }
        return bl;
    }

    private boolean kickBot(Player player, String[] stringArray) {
        if (stringArray.length < 1) {
            player.sendMessage("Usage: admin_kick_bot <objId>");
            return false;
        }
        int n = Integer.parseInt(stringArray[1]);
        for (Player player2 : GameObjectsStorage.getAllPlayersForIterate()) {
            AltRecBot altRecBot;
            ActionPlaybackContext actionPlaybackContext;
            if (player2 == null || !BotUtils.isBot(player2) || (actionPlaybackContext = (altRecBot = (AltRecBot)player2).getPlaybackContext()) == null || altRecBot.getObjectId() != n) continue;
            actionPlaybackContext.finish(0L);
            player.sendMessage("'" + altRecBot.getName() + "'[" + actionPlaybackContext.getActionRecord().getId().get() + "] kicked.");
            return true;
        }
        return false;
    }

    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (commands == Commands.admin_delete_bot_record) {
            return this.deleteBotRecord(player, stringArray);
        }
        if (commands == Commands.admin_kick_bot) {
            return this.kickBot(player, stringArray);
        }
        if (commands == Commands.admin_bots_strategy) {
            if (stringArray.length < 3) {
                player.sendMessage("//admin_bots_strategy [Constant|OnlinePercent] <num>");
            } else {
                Config.BOT_COUNT_SUPPLIER = Config.parseStrategy(stringArray[1], stringArray[2]);
                player.sendMessage("Bot Strategy now is " + stringArray[1] + " with param " + stringArray[2]);
            }
        } else if (commands == Commands.admin_bots_disable) {
            Config.BOTS_ENABLED = false;
            for (Player player2 : GameObjectsStorage.getAllPlayersForIterate()) {
                if (player2 == null || !BotUtils.isBot(player2)) continue;
                AltRecBot altRecBot = (AltRecBot)player2;
                ActionPlaybackContext actionPlaybackContext = altRecBot.getPlaybackContext();
                if (actionPlaybackContext != null) {
                    actionPlaybackContext.finish(1000L);
                    continue;
                }
                altRecBot.stopAllTimers();
                altRecBot.getInventory().clear();
                altRecBot.setIsOnline(false);
                altRecBot.deleteMe();
            }
        } else if (commands == Commands.admin_altrec1) {
            ThreadPoolManager.getInstance().execute((Runnable)new RunnableImpl(){

                public void runImpl() throws Exception {
                    for (int i = 0; i < 1000; ++i) {
                        BotSpawnManager.getInstance().spawnOne();
                        if (i % 100 != 0) continue;
                        System.out.println("Spawned " + BotSpawnManager.getInstance().getSpawnCounter().get());
                    }
                }
            });
        }
        return false;
    }

    public boolean doOnActionShift(Player player, AltRecBot altRecBot) {
        AltRecBot altRecBot2 = altRecBot;
        ActionPlaybackContext actionPlaybackContext = altRecBot2.getPlaybackContext();
        if (actionPlaybackContext == null || !player.isGM()) {
            return false;
        }
        Action<?> action = actionPlaybackContext.getCurrentAction();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body><br>");
        long l = System.currentTimeMillis() - actionPlaybackContext.getCreatedAt();
        stringBuilder.append("Action sequence id: ").append(actionPlaybackContext.getActionRecord().getId().get()).append("<br>");
        stringBuilder.append("Current action: ").append(action != null ? action.toString() : "(null)").append("<br>");
        stringBuilder.append("Next action idx: ").append(actionPlaybackContext.getActionIdx().get()).append("<br>");
        stringBuilder.append("Lifetime: ").append(l).append("<br>");
        stringBuilder.append("<button value=\"Kick this bot\" action=\"bypass -h admin_kick_bot " + altRecBot2.getObjectId() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"><br>");
        stringBuilder.append("<button value=\"Remove this bot\" action=\"bypass -h admin_delete_bot_record " + actionPlaybackContext.getActionRecord().getId().get() + "\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"><br>");
        stringBuilder.append("<button value=\"Effects\" action=\"bypass -h admin_show_effects\" width=80 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"><br>");
        stringBuilder.append("TODO ...<br>");
        stringBuilder.append("<body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        return true;
    }

    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    static enum Commands {
        admin_altrec1,
        admin_delete_bot_record,
        admin_kick_bot,
        admin_bots_strategy,
        admin_bots_disable;

    }
}

