/**
 * MIT License
 *
 * Copyright (c) 2023 CHOOSEIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.CHOOSEIT.elytraracing.gamesystem;

import fr.CHOOSEIT.elytraracing.*;
import fr.CHOOSEIT.elytraracing.GUI.Pages.HologramsChoosePage;
import fr.CHOOSEIT.elytraracing.GUI.Pages.HologramsPage.HologramsPage;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleConfig;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import fr.CHOOSEIT.elytraracing.holograms.HologramsCreator;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkPlayer;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ERConfig implements CommandExecutor, TabCompleter {
    CustomMessageConfig cmc = Main.customMessageConfig;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("erconfig")) {
                if (!player.hasPermission(cmd.getPermission())) {
                    player.sendMessage(cmc.basicsettingnoprefix(cmc.PERMISSION_MESSAGE, player));
                    return true;
                }
                if (args.length == 0) {
                    HelpMessage(player);
                } else if (args[0].equalsIgnoreCase("holograms")) {
                    if (!Holograms.isAvailableVersion()) {
                        player.sendMessage(cmc.basicsettingnoprefix("&cHolograms are no longer available in this version of Minecraft."));
                        player.sendMessage(cmc.basicsettingnoprefix("&cFor alternatives, please see the ElytraRacing's Wiki"));
                        return true;
                    }
                    if (args.length == 1) {
                        HelpMessage(player);
                        return true;
                    } else if (args[1].equalsIgnoreCase("config")) {
                        if (args.length == 3) {
                            Holograms h = Holograms.getHolo(args[2]);
                            if (h != null) {
                                HologramsPage hp = new HologramsPage(player, h);
                                hp.open();
                                return true;
                            } else {
                                player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().HOLOGRAM_NOT_FOUND, player));
                            }
                        }
                        HologramsChoosePage hcp = new HologramsChoosePage(player);
                        hcp.open();
                        hcp.displayPage(1);
                    } else if (args[1].equalsIgnoreCase("setname")) {
                        if (args.length == 4) {
                            Holograms h = Holograms.getHolo(args[2]);
                            if (h != null) {
                                HologramsCreator hc = Main.hologramsSaver.getHologramCreator(h);
                                if (hc != null) {
                                    h.name = args[3];
                                    hc.setName(args[3]);
                                    Main.serialization.saveHolograms();
                                    h.update();
                                    player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().MSG_SAVE_SUCCESSFUL, player));
                                } else {
                                    player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().ERROR_MESSAGE, player));
                                }

                            } else {
                                player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().HOLOGRAM_NOT_FOUND, player));
                            }
                        } else {
                            HelpMessage(player);
                        }
                    } else if (args[1].equalsIgnoreCase("setmap")) {
                        if (args.length == 4) {
                            Holograms h = Holograms.getHolo(args[2]);
                            if (h != null) {
                                Map m = Map.getMapName(args[3]);
                                if (m != null) {
                                    HologramsCreator hc = Main.hologramsSaver.getHologramCreator(h);
                                    if (hc != null) {
                                        h.map = m;
                                        hc.setOption_map(m.getName());
                                        Main.serialization.saveHolograms();
                                        h.update();
                                        player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().MSG_SAVE_SUCCESSFUL, player));
                                    } else {
                                        player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().ERROR_MESSAGE, player));
                                    }

                                } else {
                                    player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().MSG_MAPNOTFOUND, player));
                                }

                            } else {
                                player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().HOLOGRAM_NOT_FOUND, player));
                            }
                        } else {
                            HelpMessage(player);
                        }
                    } else {
                        HelpMessage(player);
                    }
                } else if (args[0].equalsIgnoreCase("setitem") && args.length == 2) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item == null) {
                        player.sendMessage(cmc.basicsettingnoprefix(cmc.ERCONFIG_SETITEM_ITEM_NOT_FOUND, player));
                    }
                    Material m = Utils.getMaterial(item.toString().replace("ItemStack{", "").split(" ")[0]);
                    if (m != null && m != Material.AIR) {
                        switch (args[1].toUpperCase()) {
                            case "1CP_BACK":
                                cmc.ITEM_1CP_BACK_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "2CP_BACK":
                                cmc.ITEM_2CP_BACK_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "RESTART":
                                cmc.ITEM_RESTART_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "DNF":
                                cmc.ITEM_DNF_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "BED":
                                cmc.ITEM_BED_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "SPEC":
                                cmc.ITEM_SPEC_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "CUSTOM":
                                cmc.ITEM_CUSTOM_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "HOST":
                                cmc.ITEM_HOST_MATERIAL = itemToStringFormat(item);
                                Main.instance.getSerialization().saveMessageConfig();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                break;
                            case "PRINT":
                                String sItem = itemToStringFormat(item);
                                player.sendMessage("Item: " + sItem);
                                Utils.log_print("Item: " + sItem);
                                break;
                            default:
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.ERCONFIG_SETITEM_ID_NOT_FOUND, player));
                                break;
                        }
                    } else {
                        player.sendMessage(cmc.basicsettingnoprefix(cmc.ERCONFIG_SETITEM_ITEM_NOT_FOUND, player));
                    }
                } else if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
                    CustomMessageConfig newcmc = Main.serialization.LoadMessageConfig(false);

                    if (newcmc == null) {
                        player.sendMessage(cmc.basicsettingnoprefix(cmc.ERROR_MESSAGE, player));
                        return true;
                    }
                    if (!cmc.replaceBy(newcmc)) {
                        player.sendMessage(cmc.basicsettingnoprefix(cmc.ERROR_MESSAGE, player));
                        return true;
                    }
                    cmc.save();
                    player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                    AdminGUIConfig.load();
                    ParticleConfig.load();
                    ScoreboardConfig.load();
                } else if (args[0].equalsIgnoreCase("gmi") && args.length == 1) {
                    LinkPlayer.giveItems(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("lprecision") && args.length == 2 && Utils.IsDouble(args[1])) {
                    double precisionnew = Double.parseDouble(args[1]);
                    LinkPlayer.getLinkPlayer(player).setPRECISION_L(precisionnew);
                    player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().MSG_SAVE_SUCCESSFUL, player));
                } else if (args[0].equalsIgnoreCase("dprecision") && args.length == 2 && Utils.IsDouble(args[1])) {
                    double precisionnew = Double.parseDouble(args[1]);
                    LinkPlayer.getLinkPlayer(player).setPRECISION_D(precisionnew);
                    player.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().MSG_SAVE_SUCCESSFUL, player));
                } else if (args[0].equalsIgnoreCase("db")) {
                    clearDBCommand(sender, args);
                } else {
                    HelpMessage(player);
                }
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            clearDBCommand(sender, args);
        }
        return false;
    }

    public void clearDBCommand(CommandSender commandSender, String[] args) {
        if (args[0].equalsIgnoreCase("db")) {
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("clear")) {
                    if (args[2].equalsIgnoreCase("score")) {
                        try {
                            Main.currentDataBase.clearDB(DataBase.DBTypeClear.SCORE);
                        } catch (SQLException e) {
                            Utils.log_print("Something went wrong while clearing the database !");
                        }
                        commandSender.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, null));
                    }
                }
            } else {
                if (commandSender instanceof Player) {
                    HelpMessage((Player) commandSender);
                }
            }

        }
    }

    public String itemToStringFormat(ItemStack is) {
        int dura = is.getDurability();
        String s = is.serialize().get("type") + ":" + dura;
        if (is.hasItemMeta() && is.getItemMeta() != null) {
            ItemMeta im = is.getItemMeta();
            if (Utils.hasCustomModelData(im)) {
                s += ":" + Utils.getCustomModelData(im);
            }
        }
        return s;

    }

    public void HelpMessage(Player p) {
        p.sendMessage("§6§m------------------------------------");
        p.sendMessage(" ");

        p.sendMessage("§7Help §6Configuration§7:");
        p.sendMessage(" ");

        TextComponent cmd = new TextComponent("§f/erconfig setitem §c[id_item] §7: Define custom items.");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig setitem"));
        p.spigot().sendMessage(cmd);
        cmd = new TextComponent("§f/erconfig reload §7: Reload Config.json and AdminGUI.json files. §c(Experimental)");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig reload"));
        p.spigot().sendMessage(cmd);
        cmd = new TextComponent("§f/erconfig holograms config §6<hologram> §7: Hologram configuration.");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig holograms config"));
        p.spigot().sendMessage(cmd);
        cmd = new TextComponent("§f/erconfig holograms setname §c[hologram] [new_name] §7: Change name of a hologram.");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig holograms setname"));
        p.spigot().sendMessage(cmd);
        cmd = new TextComponent("§f/erconfig holograms setmap §c[hologram] [map] §7: Change map associated to a hologram");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig holograms setmap"));
        p.spigot().sendMessage(cmd);
        cmd = new TextComponent("§f/erconfig gmi §7: Get items to modify objects");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/erconfig gmi"));
        p.spigot().sendMessage(cmd);
        cmd = new TextComponent("§f/erconfig lprecision §c[precision] §7: Modify location precision");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig lprecision"));
        p.spigot().sendMessage(cmd);
        cmd = new TextComponent("§f/erconfig dprecision §c[precision] §7: Modify degree precision");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erconfig dprecision"));
        p.spigot().sendMessage(cmd);

        p.sendMessage(" ");
        TextComponent message = new TextComponent("§7More information ");
        TextComponent click = new TextComponent("§e§nHere");
        click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://chooseit.io/er/doc/commands/command-erconfig"));
        message.addExtra(click);
        p.spigot().sendMessage(message);
        p.sendMessage("§7§oClick on commands to execute them");
        p.sendMessage("§6<>: Optional argument");
        p.sendMessage("§c[]: Required argument");
        p.sendMessage("§6§m------------------------------------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("erconfig")) {
                if (args.length == 1) {
                    return Arrays.asList("setitem", "reload", "holograms", "gmi", "lprecision", "dprecision");
                } else if (args.length == 2 && args[0].equalsIgnoreCase("setitem")) {
                    return Arrays.asList("1CP_BACK", "2CP_BACK", "RESTART", "DNF", "BED", "SPEC", "CUSTOM", "HOST");
                } else if (args.length == 2 && args[0].equalsIgnoreCase("holograms")) {
                    return Arrays.asList("config", "setname", "setmap");
                }

            }
        }
        return null;
    }
}
