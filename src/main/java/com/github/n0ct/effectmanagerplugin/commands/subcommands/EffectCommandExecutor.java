package com.github.n0ct.effectmanagerplugin.commands.subcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.commands.AbstractCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.EMCommandExecutor;
import com.github.n0ct.effectmanagerplugin.commands.MessageSender;
import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;

public class EffectCommandExecutor extends AbstractCommandExecutor implements CommandExecutor {

	public static final String COMMAND = "effect";
	public static final String FULL_COMMAND = "/" + EMCommandExecutor.COMMAND + COMMAND + " ";
	
	private static final String EFFECT_CLASSES_CMD = "classes";
	private static final String EFFECT_HELP_CMD = "help";
	private static final String EFFECT_LIST_CMD = "list";
	private static final String EFFECT_DETAILS_CMD = "details";
	private static final String EFFECT_DEL_CMD = "del";
	private static final String EFFECT_ADD_CMD = "add";
	private static final Object EFFECT_PARAM_CMD = "params";

	public EffectCommandExecutor(EffectManagerPlugin plugin) {
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
			if (args[0].equals(EFFECT_ADD_CMD)) {
				return effectAdd(player,newArgs);
			}
			if (args[0].equals(EFFECT_DEL_CMD)) {
				return effectDel(player,newArgs);
			}
			if (args[0].equals("definition") || args[0].equals(EFFECT_DETAILS_CMD)) {
				return effectDefinition(player,newArgs,true);
			}
			if (args[0].equals(EFFECT_PARAM_CMD)) {
				return showEffectClassParameters(player,newArgs);
			}
			if (args[0].equals(EFFECT_LIST_CMD)) {
				return effectList(player);
			}
			if (args[0].equals(EFFECT_HELP_CMD)) {
				showHelp(player,newArgs);
				return true;
			}
			if (args[0].equals("class") || args[0].equals(EFFECT_CLASSES_CMD)) {
				return showEffectClasses(player);
			}
			MessageSender.sendErrorMessage(player, "The command /emeffect " + args[0] + " doesn't exist.");
		}
		showHelp(player, newArgs);
		return true;
	}

	private boolean showEffectClassParameters(Player player, String[] newArgs) {
		if(newArgs.length < 1 || newArgs.length > 1) {
			MessageSender.sendErrorMessage(player, "Usage: /emeffect params <effectClass>");
			return true;
		}
		showEffectClassParameters(player,newArgs[0]);
		return true;
		
	}

	private boolean showEffectClasses(Player player) {
		StringBuilder sb = new StringBuilder();
		sb.append("Availables Effect Class Names: ");
		int i = 1;
		for (String className : plugin.getEffectManager().getEffectClassesNames()) {
			String[] classNameArray = className.split("\\.");
			sb.append(classNameArray[classNameArray.length-1]);
			if (i != plugin.getEffectManager().getEffectClassesNames().size()) {
				sb.append(", ");
			}
			i++;
		}
		sb.append(".");
		MessageSender.sendInformationMessage(player, sb.toString());
		return true;
	}

	private boolean effectList(Player player) {
		if (this.plugin.getEffectManager().getAll().size() > 0) {
			MessageSender.sendInformationMessage(player, "Available effects:");
			StringBuilder sb = new StringBuilder();
			for(String effect : this.plugin.getEffectManager().getAll()) {
				sb.append(effect + " ");
			}
			MessageSender.sendInformationMessage(player, sb.toString());
		} else {
			MessageSender.sendInformationMessage(player, "No Available effects.");
		}
		return true;
	}

	public boolean effectDefinition(Player player, String[] newArgs, boolean showPrefix) {
		if (!checkEffectExists(player, newArgs[0])) {
			MessageSender.sendErrorMessage(player, "The effect " + newArgs[0] + " doesn't exist.");
			return true;
		}

		showEffect(player, newArgs[0],showPrefix,true);
		return true;
	}

	/**
	 * @param sender
	 * @param name
	 */
	private void showEffect(Player player, String effectName,boolean showPrefix,boolean showParameters) {
		if (showPrefix) {
			MessageSender.sendInformationMessage(player, " ",showPrefix);
		}
		MessageSender.sendInformationMessage(player, plugin.getEffectManager().get(effectName).toString(true),false);
	}
	
	private void showEffectClassParameters(Player player, String name) {
		if (!plugin.getEffectManager().effectClassExists(name)) {
			MessageSender.sendErrorMessage(player,"effect class " + name + " doesn't exist.");
			return;
		}
		MessageSender.sendInformationMessage(player, plugin.getEffectManager().getDefaultEffectForClass(name).toString(true),false);
	}

	private boolean effectDel(Player player, String[] newArgs) {
		if (newArgs.length == 1) {
			if (!checkEffectExists(player, newArgs[0])) {
				MessageSender.sendErrorMessage(player, "The effect " + newArgs[0] + " doesn't exist so it cannot be deleted.");
				return true;
			}
			try {
				plugin.getEffectManager().remove(newArgs[0]);
			} catch (Exception e) {
				MessageSender.sendErrorMessage(player, e.getMessage());
				return true;
			}
			MessageSender.sendSuccessMessage(player, "Effect deleted.");
		} else {
			MessageSender.sendInformationMessage(player, "Usage /emeffect del <effectName>");
		}
		return true;
	}

	private boolean checkEffectExists(Player player, String effectName) {
		if (StringUtils.isBlank(effectName) || !plugin.getEffectManager().contains(effectName)) {
			return false;
		}
		return true;
	}

	private boolean effectAdd(Player player, String[] newArgs) {
		if (newArgs.length < 3) {
			MessageSender.sendErrorMessage(player, "Incorrect call of add effect method: You must at least specify a name, the effectClassName and then parameters.");
			MessageSender.sendInformationMessage(player, "Usage: /emeffect add <effectName> <effectClass> <parameter> [<moreParameters> ...]");
			showEffectClasses(player);
			if (newArgs.length >=2) {
				showEffectClassParameters(player, newArgs[1]);
			}
			return true;
		}
		if (newArgs[0].length() < 3) {
			MessageSender.sendErrorMessage(player,"An effect name must contain at least 3 characters.");
			return true;
		}
		if (plugin.getEffectManager().contains(newArgs[0])) {
			MessageSender.sendErrorMessage(player,"The effect "+newArgs[0]+" already exists.");
			return true;
		}

		if (!plugin.getEffectManager().effectClassExists(newArgs[1])) {
			MessageSender.sendErrorMessage(player, "The effectClassName " + newArgs[1] + " doesn't exist.");
			this.showEffectClasses(player);
			return true;
		}
		AbstractEffect effect = null;
		try {
			effect = plugin.getEffectManager().createEffect(newArgs[0], newArgs[1], StringUtils.join(ArrayUtils.subarray(newArgs, 2, newArgs.length)," "));
		} catch(IllegalArgumentException e) {
			MessageSender.sendErrorMessage(player, e.getMessage());
			return true;
		}
		MessageSender.sendSuccessMessage(player,"The effect " +  effect.getName() + " has been created with the following parameters:");
		String[] strArray = new String[1];
		strArray[0] = effect.getName();
		effectDefinition(player, strArray,false);
		return true;
	}

	public void showHelp(Player player, String[] newArgs) {
		MessageSender.sendInformationMessage(player,"Usage: /"+EMCommandExecutor.COMMAND + COMMAND + " "+EFFECT_ADD_CMD+"|"+EFFECT_DEL_CMD+"|"+EFFECT_CLASSES_CMD+"|"+EFFECT_PARAM_CMD+"|"+EFFECT_DETAILS_CMD+"|"+EFFECT_LIST_CMD+"|"+EFFECT_HELP_CMD);
	}

}
