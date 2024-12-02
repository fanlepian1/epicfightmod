package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.WitherAuraLayer;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedWitherArmorLayer extends PatchedLayer<WitherEntity, WitherPatch, WitherModel<WitherEntity>, WitherAuraLayer> {
	private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
	
	@Override
	public void renderLayer(WitherPatch entitypatch, WitherEntity entityliving, WitherAuraLayer originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		if (entitypatch.isArmorActivated()) {
			float progress = (float)entityliving.tickCount + partialTicks;
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.0D, -0.1D, 0.0D);
			matrixStackIn.scale(1.05F, 1.05F, 1.05F);
			int transparencyCount = entitypatch.getTransparency();
			float transparency = 1.0F;
			
			if (transparencyCount == 0) {
				transparency = entitypatch.isGhost() ? 0.0F : 1.0F;
				AnimationPlayer animationPlayer = entitypatch.getAnimator().getPlayerFor(null);
				
				if (animationPlayer.getAnimation() == Animations.WITHER_SPELL_ARMOR) {
					transparency = (animationPlayer.getPrevElapsedTime() + (animationPlayer.getElapsedTime() - animationPlayer.getPrevElapsedTime()) * partialTicks) / (Animations.WITHER_SPELL_ARMOR.getTotalTime() - 0.5F);
				}
			} else {
				if (transparencyCount < 0) {
					transparency = 1.0F - (Math.abs(transparencyCount) + partialTicks) / 41.0F;
				} else if (transparencyCount > 0) {
					transparency = (Math.abs(transparencyCount) + partialTicks) / 41.0F;
				}
			}
			
			IVertexBuilder ivertexbuilder = buffer.getBuffer(EpicFightRenderTypes.energySwirlTrianlges(WITHER_ARMOR_LOCATION, MathHelper.cos(progress * 0.02F) * 3.0F % 1.0F, progress * 0.01F % 1.0F));
			entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).drawAnimatedModel(matrixStackIn, ivertexbuilder, packedLightIn, transparency * 0.5F, transparency * 0.5F, transparency * 0.5F, 1.0F, OverlayTexture.NO_OVERLAY, poses);
			matrixStackIn.popPose();
		}
	}
}