package me.funke.daedalus.gui;

import me.funke.daedalus.Daedalus;
import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChecksGUI implements Listener {
    public static Inventory Daedalusmain = Bukkit.createInventory(null, 36, C.Gold + "Home");
    public static Inventory Daedaluschecks = Bukkit.createInventory(null, 45, C.Gold + "Checks: Toggle");
    public static Inventory Daedalusbannable = Bukkit.createInventory(null, 45, C.Gold + "Checks: Bannable");
    public static Inventory DaedalusTimer = Bukkit.createInventory(null, 45, C.Gold + "Checks: BanTimer");
    public static Inventory Daedalusbans = Bukkit.createInventory(null, 54, C.Gold + "Recent Bans");
    public static Inventory Daedalusstatus = Bukkit.createInventory(null, 27, C.Gold + "Status");
    private static ItemStack back = createItem(Material.REDSTONE, 1, "&6Back");
    private static me.funke.daedalus.Daedalus Daedalus;

    public ChecksGUI(Daedalus Daedalus) {
        ChecksGUI.Daedalus = Daedalus;
        ItemStack checks = createItem(Material.COMPASS, 1, "&cChecks");
        ItemStack bannable = createItem(Material.REDSTONE, 1, "&cAuto Bans");
        ItemStack timers = createItem(Material.WATCH, 1, "&cTimers");
        ItemStack resetVio = createItem(Material.PAPER, 1, "&cReset Violations");
        ItemStack reload = createItem(Material.LAVA_BUCKET, 1, "&cReload");
        ItemStack info = createItem(Material.BOOK, 1, "&aInfo");
        ItemStack checkered = createItem(Material.COAL_BLOCK, 1, Daedalus.getConfig().getBoolean("settings.gui.checkered") ? "&aCheckered" : "&cCheckered");
        ItemStack sotwMode = createItem(Daedalus.getConfig().getBoolean("settings.sotwMode") ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, 1, "&cSoTW Mode");
        ItemMeta infom = info.getItemMeta();
        infom.setLore(infoLore());
        info.setItemMeta(infom);
        Daedalusmain.setItem(9, checks);
        Daedalusmain.setItem(13, timers);
        Daedalusmain.setItem(11, bannable);
        Daedalusmain.setItem(15, reload);
        Daedalusmain.setItem(17, resetVio);
        Daedalusmain.setItem(1, grayGlass());
        Daedalusmain.setItem(3, grayGlass());
        Daedalusmain.setItem(5, grayGlass());
        Daedalusmain.setItem(7, grayGlass());
        Daedalusmain.setItem(19, grayGlass());
        Daedalusmain.setItem(21, grayGlass());
        Daedalusmain.setItem(23, grayGlass());
        Daedalusmain.setItem(25, grayGlass());
        Daedalusmain.setItem(27, sotwMode);
        Daedalusmain.setItem(29, grayGlass());
        Daedalusmain.setItem(31, grayGlass());
        Daedalusmain.setItem(33, grayGlass());
        Daedalusmain.setItem(35, checkered);
        Daedalusmain.setItem(31, info);
        if (Daedalus.getConfig().contains("settings.gui.checkered")) {
            if (Daedalus.getConfig().getBoolean("settings.gui.checkered")) {
                Daedalusmain.setItem(0, whiteGlass());
                Daedalusmain.setItem(2, whiteGlass());
                Daedalusmain.setItem(4, whiteGlass());
                Daedalusmain.setItem(6, whiteGlass());
                Daedalusmain.setItem(8, whiteGlass());
                Daedalusmain.setItem(10, whiteGlass());
                Daedalusmain.setItem(12, whiteGlass());
                Daedalusmain.setItem(14, whiteGlass());
                Daedalusmain.setItem(16, whiteGlass());
                Daedalusmain.setItem(18, whiteGlass());
                Daedalusmain.setItem(20, whiteGlass());
                Daedalusmain.setItem(22, whiteGlass());
                Daedalusmain.setItem(24, whiteGlass());
                Daedalusmain.setItem(26, whiteGlass());
                Daedalusmain.setItem(28, whiteGlass());
                Daedalusmain.setItem(30, whiteGlass());
                Daedalusmain.setItem(32, whiteGlass());
                Daedalusmain.setItem(34, whiteGlass());
            } else {
                Daedalusmain.setItem(0, grayGlass());
                Daedalusmain.setItem(2, grayGlass());
                Daedalusmain.setItem(4, grayGlass());
                Daedalusmain.setItem(6, grayGlass());
                Daedalusmain.setItem(8, grayGlass());
                Daedalusmain.setItem(10, grayGlass());
                Daedalusmain.setItem(12, grayGlass());
                Daedalusmain.setItem(14, grayGlass());
                Daedalusmain.setItem(16, grayGlass());
                Daedalusmain.setItem(18, grayGlass());
                Daedalusmain.setItem(20, grayGlass());
                Daedalusmain.setItem(22, grayGlass());
                Daedalusmain.setItem(24, grayGlass());
                Daedalusmain.setItem(26, grayGlass());
                Daedalusmain.setItem(28, grayGlass());
                Daedalusmain.setItem(30, grayGlass());
                Daedalusmain.setItem(32, grayGlass());
                Daedalusmain.setItem(34, grayGlass());
            }
        } else {
            Daedalus.getConfig().set("settings.gui.checkered", true);
            Daedalusmain.setItem(0, whiteGlass());
            Daedalusmain.setItem(2, whiteGlass());
            Daedalusmain.setItem(4, whiteGlass());
            Daedalusmain.setItem(6, whiteGlass());
            Daedalusmain.setItem(8, whiteGlass());
            Daedalusmain.setItem(10, whiteGlass());
            Daedalusmain.setItem(12, whiteGlass());
            Daedalusmain.setItem(14, whiteGlass());
            Daedalusmain.setItem(16, whiteGlass());
            Daedalusmain.setItem(18, whiteGlass());
            Daedalusmain.setItem(20, whiteGlass());
            Daedalusmain.setItem(22, whiteGlass());
            Daedalusmain.setItem(24, whiteGlass());
            Daedalusmain.setItem(26, whiteGlass());
            Daedalusmain.setItem(28, whiteGlass());
            Daedalusmain.setItem(30, whiteGlass());
            Daedalusmain.setItem(32, whiteGlass());
            Daedalusmain.setItem(34, whiteGlass());
        }
    }

    private static ArrayList<String> infoLore() {
        ArrayList<String> list = new ArrayList<>();
        list.add(" ");
        list.add(ChatColor.translateAlternateColorCodes('&', "&7You can do &f/daedalus help &7to see your"));
        list.add(ChatColor.translateAlternateColorCodes('&', "&7options for other &fcommands&7/&ffunctions&7!"));
        list.add(" ");
        list.add(ChatColor.translateAlternateColorCodes('&', "&7Current Version: &fb" + Daedalus.getDescription().getVersion()));
        if (Daedalus.hasNewVersion()) {
            list.add(C.Gold + C.Italics + "New Update: " + C.White + "b" + Daedalus.getPasteVersion());
        }
        return list;
    }

    public static void openDaedalusMain(Player player) {
        player.openInventory(Daedalusmain);
    }

    public static void openBans(Player player) {
        List<Map.Entry<String, Check>> entrybans = new ArrayList<>(Daedalus.getNamesBanned().entrySet());
        for (int i = 0; i < entrybans.size(); i++) {
            Map.Entry<String, Check> entry = entrybans.get(i);
            if (i <= 54) {
                ItemStack offender = createItem(Material.PAPER, 1, C.Red + entry.getKey(), C.Gray + entry.getValue().getName());
                Daedalusbans.setItem(i, offender);
            }
        }
    }

    public static void openStatus(Player player, Player target) {
        Daedalusstatus = Bukkit.createInventory(player, 27, C.Gold + "Status");
        Map<Check, Integer> Checks = Daedalus.getViolations(target);
        if ((Checks == null) || (Checks.isEmpty())) {
            player.sendMessage(C.Gray + "This player set off 0 checks. Yay!");
        } else {
            int slot = 0;
            for (Check Check : Checks.keySet()) {
                Integer Violations = Checks.get(Check);
                ItemStack vl = createItem(Material.PAPER, 1, C.Aqua + Check.getName() + C.DGray + " [" + C.Red + Violations + C.DGray + "]");
                Daedalusstatus.setItem(slot, vl);
                slot++;
            }
        }
        player.openInventory(Daedalusstatus);
    }

    public static ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        thingm.setLore(Arrays.asList(lore));
        thing.setItemMeta(thingm);
        return thing;
    }

    public static ItemStack createGlass(Material material, int color, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount, (short) color);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        thingm.setLore(Arrays.asList(lore));
        thing.setItemMeta(thingm);
        return thing;
    }

    public static ItemStack grayGlass() {
        ItemStack thing = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b"));
        thing.setItemMeta(thingm);
        return thing;
    }

    public static ItemStack whiteGlass() {
        ItemStack thing = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b"));
        thing.setItemMeta(thingm);
        return thing;
    }

    public void openChecks(Player player) {
        int slot = 0;
        for (Check check : Daedalus.getChecks()) {
            if (Daedalus.getConfig().getBoolean("checks." + check.getIdentifier() + ".enabled")) {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, C.Green + check.getName());
                Daedaluschecks.setItem(slot, c);
            } else {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, C.Red + check.getName());
                Daedaluschecks.setItem(slot, c);
            }
            slot++;
        }
        for (int i = slot; i < 44; i++) {
            ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, C.Gray + "N/A");
            Daedaluschecks.setItem(i, c);
        }
        Daedaluschecks.setItem(44, back);
        player.openInventory(Daedaluschecks);
    }

    public void openAutoBans(Player player) {
        int slot = 0;
        for (Check check : Daedalus.getChecks()) {
            if (Daedalus.getConfig().getBoolean("checks." + check.getIdentifier() + ".bannable")) {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, C.Green + check.getName());
                Daedalusbannable.setItem(slot, c);
            } else {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, C.Red + check.getName());
                Daedalusbannable.setItem(slot, c);
            }
            slot++;
        }
        for (int i = slot; i < 44; i++) {
            ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, C.Gray + "N/A");
            Daedalusbannable.setItem(i, c);
        }
        Daedalusbannable.setItem(44, back);
        player.openInventory(Daedalusbannable);
    }

    public void openTimer(Player player) {
        int slot = 0;
        for (Check check : Daedalus.getChecks()) {
            if (Daedalus.getConfig().getBoolean("checks." + check.getIdentifier() + ".banTimer")) {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, C.Green + check.getName());
                DaedalusTimer.setItem(slot, c);
            } else {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, C.Red + check.getName());
                DaedalusTimer.setItem(slot, c);
            }
            slot++;
        }
        for (int i = slot; i < 44; i++) {
            ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, C.Gray + "N/A");
            DaedalusTimer.setItem(i, c);
        }
        DaedalusTimer.setItem(44, back);
        player.openInventory(DaedalusTimer);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals(C.Gold + "Home")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.translateAlternateColorCodes('&', "&cChecks"))) {
                openChecks(player);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cAuto Bans"))) {
                openAutoBans(player);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cTimers"))) {
                openTimer(player);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cSoTW Mode"))) {
                if (Daedalus.getConfig().getBoolean("settings.sotwMode")) {
                    Daedalus.getConfig().set("settings.sotwMode", false);
                    Daedalus.saveConfig();
                    ItemStack sotwMode = createItem(Daedalus.getConfig().getBoolean("settings.sotwMode") ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, 1, "&cSoTW Mode");
                    Daedalusmain.setItem(27, sotwMode);
                } else {
                    Daedalus.getConfig().set("settings.sotwMode", true);
                    Daedalus.saveConfig();
                    ItemStack sotwMode = createItem(Daedalus.getConfig().getBoolean("settings.sotwMode") ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, 1, "&cSoTW Mode");
                    Daedalusmain.setItem(27, sotwMode);
                }
            }
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Checkered")) {
                Daedalus.getConfig().set("settings.gui.checkered", !Daedalus.getConfig().getBoolean("settings.gui.checkered"));
                Daedalus.saveConfig();
                ItemStack sotwMode = createItem(Daedalus.getConfig().getBoolean("settings.sotwMode") ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, 1, "&cSoTW Mode");
                if (Daedalus.getConfig().contains("settings.gui.checkered")) {
                    if (Daedalus.getConfig().getBoolean("settings.gui.checkered")) {
                        Daedalusmain.setItem(0, whiteGlass());
                        Daedalusmain.setItem(2, whiteGlass());
                        Daedalusmain.setItem(4, whiteGlass());
                        Daedalusmain.setItem(6, whiteGlass());
                        Daedalusmain.setItem(8, whiteGlass());
                        Daedalusmain.setItem(10, whiteGlass());
                        Daedalusmain.setItem(12, whiteGlass());
                        Daedalusmain.setItem(14, whiteGlass());
                        Daedalusmain.setItem(16, whiteGlass());
                        Daedalusmain.setItem(18, whiteGlass());
                        Daedalusmain.setItem(20, whiteGlass());
                        Daedalusmain.setItem(22, whiteGlass());
                        Daedalusmain.setItem(24, whiteGlass());
                        Daedalusmain.setItem(26, whiteGlass());
                        Daedalusmain.setItem(28, whiteGlass());
                        Daedalusmain.setItem(30, whiteGlass());
                        Daedalusmain.setItem(32, whiteGlass());
                        Daedalusmain.setItem(34, whiteGlass());
                    } else {
                        Daedalusmain.setItem(0, grayGlass());
                        Daedalusmain.setItem(2, grayGlass());
                        Daedalusmain.setItem(4, grayGlass());
                        Daedalusmain.setItem(6, grayGlass());
                        Daedalusmain.setItem(8, grayGlass());
                        Daedalusmain.setItem(10, grayGlass());
                        Daedalusmain.setItem(12, grayGlass());
                        Daedalusmain.setItem(14, grayGlass());
                        Daedalusmain.setItem(16, grayGlass());
                        Daedalusmain.setItem(18, grayGlass());
                        Daedalusmain.setItem(20, grayGlass());
                        Daedalusmain.setItem(22, grayGlass());
                        Daedalusmain.setItem(24, grayGlass());
                        Daedalusmain.setItem(26, grayGlass());
                        Daedalusmain.setItem(28, grayGlass());
                        Daedalusmain.setItem(30, grayGlass());
                        Daedalusmain.setItem(32, grayGlass());
                        Daedalusmain.setItem(34, grayGlass());
                    }
                } else {
                    Daedalus.getConfig().set("settings.gui.checkered", true);
                    Daedalusmain.setItem(0, whiteGlass());
                    Daedalusmain.setItem(2, whiteGlass());
                    Daedalusmain.setItem(4, whiteGlass());
                    Daedalusmain.setItem(6, whiteGlass());
                    Daedalusmain.setItem(8, whiteGlass());
                    Daedalusmain.setItem(10, whiteGlass());
                    Daedalusmain.setItem(12, whiteGlass());
                    Daedalusmain.setItem(14, whiteGlass());
                    Daedalusmain.setItem(16, whiteGlass());
                    Daedalusmain.setItem(18, whiteGlass());
                    Daedalusmain.setItem(20, whiteGlass());
                    Daedalusmain.setItem(22, whiteGlass());
                    Daedalusmain.setItem(24, whiteGlass());
                    Daedalusmain.setItem(26, whiteGlass());
                    Daedalusmain.setItem(28, whiteGlass());
                    Daedalusmain.setItem(30, whiteGlass());
                    Daedalusmain.setItem(32, whiteGlass());
                    Daedalusmain.setItem(34, whiteGlass());
                }
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cReset Violations"))) {
                Daedalus.resetAllViolations();
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                meta.setDisplayName(C.Green + C.Italics + "Success!");
                e.getCurrentItem().setItemMeta(meta);
                new BukkitRunnable() {
                    public void run() {
                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        meta.setDisplayName(C.Red + "Reset Violations");
                        e.getCurrentItem().setItemMeta(meta);
                    }
                }.runTaskLater(Daedalus, 40L);
            }

            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cReload"))) {
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                meta.setDisplayName(C.Red + C.Italics + "Reloading...");
                e.getCurrentItem().setItemMeta(meta);
                Daedalus.reloadConfig();
                meta.setDisplayName(C.Green + C.Italics + "Success!");
                e.getCurrentItem().setItemMeta(meta);
                new BukkitRunnable() {
                    public void run() {
                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        meta.setDisplayName(C.Red + "Reload");
                        e.getCurrentItem().setItemMeta(meta);
                        openDaedalusMain(player);
                    }
                }.runTaskLater(Daedalus, 40L);
            }
        } else if (e.getInventory().getName().equals(C.Gold + "Checks: Toggle")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().hasItemMeta()) {
                String check_name = e.getCurrentItem().getItemMeta().getDisplayName();
                for (Check check : Daedalus.getChecks()) {
                    if (check.getName().equals(ChatColor.stripColor(check_name))) {
                        if (Daedalus.getConfig().getBoolean("checks." + check.getIdentifier() + ".enabled")) {
                            Daedalus.getConfig().set("checks." + check.getIdentifier() + ".enabled", false);
                            Daedalus.saveConfig();
                            Daedalus.reloadConfig();
                            check.setEnabled(false);
                            openChecks(player);
                            return;
                        }
                        Daedalus.getConfig().set("checks." + check.getIdentifier() + ".enabled", true);
                        Daedalus.saveConfig();
                        Daedalus.reloadConfig();
                        check.setEnabled(true);
                        openChecks(player);
                        return;
                    }
                }
                if (ChatColor.stripColor(check_name).equals("Back")) {
                    openDaedalusMain(player);
                }
            }
        } else if (e.getInventory().getName().equals(C.Gold + "Checks: Bannable")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().hasItemMeta()) {
                String check_name = e.getCurrentItem().getItemMeta().getDisplayName();
                for (Check check : Daedalus.getChecks()) {
                    if (check.getName().contains(ChatColor.stripColor(check_name))) {
                        if (Daedalus.getConfig().getBoolean("checks." + check.getIdentifier() + ".bannable")) {
                            Daedalus.getConfig().set("checks." + check.getIdentifier() + ".banTimer", false);
                            Daedalus.getConfig().set("checks." + check.getIdentifier() + ".bannable", false);
                            Daedalus.saveConfig();
                            Daedalus.reloadConfig();
                            check.setBannable(false);
                            openAutoBans(player);
                            return;
                        }
                        Daedalus.getConfig().set("checks." + check.getIdentifier() + ".bannable", true);
                        Daedalus.saveConfig();
                        Daedalus.reloadConfig();
                        check.setBannable(true);
                        openAutoBans(player);
                        return;
                    }
                }
                if (ChatColor.stripColor(check_name).equals("Back")) {
                    openDaedalusMain(player);
                }
            }
        } else if (e.getInventory().getName().equals(C.Gold + "Checks: BanTimer")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().hasItemMeta()) {
                String check_name = e.getCurrentItem().getItemMeta().getDisplayName();
                for (Check check : Daedalus.getChecks()) {
                    if (check.getName().equals(ChatColor.stripColor(check_name))) {
                        if (Daedalus.getConfig().getBoolean("checks." + check.getIdentifier() + ".bannable")) {
                            Daedalus.getConfig().set("checks." + check.getIdentifier() + ".banTimer", false);
                            Daedalus.getConfig().set("checks." + check.getIdentifier() + ".bannable", false);
                            Daedalus.saveConfig();
                            Daedalus.reloadConfig();
                            check.setAutobanTimer(false);
                            check.setBannable(false);
                            openTimer(player);
                            return;
                        }
                        Daedalus.getConfig().set("checks." + check.getIdentifier() + ".bannable", true);
                        Daedalus.getConfig().set("checks." + check.getIdentifier() + ".banTimer", true);
                        Daedalus.saveConfig();
                        Daedalus.reloadConfig();
                        check.setAutobanTimer(true);
                        check.setBannable(true);
                        openTimer(player);
                        return;
                    }
                }
                if (ChatColor.stripColor(check_name).equals("Back")) {
                    openDaedalusMain(player);
                }
            }
        } else if (e.getInventory().getName().equals(C.Gold + "Recent Bans")) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        } else if (e.getInventory().getName().equals(C.Gold + "Status")) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    public String c(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}