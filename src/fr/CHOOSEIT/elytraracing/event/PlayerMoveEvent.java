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
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.GameMoment;
import fr.CHOOSEIT.elytraracing.gamesystem.GameState;
import fr.CHOOSEIT.elytraracing.gamesystem.PlayerMode;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.AdditionalObject;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.End;
import fr.CHOOSEIT.elytraracing.mapsystem.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerMoveEvent implements Listener {
    public String IDENTIFIER = "%%__USER__%%";

    CustomMessageConfig cmc = Main.customMessageConfig;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if(p == null) return;
        Game game = Game.Find(p, PlayerMode.ALIVE);
        Location loc = p.getLocation();
        if(loc == null) return;

        float[] coordsPlayer = new float[]{(float) loc.getX(), (float) loc.getY(), (float) loc.getZ()};
        if (game == null && Game.Find(p, PlayerMode.SPEC) == null) {
            return;
        }
        if (game == null) {
            game = Game.Find(p, PlayerMode.SPEC);
        }

        if (game.getGameMoment() == GameMoment.STARTINGCANTMOVE && Utils.distance(event.getTo(), event.getFrom()) > 0.1 && !game.getSpecList().contains(p)) {
            Location from = event.getFrom();
            from.setPitch(event.getTo().getPitch());
            from.setYaw(event.getTo().getYaw());
            p.teleport(from);
        }
        if ((game.PlayerModeof(p).equals(PlayerMode.SPEC) || game.specEnd(p)) && !game.getCurrentmap().canPassThroughWall()) {

            if (p.getLocation().clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
                game.getSpecMouvement().tpback(p);
                return;
            }
            game.getSpecMouvement().refreshMouvement(p);
        }
        if ((p.isGliding() || cmc.DISABLE_ELYTRA) && game.getGameState() == GameState.STARTED && !((game.PlayerModeof(p).equals(PlayerMode.SPEC) || game.specEnd(p)))) {
            Location lastloc = game.getPlayerInformationSaver().getLastLoc(p);
            if (lastloc != null) {
                Location current = p.getLocation().clone();
                //Additional Objects
                AdditionalObject ao;
                for (int i = 0; i < game.getCurrentmap().getAdditionalObjectsList().size(); i++) {
                    ao = game.getCurrentmap().getAdditionalObjectsList().get(i);
                    if (!game.getPlayerInformationSaver().hasPassedAdditionalObject(p, i) && Utils.Distance(ao.getCoordWithDelta(game.getCurrentDeltaTime()), coordsPlayer) < ao.getRadiusSize() + p.getVelocity().lengthSquared() && ao.Through(lastloc, current, game.getCurrentDeltaTime())) {
                        game.getPlayerInformationSaver().addPassedAdditionalObject(p, i);
                        Utils.playSoundDispatcher(p, p.getLocation(), cmc.SOUND_ADDITIONAL_OBJECT_PASSED, "BLOCK_NOTE_HAT", cmc.SOUND_ADDITIONAL_OBJECT_PASSED, "minecraft:block.note_block.hat", cmc.SOUND_ADDITIONAL_OBJECT_PASSED_VOLUME, cmc.SOUND_ADDITIONAL_OBJECT_PASSED_PITCH);
                        ao.applyPlus(p);
                    }
                }

                int CurrentCheckpoint = game.getPlayerInformationSaver().getCurrentCP(p);

                boolean ok = false;
                if ((CurrentCheckpoint == game.getCurrentmap().getCheckpointsList().size() && game.getPlayerInformationSaver().isBackCP(p))) {
                    CheckPoint cp = game.getCurrentmap().getCheckpointsList().get(CurrentCheckpoint - 1);
                    if (Utils.Distance(cp.getCoordWithDelta(game.getCurrentDeltaTime()), coordsPlayer) < cp.getRadiusSize() + p.getVelocity().lengthSquared() && cp.Through(lastloc, current, game.getCurrentDeltaTime())) {
                        cp.ApplyBoost(p);
                        game.getPlayerInformationSaver().setBackCP(p, false);
                        game.drawLineNextObject(p, cp, game.getCurrentmap().getEnd(0));
                    }
                }
                if (CurrentCheckpoint < game.getCurrentmap().getCheckpointsList().size()) {
                    CheckPoint cp = game.getCurrentmap().getCheckpointsList().get(CurrentCheckpoint);
                    if (Utils.Distance(cp.getCoordWithDelta(game.getCurrentDeltaTime()), coordsPlayer) < cp.getRadiusSize() + p.getVelocity().lengthSquared() && cp.Through(lastloc, current, game.getCurrentDeltaTime())) {
                        game.CheckpointVerified(CurrentCheckpoint, p);
                        ok = true;
                        Utils.SendTitle(p, "", "§a§l✓", 0, 15, 0);

                        Utils.playSoundDispatcher(p, p.getLocation(), cmc.SOUND_CHECKPOINT_PASSED, "BLOCK_NOTE_HAT", cmc.SOUND_CHECKPOINT_PASSED, "minecraft:block.note_block.hat", cmc.SOUND_CHECKPOINT_PASSED_VOLUME, cmc.SOUND_CHECKPOINT_PASSED_PITCH);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                Utils.SendTitle(p, " ", " ", 0, 0, 0);
                            }
                        }, 15);


                    } else if (!cp.getLinkedTo_id_order(game.getCurrentmap()).isEmpty()) {
                        CheckPoint cp2;
                        for (Integer integer : cp.getLinkedTo_id_order(game.getCurrentmap())) {
                            if (!(integer >= game.getCurrentmap().getCheckpointsList().size() || integer < 0)) {
                                cp2 = game.getCurrentmap().getCheckpointsList().get(integer);
                                if (Utils.Distance(cp2.getCoordWithDelta(game.getCurrentDeltaTime()), coordsPlayer) < cp2.getRadiusSize() + p.getVelocity().lengthSquared() && cp2.Through(lastloc, current, game.getCurrentDeltaTime())) {

                                    game.CheckpointVerified(integer, p);
                                    ok = true;

                                    Utils.playSoundDispatcher(p, p.getLocation(), cmc.SOUND_CHECKPOINT_PASSED, "BLOCK_NOTE_HAT", cmc.SOUND_CHECKPOINT_PASSED, "minecraft:block.note_block.hat", cmc.SOUND_CHECKPOINT_PASSED_VOLUME, cmc.SOUND_CHECKPOINT_PASSED_PITCH);

                                    Utils.SendTitle(p, "", "§a§l✓", 0, 15, 0);
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                                        @Override
                                        public void run() {
                                            Utils.SendTitle(p, " ", " ", 0, 0, 0);
                                        }
                                    }, 15);

                                    break;
                                }
                            } else {
                                cp.RemoveLink(integer);
                                Main.instance.getSerialization().saveMap(game.getCurrentmap());
                            }
                        }
                    }
                    if ((CurrentCheckpoint > 0 && !ok && game.getPlayerInformationSaver().isBackCP(p))) {
                        Map map = game.getCurrentmap();
                        CheckPoint bcp = map.getCheckpointsList().get(CurrentCheckpoint - 1);
                        if (Utils.Distance(bcp.getCoordWithDelta(game.getCurrentDeltaTime()), coordsPlayer) < bcp.getRadiusSize() + p.getVelocity().lengthSquared() && bcp.Through(lastloc, current, game.getCurrentDeltaTime())) {
                            bcp.ApplyBoost(p);

                            game.getPlayerInformationSaver().setBackCP(p, false);
                            int i = game.getPlayerInformationSaver().getCurrentCP(p);

                            if (cmc.LineHelper && cmc.NEXT_CHECKPOINT_PARTICLE != null && map.getCheckpointsList().size() > i && i >= 1) {
                                CheckPoint ncp = map.getCheckpointsList().get(i - 1);
                                int data = 0;
                                game.drawLineNextObject(p, bcp, cp);
                            }

                        } else if (!bcp.getLinkedTo_id_order(game.getCurrentmap()).isEmpty()) {
                            CheckPoint bcp2;
                            for (Integer integer : bcp.getLinkedTo_id_order(game.getCurrentmap())) {
                                if (!(integer >= game.getCurrentmap().getCheckpointsList().size() || integer < 0)) {
                                    bcp2 = game.getCurrentmap().getCheckpointsList().get(integer);
                                    if (Utils.Distance(bcp2.getCoordWithDelta(game.getCurrentDeltaTime()), coordsPlayer) < bcp2.getRadiusSize() + p.getVelocity().lengthSquared() && bcp2.Through(lastloc, current, game.getCurrentDeltaTime())) {
                                        bcp2.ApplyBoost(p);
                                        game.getPlayerInformationSaver().setBackCP(p, false);
                                        int i = game.getPlayerInformationSaver().getCurrentCP(p);
                                        if (cmc.LineHelper && cmc.NEXT_CHECKPOINT_PARTICLE != null && map.getCheckpointsList().size() > i && i >= 1) {
                                            CheckPoint curr = map.getCheckpointsList().get(integer);
                                            game.drawLineNextObject(p, bcp2, cp);
                                        }
                                        break;
                                    }
                                } else {
                                    cp.RemoveLink(integer);
                                    Main.instance.getSerialization().saveMap(game.getCurrentmap());
                                }

                            }
                        }
                    }

                } else {
                    for (End end : game.getCurrentmap().getEndsList()) {
                        if (Utils.Distance(end.getCoordWithDelta(game.getCurrentDeltaTime()), coordsPlayer) < end.getRadiusSize() + p.getVelocity().lengthSquared() && end.Through(lastloc, current, game.getCurrentDeltaTime())) {
                            boolean continuedraw = game.EndVerified(p);
                            CheckPoint cp_zero = game.getCurrentmap().getCheckpointsList().get(0);
                            if (continuedraw && cmc.LineHelper) {
                                Utils.playSoundDispatcher(p, p.getLocation(), cmc.SOUND_END_PASSED, "BLOCK_NOTE_HAT", cmc.SOUND_END_PASSED, "minecraft:block.note_block.hat", cmc.SOUND_END_PASSED_VOLUME, cmc.SOUND_END_PASSED_PITCH);

                                p.sendMessage(cmc.basicsetting(cmc.MSG_LAP_VERIFIED.replace("{LAP_PASSED}", String.valueOf(game.getPlayerInformationSaver().getFinishedlaps(p))).replace("{LAP_MAX}", String.valueOf(game.getCurrentmap().getNumberoflaps())), p));
                                game.drawLineNextObject(p, end, cp_zero);
                                game.getPlayerInformationSaver().addDLV(p, true, end);
                            }
                        }
                    }
                }
                game.getPlayerInformationSaver().setLastLoc(p);
            } else {
                game.getPlayerInformationSaver().setLastLoc(p);
            }
        } else if (cmc.TP_ON_HIT && p.isOnGround() && game.getGameState() == GameState.STARTED && !((game.PlayerModeof(p).equals(PlayerMode.SPEC) || game.specEnd(p)))) {
            game.back1CP(p);
        }
    }


}