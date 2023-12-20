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

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.Pages.HostMapChoosePage;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.StaticMessages;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.GrandPrixMode;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.RaceMode;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.Training;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.packetHandler.PacketHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ERHostCommand implements CommandExecutor {
    CustomMessageConfig cmc = Main.customMessageConfig;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("erhost")) {
                if (!player.hasPermission(Objects.requireNonNull(cmd.getPermission()))) {
                    player.sendMessage(cmc.basicsettingnoprefix(cmc.PERMISSION_MESSAGE, player));
                    return true;
                }
                if (args.length == 0) {
                    HelpMessage(player);
                } else {
                    if (args[0].equalsIgnoreCase("create")) {

                        if (args.length != 2 && args.length <= 3) {
                            player.sendMessage(cmc.basicsettingnoprefix(StaticMessages.USAGE_ERHOST_CREATE, player));
                        }
                        else if(args.length == 2){
                            String name = args[1];
                            HostMapChoosePage hostMapChoosePage = new HostMapChoosePage(player, name);
                            hostMapChoosePage.open();
                            hostMapChoosePage.displayPage(1);
                        }
                        else {
                            String name = args[1];
                            if (Utils.IsInteger(args[2]) && Integer.parseInt(args[2]) > 0) {
                                if (Game.ContainsPlayerGAMELIST(player, PlayerMode.ALL)) {
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_LEAVEYOURGAME, player));
                                    return true;
                                }
                                if (Game.NameAlreadyExist(args[1])) {
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_GAMENAME_EXIST, player));
                                    return true;
                                }
                                int maxplayer = Integer.parseInt(args[2]);
                                Map m = Map.Find(args[3]);
                                if (m == null) {
                                    if (args[3].contains(",")) {
                                        String[] maps = args[3].split(",");
                                        int max = 4;
                                        if (cmc != null) {
                                            max = cmc.Max_maps_per_games;
                                        }
                                        if (max > maps.length) {
                                            max = maps.length;
                                        }
                                        ArrayList<Map> maps_found = new ArrayList<>();
                                        Map map_;
                                        for (int i = 0; i < max; i++) {
                                            map_ = Map.Find(maps[i]);
                                            if (map_ == null) {
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_MAPNOTFOUND2.replace("{ARG}", maps[i]), player));
                                                return true;
                                            }
                                            if (!map_.isAvailable()) {
                                                player.sendMessage(cmc.basicsetting(cmc.MSG_MAP_NOTAVAILABLE2.replace("{MAP}", map_.getName()), player));
                                                return true;
                                            }
                                            maps_found.add(map_);
                                        }
                                        player.sendMessage(cmc.basicsetting(cmc.MSG_HOST_CREATING, player));

                                        Game game = new GrandPrixMode(name, GameDurationType.HOSTDURATION, maxplayer, 0, player, maps_found);
                                        game.rawPlayerjoin(player);
                                        return true;
                                    }
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_MAPNOTFOUND, player));
                                    return true;

                                }
                                if (!m.isAvailable()) {
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_MAP_NOTAVAILABLE, player));
                                    return true;
                                }
                                player.sendMessage(cmc.basicsetting(cmc.MSG_HOST_CREATING, player));


                                Game game = new RaceMode(name, GameDurationType.HOSTDURATION, maxplayer, 0, player, m);

                                game.rawPlayerjoin(player);
                            } else {
                                player.sendMessage(cmc.basicsetting(cmc.MAX_PLAYER_ARG, player));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
                        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_ERHOST_LISTGAMES, player));
                        String msg = cmc.basicsettingnoprefix(cmc.MSG_ERMAP_LISTGAMES_GAMESFORMAT, player);
                        String BaseMessage, Base, TchatMessage;

                        for (Game game : Game.gamelist) {
                            if (game instanceof Training) {
                                continue;
                            }
                            TchatMessage = msg.replace("{GAME_NAME}", game.getName());
                            BaseMessage = "";
                            for (String s1 : cmc.MSG_LISTGAMES_INFO) {
                                BaseMessage += cmc.basicsettingnoprefix(s1, null) + "\n";
                            }
                            BaseMessage = BaseMessage.replace("{GAME_NAME}", game.getName()).replace("{PLAYER_SIZE}", Integer.toString(game.getPlayerList().size())).replace("{MIN_PLAYER}", Integer.toString(game.getMinPlayer())).replace("{MAX_PLAYER}", Integer.toString(game.getMaxPlayer())).replace("{GAME_STATE}", cmc.basicsettingnoprefix(cmc.GameStateToString(game.getGameState()), player));
                            Base = "{\"text\":\"{TCHATMESSAGE}\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"{BASETEXT}\"}}";
                            Base = Base.replace("{TCHATMESSAGE}", TchatMessage).replace("{BASETEXT}", BaseMessage);
                            PacketHandler.sendPacketMessage(player, Base);
                        }
                    } else if (args[0].equalsIgnoreCase("start")) {
                        Game game = Game.Find(player);
                        if (game != null) {
                            if (game.isHost(player) && game.getGameDurationType() == GameDurationType.HOSTDURATION) {
                                if (game.getGameState() == GameState.WAITING) {
                                    game.askStartTimer();
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_TIMERSTART, player));
                                }

                            } else {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_NOTYOURHOST, player));
                                return true;
                            }
                        } else {
                            player.sendMessage(cmc.basicsetting(cmc.MSG_HOST_NOTEXIST, player));
                        }
                    } else if (args[0].equalsIgnoreCase("setperm")) {
                        Game game = Game.Find(player);
                        if (game != null) {
                            if (game.isHost(player) && game.getGameDurationType() == GameDurationType.HOSTDURATION) {
                                if (args.length < 2) {
                                    player.sendMessage(cmc.basicsetting(StaticMessages.USAGE_ERHOST_PERMISSION, player));
                                    return true;
                                }
                                String permission = args[1];
                                if (permission.equalsIgnoreCase("none")) {
                                    permission = null;
                                }
                                game.setPermission(permission);
                                player.sendMessage(cmc.basicsetting(cmc.MSG_SAVE_SUCCESSFUL, player));
                                return true;
                            } else {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_NOTYOURHOST, player));
                                return true;
                            }
                        } else {
                            player.sendMessage(cmc.basicsetting(cmc.MSG_HOST_NOTEXIST, player));
                        }
                    } else if (args[0].equalsIgnoreCase("kick")) {
                        Game game = Game.Find(player);
                        if (game != null) {

                            if (game.isHost(player) && game.getGameDurationType() == GameDurationType.HOSTDURATION) {
                                if (args.length < 2) {
                                    player.sendMessage(cmc.basicsetting(StaticMessages.USAGE_ERHOST_KICK, player));
                                    return true;
                                }
                                Player target = Bukkit.getPlayer(args[1]);
                                if (target != null) {
                                    Game game1 = Game.Find(target);
                                    if (game == game1) {
                                        game.PlayerLeave(target);
                                    } else {
                                        player.sendMessage(cmc.basicsetting(cmc.ERHOST_PLAYER_NOTFOUND, player));
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(cmc.basicsetting(cmc.PLAYER_NOTFOUND, player));
                                    return true;
                                }


                            } else {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_NOTYOURHOST, player));
                                return true;
                            }
                        } else {
                            player.sendMessage(cmc.basicsetting(cmc.MSG_HOST_NOTEXIST, player));
                        }
                    } else if (args[0].equalsIgnoreCase("alert")) {
                        Game game = Game.Find(player);
                        if (game != null) {
                            if (game.isHost(player) && game.getGameDurationType() == GameDurationType.HOSTDURATION) {
                                if (cmc != null) {
                                    sendAlert(player, game);

                                }

                            } else {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_NOTYOURHOST, player));
                                return true;
                            }
                        } else {
                            player.sendMessage(cmc.basicsetting(cmc.MSG_HOST_NOTEXIST, player));
                        }
                    } else if (args[0].equalsIgnoreCase("stoptimer")) {
                        Game game = Game.Find(player);
                        if (game != null) {
                            if (game.isHost(player) && game.getGameDurationType() == GameDurationType.HOSTDURATION) {
                                if (game.getGameState() == GameState.STARTING) {
                                    game.askCancelTimer();
                                    player.sendMessage(cmc.basicsetting(cmc.MSG_TIMERCANCEL, player));
                                }

                            } else {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_NOTYOURHOST, player));
                                return true;
                            }
                        } else {
                            player.sendMessage(cmc.basicsetting(cmc.MSG_HOST_NOTEXIST, player));
                        }
                    } else if (args[0].equalsIgnoreCase("listmaps")) {
                        ArrayList<String> ARRAY = new ArrayList<>();
                        for (Map map : Map.maplist) {
                            if (map.isAvailable()) {
                                ARRAY.add(map.getName());
                            }
                        }
                        player.sendMessage(cmc.basicsetting(cmc.AVAILABLE_MAPS, player).replace("{MAPS}", ARRAY.toString().replace("[", "").replace("]", "")));

                    } else {
                        HelpMessage(player);
                    }
                }

                return true;


            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (cmd.getName().equalsIgnoreCase("erhost")) {
                if (args.length == 2) {
                    String gameName = args[0];
                    Game game = Game.Find(gameName);
                    if (game == null) {
                        Utils.log_print("Game not found");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("start")) {
                        if (game.getGameDurationType() == GameDurationType.HOSTDURATION && game.getGameState() == GameState.WAITING) {
                            game.askStartTimer();
                            Utils.log_print(cmc.MSG_TIMERSTART);
                        }
                    } else if (args[1].equalsIgnoreCase("alert")) {
                        sendAlert(null, game);
                    }
                } else if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
                    if (args.length <= 3) {
                        return true;
                    }
                    if (Utils.IsInteger(args[2]) && Integer.parseInt(args[2]) > 0) {
                        String name = args[1];
                        int maxplayer = Integer.parseInt(args[2]);
                        Map m = Map.Find(args[3]);
                        if (m == null) {
                            if (args[3].contains(",")) {
                                String[] maps = args[3].split(",");
                                int max = 4;
                                if (cmc != null) {
                                    max = cmc.Max_maps_per_games;
                                }
                                if (max > maps.length) {
                                    max = maps.length;
                                }
                                ArrayList<Map> maps_found = new ArrayList<>();
                                Map map_;
                                for (int i = 0; i < max; i++) {
                                    map_ = Map.Find(maps[i]);
                                    if (map_ == null) {
                                        Utils.log_print(cmc.removeColors(cmc.MSG_MAPNOTFOUND2.replace("{ARG}", maps[i])));
                                        return true;
                                    }
                                    if (!map_.isAvailable()) {
                                        Utils.log_print(cmc.removeColors(cmc.MSG_MAP_NOTAVAILABLE2.replace("{MAP}", map_.getName())));
                                        return true;
                                    }
                                    maps_found.add(map_);
                                }
                                Utils.log_print(cmc.removeColors(cmc.MSG_HOST_CREATING));

                                Game game = new GrandPrixMode(name, GameDurationType.HOSTDURATION, maxplayer, 0, null, maps_found);
                                return true;
                            }
                            Utils.log_print(cmc.removeColors(cmc.MSG_MAPNOTFOUND));
                            return true;
                        }
                        if (!m.isAvailable()) {
                            Utils.log_print(cmc.removeColors(cmc.MSG_MAP_NOTAVAILABLE));
                            return true;
                        }
                        Utils.log_print(cmc.removeColors(cmc.MSG_HOST_CREATING));

                        Game game = new RaceMode(name, GameDurationType.HOSTDURATION, maxplayer, 0, null, m);
                    } else {
                        Utils.log_print(cmc.removeColors(cmc.MAX_PLAYER_ARG));
                    }
                }
            }

            return true;


        }
        return false;
    }

    public static void HelpMessage(Player p) {
        p.sendMessage("§6§m------------------------------------");
        p.sendMessage(" ");

        p.sendMessage("§7Help §6Host§7:");
        p.sendMessage(" ");

        TextComponent cmd = new TextComponent("§f/erhost create §c[name] <slots> <map1,map2,..> §7: Create a game");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erhost create"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/erhost alert §7: Broadcast an invitation to join your game");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/erhost alert"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/erhost start §7: Start your game");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/erhost start"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/erhost stoptimer §7: Stop the start countdown");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/erhost stoptimer"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/erhost kick §c[player] §7: Kick a player");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erhost kick"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/erhost setperm §c[permission/none] §7: Define a permission to join your game");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/erhost setperm"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/erhost list §7: List all games");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/erhost list"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/erhost listmaps §7: List all maps");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/erhost listmaps"));
        p.spigot().sendMessage(cmd);

        p.sendMessage(" ");
        TextComponent message = new TextComponent("§7More information ");
        TextComponent click = new TextComponent("§e§nHere");
        click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://chooseit.io/er/doc/commands/command-erhost"));
        message.addExtra(click);
        p.spigot().sendMessage(message);
        p.sendMessage("§7§oClick on commands to execute them");
        p.sendMessage("§6<>: Optional argument");
        p.sendMessage("§c[]: Required argument");
        p.sendMessage("§6§m------------------------------------");
    }

    public static void sendAlert(Player player, Game game) {
        CustomMessageConfig cmc = Main.cmc();
        if (player != null) {
            if (cmc.Max_alert_host == game.getAlertsent()) {
                player.sendMessage(cmc.basicsetting(cmc.MSG_ALERT, player));
                return;
            }
            if(!game.isOpen()){
                player.sendMessage(cmc.basicsetting(cmc.HOST_GAMENOTOPEN, player));
                return;
            }

            game.setAlertsent(game.getAlertsent() + 1);
        }

        String Base = "{\"text\":\"{TCHATMESSAGE}\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"{COMMAND}\"}}";
        Base = Base.replace("{TCHATMESSAGE}", cmc.basicsettingnoprefix(cmc.ALERT_HOST, null)).replace("{PLAYER}", player != null ? player.getName() : cmc.CONSOLE_USERNAME);
        Base = Base.replace("{COMMAND}", "/er join " + game.getName());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PacketHandler.sendPacketMessage(onlinePlayer, Base);
        }
    }
}
