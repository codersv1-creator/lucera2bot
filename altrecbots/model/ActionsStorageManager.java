/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.database.DatabaseFactory
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.model.ActionRecord;
import com.lucera2.scripts.altrecbots.model.BotPhrase;
import com.lucera2.scripts.altrecbots.model.BotSpawnManager;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionsStorageManager {
    private static final Logger logger = LoggerFactory.getLogger(ActionsStorageManager.class);
    private static final ActionsStorageManager instance = new ActionsStorageManager();
    private static final String SELECT_BOTS = "SELECT `id`, `face`, `hairStyle`, `hairColor`, `sex`, `x`, `y`, `z`, `is_noble` FROM `altrec_bots`";
    private static final String SELECT_SUBCLASSES = "SELECT `bot_id`, `class_id`, `exp`, `active`, `is_base` FROM `altrec_subclasses`";
    private static final String SELECT_SKILLS = "SELECT `bot_id`, `skill_id`, `skill_level` FROM `altrec_skills`";
    private static final String SELECT_ITEMS = "SELECT `bot_id`, `item_type`, `amount`, `is_equipped`, `enchant` FROM `altrec_items`";
    private static final String SELECT_ACTIONS = "SELECT `bot_id`, `ord`, `action_type`, `duration`, `body` FROM `altrec_actions` ORDER BY `bot_id`, `ord` ASC";
    private static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID() AS `id`";
    private static final String SELECT_NAMES = "SELECT `name` FROM `altrec_names` WHERE (`sex` = ?) OR (ISNULL(`sex`))";
    private static final String SELECT_TITLES = "SELECT `title` FROM `altrec_title` WHERE (`sex` = ?) OR (ISNULL(`sex`))";
    private static final String SELECT_PHRASES = "SELECT `text`, `sex`, `x`, `y`, `z` FROM `altrec_phrases`";
    private static final String INSERT_BOT = "INSERT INTO `altrec_bots` (`face`, `hairStyle`, `hairColor`, `sex`, `x`, `y`, `z`, `is_noble`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String REPLACE_BOT = "REPLACE INTO `altrec_bots` (id, face, hairStyle, hairColor, sex, x, y, z, is_noble) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String REPLACE_SUBCLASS = "REPLACE INTO `altrec_subclasses` (bot_id, class_id, exp, active, is_base) VALUES (?, ?, ?, ?, ?)";
    private static final String REPLACE_SKILL = "INSERT INTO `altrec_skills` (bot_id, skill_id, skill_level) VALUES (?, ?, ?)";
    private static final String INSERT_ITEM = "INSERT INTO `altrec_items` (bot_id, item_type, amount, is_equipped, enchant) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_ACTION = "INSERT INTO `altrec_actions` (`bot_id`, `ord`, `action_type`, `duration`, `body`) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_NAME = "INSERT INTO `altrec_names` (`name`, `sex`) VALUES (?, ?)";
    private static final String INSERT_TITLE = "INSERT INTO `altrec_title` (`title`, `sex`) VALUES (?, ?)";
    private static final String DELETE_BOT = "DELETE FROM `altrec_bots` WHERE `id` = ?";
    private static final String DELETE_BOT_SUBCLASSES = "DELETE FROM `altrec_subclasses` WHERE `bot_id` = ?";
    private static final String DELETE_BOT_SKILLS = "DELETE FROM `altrec_skills` WHERE `bot_id` = ?";
    private static final String DELETE_BOT_ITEMS = "DELETE FROM `altrec_items` WHERE `bot_id` = ?";
    private static final String DELETE_BOT_ACTIONS = "DELETE FROM `altrec_actions` WHERE `bot_id` = ?";
    private List<ActionRecord> actionRecords = new ArrayList<ActionRecord>();

    private ActionsStorageManager() {
    }

    public static ActionsStorageManager getInstance() {
        return instance;
    }

    public void init() {
        this.actionRecords = this.loadRecords();
        logger.info("AltRecBots: Loaded " + this.actionRecords.size() + " action sequence(s).");
    }

    public List<ActionRecord> getActionRecords() {
        return Collections.unmodifiableList(this.actionRecords);
    }

    public void deleteBotRecord(int n) {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();){
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOT_ACTIONS);){
                preparedStatement.setInt(1, n);
                preparedStatement.executeUpdate();
            }
            preparedStatement = connection.prepareStatement(DELETE_BOT_ITEMS);
            try {
                preparedStatement.setInt(1, n);
                preparedStatement.executeUpdate();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            preparedStatement = connection.prepareStatement(DELETE_BOT_SKILLS);
            try {
                preparedStatement.setInt(1, n);
                preparedStatement.executeUpdate();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            preparedStatement = connection.prepareStatement(DELETE_BOT_SUBCLASSES);
            try {
                preparedStatement.setInt(1, n);
                preparedStatement.executeUpdate();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            preparedStatement = connection.prepareStatement(DELETE_BOT);
            try {
                preparedStatement.setInt(1, n);
                preparedStatement.executeUpdate();
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public List<String> loadNames(int n) {
        ArrayList<String> arrayList = new ArrayList<String>();
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_NAMES);){
            preparedStatement.setInt(1, n);
            try (ResultSet resultSet = preparedStatement.executeQuery();){
                while (resultSet.next()) {
                    arrayList.add(resultSet.getString("name"));
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return arrayList;
    }

    public List<String> loadTitles(int n) {
        ArrayList<String> arrayList = new ArrayList<String>();
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TITLES);){
            preparedStatement.setInt(1, n);
            try (ResultSet resultSet = preparedStatement.executeQuery();){
                while (resultSet.next()) {
                    arrayList.add(resultSet.getString("title"));
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return arrayList;
    }

    public List<BotPhrase> loadPhrases() {
        ArrayList<BotPhrase> arrayList = new ArrayList<BotPhrase>();
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_PHRASES);){
            while (resultSet.next()) {
                String string = resultSet.getString("text");
                int n = resultSet.getInt("sex");
                BotPhrase botPhrase = new BotPhrase(string, n);
                int n2 = resultSet.getInt("x");
                int n3 = resultSet.getInt("y");
                int n4 = resultSet.getInt("z");
                if (!resultSet.wasNull() && n2 != 0 && n3 != 0 && n4 != 0) {
                    botPhrase = botPhrase.setLoc(new Location(n2, n3, n4));
                }
                arrayList.add(botPhrase);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return arrayList;
    }

    public void addNames(Set<String> set, int n) {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NAME);){
            for (String string : set) {
                preparedStatement.setString(1, string);
                preparedStatement.setInt(2, n);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void addTitles(Set<String> set, int n) {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NAME);){
            for (String string : set) {
                preparedStatement.setString(1, string);
                preparedStatement.setInt(2, n);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public List<ActionRecord> loadRecords() {
        Object object;
        block78: {
            Connection connection = DatabaseFactory.getInstance().getConnection();
            try {
                ActionRecord actionRecord;
                int n;
                ResultSet resultSet;
                HashMap<Integer, ActionRecord> hashMap = new HashMap<Integer, ActionRecord>();
                object = connection.createStatement();
                try {
                    resultSet = object.executeQuery(SELECT_BOTS);
                    try {
                        while (resultSet.next()) {
                            n = resultSet.getInt("id");
                            hashMap.put(n, new ActionRecord(n, resultSet.getInt("face"), resultSet.getInt("hairStyle"), resultSet.getInt("hairColor"), resultSet.getInt("sex"), new Location(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")), resultSet.getBoolean("is_noble")));
                        }
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                } finally {
                    if (object != null) {
                        object.close();
                    }
                }
                object = connection.createStatement();
                try {
                    resultSet = object.executeQuery(SELECT_SUBCLASSES);
                    try {
                        while (resultSet.next()) {
                            n = resultSet.getInt("bot_id");
                            actionRecord = (ActionRecord)hashMap.get(n);
                            if (actionRecord == null) continue;
                            actionRecord.getSubclasses().add(new ActionRecord.SubclassRecord(resultSet.getInt("class_id"), resultSet.getLong("exp"), resultSet.getBoolean("active"), resultSet.getBoolean("is_base")));
                        }
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                } finally {
                    if (object != null) {
                        object.close();
                    }
                }
                object = connection.createStatement();
                try {
                    resultSet = object.executeQuery(SELECT_SKILLS);
                    try {
                        while (resultSet.next()) {
                            n = resultSet.getInt("bot_id");
                            actionRecord = (ActionRecord)hashMap.get(n);
                            if (actionRecord == null) continue;
                            actionRecord.getSkills().add(new ActionRecord.SkillRecord(resultSet.getInt("skill_id"), resultSet.getInt("skill_level")));
                        }
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                } finally {
                    if (object != null) {
                        object.close();
                    }
                }
                object = connection.createStatement();
                try {
                    resultSet = object.executeQuery(SELECT_ITEMS);
                    try {
                        while (resultSet.next()) {
                            n = resultSet.getInt("bot_id");
                            actionRecord = (ActionRecord)hashMap.get(n);
                            if (actionRecord == null) continue;
                            actionRecord.getItems().add(new ActionRecord.ItemRecord(resultSet.getInt("item_type"), resultSet.getLong("amount"), resultSet.getInt("enchant"), resultSet.getBoolean("is_equipped")));
                        }
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                } finally {
                    if (object != null) {
                        object.close();
                    }
                }
                object = connection.createStatement();
                try {
                    resultSet = object.executeQuery(SELECT_ACTIONS);
                    try {
                        while (resultSet.next()) {
                            n = resultSet.getInt("bot_id");
                            actionRecord = (ActionRecord)hashMap.get(n);
                            if (actionRecord == null) continue;
                            int n2 = resultSet.getInt("ord");
                            String string = resultSet.getString("action_type");
                            ActionType actionType = Objects.requireNonNull(ActionType.valueOf(string), "Unknown action type '" + string + "'");
                            long l = resultSet.getLong("duration");
                            String string2 = Objects.requireNonNull(StringUtils.trimToNull(resultSet.getString("body")), "Body is empty for " + n + ":" + n2);
                            Object SelfT = Objects.requireNonNull((Action)BotUtils.getGSON().fromJson(string2, actionType.getActionClass()), "Body is null for " + n + ":" + n2).setDuration(l);
                            actionRecord.getActions().add((Action)SelfT);
                        }
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                } finally {
                    if (object != null) {
                        object.close();
                    }
                }
                object = new ArrayList(hashMap.values());
                if (connection == null) break block78;
            } catch (Throwable throwable) {
                try {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
            connection.close();
        }
        return object;
    }

    public ActionRecord storeRecord(ActionRecord actionRecord) {
        actionRecord = this.storeRecord0(actionRecord);
        actionRecord.getId().orElseThrow(() -> new RuntimeException("'id' not specified"));
        this.actionRecords.add(actionRecord);
        BotSpawnManager.getInstance().addActionRecord(actionRecord);
        return actionRecord;
    }

    /*
     * WARNING - void declaration
     */
    public ActionRecord storeRecord0(ActionRecord actionRecord) {
        try (Connection connection = DatabaseFactory.getInstance().getConnection();){
            AutoCloseable autoCloseable;
            Statement statement;
            Location location = Objects.requireNonNull(actionRecord.getLocation(), "'location' is null");
            if (actionRecord.getId().isPresent()) {
                statement = connection.prepareStatement(REPLACE_BOT);
                try {
                    statement.setInt(1, actionRecord.getId().get());
                    statement.setInt(2, actionRecord.getFace());
                    statement.setInt(3, actionRecord.getHairStyle());
                    statement.setInt(4, actionRecord.getHairColor());
                    statement.setInt(5, actionRecord.getSex());
                    statement.setInt(6, location.getX());
                    statement.setInt(7, location.getY());
                    statement.setInt(8, location.getZ());
                    statement.setBoolean(9, actionRecord.isNoble());
                    statement.executeUpdate();
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
            statement = connection.prepareStatement(INSERT_BOT);
            try {
                statement.setInt(1, actionRecord.getFace());
                statement.setInt(2, actionRecord.getHairStyle());
                statement.setInt(3, actionRecord.getHairColor());
                statement.setInt(4, actionRecord.getSex());
                statement.setInt(5, location.getX());
                statement.setInt(6, location.getY());
                statement.setInt(7, location.getZ());
                statement.setBoolean(8, actionRecord.isNoble());
                statement.execute();
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
            statement = connection.createStatement();
            try {
                autoCloseable = statement.executeQuery(SELECT_LAST_INSERT_ID);
                try {
                    if (autoCloseable.next()) {
                        actionRecord.setId(Optional.of(autoCloseable.getInt("id")));
                    }
                } finally {
                    if (autoCloseable != null) {
                        autoCloseable.close();
                    }
                }
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
            int n = actionRecord.getId().orElseThrow(() -> new RuntimeException("'id' not specified"));
            autoCloseable = connection.prepareStatement(REPLACE_SUBCLASS);
            try {
                for (ActionRecord.SubclassRecord object : actionRecord.getSubclasses()) {
                    autoCloseable.setInt(1, n);
                    autoCloseable.setInt(2, object.getClassId());
                    autoCloseable.setLong(3, object.getExp());
                    autoCloseable.setBoolean(4, object.isActive());
                    autoCloseable.setBoolean(5, object.isBase());
                    autoCloseable.addBatch();
                }
                autoCloseable.executeBatch();
            } finally {
                if (autoCloseable != null) {
                    autoCloseable.close();
                }
            }
            autoCloseable = connection.prepareStatement(REPLACE_SKILL);
            try {
                for (ActionRecord.SkillRecord skillRecord : actionRecord.getSkills()) {
                    autoCloseable.setInt(1, n);
                    autoCloseable.setInt(2, skillRecord.getSkillId());
                    autoCloseable.setInt(3, skillRecord.getSkillLevel());
                    autoCloseable.addBatch();
                }
                autoCloseable.executeBatch();
            } finally {
                if (autoCloseable != null) {
                    autoCloseable.close();
                }
            }
            autoCloseable = connection.prepareStatement(INSERT_ITEM);
            try {
                for (ActionRecord.ItemRecord itemRecord : actionRecord.getItems()) {
                    autoCloseable.setInt(1, n);
                    autoCloseable.setInt(2, itemRecord.getItemType());
                    autoCloseable.setLong(3, itemRecord.getAmount());
                    autoCloseable.setBoolean(4, itemRecord.isEquipped());
                    autoCloseable.setInt(5, itemRecord.getEnchant());
                    autoCloseable.addBatch();
                }
                autoCloseable.executeBatch();
            } finally {
                if (autoCloseable != null) {
                    autoCloseable.close();
                }
            }
            autoCloseable = connection.prepareStatement(INSERT_ACTION);
            try {
                void var7_32;
                List<Action> list = actionRecord.getActions();
                boolean bl = false;
                while (var7_32 < list.size()) {
                    Action action = (Action)list.get((int)var7_32);
                    autoCloseable.setInt(1, n);
                    autoCloseable.setInt(2, (int)var7_32);
                    autoCloseable.setString(3, action.getActionType().toString());
                    autoCloseable.setLong(4, action.getDuration());
                    autoCloseable.setString(5, BotUtils.getGSON().toJson((Object)action));
                    autoCloseable.addBatch();
                    ++var7_32;
                }
                autoCloseable.executeBatch();
            } finally {
                if (autoCloseable != null) {
                    autoCloseable.close();
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return actionRecord;
    }
}

