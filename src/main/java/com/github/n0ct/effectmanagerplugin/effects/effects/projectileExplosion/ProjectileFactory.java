package com.github.n0ct.effectmanagerplugin.effects.effects.projectileExplosion;


import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.n0ct.effectmanagerplugin.effects.parameters.ProjectileEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.projectile.ProjectileType;
import com.stirante.MoreProjectiles.projectile.BlockProjectile;
import com.stirante.MoreProjectiles.projectile.CustomProjectile;
import com.stirante.MoreProjectiles.projectile.ItemProjectile;
import com.stirante.MoreProjectiles.projectile.OrbProjectile;
import com.stirante.MoreProjectiles.projectile.TNTProjectile;

public class ProjectileFactory {

	@SuppressWarnings("deprecation")
	public static CustomProjectile createProjectile(ProjectileEffectParameter projectileEffectParameter, Player player, Location destination, int iteration) {
		Location origin = player.getEyeLocation();
		ProjectileType type = projectileEffectParameter.getType();
		float power = projectileEffectParameter.getPower();
		String name = projectileEffectParameter.getUniqueName() + type.getName();
		if (iteration>-1) {
			name = name + iteration;
		}
		CustomProjectile projectile = null;
		switch(type) {
			case BLOCK:
				int materialId = projectileEffectParameter.getMaterial().getId();
				projectile = new BlockProjectile(name, origin, (LivingEntity)player, materialId, 0, power);
				break;
			case ITEM:
				Material mat = projectileEffectParameter.getMaterial();
				ItemStack itemStack = new ItemStack(mat,1);
				projectile = new ItemProjectile(name,origin,(LivingEntity)player, itemStack,power);
				break;
			case ORB:
				projectile = new OrbProjectile(name, (LivingEntity)player, origin, power);
				break;
			case TNT:
				projectile = new TNTProjectile(name, (LivingEntity)player, origin,  power);
				break;
			default:
				throw new NotImplementedException();
		}
		if (projectile != null) {
			projectile.getEntity().setVelocity(getVector(origin,destination).multiply(power));
			projectile.addEntity();
		}
		return projectile;
	}
	

	private static Vector getVector(Location origin, Location destination) {
		Vector from = new Vector(origin.getX(), origin.getY(), origin.getZ());
		Vector to  = new Vector(destination.getX(), destination.getY(), destination.getZ());
		return to.subtract(from);
	}

}
