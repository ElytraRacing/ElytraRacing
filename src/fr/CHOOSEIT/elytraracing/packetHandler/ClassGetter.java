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

public enum ClassGetter {
    Packet("net.minecraft.network.protocol.Packet", "Packet"),
    ClientboundSetTitleTextPacket("net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket", ""),
    ClientboundSetSubtitleTextPacket("net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket", ""),
    ClientboundSetTitlesAnimationPacket("net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket", ""),
    DataWatcher("net.minecraft.network.syncher.DataWatcher", "DataWatcher"),
    DataWatcherObject("net.minecraft.network.syncher.DataWatcherObject", "DataWatcherObject"),
    DataWatcherRegistry("net.minecraft.network.syncher.DataWatcherRegistry", "DataWatcherRegistry"),
    DataWatcherSerializer("net.minecraft.network.syncher.DataWatcherSerializer", "DataWatcherSerializer"),
    IChatBaseComponent("net.minecraft.network.chat.IChatBaseComponent", "IChatBaseComponent"),
    PacketPlayOutEntityMetadata("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata", "PacketPlayOutEntityMetadata"),
    PacketPlayOutEntityDestroy("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy", "PacketPlayOutEntityDestroy"),
    PacketPlayOutChat("net.minecraft.network.protocol.game.PacketPlayOutChat", "PacketPlayOutChat"),
    ChatMessageType("net.minecraft.network.chat.ChatMessageType", "ChatMessageType"),
    PacketPlayOutPlayerListHeaderFooter("net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter", "PacketPlayOutPlayerListHeaderFooter"),
    EntityPlayer("net.minecraft.server.level.EntityPlayer", "EntityPlayer"),
    NONCEDL("%%__USER__%%", "%%__NONCE__%%"),
    WorldServer("net.minecraft.server.level.WorldServer", "WorldServer"),
    MinecraftServer("net.minecraft.server.MinecraftServer", "MinecraftServer"),
    PlayerInteractManager("net.minecraft.server.level.PlayerInteractManager", "PlayerInteractManager"),
    PacketPlayOutPlayerInfo("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo", "PacketPlayOutPlayerInfo"),
    PacketPlayOutNamedEntitySpawn("net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn", "PacketPlayOutNamedEntitySpawn"),
    EntityHuman("net.minecraft.world.entity.player.EntityHuman", "EntityHuman"),
    EnumItemSlot("net.minecraft.world.entity.EnumItemSlot", "EnumItemSlot"),
    ItemStack("net.minecraft.world.item.ItemStack", "ItemStack"),
    PacketPlayOutEntityEquipment("net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment", "PacketPlayOutEntityEquipment"),
    ScoreboardTeam("net.minecraft.world.scores.ScoreboardTeam", "ScoreboardTeam"),
    Scoreboard("net.minecraft.world.scores.Scoreboard", "Scoreboard"),
    ScoreboardTeamBase("net.minecraft.world.scores.ScoreboardTeamBase", "ScoreboardTeamBase"),
    PacketPlayOutScoreboardTeam("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam", "PacketPlayOutScoreboardTeam"),
    PacketPlayOutEntityTeleport("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport", "PacketPlayOutEntityTeleport"),
    PacketPlayOutEntityVelocity("net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity", "PacketPlayOutEntityVelocity"),
    Entity("net.minecraft.world.entity.Entity", "Entity"),
    Vec3D("net.minecraft.world.phys.Vec3D", "Vec3D"),
    PacketPlayOutScoreboardScore("net.minecraft.network.protocol.game.", "PacketPlayOutScoreboardScore"),
    PacketPlayOutScoreboardObjective("net.minecraft.network.protocol.game.", "PacketPlayOutScoreboardObjective"),
    IScoreboardCriteria("net.minecraft.world.scores.criteria.", "IScoreboardCriteria"),
    PacketPlayOutScoreboardDisplayObjective("net.minecraft.network.protocol.game.", "PacketPlayOutScoreboardDisplayObjective"),
    ScoreboardObjective("net.minecraft.world.scores.", "");


    private String newversion;
    private String previousVersion;

    ClassGetter(String newversion, String previousVersion) {
        this.newversion = newversion;
        this.previousVersion = previousVersion;
    }


    public Class<?> getClassRight() {
        if (Main.getVersion() >= 17) {
            try {
                if (newversion.endsWith(".")) {
                    return Class.forName(newversion + this.name());
                }
                return Class.forName(newversion);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (previousVersion.isEmpty()) {
                return Main.getNMSClass(this.name().toString());
            }
            return Main.getNMSClass(previousVersion);
        }
        return null;
    }
}
