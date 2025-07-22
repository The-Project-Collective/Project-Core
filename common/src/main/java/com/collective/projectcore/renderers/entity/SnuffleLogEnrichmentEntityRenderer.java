package com.collective.projectcore.renderers.entity;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.entities.enrichment.SnuffleLogEnrichmentEntity;
import com.collective.projectcore.models.entity.enrichment.SnuffleLogEnrichmentEntityModel;
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

public class SnuffleLogEnrichmentEntityRenderer extends EntityRenderer<SnuffleLogEnrichmentEntity, SnuffleLogEnrichmentEntityRenderer.SnuffleLogRenderState> {
    private static final Map<Integer, Identifier> TEXTURES = Util.make(new HashMap<>(), map -> {
        map.put(0, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_acacia.png"));
        map.put(1, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_birch.png"));
        map.put(2, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_cherry.png"));
        map.put(3, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_crismon.png"));
        map.put(4, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_dark_oak.png"));
        map.put(5, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_jungle.png"));
        map.put(6, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_mangrove.png"));
        map.put(7, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_oak.png"));
        map.put(8, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_pale_oak.png"));
        map.put(9, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_spruce.png"));
        map.put(10, Identifier.of(ProjectCore.MOD_ID, "textures/entity/enrichment/snuffle_log/snuffle_log_warped.png"));
    });

    private final SnuffleLogEnrichmentEntityModel model;

    public SnuffleLogEnrichmentEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new SnuffleLogEnrichmentEntityModel(ctx.getPart(SnuffleLogEnrichmentEntityModel.LAYER_LOCATION));
        this.shadowRadius = 0.4f;
    }

    @Override
    public void render(SnuffleLogRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        SnuffleLogEnrichmentEntity entity = state.getEntity();
        Identifier texture = TEXTURES.getOrDefault(entity.getEnrichmentType(), TEXTURES.get(0));
        matrices.push();
        matrices.translate(0, 0.2, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));
        float tickDelta = state.age - (int) state.age;
        float rollX = MathHelper.lerp(tickDelta, entity.getPreviousRollAngleX(), entity.getRollAngleX());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.wrapDegrees(rollX)));
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        this.model.getRootPart().render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public SnuffleLogRenderState createRenderState() {
        return new SnuffleLogRenderState();
    }

    @Override
    public void updateRenderState(SnuffleLogEnrichmentEntity entity, SnuffleLogRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.setEntity(entity);
    }

    public static class SnuffleLogRenderState extends EntityRenderState {

        private SnuffleLogEnrichmentEntity entity;

        public void setEntity(SnuffleLogEnrichmentEntity entity) {
            this.entity = entity;
        }

        public SnuffleLogEnrichmentEntity getEntity() {
            return entity;
        }
    }
}
