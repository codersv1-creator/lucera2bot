/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.threading.RunnableImpl
 *  l2.commons.util.RandomUtils
 *  l2.commons.util.Rnd
 *  l2.gameserver.Config
 *  l2.gameserver.GameServer
 *  l2.gameserver.dao.CharacterDAO
 *  l2.gameserver.model.GameObjectsStorage
 *  l2.gameserver.model.base.Experience
 *  l2.gameserver.model.items.ItemInstance
 *  l2.gameserver.tables.SkillTable
 *  l2.gameserver.templates.item.ItemTemplate
 *  l2.gameserver.utils.Util
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.ThreadPoolManager;
import com.lucera2.scripts.altrecbots.model.ActionPlaybackContext;
import com.lucera2.scripts.altrecbots.model.ActionRecord;
import com.lucera2.scripts.altrecbots.model.ActionsStorageManager;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.AltRecBotBuilder;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.RandomUtils;
import l2.commons.util.Rnd;
import l2.gameserver.GameServer;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotSpawnManager {
    private static final Logger logger = LoggerFactory.getLogger(BotSpawnManager.class);
    private static final BotSpawnManager instance = new BotSpawnManager();
    private final Map<Integer, ClassIdSpawnPool> classIdSpawnPools = new ConcurrentHashMap<Integer, ClassIdSpawnPool>();
    private final NamesPool maleNames = new NamesPool();
    private final NamesPool femaleNames = new NamesPool();
    private final NamesPool maleTitles = new NamesPool();
    private final NamesPool femaleTitles = new NamesPool();
    private final AtomicInteger spawnCounter = new AtomicInteger();
    private final AtomicInteger spawnPendingCounter = new AtomicInteger();

    private BotSpawnManager() {
    }

    public static BotSpawnManager getInstance() {
        return instance;
    }

    private Optional<String> acquireName(ActionRecord actionRecord) {
        return actionRecord.getSex() != 0 ? this.femaleNames.acquireRecord() : this.maleNames.acquireRecord();
    }

    private Optional<String> acquireTitle(ActionRecord actionRecord) {
        return actionRecord.getSex() != 0 ? this.femaleTitles.acquireRecord() : this.maleTitles.acquireRecord();
    }

    private boolean releaseName(String string, ActionRecord actionRecord) {
        return actionRecord.getSex() != 0 ? this.femaleNames.releaseRecord(string) : this.maleNames.releaseRecord(string);
    }

    private boolean releaseTitle(String string, ActionRecord actionRecord) {
        return actionRecord.getSex() != 0 ? this.femaleTitles.releaseRecord(string) : this.maleTitles.releaseRecord(string);
    }

    private Optional<ActionPlaybackContext> createContext(ActionRecord actionRecord) {
        Object object2;
        Optional<String> optional;
        Optional<Object> optional2 = Optional.empty();
        int n = 0;
        do {
            if (!(optional = this.acquireName(actionRecord)).isPresent()) {
                return Optional.empty();
            }
            object2 = optional.get();
            if (!Util.isMatchingRegexp((String)object2, (String)l2.gameserver.Config.CNAME_TEMPLATE) || Util.isMatchingRegexp((String)((String)object2).toLowerCase(), (String)l2.gameserver.Config.CNAME_FORBIDDEN_PATTERN) || Stream.of(l2.gameserver.Config.CNAME_FORBIDDEN_NAMES).filter(arg_0 -> BotSpawnManager.lambda$createContext$0((String)object2, arg_0)).findAny().isPresent() || GameObjectsStorage.getPlayer((String)object2) != null || CharacterDAO.getInstance().getObjectIdByName((String)object2) > 0) continue;
            optional2 = Optional.of(object2);
        } while (!optional2.isPresent() && n++ < 10);
        if (!optional2.isPresent()) {
            return Optional.empty();
        }
        optional = Optional.of(Config.INITIAL_BOTS_TITLE);
        if (actionRecord.isNoble() && Rnd.chance((double)Config.INDIVIDUAL_BOT_TITLE_CHANCE)) {
            optional = this.acquireTitle(actionRecord);
        }
        object2 = new AltRecBotBuilder().setAccountName(Config.BOT_ACCOUNT_NAME).setHairColor(actionRecord.getHairColor()).setHairStyle(actionRecord.getHairStyle()).setFace(actionRecord.getFace()).setSex(actionRecord.getSex()).setNoble(actionRecord.isNoble());
        object2 = ((AltRecBotBuilder)object2).setTitle(optional.isPresent() ? optional.get() : "");
        int n2 = l2.gameserver.Config.ALT_MAX_LEVEL;
        for (ActionRecord.SubclassRecord object3 : actionRecord.getSubclasses()) {
            object2 = ((AltRecBotBuilder)object2).addClassId(BotUtils.toClassId(object3.getClassId()).get(), object3.isBase(), object3.isActive());
            if (!object3.isActive()) continue;
            n2 = Experience.getLevel((long)object3.getExp());
        }
        if (n2 > Config.BOTS_SPAWN_MAX_LEVEL || n2 < Config.BOTS_SPAWN_MIN_LEVEL) {
            if (optional2.isPresent()) {
                this.releaseName((String)optional2.get(), actionRecord);
            }
            if (optional.isPresent()) {
                this.releaseTitle(optional.get(), actionRecord);
            }
            return Optional.empty();
        }
        for (ActionRecord.ItemRecord itemRecord : actionRecord.getItems()) {
            try {
                ItemTemplate itemTemplate = BotUtils.getItemTemplate(itemRecord.getItemType()).orElse(null);
                if (itemTemplate == null) continue;
                ((AltRecBotBuilder)object2).addItem(itemTemplate, itemRecord.getEnchant(), itemRecord.getAmount(), itemRecord.isEquipped());
            } catch (Exception exception) {}
        }
        String string = (String)optional2.get();
        ((AltRecBotBuilder)object2).setName(string);
        ((AltRecBotBuilder)object2).setInitialLvl(n2);
        AltRecBot altRecBot = ((AltRecBotBuilder)object2).build();
        altRecBot.setCurrentCp(0.0);
        altRecBot.setOnlineStatus(false);
        for (ActionRecord.SkillRecord skillRecord : actionRecord.getSkills()) {
            altRecBot.addSkill(SkillTable.getInstance().getInfo(skillRecord.getSkillId(), skillRecord.getSkillLevel()));
        }
        altRecBot.setOnlineStatus(true);
        for (ActionRecord.ItemRecord itemRecord : actionRecord.getItems()) {
            ItemInstance itemInstance;
            if (!itemRecord.isEquipped() || ArrayUtils.contains(Config.PLAYBACK_IGNORED_ITEM_IDS, itemRecord.getItemType()) || (itemInstance = altRecBot.getInventory().getItemByItemId(itemRecord.getItemType())) == null || !itemInstance.isEquipable()) continue;
            altRecBot.getInventory().equipItem(itemInstance);
        }
        altRecBot.spawnMe();
        return Optional.of(new ActionPlaybackContext(altRecBot, actionRecord, string));
    }

    public void init() {
        List<ActionRecord> list = ActionsStorageManager.getInstance().getActionRecords();
        for (ActionRecord actionRecord : list) {
            this.addActionRecord(actionRecord);
        }
        this.maleNames.addAll(ActionsStorageManager.getInstance().loadNames(0));
        this.femaleNames.addAll(ActionsStorageManager.getInstance().loadNames(1));
        this.maleTitles.addAll(ActionsStorageManager.getInstance().loadTitles(0));
        this.femaleTitles.addAll(ActionsStorageManager.getInstance().loadTitles(1));
    }

    public boolean spawnOne() {
        Optional<ClassIdSpawnPool> optional = this.getRandomClassIdSpawnPool();
        if (!optional.isPresent()) {
            return false;
        }
        ClassIdSpawnPool classIdSpawnPool = optional.get();
        Optional<ActionRecord> optional2 = classIdSpawnPool.acquireRecord();
        if (!optional2.isPresent()) {
            return false;
        }
        ActionRecord actionRecord = optional2.get();
        Optional<ActionPlaybackContext> optional3 = this.createContext(actionRecord);
        if (!optional3.isPresent()) {
            classIdSpawnPool.releaseRecord(actionRecord);
            return false;
        }
        if (!optional3.get().initiate()) {
            classIdSpawnPool.releaseRecord(actionRecord);
            return false;
        }
        this.spawnCounter.incrementAndGet();
        return true;
    }

    public AtomicInteger getSpawnCounter() {
        return this.spawnCounter;
    }

    public void addActionRecord(ActionRecord actionRecord) {
        Optional<ActionRecord.SubclassRecord> optional = actionRecord.getBaseSubclass();
        if (!optional.isPresent()) {
            return;
        }
        ActionRecord.SubclassRecord subclassRecord = optional.get();
        ClassIdSpawnPool classIdSpawnPool = this.classIdSpawnPools.computeIfAbsent(subclassRecord.getClassId(), n -> new ClassIdSpawnPool((int)n));
        classIdSpawnPool.add(actionRecord);
    }

    private Optional<ClassIdSpawnPool> getRandomClassIdSpawnPool() {
        ArrayList<Pair<ClassIdSpawnPool, Double>> arrayList = new ArrayList<Pair<ClassIdSpawnPool, Double>>(this.classIdSpawnPools.size());
        double d = 0.0;
        for (ClassIdSpawnPool classIdSpawnPool : this.classIdSpawnPools.values()) {
            if (!classIdSpawnPool.isFilled()) continue;
            double d2 = Config.PLAYBACK_SPAWN_CLASSID_PROBABILITY_MOD.getOrDefault(classIdSpawnPool.getClassId(), 1.0);
            arrayList.add(Pair.of(classIdSpawnPool, d2));
            d += d2;
        }
        Collections.sort(arrayList, RandomUtils.DOUBLE_GROUP_COMPARATOR);
        ClassIdSpawnPool classIdSpawnPool = (ClassIdSpawnPool)RandomUtils.pickRandomSortedGroup(arrayList, (double)d);
        if (classIdSpawnPool == null) {
            return Optional.empty();
        }
        return Optional.of(classIdSpawnPool);
    }

    public void onPlaybackFinished(ActionPlaybackContext actionPlaybackContext) {
        ActionRecord actionRecord = actionPlaybackContext.getActionRecord();
        Optional<ActionRecord.SubclassRecord> optional = actionRecord.getBaseSubclass();
        if (!optional.isPresent()) {
            return;
        }
        ActionRecord.SubclassRecord subclassRecord = optional.get();
        ClassIdSpawnPool classIdSpawnPool = this.classIdSpawnPools.computeIfAbsent(subclassRecord.getClassId(), n -> new ClassIdSpawnPool((int)n));
        if (classIdSpawnPool.releaseRecord(actionRecord)) {
            this.spawnCounter.decrementAndGet();
        }
        this.releaseName(actionPlaybackContext.getName(), actionRecord);
    }

    public void trySpawn() {
        if (!Config.BOTS_ENABLED) {
            return;
        }
        int n = Config.BOT_COUNT_SUPPLIER.get();
        int n2 = n - this.spawnPendingCounter.get();
        if (n2 > 0) {
            logger.info("AltRecBots: Spawning {} bot(s)...", (Object)n2);
        } else {
            logger.info("AltRecBots: Skip spawning. Pending: {}.", (Object)this.spawnPendingCounter.get());
        }
        while (n2-- >= 0) {
            this.spawnPendingCounter.incrementAndGet();
            ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                public void runImpl() throws Exception {
                    if (GameServer.getInstance().getPendingShutdown().get() || !Config.BOTS_ENABLED) {
                        return;
                    }
                    try {
                        BotSpawnManager.this.spawnOne();
                    } finally {
                        BotSpawnManager.this.spawnPendingCounter.decrementAndGet();
                    }
                }
            }, Rnd.get((long)Config.BOTS_SPAWN_INTERVAL_MIN, (long)Config.BOTS_SPAWN_INTERVAL_MAX));
        }
    }

    public boolean deleteRecord(ActionRecord actionRecord) {
        int n = actionRecord.getId().get();
        ActionsStorageManager.getInstance().deleteBotRecord(n);
        for (ClassIdSpawnPool classIdSpawnPool : this.classIdSpawnPools.values()) {
            if (!classIdSpawnPool.deleteRecord(actionRecord)) continue;
            return true;
        }
        return false;
    }

    private static /* synthetic */ boolean lambda$createContext$0(String string, String string2) {
        return StringUtils.equalsIgnoreCase(string2, string);
    }

    private static class NamesPool {
        private final Lock lock = new ReentrantLock();
        private final List<String> names = new LinkedList<String>();
        private final Set<String> inUse = new HashSet<String>();

        private NamesPool() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addAll(List<String> list) {
            this.lock.lock();
            try {
                for (String string : list) {
                    if (this.inUse.contains(string) || this.names.contains(string)) continue;
                    this.names.add(string);
                }
            } finally {
                this.lock.unlock();
            }
        }

        public Optional<String> acquireRecord() {
            this.lock.lock();
            try {
                if (this.names.isEmpty()) {
                    Optional<String> optional = Optional.empty();
                    return optional;
                }
                String string = this.names.remove(0);
                this.inUse.add(string);
                this.names.remove(string);
                Optional<String> optional = Optional.of(string);
                return optional;
            } finally {
                this.lock.unlock();
            }
        }

        public boolean releaseRecord(String string) {
            if (StringUtils.isBlank(string)) {
                return false;
            }
            this.lock.lock();
            try {
                if (!this.inUse.contains(string)) {
                    boolean bl = false;
                    return bl;
                }
                this.inUse.remove(string);
                this.names.add(string);
                boolean bl = true;
                return bl;
            } finally {
                this.lock.unlock();
            }
        }
    }

    private static class ClassIdSpawnPool {
        private final int classId;
        private final Lock lock = new ReentrantLock();
        private final List<ActionRecord> records = new LinkedList<ActionRecord>();
        private final Set<ActionRecord> inUse = new HashSet<ActionRecord>();

        private ClassIdSpawnPool(int n) {
            this.classId = n;
        }

        public int getClassId() {
            return this.classId;
        }

        public void add(ActionRecord actionRecord) {
            if (!actionRecord.getId().isPresent()) {
                throw new IllegalArgumentException("Undefined 'id' of an action sequence");
            }
            this.lock.lock();
            try {
                if (this.inUse.contains(actionRecord) || this.records.contains(actionRecord)) {
                    return;
                }
                this.records.add(actionRecord);
            } finally {
                this.lock.unlock();
            }
        }

        public boolean isFilled() {
            return !this.records.isEmpty();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Optional<ActionRecord> acquireRecord() {
            this.lock.lock();
            try {
                Object object;
                if (this.records.isEmpty()) {
                    Optional<ActionRecord> optional = Optional.empty();
                    return optional;
                }
                ArrayList<Pair<Optional<ActionRecord>, Double>> arrayList = new ArrayList<Pair<Optional<ActionRecord>, Double>>(this.records.size());
                double d = 0.0;
                for (int i = 0; i < this.records.size(); ++i) {
                    object = this.records.get(i);
                    if (this.inUse.contains(object)) continue;
                    double d2 = 1.0 / ((double)(i / Config.PLAYBACK_SEQUENCE_SELECTOR_RANDOM_SLOPE_MOD) + 1.0);
                    if (d2 < 0.01) break;
                    arrayList.add(Pair.of(object, d2));
                    d += d2;
                }
                Collections.sort(arrayList, RandomUtils.DOUBLE_GROUP_COMPARATOR);
                ActionRecord actionRecord = (ActionRecord)RandomUtils.pickRandomSortedGroup(arrayList, (double)d);
                if (actionRecord == null) {
                    object = Optional.empty();
                    return object;
                }
                this.records.remove(actionRecord);
                this.inUse.add(actionRecord);
                object = Optional.of(actionRecord);
                return object;
            } finally {
                this.lock.unlock();
            }
        }

        public boolean releaseRecord(ActionRecord actionRecord) {
            if (actionRecord == null) {
                return false;
            }
            this.lock.lock();
            try {
                if (!this.inUse.contains(actionRecord)) {
                    boolean bl = false;
                    return bl;
                }
                this.inUse.remove(actionRecord);
                this.records.add(actionRecord);
                boolean bl = true;
                return bl;
            } finally {
                this.lock.unlock();
            }
        }

        public boolean deleteRecord(ActionRecord actionRecord) {
            if (actionRecord == null) {
                return false;
            }
            this.lock.lock();
            try {
                if (this.inUse.contains(actionRecord)) {
                    this.inUse.remove(actionRecord);
                    boolean bl = true;
                    return bl;
                }
                if (this.records.contains(actionRecord)) {
                    this.records.remove(actionRecord);
                    boolean bl = true;
                    return bl;
                }
                boolean bl = false;
                return bl;
            } finally {
                this.lock.unlock();
            }
        }
    }
}

