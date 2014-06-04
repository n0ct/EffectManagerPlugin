package com.github.n0ct.effectmanagerplugin.effects.effects.effectattacker;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.EntityDamageByEntityListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.BooleanEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.PotionEffectTypeEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public class EffectAttackerEffect extends AbstractEffect {

	public static EffectAttackerEffect valueOf(Map<String,Object> map) {
		return new EffectAttackerEffect(map);
	}
	
	public static EffectAttackerEffect deserialize(Map<String,Object> map) {
		return new EffectAttackerEffect(map);
	}
	
	public EffectAttackerEffect(Map<String,Object> map) {
		super(map);
	}
	
	public EffectAttackerEffect(String name) {
		super(name);
	}
	

	public void onCall() {
		Player p = getPlayer();
		doEffect(p);
	}


	private static final String EFFECT_TYPE_NAME = "effectAttacker";
	
	private static final String EFFECT_DESCRIPTION = "Put some effect on the entity attacking the player.";

	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 7187034955063406594L;
			{
				add(EntityDamageByEntityListener.class);
			}
		};
	}

	@Override
	public EffectType getType() {
		return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}

	@Override
	protected void on(Event event) {
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		final EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
		if (!(entityDamageByEntityEvent.getEntity() instanceof Player)) {
			return;
		}
		Player victim = (Player) entityDamageByEntityEvent.getEntity();
		if (!victim.getName().equals(getPlayerName())) {
			return;
		}
		if (entityDamageByEntityEvent.getDamager() instanceof Player) {
			doEffect((Player)entityDamageByEntityEvent.getDamager());
		}
	}
	
	@Override
	protected String getHelp() {
		return "Puts effects on the entity which attack a player or the player himself if called dirrectly.";
	}

	@Override
	public boolean isCallable() {
		return true;
	}

	private void doEffect(final Player player) {
			if (getSurvival()) {
				if (player.getGameMode().equals(GameMode.CREATIVE)) {
					player.setGameMode(GameMode.SURVIVAL);
					
					runTaskLater(new Runnable() {
						public void run() {
							player.setGameMode(GameMode.CREATIVE);
						}
					},getTimeBeforeCreative());
				}
			}
			int maxexclusive = getNumberOfEffects();
			for(int i = 0 ; i< maxexclusive ; i++) {
				final EffectParameters params = getAnEffectParameter(i);
				runTaskLater(new Runnable() {
					@Override
					public void run() {
						player.addPotionEffect(getPotionEffectType(params).createEffect(getDuration(params), getAmplifier(params)),true);
					}
				},getDelay(params));
			}
		
	}
	

	private int getAmplifier(EffectParameters params) {
		return ((IntegerEffectParameter)params.getParameterWithUniqueName("amplifier", false)).getValue();
	}


	private int getDuration(EffectParameters params) {
		return ((IntegerEffectParameter)params.getParameterWithUniqueName("duration", false)).getValue();
	}


	private PotionEffectType getPotionEffectType(EffectParameters params) {
		return ((PotionEffectTypeEffectParameter)params.getParameterWithUniqueName("effect", false)).getValue();
	}

	private int getDelay(EffectParameters params) {
		return ((IntegerEffectParameter)params.getParameterWithUniqueName("delay", false)).getValue();
	}
	
	private EffectParameters getAnEffectParameter(int index) {
		return (EffectParameters) ((EffectParameters) getParameters().getSubParameters().get(0)).getSubParameters().get(index);
	}


	private int getNumberOfEffects() {
		return ((EffectParameters)getParameters().getSubParameters().get(0)).getSubParameters().size();
	}

	private int getTimeBeforeCreative() {
		return ((IntegerEffectParameter) getParameters().getParameterWithUniqueName("timeBeforeCreative", false)).getValue();
	}


	private boolean getSurvival() {
		return ((BooleanEffectParameter) getParameters().getParameterWithUniqueName("survival", false)).getValue();
	}


	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("Effect Attacker Parameters","params","Effect Attacker Effect Parameters.", true, " ", 0);
		EffectParameters paramEffects = new EffectParameters("Effects","Effects","The list of the effects ';' .", true, ";", 0);
		EffectParameters paramEffect = new EffectParameters("Effect","Effect"+EffectParameters.ITERATION_PARAMETER+ "0","The effect parameters ','.", true, ",", 4);
		PotionEffectTypeEffectParameter effect = new PotionEffectTypeEffectParameter("Effect type","effect","defines the Effect Type witch will be applied to the player.",true,PotionEffectType.CONFUSION);
		paramEffect.addSubEffectParameter(effect);
		IntegerEffectParameter duration = new IntegerEffectParameter("Effect duration","duration","defines the duration of the effect witch will be applied to the player.",true,10, 1, 2000);
		paramEffect.addSubEffectParameter(duration);
		IntegerEffectParameter amplifier = new IntegerEffectParameter("Effect amplifier","amplifier","defines the amplifier for the effect witch will be applied to the player.",true,1, 1, 100);
		paramEffect.addSubEffectParameter(amplifier);
		IntegerEffectParameter delay = new IntegerEffectParameter("Delay", "delay", "the delay before the effect will be applied to the player.", true, 0, 0, 2000);
		paramEffect.addSubEffectParameter(delay);
		paramEffects.addSubEffectParameter(paramEffect);
		ret.addSubEffectParameter(paramEffects);
		BooleanEffectParameter survival = new BooleanEffectParameter("survival", "survival", "Sets victim's gamemode to survival.", true, false);
		ret.addSubEffectParameter(survival);
		IntegerEffectParameter timeBeforeCreative = new IntegerEffectParameter("timeBeforeCreative","timeBeforeCreative","Defines the time before the victim's gamemode will return to creative.", true, 1, 0, 3000);
		ret.addSubEffectParameter(timeBeforeCreative);
		return ret;
	}
	

	@Override
	protected String getDescription() {
		return "Apply effects on the player witch is attacking the player or the player himself if called dirrectly.\n"
				+ "The first parameter ('Effects') can contains many 'Effect' parameters to let you apply many effect one after the other.\n"
				+"Each effect paramater must be separated by ';' and can contain 4 parameters (these 4 parameters must be separated by ','):\n"
				+"'Effect type' contains the type (id or string) of the effect. 'Effect duration' defines the duration of the effect.\n"
				+"'Effect amplifier' defines the aplifier for the applied effect. 'Delay' defines the delay afterwhile the Entity throw will be done.\n"
				+"The second parameter 'survival' is used if the Entity thrown in the air is a player:\n" 
				+"If the parameter value is 'true' it sets the gamemode of that player to survival then, after a delay defined in the third parameter 'timeBeforeCreative', sets it back to creative."; 
	}


}
