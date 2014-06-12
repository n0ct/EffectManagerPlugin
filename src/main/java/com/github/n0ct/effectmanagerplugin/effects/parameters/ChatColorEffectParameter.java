package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import org.bukkit.ChatColor;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;

public class ChatColorEffectParameter extends AbstractPrimitiveEffectParameter<ChatColor> {

	public static ChatColorEffectParameter valueOf(Map<String,Object> map) {
		return new ChatColorEffectParameter(map);
	}
	
	public static ChatColorEffectParameter deserialize(Map<String,Object> map) {
		return new ChatColorEffectParameter(map);
	}
	
	public ChatColorEffectParameter(Map<String,Object> map) {
		super(map);
	}
	
	public ChatColorEffectParameter(String name, String uniqueName,
			String description, boolean optionnal, ChatColor defaultValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
	}

	@Override
	public void setValueFromString(String str) {
		setValue(getFromString(str));
	}

	@Override
	public boolean isValid(ChatColor value) {
		if (value != null) return true;
		return false;
	}

	@Override
	protected String getString(ChatColor value) {
		return value.name();
	}

	@Override
	protected ChatColor getFromString(String string) {
		return ChatColor.valueOf(string.toUpperCase());
	}


}
