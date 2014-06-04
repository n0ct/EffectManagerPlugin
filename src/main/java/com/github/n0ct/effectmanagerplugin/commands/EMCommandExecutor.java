/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.commands.subcommands.CallEffectCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.subcommands.EffectCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.subcommands.HelpEffectCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.subcommands.InfoEffectCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.subcommands.ListEffectCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.subcommands.PlayerEffectCommandExecutor;

/**
 * @author Benjamin
 *
 */
public class EMCommandExecutor implements CommandExecutor {

	private EffectCommandExecutor effectCommandExecutor;
	
	private PlayerEffectCommandExecutor playerEffectCommandExecutor;
	
	private InfoEffectCommandExecutor infoEffectCommandExecutor;

	private CallEffectCommandExecutor callEffectCommandExecutor;
	
	private HelpEffectCommandExecutor helpEffectCommandExecutor;

	private ListEffectCommandExecutor listEffectCommandExecutor;
	
	public static final String COMMAND = "em";
	
	public static final String FULL_COMMAND = "/" + COMMAND + " ";

	private static final Object LOAD_CONFIG_COMMAND = "load";

	private static final Object SAVE_CONFIG_COMMAND = "save";
	
	private EffectManagerPlugin plugin;
	
	public EMCommandExecutor(EffectManagerPlugin plugin) {
		
		this.plugin = plugin;
		
		effectCommandExecutor = new EffectCommandExecutor(plugin);
		
		playerEffectCommandExecutor = new PlayerEffectCommandExecutor(plugin);
		
		infoEffectCommandExecutor = new InfoEffectCommandExecutor(plugin);
		
		callEffectCommandExecutor = new CallEffectCommandExecutor(plugin);
		
		helpEffectCommandExecutor = new HelpEffectCommandExecutor(plugin);
		
		listEffectCommandExecutor = new ListEffectCommandExecutor(plugin);
		
		plugin.getCommand(COMMAND).setExecutor(this);
		plugin.getCommand(COMMAND + InfoEffectCommandExecutor.COMMAND).setExecutor(this.infoEffectCommandExecutor);
		plugin.getCommand(COMMAND + PlayerEffectCommandExecutor.COMMAND).setExecutor(this.playerEffectCommandExecutor);
		plugin.getCommand(COMMAND + EffectCommandExecutor.COMMAND).setExecutor(this.effectCommandExecutor);
		plugin.getCommand(COMMAND + CallEffectCommandExecutor.COMMAND).setExecutor(this.callEffectCommandExecutor);
		plugin.getCommand(COMMAND + HelpEffectCommandExecutor.COMMAND).setExecutor(this.helpEffectCommandExecutor);
		plugin.getCommand(COMMAND + ListEffectCommandExecutor.COMMAND).setExecutor(this.listEffectCommandExecutor);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player)sender;
		if (!isPlayerAllowed(player)) {
			MessageSender.sendErrorMessage(player, "You're not authorized to access to this command.");
			return true;
		}
		String[] newArgs;
		if (args.length <= 1) {
			newArgs=new String[0];
		} else {
			newArgs = (String[])ArrayUtils.remove(args,0);
		}
		if (args.length >= 1 && args[0].equals(InfoEffectCommandExecutor.COMMAND)) {
			return infoEffectCommandExecutor.onCommand(player, command, label, newArgs);
		}
		if (args.length >= 1 && args[0].equals(PlayerEffectCommandExecutor.COMMAND)) {
			return playerEffectCommandExecutor.onCommand(player, command, label, newArgs);
		}
		if (args.length >= 1 && args[0].equals(EffectCommandExecutor.COMMAND)) {
			return effectCommandExecutor.onCommand(player, command, label, newArgs);
		}
		if (args.length >= 1 && args[0].equals(CallEffectCommandExecutor.COMMAND)) {
			return callEffectCommandExecutor.onCommand(player, command, label, newArgs);
		}
		if (args.length >= 1 && args[0].equals(HelpEffectCommandExecutor.COMMAND)) {
			return helpEffectCommandExecutor.onCommand(player, command, label, newArgs);
		}
		if (args.length >= 1 && args[0].equals(ListEffectCommandExecutor.COMMAND)) {
			return listEffectCommandExecutor.onCommand(player, command, label, newArgs);
		}
		if (args.length >= 1 && args[0].equals(LOAD_CONFIG_COMMAND)) {
			this.plugin.getConfigManager().loadConfig();
			MessageSender.sendSuccessMessage(player, "Loaded configuration from file.");
			return true;
		}
		if (args.length >= 1 && args[0].equals(SAVE_CONFIG_COMMAND)) {
			this.plugin.getConfigManager().saveConfig();
			MessageSender.sendSuccessMessage(player, "Saved configuration to file.");
			return true;
		}
		if (args.length >= 1) {
			MessageSender.sendErrorMessage(player, "The command " + args[0] + "doesn't exist.");
			showHelp(player, newArgs);
			return true;
		}
		if (args.length == 0) {
			showHelp(player, newArgs);
			return true;
		}
		return helpEffectCommandExecutor.onCommand(player, command, label, newArgs);
	}
	
	public EffectCommandExecutor getEffectCommandExecutor() {
		return effectCommandExecutor;
	}

	public PlayerEffectCommandExecutor getPlayerEffectCommandExecutor() {
		return playerEffectCommandExecutor;
	}

	public InfoEffectCommandExecutor getInfoEffectCommandExecutor() {
		return infoEffectCommandExecutor;
	}

	public CallEffectCommandExecutor getCallEffectCommandExecutor() {
		return callEffectCommandExecutor;
	}

	public HelpEffectCommandExecutor getHelpEffectCommandExecutor() {
		return helpEffectCommandExecutor;
	}

	public ListEffectCommandExecutor getListEffectCommandExecutor() {
		return listEffectCommandExecutor;
	}

	public static String getEmCommand() {
		return COMMAND;
	}

	public boolean isPlayerAllowed(Player player) {
		if(!player.isOp()) {
			return false;
		}
		return true;
	}

	public void showHelp(Player player, String[] newArgs) {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage: ").append(FULL_COMMAND);
		sb.append(EffectCommandExecutor.COMMAND).append("|");
		sb.append(PlayerEffectCommandExecutor.COMMAND).append("|");
		sb.append(CallEffectCommandExecutor.COMMAND).append("|");
		sb.append(InfoEffectCommandExecutor.COMMAND).append("|");
		sb.append(ListEffectCommandExecutor.COMMAND).append("|");
		sb.append(HelpEffectCommandExecutor.COMMAND).append("|");
		sb.append(SAVE_CONFIG_COMMAND).append("|");
		sb.append(LOAD_CONFIG_COMMAND);
		
		MessageSender.sendInformationMessage(player, sb.toString());
				
		
	}

}
