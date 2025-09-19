/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.util.RandomUtils
 *  l2.gameserver.model.World
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.ActionsStorageManager;
import com.lucera2.scripts.altrecbots.model.BotPhrase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import l2.commons.util.RandomUtils;
import l2.gameserver.model.World;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

public class BotPhrasePool {
    private static final BotPhrasePool instance = new BotPhrasePool();
    private List<BotPhrasePoolRecord> records = new ArrayList<BotPhrasePoolRecord>();

    private BotPhrasePool() {
    }

    public static BotPhrasePool getInstance() {
        return instance;
    }

    private static int regionX(int n) {
        return (n >> World.SHIFT_BY) + World.OFFSET_X;
    }

    private static int regionY(int n) {
        return (n >> World.SHIFT_BY) + World.OFFSET_Y;
    }

    private Optional<BotPhrase> findPhrase0(int n, Location location) {
        Long l;
        Object object;
        Object object2;
        long l2 = System.currentTimeMillis();
        ArrayList<Pair<BotPhrasePoolRecord, Long>> arrayList = new ArrayList<Pair<BotPhrasePoolRecord, Long>>(this.records.size());
        ArrayList<Pair<BotPhrasePoolRecord, Long>> arrayList2 = new ArrayList<Pair<BotPhrasePoolRecord, Long>>(this.records.size());
        int n2 = BotPhrasePool.regionX(location.getX());
        int n3 = BotPhrasePool.regionY(location.getY());
        for (BotPhrasePoolRecord botPhrasePoolRecord : this.records) {
            long l3 = botPhrasePoolRecord.getLastUse();
            if (Math.max(0L, l2 - l3) < Config.PHRASE_REUSE_TIME || ((BotPhrase)(object2 = botPhrasePoolRecord.getPhrase())).getSex().isPresent() && !Objects.equals(((BotPhrase)object2).getSex().get(), n)) continue;
            Optional<Location> optional = ((BotPhrase)object2).getLoc();
            if (optional.isPresent()) {
                object = optional.get();
                if (BotPhrasePool.regionX(object.getX()) != n2 || BotPhrasePool.regionY(object.getY()) != n3) continue;
                arrayList2.add(Pair.of(botPhrasePoolRecord, l3));
                continue;
            }
            arrayList.add(Pair.of(botPhrasePoolRecord, l3));
        }
        Collections.shuffle(arrayList);
        ArrayList arrayList3 = new ArrayList(arrayList.size() + arrayList2.size());
        double d = 0.0;
        if (!arrayList2.isEmpty()) {
            Collections.sort(arrayList2, (pair, pair2) -> {
                Location location2 = ((BotPhrasePoolRecord)pair.getLeft()).getPhrase().getLoc().get();
                Location location3 = ((BotPhrasePoolRecord)pair.getLeft()).getPhrase().getLoc().get();
                return -Double.compare(location.distance(location2), location.distance(location3));
            });
            for (var12_17 = 0; var12_17 < arrayList2.size(); ++var12_17) {
                object2 = (Pair)arrayList2.get(var12_17);
                double d2 = 1.0 / (double)var12_17;
                d += d2;
                arrayList3.add(Pair.of(object2, d2));
                if (var12_17 >= arrayList.size()) continue;
                arrayList3.add(Pair.of((Pair)arrayList.get(var12_17), d2));
                d += d2;
            }
        } else {
            for (var12_17 = 0; var12_17 < arrayList.size(); ++var12_17) {
                arrayList3.add(Pair.of((Pair)arrayList.get(var12_17), 1.0 / (double)var12_17));
            }
        }
        if (arrayList3.isEmpty()) {
            return Optional.empty();
        }
        Pair pair3 = (Pair)RandomUtils.pickRandomSortedGroup((Collection)arrayList3, (double)d);
        if (pair3 != null && ((BotPhrasePoolRecord)(object2 = (BotPhrasePoolRecord)pair3.getLeft())).casLastUse(l = (Long)pair3.getRight(), l2)) {
            object = ((BotPhrasePoolRecord)object2).getPhrase();
            return Optional.of(object);
        }
        return Optional.empty();
    }

    public void loadPhrases() {
        ArrayList<BotPhrasePoolRecord> arrayList = new ArrayList<BotPhrasePoolRecord>();
        for (BotPhrase botPhrase : ActionsStorageManager.getInstance().loadPhrases()) {
            arrayList.add(new BotPhrasePoolRecord(botPhrase));
        }
        this.records = arrayList;
    }

    public Optional<BotPhrase> findPhrase(int n, Location location) {
        return this.findPhrase0(n, location);
    }

    private static final class BotPhrasePoolRecord {
        private final BotPhrase phrase;
        private final AtomicLong lastUse = new AtomicLong(0L);

        private BotPhrasePoolRecord(BotPhrase botPhrase) {
            this.phrase = botPhrase;
        }

        public BotPhrase getPhrase() {
            return this.phrase;
        }

        public long getLastUse() {
            return this.lastUse.get();
        }

        public boolean casLastUse(long l, long l2) {
            return this.lastUse.compareAndSet(l, l2);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            BotPhrasePoolRecord botPhrasePoolRecord = (BotPhrasePoolRecord)object;
            return new EqualsBuilder().append(this.lastUse, botPhrasePoolRecord.lastUse).append(this.phrase, botPhrasePoolRecord.phrase).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(this.phrase).append(this.lastUse).toHashCode();
        }

        public String toString() {
            return "BotPhrasePoolRecord{phrase=" + this.phrase + ", lastUse=" + this.lastUse + "}";
        }
    }
}

