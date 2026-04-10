package com.example.nasyidasshafa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nasyidasshafa.R;

import java.util.Collections;
import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ImageViewHolder> {

    private final List<Object> images; // Bisa berisi Uri (lokal) atau String (URL Cloudinary)
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDelete(int position);
    }

    public ImagePreviewAdapter(List<Object> images) {
        this.images = images;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Object imageSource = images.get(position);
        
        Glide.with(holder.itemView.getContext())
                .load(imageSource)
                .into(holder.imagePreview);

        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(images, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(images, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePreview;
        ImageButton buttonDelete;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.imagePreview);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteImage);
        }
    }
}
