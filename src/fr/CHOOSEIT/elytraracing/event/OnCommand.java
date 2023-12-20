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

package fr.CHOOSEIT.elytraracing.event;

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class OnCommand implements Listener {
    public String IDENTIFIER = "%%__USER__%%";
    CustomMessageConfig cmc = Main.customMessageConfig;

    @EventHandler
    public void performcommand(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();
        Game g = Game.Find(p);
        if (g == null) {
            return;
        }
        if (cmc.COMMANDS_WHITELIST) {
            boolean bypass = false;
            String command = e.getMessage().split(" ")[0];
            if (command.startsWith("/")) {
                command = command.replaceFirst("/", "");
            } else {
                return;
            }

            if (cmc.COMMANDS_WHITELIST_BYPASS_PERMISSION != null && !p.hasPermission(cmc.COMMANDS_WHITELIST_BYPASS_PERMISSION) && !(command.equalsIgnoreCase("ermap") || command.equalsIgnoreCase("er") || command.equalsIgnoreCase("erhost") || command.equalsIgnoreCase("ergames"))) {
                for (String commands_whitelist_command : cmc.COMMANDS_WHITELIST_COMMANDS) {
                    if (commands_whitelist_command.equalsIgnoreCase(command)) {
                        bypass = true;
                        break;
                    }
                }
                if (!bypass) {
                    e.setCancelled(true);
                    p.sendMessage(cmc.basicsettingnoprefix(cmc.COMMANDS_MESSAGE, p));
                }
            }

        }
    }
}
