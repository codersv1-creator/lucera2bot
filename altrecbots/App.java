/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.lucera2.dbmsstruct.model.DBMSStructureSynchronizer
 *  l2.commons.listener.Listener
 *  l2.commons.threading.RunnableImpl
 *  l2.gameserver.Config
 *  l2.gameserver.GameServer
 *  l2.gameserver.ThreadPoolManager
 *  l2.gameserver.database.DatabaseFactory
 *  l2.gameserver.handler.admincommands.AdminCommandHandler
 *  l2.gameserver.handler.admincommands.IAdminCommandHandler
 *  l2.gameserver.instancemanager.ReflectionManager
 *  l2.gameserver.listener.game.OnStartListener
 *  l2.gameserver.model.GameObject
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.Zone
 *  l2.gameserver.model.Zone$ZoneType
 *  l2.gameserver.model.actor.listener.PlayerListenerList
 *  l2.gameserver.scripts.ScriptFile
 */
package com.lucera2.scripts.altrecbots;

import com.lucera2.dbmsstruct.model.DBMSStructureSynchronizer;
import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.LegacyTrashImporter;
import com.lucera2.scripts.altrecbots.model.ActionsStorageManager;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.BotPhrasePool;
import com.lucera2.scripts.altrecbots.model.BotSpawnManager;
import com.lucera2.scripts.altrecbots.model.handler.admincommands.AdminAltRecBots;
import com.lucera2.scripts.altrecbots.model.listeners.PlayerEnterListener;
import com.lucera2.scripts.altrecbots.model.listeners.ZoneListener;
import java.sql.Connection;
import java.util.Collection;
import l2.commons.listener.Listener;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App
implements OnStartListener,
ScriptFile {
    private static final Logger a = LoggerFactory.getLogger(App.class);
    private static final String b = "altrecbots";
    protected static boolean isLegacyImport = false;

    private static DBMSStructureSynchronizer a(Connection connection) {
        return GameServer.getInstance().getDbmsStructureSynchronizer(connection, App.class.getResourceAsStream("/altrecbots.dbmsstruct.json"));
    }

    private static void a() {
        if (!l2.gameserver.Config.DATABASE_EX_STRUCTURE_MANAGER) {
            return;
        }
        try (Connection connection = DatabaseFactory.getInstance().getConnection();){
            DBMSStructureSynchronizer dBMSStructureSynchronizer = App.a(connection);
            a.info("AltRecBots: Synchronizing db structure ...");
            dBMSStructureSynchronizer.synchronize(new String[]{b});
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void main(String ... stringArray) throws Exception {
    }

    public void onLoad() {
        Config.load();
        if (!Config.BOTS_ENABLED) {
            return;
        }
        App.a();
        AdminCommandHandler.getInstance().registerAdminCommandHandler((IAdminCommandHandler)AdminAltRecBots.getInstance());
        GameServer.getInstance().getListeners().add((Listener)this);
        ActionsStorageManager.getInstance().init();
        BotSpawnManager.getInstance().init();
        BotPhrasePool.getInstance().loadPhrases();
        if (isLegacyImport) {
            LegacyTrashImporter.onLoad();
        }
    }

    private void b() {
        PlayerListenerList.addGlobal((Listener)PlayerEnterListener.getInstance());
        Collection collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            if (!zone.isType(Zone.ZoneType.peace_zone)) continue;
            zone.addListener((Listener)ZoneListener.getInstance());
        }
    }

    public void onStart() {
        if (isLegacyImport) {
            LegacyTrashImporter.onStart();
            return;
        }
        this.b();
        if (Config.BOTS_ENABLED && Config.BOT_COUNT_SUPPLIER != null) {
            ThreadPoolManager.getInstance().scheduleAtFixedRate((Runnable)new RunnableImpl(){

                public void runImpl() throws Exception {
                    BotSpawnManager.getInstance().trySpawn();
                }
            }, Config.BOTS_SPAWN_CHECK_INTERVAL, Config.BOTS_SPAWN_CHECK_INTERVAL);
        }
    }

    public void onReload() {
        Config.load();
    }

    public void onShutdown() {
    }

    public boolean OnActionShift_AltRecBot(Player player, GameObject gameObject) {
        if (player == null || gameObject == null || !player.getPlayerAccess().CanViewChar) {
            return false;
        }
        if (gameObject instanceof AltRecBot) {
            return AdminAltRecBots.getInstance().doOnActionShift(player, (AltRecBot)gameObject);
        }
        return false;
    }
}

