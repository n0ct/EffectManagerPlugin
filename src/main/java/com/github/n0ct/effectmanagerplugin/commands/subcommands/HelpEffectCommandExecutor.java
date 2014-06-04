package com.github.n0ct.effectmanagerplugin.commands.subcommands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.commands.AbstractCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.EMCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.MessageSender;

public class HelpEffectCommandExecutor extends AbstractCommandExecutor implements CommandExecutor {

	public static final String COMMAND = "help";

	public static final String FULL_COMMAND = "/" + EMCommandExecutor.COMMAND + COMMAND + " ";
	
	public HelpEffectCommandExecutor(EffectManagerPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		Player player = (Player)sender;
		if (!plugin.getCommandExecutor().isPlayerAllowed(player)) {
			MessageSender.sendErrorMessage(player, "You're not authorized to access to this command.");
			return true;
		}
		String[] newArgs;
		
		if (args.length <= 1) {
			newArgs=new String[0];
		} else {
			newArgs = (String[])ArrayUtils.remove(args,0);
		}
		if (args.length >= 1) {
			if (args[0].equals(CallEffectCommandExecutor.COMMAND)) {
				plugin.getCommandExecutor().getCallEffectCommandExecutor().showHelp(player,newArgs);
				return true;
			}
			if (args[0].equals(EffectCommandExecutor.COMMAND)) {
				plugin.getCommandExecutor().getEffectCommandExecutor().showHelp(player,newArgs);
				return true;
			}
			if (args[0].equals(HelpEffectCommandExecutor.COMMAND)) {
				plugin.getCommandExecutor().getHelpEffectCommandExecutor().showHelp(player,newArgs);
				return true;
			}
			if (args[0].equals(InfoEffectCommandExecutor.COMMAND)) {
				plugin.getCommandExecutor().getInfoEffectCommandExecutor().showHelp(player,newArgs);
				return true;
			}
			if (args[0].equals(ListEffectCommandExecutor.COMMAND)) {
				plugin.getCommandExecutor().getListEffectCommandExecutor().showHelp(player,newArgs);
				return true;
			}
			if (args[0].equals(PlayerEffectCommandExecutor.COMMAND)) {
				plugin.getCommandExecutor().getPlayerEffectCommandExecutor().showHelp(player,newArgs);
				return true;
			}
			if (args[0].equals(EMCommandExecutor.COMMAND)) {
				plugin.getCommandExecutor().showHelp(player,newArgs);
				return true;
				
			}
			MessageSender.sendErrorMessage(player, "The command /em " + args[0] + " doesn't exist.");
		}
		showHelp(player,newArgs);
		return true;
	}

	private void showHelp(Player player, String[] newArgs) {
		MessageSender.sendInformationMessage(player, "Usage: "+FULL_COMMAND+"<command>");
		
	}

}
