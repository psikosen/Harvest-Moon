package joshie.harvestmoon.crops.icons;

import joshie.harvestmoon.core.lib.HMModInfo;
import joshie.harvestmoon.init.HMCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IconHandlerCorn extends AbstractIconHandler {
    @SideOnly(Side.CLIENT)
    public IIcon getIconForStage(PlantSection section, int stage) {
        if (stage <= 3) return stageIcons[0];
        else if (stage <= 7) return stageIcons[1];
        else if (stage <= 11) return stageIcons[2];
        else if (stage <= 14) return stageIcons[3];
        else return stageIcons[4];
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        stageIcons = new IIcon[5];
        for (int i = 0; i < stageIcons.length; i++) {
            stageIcons[i] = register.registerIcon(HMModInfo.CROPPATH + HMCrops.corn.getUnlocalizedName() + "_" + (i + 1));
        }
    }
}
