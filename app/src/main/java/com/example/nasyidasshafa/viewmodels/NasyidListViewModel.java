package com.example.nasyidasshafa.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.nasyidasshafa.models.Nasyid;
import com.example.nasyidasshafa.repositories.NasyidRepository;
import java.util.List;

public class NasyidListViewModel extends ViewModel {
    private final NasyidRepository nasyidRepository;

    public NasyidListViewModel() {
        // Ganti NasyidRepository.getInstance() menjadi new NasyidRepository()
        this.nasyidRepository = new NasyidRepository();
    }

    public LiveData<List<Nasyid>> getNasyidList() {
        return nasyidRepository.getNasyidListLiveData();
    }

    // Metode baru untuk me-refresh data secara manual
    public void refreshData() {
        nasyidRepository.fetchNasyidData();
    }
}
