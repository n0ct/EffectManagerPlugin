package com.github.n0ct.effectmanagerplugin.effects.effects.namechameleon;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.AsyncPlayerReceiveNameTagListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.PlayerToggleSneakListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.BooleanEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.ChatColorEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.StringEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;
//TODO transformer les noms de joueurs en UUID
public class NameChameleonEffect extends AbstractEffect {

	
	private static final String EFFECT_TYPE_NAME = "NameChameleon";
	
	private static final String EFFECT_DESCRIPTION = "Changes player name color.";

	private int currentNameChangeIndex;
	
	public static NameChameleonEffect valueOf(Map<String,Object> map) {
		return new NameChameleonEffect(map);
	}
	
	public static NameChameleonEffect deserialize(Map<String,Object> map) {
		return new NameChameleonEffect(map);
	}
	

	public NameChameleonEffect(String name) {
		super(name);
		currentNameChangeIndex = 0;
	}
	
	public NameChameleonEffect(Map<String,Object> map) {
		super(map);
		if (isActivated()) {
			startNameChanges();
		}
		currentNameChangeIndex = 0;
	}

	@Override
	public boolean isCallable() {
		return true;
	}
	
	@Override
	public EffectType getType() {
		return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}
	
	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 6588392948610579779L;

			{
				add(PlayerToggleSneakListener.class);
				add(AsyncPlayerReceiveNameTagListener.class);
			}
		};
		
	}
	
	private void setActivated(boolean b) {
		((BooleanEffectParameter)getParameters().getParameterWithUniqueName("activated", false)).setValue(b);
	}

	private boolean isActivated() {
		return ((BooleanEffectParameter)getParameters().getParameterWithUniqueName("activated", false)).getValue();
	}



	public void onCall() {
		if (isActivated()) {
			setActivated(false);
		} else {
			startNameChanges();
		}
	}


	private void startNameChanges() {
		setActivated(true);
		changeName();
	}
	

	private void nextNameChange() {
		int nbNameChange = ((EffectParameters)getParameters().getParameterWithUniqueName("nameChanges", false)).getSubParameters().size();
		if (nbNameChange > 1) {
			currentNameChangeIndex++;
			currentNameChangeIndex = currentNameChangeIndex % nbNameChange;
		}
	}
	
	public EffectParameters getCurrentNameChange() {
		EffectParameters nameChanges = ((EffectParameters)getParameters().getParameterWithUniqueName("nameChanges", false));
		return (EffectParameters) nameChanges.getSubParameters().get(currentNameChangeIndex);
	}
	
	private void changeName() {
		Player p = getPlayer();
		if (p == null) {
			setActivated(false);
			return;
		}
		if (!isActivated()) {
			TagAPI.refreshPlayer(p);
			return;
		}
		
		runTaskLater(new Runnable() {
			
			@Override
			public void run() {
				nextNameChange();
				TagAPI.refreshPlayer(getPlayer());
				changeName();
			}

		}, ((IntegerEffectParameter) getCurrentNameChange().getParameterWithUniqueName("interval", false)).getValue());
	}




	@Override
	protected void on(Event event) {
		if (event instanceof AsyncPlayerReceiveNameTagEvent) {
			AsyncPlayerReceiveNameTagEvent ev = (AsyncPlayerReceiveNameTagEvent) event;
			if (ev.getNamedPlayer().getUniqueId().equals(getPlayerUUID())) {
				if (isActivated()) {
					EffectParameters nameChange = getCurrentNameChange();
					StringBuilder sb = new StringBuilder(((ChatColorEffectParameter) nameChange.getParameterWithUniqueName("color", false)).toString());
					AbstractEffectParameter name = getCurrentNameChange().getParameterWithUniqueName("name", false);
					if (name != null && !((StringEffectParameter)name).getValue().isEmpty()) {
						sb.append(((StringEffectParameter)name).getValue());
					} else {
						sb.append(ev.getNamedPlayer().getName());
					}
					ev.setTag(sb.toString());
				} else {
					ev.setTag(ev.getNamedPlayer().getName());
				}
			}
		}
		if (event instanceof PlayerToggleSneakEvent) {
			PlayerToggleSneakEvent ev = (PlayerToggleSneakEvent) event;
			if (ev.getPlayer().getUniqueId().equals(getPlayerUUID())) {
				if (ev.isSneaking() && !isActivated()) {
					startNameChanges();
				} else if (!ev.isSneaking() && isActivated()) {
					setActivated(false);
				}
			}
		}
	}
	
	@Override
	protected String getHelp() {
		return "Changes the player name in a cyclic way while the player is sneacking or upon activation by call command.";
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("Name Chameleon Effect Parameters","params","Name Chameleon Effect Parameters.", true, UNFINDABLE_SPLIT_PARAMETER, 2);
		EffectParameters paramNameChanges = new EffectParameters("nameChanges","nameChanges","The list of the nameChanges which will be applied to the player separated by ';' .", true, ";", 0);
		EffectParameters paramName = new EffectParameters("nameChange","nameChange"+EffectParameters.ITERATION_PARAMETER+ "0","A definition of a change of name. each parameters is separated by ','.", true, ",", 3);
		ChatColorEffectParameter chatColor = new ChatColorEffectParameter("Color","color","The color which will be applied on the player's name.",false,ChatColor.RED);
		IntegerEffectParameter interval = new IntegerEffectParameter("Interval", "interval", "Interval between this name change and the next.", false, 15, 1, 2000);
		StringEffectParameter newName = new StringEffectParameter("Name","name", "The optionnal parameter used to define a change of the player's name", true, "");
		paramName.addSubEffectParameter(chatColor);
		paramName.addSubEffectParameter(interval);
		paramName.addSubEffectParameter(newName);
		paramNameChanges.addSubEffectParameter(paramName);
		ret.addSubEffectParameter(paramNameChanges);
		BooleanEffectParameter activated = new BooleanEffectParameter("Activated", "activated", "Defines if the effect is activated at start or not", true, true);
		ret.addSubEffectParameter(activated);
		return ret;
	}
	

	@Override
	protected String getDescription() {
		StringBuilder sb = new StringBuilder("Apply changes on the player's name while the player is witch attacking the player or the player himself if called dirrectly.\n");
		sb.append("The first parameter ('nameChanges') can contain many 'nameChange' parameters to let you apply many changes to the name of the player one after the other.\n");
		sb.append("Each nameChange paramater must be separated by ';' and can contain 3 parameters (these 3 parameters must be separated by ','):\n");
		sb.append("'Color' contains ChatColors which will be added before the player's name.\n");
		sb.append("'Interval' defines the time between the application of the current changeName and the next.\n");
		sb.append("The third optionnal parameter 'Name' can be used to change the shown player's name.\n");
		sb.append("The last parameter 'Activated' can be used to activate the plugin uppon startup.\n");
		return sb.toString();
	}
	
	@Override
	public void onDisable() {
		setActivated(false);
	}
	
	@Override
	public int getDisableDelay() {
		EffectParameters nameChangeParameters = (EffectParameters)getParameters().getSubParameters().get(0);
		int max = 0;
		for (int i = 0;  i<nameChangeParameters.getSubParameters().size(); i++) {
			int value = ((IntegerEffectParameter)((EffectParameters)nameChangeParameters.getSubParameters().get(i)).getParameterWithUniqueName("interval", false)).getValue();
			if (value > max) {
				max = value;
			}
		}
		return max+1;
	}


}
