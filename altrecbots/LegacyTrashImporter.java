/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.gameserver.Config
 *  l2.gameserver.GameServer
 *  l2.gameserver.Shutdown
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.base.ClassId
 *  l2.gameserver.model.base.Experience
 *  l2.gameserver.tables.SkillTable
 *  l2.gameserver.templates.item.ItemTemplate
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots;

import com.lucera2.scripts.altrecbots.App;
import com.lucera2.scripts.altrecbots.model.ActionRecord;
import com.lucera2.scripts.altrecbots.model.ActionsStorageManager;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.ActionType;
import com.lucera2.scripts.altrecbots.model.actions.EquipItem;
import com.lucera2.scripts.altrecbots.model.actions.ItemSetEnchant;
import com.lucera2.scripts.altrecbots.model.actions.SkillCast;
import com.lucera2.scripts.altrecbots.model.actions.Subclass;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.Shutdown;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Experience;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyTrashImporter {
    private static final Logger a = LoggerFactory.getLogger(LegacyTrashImporter.class);
    private static File b = null;
    private static File c = null;
    private static File d = null;
    private static File e = null;
    private static File f = null;
    private static File g = null;
    private static File h = null;

    private static final void a() {
        System.out.println("Usage: LegacyImporter -ai <ai_txt_file> -male_names <male_names_file> -female_names <female_names_file> -male_phrases <male_phrases_file> -female_phrases <male_female_phrases> -male_titles <male_titles_file> -female_titles <female_titles_file>");
    }

    public static void main(String ... stringArray) throws Exception {
        App.isLegacyImport = true;
        if (stringArray.length < 0) {
            LegacyTrashImporter.a();
            System.exit(-1);
            return;
        }
        for (int i = 0; i < stringArray.length; ++i) {
            String string = stringArray[i];
            if ("-ai".equalsIgnoreCase(string) && i + 1 < stringArray.length) {
                b = new File(stringArray[i + 1]);
                ++i;
                continue;
            }
            if ("-male_names".equalsIgnoreCase(string) && i + 1 < stringArray.length) {
                c = new File(stringArray[i + 1]);
                ++i;
                continue;
            }
            if ("-female_names".equalsIgnoreCase(string) && i + 1 < stringArray.length) {
                d = new File(stringArray[i + 1]);
                ++i;
                continue;
            }
            if ("-male_phrases".equalsIgnoreCase(string) && i + 1 < stringArray.length) {
                e = new File(stringArray[i + 1]);
                ++i;
                continue;
            }
            if ("-female_phrases".equalsIgnoreCase(string) && i + 1 < stringArray.length) {
                f = new File(stringArray[i + 1]);
                ++i;
                continue;
            }
            if ("-male_title".equalsIgnoreCase(string) && i + 1 < stringArray.length) {
                g = new File(stringArray[i + 1]);
                ++i;
                continue;
            }
            if (!"-female_title".equalsIgnoreCase(string) || i + 1 >= stringArray.length) continue;
            h = new File(stringArray[i + 1]);
            ++i;
        }
        if (b == null && c == null && d == null && e == null && f == null && g == null && h == null || b != null && !b.exists() || c != null && !c.exists() || d != null && !d.exists() || e != null && !e.exists() || f != null && !f.exists() || g != null && !g.exists() || h != null && !h.exists()) {
            LegacyTrashImporter.a();
            System.exit(-1);
            return;
        }
        GameServer.main((String[])ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static final List<ActionRecord> parseLegacyTrash(InputStream inputStream) throws Exception {
        ArrayList<ActionRecord> arrayList = new ArrayList<ActionRecord>();
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            int n = 0;
            String string = StringUtils.trim(bufferedReader.readLine());
            while (string != null) {
                block32: {
                    if (!StringUtils.isBlank(string) && !StringUtils.startsWith(string, "#")) {
                        int n2 = StringUtils.indexOf((CharSequence)string, 58);
                        if (n2 == -1) {
                            a.info("Malformed player set line location {}: \"{}\"", (Object)n, (Object)string);
                        } else {
                            int n3 = StringUtils.indexOf((CharSequence)string, 58, n2 + 1);
                            Location location = Location.parseLoc((String)StringUtils.substring(string, 0, n2));
                            if (n3 == -1) {
                                a.info("Malformed player set line args {}: \"{}\"", (Object)n, (Object)string);
                            } else {
                                String[] stringArray = StringUtils.split(StringUtils.trimToEmpty(StringUtils.substring(string, n2 + 1, n3)), ',');
                                if (stringArray.length < 10) {
                                    a.info("Malformed player set line args {}: \"{}\"", (Object)n, (Object)string);
                                } else {
                                    String string2;
                                    int n4;
                                    int n5;
                                    int n6;
                                    ArrayList<ActionRecord.ItemRecord> arrayList2 = new ArrayList<ActionRecord.ItemRecord>();
                                    ArrayList<ActionRecord.SubclassRecord> arrayList3 = new ArrayList<ActionRecord.SubclassRecord>();
                                    ArrayList<ActionRecord.SkillRecord> arrayList4 = new ArrayList<ActionRecord.SkillRecord>();
                                    ArrayList<Action> arrayList5 = new ArrayList<Action>();
                                    int n7 = StringUtils.indexOf((CharSequence)string, 58, n3 + 1);
                                    int n8 = StringUtils.lastIndexOf((CharSequence)string, 58);
                                    if (n7 != -1 && n8 > n3) {
                                        String string3 = StringUtils.trimToEmpty(StringUtils.substring(string, n3 + 1, n7));
                                        for (String string4 : StringUtils.split(string3, ';')) {
                                            if ((string4 = StringUtils.trimToEmpty(string4)).isEmpty() || (n6 = StringUtils.indexOf((CharSequence)string4, 44)) == -1) continue;
                                            n5 = Integer.parseInt(StringUtils.trim(StringUtils.substring(string4, 0, n6)));
                                            n4 = Integer.parseInt(StringUtils.trim(StringUtils.substring(string4, n6 + 1)));
                                            arrayList2.add(new ActionRecord.ItemRecord(n5, 1L, n4, true));
                                        }
                                    }
                                    int n9 = Integer.parseInt(stringArray[0]);
                                    int n10 = Integer.parseInt(stringArray[1]);
                                    int n11 = Integer.parseInt(stringArray[2]);
                                    int n12 = Integer.parseInt(stringArray[3]);
                                    int n13 = Integer.parseInt(stringArray[4]);
                                    n6 = Integer.parseInt(stringArray[5]);
                                    n5 = Integer.parseInt(stringArray[6]);
                                    n4 = Integer.parseInt(stringArray[7]);
                                    int n14 = Integer.parseInt(stringArray[8]);
                                    arrayList3.add(new ActionRecord.SubclassRecord(n9, Experience.getExpForLevel((int)n5), n6 == n9, true));
                                    if (n6 != n9) {
                                        arrayList3.add(new ActionRecord.SubclassRecord(n6, Experience.getExpForLevel((int)n4), true, false));
                                    }
                                    if (!StringUtils.isBlank(string2 = StringUtils.trimToEmpty(StringUtils.substring(string, n8 + 1)))) {
                                        ActionRecord actionRecord = new ActionRecord(n13, n11, n12, n10, location, n14 != 0, arrayList3, arrayList4, arrayList2);
                                        block17: for (String string5 : StringUtils.split(string2, ';')) {
                                            String[] stringArray2 = StringUtils.split(string5, ',');
                                            if (stringArray2.length != 8) {
                                                a.info("Malformed player set line args {}: \"{}\"", (Object)n, (Object)string);
                                                break block32;
                                            }
                                            ActionType actionType = ActionType.getActionTypeByLegacyOrd(Integer.parseInt(stringArray2[0]));
                                            if (actionType == null) {
                                                a.info("Unknown player action {}. Line args {}: \"{}\"", string2, String.valueOf(n), string);
                                                break block32;
                                            }
                                            Object object = actionType.newActionInstance();
                                            if ((object = ((Action)object).fromLegacy(new int[]{Integer.parseInt(stringArray2[2]), Integer.parseInt(stringArray2[3]), Integer.parseInt(stringArray2[4]), Integer.parseInt(stringArray2[5]), Integer.parseInt(stringArray2[6]), Integer.parseInt(stringArray2[7])})) == null) continue;
                                            ((Action)object).setDuration(Long.parseLong(stringArray2[1]));
                                            arrayList5.add((Action)object);
                                            switch (actionType) {
                                                case SKILL_CAST: {
                                                    int n15 = ((SkillCast)object).getSkillId();
                                                    int n16 = SkillTable.getInstance().getMaxLevel(n15);
                                                    if (n16 <= 0) continue block17;
                                                    arrayList4.add(new ActionRecord.SkillRecord(n15, n16));
                                                    continue block17;
                                                }
                                                case EQUIP_ITEM: 
                                                case ITEM_SET_ENCHANT: {
                                                    Optional<ItemTemplate> optional = BotUtils.getItemTemplate(actionType == ActionType.EQUIP_ITEM ? ((EquipItem)object).getItemId() : ((ItemSetEnchant)object).getItemId());
                                                    if (!optional.isPresent()) continue block17;
                                                    ItemTemplate itemTemplate = optional.get();
                                                    arrayList2.add(new ActionRecord.ItemRecord(itemTemplate.getItemId(), 1L, 0, false));
                                                    continue block17;
                                                }
                                                case SUBCLASS: {
                                                    ClassId classId = ((Subclass)object).getClassId();
                                                    ClassId classId2 = ((Subclass)object).getNewClassId();
                                                    if (classId != null && arrayList3.stream().filter(subclassRecord -> subclassRecord.getClassId() != classId.getId()).findAny().isPresent()) {
                                                        arrayList3.add(new ActionRecord.SubclassRecord(classId.getId(), Experience.getExpForLevel((int)Math.max(Player.EXPERTISE_LEVELS[classId.getLevel()], n4)), false, false));
                                                    }
                                                    if (classId2 == null || !arrayList3.stream().filter(subclassRecord -> subclassRecord.getClassId() != classId2.getId()).findAny().isPresent()) continue block17;
                                                    arrayList3.add(new ActionRecord.SubclassRecord(classId2.getId(), Experience.getExpForLevel((int)Math.max(Player.EXPERTISE_LEVELS[classId2.getLevel()], n4)), false, false));
                                                }
                                            }
                                        }
                                        if (!arrayList5.isEmpty()) {
                                            arrayList.add(actionRecord.setSkills(actionRecord.getSkills().stream().distinct().collect(Collectors.toList())).setSubclasses(actionRecord.getSubclasses().stream().distinct().collect(Collectors.toList())).setItems(actionRecord.getItems().stream().distinct().collect(Collectors.toList())).setActions(arrayList5));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                string = StringUtils.trim(bufferedReader.readLine());
                ++n;
            }
        }
        return arrayList;
    }

    protected static void onLoad() {
        Config.PORTS_GAME = ArrayUtils.EMPTY_INT_ARRAY;
    }

    private static void b() {
        if (b == null || !b.exists()) {
            a.info("LegacyImporter: Skip legacy ai.");
            return;
        }
        a.info("LegacyImporter: Importing legacy ai from \"{}\"...", (Object)b);
        try {
            try (FileInputStream fileInputStream = new FileInputStream(b);){
                int n;
                List<ActionRecord> list = LegacyTrashImporter.parseLegacyTrash(fileInputStream);
                a.info("LegacyImporter: Parsed " + list.size() + " record(s) from \"" + b.toString() + "\".");
                a.info("LegacyImporter: Storing player action record(s) ...");
                for (n = 0; n < list.size(); ++n) {
                    ActionsStorageManager.getInstance().storeRecord(list.get(n));
                    if (n % 1000 != 0) continue;
                    a.info("LegacyImporter: Stored " + n + " records.");
                }
                a.info("LegacyImporter: Stored " + n + " records.");
            }
            a.info("LegacyImporter: \"" + b.toString() + "\" done.");
        } catch (Exception exception) {
            a.error(exception.getMessage(), exception);
        }
    }

    private static void c() {
        if (c == null || !c.exists()) {
            a.info("LegacyImporter: Skip legacy male names.");
            return;
        }
        a.info("LegacyImporter: Importing legacy male names from \"{}\"...", (Object)c);
        try (FileInputStream fileInputStream = new FileInputStream(c);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fileInputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.addAll(ActionsStorageManager.getInstance().loadNames(0));
            arrayList.addAll(ActionsStorageManager.getInstance().loadNames(1));
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
            String string = bufferedReader.readLine();
            while (string != null) {
                if (!StringUtils.isBlank(string = StringUtils.trimToEmpty(string)) && string.charAt(0) != '#') {
                    String string3 = string;
                    if (!arrayList.stream().filter(string2 -> StringUtils.equalsIgnoreCase(string2, string3)).findAny().isPresent()) {
                        linkedHashSet.add(string);
                    }
                }
                string = bufferedReader.readLine();
            }
            ActionsStorageManager.getInstance().addNames(linkedHashSet, 0);
            a.info("LegacyImporter: Imported " + linkedHashSet.size() + " new names from \"" + c.toString() + "\" done.");
        } catch (Exception exception) {
            a.error(exception.getMessage(), exception);
        }
    }

    private static void d() {
        if (d == null || !d.exists()) {
            a.info("LegacyImporter: Skip legacy female names.");
            return;
        }
        a.info("LegacyImporter: Importing legacy female names from \"{}\"...", (Object)d);
        try (FileInputStream fileInputStream = new FileInputStream(d);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fileInputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.addAll(ActionsStorageManager.getInstance().loadNames(0));
            arrayList.addAll(ActionsStorageManager.getInstance().loadNames(1));
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
            String string = bufferedReader.readLine();
            while (string != null) {
                if (!StringUtils.isBlank(string = StringUtils.trimToEmpty(string)) && string.charAt(0) != '#') {
                    String string3 = string;
                    if (!arrayList.stream().filter(string2 -> StringUtils.equalsIgnoreCase(string2, string3)).findAny().isPresent()) {
                        linkedHashSet.add(string);
                    }
                }
                string = bufferedReader.readLine();
            }
            ActionsStorageManager.getInstance().addNames(linkedHashSet, 1);
            a.info("LegacyImporter: Imported " + linkedHashSet.size() + " new names from \"" + c.toString() + "\" done.");
        } catch (Exception exception) {
            a.error(exception.getMessage(), exception);
        }
    }

    private static void e() {
        if (g == null || !g.exists()) {
            a.info("LegacyImporter: Skip legacy male titles.");
            return;
        }
        a.info("LegacyImporter: Importing legacy male titles from \"{}\"...", (Object)g);
        try (FileInputStream fileInputStream = new FileInputStream(g);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fileInputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.addAll(ActionsStorageManager.getInstance().loadTitles(0));
            arrayList.addAll(ActionsStorageManager.getInstance().loadTitles(1));
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
            String string = bufferedReader.readLine();
            while (string != null) {
                if (!StringUtils.isBlank(string = StringUtils.trimToEmpty(string)) && string.charAt(0) != '#') {
                    String string3 = string;
                    if (!arrayList.stream().filter(string2 -> StringUtils.equalsIgnoreCase(string2, string3)).findAny().isPresent()) {
                        linkedHashSet.add(string);
                    }
                }
                string = bufferedReader.readLine();
            }
            ActionsStorageManager.getInstance().addTitles(linkedHashSet, 0);
            a.info("LegacyImporter: Imported " + linkedHashSet.size() + " new titles from \"" + g.toString() + "\" done.");
        } catch (Exception exception) {
            a.error(exception.getMessage(), exception);
        }
    }

    private static void f() {
        if (h == null || !h.exists()) {
            a.info("LegacyImporter: Skip legacy female titles.");
            return;
        }
        a.info("LegacyImporter: Importing legacy female titles from \"{}\"...", (Object)h);
        try (FileInputStream fileInputStream = new FileInputStream(h);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fileInputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.addAll(ActionsStorageManager.getInstance().loadTitles(0));
            arrayList.addAll(ActionsStorageManager.getInstance().loadTitles(1));
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
            String string = bufferedReader.readLine();
            while (string != null) {
                if (!StringUtils.isBlank(string = StringUtils.trimToEmpty(string)) && string.charAt(0) != '#') {
                    String string3 = string;
                    if (!arrayList.stream().filter(string2 -> StringUtils.equalsIgnoreCase(string2, string3)).findAny().isPresent()) {
                        linkedHashSet.add(string);
                    }
                }
                string = bufferedReader.readLine();
            }
            ActionsStorageManager.getInstance().addTitles(linkedHashSet, 1);
            a.info("LegacyImporter: Imported " + linkedHashSet.size() + " new titles from \"" + h.toString() + "\" done.");
        } catch (Exception exception) {
            a.error(exception.getMessage(), exception);
        }
    }

    protected static void onStart() {
        LegacyTrashImporter.b();
        LegacyTrashImporter.c();
        LegacyTrashImporter.d();
        LegacyTrashImporter.e();
        LegacyTrashImporter.f();
        a.info("LegacyImporter: Shutdown ...");
        Shutdown.getInstance().schedule(0, 0);
    }
}

