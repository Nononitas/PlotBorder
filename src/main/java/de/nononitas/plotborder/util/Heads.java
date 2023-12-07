/*
 * Author: Nononitas
 * Discord: Nononitas#9256
 * Changes of the code are not allowed!
 */

package de.nononitas.plotborder.util;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public enum Heads {

    WHITE_ARROW_RIGHT("OTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==", "Rechts"),
    WHITE_ARROW_LEFT("Y2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19", "Links");

    private final ItemStack item;
    private final String idTag;
    private final String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";

    Heads(String texture, String id) {
        item = createSkull(prefix + texture, id);
        idTag = id;
    }

    private static ItemStack createSkull(final String URL, String name) {
        String url = URL;
        if (!url.contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv")) {
            url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + url;
        }
        if (isOldBase64()) {

            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), "null");
            profile.getProperties().put("textures", new Property("textures", url));
            try {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);

            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
                error.printStackTrace();
            }
            headMeta.setDisplayName(name);
            head.setItemMeta(headMeta);


            return head;
        } else {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();

            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            try {
                textures.setSkin(new URL(base64Convert(url)));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            profile.setTextures(textures);
            headMeta.setOwnerProfile(profile);
            head.setItemMeta(headMeta);
            return head;
        }

    }

    public ItemStack getItemStack() {
        return item;
    }

    public String getName() {
        return idTag;
    }

    private static String base64Convert(String base64Value) {
        String jsonString = new String(Base64.getDecoder().decode(base64Value));
        JSONObject obj = new JSONObject(jsonString);
        String output = obj.getJSONObject("textures").getJSONObject("SKIN").getString("url");
        if (output.startsWith("http://textures.minecraft.net/texture/")) {
            return output;
        } else return "http://textures.minecraft.net/texture/d5d20330da59c207d78352838e91a48ea1e42b45a9893226144b251fe9b9d535";
    }

    private static boolean isOldBase64() {
        String nms = Bukkit.getServer().getClass().getPackage().getName();
        if (nms.contains("1_8")) {
            return true;
        } else if (nms.contains("1_9")) {
            return true;
        } else if (nms.contains("1_10")) {
            return true;
        } else if (nms.contains("1_11")) {
            return true;
        } else if (nms.contains("1_12")) {
            return true;
        } else if (nms.contains("1_13")) {
            return true;
        } else if (nms.contains("1_14")) {
            return true;
        } else if (nms.contains("1_15")) {
            return true;
        } else if (nms.contains("1_16")) {
            return true;
        } else if (nms.contains("1_17")) {
            return true;
        } else if (nms.contains("1_18")) {
            return true;
        } else if (nms.contains("1_19")) {
            return true;
        } else return nms.contains("1_20_R1");
    }
}

