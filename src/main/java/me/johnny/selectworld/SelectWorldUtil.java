package me.johnny.selectworld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.sql.SQLException;

public class SelectWorldUtil {

    public static void attemptServerChange(SelectWorld selectWorld, SelectWorld.WorldTypes worldType, Player pl) {
        try {
            Location loc = selectWorld.getPlayerLocationFromTable(worldType, pl.getUniqueId());
            if (loc == null) {
                World world = Bukkit.getWorld(worldType.name().toLowerCase());
                if (world == null) {
                    pl.sendMessage("This world has not been created by the admin yet!");
                    return;
                }
                pl.teleport(world.getSpawnLocation());
            } else {
                pl.teleport(loc);
            }

            return;

        } catch (SQLException ignore) {
            ignore.printStackTrace();
            pl.sendMessage("There was a critical error. Please try again later...");
            return;
        }
    }

}
