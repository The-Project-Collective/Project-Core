package com.collective.projectcore.entities.enrichment;

import com.collective.projectcore.items.CoreItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ToyBallEnrichmentEntity extends CoreEnrichmentEntity {

    public ToyBallEnrichmentEntity(EntityType<? extends CoreEnrichmentEntity> type, World world) {
        super(type, world);
    }

    @Override
    public Item getItemForEnrichmentType(int type) {
        EnrichmentWoolType woolType = EnrichmentWoolType.fromId(type);
        return switch (woolType) {
            case WHITE -> CoreItems.TOY_BALL_WHITE.get();
            case LIGHT_GRAY -> CoreItems.TOY_BALL_LIGHT_GRAY.get();
            case GRAY -> CoreItems.TOY_BALL_GRAY.get();
            case BLACK -> CoreItems.TOY_BALL_BLACK.get();
            case BROWN -> CoreItems.TOY_BALL_BROWN.get();
            case RED -> CoreItems.TOY_BALL_RED.get();
            case ORANGE -> CoreItems.TOY_BALL_ORANGE.get();
            case YELLOW -> CoreItems.TOY_BALL_YELLOW.get();
            case LIME -> CoreItems.TOY_BALL_LIME.get();
            case GREEN -> CoreItems.TOY_BALL_GREEN.get();
            case CYAN -> CoreItems.TOY_BALL_CYAN.get();
            case LIGHT_BLUE -> CoreItems.TOY_BALL_LIGHT_BLUE.get();
            case BLUE -> CoreItems.TOY_BALL_BLUE.get();
            case PURPLE -> CoreItems.TOY_BALL_PURPLE.get();
            case MAGENTA -> CoreItems.TOY_BALL_MAGENTA.get();
            case PINK -> CoreItems.TOY_BALL_PINK.get();
        };
    }

    @Override
    public BlockState getBlockStateParticlesForEnrichmentType(int type) {
        EnrichmentWoolType woolType = EnrichmentWoolType.fromId(type);
        return switch (woolType) {
            case WHITE -> Blocks.WHITE_WOOL.getDefaultState();
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WOOL.getDefaultState();
            case GRAY -> Blocks.GRAY_WOOL.getDefaultState();
            case BLACK -> Blocks.BLACK_WOOL.getDefaultState();
            case BROWN -> Blocks.BROWN_WOOL.getDefaultState();
            case RED -> Blocks.RED_WOOL.getDefaultState();
            case ORANGE -> Blocks.ORANGE_WOOL.getDefaultState();
            case YELLOW -> Blocks.YELLOW_WOOL.getDefaultState();
            case LIME -> Blocks.LIME_WOOL.getDefaultState();
            case GREEN -> Blocks.GREEN_WOOL.getDefaultState();
            case CYAN -> Blocks.CYAN_WOOL.getDefaultState();
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WOOL.getDefaultState();
            case BLUE -> Blocks.BLUE_WOOL.getDefaultState();
            case PURPLE -> Blocks.PURPLE_WOOL.getDefaultState();
            case MAGENTA -> Blocks.MAGENTA_WOOL.getDefaultState();
            case PINK -> Blocks.PINK_WOOL.getDefaultState();
        };
    }

    @Override
    public void tick() {
        this.baseTick();
        if (!this.getWorld().isClient()) {
            Vec3d velocity = this.getVelocity();
            List<LivingEntity> nearbyEntities = this.getWorld().getNonSpectatingEntities(
                    LivingEntity.class,
                    this.getBoundingBox().expand(0.1)
            );
            for (LivingEntity entity : nearbyEntities) {
                Vec3d pushDir = this.getPos().subtract(entity.getPos());
                double distSq = pushDir.lengthSquared();
                if (distSq > 1e-7) {
                    pushDir = pushDir.normalize().multiply(0.1 / distSq);
                    velocity = velocity.add(pushDir);
                }
            }
            if (this.isSubmergedIn(FluidTags.WATER)) {
                double fluidHeight = this.getWorld().getFluidState(this.getBlockPos()).getHeight(this.getWorld(), this.getBlockPos());
                double waterSurfaceY = this.getBlockPos().getY() + fluidHeight;
                double depth = MathHelper.clamp(waterSurfaceY - this.getY(), 0.0, 1.0);

                // Apply buoyant force proportional to how deep the entity is
                double buoyancy = 0.1 * depth;
                velocity = velocity.add(0.0, buoyancy, 0.0);

                // Apply drag to smooth movement
                velocity = new Vec3d(velocity.x * 0.85, velocity.y * 0.9, velocity.z * 0.85);

                // Cap upward velocity to avoid overshooting
                if (velocity.y > 0.15) {
                    velocity = new Vec3d(velocity.x, 0.15, velocity.z);
                }
            } else {
                // Out of water = apply gravity and drag
                velocity = velocity.add(0.0, -0.04, 0.0);
            }
            double maxSpeed = 0.3;
            double horizontalSpeedSq = velocity.x * velocity.x + velocity.z * velocity.z;
            if (horizontalSpeedSq > maxSpeed * maxSpeed) {
                double factor = maxSpeed / Math.sqrt(horizontalSpeedSq);
                velocity = new Vec3d(velocity.x * factor, velocity.y, velocity.z * factor);
            }
            this.setVelocity(velocity);
            this.move(MovementType.SELF, velocity);
            this.setPreviousRollAngleX(this.getRollAngleX());
            this.setPreviousRollAngleZ(this.getRollAngleZ());
            double dx = velocity.x;
            double dy = velocity.y;
            double dz = velocity.z;
            double horizontalSpeed = Math.sqrt(dx * dx + dz * dz);
            double verticalSpeed = Math.sqrt(dy * dy);
            if (horizontalSpeed > 0.001) {
                float radius = 0.25f;
                float deltaAngle = (float) ((horizontalSpeed / (2 * Math.PI * radius)) * 360f);
                float deltaAngleX = dz > 0 ? -deltaAngle : deltaAngle;
                float deltaAngleZ = dx > 0 ? deltaAngle : -deltaAngle;
                this.setRollAngleX(this.getRollAngleX() + deltaAngleX);
                this.setRollAngleZ(this.getRollAngleZ() + deltaAngleZ);
            } else if (verticalSpeed > 0.001) {
                float radius = 0.25f;
                verticalSpeed = 0.01;
                float deltaAngle = (float) ((verticalSpeed / (2 * Math.PI * radius)) * 360f);
                float deltaAngleX = dz > 0 ? -deltaAngle : deltaAngle;
                float deltaAngleZ = dx > 0 ? deltaAngle : -deltaAngle;
                this.setRollAngleX(this.getRollAngleX() + deltaAngleX);
                this.setRollAngleZ(this.getRollAngleZ() + deltaAngleZ);
            }

        }
    }

    public enum EnrichmentWoolType {
        WHITE(0, "white"),
        LIGHT_GRAY(1, "light_gray"),
        GRAY(2, "gray"),
        BLACK(3, "black"),
        BROWN(4, "brown"),
        RED(5, "red"),
        ORANGE(6, "orange"),
        YELLOW(7, "yellow"),
        LIME(8, "lime"),
        GREEN(9, "green"),
        CYAN(10, "cyan"),
        LIGHT_BLUE(11, "light_blue"),
        BLUE(12, "blue"),
        PURPLE(13, "purple"),
        MAGENTA(14, "magenta"),
        PINK(15, "pink");


        private final int id;
        private final String textureName;

        EnrichmentWoolType(int id, String textureName) {
            this.id = id;
            this.textureName = textureName;
        }

        public int getId() { return id; }
        public String getTextureName() { return textureName; }

        public static EnrichmentWoolType fromId(int id) {
            for (EnrichmentWoolType type : values()) {
                if (type.id == id) return type;
            }
            return WHITE;
        }
    }

}
