package joshie.harvestmoon.core.handlers.api;

import joshie.harvestmoon.api.Calendar.ISeason;
import joshie.harvestmoon.api.crops.ICrop;
import joshie.harvestmoon.api.crops.ICropData;
import joshie.harvestmoon.api.crops.ICropHandler;
import joshie.harvestmoon.api.crops.ICropRenderHandler.PlantSection;
import joshie.harvestmoon.blocks.BlockCrop;
import joshie.harvestmoon.core.helpers.CropHelper;
import joshie.harvestmoon.core.helpers.SeasonHelper;
import joshie.harvestmoon.crops.Crop;
import joshie.harvestmoon.init.HMConfiguration;
import net.minecraft.world.World;

public class CropRegistry implements ICropHandler {
    @Override
    public ICrop getCrop(String unlocalized) {
        return HMConfiguration.mappings.getCrop(unlocalized);
    }

    @Override
    public ICropData getCropAtLocation(World world, int x, int y, int z) {
        PlantSection section = BlockCrop.getSection(world, x, y, z);
        return section == PlantSection.BOTTOM ? CropHelper.getCropAtLocation(world, x, y, z) : CropHelper.getCropAtLocation(world, x, y - 1, z);
    }

    @Override
    public ICrop registerCrop(String unlocalized, int cost, int sell, int stages, int regrow, int year, int color, ISeason... seasons) {
        return new Crop(unlocalized, SeasonHelper.getSeasonsFromISeasons(seasons), cost, sell, stages, regrow, year, color);
    }

    @Override
    public ICrop registerCrop(ICrop crop) {
        return crop;
    }
}
