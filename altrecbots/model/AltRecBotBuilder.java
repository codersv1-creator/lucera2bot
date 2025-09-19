/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.ai.CharacterAI
 *  l2.gameserver.ai.PlayerAI
 *  l2.gameserver.data.xml.holder.CharacterTemplateHolder
 *  l2.gameserver.data.xml.holder.ItemHolder
 *  l2.gameserver.idfactory.IdFactory
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.SubClass
 *  l2.gameserver.model.base.ClassId
 *  l2.gameserver.model.base.Experience
 *  l2.gameserver.model.items.ItemInstance
 *  l2.gameserver.tables.SkillTable
 *  l2.gameserver.templates.PlayerTemplate
 *  l2.gameserver.templates.item.ItemTemplate
 *  l2.gameserver.templates.item.support.Grade
 *  l2.gameserver.utils.ItemFunctions
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.model.AltRecBotAI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.PlayerAI;
import l2.gameserver.data.xml.holder.CharacterTemplateHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.support.Grade;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class AltRecBotBuilder
implements Builder<AltRecBot> {
    private static final int defaultNameColor = 0xFFFFFF;
    private static final int defaultTitleColor = 0xFFFF77;
    private static final Location defaultLocation = new Location(-113360, -244676, -15536);
    private final List<ClassId> classIds = new ArrayList<ClassId>();
    private int objId;
    private ClassId activeClassId;
    private ClassId baseClassId;
    private String accountName;
    private int face;
    private int hairStyle;
    private int hairColor;
    private int sex;
    private String name;
    private int nameColor = 0xFFFFFF;
    private String title;
    private int titleColor = 0xFFFF77;
    private int initialLvl;
    private boolean isNoble;
    private PlayerAI playerAI;
    private List<Triple<ItemTemplate, Integer, Long>> inventory = new ArrayList<Triple<ItemTemplate, Integer, Long>>();
    private List<ItemTemplate> equipment = new ArrayList<ItemTemplate>();

    public List<Triple<ItemTemplate, Integer, Long>> getInventory() {
        return this.inventory;
    }

    public AltRecBotBuilder setInventory(List<Triple<ItemTemplate, Integer, Long>> list) {
        this.inventory = list;
        return this;
    }

    public AltRecBotBuilder addItem(ItemTemplate itemTemplate, int n, long l, boolean bl) {
        this.getInventory().add(Triple.of(itemTemplate, n, l));
        if (bl && itemTemplate.isEquipable()) {
            this.equipment.add(itemTemplate);
        }
        return this;
    }

    public AltRecBotBuilder addItem(int n, int n2, long l, boolean bl) {
        return this.addItem(ItemHolder.getInstance().getTemplate(n), n2, l, bl);
    }

    public int getObjId() {
        return this.objId;
    }

    public AltRecBotBuilder setObjId(int n) {
        this.objId = n;
        return this;
    }

    public AltRecBotBuilder addClassId(ClassId classId, boolean bl, boolean bl2) {
        this.classIds.add(classId);
        if (this.baseClassId == null || bl) {
            this.baseClassId = classId;
        }
        if (this.activeClassId == null || bl2) {
            this.activeClassId = classId;
        }
        return this;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public AltRecBotBuilder setAccountName(String string) {
        this.accountName = string;
        return this;
    }

    public int getSex() {
        return this.sex;
    }

    public AltRecBotBuilder setSex(int n) {
        this.sex = n;
        return this;
    }

    public int getFace() {
        return this.face;
    }

    public AltRecBotBuilder setFace(int n) {
        this.face = n;
        return this;
    }

    public int getHairStyle() {
        return this.hairStyle;
    }

    public AltRecBotBuilder setHairStyle(int n) {
        this.hairStyle = n;
        return this;
    }

    public int getHairColor() {
        return this.hairColor;
    }

    public AltRecBotBuilder setHairColor(int n) {
        this.hairColor = n;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public AltRecBotBuilder setName(String string) {
        this.name = string;
        return this;
    }

    public int getNameColor() {
        return this.nameColor;
    }

    public AltRecBotBuilder setNameColor(int n) {
        this.nameColor = n;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public AltRecBotBuilder setTitle(String string) {
        this.title = string;
        return this;
    }

    public int getTitleColor() {
        return this.titleColor;
    }

    public AltRecBotBuilder setTitleColor(int n) {
        this.titleColor = n;
        return this;
    }

    public int getInitialLvl() {
        return this.initialLvl;
    }

    public AltRecBotBuilder setInitialLvl(int n) {
        this.initialLvl = n;
        return this;
    }

    public boolean isNoble() {
        return this.isNoble;
    }

    public AltRecBotBuilder setNoble(boolean bl) {
        this.isNoble = bl;
        return this;
    }

    public List<ItemTemplate> getEquipment() {
        return this.equipment;
    }

    public AltRecBotBuilder setEquipment(List<ItemTemplate> list) {
        this.equipment = list;
        return this;
    }

    public PlayerAI getPlayerAI() {
        return this.playerAI;
    }

    public AltRecBotBuilder setPlayerAI(PlayerAI playerAI) {
        this.playerAI = playerAI;
        return this;
    }

    private void initInventoryItems(AltRecBot altRecBot) {
        AltRecBot.AltRecBotInventory altRecBotInventory = altRecBot.getInventory();
        ArrayList<Triple<ItemTemplate, Integer, Long>> arrayList = new ArrayList<Triple<ItemTemplate, Integer, Long>>(this.inventory);
        for (Map.Entry<ItemTemplate, Long> object : Config.BOT_ADDITIONAL_INVENTORY_ITEMS.entrySet()) {
            arrayList.add(Triple.of(object.getKey(), 0, object.getValue()));
        }
        for (Triple triple : arrayList) {
            ItemTemplate itemTemplate = (ItemTemplate)triple.getLeft();
            if (ArrayUtils.contains(Config.PLAYBACK_IGNORED_ITEM_IDS, itemTemplate.getItemId())) continue;
            ItemInstance itemInstance = ItemFunctions.createItem((int)((ItemTemplate)triple.getLeft()).getItemId());
            itemInstance.setCustomFlags(47);
            if (itemTemplate.getCrystalType() != Grade.NONE && (Integer)triple.getMiddle() > 0 && itemInstance.canBeEnchanted(false)) {
                itemInstance.setEnchantLevel(Math.min(Config.BOT_ITEM_ENCHANT_ANIMATE_LIMIT, (Integer)triple.getMiddle()));
            }
            itemInstance.setCount(((Long)triple.getRight()).longValue());
            altRecBotInventory.addItem(itemInstance);
            if (!this.equipment.contains(itemTemplate)) continue;
            altRecBot.getInventory().equipItem(itemInstance);
        }
        altRecBot.useShots();
    }

    @Override
    public AltRecBot build() {
        SubClass subClass;
        int n = this.objId != 0 ? this.objId : (this.objId = IdFactory.getInstance().getNextId());
        PlayerTemplate playerTemplate = CharacterTemplateHolder.getInstance().getTemplate(this.baseClassId, this.sex == 0);
        AltRecBot altRecBot = new AltRecBot(n, playerTemplate, Objects.requireNonNull(this.accountName, "'accountName' is null"));
        altRecBot.setRace(0, this.activeClassId.getRace().ordinal());
        for (ClassId object : this.classIds) {
            subClass = new SubClass();
            subClass.setClassId(object.getId());
            int n2 = Math.max(this.initialLvl, Player.EXPERTISE_LEVELS[object.getLevel()]);
            subClass.setExp(Experience.getExpForLevel((int)n2));
            subClass.setSp(0L);
            if (object == this.activeClassId) {
                subClass.setActive(true);
                subClass.setBase(object == this.baseClassId);
            }
            if (object == this.baseClassId) {
                altRecBot.setBaseClass(object.getId());
            }
            altRecBot.getSubClasses().put(object.getId(), subClass);
            if (object != this.activeClassId) continue;
            altRecBot.setActiveClass(subClass);
            altRecBot.setClassId(object.getId(), true, false);
        }
        altRecBot.setFace(this.face);
        altRecBot.setHairStyle(this.hairStyle);
        altRecBot.setHairColor(this.hairColor);
        altRecBot.setNameColor(this.nameColor);
        altRecBot.setName(Objects.requireNonNull(StringUtils.trimToNull(this.name)));
        altRecBot.setTitleColor(this.titleColor);
        altRecBot.setDisconnectedTitleColor(this.titleColor);
        if (!StringUtils.isBlank(this.title)) {
            altRecBot.setTitle(this.title);
            altRecBot.setDisconnectedTitle(this.title);
        } else {
            altRecBot.setTitle("");
            altRecBot.setDisconnectedTitle("");
        }
        altRecBot.setOnlineStatus(true);
        altRecBot.entering = false;
        altRecBot.setAI((CharacterAI)(this.playerAI != null ? this.playerAI : new AltRecBotAI(altRecBot)));
        altRecBot.setLoc(defaultLocation);
        altRecBot.setCurrentHpMp(altRecBot.getMaxHp(), altRecBot.getMaxMp());
        altRecBot.setRunning();
        altRecBot.stopAutoSaveTask();
        this.initInventoryItems(altRecBot);
        for (Pair pair : altRecBot.isMageClass() ? Config.BOT_MAGE_BUFF_ON_CHAR_CREATE : Config.BOT_WARRIOR_BUFF_ON_CHAR_CREATE) {
            subClass = SkillTable.getInstance().getInfo(((Integer)pair.getLeft()).intValue(), ((Integer)pair.getRight()).intValue());
            subClass.getEffects((Creature)altRecBot, (Creature)altRecBot, false, false);
        }
        return altRecBot;
    }
}

