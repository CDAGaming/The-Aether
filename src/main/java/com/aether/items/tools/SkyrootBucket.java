package com.aether.items.tools;

import com.aether.items.AetherItemGroups;
import com.aether.items.AetherItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class SkyrootBucket extends Item {

    private final Fluid containedBlock;

    public SkyrootBucket() {
        super(new Settings().maxCount(16).group(AetherItemGroups.MISC));
        this.containedBlock = Fluids.EMPTY;
    }

    public SkyrootBucket(Item containerIn) {
        super(new Settings().maxCount(1).group(AetherItemGroups.MISC).recipeRemainder(containerIn));
        this.containedBlock = Fluids.EMPTY;
    }

    public SkyrootBucket(Fluid containedFluidIn, Item containerIn) {
        super(new Settings().maxCount(1).group(AetherItemGroups.MISC).recipeRemainder(containerIn));
        this.containedBlock = containedFluidIn;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getStackInHand(handIn);
        HitResult raytraceresult = raycast(worldIn, playerIn, this.containedBlock == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);

        if (itemstack.getItem() != AetherItems.SKYROOT_WATER_BUCKET && itemstack.getItem() != AetherItems.SKYROOT_BUCKET) {
            playerIn.setCurrentHand(handIn);
            return new TypedActionResult<>(ActionResult.PASS, itemstack);
        }

        if (raytraceresult == null) {
            return new TypedActionResult<>(ActionResult.PASS, itemstack);
        } else if (raytraceresult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult class_3965_1 = (BlockHitResult) raytraceresult;
            BlockPos blockpos = class_3965_1.getBlockPos();

            if (worldIn.canPlayerModifyAt(playerIn, blockpos) && playerIn.canPlaceOn(blockpos, class_3965_1.getSide(), itemstack)) {
                if (this.containedBlock == Fluids.EMPTY) {
                    BlockState iblockstate1 = worldIn.getBlockState(blockpos);

                    if (iblockstate1.getBlock() instanceof FluidDrainable) {
                        Fluid fluid = ((FluidDrainable) iblockstate1.getBlock()).tryDrainFluid(worldIn, blockpos, iblockstate1);

                        if (fluid != Fluids.EMPTY) {
                            playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
                            playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            ItemStack itemstack1 = this.fillBucket(itemstack, playerIn, AetherItems.SKYROOT_WATER_BUCKET);

                            return new TypedActionResult<>(ActionResult.SUCCESS, itemstack1);
                        }
                    }

                    return new TypedActionResult<>(ActionResult.FAIL, itemstack);
                } else {
                    BlockState iblockstate = worldIn.getBlockState(blockpos);
                    BlockPos blockpos1 = iblockstate.getBlock() instanceof FluidFillable ? blockpos : class_3965_1.getBlockPos().offset(class_3965_1.getSide());

                    this.placeLiquid(playerIn, worldIn, blockpos1, class_3965_1);

                    playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
                    return new TypedActionResult<>(ActionResult.SUCCESS, this.emptyBucket(itemstack, playerIn));
                }
            } else {
                return new TypedActionResult<>(ActionResult.FAIL, itemstack);
            }
        } else {
            return new TypedActionResult<>(ActionResult.PASS, itemstack);
        }
    }

    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity)
            return this.onBucketContentsConsumed(stack, worldIn, (PlayerEntity) entityLiving);
        return super.finishUsing(stack, worldIn, entityLiving);
    }

    public ItemStack onBucketContentsConsumed(ItemStack itemstack, World world, PlayerEntity entityPlayer) {

        if (entityPlayer instanceof ServerPlayerEntity) entityPlayer.incrementStat(Stats.USED.getOrCreateStat(this));

        if (!entityPlayer.abilities.creativeMode) itemstack.setCount(itemstack.getCount() - 1);

        if (itemstack.getItem() == AetherItems.SKYROOT_POISON_BUCKET) {
            // TODO: Hurt player
        } else if (itemstack.getItem() == AetherItems.SKYROOT_REMEDY_BUCKET) {
            //TODO: Cure player
        } else if (itemstack.getItem() == AetherItems.SKYROOT_MILK_BUCKET) {
            if (!world.isClient) entityPlayer.clearStatusEffects();
        }
        return itemstack.isEmpty() ? new ItemStack(AetherItems.SKYROOT_BUCKET) : itemstack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if (stack.getItem() != AetherItems.SKYROOT_WATER_BUCKET && stack.getItem() != AetherItems.SKYROOT_BUCKET)
            return UseAction.DRINK;
        return UseAction.NONE;
    }

    protected ItemStack emptyBucket(ItemStack p_203790_1_, PlayerEntity p_203790_2_) {
        return !p_203790_2_.abilities.creativeMode ? new ItemStack(AetherItems.SKYROOT_BUCKET) : p_203790_1_;
    }

    private ItemStack fillBucket(ItemStack emptyBuckets, PlayerEntity player, Item fullBucket) {
        if (player.abilities.creativeMode) {
            return emptyBuckets;
        } else {
            emptyBuckets.setCount(emptyBuckets.getCount() - 1);
            if (emptyBuckets.isEmpty()) {
                return new ItemStack(fullBucket);
            } else {
                if (!player.inventory.insertStack(new ItemStack(fullBucket)))
                    player.dropItem(new ItemStack(fullBucket), false);
                return emptyBuckets;
            }
        }
    }

    public boolean placeLiquid(PlayerEntity playerIn, World worldIn, BlockPos posIn, BlockHitResult hitResult) {
        if (!(this.containedBlock instanceof FlowableFluid)) {
            return false;
        } else {
            BlockState iblockstate = worldIn.getBlockState(posIn);
            Material material = iblockstate.getMaterial();
            boolean flag = !material.isSolid();
            boolean flag1 = material.isReplaceable();

            if (worldIn.isAir(posIn) || flag || flag1 || iblockstate.getBlock() instanceof FluidFillable && ((FluidFillable) iblockstate.getBlock()).canFillWithFluid(worldIn, posIn, iblockstate, this.containedBlock)) {
                if (worldIn.getRegistryKey().equals(World.NETHER)) {
                    int i = posIn.getX();
                    int j = posIn.getY();
                    int k = posIn.getZ();
                    worldIn.playSound(playerIn, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);

                    for (int l = 0; l < 8; ++l) {
                        worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else if (iblockstate.getBlock() instanceof FluidFillable) {
                    if (((FluidFillable) iblockstate.getBlock()).tryFillWithFluid(worldIn, posIn, iblockstate, ((FlowableFluid) this.containedBlock).getStill(false))) {
                        this.playEmptySound(playerIn, worldIn, posIn);
                    }
                } else {
                    if (!worldIn.isClient && (flag || flag1) && !material.isLiquid()) {
                        worldIn.breakBlock(posIn, true);
                    }

                    this.playEmptySound(playerIn, worldIn, posIn);
                    worldIn.setBlockState(posIn, this.containedBlock.getDefaultState().getBlockState(), 11);
                }

                return true;
            } else {
                return hitResult != null && this.placeLiquid(playerIn, worldIn, hitResult.getBlockPos().offset(hitResult.getSide()), null);
            }
        }
    }

    protected void playEmptySound(PlayerEntity player, World worldIn, BlockPos pos) {
        worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return stack.getItem() == AetherItems.SKYROOT_REMEDY_BUCKET ? Rarity.RARE : super.getRarity(stack);
    }
}