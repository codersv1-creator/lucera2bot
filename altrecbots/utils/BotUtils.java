/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  l2.commons.lang.ArrayUtils
 *  l2.gameserver.ai.PlayerAI
 *  l2.gameserver.data.xml.holder.ItemHolder
 *  l2.gameserver.instancemanager.ReflectionManager
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.GameObject
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.Skill
 *  l2.gameserver.model.Zone$ZoneType
 *  l2.gameserver.model.base.ClassId
 *  l2.gameserver.model.base.TeamType
 *  l2.gameserver.model.instances.NpcInstance
 *  l2.gameserver.model.items.PcInventory
 *  l2.gameserver.model.items.Warehouse
 *  l2.gameserver.templates.item.ItemTemplate
 */
package com.lucera2.scripts.altrecbots.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.model.AltRecBot;
import com.lucera2.scripts.altrecbots.utils.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.ai.PlayerAI;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.tuple.Pair;

public class BotUtils {
    private static final Gson a = new GsonBuilder().create();

    private BotUtils() {
    }

    public static Gson getGSON() {
        return a;
    }

    public static boolean isBot(Player player) {
        return player != null && player instanceof AltRecBot;
    }

    public static boolean isBot(Creature creature) {
        return creature != null && creature instanceof AltRecBot;
    }

    public static Optional<ClassId> toClassId(int n) {
        return Stream.of(ClassId.values()).filter(classId -> classId.getId() == n).findAny();
    }

    public static NpcInstance setMyTargetByNpcId(AltRecBot altRecBot, Integer n) {
        if (n == null) {
            return null;
        }
        for (NpcInstance npcInstance : altRecBot.getAroundNpc(Config.BOT_NPC_FIND_RADIUS, 256)) {
            if (!Objects.equals(npcInstance.getNpcId(), n)) continue;
            altRecBot.setTarget((GameObject)npcInstance);
            return npcInstance;
        }
        return null;
    }

    public static Optional<ItemTemplate> getItemTemplate(int n) {
        return Optional.ofNullable((ItemTemplate)ArrayUtils.valid((Object[])ItemHolder.getInstance().getAllTemplates(), (int)n));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Optional<Pair<Skill, Boolean>> getSkillAndForceUseFromPlayableAI(PlayerAI playerAI) {
        try {
            Boolean bl = null;
            Skill skill = null;
            for (Field field : playerAI.getClass().getSuperclass().getDeclaredFields()) {
                boolean bl2;
                if (StringUtils.equalsIgnoreCase(field.getName(), "_forceUse") && field.getType() == Boolean.TYPE) {
                    bl2 = field.isAccessible();
                    try {
                        if (!bl2) {
                            field.setAccessible(true);
                        }
                        bl = field.getBoolean(playerAI);
                        continue;
                    } finally {
                        if (!bl2 && field.isAccessible()) {
                            field.setAccessible(false);
                        }
                    }
                }
                if (!StringUtils.equalsIgnoreCase(field.getName(), "_skill") || field.getType() != Skill.class) continue;
                bl2 = field.isAccessible();
                try {
                    if (!bl2) {
                        field.setAccessible(true);
                    }
                    skill = (Skill)field.get(playerAI);
                } finally {
                    if (!bl2 && field.isAccessible()) {
                        field.setAccessible(false);
                    }
                }
            }
            if (skill == null || bl == null) {
                return Optional.empty();
            }
            return Optional.of(Pair.of(skill, bl));
        } catch (Exception exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public static boolean applyInventoryHack(AltRecBot altRecBot, AltRecBot.AltRecBotInventory altRecBotInventory) throws Exception {
        try {
            return !ReflectionUtils.forEachField(altRecBot, (field, mutable) -> {
                if (TypeUtils.isAssignable(field.getType(), PcInventory.class)) {
                    mutable.setValue(altRecBotInventory);
                    return false;
                }
                return true;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static boolean applyWarhouseHack(AltRecBot altRecBot, AltRecBot.AltRecBotWarhouse altRecBotWarhouse) throws Exception {
        try {
            return !ReflectionUtils.forEachField(altRecBot, (field, mutable) -> {
                if (TypeUtils.isAssignable(field.getType(), Warehouse.class)) {
                    mutable.setValue(altRecBotWarhouse);
                    return false;
                }
                return true;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static boolean testRecordingCondition(Player player) {
        if (Config.AUTO_RECORD_IGNORE_GM && player.isGM()) {
            return false;
        }
        if (Config.AUTO_RECORD_IGNORE_HERO && player.isHero() || Config.AUTO_RECORD_IGNORE_NOBLE && player.isNoble()) {
            return false;
        }
        if (player.getLevel() < Config.AUTO_RECORD_MIN_LVL || player.getLevel() > Config.AUTO_RECORD_MAX_LVL) {
            return false;
        }
        if (player.isMounted() || player.isInvisible()) {
            return false;
        }
        if (player.getReflection() != null && player.getReflection() != ReflectionManager.DEFAULT) {
            return false;
        }
        if (player.isOlyParticipant() || player.isInObserverMode() || player.getTeam() != TeamType.NONE || player.isInOfflineMode() || !player.isOnline() || player.isLogoutStarted()) {
            return false;
        }
        if (player.isCursedWeaponEquipped() || player.isFishing() || player.isFalling()) {
            return false;
        }
        if (!(player.isInZone(Zone.ZoneType.peace_zone) || Config.AUTO_RECORD_IGNORE_TELEPORT && player.isTeleporting())) {
            return false;
        }
        for (String string : Config.AUTO_RECORD_IGNORE_ZONES) {
            if (!player.isInZone(string)) continue;
            return false;
        }
        return true;
    }
}

