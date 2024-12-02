package yesman.epicfight.network.server;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SPPlayAnimationInstant extends SPPlayAnimation {
	public SPPlayAnimationInstant(int namespaceId, int animation, int entityId, float convertTimeModifier) {
		super(namespaceId, animation, entityId, convertTimeModifier);
	}
	
	public SPPlayAnimationInstant(StaticAnimation animation, float convertTimeModifier, LivingEntityPatch<?> entitypatch) {
		this(animation.getNamespaceId(), animation.getId(), entitypatch.getOriginal().getId(), convertTimeModifier);
	}
	
	public static SPPlayAnimationInstant fromBytes(PacketBuffer buf) {
		return new SPPlayAnimationInstant(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat());
	}
	
	@Override
	public void onArrive() {
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.player.level.getEntity(this.entityId);
		
		if (entity == null) {
			return;
		}
		
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		entitypatch.getAnimator().playAnimationInstantly(this.namespaceId, this.animationId);
		entitypatch.getAnimator().poseTick();
		entitypatch.getAnimator().poseTick();
	}
}