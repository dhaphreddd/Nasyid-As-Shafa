package com.example.nasyidasshafa.models;

public class User {
    private String uid;
    private String email;
    private String role;

    public User() {
        // Diperlukan constructor kosong untuk Firestore
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
