/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.lang.reference.HardReference
 *  l2.commons.listener.Listener
 *  l2.gameserver.ai.PlayerAI
 *  l2.gameserver.model.Creature
 *  l2.gameserver.model.GameObject
 *  l2.gameserver.model.Player
 *  l2.gameserver.model.Skill
 *  l2.gameserver.model.SubClass
 *  l2.gameserver.model.base.ClassId
 *  l2.gameserver.model.instances.NpcInstance
 *  l2.gameserver.model.items.ItemInstance
 *  l2.gameserver.templates.item.ItemTemplate
 *  l2.gameserver.utils.Location
 */
package com.lucera2.scripts.altrecbots.model;

import com.lucera2.scripts.altrecbots.Config;
import com.lucera2.scripts.altrecbots.ContextHolder;
import com.lucera2.scripts.altrecbots.model.ActionRecord;
import com.lucera2.scripts.altrecbots.model.ActionsStorageManager;
import com.lucera2.scripts.altrecbots.model.BaseContext;
import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.Attack;
import com.lucera2.scripts.altrecbots.model.actions.EquipItem;
import com.lucera2.scripts.altrecbots.model.actions.MoveToLocation;
import com.lucera2.scripts.altrecbots.model.actions.SkillCast;
import com.lucera2.scripts.altrecbots.model.actions.Subclass;
import com.lucera2.scripts.altrecbots.model.actions.TeleportTo;
import com.lucera2.scripts.altrecbots.model.actions.UnEquipItem;
import com.lucera2.scripts.altrecbots.model.listeners.InventoryListener;
import com.lucera2.scripts.altrecbots.model.listeners.MoveListener;
import com.lucera2.scripts.altrecbots.model.listeners.PlayerListeners;
import com.lucera2.scripts.altrecbots.utils.BotUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import l2.commons.lang.reference.HardReference;
import l2.commons.listener.Listener;
import l2.gameserver.ai.PlayerAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;

