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

import fr.CHOOSEIT.elytraracing.addon.particle.ShapeParticle;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.basicShape.LineParticle;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.basicShape.SquareParticle;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.BasicComposite;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.RotatedPlayerShape;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.TranslationConstantShape;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.animated.RotatedXAnimatedShape;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.animated.RotatedYAnimatedShape;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.maskShape.animated.RotatedZAnimatedShape;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.basicShape.CircleParticle;
import fr.CHOOSEIT.elytraracing.addon.particle.shapeParticle.basicShape.PointParticle;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ParticleCreatorOptionList
{
    private static final String PARTICLE_ISNULL= "Particle used by a mask does not exist";
    private enum getterParticleCreator{

        SHAPE_POINT(SPOINT, 0, false, true),
        SHAPE_CIRCLE(SCIRCLE, 2, false, true),
        SHAPE_LINE(SLINE, 2, false, true),
        SHAPE_SQUARE(SSQUARE, 2, false, true),


        MASK_X_ROTATION(XROTATE, 2, true, false),
        MASK_Y_ROTATION(YROTATE, 2, true, false),
        MASK_Z_ROTATION(ZROTATE, 2, true, false),
        MASK_TRANSLATION(TRANSLATION, 3, true, false),
        MASK_PLAYERFOLLOW(MPLAYERFOLLOW, 0, true, false);
        private final ParticleConsumerCreator function;
        private final int nbOfParameter;
        private final boolean shapeParticleHasToBeNonNull;
        private final boolean isBasic;

        getterParticleCreator(ParticleConsumerCreator function, int nbOfParameter, boolean shapeParticleHasToBeNonNull, boolean isBasic) {
            this.function = function;
            this.nbOfParameter = nbOfParameter;
            this.shapeParticleHasToBeNonNull = shapeParticleHasToBeNonNull;
            this.isBasic = isBasic;
        }
    }

    private static ShapeParticle create(Player p, ShapeParticle shapeParticle, String key, String[] options){
        key = key.toUpperCase();
        if(!has(key)){
            logMessage(key + " could not be found");
            return null;
        }
        getterParticleCreator gpc = getterParticleCreator.valueOf(key);
        if(gpc.equals(getterParticleCreator.MASK_PLAYERFOLLOW) && p != null){
            return gpc.function.create(new String[]{p.getName()}, shapeParticle);
        }
        String[] optionsCreator;
        if(options == null || options.length < gpc.nbOfParameter)
            optionsCreator = new String[gpc.nbOfParameter];
        else
            optionsCreator = options;

        if(gpc.shapeParticleHasToBeNonNull && shapeParticle == null){
            logMessage(PARTICLE_ISNULL);
            return null;
        }
        if(isBasic(key) && shapeParticle != null)
            return new BasicComposite(shapeParticle, gpc.function.create(optionsCreator, shapeParticle));
        return gpc.function.create(optionsCreator, shapeParticle);
    }
    private static ShapeParticle create(Player p, String key, String[] options){
        return create(p, null, key, options);
    }
    private static ShapeParticle create(String key, String[] options){
        return create(null, null, key, options);
    }

    private static boolean isBasic(String key){
        return has(key) && getterParticleCreator.valueOf(key).isBasic;
    }
    private static boolean has(String key){
        String upperKey = key.toUpperCase();
        try{
            getterParticleCreator.valueOf(upperKey);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    public final static String PREFIX = "ParticleCreator";
    private final static String SEPARATOR = ";";

    public static ShapeParticle createParticle(String name,
                                       Player p, ParticleCreator.keyOption[] shapeParticle, boolean logger){
        ShapeParticle sp = null;
        for (ParticleCreator.keyOption keyOption : shapeParticle){
            sp = create(p, sp, keyOption.key, keyOption.option != null ? keyOption.option.replace(" ","").split(SEPARATOR) : null);
            if(logger && sp == null){
                logMessage("   - Step(ShapeParticle): " + keyOption.key + " 'something went wrong' ");
                return null;
            }
        }
        return sp;
    }
    public static ShapeParticle createParticle(String name,
                                               Player p, ParticleCreator.keyOption[] shapeParticle){
        return createParticle(name, p, shapeParticle, false);
    }

    public static void logMessage(String s){
        Utils.log_print(PREFIX + " >> " + s);
    }

    @FunctionalInterface
    public interface ParticleConsumerCreator {
        ShapeParticle create(String[] options, ShapeParticle shapeParticle);
    }



    private final static ParticleConsumerCreator SPOINT = (options, s) -> new PointParticle();
    private final static ParticleConsumerCreator SCIRCLE = (options, s) -> {
        if(Utils.isInteger(options[0]) && Utils.isFloat(options[1]))
            return new CircleParticle(Integer.parseInt(options[0]), Float.parseFloat(options[1]));

        logMessage("Circles parameters are not valid");
        return null;
    };
    private final static ParticleConsumerCreator SLINE = (options, s) -> {
        if(Utils.isInteger(options[0]) && Utils.isFloat(options[1]))
            return new LineParticle(Integer.parseInt(options[0]), Float.parseFloat(options[1]));

        logMessage("Line parameters are not valid");
        return null;
    };
    private final static ParticleConsumerCreator SSQUARE = (options, s) -> {
        if(Utils.isInteger(options[0]) && Utils.isFloat(options[1]))
            return new SquareParticle(Integer.parseInt(options[0]), Float.parseFloat(options[1]));

        logMessage("Square parameters are not valid");
        return null;
    };



    private final static ParticleConsumerCreator XROTATE = (options, s) -> {
        if(Utils.isInteger(options[0])){
            if(options[1].equalsIgnoreCase("constant"))
                return new RotatedXAnimatedShape(Float.parseFloat(options[0]), s);
            else
                return new RotatedXAnimatedShape(Integer.parseInt(options[0]), s, options[1].equalsIgnoreCase("true"));
        }


        logMessage("X Rotate parameters are not valid");
        return null;
    };
    private final static ParticleConsumerCreator YROTATE = (options, s) -> {
        if(Utils.isInteger(options[0])){
            if(options[1].equalsIgnoreCase("constant"))
                return new RotatedYAnimatedShape(Float.parseFloat(options[0]), s);
            else
                return new RotatedYAnimatedShape(Integer.parseInt(options[0]), s, options[1].equalsIgnoreCase("true"));
        }

        logMessage("Y Rotate parameters are not valid");
        return null;
    };
    private final static ParticleConsumerCreator ZROTATE = (options, s) -> {
        if(Utils.isInteger(options[0])){
            if(options[1].equalsIgnoreCase("constant"))
                return new RotatedZAnimatedShape(Float.parseFloat(options[0]), s);
            else
                return new RotatedZAnimatedShape(Integer.parseInt(options[0]), s, options[1].equalsIgnoreCase("true"));
        }
        logMessage("Z Rotate parameters are not valid");
        return null;
    };
    //Option have to be the name of the player
    private final static ParticleConsumerCreator MPLAYERFOLLOW = (options, s) -> new RotatedPlayerShape(s, Bukkit.getPlayer(options[0]));
    private final static ParticleConsumerCreator TRANSLATION = (options, s) -> {
        if(!Utils.isFloat(options)){
            logMessage("One or multiple options are not number");
            return null;
        }
        return new TranslationConstantShape(s, Float.parseFloat(options[0]), Float.parseFloat(options[1]), Float.parseFloat(options[2]));
    };

}
