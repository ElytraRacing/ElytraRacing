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
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TabCompleterERMAP implements TabCompleter {
    public String IDENTIFIER = "%%__USER__%%";
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ermap") && sender.hasPermission(Objects.requireNonNull(cmd.getPermission()))) {
            if (args.length == 1) {
                return Arrays.asList("list", "create", "map", "help");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("map")) {
                    ArrayList<String> list = new ArrayList<>();

                    for (Map map : Map.maplist) {
                        list.add(map.getName());
                    }
                    return list;
                } else if (args[0].equalsIgnoreCase("help")) {
                    return Arrays.asList("MapSystem", "CheckpointSystem", "EndSystem");
                }

            } else if (args.length == 3 && args[0].equalsIgnoreCase("help")) {
                ArrayList<String> list = new ArrayList<>();

                for (Map map : Map.maplist) {
                    list.add(map.getName());
                }
                return list;
            } else if (args.length == 3 && args[0].equalsIgnoreCase("map")) {
                return Arrays.asList("config", "delete", "setlocationlobby", "setlocationstart", "setlocationend", "tplobby", "tpstart", "tpend", "settimemax", "show", "enable", "podium",
                        "listcheckpoint", "addcheckpoint", "insertcheckpoint", "removecheckpoint", "checkpoint", "cleardb", "listend", "addend", "removeend");
            } else if (args.length == 4 && args[0].equalsIgnoreCase("map") &&
                    (args[2].equalsIgnoreCase("show") || args[2].equalsIgnoreCase("enable"))) {
                return Arrays.asList("on", "off");
            } else if (args.length == 4 && args[0].equalsIgnoreCase("map") &&
                    (args[2].equalsIgnoreCase("podium"))) {
                return Arrays.asList("on", "off", "setlocation", "tp");
            } else if (args.length == 5 && args[0].equalsIgnoreCase("map") &&
                    (args[2].equalsIgnoreCase("checkpoint"))) {
                return Arrays.asList("tp", "setXrotation", "setYrotation", "setZrotation", "setboostmultiplier", "setlocation", "setsize", "setparticleamount", "linkto", "removelinkto", "setparticle", "setparticleifnext");
            } else if (args.length == 5 && args[0].equalsIgnoreCase("map") &&
                    (args[2].equalsIgnoreCase("end"))) {
                return Arrays.asList("tp", "setXrotation", "setYrotation", "setZrotation", "setlocation", "setparticleamount", "setsize", "setparticle", "setparticleifnext");
            } else {
                return Arrays.asList("");
            }
        }
        return null;
    }
}
