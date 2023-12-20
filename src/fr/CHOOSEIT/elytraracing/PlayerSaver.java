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

package fr.CHOOSEIT.elytraracing;

import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerSaver {
    private static HashMap<UUID, PlayerSaver> PlayerS = new HashMap<>();

    public static void SetIf(Player player, boolean clearTab) {
        if (player.isOnline() && PlayerS.containsKey(player.getUniqueId())) {
            PlayerS.get(player.getUniqueId()).Set(player, clearTab);
        }
    }

    public static void Save(Player player) {
        PlayerSaver playerSaver = new PlayerSaver(player);
        PlayerS.put(player.getUniqueId(), playerSaver);
    }

    private void Set(Player player, boolean clearTab) {

        player.teleport(L);
        Utils.ClearEVERYTHING(player);
        player.getInventory().setContents(playerinventory);
        player.getInventory().setHelmet(HCLB[0]);
        player.getInventory().setChestplate(HCLB[1]);
        player.getInventory().setLeggings(HCLB[2]);
        player.getInventory().setBoots(HCLB[3]);
        //player.setMaxHealth(HMH[1]);
        //player.setHealth(HMH[0]);
        player.setFoodLevel(F);
        player.addPotionEffects(activepotionseffect);
        player.setGameMode(G);
        player.setLevel(LVL);
        player.setExp(FLVL);
        player.updateInventory();
        if (clearTab) {
            Utils.ClearHeader(player);
        }

        if (CommandtoExe != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                @Override
                public void run() {
                    CommandtoExe.forEach(o -> o.ExeCOMMAND(player));
                }
            }, 5);

        }

        PlayerS.remove(player.getUniqueId());
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void addCommandExe(Player player, Utils.CommandExe cmd) {

        if (PlayerS.containsKey(player.getUniqueId())) {
            if (PlayerS.get(player.getUniqueId()).CommandtoExe == null) {
                PlayerS.get(player.getUniqueId()).CommandtoExe = new ArrayList<>();
            }
            PlayerS.get(player.getUniqueId()).CommandtoExe.add(cmd);
        }
    }

    public PlayerSaver(Player player) {
        LVL = player.getLevel();
        FLVL = player.getExp();
        CommandtoExe = null;
        playerinventory = player.getInventory().getContents().clone();
        HCLB = new ItemStack[]{player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots()};
        HMH = new Double[]{player.getHealth(), player.getMaxHealth()};
        F = player.getFoodLevel();
        activepotionseffect = player.getActivePotionEffects();
        L = player.getLocation();
        G = player.getGameMode();

    }

    ItemStack[] playerinventory;
    ItemStack[] HCLB;
    Double[] HMH;
    int F;
    Collection<PotionEffect> activepotionseffect;
    Location L;
    int LVL;
    float FLVL;
    public ArrayList<Utils.CommandExe> CommandtoExe;

    GameMode G;

}
