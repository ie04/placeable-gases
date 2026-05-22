package com.ie04.placeablegases.client.render;

import com.ie04.placeablegases.block.entity.GasVoxelBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GasVoxelBlockEntityRenderer implements BlockEntityRenderer<GasVoxelBlockEntity>
{
    private static final float FULL_OPACITY_AMOUNT = 10_000.0f;
    private static final float MIN_OPACITY = 0.3f;

    public GasVoxelBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
    }

    @Override
    public void render(
            GasVoxelBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        int totalGasUnits = blockEntity.getTotalGasUnits();

        if (totalGasUnits <= 0)
            return;

        int color = blockEntity.getWeightedColor();

        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = alphaForAmount(totalGasUnits);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        float min = 0.02f;
        float max = 0.98f;

        renderCube(consumer, matrix, normal, min, max, red, green, blue, alpha, packedLight);
    }

    private static float alphaForAmount(int milliBuckets)
    {
        if (milliBuckets <= 0)
            return 0.0f;

        float normalized = Math.min(milliBuckets / FULL_OPACITY_AMOUNT, 1.0f);

        return MIN_OPACITY + ((1.0f - MIN_OPACITY) * normalized);
    }

    private static void renderCube(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normal,
            float min,
            float max,
            float red,
            float green,
            float blue,
            float alpha,
            int packedLight
    ) {
        quad(consumer, matrix, normal, min, min, max, max, min, max, max, max, max, min, max, max, 0, 0, 1, red, green, blue, alpha, packedLight);
        quad(consumer, matrix, normal, max, min, min, min, min, min, min, max, min, max, max, min, 0, 0, -1, red, green, blue, alpha, packedLight);
        quad(consumer, matrix, normal, min, max, min, max, max, min, max, max, max, min, max, max, 0, 1, 0, red, green, blue, alpha, packedLight);
        quad(consumer, matrix, normal, min, min, max, max, min, max, max, min, min, min, min, min, 0, -1, 0, red, green, blue, alpha, packedLight);
        quad(consumer, matrix, normal, max, min, max, max, min, min, max, max, min, max, max, max, 1, 0, 0, red, green, blue, alpha, packedLight);
        quad(consumer, matrix, normal, min, min, min, min, min, max, min, max, max, min, max, min, -1, 0, 0, red, green, blue, alpha, packedLight);
    }

    private static void quad(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normal,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float normalX,
            float normalY,
            float normalZ,
            float red,
            float green,
            float blue,
            float alpha,
            int packedLight
    ) {
        vertex(consumer, matrix, normal, x1, y1, z1, 0, 0, normalX, normalY, normalZ, red, green, blue, alpha, packedLight);
        vertex(consumer, matrix, normal, x2, y2, z2, 1, 0, normalX, normalY, normalZ, red, green, blue, alpha, packedLight);
        vertex(consumer, matrix, normal, x3, y3, z3, 1, 1, normalX, normalY, normalZ, red, green, blue, alpha, packedLight);
        vertex(consumer, matrix, normal, x4, y4, z4, 0, 1, normalX, normalY, normalZ, red, green, blue, alpha, packedLight);
    }

    private static void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normal,
            float x,
            float y,
            float z,
            float u,
            float v,
            float normalX,
            float normalY,
            float normalZ,
            float red,
            float green,
            float blue,
            float alpha,
            int packedLight
    ) {
        consumer.vertex(matrix, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, normalX, normalY, normalZ)
                .endVertex();
    }
}