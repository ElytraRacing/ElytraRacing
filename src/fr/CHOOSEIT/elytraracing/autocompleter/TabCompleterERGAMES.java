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

package fr.CHOOSEIT.elytraracing.autocompleter;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.gamesystem.AutoGames;
import fr.CHOOSEIT.elytraracing.gamesystem.AutoGamesSaver;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TabCompleterERGAMES implements TabCompleter {

    public String IDENTIFIER = "%%__USER__%%";
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ergames") && sender.hasPermission(Objects.requireNonNull(cmd.getPermission()))) {
            if (args.length == 1) {
                return Arrays.asList("add", "list", "set", "setqueue", "reload");

            } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                ArrayList<String> list = new ArrayList<>();
                Main.autoGamesSaver.autoGamesList.forEach(a -> list.add(a.getName()));
                return list;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("setqueue")) {
                if (Main.autoGamesSaver != null && Main.autoGamesSaver.DISABLED_QUEUES != null && Main.autoGamesSaver.ENABLED_QUEUES != null) {
                    ArrayList<String> list = Main.autoGamesSaver.DISABLED_QUEUES;
                    list.addAll(Main.autoGamesSaver.ENABLED_QUEUES);
                    return list;
                }
                return null;

            } else if (args.length == 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("setqueue"))) {
                return Arrays.asList("on", "off");
            } else {
                return Arrays.asList("");
            }
        }
        return null;
    }
}
