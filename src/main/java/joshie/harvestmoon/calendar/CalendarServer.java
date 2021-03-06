package joshie.harvestmoon.calendar;

import static joshie.harvestmoon.core.helpers.ServerHelper.markDirty;
import static joshie.harvestmoon.core.network.PacketHandler.sendToEveryone;

import java.util.List;

import joshie.harvestmoon.core.config.Calendar;
import joshie.harvestmoon.core.helpers.AnimalHelper;
import joshie.harvestmoon.core.helpers.CropHelper;
import joshie.harvestmoon.core.helpers.MineHelper;
import joshie.harvestmoon.core.helpers.PlayerHelper;
import joshie.harvestmoon.core.network.PacketSetCalendar;
import joshie.harvestmoon.core.util.IData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CalendarServer implements IData {
    private boolean loaded = false;

    private CalendarDate date = new CalendarDate(0, Season.SPRING, 1);

    public CalendarDate getDate() {
        return date;
    }

    public void setDate(int day, Season season, int year) {
        date.setDay(day).setSeason(season).setYear(year);
        markDirty();
    }

    //Increases the day
    public boolean newDay() {
        int day = date.getDay();
        Season season = date.getSeason();
        int year = date.getYear();

        if (day < Calendar.DAYS_PER_SEASON) {
            day++;
        } else {
            season = getNextSeason();
            day = 1;
            if (season == Season.SPRING) {
                year++;
            }
        }

        date.setDay(day).setSeason(season).setYear(year);
        sendToEveryone(new PacketSetCalendar(date));

        CropHelper.newDay();
        AnimalHelper.newDay();
        MineHelper.newDay();

        //Loop through all the players and do stuff related to them, Pass the world that the player is in
        for (EntityPlayer player : (List<EntityPlayer>) MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            PlayerHelper.getData(player).newDay();
        }

        loaded = true;
        markDirty();
        return true;
    }

    //Returns the season after the present one
    public Season getNextSeason() {
        return date.getSeason().ordinal() < Season.values().length - 1 ? Season.values()[date.getSeason().ordinal() + 1] : Season.values()[0];
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        date.readFromNBT(nbt);
        loaded = nbt.getBoolean("Loaded");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        date.writeToNBT(nbt);
        nbt.setBoolean("Loaded", true);
    }
}
