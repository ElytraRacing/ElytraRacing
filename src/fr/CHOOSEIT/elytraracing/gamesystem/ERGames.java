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
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.StaticMessages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ERGames implements CommandExecutor {
    CustomMessageConfig cmc = Main.customMessageConfig;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("ergames")) {
                if (!player.hasPermission(cmd.getPermission())) {
                    player.sendMessage(cmc.basicsettingnoprefix(cmc.PERMISSION_MESSAGE, player));
                    return true;
                }
                if (args.length == 0) {
                    HelpMessage(player);
                } else {
                    if (args[0].equalsIgnoreCase("add")) {
                        if (Main.autoGamesSaver.autoGamesList == null) {
                            Main.autoGamesSaver.autoGamesList = new ArrayList<>();
                        }
                        if (args.length == 1) {
                            player.sendMessage(StaticMessages.USAGE_ERGAMES_ADD);
                            return true;
                        }
                        Main.autoGamesSaver.autoGamesList.add(new AutoGames(args[1]));


                        Main.instance.getSerialization().saveAutoGames();

                        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                    }
                    if (args[0].equalsIgnoreCase("reload")) {
                        Main.serialization.loadAutoGames();

                        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_FILE_RELOAD, player));
                    } else if (args[0].equalsIgnoreCase("list")) {
                        String message = cmc.ERGAMES_LIST;
                        StringBuilder listgames = new StringBuilder();
                        if (Main.autoGamesSaver == null || Main.autoGamesSaver.autoGamesList == null || Main.autoGamesSaver.autoGamesList.isEmpty()) {
                            listgames.append("");
                        } else {
                            for (AutoGames autoGames : Main.autoGamesSaver.autoGamesList) {
                                if (autoGames == null) {
                                    continue;
                                }
                                if (autoGames.isEnabled()) {
                                    listgames.append(cmc.ERGAMES_ENABLED_PREFIX);
                                } else {
                                    listgames.append(cmc.ERGAMES_DISABLED_PREFIX);
                                }
                                listgames.append(autoGames.getName());
                                if (autoGames != Main.autoGamesSaver.autoGamesList.get(Main.autoGamesSaver.autoGamesList.size() - 1)) {
                                    listgames.append(cmc.ERGAMES_LIST_SEPERATION_PREFIX);
                                    listgames.append(",");
                                }


                            }
                        }

                        message = message.replace("{AUTOGAMES}", listgames);

                        player.sendMessage(cmc.basicsettingnoprefix(message, player));
                    } else if (args[0].equalsIgnoreCase("setqueue")) {
                        if (Main.autoGamesSaver.DISABLED_QUEUES == null) {
                            Main.autoGamesSaver.DISABLED_QUEUES = new ArrayList<>();
                        }
                        if (Main.autoGamesSaver.ENABLED_QUEUES == null) {
                            Main.autoGamesSaver.ENABLED_QUEUES = new ArrayList<>();
                        }
                        if (args.length == 3) {
                            String value = args[2];
                            if (value.equalsIgnoreCase("on") && Main.autoGamesSaver.DISABLED_QUEUES.contains(args[1])) {
                                Main.autoGamesSaver.DISABLED_QUEUES.remove(args[1]);
                                if (!Main.autoGamesSaver.isEnabled(args[1])) {
                                    Main.autoGamesSaver.ENABLED_QUEUES.add(args[1]);
                                }
                                Main.instance.getSerialization().saveAutoGames();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));

                            } else if (value.equalsIgnoreCase("off") && Main.autoGamesSaver.ENABLED_QUEUES.contains(args[1])) {
                                Main.autoGamesSaver.ENABLED_QUEUES.remove(args[1]);
                                if (!Main.autoGamesSaver.isDisabled(args[1])) {
                                    Main.autoGamesSaver.DISABLED_QUEUES.add(args[1]);
                                }
                                Main.instance.getSerialization().saveAutoGames();
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                            } else {
                                player.sendMessage(cmc.basicsettingnoprefix(StaticMessages.USAGE_ERGAMES_SETQUEUE, player));
                            }

                        } else {
                            player.sendMessage(cmc.basicsettingnoprefix(StaticMessages.USAGE_ERGAMES_SETQUEUE, player));
                        }
                    } else if (args[0].equalsIgnoreCase("set")) {
                        if (args.length == 3) {
                            String value = args[2];
                            AutoGames ga = Main.autoGamesSaver.getAutogame(args[1]);
                            if (ga != null) {
                                if (value.equalsIgnoreCase("on")) {

                                    if (Game.Find(ga.getName()) != null) {
                                        player.sendMessage(cmc.basicsettingnoprefix(cmc.ERGAMES_ALREADYGAME, player));
                                        return true;
                                    } else {
                                        ga.setEnabled(true);
                                        Main.instance.getSerialization().saveAutoGames();
                                        player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                        ga.create();
                                    }


                                } else if (value.equalsIgnoreCase("off")) {
                                    Game game = Game.Find(ga.getName());
                                    if (game != null) {
                                        if (game.getGameDurationType() == GameDurationType.SERVERDURATION && (game.getGameState() == GameState.WAITING || game.getGameState() == GameState.STARTING)) {
                                            game.getPlayersAbleToSee().forEach(player1 -> game.PlayerLeave(player1, true));
                                            game.Delete();
                                        }
                                    }
                                    ga.setEnabled(false);
                                    Main.instance.getSerialization().saveAutoGames();
                                    player.sendMessage(cmc.basicsettingnoprefix(cmc.MSG_SAVE_SUCCESSFUL, player));
                                } else {
                                    player.sendMessage(cmc.basicsettingnoprefix(StaticMessages.USAGE_ERGAMES_SET, player));
                                }
                            } else {
                                player.sendMessage(cmc.basicsettingnoprefix(cmc.ERGAMES_NOTFOUND, player));
                            }
                        } else {
                            player.sendMessage(cmc.basicsettingnoprefix(StaticMessages.USAGE_ERGAMES_SET, player));
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void HelpMessage(Player p) {
        p.sendMessage("§6§m------------------------------------");
        p.sendMessage(" ");

        p.sendMessage("§7Help §6AutoGames§7:");
        p.sendMessage(" ");
        p.sendMessage("§fConfiguration of Autogames: §7Autogames.json");
        p.sendMessage(" ");

        TextComponent cmd = new TextComponent("§f/ergames add §c[name] §7: Create a blank autogame");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ergames add"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/ergames list §7: List all autogame");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ergames list"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/ergames set §c[autogame] [on/off] §7: Enable or disable an autogame");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ergames set"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/ergames setqueue §c[queue] [on/off] §7: Enable or disable a queue");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ergames setqueue"));
        p.spigot().sendMessage(cmd);

        cmd = new TextComponent("§f/ergames reload §7: Reload configuration §c(Experimental)");
        cmd.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ergames reload"));
        p.spigot().sendMessage(cmd);
        p.sendMessage("§cReloading configuration will delete every started autogames and recreate them");

        p.sendMessage(" ");
        TextComponent message = new TextComponent("§7More information ");
        TextComponent click = new TextComponent("§e§nHere");
        click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://chooseit.gitbook.io/elytraracing/commands/command-ergames"));
        message.addExtra(click);
        p.spigot().sendMessage(message);
        p.sendMessage("§7§oClick on commands to execute them");
        p.sendMessage("§6<>: Optional argument");
        p.sendMessage("§c[]: Required argument");
        p.sendMessage("§6§m------------------------------------");
    }
}
