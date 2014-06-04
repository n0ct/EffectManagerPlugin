package com.github.n0ct.effectmanagerplugin.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.commands.AbstractCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.EMCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.MessageSender;
import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;

public class CallEffectCommandExecutor extends AbstractCommandExecutor implements CommandExecutor {

	public static final String COMMAND = "call";

	public static final String FULL_COMMAND = "/" + EMCommandExecutor.COMMAND + COMMAND + " ";
	
	public CallEffectCommandExecutor(EffectManagerPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (!plugin.getCommandExecutor().isPlayerAllowed(player)) {
			MessageSender.sendErrorMessage(player, "You're not authorized to access to this command.");
			return true;
		}
		String targetPlayerName = null;
		String effectName = null;
		if (args.length < 1) {
			targetPlayerName = player.getName();
		}
		if (args.length > 2 || args[0] == "help") {
			showHelp(player,new String[0]);
			return true;
		}
		if (args.length == 2) {
			targetPlayerName = args[0];
			effectName = args[1];
		}
		if (args.length == 1) {
			if (plugin.getEffectManager().get(args[0]) == null) {
				targetPlayerName = args[0];
			} else {
				targetPlayerName = player.getName();
				effectName = args[0];
			}
		}

		List<AbstractEffect> playerEffects = plugin.getPlayerEffectManager().getEffectsForPlayer(targetPlayerName);
		List<AbstractEffect> effectsToCall = new ArrayList<AbstractEffect>();
		if (effectName != null) {
			for (AbstractEffect ceffect : playerEffects) {
				if (ceffect.getName().equals(effectName)) {
					if (!ceffect.isCallable()) {
						MessageSender.sendErrorMessage(player, "Effect " + effectName + " is not callable.");
					}
					effectsToCall.add(ceffect);
					break;
				}
			}
			if (effectsToCall.size() == 0) {
				MessageSender.sendErrorMessage(player, "Effect " + effectName + " doesn't exist.");
				return true;
			}
		} else {
			for (AbstractEffect ceffect : playerEffects) {
				if (ceffect.isCallable()) {
					effectsToCall.add(ceffect);
				}
			}
			if (effectsToCall.size() == 0) {
				MessageSender.sendErrorMessage(player, "The player " + targetPlayerName + " has no callable effect.");
				return true;
			}
		}
		try {
			plugin.getPlayerEffectManager().callEffectsForPlayer(targetPlayerName, effectsToCall);
		} catch (IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

	public void showHelp(Player player, String[] newArgs) {
		MessageSender.sendInformationMessage(player, "Usage: /emcall [<playerName> <effectName> | ( <playerName> | <effectName> )]");
	}

}
