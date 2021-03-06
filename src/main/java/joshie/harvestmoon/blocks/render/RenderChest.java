package joshie.harvestmoon.blocks.render;

import joshie.harvestmoon.blocks.BlockIcons;
import joshie.harvestmoon.core.util.RenderBase;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderChest extends RenderBase {
    @Override
    public void renderBlock() {
        setTexture(BlockIcons.COUNTER_SIDE);
        if (dir == ForgeDirection.WEST || isItem()) {
            setTexture(BlockIcons.COUNTER_FRONT);
            renderBlock(0.09D, 0D, 0D, 0.1D, 0.95D, 1D);
        } else if (dir == ForgeDirection.NORTH) {
            setTexture(BlockIcons.COUNTER_FRONT);
            renderBlock(0D, 0D, 0.09D, 1D, 0.95D, 0.1D);
        } else if (dir == ForgeDirection.EAST) {
            setTexture(BlockIcons.COUNTER_FRONT);
            renderBlock(0.9D, 0D, 0D, 0.91D, 0.95D, 1D);
        } else if (dir == ForgeDirection.SOUTH) {
            setTexture(BlockIcons.COUNTER_FRONT);
            renderBlock(0D, 0D, 0.9D, 1D, 0.95D, 0.91D);
        }
    }
}