package to.epac.factorycraft.tictactoe.utils;

import org.bukkit.Location;

public class Utils {
	public static boolean locContains(int x, int y, int z, Location min, Location max) {
        return x >= Math.min(min.getX(), max.getX()) && x <= Math.max(min.getX(), max.getX()) &&
        		y >= Math.min(min.getY(), max.getY()) && y <= Math.max(min.getY(), max.getY()) &&
        		z >= Math.min(min.getZ(), max.getZ()) && z <= Math.max(min.getZ(), max.getZ());
	}
	
}
