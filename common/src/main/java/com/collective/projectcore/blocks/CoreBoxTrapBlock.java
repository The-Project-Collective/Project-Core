package com.collective.projectcore.blocks;

import com.collective.projectcore.blockentities.traps.CoreBoxTrapBlockEntity;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CoreBoxTrapBlockEntity trap) {
                ItemStack stack = new ItemStack(this);
                if (world instanceof ServerWorld serverWorld) {
                    RegistryWrapper.WrapperLookup registries = serverWorld.getRegistryManager();
                    NbtCompound beNbt = new NbtCompound();
                    trap.writeNbt(beNbt, registries);
                    stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(beNbt));
                    ItemScatterer.spawn(Objects.requireNonNull(Objects.requireNonNull(world.getBlockEntity(pos)).getWorld()), pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onBroken(world, pos, state);
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
                    }
                }
            }
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isSneaking() || world.isClient() || state.get(OPEN)) {
            return ActionResult.FAIL;
        }
        else {
            boolean success = tryReleaseEntity(state, (ServerWorld) world, pos);
            return success ? ActionResult.SUCCESS : ActionResult.FAIL;
        }
    }

    private boolean tryReleaseEntity(BlockState state, ServerWorld world, BlockPos pos) {
        CoreBoxTrapBlockEntity blockEntity = (CoreBoxTrapBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null) {
            MobEntity mob = blockEntity.releaseEntity(world);
            if (mob != null) {
                mob.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, world.random.nextFloat() * 360F, 0.0F);
                world.spawnEntity(mob);
                world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
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
        CoreBoxTrapBlockEntity boxEntity = (CoreBoxTrapBlockEntity)world.getBlockEntity(pos);
        if (!world.isClient() && !(entity instanceof PlayerEntity) && entity.isAlive() && entity instanceof LivingEntity) {
            if (boxEntity != null && !boxEntity.isOccupied() && entity instanceof MobEntity mobEntity) {
                boxEntity.captureEntity(mobEntity);
                world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
                world.setBlockState(pos, state.with(OPEN, false));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (nbtComponent == null) return;
        NbtCompound entityNbt = nbtComponent.getNbt();
        String entityId = entityNbt.getString("id");
        if (entityId.isEmpty()) return;
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(entityId));
        tooltip.add(Text.translatable("tooltip.project_core.box_trap.entity", entityId));
        if (entityNbt.contains("CustomName")) {
            String customName = entityNbt.getString("CustomName");
            tooltip.add(Text.translatable("tooltip.project_core.box_trap.entity_name", customName));
        }
        if (entityType.isIn(CoreTags.ALL_ENTITIES)) {
            if (entityType.isIn(CoreTags.ALL_ENTITIES)) {
                if (entityNbt.contains("Age")) {
                    int age = entityNbt.getInt("Age");
                    tooltip.add(Text.translatable("tooltip.project_core.box_trap.entity_age", age));
                }
            }
            if (entityType.isIn(CoreTags.ALL_ENTITIES)) {
                if (entityNbt.contains("Gender")) {
                    String gender = entityNbt.getString("Gender");
                    tooltip.add(Text.translatable("tooltip.project_core.box_trap.entity_gender", gender));
                }
            }
        }

    }

}
