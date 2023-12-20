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

package fr.CHOOSEIT.elytraracing.addon.particle.file;

import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.addon.particle.ColorCreator;
import fr.CHOOSEIT.elytraracing.addon.particle.PlayerParticle;
import fr.CHOOSEIT.elytraracing.addon.particle.ShapeParticle;
import fr.CHOOSEIT.elytraracing.filesystem.FileUtils;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParticleConfig {
    public static ArrayList<String> listOfSupportedColoredParticle = new ArrayList<>(Arrays.asList("REDSTONE"));
    public static File dir = new File(Main.instance.getDataFolder(), "/particles/");
    public static String filename = "particleConfig.json";
    public static ParticleConfig particleConfig;
    public static String prefixPermission = "elytraracing.particle.";

    public void save() {
        particleConfig = this;
        Main.serialization.saveConfig(dir, filename, this);
    }

    public static void load() {
        load(true);
    }

    public static void load(boolean save) {
        String json = FileUtils.loadContent(new File(dir, filename));
        ParticleConfig config = Main.serialization.deserialize(json, ParticleConfig.class, filename);
        if (config != null) {
            if (save) {
                config.save();
            }
            particleConfig = config;
        } else if (save) {
            config = new ParticleConfig();
            config.save();
            particleConfig = config;
        } else {
            particleConfig = new ParticleConfig();
        }

        particleConfig.loadParticles();

        ParticleCreator.NONE = new ParticleCreator(ParticleConfig.particleConfig.GUI_NO_PARTICLE, null, null, null);
    }

    private boolean isValid(ParticleCreator pc, Player p){
        boolean valid = true;
        if(!(isValidParticle(pc))){
            ParticleCreatorOptionList.logMessage("- Not valid particle");
            valid = false;
        }
        valid = isValidShapeParticle(pc, p) != null || valid;
        valid = isValidColorCreator(pc, p, Utils.isColoredParticle(pc.getParticle())) != null || valid;
        return valid;
    }
    private boolean isValidParticle(ParticleCreator pc){
        return Main.availableParticle.contains(pc.getParticle()) || pc.getParticle().equals("REDSTONE");
    }
    private ShapeParticle isValidShapeParticle(ParticleCreator pc, Player p){
        ShapeParticle sp = pc.createParticle(p, true);
        if(sp == null){
            Utils.log_print("- One or multiple step of this particle shape creator is not valid");
            return null;
        }
        return sp;
    }
    private ColorCreator isValidColorCreator(ParticleCreator pc, Player p, boolean hasColoredParticle){
        ColorCreator cc = pc.createColor(p, true, hasColoredParticle);
        if(pc.createColor(p, true, hasColoredParticle) == null && pc.hasColor()){
            Utils.log_print("- One or multiple step of this particle color creator is not valid");
            return null;
        }
        return cc;
    }

    public void loadParticles(){
        ParticleCreator.clear();
        ParticleCreatorOptionList.logMessage("[INFO] If you are using the showWith system, make sure that the particle you associate with this particle are loaded before ! (put them in the list before this one)");

        for (ParticleCreator particle : particles) {
            boolean valid = false;
            if(!particle.isEnable()) {
                Player p = Bukkit.getPlayer(particle.getName());
                if(p != null){
                    ParticleCreatorOptionList.logMessage("Loading and activating " + particle.getName()+ "...");
                    boolean validParticle = isValidParticle(particle);
                    ShapeParticle sp = isValidShapeParticle(particle, p);
                    ColorCreator cc = isValidColorCreator(particle, p, Utils.isColoredParticle(particle.getParticle()));
                    if(!(validParticle && sp != null && cc != null)) continue;
                    Set<String> showWith = Arrays.stream(particle.getShowWithParticle()).filter(e -> !e.equals("") && !e.equals(particle.getName()) && ParticleCreator.exist(e)).collect(Collectors.toSet());
                    if(sp != null && cc != null) {
                        if (showWith.size() > 0) {
                            List<ShapeParticle> shapes = new ArrayList<>();
                            List<ColorCreator> colors = new ArrayList<>();
                            List<String> particles = new ArrayList<>();
                            shapes.add(sp);
                            colors.add(cc);
                            particles.add(particle.getParticle());
                            for (String s : showWith) {
                                ParticleCreator pcnext = ParticleCreator.getParticle(s);
                                ShapeParticle spnext = pcnext.createParticle(p);
                                ColorCreator ccnext = pcnext.createColor(p, false, Utils.isColoredParticle(pcnext.getParticle()));
                                if (spnext != null && ccnext != null) {
                                    shapes.add(spnext);
                                    colors.add(ccnext);
                                    particles.add(pcnext.getParticle());
                                }
                            }
                            ParticleCreatorOptionList.logMessage(shapes.toString());
                            new PlayerParticle(p, particle.getName(), shapes, colors, particles, null, true).start();
                        } else {
                            new PlayerParticle(p, particle.getName(), sp, cc, particle.getParticle(), null, true).start();
                        }
                    }
                }
                continue;
            }
            ParticleCreatorOptionList.logMessage("Loading " + particle.getName()+ "...");
            valid = isValid(particle, null);
            if(!valid) continue;
            Utils.log_print("-> Valid");
            ParticleCreator.addParticle(particle);
        }
    }
    public String c(String MSG) {
        if (MSG == null) {
            return "";
        }
        return MSG.replaceAll("&", "§");
    }

    public boolean ENABLE = false;
    public int INTERVAL_PERSONAL_PARTICLE_REFRESH = 1;
    public String GUI_PARTICLE_TITLE = "Particle Selector";
    public String GUI_ITEM_PARTICLE_TITLE = "&f{PARTICLE_NAME}";
    public String GUI_ITEM_PARTICLE_TITLE_SELECTED = "&a{PARTICLE_NAME}";
    public String GUI_NO_PARTICLE = "None";
    public String GUI_DISABLE_OTHERS_PARTICLES_UNSELECTED = "&fDisable others particles";
    public String GUI_DISABLE_OTHERS_PARTICLES_SELECTED = "&fDisable others particles";
    public String GUI_DISABLE_OTHERS_PARTICLES_LORE_SELECTED = "&f-> Currently: &a✓";
    public String GUI_DISABLE_OTHERS_PARTICLES_LORE_UNSELECTED = "&f-> Currently: &c❌";
    public String MSG_SAVE_SUCCESSFUL = "&fChanges have been &asucessfully &fsaved";
    public String GUI_CLICKCHANGE = "Click here to change page";
    public boolean GUI_ENCHANT_SELECTED = true;
    public ParticleCreator[] particles =
        new ParticleCreator[]{
            new ParticleCreator("GreenCircle", "TOTEM", new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("SHAPE_CIRCLE", "7;1"),
                new ParticleCreator.keyOption("MASK_PLAYERFOLLOW", "")
            },null),

            new ParticleCreator("RotatingRainbowCircle", "REDSTONE", new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("SHAPE_CIRCLE", "7;1"),
                new ParticleCreator.keyOption("MASK_Z_ROTATION", "10000;true"),
                new ParticleCreator.keyOption("MASK_PLAYERFOLLOW", "")
            }, new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("RAINBOW", "15000;true")
            }),

            new ParticleCreator("RainbowPoint", "REDSTONE", new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("SHAPE_POINT", "")
            }, new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("RAINBOW", "15000;true")
            }),
            new ParticleCreator("RedPoint", "REDSTONE", new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("SHAPE_POINT", "")
            }, new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("CONSTANT", "255;0;0")
            }),
            new ParticleCreator("HorizontalWave", "REDSTONE", new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("SHAPE_CIRCLE", "7;0.75"),
                new ParticleCreator.keyOption("MASK_X_ROTATION",  "7000;true"),
                new ParticleCreator.keyOption("MASK_PLAYERFOLLOW", "")
            },new ParticleCreator.keyOption[]{
                new ParticleCreator.keyOption("CONSTANT", "0;153;255")
            }),
            new ParticleCreator("VerticalWavingPoint", "REDSTONE", new ParticleCreator.keyOption[]{
                    new ParticleCreator.keyOption("SHAPE_CIRCLE", "1;0.75"),
                    new ParticleCreator.keyOption("MASK_Y_ROTATION",  "1000;true"),
                    new ParticleCreator.keyOption("MASK_PLAYERFOLLOW", "")
            },new ParticleCreator.keyOption[]{
                    new ParticleCreator.keyOption("CONSTANT", "0;153;255")
            }),
            new ParticleCreator("RainbowTube", "REDSTONE", new ParticleCreator.keyOption[]{
                    new ParticleCreator.keyOption("SHAPE_CIRCLE", "20;0.66"),
                    new ParticleCreator.keyOption("MASK_PLAYERFOLLOW", "")
            }, new ParticleCreator.keyOption[]{
                    new ParticleCreator.keyOption("RAINBOW", "10000;true")
            }),

        }
    ;

}
