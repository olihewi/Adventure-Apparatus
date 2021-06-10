package com.olihewi.pickonastick.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.olihewi.pickonastick.entities.PickOnAStickEntity;
import com.olihewi.pickonastick.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class PickOnAStickRenderer extends EntityRenderer<PickOnAStickEntity>
{
  private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("pickonastick:textures/entity/pick_on_a_stick.png");
  private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);

  public PickOnAStickRenderer(EntityRendererManager rendererManager)
  {
    super(rendererManager);
  }

  public void render(PickOnAStickEntity entity, float entityYaw, float partialTick, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight) {
    LivingEntity thrower = entity.getThrower();
    if (thrower != null) {
      matrix.pushPose();
      matrix.pushPose();
      matrix.scale(0.5F, 0.5F, 0.5F);
      matrix.mulPose(this.entityRenderDispatcher.cameraOrientation());
      matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      MatrixStack.Entry matrixstack$entry = matrix.last();
      Matrix4f matrix4f = matrixstack$entry.pose();
      Matrix3f matrix3f = matrixstack$entry.normal();
      IVertexBuilder ivertexbuilder = buffer.getBuffer(RENDER_TYPE);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0);
      vertex(ivertexbuilder, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0);
      matrix.popPose();
      int i = thrower.getMainArm() == HandSide.RIGHT ? 1 : -1;
      ItemStack itemstack = thrower.getMainHandItem();
      if (itemstack.getItem() != RegistryHandler.PICK_ON_A_STICK.get()) {
        i = -i;
      }

      float f = thrower.getAttackAnim(partialTick);
      float f1 = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
      float f2 = MathHelper.lerp(partialTick, thrower.yBodyRotO, thrower.yBodyRot) * ((float)Math.PI / 180F);
      double d0 = (double)MathHelper.sin(f2);
      double d1 = (double)MathHelper.cos(f2);
      double d2 = (double)i * 0.35D;
      double d3 = 0.8D;
      double d4;
      double d5;
      double d6;
      float f3;
      if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && thrower == Minecraft.getInstance().player) {
        double d7 = this.entityRenderDispatcher.options.fov;
        d7 = d7 / 100.0D;
        Vector3d vector3d = new Vector3d((double)i * -0.36D * d7, -0.045D * d7, 0.4D);
        vector3d = vector3d.xRot(-MathHelper.lerp(partialTick, thrower.xRotO, thrower.xRot) * ((float)Math.PI / 180F));
        vector3d = vector3d.yRot(-MathHelper.lerp(partialTick, thrower.yRotO, thrower.yRot) * ((float)Math.PI / 180F));
        vector3d = vector3d.yRot(f1 * 0.5F);
        vector3d = vector3d.xRot(-f1 * 0.7F);
        d4 = MathHelper.lerp((double)partialTick, thrower.xo, thrower.getX()) + vector3d.x;
        d5 = MathHelper.lerp((double)partialTick, thrower.yo, thrower.getY()) + vector3d.y;
        d6 = MathHelper.lerp((double)partialTick, thrower.zo, thrower.getZ()) + vector3d.z;
        f3 = thrower.getEyeHeight();
      } else {
        d4 = MathHelper.lerp((double)partialTick, thrower.xo, thrower.getX()) - d1 * d2 - d0 * 0.8D;
        d5 = thrower.yo + (double)thrower.getEyeHeight() + (thrower.getY() - thrower.yo) * (double)partialTick - 0.45D;
        d6 = MathHelper.lerp((double)partialTick, thrower.zo, thrower.getZ()) - d0 * d2 + d1 * 0.8D;
        f3 = thrower.isCrouching() ? -0.1875F : 0.0F;
      }

      double d9 = MathHelper.lerp((double)partialTick, entity.xo, entity.getX());
      double d10 = MathHelper.lerp((double)partialTick, entity.yo, entity.getY()) + 0.25D;
      double d8 = MathHelper.lerp((double)partialTick, entity.zo, entity.getZ());
      float f4 = (float)(d4 - d9);
      float f5 = (float)(d5 - d10) + f3;
      float f6 = (float)(d6 - d8);
      IVertexBuilder ivertexbuilder1 = buffer.getBuffer(RenderType.lines());
      Matrix4f matrix4f1 = matrix.last().pose();
      int j = 16;

      for(int k = 0; k < 16; ++k) {
        stringVertex(f4, f5, f6, ivertexbuilder1, matrix4f1, fraction(k, 16));
        stringVertex(f4, f5, f6, ivertexbuilder1, matrix4f1, fraction(k + 1, 16));
      }

      matrix.popPose();
      super.render(entity, entityYaw, partialTick, matrix, buffer, packedLight);
    }
  }

  private static float fraction(int p_229105_0_, int p_229105_1_) {
    return (float)p_229105_0_ / (float)p_229105_1_;
  }

  private static void vertex(IVertexBuilder p_229106_0_, Matrix4f p_229106_1_, Matrix3f p_229106_2_, int p_229106_3_, float p_229106_4_, int p_229106_5_, int p_229106_6_, int p_229106_7_) {
    p_229106_0_.vertex(p_229106_1_, p_229106_4_ - 0.5F, (float)p_229106_5_ - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)p_229106_6_, (float)p_229106_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229106_3_).normal(p_229106_2_, 0.0F, 1.0F, 0.0F).endVertex();
  }

  private static void stringVertex(float p_229104_0_, float p_229104_1_, float p_229104_2_, IVertexBuilder p_229104_3_, Matrix4f p_229104_4_, float p_229104_5_) {
    p_229104_3_.vertex(p_229104_4_, p_229104_0_ * p_229104_5_, p_229104_1_ * (p_229104_5_ * p_229104_5_ + p_229104_5_) * 0.5F + 0.25F, p_229104_2_ * p_229104_5_).color(0, 0, 0, 255).endVertex();
  }
  @Override
  public ResourceLocation getTextureLocation(PickOnAStickEntity entity)
  {
    return TEXTURE_LOCATION;
  }
}
