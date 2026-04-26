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

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.google.android.material.appbar.AppBarLayout;

public class DetailNasyidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nasyid);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        AppBarLayout appBarLayout = findViewById(R.id.appBarDetail);
        
        // Sesuaikan padding toolbar agar tidak tertutup status bar
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        ViewPager2 viewPager = findViewById(R.id.viewPagerImages);
        TextView textNoImage = findViewById(R.id.textNoImage);
        View layoutIndicator = findViewById(R.id.layoutImageIndicator);
        View cardInfo = findViewById(R.id.cardImageInfo);

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
            ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageUrls, isZoomed -> {
                viewPager.setUserInputEnabled(!isZoomed); // Matikan geser jika sedang di-zoom
                setFullscreen(isZoomed, appBarLayout, layoutIndicator, cardInfo);
            });
            viewPager.setAdapter(adapter);

            // Tambahkan indikator angka di Toolbar (Subtitle)
            int totalImages = imageUrls.size();
            toolbar.setSubtitle("1 / " + totalImages); // Set subtitle awal segera setelah dimuat
            
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    toolbar.setSubtitle((position + 1) + " / " + totalImages);
                }
            });
        } else {
            // Jika tidak ada gambar, tampilkan pesan
            textNoImage.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }

    private boolean isCurrentFullscreen = false;

    private void setFullscreen(boolean isFullscreen, View appBar, View indicator, View cardInfo) {
        if (this.isCurrentFullscreen == isFullscreen) return; // Jangan jalankan jika status sudah sama
        this.isCurrentFullscreen = isFullscreen;

        if (isFullscreen) {
            appBar.animate().alpha(0f).translationY(-appBar.getHeight()).setDuration(200).withEndAction(() -> appBar.setVisibility(View.GONE));
            indicator.setVisibility(View.GONE);
            cardInfo.setVisibility(View.GONE);
            hideSystemUI();
        } else {
            appBar.setVisibility(View.VISIBLE);
            appBar.animate().alpha(1f).translationY(0).setDuration(200);
            indicator.setVisibility(View.VISIBLE);
            // cardInfo tetap sesuai logika aslinya
            showSystemUI();
        }
    }

    private void hideSystemUI() {
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private void showSystemUI() {
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.show(WindowInsetsCompat.Type.systemBars());
    }
}
