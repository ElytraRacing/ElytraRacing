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

package fr.CHOOSEIT.elytraracing.signssystem;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.gamesystem.ElytraRacingCommand;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Objects;

public class SignsSystem implements Listener {
    @EventHandler
    public void onSign(SignChangeEvent e) {
        Player p = e.getPlayer();
        if (p != null && (Main.customMessageConfig.PERMISSION_SIGN == null || p.hasPermission(Main.customMessageConfig.PERMISSION_SIGN))) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                @Override
                public void run() {
                    new SignsSystem(e.getBlock());
                }
            }, 3);
        }
    }

    public SIGN_TYPE sign_type;
    public SimpleLocation location;
    public String material_sign;
    public String option;

    public SignsSystem(Block b) {
        if (b == null) {
            return;
        }
        if (Main.signsSaver.getSign(b.getLocation()) == null) {
            Sign sign = (Sign) b.getState();
            this.material_sign = b.getType().toString();
            this.location = new SimpleLocation(b.getLocation());

            if (sign.getLine(0).equalsIgnoreCase("[er_queue]") && !sign.getLine(1).isEmpty()) {
                this.sign_type = SIGN_TYPE.QUEUE;
                this.option = sign.getLine(1);
                Main.signsSaver.Signs.add(this);
                Main.instance.getSerialization().saveSigns();
                update();
            } else {
                delete();

            }
        }
    }

    public void update() {
        if (sign_type.equals(SIGN_TYPE.QUEUE)) {
            if (location != null && location.getlocation() != null && location.getlocation().getWorld() != null && sign_type != null && Material.getMaterial(material_sign) != null) {
                Block b = Objects.requireNonNull(location.getlocation().getWorld()).getBlockAt(location.getlocation());
                if (b.getState() instanceof Sign && b.getType().equals(Material.getMaterial(material_sign))) {

                    Sign sign = (Sign) b.getState();
                    if (ElytraRacingCommand.Queuesize(option) == 1) {
                        Game game = ElytraRacingCommand.QueueoneGamesize(option);
                        if (game == null) {
                            String[] format = Main.customMessageConfig.SIGN_FORMAT_DISABLED;
                            for (int i = 0; i < 4; i++) {
                                sign.setLine(i, Main.customMessageConfig.basicsettingnoprefix(format[i], null));
                            }
                            sign.update();
                        } else {
                            String[] format = Main.customMessageConfig.SIGN_FORMAT_ONEQUEUE_ONEGAME;
                            for (int i = 0; i < 4; i++) {
                                sign.setLine(i, Main.customMessageConfig.basicsettingnoprefix(messagerefactor(format[i], game), null));
                            }
                            sign.update();
                        }
                        return;
                    }
                    Game g = ElytraRacingCommand.Queue(option, null);
                    if (g == null || Main.autoGamesSaver.DISABLED_QUEUES.contains(option)) {
                        String[] format = Main.customMessageConfig.SIGN_FORMAT_DISABLED;
                        for (int i = 0; i < 4; i++) {
                            sign.setLine(i, Main.customMessageConfig.basicsettingnoprefix(format[i], null));
                        }
                        sign.update();

                    } else {
                        String[] format = Main.customMessageConfig.SIGN_FORMAT;
                        for (int i = 0; i < 4; i++) {
                            sign.setLine(i, Main.customMessageConfig.basicsettingnoprefix(messagerefactor(format[i], g), null));
                        }
                        sign.update();
                    }
                }
            }
        }

    }

    public String messagerefactor(String s, Game g) {
        String gameState = g.getGameState().toString();
        String State = "";
        if (Main.customMessageConfig.STATES.containsKey(gameState)) {
            State = Main.customMessageConfig.STATES.get(gameState);
        }
        return s.replace("{GAME}", g.getName()).replace("{STATE}", State).replace("{MAP}", g.getCurrentmap().getName()).replace("{PLAYERS}", String.valueOf(g.getPlayerList().size())).replace("{MAX_PLAYERS}", String.valueOf(g.getMaxPlayer()));
    }

    public static void delete(Block b) {
        SignsSystem ss = Main.signsSaver.getSign(b.getLocation());
        if (ss != null) {
            ss.delete();
        }
    }

    public static void click(Block b, Player p) {
        SignsSystem ss = Main.signsSaver.getSign(b.getLocation());
        if (ss != null) {
            ss.click(p);
        }
    }

    public void click(Player p) {
        if (sign_type.equals(SIGN_TYPE.QUEUE)) {
            Game g = ElytraRacingCommand.Queue(option, p);
            if (g != null) {
                g.rawPlayerjoin(p);
                update();
                return;
            }
        }
        update();
    }

    public void delete() {
        Main.signsSaver.Signs.remove(this);
        Main.instance.getSerialization().saveSigns();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public enum SIGN_TYPE {
        QUEUE
    }
}
