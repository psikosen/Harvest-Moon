package joshie.harvestmoon.core.network;

import static joshie.harvestmoon.core.helpers.CalendarHelper.getSeason;
import static joshie.harvestmoon.core.helpers.CalendarHelper.setDate;
import io.netty.buffer.ByteBuf;
import joshie.harvestmoon.calendar.CalendarDate;
import joshie.harvestmoon.calendar.Season;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetCalendar implements IMessage, IMessageHandler<PacketSetCalendar, IMessage> {
    private int day;
    private Season season;
    private int year;
    
    public PacketSetCalendar() {}
    public PacketSetCalendar(CalendarDate date) {
        this.day = date.getDay();
        this.season = date.getSeason();
        this.year = date.getYear();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(day);
        buf.writeByte(season.ordinal());
        buf.writeShort(year);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        day = buf.readByte();
        season = Season.values()[buf.readByte()];
        year = buf.readShort();
    }
    
    @Override
    public IMessage onMessage(PacketSetCalendar message, MessageContext ctx) {        
        Season previous = getSeason();
        setDate(message.day, message.season, message.year);
        
        //Refresh all Blocks in Render range
        //If the seasons are not the same, and neither the current or past season is/was spring, re-render the client
        if(previous != message.season && previous != Season.SPRING && message.season != Season.SPRING) {
            joshie.harvestmoon.core.helpers.generic.MCClientHelper.refresh();
        }

        return null;
    }
}