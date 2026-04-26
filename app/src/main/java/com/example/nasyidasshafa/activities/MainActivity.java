package com.example.nasyidasshafa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nasyidasshafa.R;
import com.example.nasyidasshafa.adapters.NasyidAdapter;
import com.example.nasyidasshafa.viewmodels.NasyidListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NasyidListViewModel viewModel;
    private NasyidAdapter adapter;
    private FloatingActionButton fabAdd;
    private ProgressBar progressBar;
    private TextView textEmptyState;
    private FirebaseAuth mAuth;
    private Menu menu; // Menyimpan referensi menu untuk update visibilitas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // UI Initialization
        progressBar = findViewById(R.id.progressBar);
        textEmptyState = findViewById(R.id.textEmptyState);
        fabAdd = findViewById(R.id.fabAddNasyid);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNasyid);

        adapter = new NasyidAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(NasyidListViewModel.class);
        initObservers();

        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddEditNasyidActivity.class)));
    }

    private void initObservers() {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getNasyidList().observe(this, list -> {
            progressBar.setVisibility(View.GONE);
            if (list != null) {
                adapter.updateNasyidList(list);
                
                // Menampilkan total nasyid di subtitle Toolbar
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setSubtitle("Total: " + list.size() + " Nasyid");
                }

                // POIN 3: Teks muncul jika list kosong
                textEmptyState.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkRole();
    }

    private void checkRole() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseFirestore.getInstance().collection("users").document(mAuth.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        boolean isAdmin = doc.exists() && "admin".equals(doc.getString("role"));
                        adapter.setAdmin(isAdmin);
                        fabAdd.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

                        // Update Menu Visibilitas
                        if (menu != null) {
                            menu.findItem(R.id.action_logout).setVisible(isAdmin);
                            menu.findItem(R.id.action_login_admin).setVisible(!isAdmin);
                        }
                    });
        } else {
            adapter.setAdmin(false);
            fabAdd.setVisibility(View.GONE);
            if (menu != null) {
                menu.findItem(R.id.action_logout).setVisible(false);
                menu.findItem(R.id.action_login_admin).setVisible(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Cari nasyid...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        checkRole(); // Sinkronkan ulang menu setelah dibuat
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            mAuth.signOut();
            checkRole();
            return true;
        } else if (id == R.id.action_login_admin) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}