public final class ActionRecordingContext
extends BaseContext<Player> {
    private static final ContextHolder<ActionRecordingContext> contexts = new ContextHolder();
    private static final long MIN_DURATION = 333L;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final int objId;
    private final Map<Integer, SubClass> subclassesMap = new HashMap<Integer, SubClass>();
    private final List<Action> actions = new CopyOnWriteArrayList<Action>();
    private final Map<Integer, Skill> skills = new HashMap<Integer, Skill>();
    private final Map<Integer, Integer> equipedItems = new HashMap<Integer, Integer>();
    private final Map<ItemTemplate, Pair<Integer, Long>> inventory = new HashMap<ItemTemplate, Pair<Integer, Long>>();
    private final Map<Integer, Long> exps = new HashMap<Integer, Long>();
    private Set<Integer> subclasses = new HashSet<Integer>();
    private Long prevTimestamp;
    private Integer initialClassId;
    private Location initialLoc;
    private Optional<Integer> activeClassId = Optional.empty();

    private ActionRecordingContext(HardReference<Player> hardReference) {
        super(hardReference);
        this.objId = ((Player)hardReference.get()).getObjectId();
    }

    public static Optional<ActionRecordingContext> getRecordingContext(Player player) {
        return contexts.getContext(player);
    }

    public static ActionRecordingContext openContext(Player player) {
        return contexts.addContext(player, new ActionRecordingContext((HardReference<Player>)player.getRef())).onStart();
    }

    private static void addListeners(Player player) {
        player.getListeners().add((Listener)MoveListener.getInstance());
        player.getListeners().add((Listener)PlayerListeners.getInstance());
        player.getInventory().getListeners().add((Listener)InventoryListener.getInstance());
    }

    private static void removeListeners(Player player) {
        player.getInventory().getListeners().remove((Listener)InventoryListener.getInstance());
        player.getListeners().remove((Listener)PlayerListeners.getInstance());
        player.getListeners().remove((Listener)MoveListener.getInstance());
    }

    private boolean isClosed() {
        return this.isClosed.get();
    }

    @Override
    public Optional<Player> getPlayer() {
        if (this.isClosed()) {
            return Optional.empty();
        }
        return super.getPlayer();
    }

    protected ActionRecordingContext onStart() {
        Optional<Player> optional = this.getPlayer();
        if (!optional.isPresent()) {
            return null;
        }
        Player player = optional.get();
        this.initialLoc = player.getLoc().clone();
        this.prevTimestamp = System.currentTimeMillis();
        for (Map.Entry entry : player.getSubClasses().entrySet()) {
            this.subclasses.add((Integer)entry.getKey());
            this.subclassesMap.put((Integer)entry.getKey(), (SubClass)entry.getValue());
            this.exps.put((Integer)entry.getKey(), ((SubClass)entry.getValue()).getExp());
        }
        this.initialClassId = player.getActiveClassId();
        this.activeClassId = Optional.of(player.getActiveClassId());
        for (ItemInstance itemInstance : player.getInventory().getPaperdollItems()) {
            if (itemInstance == null) continue;
            this.equipedItems.put(itemInstance.getItemId(), itemInstance.getEnchantLevel());
            this.inventory.put(itemInstance.getTemplate(), Pair.of(itemInstance.getEnchantLevel(), itemInstance.getCount()));
        }
        ActionRecordingContext.addListeners(player);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close(Player player) {
        Player player2;
        block9: {
            block8: {
                if (!this.isClosed.compareAndSet(false, true)) {
                    return;
                }
                player2 = null;
                try {
                    Optional<Player> optional = this.getPlayer();
                    player2 = optional.orElse(player);
                    if (player2 != null) {
                        this.onClose(player2);
                        ActionRecordingContext.removeListeners(player2);
                    }
                    if (player2 == null) break block8;
                    contexts.removeContext(player2);
                } catch (Throwable throwable) {
                    if (player2 != null) {
                        contexts.removeContext(player2);
                    } else {
                        contexts.removeContext(this.objId);
                    }
                    if (player2 != null && !player2.isLogoutStarted() && player2.isConnected() && Config.AUTO_RECORD_PLAYER_ACTIONS && Config.AUTO_RECORD_INSTANT_NEW_SEQUENCE && BotUtils.testRecordingCondition(player2)) {
                        ActionRecordingContext.openContext(player2);
                    }
                    throw throwable;
                }
                break block9;
            }
            contexts.removeContext(this.objId);
        }
        if (player2 != null && !player2.isLogoutStarted() && player2.isConnected() && Config.AUTO_RECORD_PLAYER_ACTIONS && Config.AUTO_RECORD_INSTANT_NEW_SEQUENCE && BotUtils.testRecordingCondition(player2)) {
            ActionRecordingContext.openContext(player2);
        }
    }

    protected void onClose(Player player) {
        if (this.initialClassId == null || player == null) {
            return;
        }
        ActionRecord actionRecord = new ActionRecord(player.getFace(), player.getHairStyle(), player.getHairColor(), player.getSex(), this.initialLoc, player.isNoble(), new ArrayList<ActionRecord.SubclassRecord>(), new ArrayList<ActionRecord.SkillRecord>(), new ArrayList<ActionRecord.ItemRecord>());
        for (SubClass object : this.subclassesMap.values()) {
            long l = this.exps.getOrDefault(object.getClassId(), object.getExp());
            actionRecord.getSubclasses().add(new ActionRecord.SubclassRecord(object.getClassId(), l, this.initialClassId.equals(object.getClassId()), object.isBase()));
        }
        for (Map.Entry entry : this.inventory.entrySet()) {
            actionRecord.getItems().add(new ActionRecord.ItemRecord(((ItemTemplate)entry.getKey()).getItemId(), (Long)((Pair)entry.getValue()).getRight(), (Integer)((Pair)entry.getValue()).getLeft(), this.equipedItems.containsKey(((ItemTemplate)entry.getKey()).getItemId())));
        }
        for (Skill skill : this.skills.values()) {
            actionRecord.getSkills().add(new ActionRecord.SkillRecord(skill.getId(), skill.getLevel()));
        }
        ArrayList<Action> arrayList = new ArrayList<Action>(this.actions);
        actionRecord.setActions((List<Action>)arrayList);
        if (arrayList.size() < Config.RECORD_MIN_LENGTH || arrayList.size() > Config.RECORD_MAX_LENGTH) {
            return;
        }
        long l = arrayList.stream().mapToLong(action -> action.getDuration()).sum();
        if (l > Config.RECORD_MAX_DURATION || l < Config.RECORD_MIN_DURATION) {
            return;
        }
        ActionsStorageManager.getInstance().storeRecord(actionRecord);
    }

    protected void addAction(Action<?> action2, Player player) {
        long l = System.currentTimeMillis();
        long l2 = Math.max(l - this.prevTimestamp, 333L);
        if (this.actions.size() >= Config.RECORD_MAX_LENGTH || this.actions.stream().mapToLong(action -> action.getDuration()).sum() + l2 > Config.RECORD_MAX_DURATION) {
            this.close(player);
            return;
        }
        this.actions.add((Action)action2.setDuration(l2));
        this.prevTimestamp = l;
    }

    public void onMove(Location location) {
        Optional<Player> optional = this.getPlayer();
        if (!optional.isPresent()) {
            return;
        }
        Player player = optional.get();
        if (!BotUtils.testRecordingCondition(player)) {
            this.close(player);
            return;
        }
        Location location2 = player.getLoc();
        if (player.isTeleporting() || player.isLogoutStarted() || player.isCastingNow()) {
            return;
        }
        MoveToLocation moveToLocation = new MoveToLocation(location, 0, true).setFromLocation(location2);
        GameObject gameObject = player.getTarget();
        if (gameObject != null && gameObject.isNpc()) {
            moveToLocation = moveToLocation.setTargetNpc(((NpcInstance)gameObject).getNpcId());
        }
        this.addAction(moveToLocation, player);
    }

    public void onEquip(ItemInstance itemInstance, Player player) {
        if (this.isClosed() || player == null) {
            return;
        }
        if (player.isTeleporting() || player.isLogoutStarted() || player.isCastingNow()) {
            return;
        }
        EquipItem equipItem = new EquipItem(itemInstance.getItemId());
        if (itemInstance.getEnchantLevel() > 0) {
            equipItem.setEnchant(itemInstance.getEnchantLevel());
        }
        this.inventory.put(itemInstance.getTemplate(), Pair.of(itemInstance.getEnchantLevel(), itemInstance.getCount()));
        this.addAction(equipItem, player);
    }

    public void onUnequip(int n, Player player) {
        if (this.isClosed()) {
            return;
        }
        UnEquipItem unEquipItem = new UnEquipItem(n);
        this.addAction(unEquipItem, player);
    }

    public void onTeleported(Location location, Player player) {
        Optional<Player> optional = this.getPlayer();
        if (!optional.isPresent()) {
            return;
        }
        TeleportTo teleportTo = new TeleportTo(location.clone());
        this.addAction(teleportTo, player);
    }

    public void onSkillCast(Player player, Skill skill, Creature creature) {
        Optional<Player> optional = this.getPlayer();
        if (!optional.isPresent() || player == null) {
            return;
        }
        if (player.isTeleporting() || player.isLogoutStarted() || player.isMoving()) {
            return;
        }
        SkillCast skillCast = new SkillCast(skill.getId());
        if (creature != null && creature.isNpc()) {
            Pair<Skill, Boolean> pair;
            PlayerAI playerAI = player.getAI();
            skillCast.setTargetNpcId(creature.getNpcId()).setTargetLoc(creature.getLoc());
            Optional<Pair<Skill, Boolean>> optional2 = BotUtils.getSkillAndForceUseFromPlayableAI(playerAI);
            if (optional2.isPresent() && (pair = optional2.get()).getLeft() != null && pair.getLeft().getId() == skill.getId() && pair.getRight() != null) {
                skillCast = skillCast.setForceUse(pair.getRight());
            }
        }
        this.skills.put(skill.getId(), skill);
        this.addAction(skillCast, player);
    }

    public void onAttack(Player player, Creature creature) {
        Optional<Player> optional = this.getPlayer();
        if (!optional.isPresent() || player == null) {
            return;
        }
        if (player.isTeleporting() || player.isLogoutStarted() || player.isCastingNow()) {
            return;
        }
        if (creature == null || !creature.isNpc()) {
            return;
        }
        Attack attack = new Attack(creature.getNpcId(), creature.getLoc());
        this.addAction(attack, player);
    }

    public void onSetClass(Player player, int n) {
        Optional<ClassId> optional;
        GameObject gameObject2;
        Optional<Player> optional2 = this.getPlayer();
        if (!optional2.isPresent()) {
            return;
        }
        Player player2 = optional2.get();
        HashSet<Integer> hashSet = new HashSet<Integer>();
        for (Map.Entry object2 : player2.getSubClasses().entrySet()) {
            hashSet.add((Integer)object2.getKey());
            this.subclassesMap.put((Integer)object2.getKey(), (SubClass)object2.getValue());
        }
        Object object3 = new Subclass();
        HashSet<Integer> hashSet2 = new HashSet<Integer>();
        for (Integer n2 : hashSet) {
            if (this.subclasses.contains(n2)) continue;
            hashSet2.add(n2);
        }
        HashSet hashSet3 = new HashSet();
        for (GameObject gameObject2 : this.subclasses) {
            if (hashSet.contains(gameObject2)) continue;
            hashSet3.add(gameObject2);
        }
        if (hashSet3.isEmpty() && !hashSet2.isEmpty()) {
            Optional<ClassId> optional3 = BotUtils.toClassId((Integer)hashSet2.stream().findFirst().get());
            if (optional3.isPresent()) {
                object3 = ((Subclass)object3).setSubclassActionType(Subclass.SubclassActionType.AddNew).setClassId(optional3.get());
                gameObject2 = player2.getTarget();
                if (gameObject2 != null && gameObject2.isNpc()) {
                    object3 = ((Subclass)object3).setTargetNpcId(((NpcInstance)gameObject2).getNpcId()).setLocation(gameObject2.getLoc());
                }
                this.subclasses = hashSet;
                this.addAction((Action<?>)object3, player);
                return;
            }
        } else if (!hashSet3.isEmpty() && !hashSet2.isEmpty()) {
            Optional<ClassId> optional4 = BotUtils.toClassId((Integer)hashSet2.stream().findFirst().get());
            gameObject2 = BotUtils.toClassId((Integer)hashSet3.stream().findFirst().get());
            if (gameObject2.isPresent() && optional4.isPresent()) {
                object3 = ((Subclass)object3).setSubclassActionType(Subclass.SubclassActionType.Replace).setClassId((ClassId)gameObject2.get()).setNewClassId(optional4.get());
                GameObject gameObject3 = player2.getTarget();
                if (gameObject3 != null && gameObject3.isNpc()) {
                    object3 = ((Subclass)object3).setTargetNpcId(((NpcInstance)gameObject3).getNpcId()).setLocation(gameObject3.getLoc());
                }
                this.addAction((Action<?>)object3, player);
                this.subclasses = hashSet;
                return;
            }
        }
        if (this.activeClassId.isPresent() && !Objects.equals(this.activeClassId.get(), player2.getActiveClassId())) {
            Optional<ClassId> optional5 = BotUtils.toClassId(player2.getActiveClassId());
            if (optional5.isPresent()) {
                object3 = ((Subclass)object3).setSubclassActionType(Subclass.SubclassActionType.Change).setClassId(optional5.get());
                gameObject2 = player2.getTarget();
                if (gameObject2 != null && gameObject2.isNpc()) {
                    object3 = ((Subclass)object3).setTargetNpcId(((NpcInstance)gameObject2).getNpcId()).setLocation(gameObject2.getLoc());
                }
                this.addAction((Action<?>)object3, player);
                this.activeClassId = Optional.of(player2.getActiveClassId());
            }
            return;
        }
        if (this.subclasses.contains(n) && (optional = BotUtils.toClassId(n)).isPresent()) {
            object3 = ((Subclass)object3).setSubclassActionType(Subclass.SubclassActionType.SetNew).setClassId(optional.get());
            gameObject2 = player2.getTarget();
            if (gameObject2 != null && gameObject2.isNpc()) {
                object3 = ((Subclass)object3).setTargetNpcId(((NpcInstance)gameObject2).getNpcId()).setLocation(gameObject2.getLoc());
            }
            this.addAction((Action<?>)object3, player);
        }
    }
}

