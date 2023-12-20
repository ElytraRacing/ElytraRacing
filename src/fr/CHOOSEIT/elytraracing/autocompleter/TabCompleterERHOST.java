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

import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class TabCompleterERHOST implements TabCompleter {
    public String IDENTIFIER = "%%__USER__%%";
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("erhost") && sender.hasPermission(Objects.requireNonNull(cmd.getPermission()))) {
            if (args.length == 1) {
                return Arrays.asList("create", "alert", "listmaps", "start", "stoptimer", "list", "kick", "setperm");
            } else if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
                ArrayList<String> list = new ArrayList<>();
                for (Map map : Map.maplist) {
                    if (map.isAvailable()) {
                        list.add(map.getName());
                    }
                }
                return list;
            } else {
                return Collections.singletonList("");
            }
        }
        return null;
    }
}
