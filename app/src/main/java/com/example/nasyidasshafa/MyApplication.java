package com.example.nasyidasshafa;

import android.app.Application;
import com.example.nasyidasshafa.utils.CloudinaryHelper;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inisialisasi Cloudinary saat aplikasi dimulai
        CloudinaryHelper.init(this);
    }
}
