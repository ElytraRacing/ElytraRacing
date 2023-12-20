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

import fr.CHOOSEIT.elytraracing.addon.particle.ColorCreator;
import fr.CHOOSEIT.elytraracing.addon.particle.RGBConstant;
import fr.CHOOSEIT.elytraracing.addon.particle.ShapeParticle;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ParticleCreator {

    private static Map<String,ParticleCreator> particlelist = new HashMap<>();
    private static String DEFAULT_ITEM = "PAPER";
    public static ParticleCreator NONE;

    public static void addParticle(ParticleCreator pc){
        particlelist.put(pc.name.toUpperCase(), pc);
    }
    public static boolean exist(String name){
        return particlelist.containsKey(name.toUpperCase());
    }
    public static ParticleCreator getParticle(String name){
        if(!exist(name)) return null;
        ParticleCreator pc = particlelist.get(name.toUpperCase());
        if(pc.enable)
            return new ParticleCreator(pc);
        return null;
    }
    public static void clear(){
        particlelist.clear();
    }

    public static List<ParticleCreator> getParticlelist() {
        return new ArrayList<>(particlelist.values());
    }

    private String name;
    private boolean enable;
    private boolean removeFromPlayableSet;
    private String particle;
    private String item;
    private String[] showWithParticle = new String[]{"", "", ""};
    private keyOption[] shapeParticle;
    private keyOption[] colorCreator;


    public static class keyOption{
        public String key;
        public String option;

        public keyOption(String key, String option) {
            this.key = key;
            this.option = option;
        }
    }

    public ParticleCreator(String name, String particle, String item,String[] showWithParticle, keyOption[] shapeParticle, keyOption[] colorCreator) {
        this.name = name;
        this.particle = particle;
        this.shapeParticle = shapeParticle;
        this.item = item;
        this.enable = true;
        this.colorCreator = colorCreator;
        this.showWithParticle = showWithParticle;
    }
    public ParticleCreator(String name, String particle, keyOption[] shapeParticle, keyOption[] colorCreator) {
        this(name, particle, DEFAULT_ITEM, new String[]{"", "", ""}, shapeParticle, colorCreator);
    }

    public ParticleCreator(ParticleCreator particleCreator) {
        this(particleCreator.name, particleCreator.particle, particleCreator.item, particleCreator.showWithParticle, particleCreator.shapeParticle, particleCreator.colorCreator);
    }

    public ShapeParticle createParticle(Player p, boolean logger){
        return ParticleCreatorOptionList.createParticle(name, p, shapeParticle, logger);
    }
    public ShapeParticle createParticle(Player p){
        return createParticle(p, false);
    }
    public ColorCreator createColor(Player p, boolean logger, boolean hasColoredParticle){
        if(colorCreator == null) {
            if(hasColoredParticle){
                return null;
            }
            else return new ColorCreator() {
                @Override
                public RGBConstant getColor() {
                    return new RGBConstant(1,0,0);
                }
            };
        }
        return ColorCreatorOptionList.createColor(name, p, colorCreator, logger);
    }
    public boolean hasColor(){
        return colorCreator != null;
    }
    public boolean hasParticle(){
        return shapeParticle != null;
    }

    public boolean isNone(){
        return this.getName().equals(NONE.getName());
    }
    public String getParticle() {
        return particle;
    }

    public boolean isEnable() {
        return enable;
    }
    public boolean isPlayable(){
        return isEnable() && !removeFromPlayableSet;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem(){
        return Utils.getItemStack(item, Arrays.asList("PAPER"), "");
    }

    public String[] getShowWithParticle() {
        if(showWithParticle == null) return new String[0];
        return showWithParticle;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
