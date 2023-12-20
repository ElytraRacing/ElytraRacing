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

import fr.CHOOSEIT.elytraracing.GUI.Pages.HostPage;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.addon.particle.MainParticleTest;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.holograms.Holograms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PlayerJoinEvent implements Listener {
    public String IDENTIFIER = "%%__USER__%%";
    @EventHandler
    public void OnJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player p = event.getPlayer();

        new MainParticleTest(p);

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, new Runnable() {
            @Override
            public void run() {
                Main.currentDataBase.SavePlayer(p);
            }
        });
        for (Holograms holograms : Holograms.hologramslist) {
            if (holograms.hologram_type.equals(Holograms.HOLOGRAM_TYPE.PERSONAL_STATS)) {
                holograms.personalupdate(p);
            } else if (holograms.hologram_type.equals(Holograms.HOLOGRAM_TYPE.PERSONAL_MAPS)) {
                holograms.personalupdate_maps(p);
            }
        }
        Utils.ReloadShowHide(p);
        if (Main.oneGame() && Main.cmc().ONEGAME_REMOVEJOINQUITMESSAGES) {
            event.setJoinMessage("");
        }
        if (Main.oneGame() && Main.cmc().ONEGAME_AUTOJOINGAME.isEmpty()) {
            p.sendMessage("§c\"OneGame\" system not configured");
        } else if (Main.oneGame()) {

            Game game = Game.Find(Main.cmc().ONEGAME_AUTOJOINGAME);

            if (game == null) {
                Utils.kickPlayer(p, "", Main.cmc().ONEGAME_LOBBYSERVER);
                return;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                @Override
                public void run() {
                    String kick = game.rawPlayerjoin(p);
                    if (kick != null) {
                        Utils.kickPlayer(p, kick, Main.cmc().ONEGAME_LOBBYSERVER);
                    }
                }
            }, 5);
        }

    }

    @EventHandler
    public void serverPingList(ServerListPingEvent e) {
        if (Main.oneGame() && Main.cmc().ONEGAME_MOTD_USE && !Main.cmc().ONEGAME_AUTOJOINGAME.isEmpty()) {
            Game game = Game.Find(Main.cmc().ONEGAME_AUTOJOINGAME);
            if (game == null) {
                return;
            }
            if (Main.cmc().ONEGAME_MOTD_CHANGE_MAX_PLAYERS) {
                e.setMaxPlayers(game.getMaxPlayer());
            }
            e.setMotd(getMOTDLine(game, 0) + "\n" + getMOTDLine(game, 1));
        }

    }

    public String getMOTDLine(Game game, int id) {
        if (game == null) {
            return "";
        }
        String s = Main.cmc().ONEGAME_MOTD[id];
        s = s.replace("{STATE}", Main.cmc().STATES.get(game.getGameState().toString()));
        s = s.replace("{MAP}", game.getCurrentmap().getName());
        s = s.replace("{PLAYING}", String.valueOf(game.getPlayerList().size()));
        s = game.getMOTDModifiedString(s);
        s = s.replace("{PERMISSION}", HostPage.permstr(game.getPermission()));
        s = Main.cmc().basicsettingnoprefix(s, null);
        return s;
    }

}
