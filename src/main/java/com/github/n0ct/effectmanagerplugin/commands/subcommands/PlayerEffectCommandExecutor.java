package com.github.n0ct.effectmanagerplugin.commands.subcommands;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.commands.AbstractCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.EMCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.MessageSender;
import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;

public class PlayerEffectCommandExecutor extends AbstractCommandExecutor implements CommandExecutor{

	public static final String COMMAND = "player";
	public static final String SHORT_COMMAND = "p";
	public static final String FULL_COMMAND = "/" + EMCommandExecutor.COMMAND + COMMAND + " ";
	private static final String PLAYER_ADD_CMD = "add";
	private static final String PLAYER_DEL_CMD = "del";
	private static final String PLAYER_DETAILS_CMD = "details";
	private static final String PLAYER_LIST_CMD = "list";
	private static final String PLAYER_HELP_CMD = "help";
	private static final String PLAYER_CLEAR_CMD = "clear";

	public PlayerEffectCommandExecutor(EffectManagerPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		String[] newArgs;
		Player player = (Player)sender;
		if (!plugin.getCommandExecutor().isPlayerAllowed(player)) {
			MessageSender.sendErrorMessage(player, "You're not authorized to access to this command.");
			return true;
		}
		if (args.length <= 1) {
			newArgs=new String[0];
		} else {
			newArgs = (String[])ArrayUtils.remove(args,0);
		}
		if (args.length >= 1) {
			if (args[0].equals(PLAYER_ADD_CMD)) {
				return playerAdd(player,newArgs);
			}
			if (args[0].equals(PLAYER_DEL_CMD)) {
				return playerDel(player,newArgs);
			}
			if (args[0].equals(PLAYER_CLEAR_CMD)) {
				return playerClear(player,newArgs);
			}
			if (args[0].equals("info") || args[0].equals(PLAYER_DETAILS_CMD)) {
				return playerDetails(player,newArgs);
			}
			if (args[0].equals(PLAYER_LIST_CMD)) {
				return playerList(player);
			}
			MessageSender.sendErrorMessage(player, "The command " + args[0] + " doesn't exist.");
		}
		showHelp(player,newArgs);
		return true;
	}

	private boolean playerClear(Player player, String[] newArgs) {
		if (newArgs.length < 1) {
			MessageSender.sendErrorMessage(player, "Usage: /emplayer clear <playerName>");
			return true;
		}
		UUID playerUUID = null;
		try {
			playerUUID = getPlayerUUID(newArgs[0],false);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			return true;
		}
		List<AbstractEffect> effects = plugin.getPlayerEffectManager().getEffectsForPlayer(playerUUID);
		
		if (effects.size() == 0) {
			MessageSender.sendErrorMessage(player, "Player "+ newArgs[0] +" doesn't have any effect.");
			return true;
		}
		
		plugin.getPlayerEffectManager().clear(playerUUID);
		MessageSender.sendSuccessMessage(player, "Effects cleared from " + newArgs[0] + ".");
		return true;
	}

	private boolean playerList(Player player) {
		if (plugin.getPlayerEffectManager().getPlayersEffects().size() == 0) {
			MessageSender.sendInformationMessage(player, "There is no effect associated to players.");
			return true;
		}
		MessageSender.sendInformationMessage(player, "Players effects:");
		for (UUID playerUUID : plugin.getPlayerEffectManager().getPlayersEffects().keySet()) {
			String playerName = getPlayerName(playerUUID);
			MessageSender.sendInformationMessage(player, playerName, false);
			for (AbstractEffect effect : plugin.getPlayerEffectManager().getEffectsForPlayer(playerUUID)) {
				MessageSender.sendInformationMessage(player, " |=> " + effect.getName(), false);
			}
		}
		return true;
	}


