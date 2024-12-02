package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;

public class VexPatch extends MobPatch<VexEntity> {
	public VexPatch() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		
        this.original.goalSelector.addGoal(0, new ChargeAttackGoal());
        this.original.goalSelector.addGoal(1, new StopStandGoal());
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		super.selectGoalToRemove(toRemove);
		
		Iterator<PrioritizedGoal> iterator = this.original.goalSelector.availableGoals.iterator();
		
		int index = 0;
		while (iterator.hasNext()) {
			PrioritizedGoal goal = iterator.next();
			Goal inner = goal.getGoal();
			
			if (index == 1) {
				toRemove.add(inner);
				break;
			}
			
			index++;
        }
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.VEX_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.VEX_DEATH);
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.VEX_FLIPPING);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.original.getHealth() <= 0.0F) {
			currentLivingMotion = LivingMotions.DEATH;
		} else if (this.state.inaction() && considerInaction) {
			currentLivingMotion = LivingMotions.IDLE;
		} else {
			currentLivingMotion = LivingMotions.IDLE;
			currentCompositeMotion = LivingMotions.IDLE;
		}
	}
	
	@Override
	public void onAttackBlocked(HurtEvent.Pre hurtEvent, LivingEntityPatch<?> opponent) {
		Vector3d vec3d = opponent.getOriginal().getEyePosition(1.0F).add(opponent.getOriginal().getLookAngle());
		
		this.original.setPos(vec3d.x, vec3d.y, vec3d.z);
		this.playAnimationSynchronized(Animations.VEX_NEUTRALIZED, 0.0F);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.vex;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return Animations.VEX_HIT;
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return super.getModelMatrix(partialTicks).scale(0.4F, 0.4F, 0.4F);
	}
	
	class StopStandGoal extends Goal {
		public StopStandGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return VexPatch.this.getEntityState().inaction();
		}

		@Override
		public void start() {
			VexPatch.this.original.getMoveControl().setWantedPosition(VexPatch.this.original.getX(), VexPatch.this.original.getY(), VexPatch.this.original.getZ(), 0.25F);
		}
	}
	
	class ChargeAttackGoal extends Goal {
		private int chargingCounter;
		
		public ChargeAttackGoal() {
			this.setFlags(EnumSet.of(Flag.MOVE));
		}
		
		@Override
		public boolean canUse() {
			if (VexPatch.this.original.getTarget() != null && !VexPatch.this.getEntityState().inaction() && VexPatch.this.original.getRandom().nextInt(10) == 0) {
				double distance = VexPatch.this.original.distanceToSqr(VexPatch.this.original.getTarget());
				return distance < 50.0D;
			} else {
				return false;
			}
		}
	    
		@Override
		public boolean canContinueToUse() {
			return this.chargingCounter > 0;
		}
		
		@Override
		public void start() {
			VexPatch.this.original.getMoveControl().setWantedPosition(VexPatch.this.original.getX(), VexPatch.this.original.getY(), VexPatch.this.original.getZ(), 0.25F);
	    	VexPatch.this.playAnimationSynchronized(Animations.VEX_CHARGE, 0.0F);
	    	VexPatch.this.original.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
	    	VexPatch.this.original.setIsCharging(true);
	    	this.chargingCounter = 20;
	    }
	    
		@Override
		public void stop() {
			VexPatch.this.original.setIsCharging(false);
		}
		
		@Override
		public void tick() {
			--this.chargingCounter;
		}
	}
}