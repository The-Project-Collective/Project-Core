package com.collective.projectcore.blocks;

import com.collective.projectcore.blockentities.traps.CoreBoxTrapBlockEntity;
import com.collective.projectcore.entities.CoreAnimalEntity;
import com.collective.projectcore.groups.tags.CoreTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class CoreBoxTrapBlock extends CoreBlockWithEntity {

    protected VoxelShape BOX_COLLISION_OPEN = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D);;
    protected VoxelShape BOX_COLLISION_SHUT = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);;

    public static final BooleanProperty OPEN = Properties.OPEN;

    public CoreBoxTrapBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(OPEN) ? BOX_COLLISION_OPEN : BOX_COLLISION_SHUT;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(OPEN) ? BOX_COLLISION_OPEN : BOX_COLLISION_SHUT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(OPEN, true);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return state.get(OPEN);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CoreBoxTrapBlockEntity trap) {
                ItemStack stack = new ItemStack(this);
                if (trap.isOccupied()) {
                    NbtCompound beNbt = trap.createNbtWithId(world.getRegistryManager());
                    System.out.println("Item NBT: "+beNbt);
                    stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(beNbt));
                    ItemScatterer.spawn(Objects.requireNonNull(Objects.requireNonNull(world.getBlockEntity(pos)).getWorld()), pos.getX(), pos.getY(), pos.getZ(), stack);
                } else {
                    if (!player.isCreative()) {
                        ItemScatterer.spawn(Objects.requireNonNull(Objects.requireNonNull(world.getBlockEntity(pos)).getWorld()), pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CoreBoxTrapBlockEntity trap) {
                NbtComponent component = itemStack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
                if (component != null) {
                    if (world instanceof ServerWorld serverWorld) {
                        trap.readNbt(component.copyNbt(), serverWorld.getRegistryManager());
                        if (trap.isOccupied()) {
                            world.setBlockState(pos, state.with(OPEN, false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.offset(Direction.Axis.X, 1)).isAir() &&
                world.getBlockState(pos.offset(Direction.Axis.X, -1)).isAir() &&
                world.getBlockState(pos.offset(Direction.Axis.Z, 1)).isAir() &&
                world.getBlockState(pos.offset(Direction.Axis.Z, -1)).isAir();
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isSneaking() || state.get(OPEN)) {
            return ActionResult.FAIL;
        }
        else {
            boolean success = false;
            if (!world.isClient()) {
                success = tryReleaseEntity(state, (ServerWorld) world, pos);
            }
            if (success) {
                if (world.isClient()) {
                    this.handleParticles(ParticleTypes.POOF, world, pos);
                }
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        }
    }

    private boolean tryReleaseEntity(BlockState state, ServerWorld world, BlockPos pos) {
        CoreBoxTrapBlockEntity blockEntity = (CoreBoxTrapBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null) {
            MobEntity mob = blockEntity.releaseEntity(world);
            if (mob != null) {
                mob.refreshPositionAndAngles(pos.getX() + 2, pos.getY() + 0.5, pos.getZ() + 2, world.random.nextFloat() * 360F, 0.0F);
                world.spawnEntity(mob);
                world.playSound(null, pos, SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 0.3F, 0.8F);
                world.setBlockState(pos, state.with(OPEN, true));
                blockEntity.clearEntityData();
                return true;
            }
            else {
                world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.6F);
            }
        }
        return false;
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        boolean captured = false;
        CoreBoxTrapBlockEntity boxEntity = (CoreBoxTrapBlockEntity)world.getBlockEntity(pos);
        if (!world.isClient() && !(entity instanceof PlayerEntity) && entity.isAlive() && entity instanceof LivingEntity) {
            if (boxEntity != null && !boxEntity.isOccupied() && entity instanceof MobEntity mobEntity) {
                if ((mobEntity instanceof CoreAnimalEntity coreAnimalEntity && coreAnimalEntity.doesAge() && coreAnimalEntity.isAdult()) || (!(mobEntity instanceof CoreAnimalEntity) && !mobEntity.isBaby())) {
                    boxEntity.captureEntity(mobEntity);
                    world.playSound(null, pos, SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 0.3F, 0.8F);
                    world.setBlockState(pos, state.with(OPEN, false));
                    captured = true;
                }
            }
        } if (world.isClient() && captured) {
            this.handleParticles(ParticleTypes.POOF, world, pos);
        }
    }

    public void handleParticles(ParticleEffect particleEffect, World world, BlockPos pos) {
        Random random = new Random();
        for(int i = 0; i < 7; ++i) {
            double d = random.nextFloat() * 0.02;
            double e = random.nextFloat() * 0.02;
            double f = random.nextFloat() * 0.02;
            world.addImportantParticle(particleEffect, pos.getX() + random.nextFloat(), pos.getY() + 2, pos.getZ() + random.nextFloat(), d, e, f);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (nbtComponent != null) {
            NbtCompound entityNbt = nbtComponent.getNbt().copy().getCompound("EntityTag");
            String entityId = entityNbt.getString("id");
            if (!entityId.isEmpty()) {
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(entityId));
                tooltip.add(Text.translatable(entityType.toString()).formatted(Formatting.ITALIC, Formatting.YELLOW));
                if (entityNbt.contains("CustomName")) {
                    String customName = entityNbt.getString("CustomName");
                    tooltip.add(Text.translatable("tooltip.project_core.box_trap.custom_name", customName).formatted(Formatting.ITALIC, Formatting.GRAY));
                }
                if (entityType.isIn(CoreTags.ALL_ENTITIES)) {
                    if (entityType.isIn(CoreTags.ALL_ENTITIES)) {
                        if (entityNbt.contains("AgeTicks")) {
                            int age = entityNbt.getInt("AgeTicks") / 24000;
                            tooltip.add(Text.translatable("tooltip.project_core.box_trap.age", age).formatted(Formatting.ITALIC, Formatting.GRAY));
                        }
                    }
                    if (entityType.isIn(CoreTags.ALL_ENTITIES)) {
                        if (entityNbt.contains("Gender")) {
                            String gender = entityNbt.getInt("Gender") == 0 ? "tooltip.project_core.box_trap.male" : "tooltip.project_core.box_trap.female";
                            tooltip.add(Text.translatable(gender).formatted(Formatting.ITALIC, Formatting.GRAY));
                        }
                    }
                }
            } else {
                tooltip.add(Text.translatable("tooltip.project_core.box_trap.empty").formatted(Formatting.ITALIC, Formatting.GRAY));
            }
        } else {
            tooltip.add(Text.translatable("tooltip.project_core.box_trap.empty").formatted(Formatting.ITALIC, Formatting.GRAY));
        }

    }

}
