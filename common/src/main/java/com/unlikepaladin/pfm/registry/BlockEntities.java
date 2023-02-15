package com.unlikepaladin.pfm.registry;

import com.unlikepaladin.pfm.blocks.blockentities.*;
import com.unlikepaladin.pfm.blocks.blockentities.*;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class BlockEntities {
    public static BlockEntityType<GenericStorageBlockEntity9x3> DRAWER_BLOCK_ENTITY;
    public static BlockEntityType<FridgeBlockEntity> FRIDGE_BLOCK_ENTITY;
    public static BlockEntityType<FreezerBlockEntity> FREEZER_BLOCK_ENTITY;
    public static BlockEntityType<FreezerBlockEntity> IRON_FREEZER_BLOCK_ENTITY;
    public static BlockEntityType<? extends StoveBlockEntity> STOVE_BLOCK_ENTITY;
    public static BlockEntityType<CounterOvenBlockEntity> KITCHEN_COUNTER_OVEN_BLOCK_ENTITY;
    public static BlockEntityType<LightSwitchBlockEntity> LIGHT_SWITCH_BLOCK_ENTITY;
    public static BlockEntityType<? extends MicrowaveBlockEntity> MICROWAVE_BLOCK_ENTITY;
    public static BlockEntityType<? extends StovetopBlockEntity> STOVE_TOP_BLOCK_ENTITY;
    public static BlockEntityType<? extends PlateBlockEntity> PLATE_BLOCK_ENTITY;
    public static BlockEntityType<ToiletBlockEntity> TOILET_BLOCK_ENTITY;
    public static BlockEntityType<SinkBlockEntity> SINK_BLOCK_ENTITY;
    public static BlockEntityType<? extends TrashcanBlockEntity> TRASHCAN_BLOCK_ENTITY;
    public static BlockEntityType<GenericStorageBlockEntity3x3> KITCHEN_DRAWER_SMALL_BLOCK_ENTITY;

    public static BlockEntityType<? extends BedBlockEntity> SIMPLE_BED_BLOCK_ENTITY;

}
