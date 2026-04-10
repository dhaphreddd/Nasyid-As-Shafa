package com.example.nasyidasshafa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nasyidasshafa.R;
import com.example.nasyidasshafa.adapters.ImageSliderAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class DetailNasyidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nasyid);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        ViewPager2 viewPager = findViewById(R.id.viewPagerImages);
        TextView textNoImage = findViewById(R.id.textNoImage);

        // Ambil data yang dikirim dari NasyidAdapter
        String judul = getIntent().getStringExtra("NASYID_JUDUL");
        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra("NASYID_IMAGES");

        // Set judul di toolbar dan listener untuk tombol kembali
        toolbar.setTitle(judul);
        toolbar.setNavigationOnClickListener(v -> finish());

        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Jika ada gambar, tampilkan galeri
            textNoImage.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageUrls);
            viewPager.setAdapter(adapter);
        } else {
            // Jika tidak ada gambar, tampilkan pesan
            textNoImage.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }
}
