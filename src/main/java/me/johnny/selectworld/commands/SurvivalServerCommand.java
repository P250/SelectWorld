package me.johnny.selectworld.commands;

import me.johnny.selectworld.SelectWorld;
import me.johnny.selectworld.SelectWorldUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SurvivalServerCommand implements CommandExecutor {

    private final SelectWorld selectWorld;

    public SurvivalServerCommand(SelectWorld selectWorld) {
        this.selectWorld = selectWorld;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player pl = (Player) sender;
        SelectWorldUtil.attemptServerChange(selectWorld, SelectWorld.WorldTypes.SURVIVAL, pl);
        return true;

    }
}
