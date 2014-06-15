package com.github.n0ct.effectmanagerplugin.effects.effects.shockwavelending;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.github.n0ct.effectmanagerplugin.effects.effects.SavedBlock;
import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.EntityDamageListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.FloatEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public class ShockWaveLendingEffect extends AbstractEffect {


	private static final String EFFECT_TYPE_NAME = "lendingEffect";
	
	private static final String EFFECT_DESCRIPTION = "Apply a shockwave effect on the environnement when the player toutch the ground after a fall.";

	private static final int MAX_HEIGHT_DIF = 3;
	
	public static ShockWaveLendingEffect valueOf(Map<String,Object> map) {
		return new ShockWaveLendingEffect(map);
	}
	
	public static ShockWaveLendingEffect deserialize(Map<String,Object> map) {
		return new ShockWaveLendingEffect(map);
	}
	
	public ShockWaveLendingEffect(Map<String,Object> map) {
		super(map);
	}
	
	public ShockWaveLendingEffect(String name) {
		super(name);
	}

	@Override
	public boolean isCallable() {
		return true;
	}
	
	public void onCall() {
		Player p = getPlayer();
		doShockwave(p);
	}

	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 1366108864537460416L;
			{
				add(EntityDamageListener.class);
			}
		};
	}
	
	@Override
	public EffectType getType() {
		return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}
	
	@Override
	protected void on(Event event) {
		if (!(event instanceof EntityDamageEvent)) {
			return;
		}
		final EntityDamageEvent entityDamageEvent = (EntityDamageEvent) event;
		if (!entityDamageEvent.getCause().equals(DamageCause.FALL)) {
			return;
		}
		if (!(entityDamageEvent.getEntity() instanceof Player)) {
			return;
		}
		Player victim = (Player) entityDamageEvent.getEntity();
		if (!victim.getUniqueId().equals(getPlayerUUID())) {
			return;
		}
		if (victim.getFallDistance() < getMinFallDistance()) {
			return;
		}
		doShockwave(victim);
	}
	


	private void doShockwave(Player p) {
		int radius = getShockwavesRadius();
		Location origin = p.getLocation();
		World w = p.getWorld();
		//creation de la map qui contiendra pour chaque valeur d'un rayon R un Set de Location avec les blocks d'un cercle de rayon R autour du joueur.
		Map<Integer,List<Location>> map = findShockwavesLocation(radius, origin, w);
		//Do the shockWaves
		doShockwave(w,map);
	}

	/**
	 * @param radius
	 * @param origin
	 * @param w
	 */
	private Map<Integer,List<Location>> findShockwavesLocation(int radius, Location origin, World w) {
		Map<Integer,List<Location>> map = new TreeMap<Integer,List<Location>>();
		List<Location> originSet = new ArrayList<Location>();
		originSet.add(origin);
		map.put(0,originSet);
		for (int r=1;r<=radius;r++) {
			map.put(r, getCircle(origin, radius));
		}
		// Modifies the map elements to be sure each saved block is at the ground level.
		// So we increase or decrease the Y component of the location until we find the ground level.
		for (Integer r:map.keySet()) {
			List<Location> circleLocations = map.get(r);
			for (int i = circleLocations.size()-1;i>=0;i--) {
				Location location = circleLocations.get(i);
				//foreach locations
				Location upperLocation = new Location(w,location.getBlockX(),location.getBlockY()+1,location.getBlockZ());
				Material locationMaterial = w.getBlockAt(location).getType();
				Material upperLocationMaterial = w.getBlockAt(upperLocation).getType();
				Location groundLocation = null;
				if (locationMaterial.equals(Material.AIR)) { //We must decrease Height to find the ground
					groundLocation = findGround(w,location,false);
				} else if (!upperLocationMaterial.equals(Material.AIR)) { // We must increase Height to find the ground
					groundLocation = findGround(w,location,true);
				} else { //We already have the ground;
					continue;
				}
				if (groundLocation != null) {
					circleLocations.add(groundLocation);
				}
				circleLocations.remove(i);
			}
		}
		return map;
	}

	
	private static Location findGround(World w, Location origin, boolean higher) {
		int incrementation = 0;
		int maxHeightDiff = MAX_HEIGHT_DIF;
		if (higher) {
			incrementation = 1;
			maxHeightDiff++;
		}
		if (!higher)
			incrementation = -1;

		int i = 1;
		Location curLocation;
		for (int y=origin.getBlockY()+incrementation;i <= maxHeightDiff;y=y+incrementation) {
			//If we are too high, too low or if the height difference is two big.
			if (y <= 2) return null;
			if (y -2 >= w.getMaxHeight()) return null;
			curLocation = new Location(w,origin.getBlockX(),y,origin.getBlockZ());
			if (higher) { // we are searching an AIR block and we return the under block location.
				if (w.getBlockAt(curLocation).getType().equals(Material.AIR)) {
					return new Location(w,origin.getBlockX(),curLocation.getBlockY()-1,origin.getBlockZ());
				}
			} else { // we are searching a block which is not AIR and we return its location.
				if (!w.getBlockAt(curLocation).getType().equals(Material.AIR)) {
					return curLocation;
				}
			}
			i++;
		}
		return null;
	}
		
	
	/**
	 * 
	 * @param centerLoc - Central Location
	 * @param radius - Distance in blocks from the "centerLoc"
	 * @return Circle
	 * @note - it will return only the blocks that are in the "radius" position.
	 * @author ArthurMaker
	 * @credits CaptainBern, skore87, Google!
	 */
	public static List<Location> getCircle(Location centerLoc, int radius){
	    List<Location> circle = new ArrayList<Location>();
	    World world = centerLoc.getWorld();
	    int x = 0;
	    int z = radius;
	    int error = 0;
	    int d = 2 - 2 * radius;
	    while (z >= 0) {
	      circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc.getY(), centerLoc.getBlockZ() + z));
	      circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc.getY(), centerLoc.getBlockZ() + z));
	      circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc.getY(), centerLoc.getBlockZ() - z));
	      circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc.getY(), centerLoc.getBlockZ() - z));
	      error = 2 * (d + z) - 1;
	      if ((d < 0) && (error <= 0)) {
	        x++;
	        d += 2 * x + 1;
	      }
	      else {
	        error = 2 * (d - x) - 1;
	        if ((d > 0) && (error > 0)) {
	          z--;
	          d += 1 - 2 * z;
	        }
	        else {
	          x++;
	          d += 2 * (x - z);
	          z--;
	        }
	      }
	    }
	    return circle;
	}

	private void doShockwave(World w, Map<Integer, List<Location>> map) {
		/*int power = getShockwavePower();
		int speed = getShockwaveSpeed();*/
		//We do the ground effect
		for (int i = 0; i<map.size(); i++) {
			List<Location> circleLocations = map.get(i);
			for (Location location : circleLocations) {
				for (int depth = 0;depth<map.size()-i;i++) {
					final Block b = w.getBlockAt(new Location(w,location.getBlockX(),location.getBlockY()-depth,location.getBlockZ()));
					final SavedBlock savedB = new SavedBlock(b);
					//Schedule the reinit of the block in its original state
					runTaskLater(new Runnable() {
						@Override
						public void run() {
							savedB.reinitBlock();
						}
					}, depth+(map.size()*4*i));
					//Destroy the block.
					runTaskLater(new Runnable() {
						@Override
						public void run() {
							b.breakNaturally(new ItemStack(b.getType(), 0));
						}
					}, depth+(map.size()*i));
				}
			}
		}
	}
	
	
	@Override
	protected String getHelp() {
		//TODO
		return "Bump the entity which attack a player or the player himself if called dirrectly.";
	}

	@Override
	protected String getDescription() {
		//TODO
		return "Throw the entity attacking the player or the player himself if called dirrectly.\n"
				+ "The first parameter ('Bumps') can contains many 'Bump' parameters to let you do many bump one after the other.\n"
				+"Each Bump paramater must be separated by ';' and can contain 4 parameters (these 4 parameters must be separated by ','):\n"
				+"'velocityX','velocityY','velocityZ' defines the velocity Vector that is used to throw the Entity.\n"
				+"'delay' defines the delay afterwhile the Entity throw will be done.\n"
				+"The second parameter 'survival' is used if the Entity thrown in the air is a player:\n" 
				+"If the parameter value is 'true' it sets the gamemode of that player to survival then, after a delay defined in the third parameter 'timeBeforeCreative', sets it back to creative."; 
	}

	/*private int getShockwaveSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}*/

	private int getShockwavesRadius() {
		// TODO Auto-generated method stub
		return 5;
	}

	private float getMinFallDistance() {
		return 3;
	}
	
	/*private int getShockwavePower() {
		// TODO Auto-generated method stub
		return 0;
	}*/
	
	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("Shockwave effect Parameters","params","Shockwave Lending Effect Parameters.", true, " ", 0);
		FloatEffectParameter paramMinFallDistance = new FloatEffectParameter("minFallDistance","minFallDistance","defines the minimum fall distance triggering the effect.",true,new Float(5), new Float(1), new Float(256));
		ret.addSubEffectParameter(paramMinFallDistance);
		IntegerEffectParameter paramRaduis = new IntegerEffectParameter("radius","radius","The radius of the effect",true,5,2,10);
		ret.addSubEffectParameter(paramRaduis);
		return ret;
	}
	


}
