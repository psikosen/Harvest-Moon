package joshie.harvestmoon.core.helpers;

import java.util.Random;

import joshie.harvestmoon.api.core.ISizeable.Size;
import joshie.harvestmoon.core.lib.SizeableMeta;
import joshie.harvestmoon.init.HMConfiguration;
import joshie.harvestmoon.init.HMItems;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SizeableHelper {
    private static final Random rand = new Random();

    public static ItemStack getEgg(EntityPlayer player, EntityAnimal animal) {
        Size size = Size.SMALL;
        int relationship = RelationsHelper.getRelationshipValue(animal, player);
        int chance = Math.max(1, RelationsHelper.ADJUSTED_MAX - relationship);
        int chance2 = Math.max(1, chance / 3);
        if (rand.nextInt(chance) == 0) size = Size.LARGE;
        else if (rand.nextInt(chance2) == 0) size = Size.MEDIUM;
        ItemStack sizeable = getSizeable(relationship, SizeableMeta.EGG, size);
        if (HMConfiguration.vanilla.EGG_OVERRIDE) {
            return new ItemStack(Items.egg, 1, sizeable.getItemDamage());
        } else return sizeable;
    }

    public static ItemStack getSizeable(EntityPlayer player, EntityAnimal animal, SizeableMeta meta, Size size) {
        int relationship = RelationsHelper.getRelationshipValue(animal, player);
        return getSizeable(relationship, meta, size);
    }

    public static ItemStack getSizeable(int relationship, SizeableMeta meta, Size size) {
        double quality = 1D + (relationship / RelationsHelper.ADJUSTED_MAX) * 99;
        return new ItemStack(HMItems.sized.get(meta), 1, getDamage((int) quality, size));
    }

    /** Converts the meta, quality and size of a product to the proper meta data **/
    public static int getDamage(int quality, Size size) {
        int sizeableSize = getSize(size);
        int sizeableQuality = ((quality - 1) * 100);
        return sizeableSize + sizeableQuality;
    }

    /** @return returns the Quality of this sizeable, Returns a vlue between 0-109 **/
    public static int getQuality(int meta) {
        return (int) Math.ceil(getInternalMeta(meta) / 100);
    }

    public static Size getSize(int meta) {
        int size = Math.max(0, meta / 10000);
        return Size.values()[Math.min(2, size)];
    }

    //Internal Meta value
    private static int getInternalMeta(int meta) {
        return meta % 10000;
    }

    /** Returns the stack that this size should return **/
    private static int getSize(Size size) {
        return size.ordinal() * 10000;
    }
}
