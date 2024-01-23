package cn.bossfriday.cloudmusic.converter.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * GsonUtils
 *
 * @author chenx
 */
public class GsonUtils {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private GsonUtils() {

    }

    /**
     * toJson
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }

        return gsonString;
    }

    /**
     * fromJson
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String text, Class<T> clazz) {
        if (gson != null) {
            return gson.fromJson(text, clazz);
        }

        return null;
    }

    /**
     * fromJson
     *
     * @param str
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        return gson.fromJson(str, type);
    }

    /**
     * fromJson
     *
     * @param json
     * @param typeToken
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }
}
