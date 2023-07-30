package me.johnny.selectworld.lobby;

import me.johnny.selectworld.SelectWorld;
import me.johnny.selectworld.SelectWorldUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerSelectionRunnable extends BukkitRunnable implements Listener {

    private final SelectWorld selectWorld;
    private final Player pl;
    public static final Inventory SELECTION_INV = Bukkit.createInventory(null, 27);
    public static final ItemStack CREATIVE_SERVER_ICON = new ItemStack(Material.GRASS_BLOCK);
    public static final ItemStack SURVIVAL_SERVER_ICON = new ItemStack(Material.IRON_SWORD);

    /**
     * I want the inventory to be publically accessable so other listeners can check if the clicked inventory matches this one!
     */
    static {
        ItemMeta creativeItemMeta = CREATIVE_SERVER_ICON.getItemMeta();
        creativeItemMeta.displayName(Component.empty().content("Creative").color(TextColor.color(0, 255, 255)).decorate(TextDecoration.BOLD));
        CREATIVE_SERVER_ICON.setItemMeta(creativeItemMeta);

        ItemMeta survivalItemMeta = SURVIVAL_SERVER_ICON.getItemMeta();
        survivalItemMeta.displayName(Component.empty().content("Survival").color(TextColor.fromHexString("#FF5555")).decorate(TextDecoration.BOLD));
        SURVIVAL_SERVER_ICON.setItemMeta(survivalItemMeta);

        SELECTION_INV.setItem(12, CREATIVE_SERVER_ICON);
        SELECTION_INV.setItem(14, SURVIVAL_SERVER_ICON);

    }


    public ServerSelectionRunnable(SelectWorld selectWorld, Player pl) {
        this.selectWorld = selectWorld;
        this.pl = pl;
    }

    @Override
    public void run() {
        if (!pl.isOnline()) { cancel(); return; }
        pl.openInventory(SELECTION_INV);
    }

    @EventHandler
    public void IconClickEvent(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) { return; }
        if (clickedItem.isSimilar(CREATIVE_SERVER_ICON)) {
            event.getWhoClicked().sendMessage("Teleporting to creative server!");
            SelectWorldUtil.attemptServerChange(selectWorld, SelectWorld.WorldTypes.CREATIVE, pl);
            cancel();
        } else if (clickedItem.isSimilar(SURVIVAL_SERVER_ICON)) {
            event.getWhoClicked().sendMessage("Teleporting to survival server!");
            SelectWorldUtil.attemptServerChange(selectWorld, SelectWorld.WorldTypes.SURVIVAL, pl);
            cancel();
        }
        
        event.setCancelled(true);

    }



}
