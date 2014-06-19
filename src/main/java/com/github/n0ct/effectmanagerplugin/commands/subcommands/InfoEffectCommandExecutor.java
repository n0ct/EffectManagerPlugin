package com.github.n0ct.effectmanagerplugin.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.commands.AbstractCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.EMCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.MessageSender;
import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;

/**
 * usage: /eminfo [<effectName>|<playerName>]
 * 
 * @author Benjamin
 *
 */
public class InfoEffectCommandExecutor extends AbstractCommandExecutor implements CommandExecutor {

	public static final String COMMAND = "info";

	public static final String FULL_COMMAND = "/" + EMCommandExecutor.COMMAND + COMMAND + " ";

	public static final String SHORT_COMMAND = "i";
	
	public InfoEffectCommandExecutor(EffectManagerPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {

		Player player = (Player)sender;
		if (!plugin.getCommandExecutor().isPlayerAllowed(player)) {
			MessageSender.sendErrorMessage(player, "You're not authorized to access to this command.");
			return true;
		}
		
		if(args.length > 1 || (args.length == 1 && args[0].equals("help"))) {
			showHelp(player, args);
			return true;
		}
		if (args.length == 1) {
			AbstractEffect effect = plugin.getEffectManager().get(args[0]);
			if (effect != null) {
				plugin.getCommandExecutor().getEffectCommandExecutor().effectDefinition(player, args, true);
				return true;
			}
			if (plugin.getEffectManager().getEffectClass(args[0]) != null) {
				MessageSender.sendInformationMessage(player, plugin.getEffectManager().getDefaultEffectForClass(args[0]).toString(true),false);
				return true;
			}
		}
		return plugin.getCommandExecutor().getPlayerEffectCommandExecutor().playerDetails(player, args);
		
	}

	public void showHelp(Player player, String[] newArgs) {
		MessageSender.sendErrorMessage(player, "Usage: /eminfo [<effectName>|<playerName>]");
	}

}
