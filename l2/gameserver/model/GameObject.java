package l2.gameserver.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.EventOwner;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.network.l2.s2c.DeleteObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.scripts.Events;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameObject extends EventOwner {
   private static final Logger _log = LoggerFactory.getLogger(GameObject.class);
   public static final GameObject[] EMPTY_L2OBJECT_ARRAY = new GameObject[0];
   protected static final int CREATED = 0;
   protected static final int VISIBLE = 1;
   protected static final int DELETED = -1;
   protected int objectId;
   private int _x;
   private int _y;
   private int _z;
   protected Reflection _reflection;
   private WorldRegion _currentRegion;
   private final AtomicInteger _state;
   private boolean captureCastleEvent;
   private boolean captureCastleEventOwner;

   protected GameObject() {
      this._reflection = ReflectionManager.DEFAULT;
      this._state = new AtomicInteger(0);
      this.captureCastleEvent = false;
      this.captureCastleEventOwner = false;
   }

   public GameObject(int objectId) {
      this._reflection = ReflectionManager.DEFAULT;
      this._state = new AtomicInteger(0);
      this.captureCastleEvent = false;
      this.captureCastleEventOwner = false;
      this.objectId = objectId;
   }

   public HardReference<? extends GameObject> getRef() {
      return HardReferences.emptyRef();
   }

   private void clearRef() {
      HardReference<? extends GameObject> reference = this.getRef();
      if (reference != null) {
         reference.clear();
      }

   }

   public Reflection getReflection() {
      return this._reflection;
   }

   public int getReflectionId() {
      return this._reflection.getId();
   }

   public int getGeoIndex() {
      return this._reflection.getGeoIndex();
   }

   public void setReflection(Reflection reflection) {
      if (this._reflection != reflection) {
         boolean respawn = false;
         if (this.isVisible()) {
            this.decayMe();
            respawn = true;
         }

         Reflection r = this.getReflection();
         if (!r.isDefault()) {
            r.removeObject(this);
         }

         this._reflection = reflection;
         if (!reflection.isDefault()) {
            reflection.addObject(this);
         }

         if (respawn) {
            this.spawnMe();
         }

      }
   }

   public void setReflection(int reflectionId) {
      Reflection r = ReflectionManager.getInstance().get(reflectionId);
      if (r == null) {
         Log.debug("Trying to set unavailable reflection: " + reflectionId + " for object: " + this + "!", (new Throwable()).fillInStackTrace());
      } else {
         this.setReflection(r);
      }
   }

   public final int hashCode() {
      return this.objectId;
   }

   public final int getObjectId() {
      return this.objectId;
   }

   public int getX() {
      return this._x;
   }

   public int getY() {
      return this._y;
   }

   public int getZ() {
      return this._z;
   }

   public Location getLoc() {
      return new Location(this._x, this._y, this._z, this.getHeading());
   }

   public int getGeoZ(Location loc) {
      return GeoEngine.getHeight(loc, this.getGeoIndex());
   }

   public void setLoc(Location loc) {
      this.setXYZ(loc.x, loc.y, loc.z);
   }

   public void setXYZ(int x, int y, int z) {
      this._x = World.validCoordX(x);
      this._y = World.validCoordY(y);
      this._z = World.validCoordZ(z);
      World.addVisibleObject(this, (Creature)null);
   }

   public final boolean isVisible() {
      return this._state.get() == 1;
   }

   public InvisibleType getInvisibleType() {
      return InvisibleType.NONE;
   }

   public final boolean isInvisible() {
      return this.getInvisibleType() != InvisibleType.NONE;
   }

   public void spawnMe(Location loc) {
      this.spawnMe0(loc, (Creature)null);
   }

   protected void spawnMe0(Location loc, Creature dropper) {
      this._x = loc.x;
      this._y = loc.y;
      this._z = this.getGeoZ(loc);
      this.spawn0(dropper);
   }

   public final void spawnMe() {
      this.spawn0((Creature)null);
   }

   protected void spawn0(Creature dropper) {
      if (this._state.compareAndSet(0, 1)) {
         World.addVisibleObject(this, dropper);
         this.onSpawn();
      }
   }

   public void toggleVisible() {
      if (this.isVisible()) {
         this.decayMe();
      } else {
         this.spawnMe();
      }

   }

   protected void onSpawn() {
   }

   public final void decayMe() {
      if (this._state.compareAndSet(1, 0)) {
         World.removeVisibleObject(this);
         this.onDespawn();
      }
   }

   protected void onDespawn() {
   }

   public final void deleteMe() {
      this.decayMe();
      if (this._state.compareAndSet(0, -1)) {
         this.onDelete();
      }
   }

   public final boolean isDeleted() {
      return this._state.get() == -1;
   }

   protected void onDelete() {
      Reflection r = this.getReflection();
      if (!r.isDefault()) {
         r.removeObject(this);
      }

      this.clearRef();
   }

   public void onAction(Player player, boolean shift) {
      if (!Events.onAction(player, this, shift)) {
         player.sendActionFailed();
      }
   }

   public int getActingRange() {
      return -1;
   }

   public void onForcedAttack(Player player, boolean shift) {
      player.sendActionFailed();
   }

   public boolean isAttackable(Creature attacker) {
      return false;
   }

   public String getL2ClassShortName() {
      return this.getClass().getSimpleName();
   }

   public final long getXYDeltaSq(int x, int y) {
      long dx = (long)(x - this.getX());
      long dy = (long)(y - this.getY());
      return dx * dx + dy * dy;
   }

   public final long getXYDeltaSq(Location loc) {
      return this.getXYDeltaSq(loc.x, loc.y);
   }

   public final long getZDeltaSq(int z) {
      long dz = (long)(z - this.getZ());
      return dz * dz;
   }

   public final long getZDeltaSq(Location loc) {
      return this.getZDeltaSq(loc.z);
   }

   public final long getXYZDeltaSq(int x, int y, int z) {
      return this.getXYDeltaSq(x, y) + this.getZDeltaSq(z);
   }

   public final long getXYZDeltaSq(Location loc) {
      return this.getXYDeltaSq(loc.x, loc.y) + this.getZDeltaSq(loc.z);
   }

   public final double getDistance(int x, int y) {
      return Math.sqrt((double)this.getXYDeltaSq(x, y));
   }

   public final double getDistance(int x, int y, int z) {
      return Math.sqrt((double)this.getXYZDeltaSq(x, y, z));
   }

   public final double getDistance(Location loc) {
      return this.getDistance(loc.x, loc.y, loc.z);
   }

   public final boolean isInRange(GameObject obj, long range) {
      if (obj == null) {
         return false;
      } else if (obj.getReflection() != this.getReflection()) {
         return false;
      } else {
         long dx = (long)Math.abs(obj.getX() - this.getX());
         if (dx > range) {
            return false;
         } else {
            long dy = (long)Math.abs(obj.getY() - this.getY());
            if (dy > range) {
               return false;
            } else {
               long dz = (long)Math.abs(obj.getZ() - this.getZ());
               return dz <= 1500L && dx * dx + dy * dy <= range * range;
            }
         }
      }
   }

   public final boolean isInActingRange(GameObject obj) {
      return this.isInRange(obj, (long)this.getActingRange());
   }

   public final boolean isInRangeZ(GameObject obj, long range) {
      if (obj == null) {
         return false;
      } else if (obj.getReflection() != this.getReflection()) {
         return false;
      } else {
         long dx = (long)Math.abs(obj.getX() - this.getX());
         if (dx > range) {
            return false;
         } else {
            long dy = (long)Math.abs(obj.getY() - this.getY());
            if (dy > range) {
               return false;
            } else {
               long dz = (long)Math.abs(obj.getZ() - this.getZ());
               return dz <= range && dx * dx + dy * dy + dz * dz <= range * range;
            }
         }
      }
   }

   public final boolean isInRange(Location loc, long range) {
      return this.isInRangeSq(loc, range * range);
   }

   public final boolean isInRangeSq(Location loc, long range) {
      return this.getXYDeltaSq(loc) <= range;
   }

   public final boolean isInRangeZ(Location loc, long range) {
      return this.isInRangeZSq(loc, range * range);
   }

   public final boolean isInRangeZSq(Location loc, long range) {
      return this.getXYZDeltaSq(loc) <= range;
   }

   public final double getDistance(GameObject obj) {
      return obj == null ? 0.0D : Math.sqrt((double)this.getXYDeltaSq(obj.getX(), obj.getY()));
   }

   public final double getDistance3D(GameObject obj) {
      return obj == null ? 0.0D : Math.sqrt((double)this.getXYZDeltaSq(obj.getX(), obj.getY(), obj.getZ()));
   }

   public final double getRealDistance(GameObject obj) {
      return this.getRealDistance3D(obj, true);
   }

   public final double getRealDistance3D(GameObject obj) {
      return this.getRealDistance3D(obj, false);
   }

   public final double getRealDistance3D(GameObject obj, boolean ignoreZ) {
      double distance = ignoreZ ? this.getDistance(obj) : this.getDistance3D(obj);
      if (this.isCreature()) {
         distance -= ((Creature)this).getTemplate().collisionRadius;
      }

      if (obj.isCreature()) {
         distance -= ((Creature)obj).getTemplate().collisionRadius;
      }

      return distance > 0.0D ? distance : 0.0D;
   }

   public final long getSqDistance(int x, int y) {
      return this.getXYDeltaSq(x, y);
   }

   public final long getSqDistance(GameObject obj) {
      return obj == null ? 0L : this.getXYDeltaSq(obj.getLoc());
   }

   public Player getPlayer() {
      return null;
   }

   public int getHeading() {
      return 0;
   }

   public int getMoveSpeed() {
      return 0;
   }

   public WorldRegion getCurrentRegion() {
      return this._currentRegion;
   }

   public void setCurrentRegion(WorldRegion region) {
      this._currentRegion = region;
   }

   public boolean isInObserverMode() {
      return false;
   }

   public boolean isOlyParticipant() {
      return false;
   }

   public boolean isInBoat() {
      return false;
   }

   public boolean isFlying() {
      return false;
   }

   public double getColRadius() {
      _log.warn("getColRadius called directly from L2Object");
      Thread.dumpStack();
      return 0.0D;
   }

   public double getColHeight() {
      _log.warn("getColHeight called directly from L2Object");
      Thread.dumpStack();
      return 0.0D;
   }

   public boolean isCreature() {
      return false;
   }

   public boolean isPlayable() {
      return false;
   }

   public boolean isPlayer() {
      return false;
   }

   public boolean isPet() {
      return false;
   }

   public boolean isSummon() {
      return false;
   }

   public boolean isNpc() {
      return false;
   }

   public boolean isMonster() {
      return false;
   }

   public boolean isItem() {
      return false;
   }

   public boolean isRaid() {
      return false;
   }

   public boolean isBoss() {
      return false;
   }

   public boolean isTrap() {
      return false;
   }

   public boolean isDoor() {
      return false;
   }

   public boolean isArtefact() {
      return false;
   }

   public boolean isSiegeGuard() {
      return false;
   }

   public boolean isBoat() {
      return false;
   }

   public boolean isVehicle() {
      return false;
   }

   public boolean isMinion() {
      return false;
   }

   public String getName() {
      return this.getClass().getSimpleName() + ":" + this.objectId;
   }

   public String dump() {
      return this.dump(true);
   }

   public String dump(boolean simpleTypes) {
      return Util.dumpObject(this, simpleTypes, true, true);
   }

   public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
      return Collections.emptyList();
   }

   public List<L2GameServerPacket> deletePacketList() {
      return Collections.singletonList(new DeleteObject(this));
   }

   public void addEvent(GlobalEvent event) {
      event.onAddEvent(this);
      super.addEvent(event);
   }

   public void removeEvent(GlobalEvent event) {
      event.onRemoveEvent(this);
      super.removeEvent(event);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (obj.getClass() != this.getClass()) {
         return false;
      } else {
         return ((GameObject)obj).getObjectId() == this.getObjectId();
      }
   }

   public boolean isEventCastleArtefact() {
      return false;
   }

   public boolean isInCaptureCastleEvent() {
      Player player = this.getPlayer();
      return player == null ? false : this.captureCastleEvent;
   }

   public void setIsInCaptureCastleEvent(boolean value) {
      this.captureCastleEvent = value;
   }

   public boolean isInCaptureCastleEventOwner() {
      Player player = this.getPlayer();
      return player == null ? false : this.captureCastleEventOwner;
   }

   public void setIsInCaptureCastleEventOwner(boolean value) {
      this.captureCastleEventOwner = value;
   }
}
