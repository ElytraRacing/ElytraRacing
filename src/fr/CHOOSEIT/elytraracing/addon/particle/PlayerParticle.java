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

package fr.CHOOSEIT.elytraracing.addon.particle;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.PlayerInformation.GamePlayer;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleConfig;
import fr.CHOOSEIT.elytraracing.addon.particle.file.ParticleCreator;
import fr.CHOOSEIT.elytraracing.gamesystem.Game;
import fr.CHOOSEIT.elytraracing.gamesystem.timer.InfiniteGameTimer;
import fr.CHOOSEIT.elytraracing.gamesystem.timer.eTimer;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerParticle {

    private static Map<UUID, String> lastUsedParticlePlayer = new HashMap<>();
    private static Map<UUID, Boolean> disableParticlePlayer = new HashMap<>();
    private static HashMap<Player, PlayerParticle> ppmap = new HashMap<>();
    private static ParticleConfig pc = ParticleConfig.particleConfig;


    public static void clearPlayerData(UUID uuid){
        lastUsedParticlePlayer.remove(uuid);
        disableParticlePlayer.remove(uuid);
    }
    private static boolean isDisableParticle(UUID uuid){
        return disableParticlePlayer.getOrDefault(uuid, false);
    }
    public static boolean isDisableParticle(Player p){
        return isDisableParticle(p.getUniqueId());
    }
    public static boolean isDisableParticle(GamePlayer p){
        return isDisableParticle(p.getSpigotPlayer());
    }
    public static void setDisableParticle(UUID uuid, boolean value){
        disableParticlePlayer.put(uuid, value);
    }

    public static boolean selectLastUsed(Player p, Game game){
        String lastUsed = lastUsedParticlePlayer.getOrDefault(p.getUniqueId(), null);
        if(lastUsed == null) return true;
        ParticleCreator pc = ParticleCreator.getParticle(lastUsed);
        if(pc == null) return false;
        return select(p, pc, game);
    }
    public static boolean select(Player p, ParticleCreator pc, Game game){
        if(pc == null || pc.equals(ParticleCreator.NONE)){
            delete(p);
            lastUsedParticlePlayer.remove(p.getUniqueId());
            return true;
        }
        else{
            ShapeParticle sp = pc.createParticle(p);
            ColorCreator cc = pc.createColor(p, false, Utils.isColoredParticle(pc.getParticle()));
            Set<String> showWith = Arrays.stream(pc.getShowWithParticle()).filter(e -> !e.equals("") && !e.equals(pc.getName()) && ParticleCreator.exist(e)).collect(Collectors.toSet());
            if(sp != null && cc != null){
                if(showWith.size() > 0){
                    List<ShapeParticle> shapes = new ArrayList<>();
                    List<ColorCreator> colors = new ArrayList<>();
                    List<String> particles = new ArrayList<>();
                    shapes.add(sp);
                    colors.add(cc);
                    particles.add(pc.getParticle());
                    for (String s : showWith) {
                        ParticleCreator pcnext = ParticleCreator.getParticle(s);
                        ShapeParticle spnext = pcnext.createParticle(p);
                        ColorCreator ccnext = pcnext.createColor(p, false, Utils.isColoredParticle(pcnext.getParticle()));
                        if(spnext != null && ccnext != null){
                            shapes.add(spnext);
                            colors.add(ccnext);
                            particles.add(pcnext.getParticle());
                        }
                    }
                    new PlayerParticle(p, pc.getName(), shapes, colors, particles, game);
                }
                else {
                    new PlayerParticle(p, pc.getName(), sp, cc, pc.getParticle(), game);
                }
                lastUsedParticlePlayer.put(p.getUniqueId(), pc.getName());
                return true;
            }
            return false;
        }
    }
    public static boolean selectNone(Player p){
        return select(p, ParticleCreator.NONE, null);
    }
    public static void delete(Player p){
        if(has(p)){
            ppmap.get(p).delete();
        }
        ppmap.remove(p);
    }
    public static void start(Player p){
        if(has(p)){
            ppmap.get(p).start();
        }
    }

    public static boolean has(Player p){
        return ppmap.containsKey(p) && ppmap.get(p) != null;
    }
    public static String getNameOfParticle(Player p){
        if(has(p)) return ppmap.get(p).getName();
        return "";
    }

    private eTimer playerTimer = eTimer.NONE;
    private String name = "";

    public PlayerParticle(Player p,String name , ShapeParticle shapeParticle, ColorCreator colorCreator, String particle){
        this(p, name, shapeParticle, colorCreator, particle, null);
    }
    public PlayerParticle(Player p, String name, ShapeParticle shapeParticle, ColorCreator colorCreator, String particle, Game game){
        this(p, name, shapeParticle, colorCreator, particle, game, false);
    }
    public PlayerParticle(Player p, String name, List<ShapeParticle> shapeParticles, List<ColorCreator> colorCreators, List<String> particles, Game game){
        this(p, name, shapeParticles, colorCreators, particles, game, false);
    }
    public PlayerParticle(Player p, String name, ShapeParticle shapeParticle, ColorCreator colorCreator, String particle, Game game, boolean forceNonGliding) {
        this(p, name, Collections.singletonList(shapeParticle), Collections.singletonList(colorCreator), Collections.singletonList(particle), game, forceNonGliding);
    }

    public PlayerParticle(Player p, String name, List<ShapeParticle> shapeParticles, List<ColorCreator> colorCreators, List<String> particles, Game game, boolean forceNonGliding) {
        if(has(p)) delete(p);
        if(!pc.ENABLE) return;
        if(shapeParticles == null || p == null) return;
        playerTimer = new InfiniteGameTimer(pc.INTERVAL_PERSONAL_PARTICLE_REFRESH) {
            @Override
            protected void event(int timeLeft) {
                super.event(timeLeft);
                if(!p.isGliding() && !forceNonGliding) return;
                for (int j = 0; j < Math.min(shapeParticles.size(), Math.min(colorCreators.size(), particles.size())); j++) {

                    ShapeParticle shapeParticle = shapeParticles.get(j);
                    ColorCreator colorCreator = colorCreators.get(j);
                    String particle = particles.get(j);

                    if(shapeParticle == null) continue;
                    boolean coloredParticle = (particle.equalsIgnoreCase("REDSTONE"));
                    if(!(Main.availableParticle.contains(particle) || coloredParticle)) continue;
                    Location loc = p.getLocation();
                    float x = (float) (loc.getX());
                    float y = (float) (loc.getY());
                    float z = (float) (loc.getZ());
                    float[][] pl = shapeParticle.coord();
                    boolean hasColor = colorCreator != null && coloredParticle;

                    RGBConstant rgb = null;
                    if(hasColor) rgb = colorCreator.getColor();
                    Set<Player> players = new HashSet<>();
                    if(game != null){
                        game.getPlayersAbleToSee().forEach(p -> {
                            Player player = p.getSpigotPlayer();
                            if(player != null)
                                if(player.equals(p.getSpigotPlayer()))
                                    players.add(player);
                                else if(!PlayerParticle.isDisableParticle(p))
                                    players.add(player);
                        });
                    }
                    else {
                        players.add(p);
                    }
                    for (int i = 0; i < pl.length; i++) {
                        if(coloredParticle)
                            Utils.sendColoredParticle(players, particle,x + pl[i][0], y + pl[i][1], z + pl[i][2], hasColor ? rgb.r : 0, hasColor ? rgb.g : 0, hasColor ? rgb.b : 0, 1);
                        else
                            Utils.sendParticle(players, particle,x + pl[i][0], y + pl[i][1], z + pl[i][2],0 , 0, 0, 1);
                    }
                }
            }
        };
        ppmap.put(p, this);
        this.name = name;
    }
    public void start(){
        if(!pc.ENABLE) return;
        playerTimer.start();
    }

    public void delete(){
        if(!pc.ENABLE) return;
        playerTimer.cancel();
    }

    public String getName() {
        return name;
    }
}
