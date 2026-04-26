package com.example.nasyidasshafa.adapters;

import android.content.Context;
import android.view.LayoutInflater;import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nasyidasshafa.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

    private final Context context;
    private final List<String> imageUrls;
    private final OnZoomListener zoomListener;

    public interface OnZoomListener {
        void onZoomChanged(boolean isZoomed);
    }

    public ImageSliderAdapter(Context context, List<String> imageUrls, OnZoomListener zoomListener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.zoomListener = zoomListener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Kita butuh layout baru untuk item ini
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context)
                .load(imageUrl)
                .into(holder.photoView);

        // Atur zoom maksimal agar lebih besar seperti galeri HP (default PhotoView hanya 3.0f)
        holder.photoView.setMaximumScale(8.0f);
        holder.photoView.setMediumScale(3.0f);


        // Deteksi perubahan zoom
        holder.photoView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            if (zoomListener != null) {
                // Jika skala > 1.0 berarti sedang di-zoom
                zoomListener.onZoomChanged(holder.photoView.getScale() > 1.0f);
            }
        });

        // Deteksi perubahan zoom (lebih aman menggunakan Matrix)
        holder.photoView.setOnMatrixChangeListener(rect -> {
            if (zoomListener != null) {
                float scale = holder.photoView.getScale();
                // Jika sedang di-zoom (skala > 1.0), matikan swipe ViewPager
                if (scale > 1.0f) {
                    holder.photoView.getParent().requestDisallowInterceptTouchEvent(true);
                    zoomListener.onZoomChanged(true);
                } else {
                    zoomListener.onZoomChanged(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}
