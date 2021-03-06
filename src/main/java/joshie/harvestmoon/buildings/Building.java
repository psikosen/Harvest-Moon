package joshie.harvestmoon.buildings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import joshie.harvestmoon.buildings.placeable.Placeable;
import joshie.harvestmoon.buildings.placeable.Placeable.PlacementStage;
import joshie.harvestmoon.buildings.placeable.entities.PlaceableNPC;
import joshie.harvestmoon.core.util.BlockAccessPreview;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Building {
    //List of all placeable elements
    public HashMap<String, PlaceableNPC> npc_offsets = new HashMap();
    protected ArrayList<Placeable> list;
    private BlockAccessPreview preview;
    private BuildingGroup group;
    private int index;
    protected int offsetY;
    protected int tickTime = 20;

    public Building() {}

    public long getTickTime() {
        return tickTime;
    }

    public IBlockAccess getBlockAccess(int worldX, int worldY, int worldZ, boolean n1, boolean n2, boolean swap) {
        return preview.setCoordinatesAndDirection(worldX, worldY, worldZ, n1, n2, swap);
    }

    public BuildingGroup getGroup() {
        return group;
    }

    public int getInt() {
        return index;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void init(BuildingGroup group, int index) {
        preview = new BlockAccessPreview(this, list);
        this.index = index;
        this.group = group;
    }

    public int getSize() {
        return list.size();
    }

    public Placeable get(int index) {
        return list.get(index);
    }

    public ArrayList<Placeable> getList() {
        return list;
    }

    //TODO: LilyPad/Tripwire Hooks, Levers, Doors, Furnaces, Ladders, ItemFrame Loot????????????
    public boolean generate(UUID uuid, World world, int xCoord, int yCoord, int zCoord) {
        if (!world.isRemote) {
            boolean n1 = world.rand.nextBoolean();
            boolean n2 = world.rand.nextBoolean();
            boolean swap = world.rand.nextBoolean();

            /** First loop we place solid blocks **/
            for (Placeable block : list) {
                block.place(uuid, world, xCoord, yCoord, zCoord, n1, n2, swap, PlacementStage.BLOCKS);
            }

            /** Second loop we place entities etc. **/
            for (Placeable block : list) {
                block.place(uuid, world, xCoord, yCoord, zCoord, n1, n2, swap, PlacementStage.ENTITIES);
            }

            /** Third loop we place torch/ladders etc **/
            for (Placeable block : list) {
                block.place(uuid, world, xCoord, yCoord, zCoord, n1, n2, swap, PlacementStage.TORCHES);
            }

            /** Fourth loop we place NPCs **/
            for (Placeable block : list) {
                block.place(uuid, world, xCoord, yCoord, zCoord, n1, n2, swap, PlacementStage.NPC);
            }
        }

        return true;
    }
}
