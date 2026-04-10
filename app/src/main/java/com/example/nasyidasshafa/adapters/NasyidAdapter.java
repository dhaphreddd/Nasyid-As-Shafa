package com.example.nasyidasshafa.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nasyidasshafa.R;
import com.example.nasyidasshafa.activities.AddEditNasyidActivity;
import com.example.nasyidasshafa.activities.DetailNasyidActivity;
import com.example.nasyidasshafa.models.Nasyid;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropTransformation;

public class NasyidAdapter extends RecyclerView.Adapter<NasyidAdapter.NasyidViewHolder> implements Filterable {

    private final Context context;
    private List<Nasyid> nasyidList; // List yang ditampilkan di layar
    private final List<Nasyid> listAsli; // Cadangan data asli untuk keperluan filter
    private boolean isAdmin = false;

    public NasyidAdapter(Context context, List<Nasyid> nasyidList) {
        this.context = context;
        this.nasyidList = nasyidList;
        this.listAsli = new ArrayList<>(nasyidList);
    }

    @NonNull
    @Override
    public NasyidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nasyid, parent, false);
        return new NasyidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NasyidViewHolder holder, int position) {
        holder.bind(nasyidList.get(position));
    }

    @Override
    public int getItemCount() {
        return nasyidList.size();
    }

    // Dipanggil saat data dari Firestore berubah
    public void updateNasyidList(List<Nasyid> newList) {
        listAsli.clear();
        listAsli.addAll(newList);
        nasyidList = new ArrayList<>(listAsli);
        notifyDataSetChanged();
    }

    // Dipanggil dari MainActivity untuk mengatur hak akses tombol
    public void setAdmin(boolean admin) {
        isAdmin = admin;
        notifyDataSetChanged();
    }

    // --- FITUR SEARCH ---
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Nasyid> filtered = new ArrayList<>();
                String pattern = constraint.toString().toLowerCase().trim();

                if (pattern.isEmpty()) {
                    filtered.addAll(listAsli);
                } else {
                    for (Nasyid item : listAsli) {
                        if (item.getJudul().toLowerCase().contains(pattern)) {
                            filtered.add(item);
                        }
                    }
                }

                FilterResults res = new FilterResults();
                res.values = filtered;
                return res;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                nasyidList = (List<Nasyid>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class NasyidViewHolder extends RecyclerView.ViewHolder {
        TextView judul;
        ImageView preview;
        Button btnUpdate;
        Button btnDelete;
        LinearLayout layoutActions;

        NasyidViewHolder(View v) {
            super(v);
            judul = v.findViewById(R.id.textViewJudulNasyid);
            preview = v.findViewById(R.id.imagePreview);
            btnUpdate = v.findViewById(R.id.buttonUpdate);
            btnDelete = v.findViewById(R.id.buttonDelete);
            layoutActions = v.findViewById(R.id.layoutActions);
        }

        void bind(Nasyid n) {
            judul.setText(n.getJudul());

            // --- TAMPILKAN GAMBAR PREVIEW ---
            if (n.getImages() != null && !n.getImages().isEmpty()) {
                Glide.with(context)
                        .load(n.getImages().get(0))
                        // Mengembalikan ke ukuran semula (1000x500)
                        .transform(new CropTransformation(1000, 500, CropTransformation.CropType.TOP))
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(preview);
                preview.setVisibility(View.VISIBLE);
            } else {
                preview.setVisibility(View.GONE);
            }

            // --- VISIBILITAS ADMIN ---
            layoutActions.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            btnUpdate.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

            // --- LISTENER KLIK ---

            // Klik item (Ke Detail)
            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, DetailNasyidActivity.class);
                i.putExtra("NASYID_JUDUL", n.getJudul());
                i.putStringArrayListExtra("NASYID_IMAGES", new ArrayList<>(n.getImages()));
                context.startActivity(i);
            });

            // Klik Edit (Ke AddEditActivity)
            btnUpdate.setOnClickListener(v -> {
                Intent i = new Intent(context, AddEditNasyidActivity.class);
                i.putExtra("NASYID_ID", n.getId());
                i.putExtra("NASYID_JUDUL", n.getJudul());
                context.startActivity(i);
            });

            // Klik Hapus (Firestore Delete)
            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Hapus Nasyid")
                        .setMessage("Apakah Anda yakin ingin menghapus '" + n.getJudul() + "'?")
                        .setPositiveButton("Hapus", (d, w) -> {
                            FirebaseFirestore.getInstance().collection("nasyid")
                                    .document(n.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            });
        }
    }
}