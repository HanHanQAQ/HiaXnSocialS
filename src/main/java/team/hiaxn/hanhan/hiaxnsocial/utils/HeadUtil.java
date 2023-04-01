package team.hiaxn.hanhan.hiaxnsocial.utils;

import java.lang.reflect.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

public class HeadUtil
{
    private static Method GET_PROPERTIES;
    private static Method INSERT_PROPERTY;
    private static Constructor GAME_PROFILE_CONSTRUCTOR;
    private static Constructor PROPERTY_CONSTRUCTOR;

    static {
        try {
            final Class gameProfile = Class.forName("com.mojang.authlib.GameProfile");
            final Class property = Class.forName("com.mojang.authlib.properties.Property");
            final Class propertyMap = Class.forName("com.mojang.authlib.properties.PropertyMap");
            HeadUtil.GAME_PROFILE_CONSTRUCTOR = getConstructor(gameProfile, 2);
            HeadUtil.PROPERTY_CONSTRUCTOR = getConstructor(property, 2);
            HeadUtil.GET_PROPERTIES = getMethod(gameProfile, "getProperties");
            HeadUtil.INSERT_PROPERTY = getMethod(propertyMap, "put");
        }
        catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public static Method getMethod(final Class clazz, final String name) {
        Method[] arrayOfMethod;
        for (int j = (arrayOfMethod = clazz.getMethods()).length, i = 0; i < j; ++i) {
            final Method m = arrayOfMethod[i];
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    private static Field getField(final Class clazz, final String fieldName) throws NoSuchFieldException {
        return clazz.getDeclaredField(fieldName);
    }

    public static void setFieldValue(final Object object, final String fieldName, final Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field f = getField(object.getClass(), fieldName);
        f.setAccessible(true);
        f.set(object, value);
    }

    public static Constructor getConstructor(final Class clazz, final int numParams) {
        Constructor[] arrayOfConstructor;
        for (int j = (arrayOfConstructor = clazz.getConstructors()).length, i = 0; i < j; ++i) {
            final Constructor constructor = arrayOfConstructor[i];
            if (constructor.getParameterTypes().length == numParams) {
                return constructor;
            }
        }
        return null;
    }

    public static ItemStack getSkull(String Value) {
        Value = Value.replace(" ", "");
        if (Value.length() > 16) {
            try {
                final ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
                final ItemMeta meta = skull.getItemMeta();
                try {
                    final Object profile = HeadUtil.GAME_PROFILE_CONSTRUCTOR.newInstance(UUID.randomUUID(), UUID.randomUUID().toString().substring(17).replace("-", ""));
                    final Object properties = HeadUtil.GET_PROPERTIES.invoke(profile, new Object[0]);
                    HeadUtil.INSERT_PROPERTY.invoke(properties, "textures", HeadUtil.PROPERTY_CONSTRUCTOR.newInstance("textures", Value));
                    setFieldValue(meta, "profile", profile);
                }
                catch (Exception var5) {
                    var5.printStackTrace();
                }
                skull.setItemMeta(meta);
                return skull;
            }
            catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        }
        final ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        final SkullMeta sm = (SkullMeta)skull.getItemMeta();
        sm.setOwner(Value);
        skull.setItemMeta((ItemMeta)sm);
        return skull;
    }

    public static ItemStack getPlayerHead(final String playerName) {
        final ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        final SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
        skullMeta.setOwner(playerName);
        itemStack.setItemMeta((ItemMeta)skullMeta);
        return itemStack;
    }
}