	public boolean playerDetails(Player player, String[] newArgs) {
		
		String targetPlayerName ="";
		if (newArgs.length < 1) {
			targetPlayerName = player.getName();
		} else {
			if (newArgs.length > 1 || (newArgs.length == 1 && newArgs[0] == "help")) {
				MessageSender.sendInformationMessage(player, "Usage: /emplayer details [<playerName>]");
				return true;
			} else {
				targetPlayerName = newArgs[0];
			}
		}
		UUID playerUUID = null;
		try {
			playerUUID = getPlayerUUID(targetPlayerName);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
		}
		
		List<AbstractEffect> effects = null;
		try {
			effects = plugin.getPlayerEffectManager().getEffectsForPlayer(playerUUID);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			return true;
		}
		
		if (effects.size() == 0) {
			MessageSender.sendInformationMessage(player,targetPlayerName +" doesn't have any effect.");
			return true;
		} else {
			Iterator<AbstractEffect> it =effects.iterator();
			StringBuilder sb=new StringBuilder();;
			sb.append(targetPlayerName).append(" have the following effects: ");
			for (;it.hasNext();) {
				sb.append("\n                  |\n");
				AbstractEffect effect = it.next();
				if (it.hasNext()) {
					sb.append("                  |=> ");
				} else {
					sb.append("                  \\=> ");
				}
				sb.append(effect.getName());
			}
			MessageSender.sendInformationMessage(player, sb.toString());
		}
		return true;
	}

	private boolean playerDel(Player player, String[] newArgs) {
		if (newArgs.length < 2) {
			MessageSender.sendErrorMessage(player, "Usage: /emplayer del <playerName> <effectName>");
			return true;
		}

		UUID playerUUID = null;
		try {
			playerUUID = getPlayerUUID(newArgs[0]);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			return true;
		}
		List<AbstractEffect> effects=null;
		try {
			effects = plugin.getPlayerEffectManager().getEffectsForPlayer(playerUUID);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			return true;
		}
		
		if (effects.size() == 0) {
			MessageSender.sendErrorMessage(player, "Player "+ newArgs[0] +" doesn't have any effect. Cannot delete the effect " + newArgs[1] + ".");
			return true;
		}
		try {
			plugin.getPlayerEffectManager().del(playerUUID, newArgs[1]);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			return true;
		}
		MessageSender.sendSuccessMessage(player, "Effect "+newArgs[1]+" deleted from player " + newArgs[0] + ".");
		return true;
	}

	private boolean playerAdd(Player player, String[] newArgs) {
		if (newArgs.length < 2) {
			MessageSender.sendErrorMessage(player, "Usage: /emplayer add <playerName> <effectName>");
			showEffectList(player);
			return true;
		}
		

		UUID playerUUID = null;
		try {
			playerUUID = getPlayerUUID(newArgs[0]);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			showEffectList(player);
			return true;
		}
		
		try {
			plugin.getPlayerEffectManager().add(playerUUID, newArgs[1]);
		} catch(IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			showEffectList(player);
			return true;
		}
		
		MessageSender.sendSuccessMessage(player, "Effect " + newArgs[1] + " added to player " + newArgs[0] + "." );
		return true;
	}
	
	public boolean showEffectList(Player player) {
		if (this.plugin.getEffectManager().getAll().size() > 0) {
			MessageSender.sendInformationMessage(player, "Available effects:");
			StringBuilder sb = new StringBuilder();
			for(String effect : this.plugin.getEffectManager().getAll()) {
				sb.append(effect + " ");
			}
			MessageSender.sendInformationMessage(player, sb.toString());
			return true;
		}
		MessageSender.sendInformationMessage(player, "No Available effects.");
		MessageSender.sendInformationMessage(player, "First you have to use \'/em effect add\' command to create new effects before to apply them on players.");
		return false;
	}

	public void showHelp(Player player, String[] newArgs) {
		MessageSender.sendInformationMessage(player, "Usage: "+FULL_COMMAND + PLAYER_ADD_CMD + "|" + PLAYER_DEL_CMD + "|" + PLAYER_DETAILS_CMD + "|" + PLAYER_LIST_CMD + "|" + PLAYER_HELP_CMD);
	}

}
