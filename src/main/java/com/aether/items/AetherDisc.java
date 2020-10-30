package com.aether.items;

import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Rarity;

public class AetherDisc extends MusicDiscItem {
    public AetherDisc(int comparatorValueIn, SoundEvent soundIn) {
        super(comparatorValueIn, soundIn, new Settings().maxCount(1).group(AetherItemGroups.MISC).rarity(Rarity.RARE));
    }
}