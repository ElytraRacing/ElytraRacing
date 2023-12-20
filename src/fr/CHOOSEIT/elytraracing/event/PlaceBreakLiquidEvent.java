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

import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.PlayerMode;
import fr.CHOOSEIT.elytraracing.signssystem.SignsSystem;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceBreakLiquidEvent implements Listener {
    public String IDENTIFIER = "%%__USER__%%";
    @EventHandler
    public void OnPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Game game = Game.Find(player, PlayerMode.ALIVE);
        if (game == null) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void OnBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getState() instanceof Sign) {
            SignsSystem.delete(event.getBlock());
        }
        Game game = Game.Find(player, PlayerMode.ALIVE);
        if (game == null) {
            return;
        }
        event.setCancelled(true);
    }
}
