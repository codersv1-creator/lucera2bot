/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.threading.RunnableImpl
 *  l2.commons.util.Rnd
 *  l2.gameserver.GameServer
 *  l2.gameserver.instancemanager.ReflectionManager
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.GameObject
 *  l2.gameserver.model.Party
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.Skill
 *  l2.gameserver.model.Summon
 *  l2.gameserver.model.World
 *  l2.gameserver.model.chat.ChatFilters
 *  l2.gameserver.model.chat.chatfilter.ChatFilter
 *  l2.gameserver.network.l2.components.ChatType
 *  l2.gameserver.network.l2.components.IStaticPacket
 *  l2.gameserver.network.l2.s2c.L2GameServerPacket
 *  l2.gameserver.network.l2.s2c.MagicSkillLaunched
 *  l2.gameserver.network.l2.s2c.MagicSkillUse
 *  l2.gameserver.network.l2.s2c.Say2
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.ThreadPoolManager;
import com.lucera2.scripts.altrecbots.model.ActionRecord;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.BaseContext;
import com.lucera2.scripts.altrecbots.model.BotPhrase;
import com.lucera2.scripts.altrecbots.model.BotPhrasePool;
import com.lucera2.scripts.altrecbots.model.BotSpawnManager;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.GameServer;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.chat.ChatFilters;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.Say2;

