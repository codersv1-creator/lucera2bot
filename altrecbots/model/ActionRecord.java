/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ActionRecord {
    private Optional<Integer> idOpt;
    private int face;
    private int hairStyle;
    private int hairColor;
    private int sex;
    private Location location;
    private boolean isNoble;
    private List<SubclassRecord> subclasses;
    private List<SkillRecord> skills;
    private List<ItemRecord> items;
    private List<Action> actions = new ArrayList<Action>();

    public ActionRecord(Optional<Integer> optional, int n, int n2, int n3, int n4, Location location, boolean bl, List<SubclassRecord> list, List<SkillRecord> list2, List<ItemRecord> list3) {
        this.idOpt = optional;
        this.face = n;
        this.hairStyle = n2;
        this.hairColor = n3;
        this.sex = n4;
        this.location = location;
        this.isNoble = bl;
        this.subclasses = list;
        this.skills = list2;
        this.items = list3;
    }

    public ActionRecord(int n, int n2, int n3, int n4, Location location, boolean bl, List<SubclassRecord> list, List<SkillRecord> list2, List<ItemRecord> list3) {
        this(Optional.empty(), n, n2, n3, n4, location, bl, list, list2, list3);
    }

    public ActionRecord(Integer n, int n2, int n3, int n4, int n5, Location location, boolean bl) {
        this(Optional.ofNullable(n), n2, n3, n4, n5, location, bl, new ArrayList<SubclassRecord>(), new ArrayList<SkillRecord>(), new ArrayList<ItemRecord>());
    }

    public List<Action> getActions() {
        return this.actions;
    }

    public ActionRecord setActions(List<Action> list) {
        this.actions = list;
        return this;
    }

    public Optional<Integer> getId() {
        return this.idOpt;
    }

    public ActionRecord setId(Integer n) {
        return this.setId(Optional.ofNullable(n));
    }

    public ActionRecord setId(Optional<Integer> optional) {
        this.idOpt = optional;
        return this;
    }

    public int getFace() {
        return this.face;
    }

    public ActionRecord setFace(int n) {
        this.face = n;
        return this;
    }

    public int getHairStyle() {
        return this.hairStyle;
    }

    public ActionRecord setHairStyle(int n) {
        this.hairStyle = n;
        return this;
    }

    public int getHairColor() {
        return this.hairColor;
    }

    public ActionRecord setHairColor(int n) {
        this.hairColor = n;
        return this;
    }

    public int getSex() {
        return this.sex;
    }

    public ActionRecord setSex(int n) {
        this.sex = n;
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public ActionRecord setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Location getLocationRandomized() {
        if (Config.PLAYBACK_SPAWN_POS_RANDOM_RADIUS > 0) {
            return Location.findPointToStay((Location)this.getLocation(), (int)(Config.PLAYBACK_SPAWN_POS_RANDOM_RADIUS / 3), (int)Config.PLAYBACK_SPAWN_POS_RANDOM_RADIUS);
        }
        return this.getLocation();
    }

    public boolean isNoble() {
        return this.isNoble;
    }

    public ActionRecord setNoble(boolean bl) {
        this.isNoble = bl;
        return this;
    }

    public List<SubclassRecord> getSubclasses() {
        return this.subclasses;
    }

    public ActionRecord setSubclasses(List<SubclassRecord> list) {
        this.subclasses = list;
        return this;
    }

    public Optional<SubclassRecord> getBaseSubclass() {
        return this.getSubclasses().stream().filter(SubclassRecord::isBase).findFirst();
    }

    public List<SkillRecord> getSkills() {
        return this.skills;
    }

    public ActionRecord setSkills(List<SkillRecord> list) {
        this.skills = list;
        return this;
    }

    public List<ItemRecord> getItems() {
        return this.items;
    }

    public ActionRecord setItems(List<ItemRecord> list) {
        this.items = list;
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ActionRecord actionRecord = (ActionRecord)object;
        if (this.idOpt.isPresent() && actionRecord.idOpt.isPresent()) {
            return new EqualsBuilder().append(this.idOpt, actionRecord.idOpt).isEquals();
        }
        return new EqualsBuilder().append(this.idOpt, actionRecord.idOpt).append(this.face, actionRecord.face).append(this.hairStyle, actionRecord.hairStyle).append(this.hairColor, actionRecord.hairColor).append(this.sex, actionRecord.sex).append(this.isNoble, actionRecord.isNoble).append(this.location, actionRecord.location).append(this.subclasses, actionRecord.subclasses).append(this.skills, actionRecord.skills).append(this.items, actionRecord.items).isEquals();
    }

    public int hashCode() {
        if (this.idOpt.isPresent()) {
            return new HashCodeBuilder(17, 37).append(this.idOpt.get()).toHashCode();
        }
        return new HashCodeBuilder(17, 37).append(this.face).append(this.hairStyle).append(this.hairColor).append(this.sex).append(this.location).append(this.isNoble).append(this.subclasses).append(this.skills).append(this.items).toHashCode();
    }

    public String toString() {
        return "ActionRecord{id=" + this.idOpt.orElse(null) + ", face=" + this.face + ", hairStyle=" + this.hairStyle + ", hairColor=" + this.hairColor + ", sex=" + this.sex + ", location=" + this.location + ", isNoble=" + this.isNoble + ", subclasses=" + this.subclasses + ", skills=" + this.skills + ", items=" + this.items + "}";
    }

    public static class ItemRecord {
        private int itemType;
        private long amount;
        private int enchant;
        private boolean isEquipped;

        public ItemRecord(int n, long l, int n2, boolean bl) {
            this.itemType = n;
            this.amount = l;
            this.enchant = n2;
            this.isEquipped = bl;
        }

        public int getItemType() {
            return this.itemType;
        }

        public ItemRecord setItemType(int n) {
            this.itemType = n;
            return this;
        }

        public long getAmount() {
            return this.amount;
        }

        public ItemRecord setAmount(long l) {
            this.amount = l;
            return this;
        }

        public int getEnchant() {
            return this.enchant;
        }

        public ItemRecord setEnchant(int n) {
            this.enchant = n;
            return this;
        }

        public boolean isEquipped() {
            return this.isEquipped;
        }

        public ItemRecord setEquipped(boolean bl) {
            this.isEquipped = bl;
            return this;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ItemRecord itemRecord = (ItemRecord)object;
            return new EqualsBuilder().append(this.itemType, itemRecord.itemType).append(this.amount, itemRecord.amount).append(this.enchant, itemRecord.enchant).append(this.isEquipped, itemRecord.isEquipped).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(this.itemType).append(this.amount).append(this.enchant).append(this.isEquipped).toHashCode();
        }

        public String toString() {
            return "ItemRecord{itemType=" + this.itemType + ", amount=" + this.amount + ", enchant=" + this.enchant + ", isEquipped=" + this.isEquipped + "}";
        }
    }

    public static class SkillRecord {
        private int skillId;
        private int skillLevel;

        public SkillRecord(int n, int n2) {
            this.skillId = n;
            this.skillLevel = n2;
        }

        public int getSkillId() {
            return this.skillId;
        }

        public SkillRecord setSkillId(int n) {
            this.skillId = n;
            return this;
        }

        public int getSkillLevel() {
            return this.skillLevel;
        }

        public SkillRecord setSkillLevel(int n) {
            this.skillLevel = n;
            return this;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            SkillRecord skillRecord = (SkillRecord)object;
            return new EqualsBuilder().append(this.skillId, skillRecord.skillId).append(this.skillLevel, skillRecord.skillLevel).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(this.skillId).append(this.skillLevel).toHashCode();
        }

        public String toString() {
            return "SkillRecord{skillId=" + this.skillId + ", skillLevel=" + this.skillLevel + "}";
        }
    }

    public static class SubclassRecord {
        private int classId;
        private long exp;
        private boolean isActive;
        private boolean isBase;

        public SubclassRecord(int n, long l, boolean bl, boolean bl2) {
            this.classId = n;
            this.exp = l;
            this.isActive = bl;
            this.isBase = bl2;
        }

        public int getClassId() {
            return this.classId;
        }

        public SubclassRecord setClassId(int n) {
            this.classId = n;
            return this;
        }

        public long getExp() {
            return this.exp;
        }

        public SubclassRecord setExp(long l) {
            this.exp = l;
            return this;
        }

        public boolean isActive() {
            return this.isActive;
        }

        public SubclassRecord setActive(boolean bl) {
            this.isActive = bl;
            return this;
        }

        public boolean isBase() {
            return this.isBase;
        }

        public SubclassRecord setBase(boolean bl) {
            this.isBase = bl;
            return this;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            SubclassRecord subclassRecord = (SubclassRecord)object;
            return new EqualsBuilder().append(this.classId, subclassRecord.classId).append(this.exp, subclassRecord.exp).append(this.isActive, subclassRecord.isActive).append(this.isBase, subclassRecord.isBase).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(this.classId).append(this.exp).append(this.isActive).append(this.isBase).toHashCode();
        }

        public String toString() {
            return "SubclassRecord{classId=" + this.classId + ", exp=" + this.exp + ", isActive=" + this.isActive + ", isBase=" + this.isBase + "}";
        }
    }
}

