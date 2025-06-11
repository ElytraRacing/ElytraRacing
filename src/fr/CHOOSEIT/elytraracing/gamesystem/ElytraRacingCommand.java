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

import fr.CHOOSEIT.elytraracing.PlayerInformation.GamePlayer;
import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.GUI.Pages.ErTrainMapChoosePage;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.SqlHandle.DataBase;
import fr.CHOOSEIT.elytraracing.StaticMessages;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.gamesystem.Games.Training;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
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

public class ElytraRacingCommand implements CommandExecutor {
    private CustomMessageConfig cmc = Main.customMessageConfig;
    private static String USER = "%%__USER__%%";
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("er")) {
                if (args.length == 0) {
                    HelpMessage(player);
                } else {
                    if (args[0].equalsIgnoreCase("join")) {
                        if (args.length <= 1) {
                            player.sendMessage(cmc.basicsettingnoprefix(StaticMessages.USAGE_ER_JOIN, player));
                        } else {
                            Game game = Game.Find(args[1]);
                            if (game == null) {
                                player.sendMessage(cmc.basicsetting(cmc.MSG_GAMENOTEXIST, player));
                                return true;
                            }
                            game.rawPlayerjoin(player);
                            return true;
                        }

                    } else if (args[0].equalsIgnoreCase("train")) {
                        if (cmc.PERMISSION_ER_TRAIN != null && !player.hasPermission(cmc.PERMISSION_ER_TRAIN)) {
                            player.sendMessage(cmc.basicsettingnoprefix(cmc.PERMISSION_MESSAGE, player));
                            return true;
                        }
                        if (!cmc.ER_TRAIN_ENABLE) {
                            player.sendMessage(cmc.basicsettingnoprefix(cmc.ER_TRAIN_ENABLED, player));
                            return true;
                        }
                        if (args.length <= 1) {
                            String gui_title = Main.cmc().ER_TRAIN_CHOOSE_MAP_GUI_TITLE;
                            if (gui_title == null || gui_title.isEmpty()) {
                                gui_title = "Choose a map";
                            }
                            ErTrainMapChoosePage pa = new ErTrainMapChoosePage(gui_title, player);
                            pa.open();
                            pa.displayPage(1);

                        } else {
                            ExCommandTrain(player, args[1]);
                            return true;
                        }

                    } else if (args[0].equalsIgnoreCase("play")) {
                        if (cmc.PERMISSION_ER_PLAY == null || player.hasPermission(cmc.PERMISSION_ER_PLAY)) {
                            if (args.length <= 1) {
                                Game g = Queue(null, player);
                                if (g != null) {
                                    g.rawPlayerjoin(player);
                                } else {
                                    player.sendMessage(cmc.basicsettingnoprefix(cmc.ER_PLAY_GAME, player));
                                }
                            } else {
                                Game g = Queue(args[1], player);
                                if (g != null) {
                                    g.rawPlayerjoin(player);
                                } else {
                                    player.sendMessage(cmc.basicsettingnoprefix(cmc.ER_PLAY_GAME, player));
                                }
                                return true;
                            }

                            return true;
                        } else {
                            player.sendMessage(cmc.basicsettingnoprefix(cmc.PERMISSION_MESSAGE, player));
                            return true;
                        }

                    } else if (args[0].equalsIgnoreCase("stats")) {
                        Player_Stats(player, player);
                    } else if (args[0].equalsIgnoreCase("near")) {
                        Game game = Game.Find(player, PlayerMode.SEEING);
                        if (game != null) {
                            if (game.spectatingOfEndRound(player) || game.getSpecList().contains(player)) {
                                Float min_dis = 999999999999999f;
                                Float co_dis;
                                GamePlayer min_player = null;
                                min_dis = -1f;
                                for (GamePlayer player1 : game.getGamePlayers()) {
                                    if (!game.getPlayerInformationSaver().hasFinished(player1.getSpigotPlayer())) {
                                        if (min_dis == -1) {
                                            min_dis = (float) Utils.distance(player.getLocation(), player1.getLocation());
                                            min_player = player1;
                                        } else {
                                            co_dis = (float) Utils.distance(player.getLocation(), player1.getLocation());
                                            if (co_dis < min_dis) {
                                                min_dis = co_dis;
                                                min_player = player1;
                                            }
                                        }
                                    }


                                }
                                if (min_player != null) {
                                    player.teleport(min_player.getSpigotPlayer());
                                }
                                game.getSpecMouvement().refreshMouvement(player);
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("quitspec")) {
                        Game game = Game.Find(player, PlayerMode.SEEING);
                        if (game == null) return true;
                        game.quitSpec(game, player);
                    } else if (args[0].equalsIgnoreCase("leave")) {
                        Game game = Game.Find(player);
                        if (game == null) {
                            game = Game.Find(player, PlayerMode.SPEC);
                            if (game != null) {
                                game.PlayerLeave(player);
                                return true;
                            }
                            player.sendMessage(cmc.basicsetting(cmc.MSG_NOGAMETOLEAVE, null));
                            return true;
                        }
                        game.PlayerLeave(player);
                    }else if (args[0].equalsIgnoreCase("kick") && player.hasPermission("elytraracing.admin")){
                        if(args.length == 2){
                            return kickCommand(player, args[1]);
                        }
                        else{
                            player.sendMessage("§cYou must specify the name of a player");
                        }
                    } else {
                        HelpMessage(player);
                    }
                }
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (args[0].equalsIgnoreCase("kick")) {
                if (args.length == 2) {
                    return kickCommand(sender, args[1]);
                } else {
                    sender.sendMessage("§cYou must specify the name of a player");
                }
            }
        }
        return false;
    }

    public boolean kickCommand(CommandSender sender, String playerName){
        Player target = Bukkit.getPlayer(playerName);
        if (target == null){
            if (sender instanceof Player)
                sender.sendMessage("§cThe player cannot be found");
            else
                sender.sendMessage("The player cannot be found");
            return true;
        }
        silenceKick(target);
        return true;
    }

    public static void silenceKick(Player player){
        Game game = Game.Find(player);
        if (game == null) {
            game = Game.Find(player, PlayerMode.SPEC);
            if (game == null)
                return;
        }
        game.PlayerLeave(player, true);
    }

    public static Game Queue(String s, Player p) {
        if (s == null) {
            s = Main.autoGamesSaver.DEFAULT_QUEUE;
        }
        if (Main.autoGamesSaver.DISABLED_QUEUES.contains(s)) {
            return null;
        }
        ArrayList<Game> play_games = new ArrayList<>();
        AutoGames ag;
        for (Game game : Game.gamelist) {
            if ((p == null || (game.getPermission() == null || p.hasPermission(game.getPermission()))) && game.getGameDurationType() == GameDurationType.SERVERDURATION && !game.isFull() && (game.getGameState() == GameState.WAITING || game.getGameState() == GameState.STARTING)) {
                ag = Main.autoGamesSaver.getAutogame(game.getName());
                if (ag != null) {
                    if (ag.getQueue().equalsIgnoreCase(s)) {
                        play_games.add(game);
                    }
                }

            }
        }
        if (play_games.size() == 0) {
            return null;
        }
        Collections.sort(play_games, new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                return o2.getPlayerList().size() - o1.getPlayerList().size();
            }
        });
        return play_games.get(0);
    }

    public static int Queuesize(String s) {
        if (s == null) {
            s = Main.autoGamesSaver.DEFAULT_QUEUE;
        }
        ArrayList<Game> play_games = new ArrayList<>();
        AutoGames ag;
        for (Game game : Game.gamelist) {
            if (game.getGameDurationType() == GameDurationType.SERVERDURATION) {
                ag = Main.autoGamesSaver.getAutogame(game.getName());
                if (ag != null) {
                    if (ag.getQueue().equalsIgnoreCase(s)) {
                        play_games.add(game);
                    }
                }

            }
        }
        return play_games.size();
    }

    public static Game QueueoneGamesize(String s) {
        if (s == null) {
            s = Main.autoGamesSaver.DEFAULT_QUEUE;
        }
        ArrayList<Game> play_games = new ArrayList<>();
        AutoGames ag;
        for (Game game : Game.gamelist) {
            if (game.getGameDurationType() == GameDurationType.SERVERDURATION) {
                ag = Main.autoGamesSaver.getAutogame(game.getName());
                if (ag != null) {
                    if (ag.getQueue().equalsIgnoreCase(s)) {
                        return game;
                    }
                }

            }
        }
        return null;
    }


    private void HelpMessage(Player p) {
        p.sendMessage("§6§m------------------------------------");
        p.sendMessage(" ");

        p.sendMessage("§7Help §6ElytraRacing§7:");
        p.sendMessage(" ");

        TextComponent cmd = new TextComponent("§f/er join §c[game] §7: Join a game");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/er join"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/er leave §7: Leave a game");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/er leave"));
        p.spigot().sendMessage(cmd);

        if (cmc.ER_SHOW_PLAY) {
            cmd = new TextComponent("§f/er play §6<queue> §7: Join a game");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/er play"));
            p.spigot().sendMessage(cmd);
        }

        cmd = new TextComponent("§f/er stats §7: Display personal stats");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/er stats"));
        p.spigot().sendMessage(cmd);

        if (cmc.ER_SHOW_TRAIN) {
            cmd = new TextComponent("§f/er train §c[map] §7: Training");
            cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/er train"));
            p.spigot().sendMessage(cmd);
        }


        p.sendMessage(" ");

        if (p.isOp()) {
            TextComponent message = new TextComponent("§7More information ");
            TextComponent click = new TextComponent("§e§nHere");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://chooseit.gitbook.io/elytraracing/commands/command-er"));
            message.addExtra(click);
            p.spigot().sendMessage(message);
        }

        p.sendMessage("§7§oClick on commands to execute them");
        p.sendMessage("§6<>: Optional argument");
        p.sendMessage("§c[]: Required argument");
        p.sendMessage("§6§m------------------------------------");
    }

    private void Player_Stats(Player p, Player sendto) {
        DataBase dataBase = Main.currentDataBase;
        DataBase.Playerinfos playerinfo = DataBase.Playerinfos.getPlayerInfos(dataBase.get_player_id(p));
        for (String s1 : cmc.USAGE_ERSTATS_SHOW) {
            if (s1.startsWith("[HOVERMESSAGE_MAP_STATS]")) {
                Map_Stats(sendto, cmc.basicsettingnoprefix(Stats_message(p, s1.replace("[HOVERMESSAGE_MAP_STATS]", ""), playerinfo), p), playerinfo);
                continue;
            }
            sendto.sendMessage(cmc.basicsettingnoprefix(Stats_message(p, s1, playerinfo), p));
        }
    }

    public static String Stats_message(Player p, String msg, DataBase.Playerinfos playerinfo) {
        int pourcentage_grandprix = 0, pourcentage_racemode = 0;
        if (playerinfo.player_game_played_racemode > 0) {
            pourcentage_racemode = Math.round(playerinfo.player_game_won_racemode / Float.valueOf(playerinfo.player_game_played_racemode) * 100);
        }
        if (playerinfo.player_game_played_grandprix > 0) {
            pourcentage_grandprix = Math.round(playerinfo.player_game_won_grandprix / Float.valueOf(playerinfo.player_game_played_grandprix) * 100);
        }
        String favoritemap = "";
        int favoritemapwinrate = 0;
        if (playerinfo.playerMapinfos.size() >= 1) {
            Map favoritemapinstance = Map.getMap(playerinfo.playerMapinfos.get(0).map_uuid);
            if (favoritemapinstance != null) {
                favoritemap = favoritemapinstance.getName();
                favoritemapwinrate = playerinfo.playerMapinfos.get(0).winrate;
            }
        }
        msg = msg
                .replace("{USERNAME}", p.getName())
                .replace("{PLAYER_GAME_PLAYED_RACEMODE}", String.valueOf(playerinfo.player_game_played_racemode))
                .replace("{PLAYER_GAME_PLAYED_GRANDPRIX}", String.valueOf(playerinfo.player_game_played_grandprix))
                .replace("{PLAYER_GAME_WON_RACEMODE}", String.valueOf(playerinfo.player_game_won_racemode))
                .replace("{PLAYER_GAME_WON_GRANDPRIX}", String.valueOf(playerinfo.player_game_won_grandprix))
                .replace("{PLAYER_GAME_MAP_PLAYED}", String.valueOf(playerinfo.player_game_won_grandprix))
                .replace("{PLAYER_GAME_MAP_FINISHED}", String.valueOf(playerinfo.player_game_won_grandprix))
                .replace("{POURCENTAGE_WINRATE_RACEMODE}", String.valueOf(pourcentage_racemode))
                .replace("{POURCENTAGE_WINRATE_GRANDPRIX}", String.valueOf(pourcentage_grandprix))
                .replace("{PERCENTAGE_WINRATE_RACEMODE}", String.valueOf(pourcentage_racemode))
                .replace("{PERCENTAGE_WINRATE_GRANDPRIX}", String.valueOf(pourcentage_grandprix))
                .replace("{RANK_WON_GRANDPRIX}", String.valueOf(playerinfo.rank_won_grandprix))
                .replace("{RANK_WON_RACEMODE}", String.valueOf(playerinfo.rank_won_racemode))
                .replace("{RANK_SCORE_TOTAL}", String.valueOf(playerinfo.rank_score_total))
                .replace("{SCORE_TOTAL}", String.valueOf(playerinfo.score_total))
                .replace("{FAVORITE_MAP}", favoritemap)
                .replace("{FAVORITE_MAP_WINRATE}", String.valueOf(favoritemapwinrate))


        ;
        return msg;
    }

    public void Map_Stats(Player player, String TMsg, DataBase.Playerinfos playerinfos) {
        String BaseMessage, Base = cmc.basicsettingnoprefix(cmc.ERSTATS_MAP, player) + "\n", Ba;
        Map map;
        for (DataBase.PlayerMapinfos playerMapinfo : playerinfos.playerMapinfos) {
            map = Map.getMap(playerMapinfo.map_uuid);
            BaseMessage = cmc.ERSTATS_MAP_EACH;
            if (map != null) {
                BaseMessage = BaseMessage.replace("{MAP_NAME}", map.getName());
            }


            BaseMessage = BaseMessage.replace("{WIN}", String.valueOf(playerMapinfo.win)).replace("{PLAYER_RANK}", String.valueOf(playerMapinfo.ranking)).replace("{PLAYER_TIME}", Utils.timediffToString(playerMapinfo.player_time)).replace("{WINRATE}", String.valueOf(playerMapinfo.winrate));

            BaseMessage = cmc.basicsettingnoprefix(BaseMessage, player);
            Base += BaseMessage + "\n";

        }

        Ba = "{\"text\":\"{TCHATMESSAGE}\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"{BASETEXT}\"}}";
        Ba = Ba.replace("{TCHATMESSAGE}", TMsg).replace("{BASETEXT}", Base);

        PacketHandler.sendPacketMessage(player, Ba);
    }

    public static void ExCommandTrain(Player p, String maparg) {
        Map map = Map.Find(maparg);
        if (map != null && map.isAvailable()) {
            if (Main.cmc().ER_TRAIN_MAPS.contains(map.getName()) && Map.getMapName(map.getName()) != null) {
                Training g = new Training(p, map);
                g.rawPlayerjoin(p);
                g.Start();
            } else {
                p.sendMessage(Main.cmc().basicsetting(Main.cmc().ER_TRAIN_MAP_NOTFOUND, p));
            }
            return;
        }
        p.sendMessage(Main.cmc().basicsettingnoprefix(Main.cmc().MSG_MAPNOTFOUND, p));
    }
}

