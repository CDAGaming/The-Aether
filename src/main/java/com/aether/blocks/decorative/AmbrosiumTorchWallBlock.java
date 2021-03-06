package com.aether.blocks.decorative;

import com.aether.blocks.AetherBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class AmbrosiumTorchWallBlock extends WallTorchBlock {
    public AmbrosiumTorchWallBlock() {
        super(FabricBlockSettings.of(Material.SUPPORTED).collidable(false).breakByHand(true).ticksRandomly().lightLevel(15).sounds(BlockSoundGroup.WOOD).dropsLike(AetherBlocks.AMBROSIUM_TORCH), new DustParticleEffect(1f, 0.67f, 0.392f, 0.7f));
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int max = random.nextInt(3) + 2;
        for(int i = 0; i <= max; i++) {
            Direction direction = ((Direction) state.get(FACING)).getOpposite();
            double d = 0.27D;
            double e = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.23D + 0.3D * (double) direction.getOffsetX();
            double f = (double) pos.getY() + 0.6D + (random.nextDouble() - 0.5D) * 0.25D + 0.22D;
            double g = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.23D + 0.3D * (double) direction.getOffsetZ();
            world.addParticle(this.particle, e, f, g, 0.0D, -4.0D, 0.0D);
        }
    }
}
