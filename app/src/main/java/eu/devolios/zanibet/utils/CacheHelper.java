package eu.devolios.zanibet.utils;

import android.content.Context;

import java.io.File;

import okhttp3.Response;

public class CacheHelper {

    private static final long MAX_AGE_CACHE = 60 * 60 * 24 * 5L;
    public static final long MAX_DISK_CACHE = 50 * 1024 * 1024L;

    private static final String IMAGES_CACHE_FOLDER = "images";

    public static Response.Builder addCacheControl(Response.Builder builder) {
        return builder.header("Cache-Control", "max-age=" + MAX_AGE_CACHE);
    }

    public static File getImagesCacheDir(Context context) {
        File cacheDir = new File(context.getCacheDir(), IMAGES_CACHE_FOLDER);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return cacheDir;
    }


}