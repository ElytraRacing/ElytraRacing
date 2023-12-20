package fr.CHOOSEIT.elytraracing.GUI;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.CHOOSEIT.elytraracing.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HeadDatabase {
    public static ItemStack TRUE = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=");
    public static ItemStack FALSE = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==");

    public static ItemStack STAR = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIxNmVlNDA1OTNjMDk4MWVkMjhmNWJkNjc0ODc5NzgxYzQyNWNlMDg0MWI2ODc0ODFjNGY3MTE4YmI1YzNiMSJ9fX0=");

    public static ItemStack head(String url, String name) {
        org.bukkit.inventory.ItemStack head = new ItemStack(VisibleObject.getRenderItem("PLAYER_HEAD", Arrays.asList("SKULL_ITEM:3", "SKULL:3"), ""));
        try {

            SkullMeta hm = (SkullMeta) head.getItemMeta();
            assert hm != null;
            GameProfile pro = null;
            if ((Main.getVersion() >= 20 &&  Main.getSubVersion() >= 2) || Main.getVersion() > 20){
                pro = new GameProfile(UUID.randomUUID(), name);
            } else {
                pro = new GameProfile(UUID.randomUUID(), null);
            }

            pro.getProperties().put("textures", new Property("textures", url));
            Field profi = hm.getClass().getDeclaredField("profile");
            profi.setAccessible(true);
            profi.set(hm, pro);
            if (name != null) {
                hm.setDisplayName(name);
            }
            head.setItemMeta(hm);
        } catch (NoSuchFieldException | IllegalAccessException e) {

        }


        return head;
    }

    public static ItemStack head(String url) {
        return head(url, " ");
    }

    public static ItemStack getCustomHead(ItemStack head, String name, List<String> lore) {
        ItemMeta im = head.getItemMeta();
        if (name != null && im != null) {
            im.setDisplayName(name);
        }
        if (lore != null && im != null) {
            im.setLore(lore);
        }
        head.setItemMeta(im);

        return head;
    }

    public static ItemStack getNumber(String TYPE, int n) {
        try {
            return (ItemStack) HeadDatabase.class.getField(TYPE + "_" + n).get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack WHITE_0 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2YwOTAxOGY0NmYzNDllNTUzNDQ2OTQ2YTM4NjQ5ZmNmY2Y5ZmRmZDYyOTE2YWVjMzNlYmNhOTZiYjIxYjUifX19");
    public static ItemStack WHITE_1 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E1MTZmYmFlMTYwNThmMjUxYWVmOWE2OGQzMDc4NTQ5ZjQ4ZjZkNWI2ODNmMTljZjVhMTc0NTIxN2Q3MmNjIn19fQ==");
    public static ItemStack WHITE_2 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY5OGFkZDM5Y2Y5ZTRlYTkyZDQyZmFkZWZkZWMzYmU4YTdkYWZhMTFmYjM1OWRlNzUyZTlmNTRhZWNlZGM5YSJ9fX0=");
    public static ItemStack WHITE_3 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQ5ZTRjZDVlMWI5ZjNjOGQ2Y2E1YTFiZjQ1ZDg2ZWRkMWQ1MWU1MzVkYmY4NTVmZTlkMmY1ZDRjZmZjZDIifX19");
    public static ItemStack WHITE_4 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJhM2Q1Mzg5ODE0MWM1OGQ1YWNiY2ZjODc0NjlhODdkNDhjNWMxZmM4MmZiNGU3MmY3MDE1YTM2NDgwNTgifX19");
    public static ItemStack WHITE_5 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDFmZTM2YzQxMDQyNDdjODdlYmZkMzU4YWU2Y2E3ODA5YjYxYWZmZDYyNDVmYTk4NDA2OTI3NWQxY2JhNzYzIn19fQ==");
    public static ItemStack WHITE_6 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2FiNGRhMjM1OGI3YjBlODk4MGQwM2JkYjY0Mzk5ZWZiNDQxODc2M2FhZjg5YWZiMDQzNDUzNTYzN2YwYTEifX19");
    public static ItemStack WHITE_7 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk3NzEyYmEzMjQ5NmM5ZTgyYjIwY2M3ZDE2ZTE2OGIwMzViNmY4OWYzZGYwMTQzMjRlNGQ3YzM2NWRiM2ZiIn19fQ==");
    public static ItemStack WHITE_8 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJjMGZkYTlmYTFkOTg0N2EzYjE0NjQ1NGFkNjczN2FkMWJlNDhiZGFhOTQzMjQ0MjZlY2EwOTE4NTEyZCJ9fX0=");
    public static ItemStack WHITE_9 = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZhYmM2MWRjYWVmYmQ1MmQ5Njg5YzA2OTdjMjRjN2VjNGJjMWFmYjU2YjhiMzc1NWU2MTU0YjI0YTVkOGJhIn19fQ==");
    public static ItemStack WHITE_SEPARATOR = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZmY2MzMThjMjEyZGM3NDliNTk5NzU1ZTc2OTdkNDkyMzgyOTkzYzA3ZGUzZjhlNTRmZThmYzdkZGQxZSJ9fX0=");


    public static ItemStack RED_LEFT_ARROW = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=");
    public static ItemStack BLUE_RIGHT_ARROW = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE3ZjM2NjZkM2NlZGZhZTU3Nzc4Yzc4MjMwZDQ4MGM3MTlmZDVmNjVmZmEyYWQzMjU1Mzg1ZTQzM2I4NmUifX19=");
    public static ItemStack RED_E = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY0ZjI1MTc1MmM3MzE3YmIyZmI2MjhlNGMzN2M4MmM2MDcxOWQ5MDk5ODUxNDZiMjYxODMyMTUyYTMwYWRhMiJ9fX0=");
    public static ItemStack GREEN_C = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWFjMGRhOTQ5NGI5NzRiZGJjYjc4ODYxZGY5NmFhNWFhYTVkYTM1YzY1ODcyMTcxOGFiMjZkYzJmZjY3ZDg3In19fQ==");
    public static ItemStack ORANGE_P = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdkMzg4MWYyM2ZmOWZiMTRkYjY0ZTQxZmVmYmQ4NWU1MjU4Zjg4NGY5YTU1YWI2YTg2NWFhZTVkYTU1ZmNkYyJ9fX0=");
    public static ItemStack ORANGE_N = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM5ODE0ZTlkMTdmNzlmOWY4YmE2ZTFiOTExNWM1MWFlZTRhOWZlMzI5YWVlNGE1YTQ3NWJkZjM2NzhjM2IifX19");
    public static ItemStack ORANGE_O = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFjNTNmNzBiYTY0Y2ZjM2VjM2Y0MWZlNmY2Yzc0OWU2YmMxMzJhYzBmY2QxZGNjZDU1NGJjZjg5ZTVjZDIifX19");
    public static ItemStack GRAY_C = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5ZTQ4YzBkZjc5OTVlOTFkYjViZDNjOTMwZTViY2MwYWJjZmFmMzEyNzM3MzJhZWFiZjMzYzVkODY0OTEifX19");
    public static ItemStack CYAN_P = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzk5ZjFkOGU1ZTUzODM3ZWM1ZGI5MTg3ODEyYjFiYjFhNDg5OWFlN2JhYTE1YzFjNGZlNjNlNDYyNzFiNjEyIn19fQ==");
    public static ItemStack IRON_RIGHT_ARROW = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFlMjk0MjJkYjQwNDdlZmRiOWJhYzJjZGFlNWEwNzE5ZWI3NzJmY2NjODhhNjZkOTEyMzIwYjM0M2MzNDEifX19");
    public static ItemStack BLUE_P = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ4OWVlMTVjY2VkNGI1NDk5MzAyODJmY2JlYjBmZjRjMTI3NmY2MzAzOTZiYmE2YmE3YzAzZWRkOTBkNSJ9fX0==");
    public static ItemStack GREEN_M = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMxNzEzZDkwY2E1OTk4NzE2ZWFhN2RkMzAyZjZjOGQ2NzI1OTVkY2M2OTU2YzJlMjBlOTQxODRiYWUyIn19fQ==");
    public static ItemStack RED_R = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzVmNDVjODc1ZWZmNTM4ZmNlYzk4ZjZhY2MxZGYyYWVjYWUyOGY0ODYwYWVjZDI0ZTJkYmRmMTM5MjRiMzI3In19fQ==");
    public static ItemStack PURPLE_S = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThmNzkxNGU5OGQ5OGU5NGE3ZTNjZjk3MmNiNjZjNTNhNjZjZmEyNDMwNWQzNGIzMzNjYWU4MmRmZDRiZTQ1NSJ9fX0=");
    public static ItemStack YELLOW_G = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGEzOWRmODk4Y2JkZjBjYTc0MWFjNWI1NzU0MjE1MjQxOWM4ZjYwNzAzZDVjMzQ5YjhmZDBlODYxMTdiMmUxIn19fQ==");
    public static ItemStack RED_L = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQxNmU5NTgyYTQ4YTQzMGU4MjdlZjA5YjcxNTc4OWE3MmQ5ZmE1YjEyMjRhMzI2MWM0YmJlNWNiYmMyYyJ9fX0=");
    public static ItemStack GREEN_A = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBhZmQ3NzdkNTU3YTIwN2JhYzdhYWQ4NDIxZmRmNzg4ZDY2ODU4NzNjNDk1MTVkNTUyOTFlOTMwNjk5ZiJ9fX0=");
    public static ItemStack STONE_REFRESH = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk3ZDZkN2JlOTg1ZDA2MjJhNDhlOTA2OThlOTA3M2Y3ZmY4ODEzMjkyODEyZWJkMTczMGRiYTBlMDFjZjE4ZiJ9fX0=");
    public static ItemStack YELLOW_B = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI2OTRlZGU5NmIyMTg1MTE2MDIxNTNmNjY1Mzk4YmU3Yzk2NjljYWY2NzJjNGJjOGYzNWJjZDUxN2UyOGMzIn19fQ==");
    public static ItemStack PURPLE_L = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRkODA2NDMzZGY3NjkwOTc1ZjJhNTE2NTI1MjRiZTVhNWE0MmMzMmRjNzM4NDUxMjRjN2YxM2RlNDUifX19");
    public static ItemStack GRAY_A = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE3NGQ5MjEyZTY0OTFiYzM3MGNhZjAyNWZkNDU5ZmU2MzJjYTdjNmJhNGRmN2ViZWZhZmQ0ODlhYjMyZmQifX19");
    public static ItemStack GREEN_PLUS = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");
    public static ItemStack WHITE_LOCK = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZiMTkzMmM0MmNkN2FmNjIxYjhlNTJmZGY0OWE0YTdmYTZmNDgwOTViYjYwOGUwNTgwNTVhZjM4YjNmMWZjNCJ9fX0=");


    public static ItemStack ORANGE_X = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2ZlMWQyMjJlYjFhMzVkNzMzZmJkZTdjMTM0MmVmYTgyMTg5OGI1NmM3YzI4YTY5YjY4Nzg5MjBiNTExIn19fQ==");
    public static ItemStack ORANGE_Y = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY2MDQ4MWFhZTQ0NmYyMzYwNDYyMmMyNTEzM2JkMjJiOTliNTZhMWMyOGZjZGEyZDkxNTYwZWZkYWFkZWRhIn19fQ==");
    public static ItemStack ORANGE_Z = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzliZjZkYmI0N2Y5ODZlODI1ZGRiNTg4YjU1MTkyODNjMmYwMzg2MGZkYTgxODQ3ZDUxMWUxZDU5NjZlOGNmYSJ9fX0=");

    public static ItemStack BLUE_X = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVlMWJlMzMzNzRmZDdiMmJhZTljOGZjOTE0NmI2ZWQzZWVkY2IxNDc2YjNiN2I4MDEwZjVmNDRiZmE4NDNlIn19fQ==");
    public static ItemStack BLUE_Y = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk0ODNkMGQzZmYyMTI0OWRhOWU2ZjQwNWJiMGVhNzg4NjU1OGRmMWE4NGM5YWUzNjRlZjRlODY3NGViYTgifX19");
    public static ItemStack BLUE_Z = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJkMzU0OTI4ZDljNzI4Y2MzZDQ0NjFhYTI2NmZmZTRiYWNmZTQ0OTIxYWZlYWQyYjU5ZGM3Yzk4MWE5MzQ2In19fQ==");
    public static ItemStack MONITOR = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA5Y2RlMWFmYzk1YTQ3NGQyMjI1NTQwOTdlZDZkMzkxZTdjYzdhZTFmMjAyZmRiZmQyZDZkYmM5ODMwOTM3MCJ9fX0=");
    public static ItemStack GREEN_H = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE1OTU3NzMwNDhkZTljNDExODk2ZmFlOWJhOWQ0OGQ1NWI3MTY3NzE5OTcwMTA2NDk4NGI0MmNkMmExN2Q4In19fQ==");
    public static ItemStack RED_D = head("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ2OGFlMzE1MGQ4MWU0YzBhOWQxNzJiZDg0YzRmZjczY2RjMGI4N2ZlZThlYzY2MTIxMzQ2OGQ1NDQ0ODMifX19");
}

