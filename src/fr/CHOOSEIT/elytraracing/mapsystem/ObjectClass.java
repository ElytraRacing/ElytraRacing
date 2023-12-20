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

import fr.CHOOSEIT.elytraracing.CustomMessageConfig;
import fr.CHOOSEIT.elytraracing.Main;
import fr.CHOOSEIT.elytraracing.mapsystem.objectAnimation.ObjectAnimationDeltaValue;
import fr.CHOOSEIT.elytraracing.parserClassSimple.SimpleLocation;
import fr.CHOOSEIT.elytraracing.utils.Utils;
import fr.CHOOSEIT.elytraracing.api.elytraracing.viewer.ERObject;
import fr.CHOOSEIT.elytraracing.mapsystem.shapes.IShape;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class ObjectClass implements ERObject {
    public ObjectClass(float x, float y, float z, String world, int particle_amount, float x_degrees, float y_degrees, float z_degrees) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.x_degrees = x_degrees;
        this.y_degrees = y_degrees;
        this.z_degrees = z_degrees;
        this.particle_amount = particle_amount;
    }

    public Location getMiddleLocation() {
        float[] middle = getShape().CreatePointWithAngle(this, 0, 0);
        World w = Bukkit.getWorld(getWorld());
        if (w != null) {
            return new Location(w, middle[0], middle[1], middle[2]);
        }
        return null;
    }

    public void show(Player p, String enumParticle, String enumParticle_ifnext, boolean isnext, long deltaTime) {
        ArrayList<Player> arr = new ArrayList<>();
        arr.add(p);
        show(arr, enumParticle, enumParticle_ifnext, isnext, deltaTime);
    }

    public int getID(ArrayList<? extends ObjectClass> list) {
        return list.indexOf(this);
    }

    public String getEnumParticle() {
        return enumParticle;
    }

    public static String particleAsString(String s) {
        if (s == null) {
            return "DEFAULT";
        }
        return s;
    }

    public String getEnumParticle_IFNEXT() {
        return enumParticle_IFNEXT;
    }

    public abstract void ReloadShow();
    public abstract boolean Through(Location last, Location loc, long deltaTime);

    public void show(ArrayList<Player> arrplayer, String enumParticle_default, String enumParticle_default_ifnext, boolean isnext, long deltaTime) {
        if ((((enumParticle_default != null && enumParticle_default.equals("INVISIBLE")) || (enumParticle != null && enumParticle.equals("INVISIBLE"))) && !isnext) || (((enumParticle_default_ifnext != null && enumParticle_default_ifnext.equals("INVISIBLE")) || (enumParticle_IFNEXT != null && enumParticle_IFNEXT.equals("INVISIBLE"))) && isnext)) {
            return;
        }
        Particle particle = availableParticleGetter(enumParticle_default, enumParticle, isnext, enumParticle_IFNEXT, enumParticle_default_ifnext);
        if (particle == null) {
            return;
        }
        String[] options = availableParticleGetterOptionSplitter(enumParticle_default, enumParticle, isnext, enumParticle_IFNEXT, enumParticle_default_ifnext);

        int amount = particle_amount - 1;
        if (amount < 0) {
            amount = 0;
        }
        if (!RenderObjectCache.isCreated(this)) {
            ReloadShow();
        }
        ArrayList<SimpleLocation> arrayList = RenderObjectCache.Load(this);
        for (SimpleLocation simpleLocation : arrayList) {
            for (Player player : arrplayer) {
                if (Utils.distance(player.getLocation(), getLocation()) > Main.cmc().PARTICLE_VIEW_DISTANCE) {
                    continue;
                }
                double deltaX = xCoordAnimation != null && xCoordAnimation.isValid() ? xCoordAnimation.getDeltaValue(deltaTime) : 0;
                double deltaY = yCoordAnimation != null && xCoordAnimation.isValid() ? yCoordAnimation.getDeltaValue(deltaTime) : 0;
                double deltaZ = zCoordAnimation != null && xCoordAnimation.isValid() ? zCoordAnimation.getDeltaValue(deltaTime) : 0;
                if(options.length <= 2)
                    player.spawnParticle(particle, simpleLocation.getX() + deltaX, simpleLocation.getY() + deltaY, simpleLocation.getZ() + deltaZ, amount, 0f, 0f, 0f, 0, null);
                else if(Utils.isInteger(options))
                    Utils.sendColoredParticle(player, particle.toString(), (float) (simpleLocation.getX() + deltaX),  (float) (simpleLocation.getY() + deltaY),  (float) (simpleLocation.getZ() + deltaZ),  (float) ((Integer.parseInt(options[0]) % 256)) / 255.0, (float) ((Integer.parseInt(options[1]) % 256)) / 255.0 , (float) ((Integer.parseInt(options[2]) % 256)) / 255.0, amount);
            }
        }
    }

    public void serializationVerifications(){
        if(xCoordAnimation == null) xCoordAnimation = new ObjectAnimationDeltaValue(0, 0);
        if(yCoordAnimation == null) yCoordAnimation = new ObjectAnimationDeltaValue(0, 0);
        if(zCoordAnimation == null) zCoordAnimation = new ObjectAnimationDeltaValue(0, 0);
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public void Delete() {
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void setY_degrees(float y_degrees) {
        this.y_degrees = y_degrees;
        ReloadShow();
    }

    public void setX_degrees(float x_degrees) {
        this.x_degrees = x_degrees;
        ReloadShow();
    }

    public void setZ_degrees(float z_degrees) {
        this.z_degrees = z_degrees;
        ReloadShow();
    }

    public float getXDegrees() {
        return x_degrees;
    }

    public float getYDegrees() {
        return y_degrees;
    }

    public float getZDegrees() {
        return z_degrees;
    }

    public float getX() {
        return x;
    }
    public float getDeltaX(long deltatime){
        return xCoordAnimation != null ? (float) xCoordAnimation.getDeltaValue(deltatime) : 0;
    }
    public float getX(long deltatime){
        return (float) (x + getDeltaX(deltatime));
    }

    public float getY() {
        return y;
    }
    public float getDeltaY(long deltatime){
        return yCoordAnimation != null ? (float) yCoordAnimation.getDeltaValue(deltatime) : 0;
    }
    public float getY(long deltatime){
        return (float) (y + getDeltaY(deltatime));
    }

    public float getZ() {
        return z;
    }
    public float getDeltaZ(long deltatime){
        return zCoordAnimation != null ? (float) zCoordAnimation.getDeltaValue(deltatime) : 0;
    }
    public float getZ(long deltatime){
        return (float) (z + getDeltaZ(deltatime));
    }

    public void setRadius_bold(int particle_amount) {
        this.particle_amount = particle_amount;
        ReloadShow();
    }

    public int getParticleAmount() {
        return particle_amount;
    }

    public void setLocation(Location location) {
        this.x = (float) location.getX();
        this.y = (float) location.getY();
        this.z = (float) location.getZ();
        this.world = location.getWorld().getName();
        ReloadShow();
    }


    public String getWorld() {
        return world;
    }

    public float[] getCoord() {
        return new float[]{x, y, z};
    }

    public float[] getCoordWithDelta(long deltaTime){
        return Utils.add(getCoord(),  xCoordAnimation != null ? (float)xCoordAnimation.getDeltaValue(deltaTime) : 0, yCoordAnimation != null ? (float)yCoordAnimation.getDeltaValue(deltaTime) : 0, zCoordAnimation != null ? (float) zCoordAnimation.getDeltaValue(deltaTime) : 0);
    }

    public float[] rotateX(double angle_radians, float x, float y, float z) {
        return Utils.rotateX(angle_radians, x, y, z);
    }

    public float[] rotateY(double angle_radians, float x, float y, float z) {
        return Utils.rotateY(angle_radians, x, y, z);
    }

    public float[] rotateZ(double angle_radians, float x, float y, float z) {
        return Utils.rotateZ(angle_radians, x, y, z);
    }

    public static class RenderObjectCache {
        private static HashMap<ObjectClass, ArrayList<SimpleLocation>> CacheObject = new HashMap<>();

        public static void Save(ObjectClass objectClass, ArrayList<SimpleLocation> arrayList) {
            CacheObject.put(objectClass, arrayList);
        }

        public static boolean isCreated(ObjectClass objectClass) {
            return CacheObject.containsKey(objectClass);
        }

        public static ArrayList<SimpleLocation> Load(ObjectClass objectClass) {
            if (CacheObject.containsKey(objectClass)) {
                return CacheObject.get(objectClass);
            }
            return null;
        }
    }

    public static String[] availableParticleGetterOptionSplitter(String defaultp, String particleg, boolean isnext, String particleisnext, String particleisnextdefault) {
        if (defaultp.equalsIgnoreCase("INVISIBLE")) {
            return new String[0];
        }
        String[] r = defaultp.split(";");
        Particle particle = Particle.valueOf(r[0]);
        if (particleg != null) {
            if (!particleg.equalsIgnoreCase("INVISIBLE")) {
                r = particleg.split(";");
                particle = Particle.valueOf(r[0]);

            }

        }
        if (isnext) {
            if (particleisnext != null) {
                if (particleisnext.equalsIgnoreCase("INVISIBLE")) {
                    return null;
                }
                r = particleisnext.split(";");
                particle = Particle.valueOf(r[0]);
            } else {
                if (particleisnextdefault != null) {
                    if (particleisnextdefault.equalsIgnoreCase("INVISIBLE")) {
                        return null;
                    }
                    r = particleisnextdefault.split(";");
                    particle = Particle.valueOf(r[0]);
                }

            }
        }
        if(r.length > 1)
            return Arrays.copyOfRange(r, 1, r.length);
        return new String[0];
    }
    public static Particle availableParticleGetter(String defaultp, String particleg, boolean isnext, String particleisnext, String particleisnextdefault) {
        if (defaultp.equalsIgnoreCase("INVISIBLE")) {
            return null;
        }
        Particle particle = Particle.valueOf(defaultp.split(";")[0]);
        if (particleg != null) {
            if (!particleg.equalsIgnoreCase("INVISIBLE")) {
                particle = Particle.valueOf(particleg.split(";")[0]);
            }

        }
        if (isnext) {
            if (particleisnext != null) {
                if (particleisnext.equalsIgnoreCase("INVISIBLE")) {
                    return null;
                }
                particle = Particle.valueOf(particleisnext.split(";")[0]);
            } else {
                if (particleisnextdefault != null) {
                    if (particleisnextdefault.equalsIgnoreCase("INVISIBLE")) {
                        return null;
                    }
                    particle = Particle.valueOf(particleisnextdefault.split(";")[0]);
                }

            }
        }
        return particle;
    }

    public IShape getShape() {
        return IShape.getShape(shape);
    }

    public String getShapeStr() {
        return IShape.getShape(shape).getClass().getSimpleName();
    }

    public void setShape(String shape) {
        this.shape = shape;
        ReloadShow();
    }

    public ArrayList<Integer> getLineHelperto() {
        if (lineHelperto == null) {
            lineHelperto = new ArrayList<>();
        }
        return lineHelperto;
    }

    public void setLineHelperto(ArrayList<Integer> lineHelperto) {
        this.lineHelperto = lineHelperto;
    }

    public void setParticle_amount(int particle_amount) {
        this.particle_amount = particle_amount;
    }

    public void setEnumParticle(String enumParticle) {
        this.enumParticle = enumParticle;
    }

    public void setEnumParticle_IFNEXT(String enumParticle_IFNEXT) {
        this.enumParticle_IFNEXT = enumParticle_IFNEXT;
    }

    public static CustomMessageConfig cmc = Main.customMessageConfig;

    public abstract void setID_JFI(Map map);

    protected int ID_justforindication;
    protected String enumParticle = null;
    protected String enumParticle_IFNEXT = null;
    protected String world;
    protected int particle_amount;
    protected float x, y, z, x_degrees, y_degrees, z_degrees;
    private String shape;
    private ArrayList<Integer> lineHelperto;
    protected ObjectAnimationDeltaValue xCoordAnimation, yCoordAnimation, zCoordAnimation;
}
