package yesman.epicfight.api.animation.types;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.client.animation.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MirrorAnimation extends StaticAnimation {
	public StaticAnimation original;
	public StaticAnimation mirror;
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, Model model) {
		super(0.0F, false, path1, model);
		this.original = new StaticAnimation(convertTime, repeatPlay, path1, model, true);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2, model, true);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		StaticAnimation animation = this.checkHandAndReturnAnimation(entitypatch.getOriginal().getUsedItemHand());
		entitypatch.getClientAnimator().playAnimation(animation, 0.0F);
	}
	
	@Override
	public void loadAnimation(IResourceManager resourceManager) {
		load(resourceManager, this.original);
		load(resourceManager, this.mirror);
	}
	
	@Override
	public boolean isMetaAnimation() {
		return true;
	}
	
	@Override
	public <V> StaticAnimation addProperty(StaticAnimationProperty<V> propertyType, V value) {
		this.original.properties.put(propertyType, value);
		this.mirror.properties.put(propertyType, value);
		return this;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Layer.Priority getPriority() {
		return this.original.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.LOWEST);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Layer.LayerType getLayerType() {
		return this.original.getProperty(ClientAnimationProperties.LAYER_TYPE).orElse(Layer.LayerType.BASE_LAYER);
	}
	
	private StaticAnimation checkHandAndReturnAnimation(Hand hand) {
		if (hand == Hand.OFF_HAND) {
			return this.mirror;
		}
		return this.original;
	}
}