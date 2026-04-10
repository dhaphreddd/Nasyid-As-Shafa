package com.example.nasyidasshafa.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nasyidasshafa.models.Nasyid;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NasyidRepository {

    private static final String TAG = "NasyidRepository";
    private final CollectionReference nasyidCollection = FirebaseFirestore.getInstance().collection("nasyid");
    private final MutableLiveData<List<Nasyid>> nasyidListLiveData = new MutableLiveData<>();

    public NasyidRepository() {
        // Panggil listener saat repository dibuat
        listenForNasyidChanges();
    }

    public LiveData<List<Nasyid>> getNasyidListLiveData() {
        return nasyidListLiveData;
    }

    private void listenForNasyidChanges() {
        // Menggunakan addSnapshotListener untuk pembaruan real-time
        nasyidCollection.orderBy("judul", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Jika ada error saat mendengarkan
                        Log.e(TAG, "Gagal mendengarkan perubahan data nasyid", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Nasyid> nasyidList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Konversi setiap dokumen menjadi objek Nasyid
                            Nasyid nasyid = document.toObject(Nasyid.class);
                            nasyid.setId(document.getId()); // Jangan lupa set ID dokumen
                            nasyidList.add(nasyid);
                        }
                        // Kirim data baru ke LiveData, yang akan diterima oleh MainActivity
                        nasyidListLiveData.setValue(nasyidList);
                        Log.d(TAG, "Data nasyid diperbarui secara real-time. Jumlah: " + nasyidList.size());
                    }
                });
    }

    // Metode fetchNasyidData() tidak lagi dibutuhkan karena kita sudah menggunakan listener real-time,
    // tapi kita biarkan saja agar tidak menyebabkan error di ViewModel.
    public void fetchNasyidData() {
        // Metode ini sengaja dikosongkan. Semua logika sudah pindah ke listener.
        Log.d(TAG, "refreshData() dipanggil, tetapi pembaruan ditangani oleh listener.");
    }
}
