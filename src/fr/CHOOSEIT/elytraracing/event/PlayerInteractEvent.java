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
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.GameMoment;
import fr.CHOOSEIT.elytraracing.gamesystem.GameState;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkInteract;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkItems;
import fr.CHOOSEIT.elytraracing.mapsystem.linkplayer.LinkPlayer;
import fr.CHOOSEIT.elytraracing.signssystem.SignsSystem;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PlayerInteractEvent implements Listener {
    public String IDENTIFIER = "%%__USER__%%";

    private CustomMessageConfig cmc = Main.customMessageConfig;

    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        Game game = Game.Find(player);
        if (action.equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                SignsSystem.click(event.getClickedBlock(), player);
            }
        }
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR && player.getInventory().getItemInMainHand().getItemMeta() != null) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (LinkItems.LOCK.getItem().getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName() != null && LinkItems.LOCK.getItem().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                LinkInteract.InteractLinkObject(player, LinkItems.LOCK, action);
                event.setCancelled(true);
                return;
            }
            if (LinkItems.MAP.getItem().getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName() != null && LinkItems.MAP.getItem().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                LinkInteract.InteractLinkObject(player, LinkItems.MAP, action);
                event.setCancelled(true);
                return;
            }
            if (LinkPlayer.existLink(player)) {
                for (LinkItems value : LinkItems.values()) {
                    if (value != LinkItems.LOCK && value != LinkItems.MAP && (value.getItem().getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName() != null && value.getItem().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))) {
                        LinkInteract.InteractLinkObject(player, value, action);
                        event.setCancelled(true);
                        return;
                    }
                }
            }


        }
        if (game == null) {
            return;
        }
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {

            if (player.getInventory().getItemInMainHand().getType() != Material.AIR && player.getInventory().getItemInMainHand().getItemMeta() != null) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null || game.getGameMoment() == GameMoment.STARTINGCANTMOVE) {
                    return;
                }
                if (item.getType() == Material.getMaterial("FIREWORK")) {
                    event.setCancelled(false);

                    //Make firework infinite
                    if(game.IsInfiniteFireworks()){
                        item.setAmount(item.getAmount() + 1);
                    }
                    return;
                }
                event.setCancelled(true);

                ItemStack similarTo = Utils.getItemStack(cmc.ITEM_1CP_BACK_MATERIAL, Arrays.asList("REDSTONE"), cmc.basicsettingnoprefix(cmc.ITEM_1CP_BACK, null), player.getName());
                if (isSimilar(item, similarTo)) {
                    Interact(player, InteractAction.ONECP, game, item);
                    return;
                }
                similarTo = Utils.getItemStack(cmc.ITEM_BED_MATERIAL, Arrays.asList("BED", "LEGACY_BED_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_BED, null), player.getName());
                if (isSimilar(item, similarTo)) {
                    Interact(player, InteractAction.BED, game, item);
                    return;
                }
                similarTo = Utils.getItemStack(cmc.ITEM_RESTART_MATERIAL, Arrays.asList("LEGACY_PISTON_BASE", "PISTON_BASE", "PISTON"), cmc.basicsettingnoprefix(cmc.ITEM_RESTART, null), player.getName());
                if (isSimilar(item, similarTo)) {
                    Interact(player, InteractAction.RESTART, game, item);
                    return;
                }
                similarTo = Utils.getItemStack(cmc.ITEM_2CP_BACK_MATERIAL, Arrays.asList("REDSTONE_BLOCK"), cmc.basicsettingnoprefix(cmc.ITEM_2CP_BACK, null), player.getName());
                if (isSimilar(item, similarTo)) {
                    Interact(player, InteractAction.TWOCP, game, item);
                    return;
                }
                similarTo = Utils.getItemStack(cmc.ITEM_SPEC_MATERIAL, Arrays.asList("EYE_OF_ENDER", "ENDER_EYE"), cmc.basicsettingnoprefix(cmc.ITEM_SPEC, null), player.getName());
                if (isSimilar(item, similarTo) && game.getPlayerInformationSaver().hasFinished(player)) {
                    Interact(player, InteractAction.SPEC, game, item);
                    return;
                }
                similarTo = Utils.getItemStack(cmc.ITEM_HOST_MATERIAL, Arrays.asList("CLOCK", "WATCH"), cmc.basicsettingnoprefix(cmc.ITEM_HOST, null), player.getName());

                if (isSimilar(item, similarTo) && (game.getGameState().equals(GameState.WAITING) || game.getGameState().equals(GameState.STARTING))) {
                    Interact(player, InteractAction.HOST, game, item);
                    return;
                }
                similarTo = Utils.getItemStack(cmc.ITEM_DNF_MATERIAL, Arrays.asList("COMPARATOR", "REDSTONE_COMPARATOR", "LEGACY_REDSTONE_COMPARATOR_OFF"), cmc.basicsettingnoprefix(cmc.ITEM_DNF, null), player.getName());
                if (isSimilar(item, similarTo)) {
                    Interact(player, InteractAction.DNF, game, item);
                    return;
                }

                similarTo = Utils.getItemStack(cmc.ITEM_PARTICLE_MATERIAL, Arrays.asList("MAGMA_CREAM"), cmc.basicsettingnoprefix(cmc.ITEM_PARTICLE, null), player.getName());
                if (isSimilar(item, similarTo)) {
                    Interact(player, InteractAction.PARTICLE, game, item);
                    return;
                }

            }
        }
    }

    public enum InteractAction {
        DNF, BED, ONECP, TWOCP, RESTART, SPEC, CUSTOM, HOST, PARTICLE
    }

    public void Interact(Player player, InteractAction interactAction, Game game, ItemStack itemStack) {
        game.Interact(player, interactAction, game, itemStack);
    }

    public boolean isSimilar(ItemStack one, ItemStack other) {
        try {
            return one.isSimilar(other);
        } catch (NullPointerException e) {
            if (one.getType() == other.getType() && one.getItemMeta() != null && other.getItemMeta() != null && one.getItemMeta().getDisplayName() != null && other.getItemMeta().getDisplayName() != null) {
                return one.getItemMeta().getDisplayName().equals(other.getItemMeta().getDisplayName());
            }
        }
        return false;
    }


}
