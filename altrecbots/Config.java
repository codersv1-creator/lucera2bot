/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.configuration.ExProperties
 *  l2.gameserver.Config
 *  l2.gameserver.model.Request$L2RequestType
 *  l2.gameserver.model.Skill
 *  l2.gameserver.tables.SkillTable
 *  l2.gameserver.templates.item.ItemTemplate
 */
package com.lucera2.scripts.altrecbots;

import com.lucera2.scripts.altrecbots.model.BotSpawnStrategy;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import l2.commons.configuration.ExProperties;
import l2.gameserver.model.Request;
import l2.gameserver.model.Skill;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    private static final String a = "config/altrecbots.properties";
    public static boolean BOTS_ENABLED;
    public static boolean BOTS_LOG_ENABLE;
    public static boolean DEDICATED_EXECUTOR;
    public static int DEDICATED_EXECUTOR_THREADS;
    public static int DEDICATED_SCHEDULED_THREADS;
    public static boolean AUTO_RECORD_PLAYER_ACTIONS;
    public static int AUTO_RECORD_MIN_LVL;
    public static int AUTO_RECORD_MAX_LVL;
    public static boolean AUTO_RECORD_IGNORE_NOBLE;
    public static boolean AUTO_RECORD_IGNORE_HERO;
    public static boolean AUTO_RECORD_IGNORE_GM;
    public static String[] AUTO_RECORD_IGNORE_ZONES;
    public static boolean AUTO_RECORD_IGNORE_TELEPORT;
    public static int RECORD_MIN_LENGTH;
    public static int RECORD_MAX_LENGTH;
    public static long RECORD_MIN_DURATION;
    public static long RECORD_MAX_DURATION;
    public static boolean AUTO_RECORD_INSTANT_NEW_SEQUENCE;
    public static boolean LOOP_PLAYBACK;
    public static long BOT_TTL;
    public static int[] PLAYBACK_IGNORED_ITEM_IDS;
    public static Map<Integer, Double> PLAYBACK_SPAWN_CLASSID_PROBABILITY_MOD;
    public static int PLAYBACK_SEQUENCE_SELECTOR_RANDOM_SLOPE_MOD;
    public static double PLAYBACK_SEQUENCE_SELECTOR_RANDOM_THRESHOLD;
    public static int PLAYBACK_SPAWN_POS_RANDOM_RADIUS;
    public static String INITIAL_BOTS_TITLE;
    public static double INDIVIDUAL_BOT_TITLE_CHANCE;
    public static long BOTS_UNSPAWN_INTERVAL_MIN;
    public static long BOTS_UNSPAWN_INTERVAL_MAX;
    public static int BOTS_SPAWN_MIN_LEVEL;
    public static int BOTS_SPAWN_MAX_LEVEL;
    public static long BOTS_SPAWN_INTERVAL_MIN;
    public static long BOTS_SPAWN_INTERVAL_MAX;
    public static long BOTS_FIRST_ACTION_MIN;
    public static long BOTS_FIRST_ACTION_MAX;
    public static long BOTS_SPAWN_CHECK_INTERVAL;
    public static Map<Request.L2RequestType, Double> BOT_ACCEPT_REQUEST_CHANCE;
    public static Map<Request.L2RequestType, Double> BOT_DENY_REQUEST_CHANCE;
    public static long PHRASE_REUSE_TIME;
    public static double BOT_TALK_CHANCE;
    public static double BOT_TALK_CHANCE_SHOUT;
    public static String BOT_ACCOUNT_NAME;
    public static Supplier<Integer> BOT_COUNT_SUPPLIER;
    public static Map<ItemTemplate, Long> BOT_ADDITIONAL_INVENTORY_ITEMS;
    public static List<Skill> BOT_INITIAL_EFFECTS;
    public static int BOT_ITEM_ENCHANT_ANIMATE_LIMIT;
    public static int BOT_NPC_FIND_RADIUS;
    public static List<Pair<Integer, Integer>> BOT_MAGE_BUFF_ON_CHAR_CREATE;
    public static List<Pair<Integer, Integer>> BOT_WARRIOR_BUFF_ON_CHAR_CREATE;

    private Config() {
    }

    public static Supplier<Integer> parseStrategy(String string, String string2) {
        String[] stringArray = StringUtils.isBlank(string2) ? ArrayUtils.EMPTY_STRING_ARRAY : (String[])Stream.of(StringUtils.split(string2, ',')).map(StringUtils::trimToEmpty).toArray(String[]::new);
        BotSpawnStrategy botSpawnStrategy = BotSpawnStrategy.valueOf(string);
        return () -> botSpawnStrategy.getSpawnNeeded(stringArray);
    }

    public static void load() {
        String[] stringArray;
        int n;
        ExProperties exProperties = l2.gameserver.Config.load((String)a);
        BOTS_ENABLED = exProperties.getProperty("BotsEnabled", false);
        DEDICATED_EXECUTOR = exProperties.getProperty("UseDedicatedExecutor", true);
        DEDICATED_EXECUTOR_THREADS = exProperties.getProperty("DedicatedExecutorThreads", 1);
        DEDICATED_SCHEDULED_THREADS = exProperties.getProperty("DedicatedScheduledThreads", 2);
        AUTO_RECORD_PLAYER_ACTIONS = exProperties.getProperty("AutoRecordPlayerActions", false);
        AUTO_RECORD_MIN_LVL = exProperties.getProperty("AutoRecordMinLvl", 10);
        AUTO_RECORD_MAX_LVL = exProperties.getProperty("AutoRecordMaxLvl", 78);
        AUTO_RECORD_IGNORE_NOBLE = exProperties.getProperty("AutoRecordIgnoreNoble", false);
        AUTO_RECORD_IGNORE_HERO = exProperties.getProperty("AutoRecordIgnoreHero", true);
        AUTO_RECORD_IGNORE_GM = exProperties.getProperty("AutoRecordIgnoreGM", false);
        AUTO_RECORD_IGNORE_ZONES = exProperties.getProperty("AutoRecordIgnoreZones", ArrayUtils.EMPTY_STRING_ARRAY);
        AUTO_RECORD_IGNORE_TELEPORT = exProperties.getProperty("AutoRecordIgnorePeaceTeleport", true);
        RECORD_MIN_LENGTH = exProperties.getProperty("PlayerRecordMinSequenceLength", 10);
        RECORD_MAX_LENGTH = exProperties.getProperty("PlayerRecordMaxSequenceLength", 500);
        RECORD_MIN_DURATION = exProperties.getProperty("PlayerRecordMinSequenceDuration", 10000L);
        RECORD_MAX_DURATION = exProperties.getProperty("PlayerRecordMaxSequenceDuration", 600000L);
        AUTO_RECORD_INSTANT_NEW_SEQUENCE = exProperties.getProperty("AutoRecordNewSequence", true);
        LOOP_PLAYBACK = exProperties.getProperty("LoopPlayback", false);
        BOT_TTL = exProperties.getProperty("PlaybackBotTTL", 600000L);
        BOT_ITEM_ENCHANT_ANIMATE_LIMIT = exProperties.getProperty("BotItemEnchantAnimateLimit", 128);
        PLAYBACK_SPAWN_POS_RANDOM_RADIUS = exProperties.getProperty("PlaybackSpawnPosRandomRadius", 128);
        PLAYBACK_IGNORED_ITEM_IDS = exProperties.getProperty("PlaybackIgnoredItemIds", ArrayUtils.EMPTY_INT_ARRAY);
        PLAYBACK_SEQUENCE_SELECTOR_RANDOM_SLOPE_MOD = exProperties.getProperty("PlaybackSequenceSelectorRandomSlopeMod", 10);
        PLAYBACK_SEQUENCE_SELECTOR_RANDOM_THRESHOLD = exProperties.getProperty("PlaybackSequenceSelectorRandomThreshold", 0.01);
        String string2 = exProperties.getProperty("PlaybackClassIdProbabilityMod", "");
        Object object = new StringTokenizer(string2, ";");
        while (((StringTokenizer)object).hasMoreTokens()) {
            String string3 = ((StringTokenizer)object).nextToken();
            if (StringUtils.isBlank(string3)) continue;
            n = string3.indexOf(58);
            if (n < 0 || n == 0) {
                System.err.println("Skipping invalid token: '" + string3 + "'");
                continue;
            }
            try {
                int n2 = Integer.parseInt(string3.substring(0, n).trim());
                double d = Double.parseDouble(string3.substring(n + 1).trim());
                PLAYBACK_SPAWN_CLASSID_PROBABILITY_MOD.put(n2, d);
            } catch (NumberFormatException numberFormatException) {
                System.err.println("Failed to parse token: '" + string3 + "'. Error: " + numberFormatException.getMessage());
            }
        }
        BOT_TALK_CHANCE = exProperties.getProperty("BotTalkChance", 0.5);
        BOT_TALK_CHANCE_SHOUT = exProperties.getProperty("BotTalkChanceShout", 0.2);
        PHRASE_REUSE_TIME = exProperties.getProperty("PhraseReuseTime", 30000L);
        BOT_ACCOUNT_NAME = exProperties.getProperty("BotAccountName", "ololo_bot_account");
        object = exProperties.getProperty("BotSpawnStrategy", "Constant(500)");
        if (!StringUtils.isBlank((CharSequence)object)) {
            int n3 = ((String)object).indexOf(40);
            n = ((String)object).lastIndexOf(41);
            BOT_COUNT_SUPPLIER = Config.parseStrategy(StringUtils.trimToEmpty(((String)object).substring(0, n3)), ((String)object).substring(n3 + 1, n));
        } else {
            BOT_COUNT_SUPPLIER = null;
        }
        BOTS_UNSPAWN_INTERVAL_MIN = exProperties.getProperty("BotsUnspawnIntervalMin", 5000);
        BOTS_UNSPAWN_INTERVAL_MAX = exProperties.getProperty("BotsUnspawnIntervalMax", 15000);
        BOTS_SPAWN_MIN_LEVEL = exProperties.getProperty("BotsSpawnLevelMin", 1);
        BOTS_SPAWN_MAX_LEVEL = exProperties.getProperty("BotsSpawnLevelMax", 80);
        BOTS_FIRST_ACTION_MIN = exProperties.getProperty("BotsFirstActionMin", 5000);
        BOTS_FIRST_ACTION_MAX = exProperties.getProperty("BotsFirstActionMax", 5000);
        BOTS_SPAWN_INTERVAL_MIN = exProperties.getProperty("BotsSpawnIntervalMin", 5000);
        BOTS_SPAWN_INTERVAL_MAX = exProperties.getProperty("BotsSpawnIntervalMax", 15000);
        BOTS_SPAWN_CHECK_INTERVAL = exProperties.getProperty("BotsSpawnCheckInterval", 60000);
        BOT_ACCEPT_REQUEST_CHANCE = Stream.of(exProperties.getProperty("BotAcceptRequestChances", ArrayUtils.EMPTY_STRING_ARRAY)).filter(StringUtils::isNotBlank).collect(Collectors.toMap(string -> Request.L2RequestType.valueOf((String)string.substring(0, string.indexOf(58))), string -> Double.parseDouble(string.substring(string.indexOf(58) + 1).trim())));
        BOT_DENY_REQUEST_CHANCE = Stream.of(exProperties.getProperty("BotDenyRequestChances", ArrayUtils.EMPTY_STRING_ARRAY)).filter(StringUtils::isNotBlank).collect(Collectors.toMap(string -> Request.L2RequestType.valueOf((String)string.substring(0, string.indexOf(58))), string -> Double.parseDouble(string.substring(string.indexOf(58) + 1).trim())));
        BOT_ADDITIONAL_INVENTORY_ITEMS = Stream.of(exProperties.getProperty("BotAdditionalInventoryItems", ArrayUtils.EMPTY_STRING_ARRAY)).filter(StringUtils::isNotBlank).collect(Collectors.toMap(string -> BotUtils.getItemTemplate(Integer.parseInt(string.substring(0, string.indexOf(58)).trim())).get(), string -> Long.parseLong(string.substring(string.indexOf(58) + 1).trim())));
        BOT_INITIAL_EFFECTS = Stream.of(exProperties.getProperty("BotInitialEffects", ArrayUtils.EMPTY_STRING_ARRAY)).filter(StringUtils::isNotBlank).map(string -> SkillTable.getInstance().getInfo(Integer.parseInt(string.substring(0, string.indexOf(58)).trim()), Integer.parseInt(string.substring(string.indexOf(58) + 1).trim()))).collect(Collectors.toList());
        BOT_NPC_FIND_RADIUS = exProperties.getProperty("BotFindNpcAtRadius", 1024);
        INITIAL_BOTS_TITLE = exProperties.getProperty("InitialBotsTitle", "");
        INDIVIDUAL_BOT_TITLE_CHANCE = exProperties.getProperty("IndividualBotTitleChance", 30.0);
        BOT_MAGE_BUFF_ON_CHAR_CREATE = new ArrayList<Pair<Integer, Integer>>();
        for (String string4 : StringUtils.split(exProperties.getProperty("BotMageBuffList", "1303-1"), ";,")) {
            stringArray = StringUtils.split(string4, "-:");
            BOT_MAGE_BUFF_ON_CHAR_CREATE.add(Pair.of(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1])));
        }
        BOT_WARRIOR_BUFF_ON_CHAR_CREATE = new ArrayList<Pair<Integer, Integer>>();
        for (String string5 : StringUtils.split(exProperties.getProperty("BotWarriorBuffList", "1086-1"), ";,")) {
            stringArray = StringUtils.split(string5, "-:");
            BOT_WARRIOR_BUFF_ON_CHAR_CREATE.add(Pair.of(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1])));
        }
    }

    static {
        PLAYBACK_SPAWN_CLASSID_PROBABILITY_MOD = new HashMap<Integer, Double>();
    }
}

