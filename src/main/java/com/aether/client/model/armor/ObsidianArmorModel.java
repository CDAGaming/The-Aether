package com.aether.client.model.armor;

import com.aether.Aether;
import com.aether.items.armor.ObsidianArmor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ObsidianArmorModel extends AnimatedGeoModel<ObsidianArmor> {

    @Override
    public Identifier getModelLocation(ObsidianArmor object) {
        return new Identifier(Aether.MOD_ID, "geo/obsidian_armor.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ObsidianArmor object) {
        return new Identifier(Aether.MOD_ID, "textures/armor/obsidian_armor.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ObsidianArmor animatable) {
        return new Identifier(Aether.MOD_ID, "animations/armor/obsidian_armor.animation");
    }
}