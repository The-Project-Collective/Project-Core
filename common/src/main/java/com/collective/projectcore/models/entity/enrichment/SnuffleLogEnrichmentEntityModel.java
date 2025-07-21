package com.collective.projectcore.models.entity.enrichment;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.renderers.entity.SnuffleLogEnrichmentEntityRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class SnuffleLogEnrichmentEntityModel extends EntityModel<SnuffleLogEnrichmentEntityRenderer.SnuffleLogRenderState> {
    private final ModelPart root;
    private final ModelPart main;

    public static final EntityModelLayer LAYER_LOCATION =
            new EntityModelLayer(Identifier.of(ProjectCore.MOD_ID, "enrichment/snuffle_log"), "main");

    public SnuffleLogEnrichmentEntityModel(ModelPart root) {
        super(root, RenderLayer::getEntityCutout);
        this.root = root;
        this.main = root.getChild("main");
    }

    @Override
    public void setAngles(SnuffleLogEnrichmentEntityRenderer.SnuffleLogRenderState state) {
        super.setAngles(state);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("main", ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-8.0F, -4.0F, -4.0F, 16.0F, 8.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.2F, 0.0F)); // Pivot at center of 8-tall model

        return TexturedModelData.of(modelData, 64, 64);
    }

}
