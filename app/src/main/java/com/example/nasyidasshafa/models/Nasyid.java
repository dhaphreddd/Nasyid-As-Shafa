package com.example.nasyidasshafa.models;

import java.util.List;

public class Nasyid {
    private String id;
    private String judul;
    private String lirik; // Variabel ini sudah ada
    private List<String> images;

    // Pastikan Constructor kosong ada
    public Nasyid() {
    }

    // --- GETTER DAN SETTER ---
    // Pastikan semua ini ada, terutama getLirik() dan setLirik()

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    // INI YANG KEMUNGKINAN BESAR HILANG
    public String getLirik() {
        return lirik;
    }

    public void setLirik(String lirik) {
        this.lirik = lirik;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
