package com.unlikepaladin.pfm.blocks;

import com.unlikepaladin.pfm.blocks.blockentities.SinkBlockEntity;
import com.unlikepaladin.pfm.registry.BlockEntities;
import com.unlikepaladin.pfm.registry.ParticleIDs;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

import static com.unlikepaladin.pfm.blocks.BasicToiletBlock.checkType;

public abstract class AbstractSinkBlock extends AbstractCauldronBlock implements BlockEntityProvider {
    public static final IntProperty LEVEL_4 = IntProperty.of("level", 0, 3);
    final Map<Item, CauldronBehavior> behaviorMap;
    final Predicate<Biome.Precipitation> precipitationPredicate;

    public AbstractSinkBlock(Settings settings, Predicate<Biome.Precipitation> precipitationPredicate, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
        this.behaviorMap = behaviorMap;
        this.precipitationPredicate = precipitationPredicate;
        this.setDefaultState(this.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(LEVEL_4, 0));

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(LEVEL_4);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockPos sourcePos = pos.down().down();
        ItemStack itemStack = player.getStackInHand(hand);
        CauldronBehavior sinkBehavior = this.behaviorMap.get(itemStack.getItem());
        if (sinkBehavior != null && itemStack.getItem() != Items.AIR) {
            return sinkBehavior.interact(state, world, pos, player, hand, itemStack);
        }
        if (state.get(LEVEL_4) < 3) {
            BlockState sourceState = world.getBlockState(sourcePos);
            if (sourceState.getFluidState().getFluid() == Fluids.WATER && !sourceState.getFluidState().isEmpty()) {
                if (sourceState.getProperties().contains(Properties.WATERLOGGED)) {
                    world.setBlockState(sourcePos, sourceState.with(Properties.WATERLOGGED, false));
                }
                else {
                    world.setBlockState(sourcePos, Blocks.AIR.getDefaultState());
                }
                SinkBlockEntity blockEntity = (SinkBlockEntity) world.getBlockEntity(pos);
                if (blockEntity != null) {
                    blockEntity.setFilling(true);
                }
                world.setBlockState(pos, state.with(LEVEL_4, 3));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL_4);
    }

    @Override
    protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
        if (this.isFull(state)) {
            return;
        }
        world.setBlockState(pos, (BlockState)state.with(LEVEL_4, state.get(LEVEL_4) + 1));
        world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON, pos, 0);
    }

    protected static boolean canFillWithPrecipitation(World world, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.05f;
        }
        if (precipitation == Biome.Precipitation.SNOW) {
            return world.getRandom().nextFloat() < 0.1f;
        }
        return false;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockEntities.SINK_BLOCK_ENTITY, SinkBlockEntity::tick);
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (!canFillWithPrecipitation(world, precipitation) || state.get(LEVEL_4) == 3 || !this.precipitationPredicate.test(precipitation)) {
            return;
        }
        world.setBlockState(pos, state.cycle(LEVEL_4));
    }

    public static void spawnParticles(Direction facing, World world, BlockPos pos) {
        if (world.isClient) {
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            if (facing == Direction.EAST) {
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.76, y + 1.19, z + 0.5, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.76, y + 1.19, z + 0.5, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.76, y + 1.19, z + 0.5, 0.0, 0.0, 0.0);
            }
            else if (facing == Direction.SOUTH){
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.5, y + 1.19, z + 0.76, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.5, y + 1.19, z + 0.76, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.5, y + 1.19, z + 0.76, 0.0, 0.0, 0.0);
            }
            else if (facing == Direction.NORTH){
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.5, y + 1.19, z + 0.24, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.5, y + 1.19, z + 0.24, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.5, y + 1.19, z + 0.24, 0.0, 0.0, 0.0);
            }
            else {
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.24, y + 1.19, z + 0.5, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.24, y + 1.19, z + 0.5, 0.0, 0.0, 0.0);
                world.addParticle(ParticleIDs.WATER_DROP, x + 0.24, y + 1.19, z + 0.5, 0.0, 0.0, 0.0);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SinkBlockEntity(pos, state);
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return (6.0 + (double) state.get(LEVEL_4).intValue() * 3.0) / 16.0;
    }

    @Override
    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid == Fluids.WATER && this.precipitationPredicate == LeveledCauldronBlock.RAIN_PREDICATE;
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.get(LEVEL_4) == 3;
    }

    public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
        int i = state.get(LEVEL_4) - 1;
        world.setBlockState(pos, state.with(LEVEL_4, i));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void onFireCollision(BlockState state, World world, BlockPos pos) {
        AbstractSinkBlock.decrementFluidLevel(state, world, pos);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
            entity.extinguish();
            if (entity.canModifyAt(world, pos)) {
                this.onFireCollision(state, world, pos);
            }
        }
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
