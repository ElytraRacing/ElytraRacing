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

import fr.CHOOSEIT.elytraracing.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class PacketHandler {
    private static Method sendPacketMethodSaver;

    public static void sendPacket(Object packet, Player player) {
        sendPacket(player, Arrays.asList(packet));
    }

    public static void sendPacket(Object packet, ArrayList<UUID> playersuuid) {
        Player p;
        for (UUID player : playersuuid) {
            p = Bukkit.getPlayer(player);
            if (p != null) {
                sendPacket(packet, p);
            }
        }
    }

    public static void sendPacket(Object packet, UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            sendPacket(packet, p);
        }

    }

    public static Method getSendPacketMethod(Class playerConnection, Class packetClass) {
        if (sendPacketMethodSaver == null) {
            Method[] ms = playerConnection.getMethods();
            for (Method m : ms) {
                if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == packetClass) {
                    sendPacketMethodSaver = m;
                }
            }
        }
        return sendPacketMethodSaver;
    }

    public static void sendPacket(Player player, List<Object> packets) {
        try {
            Object handle = getHandle(player);
            Object playerConnection_o = getPlayerConnection(handle);
            for (Object packet : packets) {
                getSendPacketMethod(playerConnection_o.getClass(), ClassGetter.Packet.getClassRight()).invoke(playerConnection_o, packet);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    public static Object getHandle(Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (Main.getVersion() >= 17) {
            return player.getClass().getMethod("getHandle").invoke(player);
        } else {
            return player.getClass().getMethod("getHandle").invoke(player);
        }
    }
    public static Object getPlayerConnection(Object handle) throws NoSuchFieldException, IllegalAccessException {
        if (Main.getVersion() >= 20){
            return handle.getClass().getField("c").get(handle);
        } else if (Main.getVersion() >= 17) {
            return handle.getClass().getField("b").get(handle);
        } else {
            return handle.getClass().getField("playerConnection").get(handle);
        }
    }

    public static void sendPacketMessage(Player player, String base) {
        Class<?> IChatBaseComponent_class = ClassGetter.IChatBaseComponent.getClassRight();
        Class<?> ChatSerializer_o = IChatBaseComponent_class.getDeclaredClasses()[0];

        try {
            Object comp = ChatSerializer_o.getMethod("a", String.class).invoke(ChatSerializer_o, base);
            Object chat;
            Class<?> PacketPlayOutChat_class = null;
            if (Main.instance.getVersion() >= 19) {
                PacketPlayOutChat_class = Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket");
            }
            else{
                PacketPlayOutChat_class = ClassGetter.PacketPlayOutChat.getClassRight();
            }

            Constructor<?> PacketPlayOutChat_Constructor;
            if (Main.instance.getVersion() >= 19) {
                PacketPlayOutChat_Constructor = PacketPlayOutChat_class.getConstructor(IChatBaseComponent_class, boolean.class);
                chat = PacketPlayOutChat_Constructor.newInstance(comp, true);
            }
            else if (Main.instance.getVersion() >= 17) {
                Class<?> ChatMessageType_class = ClassGetter.ChatMessageType.getClassRight();
                PacketPlayOutChat_Constructor = PacketPlayOutChat_class.getConstructor(IChatBaseComponent_class, ChatMessageType_class, UUID.class);
                chat = PacketPlayOutChat_Constructor.newInstance(comp, ChatMessageType_class.getField("a").get(null), UUID.randomUUID());
            } else if (Main.instance.getVersion() >= 16) {
                Class<?> ChatMessageType_class = ClassGetter.ChatMessageType.getClassRight();
                PacketPlayOutChat_Constructor = PacketPlayOutChat_class.getConstructor(IChatBaseComponent_class, ChatMessageType_class, UUID.class);
                chat = PacketPlayOutChat_Constructor.newInstance(comp, ChatMessageType_class.getField("CHAT").get(null), UUID.randomUUID());
            } else {
                PacketPlayOutChat_Constructor = PacketPlayOutChat_class.getConstructor(IChatBaseComponent_class);
                chat = PacketPlayOutChat_Constructor.newInstance(comp);
            }
            PacketHandler.sendPacket(chat, player);


        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
