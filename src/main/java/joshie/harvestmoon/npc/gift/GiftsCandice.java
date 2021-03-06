package joshie.harvestmoon.npc.gift;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GiftsCandice extends Gifts {
    @Override
    public Quality getQuality(ItemStack stack) {
        if (stack.getItem() == Items.milk_bucket) {
            return Quality.AWESOME;
        }

        if (is(stack, Category.ANIMALS)) {
            return Quality.GOOD;
        }

        if (is(stack, Category.COOKING)) {
            return Quality.BAD;
        }

        return Quality.DECENT;
    }
}
