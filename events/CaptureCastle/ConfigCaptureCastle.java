package events.CaptureCastle;

import java.util.HashMap;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigCaptureCastle {
   private static Logger _log = LoggerFactory.getLogger(ConfigCaptureCastle.class);
   public static final String CONFIG_FILE = "config/events/capture_castle.properties";
   public static int CAPTURE_CASTLE_Time_Battle;
   public static int CAPTURE_CASTLE_Time_TO_START;
   public static boolean CAPTURE_CASTLE_Allow_Calendar_Day;
   public static int[] CAPTURE_CASTLE_Time_Start;
   public static int CAPTURE_CASTLE_Time_Paralyze;
   public static boolean CAPTURE_CASTLE_AllowClanSkill;
   public static boolean CAPTURE_CASTLE_AllowHeroSkill;
   public static boolean CAPTURE_CASTLE_DispelTransformation;
   public static boolean CAPTURE_CASTLE_AllowSummons;
   public static boolean CAPTURE_CASTLE_Categories;
   public static boolean CAPTURE_CASTLE_AllowBuffs;
   public static boolean CAPTURE_CASTLE_AllowHwidCheck;
   public static boolean CAPTURE_CASTLE_AllowIpCheck;
   public static String[] CAPTURE_CASTLE_FighterBuffs;
   public static String[] CAPTURE_CASTLE_MageBuffs;
   public static boolean CAPTURE_CASTLE_BuffPlayers;
   public static int[] CAPTURE_CASTLE_Rewards;
   public static boolean CAPTURE_CASTLE_EnableTopKiller;
   public static Map<Integer, int[]> CAPTURE_CASTLE_TopKillerReward;
   public static boolean CAPTURE_CASTLE_EnableKillsInTitle;
   public static int[] CAPTURE_CASTLE_INCLUDE_ITEMS;
   public static int[] CAPTURE_CASTLE_RESTRICTED_SKILL_IDS;
   public static int CAPTURE_CASTLE_BUFF_TIME_MAGE;
   public static int CAPTURE_CASTLE_BUFF_TIME_FIGHTER;
   public static boolean CAPTURE_CASTLE_CAN_PARTY_INVITE;
   public static int CAPTURE_CASTLE_MaxPlayerInTeam;
   public static int CAPTURE_CASTLE_MinPlayerInTeam;
   public static int CAPTURE_CASTLE_Instance;
   public static int CAPTURE_CASTLE_FLAG_ID;
   public static Location CAPTURE_CASTLE_FLAG_LOC;
   public static int[] CAPTURE_CASTLE_DOORS;
   public static String CAPTURE_CASTLE_ZONE;
   public static Location CAPTURE_CASTLE_FIRST_TEAM_SPAWN_LOC;
   public static Location CAPTURE_CASTLE_SECOND_TEAM_SPAWN_LOC;
   public static Location CAPTURE_CASTLE_SPAWN_LOC_OWNER_TEAM;
   public static Location CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_RED_TEAM;
   public static Location CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_BLUE_TEAM;
   public static int CAPTURE_CASTLE_TIME_BACK;
   public static boolean CAPTURE_CASTLE_SPAWN_REG_MANAGER;
   public static int CAPTURE_CASTLE_REG_MANAGER_ID;
   public static String CAPTURE_CASTLE_REG_MANAGER_LOC;
   public static boolean CAPTURE_CASTLE_ENABLE_COMMAND;
   public static boolean CAPTURE_CASTLE_SEND_REG_WINDOW;
   public static boolean CAPTURE_CASTLE_RETURN_POINT_ENABLE;
   public static Location CAPTURE_CASTLE_RETURN_POINT;
   public static boolean CAPTURE_CASTLE_RESTORE_DESTROYED_DOORS;
   public static int CAPTURE_CASTLE_RES_DELAY;
   public static boolean CAPTURE_CASTLE_BROADCAST_TIMER;
   public static boolean CAPTURE_CASTLE_HIDE_NAME;

   public static void load() {
      ExProperties EventSettings = Config.load("config/events/capture_castle.properties");
      CAPTURE_CASTLE_Time_Battle = EventSettings.getProperty("CaptureCastleTimeBattle", 5);
      CAPTURE_CASTLE_Time_TO_START = EventSettings.getProperty("CaptureCastleTimeToStart", 5);
      CAPTURE_CASTLE_Allow_Calendar_Day = EventSettings.getProperty("CaptureCastleAllow_Calendar_Day", false);
      CAPTURE_CASTLE_Time_Start = EventSettings.getProperty("CaptureCastleTime_Start", new int[]{18, 30, 6});
      CAPTURE_CASTLE_Time_Paralyze = EventSettings.getProperty("CaptureCastleTime_Paralyze", 60);
      CAPTURE_CASTLE_AllowClanSkill = EventSettings.getProperty("CaptureCastleAllowClanSkill", false);
      CAPTURE_CASTLE_AllowHeroSkill = EventSettings.getProperty("CaptureCastleAllowHeroSkill", false);
      CAPTURE_CASTLE_AllowSummons = EventSettings.getProperty("CaptureCastleAllowSummons", false);
      CAPTURE_CASTLE_DispelTransformation = EventSettings.getProperty("CaptureCastleDispelTransformation", false);
      CAPTURE_CASTLE_Rewards = EventSettings.getProperty("CaptureCastleRewards", new int[]{57, 10000});
      CAPTURE_CASTLE_EnableTopKiller = EventSettings.getProperty("CaptureCastleEnableTopKiller", true);
      CAPTURE_CASTLE_TopKillerReward = new HashMap();
      String[] topKillerRewards = EventSettings.getProperty("CaptureCastleTopKillerRewards", "1:4037,100;2:4037,50;3:4037,20").split(";");
      String[] var2 = topKillerRewards;
      int var3 = topKillerRewards.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String reward = var2[var4];
         if (!reward.trim().isEmpty()) {
            String[] rewardParts = reward.split(":");
            int place = Integer.parseInt(rewardParts[0]);
            String[] itemStrs = rewardParts[1].split(",");
            int[] items = new int[itemStrs.length];

            for(int i = 0; i < itemStrs.length; ++i) {
               items[i] = Integer.parseInt(itemStrs[i]);
            }

            CAPTURE_CASTLE_TopKillerReward.put(place, items);
         }
      }

      CAPTURE_CASTLE_EnableKillsInTitle = EventSettings.getProperty("CaptureCastleKillsInTitle", false);
      CAPTURE_CASTLE_Categories = EventSettings.getProperty("CaptureCastleCategories", false);
      CAPTURE_CASTLE_AllowSummons = EventSettings.getProperty("CaptureCastleAllowSummons", false);
      CAPTURE_CASTLE_AllowBuffs = EventSettings.getProperty("CaptureCastleAllowBuffs", false);
      CAPTURE_CASTLE_AllowHwidCheck = EventSettings.getProperty("CaptureCastleAllowHwidCheck", false);
      CAPTURE_CASTLE_AllowIpCheck = EventSettings.getProperty("CaptureCastleAllowIpCheck", false);
      CAPTURE_CASTLE_FighterBuffs = EventSettings.getProperty("CaptureCastleFighterBuffs", "").trim().replaceAll(" ", "").split(";");
      CAPTURE_CASTLE_MageBuffs = EventSettings.getProperty("CaptureCastleMageBuffs", "").trim().replaceAll(" ", "").split(";");
      CAPTURE_CASTLE_BuffPlayers = EventSettings.getProperty("CaptureCastleBuffPlayers", false);
      CAPTURE_CASTLE_INCLUDE_ITEMS = EventSettings.getProperty("CaptureCastleIncludeItems", new int[0]);
      CAPTURE_CASTLE_RESTRICTED_SKILL_IDS = EventSettings.getProperty("CaptureCastleRestrictedSkillIds", new int[0]);
      CAPTURE_CASTLE_BUFF_TIME_FIGHTER = EventSettings.getProperty("CaptureCastleBuffTimeFighter", 20);
      CAPTURE_CASTLE_BUFF_TIME_MAGE = EventSettings.getProperty("CaptureCastleBuffTimeMage", 20);
      CAPTURE_CASTLE_CAN_PARTY_INVITE = EventSettings.getProperty("CaptureCastleCanPartyInvite", false);
      CAPTURE_CASTLE_MaxPlayerInTeam = EventSettings.getProperty("CaptureCastleMaxPlayerInTeam", 200);
      CAPTURE_CASTLE_MinPlayerInTeam = EventSettings.getProperty("CaptureCastleMinPlayerInTeam", 1);
      CAPTURE_CASTLE_Instance = EventSettings.getProperty("CaptureCastleInstance", 611);
      CAPTURE_CASTLE_FLAG_ID = EventSettings.getProperty("CaptureCastleFlagId", 50025);
      CAPTURE_CASTLE_FLAG_LOC = Location.parseLoc(EventSettings.getProperty("CaptureCastleFlagLoc", "22073,161778,-2674,49152"));
      CAPTURE_CASTLE_DOORS = EventSettings.getProperty("CaptureCastleDoors", new int[]{20220001, 20220002, 20220005, 20220006});
      CAPTURE_CASTLE_ZONE = EventSettings.getProperty("CaptureCastleZone", "[capture_castle_dion]");
      CAPTURE_CASTLE_FIRST_TEAM_SPAWN_LOC = Location.parseLoc(EventSettings.getProperty("CaptureCastleFirstTeamSpawnLoc", "24328,153144,-3010"));
      CAPTURE_CASTLE_SECOND_TEAM_SPAWN_LOC = Location.parseLoc(EventSettings.getProperty("CaptureCastleSecondTeamSpawnLoc", "19863,154472,-3051"));
      CAPTURE_CASTLE_SPAWN_LOC_OWNER_TEAM = Location.parseLoc(EventSettings.getProperty("CaptureCastleSpawnLocOwnerTeam", "22264,160712,-2754"));
      CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_BLUE_TEAM = Location.parseLoc(EventSettings.getProperty("CaptureCastleSpawnLocAttackingBlueTeam", "23112,154040,-3010"));
      CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_RED_TEAM = Location.parseLoc(EventSettings.getProperty("CaptureCastleSpawnLocAttackingRedTeam", "23112,154040,-3010"));
      CAPTURE_CASTLE_TIME_BACK = EventSettings.getProperty("CaptureCastleTimeBack", 30);
      CAPTURE_CASTLE_SPAWN_REG_MANAGER = EventSettings.getProperty("CaptureCastleSpawnRegManager", false);
      CAPTURE_CASTLE_REG_MANAGER_ID = EventSettings.getProperty("CaptureCastleRegManagerId", 31225);
      CAPTURE_CASTLE_REG_MANAGER_LOC = EventSettings.getProperty("CaptureCastleRegManagerLoc", "83448,148375,-3425,47670");
      CAPTURE_CASTLE_ENABLE_COMMAND = EventSettings.getProperty("CaptureCastleEnableCommand", true);
      CAPTURE_CASTLE_SEND_REG_WINDOW = EventSettings.getProperty("CaptureCastleSendRegWindow", true);
      CAPTURE_CASTLE_RETURN_POINT_ENABLE = EventSettings.getProperty("CaptureCastleReturnPointEnable", true);
      CAPTURE_CASTLE_RETURN_POINT = Location.parseLoc(EventSettings.getProperty("CaptureCastleReturnPoint", "81043,148618,-3472"));
      CAPTURE_CASTLE_RESTORE_DESTROYED_DOORS = EventSettings.getProperty("CaptureCastleRestoreDestroyedDoors", true);
      CAPTURE_CASTLE_RES_DELAY = EventSettings.getProperty("CaptureCastleResDelay", 10);
      CAPTURE_CASTLE_BROADCAST_TIMER = EventSettings.getProperty("CaptureCastleBroadcastTimer", true);
      CAPTURE_CASTLE_HIDE_NAME = EventSettings.getProperty("CaptureCastleHideName", true);
   }
}
