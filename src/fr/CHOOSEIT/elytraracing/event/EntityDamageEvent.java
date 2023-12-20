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

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.GameState;
import fr.CHOOSEIT.elytraracing.gamesystem.PlayerMode;
import fr.CHOOSEIT.elytraracing.optiHelper.McHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityDamageEvent implements Listener {
    public String IDENTIFIER = "%%__USER__%%";
    @EventHandler
    public void onDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Game game = Game.Find(player, PlayerMode.ALIVE);
            if (game != null) {
                if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FLY_INTO_WALL || event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL) {
                    if (Main.cmc().TP_ON_HIT && game.getGameState() == GameState.STARTED) {
                        if (!game.back1CP(player) && !game.getPlayerInformationSaver().hasFinished(player)) {
                            if(game.isInterRounds()){
                                player.teleport(game.getCurrentmap().getLocation_lobby());
                            }else{
                                player.teleport(game.getCurrentmap().getLocation_start());
                            }
                            McHelper.setGliding(player, false);
                        }
                    }

                }
                event.setCancelled(true);
            }

        }
    }
}
