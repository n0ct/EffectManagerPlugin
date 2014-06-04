package com.github.n0ct.effectmanagerplugin.effects.effects.bumpattacker;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.EntityDamageByEntityListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.BooleanEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.DoubleEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public class BumpAttackerEffect extends AbstractEffect {


	private static final String EFFECT_TYPE_NAME = "bumpAttacker";
	
	private static final String EFFECT_DESCRIPTION = "Throw the entity attacking the player in the air.";
	
	public static BumpAttackerEffect valueOf(Map<String,Object> map) {
		return new BumpAttackerEffect(map);
	}
	
	public static BumpAttackerEffect deserialize(Map<String,Object> map) {
		return new BumpAttackerEffect(map);
	}
	
	public BumpAttackerEffect(Map<String,Object> map) {
		super(map);
	}
	
	
	public BumpAttackerEffect(String name) {
		super(name);
	}

	public void onCall() {
		Player p = getPlayer();
		doBumps(p);
	}


	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 3906971812417086983L;
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
		doBumps(entityDamageByEntityEvent.getDamager());
	}

	/**
	 * @param entityDamageByEntityEvent
	 */
	private void doBumps(final Entity entity) {
		if (entity instanceof Player && getSurvivalParam()) {
			final Player damager = (Player) entity;
			if (damager.getGameMode().equals(GameMode.CREATIVE)) {
				damager.setGameMode(GameMode.SURVIVAL);
				
				runTaskLater(new Runnable() {
					public void run() {
						damager.setGameMode(GameMode.CREATIVE);
					}
				},getTimeBeforeCreative());
			}
		}
		int maxexclusive = getNumberOfBumps();

		for(int i = 0 ; i< maxexclusive ; i++) {
			final EffectParameters params = getABumpParameter(i);
			final double velX = getXVelocity(params);
			final double velY = getYVelocity(params);
			final double velZ = getZVelocity(params);
			runTaskLater(new Runnable() {
				
				@Override
				public void run() {
					{
						entity.setVelocity(entity.getVelocity().add(new Vector(velX,velY,velZ)));
					}
					
				}
			}, getDelay(params));
		}
	}

	private EffectParameters getABumpParameter(int index)
	{
		return (EffectParameters) (((EffectParameters)getParameters().getSubParameters().get(0)).getSubParameters().get(index));
	}
	
	private int getNumberOfBumps() {
		return ((EffectParameters)getParameters().getSubParameters().get(0)).getSubParameters().size();
	}
	
	private int getTimeBeforeCreative() {
		return ((IntegerEffectParameter) getParameters().getParameterWithUniqueName("timeBeforeCreative", false)).getValue();
	}

	private boolean getSurvivalParam() {
		return ((BooleanEffectParameter) getParameters().getParameterWithUniqueName("survival", false)).getValue();
	}

	private double getZVelocity(EffectParameters aBumpParameters) {
		return ((DoubleEffectParameter)aBumpParameters.getParameterWithUniqueName("velocityZ", false)).getValue();
	}

	private double getYVelocity(EffectParameters aBumpParameters) {
		return ((DoubleEffectParameter)aBumpParameters.getParameterWithUniqueName("velocityY", false)).getValue();
	}

	private double getXVelocity(EffectParameters aBumpParameters) {
		return ((DoubleEffectParameter)aBumpParameters.getParameterWithUniqueName("velocityX", false)).getValue();
	}
	
	private int getDelay(EffectParameters aBumpParameters) {
		return ((IntegerEffectParameter)aBumpParameters.getParameterWithUniqueName("delay", false)).getValue();
	}

	@Override
	public boolean isCallable() {
		return true;
	}

	@Override
	protected String getHelp() {
		return "Bump the entity which attack a player or the player himself if called dirrectly.";
	}

	@Override
	protected String getDescription() {
		return "Throw the entity attacking the player or the player himself if called dirrectly.\n"
				+ "The first parameter ('Bumps') can contains many 'Bump' parameters to let you do many bump one after the other.\n"
				+"Each Bump paramater must be separated by ';' and can contain 4 parameters (these 4 parameters must be separated by ','):\n"
				+"'velocityX','velocityY','velocityZ' defines the velocity Vector that is used to throw the Entity.\n"
				+"'delay' defines the delay afterwhile the Entity throw will be done.\n"
				+"The second parameter 'survival' is used if the Entity thrown in the air is a player:\n" 
				+"If the parameter value is 'true' it sets the gamemode of that player to survival then, after a delay defined in the third parameter 'timeBeforeCreative', sets it back to creative."; 
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("effect Parameters","params","Bump Effect Parameters.", true, " ", 0);
		EffectParameters paramBumps = new EffectParameters("Bumps","Bumps","The list of the bumps effects ';' .", true, ";", 0);
		EffectParameters paramBump = new EffectParameters("Bump","Bump"+EffectParameters.ITERATION_PARAMETER+ "0","The bump parameters ','.", true, ",", 4);
		DoubleEffectParameter paramVelocityX = new DoubleEffectParameter("velocityX","velocityX","defines the bump vector X coordinate and by the way the bump power.",true,new Double(0), new Double(-999), new Double(999));
		paramBump.addSubEffectParameter(paramVelocityX);
		DoubleEffectParameter paramVelocityY = new DoubleEffectParameter("velocityY","velocityY","defines the bump vector Y coordinate and by the way the bump power.",true,new Double(0), new Double(-999), new Double(999));
		paramBump.addSubEffectParameter(paramVelocityY);
		DoubleEffectParameter paramVelocityZ = new DoubleEffectParameter("velocityZ","velocityZ","defines the bump vector Z coordinateand and by the way the bump power.",true,new Double(0), new Double(-999), new Double(999));
		paramBump.addSubEffectParameter(paramVelocityZ);
		IntegerEffectParameter paramDelay = new IntegerEffectParameter("delay", "delay", "the delay before the bump will be done.", true, 0, 0, 2000);
		paramBump.addSubEffectParameter(paramDelay);
		paramBumps.addSubEffectParameter(paramBump);
		ret.addSubEffectParameter(paramBumps);
		BooleanEffectParameter survival = new BooleanEffectParameter("survival", "survival", "Sets victim's gamemode to survival.", true, false);
		ret.addSubEffectParameter(survival);
		IntegerEffectParameter timeBeforeCreative = new IntegerEffectParameter("timeBeforeCreative","timeBeforeCreative","Defines the time before the victim's gamemode will return to creative.", true, 1, 0, 3000);
		ret.addSubEffectParameter(timeBeforeCreative);
		return ret;
	}

}
