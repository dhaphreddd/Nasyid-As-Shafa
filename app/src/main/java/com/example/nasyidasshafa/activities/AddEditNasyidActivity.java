package com.example.nasyidasshafa.activities;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.nasyidasshafa.R;
import com.example.nasyidasshafa.adapters.ImagePreviewAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AddEditNasyidActivity extends AppCompatActivity {

    private static final String TAG = "AddEditNasyidActivity";
    private boolean isEditMode = false;
    private String nasyidId;
    private MaterialToolbar toolbar;
    private TextInputEditText editTextJudul;
    private RecyclerView recyclerImages;
    private Button buttonPilihGambar, buttonUpload;
    private ProgressBar progressBar;
    private TextView textEmpty;
    private List<Object> imageList = new ArrayList<>();
    private ImagePreviewAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_nasyid);
        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerView();
        setupImagePicker();
        setupMode();

        buttonPilihGambar.setOnClickListener(v -> openImagePicker());
        buttonUpload.setOnClickListener(v -> handleSave());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarAddEdit);
        editTextJudul = findViewById(R.id.editTextJudul);
        recyclerImages = findViewById(R.id.recyclerImagesToUpload);
        buttonPilihGambar = findViewById(R.id.buttonPilihGambar);
        buttonUpload = findViewById(R.id.buttonUpload);
        progressBar = findViewById(R.id.progressBarUpload);
        textEmpty = findViewById(R.id.textEmpty);
    }

    private void setupMode() {
        // Cek apakah activity dibuka dari share intent
        handleSharedIntent();

        if (getIntent().hasExtra("NASYID_ID")) {
            isEditMode = true;
            nasyidId = getIntent().getStringExtra("NASYID_ID");
            editTextJudul.setText(getIntent().getStringExtra("NASYID_JUDUL"));
            toolbar.setTitle("Edit Nasyid");
            loadNasyidData(nasyidId);
        } else {
            toolbar.setTitle("Tambah Nasyid Baru");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void handleSharedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // Cek apakah ini share intent dari galeri
        if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (imageUri != null) {
                imageList.add(imageUri);
                adapter.notifyDataSetChanged();
                textEmpty.setVisibility(imageList.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(this, "Gambar dari galeri ditambahkan", Toast.LENGTH_SHORT).show();
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null && type.startsWith("image/")) {
            ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (imageUris != null) {
                for (Uri uri : imageUris) {
                    imageList.add(uri);
                }
                adapter.notifyDataSetChanged();
                textEmpty.setVisibility(imageList.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(this, imageUris.size() + " gambar dari galeri ditambahkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadNasyidData(String id) {
        setLoading(true);
        db.collection("nasyid").document(id).get().addOnSuccessListener(doc -> {
            setLoading(false);
            if (doc.exists()) {
                List<String> images = (List<String>) doc.get("images");
                if (images != null) {
                    imageList.addAll(images);
                    adapter.notifyDataSetChanged();
                    textEmpty.setVisibility(imageList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void handleSave() {
        String judul = editTextJudul.getText().toString().trim();
        if (judul.isEmpty()) {
            editTextJudul.setError("Judul wajib diisi");
            return;
        }

        setLoading(true);
        List<Uri> newUris = new ArrayList<>();
        List<String> existingUrls = new ArrayList<>();
        for (Object item : imageList) {
            if (item instanceof Uri) newUris.add((Uri) item);
            else if (item instanceof String) existingUrls.add((String) item);
        }

        if (newUris.isEmpty()) saveToFirestore(judul, existingUrls);
        else uploadNewImagesAndSave(judul, newUris, existingUrls);
    }

    private void uploadNewImagesAndSave(String judul, List<Uri> uris, List<String> existingUrls) {
        List<String> allUrls = new ArrayList<>(existingUrls);
        AtomicInteger uploadCounter = new AtomicInteger(uris.size());

        for (Uri uri : uris) {
            try {
                // KOMPRESI GAMBAR
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                byte[] data = bos.toByteArray();

                MediaManager.get().upload(data)
                        .unsigned("unsigned_upload_preset") // GANTI DENGAN PRESET ANDA
                        .callback(new UploadCallback() {
                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                allUrls.add((String) resultData.get("secure_url"));
                                if (uploadCounter.decrementAndGet() == 0) saveToFirestore(judul, allUrls);
                            }
                            @Override public void onError(String requestId, ErrorInfo error) {
                                if (uploadCounter.decrementAndGet() == 0) saveToFirestore(judul, allUrls);
                            }
                            @Override public void onStart(String requestId) {}
                            @Override public void onProgress(String requestId, long b, long t) {}
                            @Override public void onReschedule(String requestId, ErrorInfo e) {}
                        }).dispatch();
            } catch (Exception e) {
                if (uploadCounter.decrementAndGet() == 0) saveToFirestore(judul, allUrls);
            }
        }
    }

    private void saveToFirestore(String judul, List<String> urls) {
        Map<String, Object> data = new HashMap<>();
        data.put("judul", judul);
        data.put("images", urls);

        if (isEditMode) {
            db.collection("nasyid").document(nasyidId).update(data)
                    .addOnSuccessListener(a -> finish());
        } else {
            db.collection("nasyid").add(data)
                    .addOnSuccessListener(a -> finish());
        }
    }

    private void setupRecyclerView() {
        adapter = new ImagePreviewAdapter(imageList);
        adapter.setOnItemDeleteListener(pos -> {
            imageList.remove(pos);
            adapter.notifyItemRemoved(pos);
            textEmpty.setVisibility(imageList.isEmpty() ? View.VISIBLE : View.GONE);
        });
        recyclerImages.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerImages.setAdapter(adapter);

        // Tambahkan ItemTouchHelper untuk drag-and-drop
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                // Pindahkan item dalam list
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(imageList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(imageList, i, i - 1);
                    }
                }

                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Tidak digunakan
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true; // Aktifkan long-press drag
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerImages);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                if (result.getData().getClipData() != null) {
                    ClipData cd = result.getData().getClipData();
                    for (int i = 0; i < cd.getItemCount(); i++) imageList.add(cd.getItemAt(i).getUri());
                } else if (result.getData().getData() != null) {
                    imageList.add(result.getData().getData());
                }
                adapter.notifyDataSetChanged();
                textEmpty.setVisibility(imageList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Pilih Gambar"));
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonUpload.setEnabled(!loading);
    }
}