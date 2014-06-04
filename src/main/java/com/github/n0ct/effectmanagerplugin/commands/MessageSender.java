package com.github.n0ct.effectmanagerplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageSender {
	
	public static final String LONG_PREFIX = ChatColor.GREEN + "E" + ChatColor.BLACK + "ffect" + ChatColor.YELLOW + "M" + ChatColor.BLACK + "anager" + ChatColor.RED + ": ";
	public static final String SHORT_PREFIX = ChatColor.GREEN + "E" + ChatColor.YELLOW + "M" + ChatColor.RED + ": ";
	public static final boolean PREFIX_USE_LONG_VERSION = true;
	
	public static void sendMessage(Player player, String message) {
		sendMessage(player, message, true, ChatColor.WHITE);
	}
	
	public static void sendErrorMessage(Player player, String message) {
		sendErrorMessage(player, message,true);
	}
	
	public static void sendSuccessMessage(Player player, String message) {
		sendSuccessMessage(player, message, true);
	}
	
	public static void sendInformationMessage (Player player, String message) {
		sendInformationMessage(player, message, true);
	}
	
	public static void sendErrorMessage(Player player, String message,boolean prefix) {
		sendMessage(player, message, prefix , ChatColor.RED);
	}
	
	public static void sendSuccessMessage(Player player, String message,boolean prefix) {
		sendMessage(player,message, prefix, ChatColor.GREEN);
	}
	
	public static void sendInformationMessage (Player player, String message,boolean prefix) {
		sendMessage(player, message, prefix,ChatColor.BLUE);
	}
	
	public static void sendMessage(Player player, String message, boolean prefix,ChatColor color) {
		StringBuilder firstMessage = new StringBuilder();
		if (prefix) {
			if (PREFIX_USE_LONG_VERSION) {
				firstMessage.append(LONG_PREFIX);
			} else {
				firstMessage.append(SHORT_PREFIX);
			}
		}
		firstMessage.append(color);
		if (!message.contains("\n")) {
			firstMessage.append(message);
			player.sendMessage(firstMessage.toString());
			return;
		}
		String[] messages = message.split("\\\n");
		firstMessage.append(messages[0]);
		player.sendMessage(firstMessage.toString());
		for(int i=1; i<messages.length;i++) {
			player.sendMessage(color + messages[i]);
		}
	}
	
}
