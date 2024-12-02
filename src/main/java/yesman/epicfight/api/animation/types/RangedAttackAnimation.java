package yesman.epicfight.api.animation.types;

import net.minecraft.entity.IRangedAttackMob;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RangedAttackAnimation extends AttackAnimation {
	public RangedAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
	}
	
	@Override
	public void hurtCollidingEntities(LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase) {
		if (state.attacking() && entitypatch.getTarget() != null && (entitypatch.getOriginal() instanceof IRangedAttackMob)) {
			((IRangedAttackMob)entitypatch.getOriginal()).performRangedAttack(entitypatch.getTarget(), elapsedTime);
		}
	}
}