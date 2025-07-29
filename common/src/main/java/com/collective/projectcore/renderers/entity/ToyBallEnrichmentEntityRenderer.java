package com.collective.projectcore.renderers.entity;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.entities.enrichment.ToyBallEnrichmentEntity;
import com.collective.projectcore.entities.enrichment.ToyBallEnrichmentEntity;
import com.collective.projectcore.models.entity.enrichment.ToyBallEnrichmentEntityModel;
import com.collective.projectcore.models.entity.enrichment.ToyBallEnrichmentEntityModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;
import java.util.Map;

public class ToyBallEnrichmentEntityRenderer extends EntityRenderer<ToyBallEnrichmentEntity, ToyBallEnrichmentEntityRenderer.ToyBallRenderState> {
    private static final Map<Integer, Identifier> TEXTURES = Util.make(new HashMap<>(), map -> {
        map.put(0, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_white.png"));
        map.put(1, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_light_gray.png"));
        map.put(2, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_gray.png"));
        map.put(3, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_black.png"));
        map.put(4, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_brown.png"));
        map.put(5, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_red.png"));
        map.put(6, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_orange.png"));
        map.put(7, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_yellow.png"));
        map.put(8, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_lime.png"));
        map.put(9, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_green.png"));
        map.put(10, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_cyan.png"));
        map.put(11, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_light_blue.png"));
        map.put(12, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_blue.png"));
        map.put(13, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_purple.png"));
        map.put(14, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_magenta.png"));
        map.put(15, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/toy_ball/toy_ball_pink.png"));
    });

    private final ToyBallEnrichmentEntityModel model;

    public ToyBallEnrichmentEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new ToyBallEnrichmentEntityModel(ctx.getPart(ToyBallEnrichmentEntityModel.LAYER_LOCATION));
        this.shadowRadius = 0.4f;
    }

    @Override
    public void render(ToyBallRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        ToyBallEnrichmentEntity entity = state.getEntity();
        Identifier texture = TEXTURES.getOrDefault(entity.getEnrichmentType(), TEXTURES.get(0));
        matrices.push();
        matrices.translate(0, 0.3, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));
        float tickDelta = state.age - (int) state.age;
        float rollX = MathHelper.lerp(tickDelta, entity.getPreviousRollAngleX(), entity.getRollAngleX());
        float rollZ = MathHelper.lerp(tickDelta, entity.getPreviousRollAngleZ(), entity.getRollAngleZ());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.wrapDegrees(rollX)));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.wrapDegrees(rollZ)));
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        this.model.getRootPart().render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public ToyBallRenderState createRenderState() {
        return new ToyBallRenderState();
    }

    @Override
    public void updateRenderState(ToyBallEnrichmentEntity entity, ToyBallRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.setEntity(entity);
    }

    public static class ToyBallRenderState extends EntityRenderState {

        private ToyBallEnrichmentEntity entity;

        public void setEntity(ToyBallEnrichmentEntity entity) {
            this.entity = entity;
        }

        public ToyBallEnrichmentEntity getEntity() {
            return entity;
        }
    }
}