public class ActionPlaybackContext
extends BaseContext<AltRecBot> {
    private final ActionRecord actionRecord;
    private final AtomicInteger actionIdx = new AtomicInteger(0);
    private final long createdAt;
    private final ReentrantLock lock = new ReentrantLock();
    private final String name;
    private ScheduledFuture<?> nextActionFuture;
    private volatile boolean isFinished = false;
    private volatile Action<?> currentAction;
    private volatile long lastTalk;

    public ActionPlaybackContext(AltRecBot altRecBot, ActionRecord actionRecord, String string) {
        super(altRecBot.getRef());
        this.name = string;
        this.actionRecord = actionRecord;
        altRecBot.setPlaybackContext(this);
        this.createdAt = System.currentTimeMillis();
    }

    public String getName() {
        return this.name;
    }

    public ActionRecord getActionRecord() {
        return this.actionRecord;
    }

    public AtomicInteger getActionIdx() {
        return this.actionIdx;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public boolean haveNext() {
        return this.actionIdx.get() < this.actionRecord.getActions().size();
    }

    public boolean initiate() {
        Skill skill2;
        Optional optional = this.getPlayer();
        if (!optional.isPresent()) {
            return false;
        }
        AltRecBot altRecBot = (AltRecBot)((Object)optional.get());
        if (altRecBot.isDeleted()) {
            return false;
        }
        Party party = altRecBot.getParty();
        if (party != null) {
            altRecBot.leaveParty();
        }
        altRecBot.setCurrentHpMp(altRecBot.getMaxHp(), altRecBot.getMaxMp());
        if (!Config.BOT_INITIAL_EFFECTS.isEmpty()) {
            for (Skill skill2 : Config.BOT_INITIAL_EFFECTS) {
                skill2.getEffects((Creature)altRecBot, (Creature)altRecBot, false, false);
            }
        }
        Summon summon = altRecBot.getPet();
        if (altRecBot.getPet() != null) {
            summon.getEffectList().stopAllEffects();
            summon.unSummon();
        }
        if (!altRecBot.isVisible()) {
            altRecBot.setLoc(this.actionRecord.getLocationRandomized());
            altRecBot.spawnMe();
            this.actionIdx.set(0);
            return this.scheduleNextAction();
        }
        if (this.actionRecord.getLocation().distance(altRecBot.getLoc()) > (double)(Config.PLAYBACK_SPAWN_POS_RANDOM_RADIUS + 32)) {
            if (altRecBot.isInPeaceZone()) {
                altRecBot.decayMe();
                altRecBot.spawnMe(this.actionRecord.getLocationRandomized());
                this.actionIdx.set(0);
                return this.scheduleNextAction();
            }
        } else {
            this.actionIdx.set(0);
            return this.scheduleNextAction();
        }
        skill2 = new MagicSkillUse((Creature)altRecBot, (Creature)altRecBot, 2213, 1, 20000, 0L);
        altRecBot.broadcastPacket(new L2GameServerPacket[]{skill2});
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            public void runImpl() throws Exception {
                Optional optional = ActionPlaybackContext.this.getPlayer();
                if (!optional.isPresent()) {
                    return;
                }
                AltRecBot altRecBot = (AltRecBot)((Object)optional.get());
                altRecBot.broadcastPacket(new L2GameServerPacket[]{new MagicSkillLaunched((Creature)altRecBot, 2213, 1, (Creature)altRecBot)});
                altRecBot.teleToLocation(ActionPlaybackContext.this.actionRecord.getLocationRandomized().correctGeoZ(), ReflectionManager.DEFAULT);
                altRecBot.onTeleported();
                ActionPlaybackContext.this.actionIdx.set(0);
                ActionPlaybackContext.this.scheduleNextAction();
            }
        }, 20000L);
        return true;
    }

    public Action<?> getCurrentAction() {
        return this.currentAction;
    }

    public void finish(long l) {
        Party party;
        this.isFinished = true;
        BotSpawnManager.getInstance().onPlaybackFinished(this);
        final Optional optional = this.getPlayer();
        if (!optional.isPresent()) {
            return;
        }
        AltRecBot altRecBot = (AltRecBot)((Object)optional.get());
        if (altRecBot.isCastingNow()) {
            altRecBot.abortCast(true, false);
        }
        if (altRecBot.isMoving()) {
            altRecBot.stopMove();
        }
        if ((party = altRecBot.getParty()) != null) {
            altRecBot.leaveParty();
        }
        Summon summon = altRecBot.getPet();
        if (altRecBot.getPet() != null) {
            summon.getEffectList().stopAllEffects();
            summon.unSummon();
        }
        RunnableImpl runnableImpl = new RunnableImpl(){

            public void runImpl() throws Exception {
                AltRecBot altRecBot = (AltRecBot)((Object)optional.get());
                if (altRecBot == null) {
                    return;
                }
                altRecBot.stopAllTimers();
                altRecBot.getInventory().clear();
                altRecBot.setIsOnline(false);
                altRecBot.deleteMe();
            }
        };
        if (l > 0L) {
            ThreadPoolManager.getInstance().schedule(runnableImpl, l);
        } else {
            runnableImpl.run();
        }
    }

    public void finish() {
        this.finish(Rnd.get((long)Config.BOTS_UNSPAWN_INTERVAL_MIN, (long)Config.BOTS_UNSPAWN_INTERVAL_MAX));
    }

    public void stopNextActionTimer() {
        ScheduledFuture<?> scheduledFuture = this.nextActionFuture;
        if (scheduledFuture != null) {
            this.nextActionFuture = null;
            scheduledFuture.cancel(false);
        }
    }

    public boolean scheduleNextAction() {
        if (this.isFinished) {
            return false;
        }
        int n = this.actionIdx.getAndIncrement();
        long l = System.currentTimeMillis();
        if (GameServer.getInstance().getPendingShutdown().get() || !Config.BOTS_ENABLED || l - this.createdAt >= Config.BOT_TTL) {
            this.finish();
            return false;
        }
        if (n >= this.actionRecord.getActions().size()) {
            if (!this.actionRecord.getActions().isEmpty()) {
                if (!Config.LOOP_PLAYBACK) {
                    this.finish();
                    return false;
                }
                return this.initiate();
            }
            this.finish();
            return false;
        }
        Action action = this.actionRecord.getActions().get(n);
        long l2 = action.getDuration(this);
        if (n == 0) {
            l2 += Rnd.get((long)Config.BOTS_FIRST_ACTION_MIN, (long)Config.BOTS_FIRST_ACTION_MAX);
        }
        long l3 = Math.max(32L, Math.min(l2, Config.BOT_TTL - (l - this.createdAt)));
        this.nextActionFuture = ThreadPoolManager.getInstance().schedule(new ActionRunner(this, action), l3);
        if (l3 > 1000L && l - this.lastTalk > Config.PHRASE_REUSE_TIME) {
            boolean bl = false;
            if (Rnd.chance((double)Config.BOT_TALK_CHANCE) || (bl = Rnd.chance((double)Config.BOT_TALK_CHANCE_SHOUT))) {
                this.lastTalk = l;
                ThreadPoolManager.getInstance().schedule(new BotSpeak(this, bl), l3 / 2L);
            }
        }
        return true;
    }

    private static class ActionRunner
    extends RunnableImpl {
        private final ActionPlaybackContext playbackCtx;
        private final Action<?> action;

        private ActionRunner(ActionPlaybackContext actionPlaybackContext, Action<?> action) {
            this.playbackCtx = actionPlaybackContext;
            this.action = action;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void runImpl() throws Exception {
            this.playbackCtx.lock.lock();
            try {
                this.playbackCtx.currentAction = this.action;
                try {
                    Optional optional = this.playbackCtx.getPlayer();
                    if (!optional.isPresent()) {
                        return;
                    }
                    AltRecBot altRecBot = (AltRecBot)((Object)optional.get());
                    this.action.doIt(altRecBot, this.playbackCtx);
                } finally {
                    this.playbackCtx.scheduleNextAction();
                }
            } finally {
                this.playbackCtx.lock.unlock();
            }
        }
    }

    public static class BotSpeak
    extends RunnableImpl {
        private final ActionPlaybackContext playbackCtx;
        private final boolean isShout;

        public BotSpeak(ActionPlaybackContext actionPlaybackContext, boolean bl) {
            this.playbackCtx = actionPlaybackContext;
            this.isShout = bl;
        }

        public void runImpl() throws Exception {
            Optional optional = this.playbackCtx.getPlayer();
            if (this.playbackCtx.isFinished || !optional.isPresent()) {
                return;
            }
            AltRecBot altRecBot = (AltRecBot)((Object)optional.get());
            Optional<BotPhrase> optional2 = BotPhrasePool.getInstance().findPhrase(altRecBot.getSex(), altRecBot.getLoc());
            if (!optional2.isPresent()) {
                return;
            }
            BotPhrase botPhrase = optional2.get();
            ChatType chatType = this.isShout ? ChatType.SHOUT : ChatType.ALL;
            for (ChatFilter chatFilter : ChatFilters.getinstance().getFilters()) {
                if (!chatFilter.isMatch((Player)altRecBot, chatType, botPhrase.getText(), null) || chatFilter.getAction() == 0) continue;
                return;
            }
            Say2 say2 = new Say2(altRecBot.getObjectId(), chatType, altRecBot.getName(), botPhrase.getText());
            List list = World.getAroundPlayers((GameObject)altRecBot);
            if (list != null) {
                for (ChatFilter chatFilter : list) {
                    if (chatFilter == altRecBot || chatFilter.getReflection() != altRecBot.getReflection() || chatFilter.isBlockAll() || chatFilter.isInBlockList((Player)altRecBot)) continue;
                    chatFilter.sendPacket((IStaticPacket)say2);
                }
            }
        }
    }
}

