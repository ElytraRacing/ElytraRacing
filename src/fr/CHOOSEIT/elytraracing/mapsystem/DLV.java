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

package fr.CHOOSEIT.elytraracing.mapsystem;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.optiHelper.McHelper;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.mapsystem.Objects.CheckPoint;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DLV {
    public DLV(SimpleLocation location, Vector velocity, int currentFireworks, ArrayList<Integer> passedAdditionalObjectsPassed, ObjectClass cp) {
        this.location = location;
        this.velocity = velocity;
        this.fireworks = currentFireworks;
        this.passedAdditionalObjectsPassed = passedAdditionalObjectsPassed;
        this.cp = cp;
    }

    public void Set(Player player, Game game, Location next) {
        Location loc = location.getlocation();
        if (game.getCurrentmap().isNextCheckpointAngleAvailable() && game.getCurrentmap().getAngleType() == CheckPoint.TYPE_ANGLE_CHECKPOINT.NEXT_CHECKPOINT && next != null) {
            loc.setDirection(next.toVector().subtract(loc.toVector()));
        }
        McHelper.setGliding(player, true, !Main.customMessageConfig.DISABLE_ELYTRA);
        game.getPlayerInformationSaver().setPassedAdditionalObjects(player, passedAdditionalObjectsPassed);


        if (loc.getWorld() != null) {
            player.teleport(loc);
        } else {
            Utils.log_print("[ERROR][ElytraRacing] This world isn't loaded");
        }
        if (Main.cmc().CHECKPOINT_TELEPORT_MIDDLE) {
            Location middle = cp.getMiddleLocation();
            if (middle != null) {
                player.teleport(middle);
            }
        }
        player.setVelocity(new Vector().zero());
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                player.setVelocity(velocity.clone());
            }
        }, 1L);
        ItemStack fireworksitem = player.getInventory().getItem(1);
        if (fireworksitem != null && fireworksitem.getType() == Material.getMaterial("FIREWORK")) {
            if (fireworks == 0) {
                fireworksitem.setType(Material.AIR);
            } else {
                fireworksitem.setAmount(fireworks);
            }
        } else {
            player.getInventory().setItem(2, new ItemStack(Material.getMaterial("FIREWORK"), fireworks));
        }


    }

    public void Delete() {
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private final SimpleLocation location;
    private final Vector velocity;
    private final int fireworks;
    private ArrayList<Integer> passedAdditionalObjectsPassed;
    private ObjectClass cp;
}
