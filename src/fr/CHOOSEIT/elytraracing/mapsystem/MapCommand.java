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

package fr.CHOOSEIT.elytraracing.mapsystem;

import fr.CHOOSEIT.elytraracing.*;
import fr.CHOOSEIT.elytraracing.GUI.Page;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapChoosePage;
import fr.CHOOSEIT.elytraracing.GUI.Pages.MapPage;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import fr.CHOOSEIT.elytraracing.packetHandler.PacketHandler;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class MapCommand implements CommandExecutor {

    CustomMessageConfig cmc = Main.customMessageConfig;

    public static HashMap<Player, Integer> ShowMapCP = new HashMap<>();
    public static HashMap<Player, Map> ShowMapCPMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("ermap")) {
                if (!player.hasPermission(cmd.getPermission())) {
                    player.sendMessage(cmc.basicsettingnoprefix(cmc.PERMISSION_MESSAGE, player));
                    return true;
                }
                if (args.length == 0) {
                    HelpMessage(player, HelpMessage.DEFAULT, null);
                } else {
                    if (args[0].equalsIgnoreCase("list")) {
                        ListMap(player);
                        return true;
                    } else if (args[0].equalsIgnoreCase("create")) {
                        if (args.length <= 1) {
                            HelpMessage(player, HelpMessage.MAP, null);
                            return true;
                        } else {
                            Map map = Map.createMap(player, args[1]);
                            Main.instance.getSerialization().saveMap(map);
                            player.sendMessage(cmc.basicsetting(cmc.MSG_MAPCREATING, player));
                            return true;
                        }

                    }
                    if (args[0].equalsIgnoreCase("help")) {
                        if (args.length == 2) {
                            if (args[1].equalsIgnoreCase("mapsystem")) {
                                HelpMessage(player, HelpMessage.MAP, null);
                            } else if (args[1].equalsIgnoreCase("completemapsystem")) {
                                HelpMessage(player, HelpMessage.COMPLETE_MAP, null);
                            } else if (args[1].equalsIgnoreCase("endsystem")) {
                                HelpMessage(player, HelpMessage.END, null);
                            } else if (args[1].equalsIgnoreCase("checkpointsystem")) {
                                HelpMessage(player, HelpMessage.CHECKPOINT, null);
                            } else {
                                HelpMessage(player, HelpMessage.DEFAULT, null);
                            }
                        } else if (args.length == 3) {
                            Map m = Map.Find(args[2]);
                            if (m == null) {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_MAPNOTFOUND, player));
                            }
                            if (args[1].equalsIgnoreCase("mapsystem")) {
                                HelpMessage(player, HelpMessage.MAP, m);
                            } else if (args[1].equalsIgnoreCase("completemapsystem")) {
                                HelpMessage(player, HelpMessage.COMPLETE_MAP, m);
                            } else if (args[1].equalsIgnoreCase("endsystem")) {
                                HelpMessage(player, HelpMessage.END, m);
                            } else if (args[1].equalsIgnoreCase("checkpointsystem")) {
                                HelpMessage(player, HelpMessage.CHECKPOINT, m);
                            } else {
                                HelpMessage(player, HelpMessage.DEFAULT, m);
                            }
                        } else if (args.length == 4) {
                            Map m = Map.Find(args[2]);
                            if (m == null) {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_MAPNOTFOUND, player));
                            }
                            int id = -1;
                            if (Utils.IsInteger(args[3])) {
                                id = Integer.parseInt(args[3]);
                            }
                            if (args[1].equalsIgnoreCase("mapsystem")) {
                                HelpMessage(player, HelpMessage.MAP, m, id);
                            } else if (args[1].equalsIgnoreCase("completemapsystem")) {
                                HelpMessage(player, HelpMessage.COMPLETE_MAP, m, id);
                            } else if (args[1].equalsIgnoreCase("endsystem")) {
                                HelpMessage(player, HelpMessage.END, m, id);
                            } else if (args[1].equalsIgnoreCase("checkpointsystem")) {
                                HelpMessage(player, HelpMessage.CHECKPOINT, m, id);
                            } else {
                                HelpMessage(player, HelpMessage.DEFAULT, m, id);
                            }
                        } else {
                            HelpMessage(player, HelpMessage.DEFAULT, null);
                        }

                    } else if (args[0].equalsIgnoreCase("map")) {
                        if (args.length == 2) {

                            Map m = Map.Find(args[1]);
                            if (m == null) {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_MAPNOTFOUND, null));
                            }
                            HelpMessage(player, HelpMessage.DEFAULT, m);
                        } else if (args.length == 1) {
                            HelpMessage(player, HelpMessage.DEFAULT, null);
                        } else {
                            Map m = Map.Find(args[1]);
                            if (m == null) {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_MAPNOTFOUND, player));
                                return true;
                            }
                            if (args.length == 3) {
                                if (args[2].equalsIgnoreCase("listcheckpoint")) {
                                    ListCheckpoint(player, m, 1);
                                } else if (args[2].equalsIgnoreCase("cleardb")) {
                                    int map_id = Main.currentDataBase.get_map_id(m);
                                    if (map_id > 0) {
                                        Main.currentDataBase.Deleteinformations_Map(map_id);
                                        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        return true;
                                    }

                                } else if (args[2].equalsIgnoreCase("config")) {
                                    Page page = new MapPage(player, m);
                                    page.open();
                                    return true;
                                } else if (args[2].equalsIgnoreCase("listend")) {
                                    ListEnd(player, m, 1);
                                } else if (args[2].equalsIgnoreCase("addcheckpoint")) {
                                    Location loc = player.getLocation();
                                    CheckPoint cp = new CheckPoint((float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), loc.getWorld().getName(), 3, 1, 0, 0, 0, 0.1f);
                                    m.addCheckpoint(cp);
                                    Main.instance.getSerialization().saveMap(m);
                                    HelpMessage(player, HelpMessage.CHECKPOINT, m, m.getCpNumber() - 1);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    return true;

                                } else if (args[2].equalsIgnoreCase("addend")) {
                                    Location loc = player.getLocation();
                                    End end = new End((float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), loc.getWorld().getName(), 3, 1, 0, 0, 0);
                                    m.addEnds(end);
                                    Main.instance.getSerialization().saveMap(m);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    return true;

                                } else if (args[2].equalsIgnoreCase("setlocationlobby")) {
                                    m.setLocation_lobby(player.getLocation());
                                    Main.instance.getSerialization().saveMap(m);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    return true;
                                } else if (args[2].equalsIgnoreCase("delete")) {

                                    Main.instance.getSerialization().Delete(m);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    player.sendMessage(cmc.basicsetting(cmc.ERMAP_MAP_DELETE, player));
                                    return true;
                                } else if (args[2].equalsIgnoreCase("setlocationstart")) {
                                    m.setLocation_start(player.getLocation());
                                    Main.instance.getSerialization().saveMap(m);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    return true;
                                } else if (args[2].equalsIgnoreCase("setlocationend")) {
                                    m.setLocation_end(player.getLocation());
                                    Main.instance.getSerialization().saveMap(m);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    return true;
                                } else if (args[2].equalsIgnoreCase("tplobby")) {
                                    if (m.getLocation_lobby() == null) {
                                        player.sendMessage(cmc.basicsetting(cmc.LOCATIONNOTDEFINED, player));
                                        return true;
                                    }
                                    Location loc = m.getLocation_lobby();
                                    if (loc.getWorld() != null) {
                                        player.teleport(loc);
                                        player.sendMessage(cmc.basicsetting(cmc.TELEPORTING, player));
                                        return true;
                                    } else {
                                        Utils.log_print("[ERROR][ElytraRacing] This world isn't loaded");
                                    }

                                } else if (args[2].equalsIgnoreCase("tpstart")) {
                                    if (m.getLocation_start() == null) {
                                        player.sendMessage(cmc.basicsetting(cmc.LOCATIONNOTDEFINED, player));
                                        return true;
                                    }
                                    Location loc = m.getLocation_start();
                                    if (loc.getWorld() != null) {
                                        player.teleport(loc);
                                        player.sendMessage(cmc.basicsetting(cmc.TELEPORTING, player));
                                        return true;
                                    } else {
                                        Utils.log_print("[ERROR][ElytraRacing] This world isn't loaded");
                                    }
                                } else if (args[2].equalsIgnoreCase("tpend")) {
                                    if (m.getLocation_end() == null) {
                                        player.sendMessage(cmc.basicsetting(cmc.LOCATIONNOTDEFINED, player));
                                        return true;
                                    }
                                    Location loc = m.getLocation_end();
                                    if (loc.getWorld() != null) {
                                        player.teleport(loc);
                                        player.sendMessage(cmc.basicsetting(cmc.TELEPORTING, player));
                                        return true;
                                    } else {
                                        Utils.log_print("[ERROR][ElytraRacing] This world isn't loaded");
                                    }
                                } else if (args[2].equalsIgnoreCase("show")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else if (args[2].equalsIgnoreCase("setdifficulty")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else if (args[2].equalsIgnoreCase("enable")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else if (args[2].equalsIgnoreCase("podium")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else if (args[2].equalsIgnoreCase("insertcheckpoint")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else if (args[2].equalsIgnoreCase("removeend")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else if (args[2].equalsIgnoreCase("removecheckpoint")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else if (args[2].equalsIgnoreCase("end")) {
                                    HelpMessage(player, HelpMessage.END, m);
                                } else if (args[2].equalsIgnoreCase("cp") || args[2].equalsIgnoreCase("checkpoint")) {
                                    HelpMessage(player, HelpMessage.CHECKPOINT, m);
                                } else if (args[2].equalsIgnoreCase("settimemax")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else {
                                    HelpMessage(player, HelpMessage.DEFAULT, m);
                                }

                            } else if (args.length == 4) {
                                if (args[2].equalsIgnoreCase("show")) {
                                    if (args[3].equalsIgnoreCase("on")) {
                                        show(player, m);
                                        return true;
                                    } else if (args[3].equalsIgnoreCase("off")) {
                                        hide(player);
                                        return true;
                                    } else {
                                        HelpMessage(player, HelpMessage.MAP, m);
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("setdifficulty")) {
                                    if (Utils.IsInteger(args[3]) && Integer.parseInt(args[3]) > 0 && Integer.parseInt(args[3]) <= cmc.MAP_DIFFICULTY_MAX) {
                                        m.setDifficulty(Integer.parseInt(args[3]));
                                        Main.instance.getSerialization().saveMap(m);
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        return true;
                                    } else {
                                        HelpMessage(player, HelpMessage.MAP, m);
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("listcheckpoint")) {
                                    if (Utils.IsInteger(args[3]) && Integer.parseInt(args[3]) > 0) {
                                        ListCheckpoint(player, m, Integer.parseInt(args[3]));
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.PAGE_ARG, player));
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("listend")) {
                                    if (Utils.IsInteger(args[3]) && Integer.parseInt(args[3]) > 0) {
                                        ListEnd(player, m, Integer.parseInt(args[3]));
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.PAGE_ARG, player));
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("enable")) {
                                    if (args[3].equalsIgnoreCase("on")) {
                                        m.setEnabled(true);
                                        Main.instance.getSerialization().saveMap(m);
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        return true;
                                    } else if (args[3].equalsIgnoreCase("off")) {
                                        m.setEnabled(false);
                                        Main.instance.getSerialization().saveMap(m);
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        return true;
                                    } else {
                                        HelpMessage(player, HelpMessage.MAP, m);
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("podium")) {
                                    if (args[3].equalsIgnoreCase("on")) {
                                        m.setPodium(true);
                                        Main.instance.getSerialization().saveMap(m);
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        return true;
                                    } else if (args[3].equalsIgnoreCase("off")) {
                                        m.setPodium(false);
                                        Main.instance.getSerialization().saveMap(m);
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        return true;
                                    } else if (args[3].equalsIgnoreCase("setlocation")) {
                                        HelpMessage(player, HelpMessage.MAP, m);
                                        return true;
                                    } else {
                                        HelpMessage(player, HelpMessage.MAP, m);
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("insertcheckpoint")) {
                                    if (Utils.IsInteger(args[3])) {
                                        int id_order = Integer.parseInt(args[3]);
                                        Location loc = player.getLocation();
                                        CheckPoint cp = new CheckPoint((float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), loc.getWorld().getName(), 3, 1, 0, 0, 0, 0.1f);
                                        m.InsertCheckpoint(cp, id_order);
                                        Main.instance.getSerialization().saveMap(m);
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        return true;
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_CHECKPOINT_NOTFOUND, player));
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("removeend")) {
                                    int id_order = Integer.parseInt(args[3]);
                                    if (id_order >= m.getEndsList().size() || id_order < 0) {
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_END_NOTFOUND, player));
                                        return true;
                                    }
                                    m.getEndsList().get(id_order).Delete();
                                    m.getEndsList().remove(id_order);
                                    Main.instance.getSerialization().saveMap(m);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    return true;


                                } else if (args[2].equalsIgnoreCase("removecheckpoint")) {
                                    int id_order = Integer.parseInt(args[3]);
                                    if (id_order >= m.getCheckpointsList().size() || id_order < 0) {
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_CHECKPOINT_NOTFOUND, player));
                                        return true;
                                    }
                                    m.getCheckpointsList().get(id_order).Delete();
                                    m.getCheckpointsList().remove(id_order);
                                    Main.instance.getSerialization().saveMap(m);
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                    return true;

                                } else if (args[2].equalsIgnoreCase("cp") || args[2].equalsIgnoreCase("checkpoint")) {
                                    HelpMessage(player, HelpMessage.CHECKPOINT, m);
                                } else if (args[2].equalsIgnoreCase("settimemax")) {
                                    HelpMessage(player, HelpMessage.MAP, m);
                                    return true;
                                } else {
                                    HelpMessage(player, HelpMessage.DEFAULT, m);
                                }
                            } else if (args.length == 5) {
                                if (args[2].equalsIgnoreCase("checkpoint") || args[2].equalsIgnoreCase("cp")) {
                                    if (Utils.IsInteger(args[3])) {
                                        int id_order = Integer.parseInt(args[3]);
                                        if (id_order >= m.getCheckpointsList().size() || id_order < 0) {
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_CHECKPOINT_NOTFOUND, player));
                                            return true;
                                        }
                                        if (args[4].equalsIgnoreCase("setlocation")) {
                                            m.getCheckpointsList().get(id_order).setLocation(player.getLocation());
                                            Main.instance.getSerialization().saveMap(m);
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        } else if (args[4].equalsIgnoreCase("setparticle") || args[4].equalsIgnoreCase("setparticleifnext")) {
                                            Utils.ParticleListClickable(player, id_order, args[4], args[2], m);
                                        } else if (args[4].equalsIgnoreCase("tp")) {
                                            Location loc = m.getCheckpointsList().get(id_order).getLocation();
                                            if (loc.getWorld() != null) {
                                                player.teleport(loc);
                                                player.sendMessage(cmc.basicsetting(cmc.TELEPORTING, player));
                                            } else {
                                                Utils.log_print("[ERROR][ElytraRacing] This world isn't loaded");
                                            }
                                        } else {
                                            HelpMessage(player, HelpMessage.CHECKPOINT, m);
                                        }
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.ID_ORDER_ARG, player));
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("podium") && args[3].equalsIgnoreCase("tp")) {
                                    if (Utils.IsInteger(args[4])) {
                                        int placement = Integer.parseInt(args[4]);
                                        if (placement <= 0) {
                                            HelpMessage(player, HelpMessage.MAP, m);
                                            return true;
                                        }
                                        Location locc = m.getLocations_podium(placement);
                                        if (locc != null) {
                                            player.teleport(locc);
                                            player.sendMessage(cmc.basicsetting(cmc.TELEPORTING, player));
                                            return true;
                                        }

                                    } else {
                                        HelpMessage(player, HelpMessage.MAP, m);
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("podium")) {
                                    if (args[3].equalsIgnoreCase("setlocation")) {
                                        if (Utils.IsInteger(args[4])) {
                                            int placement = Integer.parseInt(args[4]);
                                            if (placement <= 0) {
                                                HelpMessage(player, HelpMessage.MAP, m);
                                                return true;
                                            }
                                            m.setLocations_podium(placement, new SimpleLocation(player.getLocation()));
                                            Main.instance.getSerialization().saveMap(m);
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            return true;
                                        } else {
                                            HelpMessage(player, HelpMessage.MAP, m);
                                            return true;
                                        }
                                    } else {
                                        HelpMessage(player, HelpMessage.MAP, m);
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("settimemax")) {
                                    if (Utils.IsInteger(args[3])) {
                                        if (Utils.IsInteger(args[4])) {
                                            int minutes = Integer.parseInt(args[3]);
                                            int seconds = Integer.parseInt(args[4]);
                                            if (seconds > 59 || seconds < 0) {
                                                player.sendMessage(cmc.basicsetting(cmc.SECONDS_ARG, player));
                                                return true;
                                            }
                                            if (minutes < 0) {
                                                player.sendMessage(cmc.basicsetting(cmc.MINUTES_ARG, player));
                                                return true;
                                            }
                                            m.setMAP_TIME_MAX_MINUTES(minutes);
                                            m.setMAP_TIME_MAX_SECONDS(seconds);
                                            Main.instance.getSerialization().saveMap(m);
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            return true;
                                        } else {
                                            player.sendMessage(cmc.basicsetting(cmc.SECONDS_ARG, player));
                                            return true;
                                        }

                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.MINUTES_ARG, player));
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("end")) {
                                    if (Utils.IsInteger(args[3])) {
                                        int id_order = Integer.parseInt(args[3]);
                                        if (id_order >= m.getEndsList().size() || id_order < 0) {
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_END_NOTFOUND, player));
                                            return true;
                                        }
                                        if (args[4].equalsIgnoreCase("setlocation")) {
                                            m.getEndsList().get(id_order).setLocation(player.getLocation());
                                            Main.instance.getSerialization().saveMap(m);
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        } else if (args[4].equalsIgnoreCase("setparticle") || args[4].equalsIgnoreCase("setparticleifnext")) {
                                            Utils.ParticleListClickable(player, id_order, args[4], args[2], m);
                                        } else if (args[4].equalsIgnoreCase("tp")) {
                                            Location loc = m.getEndsList().get(id_order).getLocation();
                                            if (loc.getWorld() != null) {
                                                player.teleport(loc);
                                                player.sendMessage(cmc.basicsetting(cmc.TELEPORTING, player));
                                            } else {
                                                Utils.log_print("[ERROR][ElytraRacing] This world isn't loaded");
                                            }
                                        } else {
                                            HelpMessage(player, HelpMessage.END, m);
                                        }
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.ID_ORDER_ARG, player));
                                        return true;
                                    }
                                }
                            } else if (args.length == 6) {
                                if (args[2].equalsIgnoreCase("checkpoint") || args[2].equalsIgnoreCase("cp")) {

                                    if (Utils.IsInteger(args[3])) {
                                        int id_order = Integer.parseInt(args[3]);
                                        if (id_order >= m.getCheckpointsList().size() || id_order < 0) {
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_CHECKPOINT_NOTFOUND, player));
                                            return true;
                                        }
                                        //Utils.log_print(args[4].equalsIgnoreCase("setparticle"));
                                        if (args[4].equalsIgnoreCase("setsize")) {
                                            if (Utils.IsFloat(args[5])) {
                                                float size = Float.parseFloat(args[5]);

                                                m.getCheckpointsList().get(id_order).setRadius_size(size);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                return true;
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.SIZE_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setparticle")) {
                                            if (Main.availableParticle.contains(args[5])) {
                                                m.getCheckpointsList().get(id_order).enumParticle = args[5];
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else if (args[5].equalsIgnoreCase("default") || args[5].equalsIgnoreCase("null")) {
                                                m.getCheckpointsList().get(id_order).enumParticle = null;
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else if (args[5].equalsIgnoreCase("INVISIBLE")) {
                                                m.getCheckpointsList().get(id_order).enumParticle = "INVISIBLE";
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                return true;
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.PARTICLE_LIST_NOTFOUND, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setparticleifnext")) {
                                            if (Main.availableParticle.contains(args[5])) {
                                                m.getCheckpointsList().get(id_order).enumParticle_IFNEXT = args[5];
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                return true;
                                            } else if (args[5].equalsIgnoreCase("default") || args[5].equalsIgnoreCase("null")) {
                                                m.getCheckpointsList().get(id_order).enumParticle_IFNEXT = null;
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                return true;
                                            } else if (args[5].equalsIgnoreCase("INVISIBLE")) {
                                                m.getCheckpointsList().get(id_order).enumParticle = "INVISIBLE";
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                return true;
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.PARTICLE_LIST_NOTFOUND, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setparticleamount")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int size = Integer.parseInt(args[5]);

                                                m.getCheckpointsList().get(id_order).setRadius_bold(size);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                return true;
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.AMOUNT_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setXrotation")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int degrees = Integer.parseInt(args[5]);

                                                m.getCheckpointsList().get(id_order).setX_degrees(degrees);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.DEGREES_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setYrotation")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int degrees = Integer.parseInt(args[5]);

                                                m.getCheckpointsList().get(id_order).setY_degrees(degrees);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.DEGREES_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setZrotation")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int degrees = Integer.parseInt(args[5]);
                                                m.getCheckpointsList().get(id_order).setZ_degrees(degrees);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.DEGREES_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("Setboostmultiplier")) {
                                            if (Utils.IsFloat(args[5])) {
                                                float mul = Float.parseFloat(args[5]);
                                                m.getCheckpointsList().get(id_order).setBoostMultiplier(mul);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.MULTIPLIER_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("linkto")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int id_order_target = Integer.parseInt(args[5]);
                                                if (!(id_order_target >= m.getCheckpointsList().size() || id_order_target < 0)) {
                                                    if (id_order != id_order_target) {
                                                        m.getCheckpointsList().get(id_order).AddLink(id_order, id_order_target);
                                                        m.getCheckpointsList().get(id_order_target).AddLink(id_order_target, id_order);
                                                        Main.instance.getSerialization().saveMap(m);
                                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                    }
                                                }

                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.ID_ORDER_TARGET_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("removelinkto")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int id_order_target = Integer.parseInt(args[5]);
                                                if (!(id_order_target >= m.getCheckpointsList().size() || id_order_target < 0)) {
                                                    if (id_order != id_order_target) {
                                                        m.getCheckpointsList().get(id_order).RemoveLink(id_order_target);
                                                        m.getCheckpointsList().get(id_order_target).RemoveLink(id_order);
                                                        Main.instance.getSerialization().saveMap(m);
                                                        player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                                    }

                                                }

                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.ID_ORDER_TARGET_ARG, player));
                                                return true;
                                            }
                                        } else {
                                            HelpMessage(player, HelpMessage.CHECKPOINT, m);
                                        }
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.ID_ORDER_ARG, player));
                                        return true;
                                    }
                                } else if (args[2].equalsIgnoreCase("end")) {
                                    if (Utils.IsInteger(args[3])) {
                                        int id_order = Integer.parseInt(args[3]);
                                        if (id_order >= m.getEndsList().size() || id_order < 0) {
                                            player.sendMessage(cmc.basicsetting(cmc.MSG_CHECKPOINT_NOTFOUND, player));
                                            return true;
                                        }
                                        if (args[4].equalsIgnoreCase("setsize")) {
                                            if (Utils.IsFloat(args[5])) {
                                                float size = Float.parseFloat(args[5]);

                                                m.getEndsList().get(id_order).setRadius_size(size);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.SIZE_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setparticleamount")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int size = Integer.parseInt(args[5]);

                                                m.getEndsList().get(id_order).setRadius_bold(size);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.AMOUNT_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setXrotation")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int degrees = Integer.parseInt(args[5]);

                                                m.getEndsList().get(id_order).setX_degrees(degrees);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.DEGREES_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setparticle")) {
                                            if (Main.availableParticle.contains(args[5])) {
                                                m.getEndsList().get(id_order).enumParticle = args[5];
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else if (args[5].equalsIgnoreCase("default") || args[5].equalsIgnoreCase("null")) {
                                                m.getEndsList().get(id_order).enumParticle = null;
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.PARTICLE_LIST_NOTFOUND, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setparticleifnext")) {
                                            if (Main.availableParticle.contains(args[5])) {
                                                m.getEndsList().get(id_order).enumParticle_IFNEXT = args[5];
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else if (args[5].equalsIgnoreCase("default") || args[5].equalsIgnoreCase("null")) {
                                                m.getEndsList().get(id_order).enumParticle_IFNEXT = null;
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.PARTICLE_LIST_NOTFOUND, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setYrotation")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int degrees = Integer.parseInt(args[5]);

                                                m.getEndsList().get(id_order).setY_degrees(degrees);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.DEGREES_ARG, player));
                                                return true;
                                            }
                                        } else if (args[4].equalsIgnoreCase("setZrotation")) {
                                            if (Utils.IsInteger(args[5])) {
                                                int degrees = Integer.parseInt(args[5]);
                                                m.getEndsList().get(id_order).setZ_degrees(degrees);
                                                Main.instance.getSerialization().saveMap(m);
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                            } else {
                                                player.sendMessage(cmc.basicsetting(cmc.DEGREES_ARG, player));
                                                return true;
                                            }
                                        } else {
                                            HelpMessage(player, HelpMessage.END, m);
                                        }
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.ID_ORDER_ARG, player));
                                        return true;
                                    }
                                } else {
                                    HelpMessage(player, HelpMessage.DEFAULT, m);
                                }
                            } else {
                                HelpMessage(player, HelpMessage.DEFAULT, m);
                            }
                        }
                    } else {
                        HelpMessage(player, HelpMessage.DEFAULT, null);
                    }
                }

                return true;
            }
        }

        return false;
    }

    public static void HelpMessage(Player p, HelpMessage helpMessage, Map map) {
        HelpMessage(p, helpMessage, map, -1);
    }

    public static void HelpMessage(Player p, HelpMessage helpMessage, Map map, int object_id) {
        String URLHELP = "https://chooseit.gitbook.io/elytraracing/commands/command-ermap";
        if (helpMessage == HelpMessage.DEFAULT) {
            p.sendMessage("§6§m------------------------------------");
            String plusarg = "";
            if (map != null) {
                p.sendMessage("§7You are working on: §e" + map.getName());
                plusarg = " " + map.getName();
            } else {
                p.sendMessage("§7You are working on: §cNone");
            }
            p.sendMessage(" ");

            p.sendMessage("§7Help:");
            p.sendMessage(" ");

            p.sendMessage("§cOptional argument(s) will autocomplete your commands");

            TextComponent cmd = new TextComponent("§f/ermap help MapSystem §6<map> §7: Help for the map system");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ermap help MapSystem" + plusarg));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap help CheckpointSystem §6<map> §6<id> §7: Help for the checkpoint system");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ermap help CheckpointSystem" + plusarg));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap help EndSystem §6<map> §6<id> §7: Help for the end system");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ermap help EndSystem" + plusarg));
            p.spigot().sendMessage(cmd);

            p.sendMessage(" ");
            TextComponent message = new TextComponent("§7More information ");
            TextComponent click = new TextComponent("§e§nHere");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URLHELP));
            message.addExtra(click);
            p.spigot().sendMessage(message);
            p.sendMessage("§7§oClick on commands to suggest them");
            p.sendMessage("§6<>: Optional argument");
            p.sendMessage("§c[]: Required argument");
            p.sendMessage("§6§m------------------------------------");


        } else if (helpMessage == HelpMessage.MAP) {
            p.sendMessage("§6§m------------------------------------");

            TextComponent cmd = new TextComponent("§7§o§nClick here to open complete help");
            if (map != null) {
                p.sendMessage("§7You are working on: §e" + map.getName());
                cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ermap help CompleteMapSystem " + map.getName()));
            } else {
                p.sendMessage("§7You are working on: §cNone");
                cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ermap help CompleteMapSystem"));
            }
            p.sendMessage(" ");

            p.spigot().sendMessage(cmd);
            p.sendMessage(" ");

            p.sendMessage("§7Help: §6Map §7§o(Simplified)§7:");
            p.sendMessage(" ");

            cmd = new TextComponent("§f/ermap list §7: List of map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ermap list"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap create §c[map] §7: Create a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ermap create "));
            p.spigot().sendMessage(cmd);

            String startcmd = "/ermap map ";
            if (map != null) {
                startcmd += map.getName() + " ";
            } else {
                startcmd += "[] ";
            }

            cmd = new TextComponent("§f/ermap map §c[map] §fconfig §7: Config a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "config"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fshow §c[on/off] §7: Show a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "show"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fdelete §7: Delete a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "delete"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcleardb §7: Clear saved information");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "cleardb"));
            p.spigot().sendMessage(cmd);

            p.sendMessage(" ");
            TextComponent message = new TextComponent("§7More information ");
            TextComponent click = new TextComponent("§e§nHere");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URLHELP));
            message.addExtra(click);
            p.spigot().sendMessage(message);
            p.sendMessage("§7§oClick on commands to suggest them");
            p.sendMessage("§6<>: Optional argument");
            p.sendMessage("§c[]: Required argument");
            p.sendMessage("§6§m------------------------------------");
        } else if (helpMessage == HelpMessage.COMPLETE_MAP) {
            p.sendMessage("§6§m------------------------------------");

            if (map != null) {
                p.sendMessage("§7You are working on: §e" + map.getName());
            } else {
                p.sendMessage("§7You are working on: §cNone");
            }
            p.sendMessage(" ");

            p.sendMessage("§7Help: §6Map §7:");
            p.sendMessage(" ");

            TextComponent cmd = new TextComponent("§f/ermap list §7: List of map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ermap list"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap create §c[map] §7: Create a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ermap create "));
            p.spigot().sendMessage(cmd);

            String startcmd = "/ermap map ";
            if (map != null) {
                startcmd += map.getName() + " ";
            } else {
                startcmd += "[] ";
            }

            cmd = new TextComponent("§f/ermap map §c[map] §fconfig §7: Config a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "config"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fshow §c[on/off] §7: Show a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "show"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fdelete §7: Delete a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "delete"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcleardb §7: Clear saved information");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "cleardb"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fsetlobbylocation §7: Define lobby location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "setlobbylocation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fsetstartlocation §7: Define start location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "setstartlocation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fsetendlocation §7: Define end location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "setendlocation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §ftplobby §7: Tp to the lobby location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "tplobby"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §ftpstart §7: Tp to the start location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "tpstart"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §ftpend §7: Tp to the end location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "tpend"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fsetmaxtime §c[minutes] [secondes] §7: Define max. time possible");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "setmaxtime"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fenable §c[on/off] §7: Enable or disable a map");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "enable"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fpodium §c[on/off] §7: Enable or disable a podium");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "podium"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fpodium tp §c[placement 1, 2..] §7: Tp to a podium placement");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "podium tp"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fpodium setlocation §c[placement 1, 2..] §7: Define location of a podium placement");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "podium setlocation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fsetdifficulty §c[difficulty] §7: Define difficulty");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "setdifficulty"));
            p.spigot().sendMessage(cmd);

            p.sendMessage(" ");
            TextComponent message = new TextComponent("§7More information ");
            TextComponent click = new TextComponent("§e§nHere");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URLHELP));
            message.addExtra(click);
            p.spigot().sendMessage(message);
            p.sendMessage("§7§oClick on commands to suggest them");
            p.sendMessage("§6<>: Optional argument");
            p.sendMessage("§c[]: Required argument");
            p.sendMessage("§6§m------------------------------------");
        } else if (helpMessage == HelpMessage.CHECKPOINT) {
            p.sendMessage("§6§m------------------------------------");
            if (map != null) {
                p.sendMessage("§7You are working on: §e" + map.getName());
            } else {
                p.sendMessage("§7You are working on: §cNone");
            }
            if (object_id > -1) {
                p.sendMessage("§7Checkpoint id: §e" + object_id);
            } else {
                p.sendMessage("§7Checkpoint id: §cNone");
            }

            p.sendMessage(" ");
            p.sendMessage("§7Help §6Checkpoint§7:");
            p.sendMessage(" ");

            String startcmd = "/ermap map ";
            if (map != null) {
                startcmd += map.getName() + " ";
            } else {
                startcmd += "[] ";
            }
            String idarg = "[]";
            if (object_id >= 0) {
                idarg = String.valueOf(object_id);
            }

            TextComponent cmd = new TextComponent("§f/ermap map §c[map] §flistcheckpoint §6<page> §7: List of checkpoint (Find id(s))");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "listcheckpoint"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §faddcheckpoint §7: Create a checkpoint");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "addcheckpoint"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fremovecheckpoint §c[id] §7: Remove a checkpoint");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "removecheckpoint " + idarg));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §finsertcheckpoint §c[id_wanted] §7: Create a checkpoint with a certain id");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "insertcheckpoint "));
            p.spigot().sendMessage(cmd);


            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §flinkto §c[id_other]§7: Links a checkpoint to another");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " linkto"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fremovelinkto §c[id_other]§7: Remove links between two checkpoint");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " removelinkto"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetparticle §7: Set particle");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setparticleifnext"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetparticleifnext §7: Set particle (if next)");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setparticle"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §ftp §7: Tp. to a checkpoint");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " tp"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetXrotation §c[rotation] §7: Rotate on the X axis");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setXrotation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetYrotation §c[rotation] §7: Rotate on the Y axis");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setYrotation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetZrotation §c[rotation] §7: Rotate on the Z axis");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setZrotation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetboostmultiplier §c[amount] §7: Set boost multiplier");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setboostmultiplier"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetlocation §7: Define location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setlocation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetparticleamount §c[amount] §7: Set particle amount visible");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setparticleamount"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fcheckpoint §c[id] §fsetsize §c[size] §7: Set size");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "checkpoint " + idarg + " setsize"));
            p.spigot().sendMessage(cmd);

            p.sendMessage(" ");
            TextComponent message = new TextComponent("§7More information ");
            TextComponent click = new TextComponent("§e§nHere");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URLHELP));
            message.addExtra(click);
            p.spigot().sendMessage(message);
            p.sendMessage("§7§oClick on commands to suggest them");
            p.sendMessage("§6<>: Optional argument");
            p.sendMessage("§c[]: Required argument");
            p.sendMessage("§6§m------------------------------------");
        } else if (helpMessage == HelpMessage.END) {
            p.sendMessage("§6§m------------------------------------");
            if (map != null) {
                p.sendMessage("§7You are working on: §e" + map.getName());
            } else {
                p.sendMessage("§7You are working on: §cNone");
            }
            if (object_id > -1) {
                p.sendMessage("§7End id: §e" + object_id);
            } else {
                p.sendMessage("§7End id: §cNone");
            }

            p.sendMessage(" ");
            p.sendMessage("§7Help §6End§7:");
            p.sendMessage(" ");

            String startcmd = "/ermap map ";
            if (map != null) {
                startcmd += map.getName() + " ";
            } else {
                startcmd += " ";
            }
            String idarg = "";
            if (object_id >= 0) {
                idarg = String.valueOf(object_id);
            }

            TextComponent cmd = new TextComponent("§f/ermap map §c[map] §flistend §6<page> §7: List of end (Find id(s))");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "listend"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §faddend §7: Create a end");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "addend"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fremoveend §c[id] §7: Remove a end");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "removeend " + idarg));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetparticle §7: Set particle");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setparticleifnext"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetparticleifnext §7: Set particle (if next)");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setparticle"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §ftp §7: Tp. to a end");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " tp"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetXrotation §c[rotation] §7: Rotate on the X axis");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setXrotation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetYrotation §c[rotation] §7: Rotate on the Y axis");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setYrotation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetZrotation §c[rotation] §7: Rotate on the Z axis");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setZrotation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetlocation §7: Define location");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setlocation"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetparticleamount §c[amount] §7: Set particle amount visible");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setparticleamount"));
            p.spigot().sendMessage(cmd);

            cmd = new TextComponent("§f/ermap map §c[map] §fend §c[id] §fsetsize §c[size] §7: Set size");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, startcmd + "end " + idarg + " setsize"));
            p.spigot().sendMessage(cmd);

            p.sendMessage(" ");
            TextComponent message = new TextComponent("§7More information ");
            TextComponent click = new TextComponent("§e§nHere");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URLHELP));
            message.addExtra(click);
            p.spigot().sendMessage(message);
            p.sendMessage("§7§oClick on commands to suggest them");
            p.sendMessage("§6<>: Optional argument");
            p.sendMessage("§c[]: Required argument");
            p.sendMessage("§6§m------------------------------------");
        }
    }

    public void ListCheckpoint(Player player, Map m, int page) {
        if (page <= 0) {
            page = 1;
        }
        ArrayList<CheckPoint> cps = m.getCheckpointsList();
        int start = 0;
        int end = 5;
        int size = 5;
        int max_pages = (int) Math.ceil(cps.size() / 5.0);

        if (cmc != null) {
            start = (page - 1) * cmc.max_info_per_page;
            end = page * cmc.max_info_per_page;
            max_pages = (int) Math.ceil(cps.size() / (float) cmc.max_info_per_page);
        }
        if (start < 0) {
            start = 0;
        }


        assert cmc != null;
        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_PAGE.replace("{PAGE}", Integer.toString(page)).replace("{MAX_PAGE}", Integer.toString(max_pages)), player));


        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_ERMAP_LISTCHECKPOINT, player));
        String msg = cmc.basicsettingnoprefix(cmc.MSG_ERMAP_LISTCHECKPOINT_CHECKPOINTFORMAT, player);
        String BaseMessage, Base, TchatMessage, particle_type, link;

        for (int i = start; i < end; i++) {
            if (i >= cps.size()) {
                break;
            }
            CheckPoint CP = cps.get(i);
            TchatMessage = msg.replace("{CHECKPOINT_ID_ORDER}", Integer.toString(i)).replace("{X}", Float.toString(CP.getX())).replace("{Y}", Float.toString(CP.getY())).replace("{Z}", Float.toString(CP.getZ()));
            BaseMessage = "";
            for (String s1 : cmc.MSG_LISTCHECKPOINT_INFO) {
                BaseMessage += cmc.basicsettingnoprefix(s1, player) + "\n";
            }

            if (CP.enumParticle == null) {
                particle_type = cmc.basicsettingnoprefix(cmc.DEFAULT, player);
            } else {
                particle_type = CP.enumParticle.toString();
            }


            if (CP.getLinkedTo_id_order(m).isEmpty()) {
                link = cmc.basicsettingnoprefix(cmc.NONE, player);
            } else {
                //link = "§a"+CP.wrotelinked().toString().replace("[", "").replace("]", "") + " §7("+CP.getLinkedTo_id_order(m).toString().replace("[", "").replace("]", "");
                link = CP.getLinkedTo_id_order(m).toString().replace("[", "").replace("]", "");
            }
            BaseMessage = BaseMessage.replace("{ID_ORDER}", Integer.toString(i)).replace("{X}", Float.toString(CP.getX())).replace("{Y}", Float.toString(CP.getY())).replace("{Z}", Float.toString(CP.getZ())).replace("{WORLD}", CP.getWorld()).replace("{RADIUS}", Float.toString(CP.getRadiusSize())).replace("{PARTICLE_AMOUNT}", Float.toString(CP.getParticleAmount())).replace("{BOOST_MULTIPLIER}", Float.toString(CP.getBoostMultiplier())).replace("{PARTICLE_TYPE}", particle_type).replace("{LINK}", link);

            Base = "{\"text\":\"{TCHATMESSAGE}\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"{BASETEXT}\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"{COMMAND}\"}}";
            Base = Base.replace("{TCHATMESSAGE}", TchatMessage).replace("{BASETEXT}", BaseMessage);
            Base = Base.replace("{COMMAND}", "/ermap map " + m.getName() + " checkpoint " + i + " tp");

            PacketHandler.sendPacketMessage(player, Base);
        }
    }

    public void ListEnd(Player player, Map m, int page) {
        if (page <= 0) {
            page = 1;
        }
        ArrayList<End> ends = m.getEndsList();
        int start = 0;
        int end = 5;
        int size = 5;
        int max_pages = (int) Math.ceil(ends.size() / 5.0);

        if (cmc != null) {
            start = (page - 1) * cmc.max_info_per_page;
            end = page * cmc.max_info_per_page;
            max_pages = (int) Math.ceil(ends.size() / (float) cmc.max_info_per_page);
        }
        if (start < 0) {
            start = 0;
        }


        assert cmc != null;
        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_PAGE.replace("{PAGE}", Integer.toString(page)).replace("{MAX_PAGE}", Integer.toString(max_pages)), player));
        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_ERMAP_LISTEND, player));
        String msg = cmc.basicsettingnoprefix(cmc.MSG_ERMAP_LISTEND_ENDFORMAT, player);
        String BaseMessage, Base, TchatMessage, particle_type;

        for (int i = start; i < end; i++) {
            if (i >= ends.size()) {
                break;
            }
            End CP = ends.get(i);
            TchatMessage = msg.replace("{END_ID_ORDER}", Integer.toString(i)).replace("{X}", Float.toString(CP.getX())).replace("{Y}", Float.toString(CP.getY())).replace("{Z}", Float.toString(CP.getZ()));
            BaseMessage = "";
            for (String s1 : cmc.MSG_LISTEND_INFO) {
                BaseMessage += cmc.basicsettingnoprefix(s1, player) + "\n";
            }

            if (CP.enumParticle == null) {
                particle_type = cmc.basicsettingnoprefix(cmc.DEFAULT, player);
            } else {
                particle_type = CP.enumParticle.toString();
            }
            BaseMessage = BaseMessage.replace("{ID_ORDER}", Integer.toString(i)).replace("{X}", Float.toString(CP.getX())).replace("{Y}", Float.toString(CP.getY())).replace("{Z}", Float.toString(CP.getZ())).replace("{WORLD}", CP.getWorld()).replace("{RADIUS}", Float.toString(CP.getRadiusSize())).replace("{PARTICLE_AMOUNT}", Float.toString(CP.getParticleAmount())).replace("{PARTICLE_TYPE}", particle_type);

            Base = "{\"text\":\"{TCHATMESSAGE}\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"{BASETEXT}\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"{COMMAND}\"}}";
            Base = Base.replace("{TCHATMESSAGE}", TchatMessage).replace("{BASETEXT}", BaseMessage);
            Base = Base.replace("{COMMAND}", "/ermap map " + m.getName() + " end " + i + " tp");
            PacketHandler.sendPacketMessage(player, Base);
        }
    }

    public void ListMap(Player player) {
        MapChoosePage mcp = new MapChoosePage(player);
        mcp.open();
        mcp.displayPage(1);
    }

    public static enum HelpMessage {
        DEFAULT,
        MAP,
        END,
        CHECKPOINT,
        COMPLETE_MAP
    }

    public static boolean isShowingMap(Player player, Map map) {
        return ShowMapCP.containsKey(player) && ShowMapCPMap.get(player).equals(map);
    }

    public static void show(Player player, Map map) {
        CustomMessageConfig cmc = Main.cmc();
        hide(player);
        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                map.ShowCheckpoints(player, null, System.currentTimeMillis());
            }
        }, 0, cmc.RELOADINTERVALOBJECTS);
        player.sendMessage(cmc.basicsetting(cmc.MSG_SHOW_ON, player));
        ShowMapCP.put(player, id);
        ShowMapCPMap.put(player, map);
    }

    public static void hide(Player player) {
        CustomMessageConfig cmc = Main.cmc();
        if (ShowMapCP.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(ShowMapCP.get(player));
            ShowMapCP.remove(player);
            ShowMapCPMap.remove(player);
            player.sendMessage(cmc.basicsetting(cmc.MSG_SHOW_OFF, player));
        }
    }

    public static Map getShowingMap(Player player) {
        if (ShowMapCPMap.containsKey(player)) {
            return ShowMapCPMap.get(player);
        }
        return null;
    }

}
