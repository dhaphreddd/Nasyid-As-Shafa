package com.example.nasyidasshafa.utils;

import android.content.Context;
import android.util.Log;
import com.cloudinary.android.MediaManager;
import com.example.nasyidasshafa.BuildConfig;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {

    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) return;

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
        config.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
        config.put("api_secret", BuildConfig.CLOUDINARY_API_SECRET);

        // Validate that credentials are set
        if (BuildConfig.CLOUDINARY_CLOUD_NAME.isEmpty() ||
            BuildConfig.CLOUDINARY_API_KEY.isEmpty() ||
            BuildConfig.CLOUDINARY_API_SECRET.isEmpty()) {
            Log.e("CloudinaryHelper", "Cloudinary credentials not set. Please configure them in local.properties");
            return;
        }

        MediaManager.init(context, config);
        initialized = true;
    }
}
