/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.threading.RunnableImpl
 *  l2.commons.util.Rnd
 *  l2.gameserver.Config
 *  l2.gameserver.dao.CharacterDAO
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.GameObject
 *  l2.gameserver.model.Party
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.Request
 *  l2.gameserver.model.Request$L2RequestType
 *  l2.gameserver.model.items.ItemInstance
 *  l2.gameserver.model.items.PcInventory
 *  l2.gameserver.model.items.PcWarehouse
 *  l2.gameserver.network.l2.components.IStaticPacket
 *  l2.gameserver.network.l2.components.SystemMsg
 *  l2.gameserver.network.l2.s2c.JoinParty
 *  l2.gameserver.network.l2.s2c.SystemMessage
 *  l2.gameserver.network.l2.s2c.TradeStart
 *  l2.gameserver.templates.PlayerTemplate
 *  l2.gameserver.templates.item.ItemTemplate
 *  l2.gameserver.templates.item.ItemTemplate$ItemClass
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.ThreadPoolManager;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.PcWarehouse;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.JoinParty;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.TradeStart;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.templates.item.ItemTemplate;

public class AltRecBot
extends Player {
    private static final ItemInstance[] EMPTY_ITEM_INSTANCE_ARRAY = new ItemInstance[0];
    private final int botObjId;
    private final AltRecBotInventory botInventory;
    private final AltRecBotWarhouse botWarhouse;
    private ActionPlaybackContext _playbackContext;

    public AltRecBot(int n, PlayerTemplate playerTemplate, String string) {
        super(n, playerTemplate, string);
        this.botObjId = n;
        this.botInventory = new AltRecBotInventory(this);
        this.botWarhouse = new AltRecBotWarhouse(this);
        AltRecBot.applyMagic(this);
    }

    private static void applyMagic(AltRecBot altRecBot) {
        try {
            BotUtils.applyInventoryHack(altRecBot, altRecBot.botInventory);
            BotUtils.applyWarhouseHack(altRecBot, altRecBot.botWarhouse);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void useShots() {
        for (ItemInstance itemInstance : this.getInventory().getItems()) {
            ItemTemplate itemTemplate;
            if (itemInstance == null || !(itemTemplate = itemInstance.getTemplate()).isShotItem()) continue;
            this.addAutoSoulShot(itemTemplate.getItemId());
        }
        this.autoShot();
    }

    public ActionPlaybackContext getPlaybackContext() {
        return this._playbackContext;
    }

    public AltRecBot setPlaybackContext(ActionPlaybackContext actionPlaybackContext) {
        this._playbackContext = actionPlaybackContext;
        return this;
    }

    public void setRequest(final Request request) {
        super.setRequest(request);
        if (request == null || request.getReciever() != this) {
            return;
        }
        if (request.isTypeOf(Request.L2RequestType.CUSTOM)) {
            request.cancel();
            return;
        }
        for (final Request.L2RequestType l2RequestType : Config.BOT_ACCEPT_REQUEST_CHANCE.keySet()) {
            if (!request.isTypeOf(l2RequestType) || !Rnd.chance((double)Config.BOT_ACCEPT_REQUEST_CHANCE.get(l2RequestType))) continue;
            ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void runImpl() throws Exception {
                    Player player = request.getReciever();
                    if (player == null || player.getRequest() != request) {
                        return;
                    }
                    Player player2 = request.getRequestor();
                    switch (l2RequestType) {
                        case TRADE_REQUEST: {
                            if (player2 == null || !player2.isInActingRange((GameObject)player)) {
                                request.cancel();
                                return;
                            }
                            try {
                                new Request(Request.L2RequestType.TRADE, player, player2);
                                player2.setTradeList(new CopyOnWriteArrayList());
                                player2.sendPacket(new IStaticPacket[]{new SystemMessage(SystemMsg.YOU_BEGIN_TRADING_WITH_C1).addString(player.getName()), new TradeStart(player2, player)});
                                break;
                            } finally {
                                request.done();
                            }
                        }
                        case PARTY: {
                            if (player2 == null || player2.getRequest() != request || player2.isOlyParticipant() || player2.isOutOfControl()) {
                                request.cancel();
                                return;
                            }
                            Party party = player2.getParty();
                            if (party != null && party.getMemberCount() >= l2.gameserver.Config.ALT_MAX_PARTY_SIZE) {
                                request.cancel();
                                player2.sendPacket((IStaticPacket)SystemMsg.THE_PARTY_IS_FULL);
                                player2.sendPacket((IStaticPacket)JoinParty.FAIL);
                                return;
                            }
                            IStaticPacket iStaticPacket = player.canJoinParty(player2);
                            if (iStaticPacket != null || player.getParty() != null) {
                                request.cancel();
                                player2.sendPacket((IStaticPacket)JoinParty.FAIL);
                                return;
                            }
                            if (party == null) {
                                int n = request.getInteger((Object)"itemDistribution", 0);
                                party = new Party(player2, n);
                                player2.setParty(party);
                            }
                            try {
                                player.joinParty(party);
                                player2.sendPacket((IStaticPacket)JoinParty.SUCCESS);
                                break;
                            } finally {
                                request.done();
                            }
                        }
                        default: {
                            request.done();
                        }
                    }
                }
            }, 3000 + Rnd.get((int)6000));
            return;
        }
        for (final Request.L2RequestType l2RequestType : Config.BOT_DENY_REQUEST_CHANCE.keySet()) {
            if (!request.isTypeOf(l2RequestType) || !Rnd.chance((double)Config.BOT_DENY_REQUEST_CHANCE.get(l2RequestType))) continue;
            ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                public void runImpl() throws Exception {
                    Player player = request.getReciever();
                    if (player == null || player.getRequest() != request) {
                        return;
                    }
                    Player player2 = request.getRequestor();
                    request.cancel();
                    switch (l2RequestType) {
                        case PARTY: {
                            if (player2 == null) break;
                            player2.sendPacket((IStaticPacket)JoinParty.FAIL);
                            break;
                        }
                        case TRADE_REQUEST: {
                            player2.sendPacket((IStaticPacket)new SystemMessage(SystemMsg.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE).addString(player.getName()));
                        }
                    }
                }
            }, 3000 + Rnd.get((int)6000));
            return;
        }
    }

    public int getBotObjId() {
        return this.botObjId;
    }

    public void store(boolean bl) {
    }

    public void storeDisableSkills() {
    }

    public void storeCharSubClasses() {
    }

    public AltRecBotInventory getInventory() {
        return this.botInventory;
    }

    public AltRecBotWarhouse getWarehouse() {
        return this.botWarhouse;
    }

    protected void onDelete() {
        super.onDelete();
        this.getInventory().clear();
        this.getWarehouse().clear();
        CharacterDAO.getInstance().deleteCharacterDataByObjId(this.getBotObjId());
    }

    protected void onDeath(Creature creature) {
        super.onDeath(creature);
        ActionPlaybackContext actionPlaybackContext = this.getPlaybackContext();
        if (actionPlaybackContext == null) {
            return;
        }
        actionPlaybackContext.finish();
    }

    public void stopAllTimers() {
        super.stopAllTimers();
        ActionPlaybackContext actionPlaybackContext = this.getPlaybackContext();
        if (actionPlaybackContext == null) {
            return;
        }
        actionPlaybackContext.stopNextActionTimer();
    }

    public static class AltRecBotInventory
    extends PcInventory {
        public AltRecBotInventory(AltRecBot altRecBot) {
            super((Player)altRecBot);
        }

        public void restore() {
        }

        public void store() {
        }

        protected void onAddItem(ItemInstance itemInstance) {
            itemInstance.setOwnerId(this.getOwnerId());
            itemInstance.setLocation(this.getBaseLocation());
            itemInstance.setLocData(this.findSlot());
            itemInstance.setCustomFlags(47);
        }

        protected void onModifyItem(ItemInstance itemInstance) {
        }

        protected void onDestroyItem(ItemInstance itemInstance) {
            if (itemInstance == null || itemInstance.getCount() == 0L && itemInstance.getLocData() == -1) {
                return;
            }
            itemInstance.setCount(0L);
            itemInstance.setLocData(-1);
            itemInstance.delete();
        }

        protected void onRestoreItem(ItemInstance itemInstance) {
            this._totalWeight = (int)((long)this._totalWeight + (long)itemInstance.getTemplate().getWeight() * itemInstance.getCount());
        }

        protected void onRemoveItem(ItemInstance itemInstance) {
            if (itemInstance.isEquipped()) {
                this.unEquipItem(itemInstance);
            }
            itemInstance.setCount(0L);
            itemInstance.setLocData(-1);
            itemInstance.delete();
        }

        protected void onEquip(int n, ItemInstance itemInstance) {
            itemInstance.setLocation(this.getEquipLocation());
            itemInstance.setLocData(n);
            itemInstance.setEquipped(true);
            this.sendModifyItem(itemInstance);
            itemInstance.setLocation(this.getEquipLocation());
            itemInstance.setLocData(n);
            if (itemInstance.isWeapon() || itemInstance.isArmor() || itemInstance.isAccessory()) {
                this.getListeners().onEquip(n, itemInstance);
            }
        }

        protected void onUnequip(int n, ItemInstance itemInstance) {
            if (itemInstance.isWeapon() || itemInstance.isArmor() || itemInstance.isAccessory()) {
                this.getListeners().onUnequip(n, itemInstance);
            }
            itemInstance.setLocation(this.getBaseLocation());
            itemInstance.setLocData(this.findSlot());
            itemInstance.setEquipped(false);
            itemInstance.setChargedSpiritshot(0);
            itemInstance.setChargedSoulshot(0);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void clear() {
            this.writeLock();
            try {
                for (ItemInstance itemInstance : this.getItems()) {
                    this.destroyItem(itemInstance);
                }
            } finally {
                this.writeUnlock();
            }
            super.clear();
        }

        private int findSlot() {
            int n = 0;
            block0: for (n = 0; n < this._items.size(); ++n) {
                for (int i = 0; i < this._items.size(); ++i) {
                    ItemInstance itemInstance = (ItemInstance)this._items.get(i);
                    if (!itemInstance.isEquipped() && !itemInstance.getTemplate().isQuest() && itemInstance.getEquipSlot() == n) continue block0;
                }
            }
            return n;
        }
    }

    public static class AltRecBotWarhouse
    extends PcWarehouse {
        public AltRecBotWarhouse(Player player) {
            super(player);
        }

        public AltRecBotWarhouse(int n) {
            super(n);
        }

        public ItemInstance[] getItems(ItemTemplate.ItemClass itemClass) {
            return EMPTY_ITEM_INSTANCE_ARRAY;
        }

        public long getCountOfAdena() {
            return 0L;
        }

        protected void onAddItem(ItemInstance itemInstance) {
            itemInstance.delete();
        }

        protected void onModifyItem(ItemInstance itemInstance) {
            itemInstance.delete();
        }

        protected void onRemoveItem(ItemInstance itemInstance) {
            itemInstance.delete();
        }

        protected void onDestroyItem(ItemInstance itemInstance) {
            itemInstance.delete();
        }

        public void restore() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void clear() {
            this.writeLock();
            try {
                for (ItemInstance itemInstance : this.getItems()) {
                    this.destroyItem(itemInstance);
                }
            } finally {
                this.writeUnlock();
            }
            super.clear();
        }
    }
}

