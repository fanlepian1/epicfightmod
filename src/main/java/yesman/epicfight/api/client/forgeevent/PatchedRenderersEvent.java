package yesman.epicfight.api.client.forgeevent;

import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("rawtypes")
public abstract class PatchedRenderersEvent extends Event implements IModBusEvent {
	public static class Add extends PatchedRenderersEvent {
		private Map<EntityType<?>, Supplier<PatchedEntityRenderer>> entityRendererProvider;
		private Map<Item, RenderItemBase> itemRenerers;
		
		public Add(Map<EntityType<?>, Supplier<PatchedEntityRenderer>> entityRendererProvider, Map<Item, RenderItemBase> itemRenerers) {
			this.entityRendererProvider = entityRendererProvider;
			this.itemRenerers = itemRenerers;
		}
		
		public void addPatchedEntityRenderer(EntityType<?> entityType, Supplier<PatchedEntityRenderer> provider) {
			this.entityRendererProvider.put(entityType, provider);
		}
		
		public void addItemRenderer(Item item, RenderItemBase renderer) {
			this.itemRenerers.put(item, renderer);
		}
	}
	
	public static class Modify extends PatchedRenderersEvent {
		private Map<EntityType<?>, PatchedEntityRenderer> renderers;
		
		public Modify(Map<EntityType<?>, PatchedEntityRenderer> renderers) {
			this.renderers = renderers;
		}
		
		public PatchedEntityRenderer get(EntityType<?> entityType) {
			return this.renderers.get(entityType);
		}
	}
}