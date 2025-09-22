package npc.model;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class CaptureCastleEventInstance extends NpcInstance {
   private TeamType ownerTeam;
   private boolean inEvent;
   private static final long serialVersionUID = 9109837110671749554L;

   public CaptureCastleEventInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
      this.ownerTeam = TeamType.NONE;
      this.setHasChatWindow(false);
   }

   public TeamType getOwnerTeam() {
      return this.ownerTeam;
   }

   public void setOwnerTeam(TeamType ownerTeam) {
      this.ownerTeam = ownerTeam;
   }

   public void setIsInCaptureCastleEvent(boolean b) {
      this.inEvent = b;
   }

   public boolean isInCaptureCastleEvent() {
      return this.inEvent;
   }

   public boolean isArtefact() {
      return true;
   }

   public boolean isEventCastleArtefact() {
      return true;
   }

   public boolean isAutoAttackable(Creature attacker) {
      return false;
   }

   public boolean isAttackable(Creature attacker) {
      return attacker.getCastingSkill() != null && attacker.getCastingSkill().getSkillType() == SkillType.TAKECASTLE ? this.inEvent : false;
   }

   public boolean isFearImmune() {
      return true;
   }

   public boolean isParalyzeImmune() {
      return true;
   }

   public boolean isLethalImmune() {
      return true;
   }
}
