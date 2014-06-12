/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.effects.projectileExplosion;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.CustomProjectileHitListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.EntityDamageListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.ProjectileEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;
import com.stirante.MoreProjectiles.event.CustomProjectileHitEvent;
import com.stirante.MoreProjectiles.event.CustomProjectileHitEvent.HitType;

/**
 * @author Benjamin
 *
 */
public class ProjectileExplosionEffect extends AbstractEffect {
	
	private static final String EFFECT_TYPE_NAME = "projectileExplosion";
	
	private static final String EFFECT_DESCRIPTION = "Sends many projectiles around the player.";


	@Override
	public EffectType getType() {
		return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}
	
	public static ProjectileExplosionEffect valueOf(Map<String,Object> map) {
		return new ProjectileExplosionEffect(map);
	}
	
	public static ProjectileExplosionEffect deserialize(Map<String,Object> map) {
		return new ProjectileExplosionEffect(map);
	}
	
	public ProjectileExplosionEffect(Map<String,Object> map) {
		super(map);
	}
	
	public ProjectileExplosionEffect(String name) {
		super(name);
	}

	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 6619922397961321409L;
			{
				add(EntityDamageListener.class);
				add(CustomProjectileHitListener.class);
			}
		};
	}
	
	@Override
	public boolean isCallable() {
		return true;
	}
	
	protected void onCall() {
		doProjectileExplosion(getPlayer());
	}
	
	@Override
	protected String getHelp() {
		return "Drops items when the player takes damages.";
	}
	
	@Override
	protected void on(Event event) {
		if ((event instanceof EntityDamageEvent)) {
			Entity entity = ((EntityDamageEvent)event).getEntity();
			if (!(entity instanceof Player)) {
				return;
			}
			Player player = (Player) entity;
			if (!player.getUniqueId().equals(getPlayerUUID())) {
				return;
			}
			doProjectileExplosion(player);
		}
		if ((event instanceof CustomProjectileHitEvent)) {
			CustomProjectileHitEvent e = ((CustomProjectileHitEvent)event);
	        if (e.getHitType() == HitType.ENTITY){
	        	e.getHitEntity().damage(3D, e.getProjectile().getShooter());
	        }
	    }
	}

	private void doProjectileExplosion(Player player) {
		Location origin = player.getEyeLocation();
		Set<Location> destinations = getHollowSphereLocations(origin, 4);
		int i = 0;
		for (Location destination : destinations) {
			ProjectileFactory.createProjectile(getProjectileEffectParameter(), player, destination, i);
			i++;
		}
	}

	private Set<Location> getHollowSphereLocations(Location origin, int radius) {
		Set<Location> locations = new TreeSet<Location>();
		 
		radius += 0.5;
		double radiusX, radiusY, radiusZ;
		radiusX = radiusY = radiusZ = radius;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusY = (int) Math.ceil(radiusY);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0;
        forX: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY: for (int y = 0; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }
                    //pour que seul le contour de la sphère soit sélectionné
                    if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
                            continue;
                    }
                    
                    locations.add(origin.clone().add(x, y, z));
                    locations.add(origin.clone().add(-x,y,z));
                    locations.add(origin.clone().add(x,-y,z));
                    locations.add(origin.clone().add(x,y,-z));
                    locations.add(origin.clone().add(-x,-y,z));
                    locations.add(origin.clone().add(x,-y,-z));
                    locations.add(origin.clone().add(-x,y,-z));
                    locations.add(origin.clone().add(-x,-y,-z));
                }
            }
        }
        for (int i = locations.size()-1;i>=0; i--) {
        	Location location = (Location) locations.toArray()[i];
        	Material m = origin.getWorld().getBlockAt(location).getType();
        	//supressions des localisations correspondant à des blocs dans lesquels des entites ne peuvent pas se déplacer.
			if (m != Material.AIR && m != Material.WATER && m != Material.LAVA && m != Material.STATIONARY_WATER && m != Material.STATIONARY_LAVA) {
				locations.remove(i);
			}
		}
		return locations;
	}

	private ProjectileEffectParameter getProjectileEffectParameter() {
		return ((ProjectileEffectParameter) getParameters().getSubParameters().get(0));
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("Projectile explosion effect Parameters","Projectile explosion effect Parameters","Projectile explosion effect Parameter", false, " ", 1);
		Map<String,String> defaultValue = new TreeMap<String, String>();
		defaultValue.put(ProjectileEffectParameter.MATERIAL_MAP_KEY, Material.STONE.name());
		defaultValue.put(ProjectileEffectParameter.POWER_MAP_KEY, "1");
		defaultValue.put(ProjectileEffectParameter.TYPE_MAP_KEY, "block");
		ProjectileEffectParameter projectile = new ProjectileEffectParameter("projectile", "projectile", "The projectile must be defined with the following parameters \"<block|item|tnt|orb>,<power (decimal number)>[,<material if the projectile is a block or an item (\'[]\' means it's optional)>]", true, defaultValue);
		ret.addSubEffectParameter(projectile);
		return ret;
	}

	@Override
	protected String getDescription() {
		return "Sends projectiles around the player.\n" +
				"The first parameter let us define the type of the projectileThe amplifier defines the number of block dropped when the player takes damages. The blocks are the blocks witch can be dropped.";
	}

	private static final double lengthSq(double x, double y, double z) {
	    return (x * x) + (y * y) + (z * z);
	}
	

}
