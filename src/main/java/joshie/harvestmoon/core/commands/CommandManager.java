package joshie.harvestmoon.core.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joshie.harvestmoon.api.HMApi;
import joshie.harvestmoon.api.commands.IHMCommand;
import joshie.harvestmoon.api.commands.IHMCommandHandler;
import joshie.harvestmoon.core.lib.HMModInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.CommandEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommandManager extends CommandBase implements IHMCommandHandler {
    public static final IHMCommandHandler INSTANCE = new CommandManager();
    private HashMap<String, IHMCommand> commands = new HashMap();

    @Override
    public void registerCommand(IHMCommand command) {
        commands.put(command.getCommandName(), command);
    }

    @Override
    public Map getCommands() {
        return commands;
    }

    @Override
    public List getPossibleCommands(ICommandSender sender) {
        ArrayList list = new ArrayList();
        for (IHMCommand command: commands.values()) {
            if (sender.canCommandSenderUseCommand(command.getPermissionLevel().ordinal(), command.getCommandName())) {
                list.add(command);
            }
        }
        
        return list;
    }

    @Override
    public String getCommandName() {
        return HMModInfo.COMMANDNAME;
    }

    @SubscribeEvent
    public void onCommandSend(CommandEvent event) {
        if (event.command == this && event.parameters.length > 0) {
            String commandName = event.parameters[0];
            IHMCommand command = commands.get(commandName);
            if (command == null || !event.sender.canCommandSenderUseCommand(command.getPermissionLevel().ordinal(), commandName)) {
                event.setCanceled(true);
            } else {
                processCommand(event, command);
            }
        }
    }

    //Attempt to process the command, throw wrong usage otherwise
    private void processCommand(CommandEvent event, IHMCommand command) {
        String[] args = new String[event.parameters.length - 1];
        System.arraycopy(event.parameters, 1, args, 0, args.length);
        if (!command.processCommand(event.sender, args)) {
            throwError(event.sender, command);
        }
    }
    
    static void throwError(ICommandSender sender, IHMCommand command) {
        ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(getUsage(command), new Object[0]);
        chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
        sender.addChatMessage(chatcomponenttranslation1);
    }
    
    static String getUsage(IHMCommand command) {
        return "/" + HMApi.COMMANDS.getCommandName() + " " + command.getCommandName() + " " + command.getUsage();
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] parameters) {
        return new ArrayList();
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] values) {
        if(values.length == 0) {
            throwError(sender, new HMCommandHelp());
        }
    } //Do sweet nothing

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
