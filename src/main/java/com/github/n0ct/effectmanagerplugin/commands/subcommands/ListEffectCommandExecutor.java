package com.github.n0ct.effectmanagerplugin.commands.subcommands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.commands.AbstractCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.EMCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.MessageSender;
/**
 * 
 * description: Retrieves a list of effect or a list of effect applied to a player
 * usage: /emlist [player]
 *       
 * @author Benjamin
 *
 */
public class ListEffectCommandExecutor extends AbstractCommandExecutor implements CommandExecutor {

	public static final String COMMAND = "list";
	
	public static final String FULL_COMMAND = "/" + EMCommandExecutor.COMMAND + COMMAND + " ";

	public static final String SHORT_COMMAND = "l";

	public ListEffectCommandExecutor(EffectManagerPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		Player player = (Player)sender;
		if (!plugin.getCommandExecutor().isPlayerAllowed(player)) {
			MessageSender.sendErrorMessage(player, "You're not authorized to access to this command.");
			return true;
		}
		if (args.length > 1) {
			MessageSender.sendErrorMessage(player, FULL_COMMAND +  StringUtils.join(args," "));
			showHelp(player,args);
			return true;
		}
		plugin.getCommandExecutor().getPlayerEffectCommandExecutor().playerDetails(player, args);
		return true;
	}

	/**
	 * @param player
	 * @param newArgs 
	 */
	public void showHelp(Player player, String[] newArgs) {
		MessageSender.sendErrorMessage(player, "Usage: "+FULL_COMMAND+"[player]");
	}

}
