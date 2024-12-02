package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.CreeperSwellStoppableGoal;

public class CreeperPatch extends MobPatch<CreeperEntity> {
	public CreeperPatch() {
		super(Faction.NEUTRAL);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(1.0D);
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		for (PrioritizedGoal wrappedGoal : this.original.goalSelector.availableGoals) {
			Goal goal = wrappedGoal.getGoal();
			
			if (goal instanceof CreeperSwellGoal) {
				toRemove.add(goal);
			}
		}
	}
	
	@Override
	protected void initAI() {
		super.initAI();
        this.original.goalSelector.addGoal(2, new CreeperSwellStoppableGoal(this, this.original));
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.CREEPER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.CREEPER_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.CREEPER_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);

		if (this.isLogicalClient()) {
			float f = this.original.getSwelling(partialTicks);
			float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
	        f = MathHelper.clamp(f, 0.0F, 1.0F);
	        f = f * f;
	        f = f * f;
	        float f2 = (1.0F + f * 0.4F) * f1;
	        float f3 = (1.0F + f * 0.1F) / f1;
	        
			OpenMatrix4f.scale(new Vec3f(f2, f3, f2), mat, mat);
		}
		
		return mat;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (stunType == StunType.LONG) {
			return Animations.CREEPER_HIT_LONG;
		} else {
			return Animations.CREEPER_HIT_SHORT;
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.creeper;
	}
}