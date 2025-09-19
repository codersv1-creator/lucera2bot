/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.lucera2.scripts.altrecbots.model.actions;

import com.lucera2.scripts.altrecbots.model.actions.Action;
import com.lucera2.scripts.altrecbots.model.actions.Attack;
import com.lucera2.scripts.altrecbots.model.actions.EquipItem;
import com.lucera2.scripts.altrecbots.model.actions.GainExp;
import com.lucera2.scripts.altrecbots.model.actions.ItemSetEnchant;
import com.lucera2.scripts.altrecbots.model.actions.MoveToLocation;
import com.lucera2.scripts.altrecbots.model.actions.SkillCast;
import com.lucera2.scripts.altrecbots.model.actions.Subclass;
import com.lucera2.scripts.altrecbots.model.actions.TeleportTo;
import com.lucera2.scripts.altrecbots.model.actions.UnEquipItem;
import com.lucera2.scripts.altrecbots.model.actions.UseItem;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ActionType {
    MOVE_TO_LOCATION(1, MoveToLocation.class),
    EQUIP_ITEM(2, EquipItem.class),
    UNEQUIP_SLOT(3, UnEquipItem.class),
    ITEM_SET_ENCHANT(4, ItemSetEnchant.class),
    ATTACK(5, Attack.class),
    SKILL_CAST(6, SkillCast.class),
    SUBCLASS(7, Subclass.class),
    GAIN_EXP(8, GainExp.class),
    TELEPORT_TO(9, TeleportTo.class),
    USE_ITEM(10, UseItem.class);

    private static final Map<Integer, ActionType> ACTION_TYPE_LEGACY_ORDS;
    private final int legacyOrd;
    private final Class<? extends Action> actionClazz;

    private ActionType(int n2, Class<? extends Action> clazz) {
        this.legacyOrd = n2;
        this.actionClazz = clazz;
    }

    public static ActionType getActionTypeByLegacyOrd(int n) {
        return ACTION_TYPE_LEGACY_ORDS.get(n);
    }

    public int getLegacyOrd() {
        return this.legacyOrd;
    }

    public Class<? extends Action> getActionClass() {
        return this.actionClazz;
    }

    public <T extends Action> T newActionInstance() {
        try {
            Action action = this.actionClazz.newInstance();
            return (T)action;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    static {
        ACTION_TYPE_LEGACY_ORDS = Stream.of(ActionType.values()).collect(Collectors.toMap(ActionType::getLegacyOrd, Function.identity()));
    }
}

