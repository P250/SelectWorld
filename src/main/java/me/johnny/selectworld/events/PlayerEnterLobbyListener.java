package me.johnny.selectworld.events;

import me.johnny.selectworld.SelectWorld;
import me.johnny.selectworld.lobby.ServerSelectionRunnable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEnterLobbyListener implements Listener {

    private SelectWorld selectWorld;

    public PlayerEnterLobbyListener(SelectWorld selectWorld) {
        this.selectWorld = selectWorld;
    }

    @EventHandler
    public void PlayerJoinLobbyListener(PlayerChangedWorldEvent event) {
        forceSelectionMenuIfApplicable(event.getPlayer(), event.getPlayer().getWorld());
    }

    @EventHandler
    public void PlayerJoinLobbyListener(PlayerJoinEvent event) {
        forceSelectionMenuIfApplicable(event.getPlayer(), event.getPlayer().getWorld());
    }

    private void forceSelectionMenuIfApplicable(Player pl, World world) {

        if (!world.getName().equals(SelectWorld.WorldTypes.LOBBY.name().toLowerCase())) {
            return;
        }

        ServerSelectionRunnable forceShowGuiRunnable = new ServerSelectionRunnable(selectWorld, pl);
        forceShowGuiRunnable.runTaskTimer(selectWorld, 0L, 20L);
        Bukkit.getPluginManager().registerEvents(forceShowGuiRunnable, selectWorld);

    }


}
