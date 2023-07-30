package me.johnny.selectworld.events;

import me.johnny.selectworld.SelectWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.sql.SQLException;

public class PlayerLeaveWorldListener implements Listener {

    public SelectWorld selectWorld;

    public PlayerLeaveWorldListener(SelectWorld selectWorld) {
        this.selectWorld = selectWorld;
    }

    @EventHandler
    public void playerLeaveWorldEvent(PlayerQuitEvent event) {

        Player pl = event.getPlayer();
        Location loc = pl.getLocation();
        World world = pl.getWorld();

        updatePlayerLocationInTable(world, pl, loc);

    }

    @EventHandler
    public void playerTeleportWorldEvent(PlayerTeleportEvent event) {

        Player pl = event.getPlayer();
        Location loc = pl.getLocation();
        World world = pl.getWorld();

        updatePlayerLocationInTable(world, pl, loc);
    }

    private void updatePlayerLocationInTable(World world, Player pl, Location loc) {
        SelectWorld.WorldTypes worldTableName;
        if (world.getName().equals("survival")) {
            worldTableName = SelectWorld.WorldTypes.SURVIVAL;
        } else if (world.getName().equals("creative")) {
            worldTableName = SelectWorld.WorldTypes.CREATIVE;
        } else {
            return;
        }

        try {

            selectWorld.updatePlayerLocation(
                    worldTableName,
                    pl.getUniqueId(),
                    world.getName(),
                    loc.x(),
                    loc.y(),
                    loc.z(),
                    loc.getYaw(),
                    loc.getPitch()
            );

        } catch (SQLException ignore) {
            ignore.printStackTrace();
            selectWorld.getLogger().severe("There was an error saving a player's final position when logging off. Stop the plugin and revert to a previous save of the database...");
        }
    }


}
