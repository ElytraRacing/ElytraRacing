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

package fr.CHOOSEIT.elytraracing.packetHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassHelper {
    public static void setField(Object o, String variablename, Object value) {
        try {
            Field f = o.getClass().getDeclaredField(variablename);
            f.setAccessible(true);
            f.set(o, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Method getDeclaredMethod(Class<?> classused, String name, int next) {
        for (Method declaredMethod : classused.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(name)) {

                if (next == 1) {
                    return declaredMethod;
                }
                next--;
            }
        }
        return null;
    }

    public static Method getDeclaredMethod(Class<?> classused, String name, int next, int argumentNumber) {
        for (Method declaredMethod : classused.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(name)) {
                if (declaredMethod.getParameterCount() == argumentNumber) {
                    if (next == 1) {
                        return declaredMethod;
                    }
                }
                next--;
            }
        }
        return null;
    }

    public static Constructor getConstructorByArgs(Class<?> classused, int numberArg) {
        for (Constructor conss : classused.getDeclaredConstructors()) {
            if (conss.getParameters().length == numberArg) {
                conss.setAccessible(true);
                return conss;
            }
        }
        return null;
    }

    public static Class getDeclaredClass(Class<?> classused, String name) {
        for (Class dc : classused.getDeclaredClasses()) {
            if (dc.getSimpleName().equals(name)) {
                return dc;
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> classused, String name, int next) {
        for (Method method : classused.getMethods()) {
            if (method.getName().equals(name)) {
                if (next == 1) {
                    return method;
                }
                next--;
            }
        }
        return null;
    }
}
