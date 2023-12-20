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
import fr.CHOOSEIT.elytraracing.addon.particle.colorCreator.basicColor.ConstantColor;
import fr.CHOOSEIT.elytraracing.addon.particle.colorCreator.basicColor.animated.HSVGradientColor;
import fr.CHOOSEIT.elytraracing.addon.particle.colorCreator.basicColor.animated.RainbowColor;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import org.bukkit.entity.Player;

public class ColorCreatorOptionList
{
    private enum getterColorCreator {

        RAINBOW(ColorCreatorOptionList.RAINBOW, 2),
        CONSTANT(ColorCreatorOptionList.CONSTANT, 3),
        HSV_GRADIENT(ColorCreatorOptionList.HSVGRADIENT, 5);

        private final ColorConsumerCreator function;
        private final int nbOfParameter;

        getterColorCreator(ColorConsumerCreator function, int nbOfParameter) {
            this.function = function;
            this.nbOfParameter = nbOfParameter;
        }
    }

    private static ColorCreator create(Player p, ColorCreator colorCreator, String key, String[] options){
        key = key.toUpperCase();
        if(!has(key)) return null;
        getterColorCreator gpc = getterColorCreator.valueOf(key);
        String[] optionsCreator;
        if(options == null || options.length < gpc.nbOfParameter)
            optionsCreator = new String[gpc.nbOfParameter];
        else
            optionsCreator = options;

        return gpc.function.create(optionsCreator, colorCreator);
    }
    private static ColorCreator create(Player p, String key, String[] options){
        return create(p, null, key, options);
    }
    private static ColorCreator create(String key, String[] options){
        return create(null, null, key, options);
    }

    private static boolean has(String key){
        String upperKey = key.toUpperCase();
        try{
            getterColorCreator.valueOf(upperKey);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    private final static String PREFIX = ParticleCreatorOptionList.PREFIX;
    private final static String SEPARATOR = ";";

    public static ColorCreator createColor(String name,
                                       Player p, ParticleCreator.keyOption[] colorCreator, boolean logger){
        ColorCreator cc = null;
        for (ParticleCreator.keyOption keyOption : colorCreator){
            cc = create(p, cc, keyOption.key, keyOption.option != null ? keyOption.option.replace(" ","").split(SEPARATOR) : null);
            if(logger && cc == null){
                logMessage("   - Step(ColorCreator): " + keyOption.key + " 'something went wrong' ");
                return null;
            }
        }
        return cc;
    }
    public static ColorCreator createColor(String name,
                                           Player p, ParticleCreator.keyOption[] colorCreator){
        return createColor(name, p, colorCreator, false);
    }

    public static void logMessage(String s){
        Utils.log_print(PREFIX + " >> " + s);
    }

    @FunctionalInterface
    public interface ColorConsumerCreator {
        ColorCreator create(String[] options, ColorCreator colorCreator);
    }



    private final static ColorConsumerCreator HSVGRADIENT = (options, s) ->{
        // (Duration;H;H;addingAtStart;BounceBack)
        if(Utils.isInteger(options[0])
                && Utils.isInteger(options[1])
                && Utils.isInteger(options[2])){

            int duration = Integer.parseInt(options[0]);
            float H1 = (float) ((Integer.parseInt(options[1]) % 360) / 360.0);
            float H2 = (float) ((Integer.parseInt(options[2]) % 360 )/ 360.0);
            boolean adding = options[3].equalsIgnoreCase("true");
            boolean bounceback = options[4].equalsIgnoreCase("true");

            return new HSVGradientColor(duration, H1, H2, adding, bounceback);
        }

        logMessage("Gradient parameters are not valid");
        return null;
    };
    private final static ColorConsumerCreator RAINBOW = (options, s) ->{
        if(Utils.isInteger(options[0]))
            return new RainbowColor(Integer.parseInt(options[0]), options[1].equalsIgnoreCase("true"));

        logMessage("Rainbow parameters are not valid");
        return null;
    };
    private final static ColorConsumerCreator CONSTANT = (options, s) ->{
        if(Utils.isInteger(options))
            return new ConstantColor(new RGBConstant((Math.abs(Integer.parseInt(options[0])) % 256)/255.0f, (Math.abs(Integer.parseInt(options[1])) % 256)/255.0f, (Math.abs(Integer.parseInt(options[2])) % 256)/255.0f));

        logMessage("Constant parameters are not valid");
        return null;
    };
}
