package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;

public class TakeCastle extends Skill {
   public TakeCastle(StatsSet set) {
      super(set);
   }

   public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
      if (!super.checkCondition(activeChar, target, forceUse, dontMove, first)) {
         return false;
      } else if (activeChar != null && activeChar.isPlayer()) {
         Player player = (Player)activeChar;
         if (target.isEventCastleArtefact() && player.isInCaptureCastleEvent()) {
            if (player.getTeam() == TeamType.NONE) {
               return false;
            } else if (player.isMounted()) {
               activeChar.sendPacket((new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
               return false;
            } else if (!player.isInRangeZ(target, 185L)) {
               player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
               return false;
            } else if (player.isInCaptureCastleEventOwner()) {
               player.sendPacket(SystemMsg.INVALID_TARGET);
               return false;
            } else {
               this.setHitTime(30000);
               return true;
            }
         } else if (player.getClan() != null && player.isClanLeader()) {
            CastleSiegeEvent siegeEvent = (CastleSiegeEvent)player.getEvent(CastleSiegeEvent.class);
            if (siegeEvent == null) {
               activeChar.sendPacket((new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
               return false;
            } else if (siegeEvent.getSiegeClan("attackers", player.getClan()) == null) {
               activeChar.sendPacket((new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
               return false;
            } else if (player.isMounted()) {
               activeChar.sendPacket((new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
               return false;
            } else if (!player.isInRangeZ(target, 185L)) {
               player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
               return false;
            } else if (((Castle)siegeEvent.getResidence()).getZone().checkIfInZone(activeChar) && ((Castle)siegeEvent.getResidence()).getZone().checkIfInZone(target)) {
               if (first) {
                  siegeEvent.broadcastTo(SystemMsg.THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT, new String[]{"defenders"});
               }

               Skill tempSkill = SkillTable.getInstance().getInfo(this.getId(), this.getLevel());
               this.setHitTime(tempSkill.getHitTime());
               return true;
            } else {
               activeChar.sendPacket((new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
               return false;
            }
         } else {
            activeChar.sendPacket((new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
            return false;
         }
      } else {
         return false;
      }
   }

   public SystemMsg checkTarget(Creature activeChar, Creature target, Creature aimingTarget, boolean forceUse, boolean first) {
      if ((target != activeChar || !this.isNotTargetAoE()) && (target != activeChar.getPet() || this._targetType != SkillTargetType.TARGET_PET_AURA)) {
         if (target != null && (!this.isOffensive() || target != activeChar)) {
            if (activeChar.getReflection() != target.getReflection()) {
               return SystemMsg.CANNOT_SEE_TARGET;
            } else {
               if (target != activeChar && target == aimingTarget && this.getCastRange() > 0 && this.getCastRange() < 32767 && !first) {
                  int minRange = (int)((double)Math.max(0, this.getEffectiveRange()) + activeChar.getMinDistance(target) + 16.0D);
                  if (!activeChar.isInRange(target.getLoc(), (long)minRange)) {
                     return SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED;
                  }
               }

               return null;
            }
         } else {
            return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
         }
      } else {
         return null;
      }
   }

   public void useSkill(Creature activeChar, List<Creature> targets) {
      Iterator var3 = targets.iterator();

      while(var3.hasNext()) {
         Creature target = (Creature)var3.next();
         if (target != null) {
            Player player;
            if (target.isEventCastleArtefact()) {
               player = (Player)activeChar;
               TeamType teamType = player.getTeam();
               Functions.callScripts("events.CaptureCastle.CaptureCastle", "onTake", new Object[]{player.getObjectId(), teamType.ordinal()});
            } else if (target.isArtefact()) {
               player = (Player)activeChar;
               CastleSiegeEvent siegeEvent = (CastleSiegeEvent)player.getEvent(CastleSiegeEvent.class);
               if (siegeEvent != null) {
                  siegeEvent.broadcastTo((new SystemMessage(SystemMsg.CLAN_S1_HAS_SUCCESSFULLY_ENGRAVED_THE_HOLY_ARTIFACT)).addString(player.getClan().getName()), new String[]{"attackers", "defenders"});
                  siegeEvent.processStep(player.getClan());
               }
            }
         }
      }

      this.getEffects(activeChar, activeChar, this.getActivateRate() > 0, false);
   }
}
