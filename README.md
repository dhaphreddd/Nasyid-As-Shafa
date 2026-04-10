# Nasyid As-Shafa 🎵

![Android](https://img.shields.io/badge/Android-7.0%2B-green.svg)
![API](https://img.shields.io/badge/API-34%2B-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

Aplikasi Android untuk mengelola dan menampilkan koleksi Nasyid As-Shafa dengan fitur lengkap termasuk manajemen gambar dan sistem autentikasi pengguna.


## ✨ Fitur Utama

- **Koleksi Nasyid**: Tampilan daftar nasyid dengan judul dan gambar
- **Detail Nasyid**: Lihat lirik dan gambar dengan fitur zoom
- **Image Slider**: Tampilan gambar bergeser dengan dukungan zoom menggunakan PhotoView
- **Pencarian**: Cari nasyid berdasarkan judul secara real-time
- **Autentikasi Pengguna**: Login sebagai admin untuk mengelola konten
- **Role-Based Access Control**: 
  - Admin: Dapat menambah, mengedit, dan menghapus nasyid
  - User Biasa: Hanya dapat melihat dan mencari nasyid
- **Firebase Integration**: 
  - Firebase Authentication untuk login
  - Cloud Firestore untuk penyimpanan data
- **Cloudinary**: Penyimpanan dan manajemen gambar di cloud
- **Material Design**: UI modern dengan komponen Material Design
- **MVVM Architecture**: Menggunakan ViewModel dan LiveData untuk manajemen state

## 🛠️ Teknologi yang Digunakan

- **Language**: Java
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle with Kotlin DSL

### Libraries

- **AndroidX**
  - AppCompat 1.6.1
  - Material Design 1.11.0
  - ConstraintLayout 2.1.4
  - Activity 1.8.0
  - Lifecycle ViewModel & LiveData 2.7.0
  - ViewPager2 1.1.0

- **Firebase** (via BOM 33.0.0)
  - Firebase Analytics
  - Firebase Authentication
  - Cloud Firestore

- **Third-party Libraries**
  - Glide 4.16.0 - Image loading
  - PhotoView 2.3.0 - Image zoom
  - Glide Transformations 4.3.0 - Image effects
  - Cloudinary Android 2.4.0 - Cloud image storage


## 📋 Prasyarat

Sebelum menjalankan proyek ini, pastikan Anda telah:

1. Menginstal Android Studio (versi terbaru disarankan)
2. Menginstal JDK 8 atau yang lebih baru
3. Membuat project di [Firebase Console](https://console.firebase.google.com/)
4. Mendapatkan akun Cloudinary dari [Cloudinary Console](https://cloudinary.com/)

## 🔧 Cara Instalasi

1. **Clone repository**
   ```bash
   git clone https://github.com/dhaphreddd/Nasyid-As-Shafa.git
   cd Nasyid-As-Shafa
   ```

2. **Buka project di Android Studio**
   - Buka Android Studio
   - Pilih "Open an Existing Project"
   - Navigasi ke folder project yang telah di-clone

3. **Konfigurasi Firebase**
   - Buka [Firebase Console](https://console.firebase.google.com/)
   - Buat project baru atau gunakan project yang sudah ada
   - Tambahkan aplikasi Android dengan package name: `com.example.nasyidasshafa`
   - Download file `google-services.json`
   - Letakkan file tersebut di folder `app/` project

4. **Setup Cloud Firestore Database**
   - Di Firebase Console, navigasi ke menu **Firestore Database**
   - Klik **Create Database** untuk membuat database baru
   - Pilih lokasi database (disarankan pilih lokasi terdekat dengan pengguna)
   - Pilih mode **Start in Test Mode** untuk development (bisa diubah ke production mode nanti)
   - Buat collection dan documents sesuai kebutuhan aplikasi:
     - Collection: `nasyid` (untuk menyimpan data nasyid)
     - Collection: `users` (untuk menyimpan data user dan role)
   - Atur rules Firestore sesuai kebutuhan (test mode allows all read/write)

5. **Konfigurasi Cloudinary**
   - Buka file `local.properties` di root project
   - Tambahkan konfigurasi Cloudinary Anda:
   ```properties
   cloudinary.cloud_name=your-cloud-name
   cloudinary.api_key=your-api-key
   cloudinary.api_secret=your-api-secret
   ```
   - ⚠️ **Penting**: File `local.properties` sudah ada di `.gitignore` sehingga tidak akan di-commit ke GitHub
   - Dapatkan kredensial dari [Cloudinary Console](https://cloudinary.com/console)

6. **Sync Gradle**
   - Android Studio akan otomatis mendeteksi perubahan dan meminta untuk sync Gradle
   - Klik "Sync Now" atau pilih File > Sync Project with Gradle Files

7. **Build dan Run**
   - Klik tombol Run (▶) atau tekan Shift + F10
   - Pilih emulator atau perangkat Android yang terhubung

## 📱 Penggunaan Aplikasi

### Sebagai User Biasa
- Buka aplikasi untuk melihat daftar nasyid
- Gunakan fitur pencarian untuk mencari nasyid tertentu
- Klik pada nasyid untuk melihat detail, lirik, dan gambar
- Zoom gambar dengan pinch gesture

### Sebagai Admin
1. Login sebagai admin melalui menu "Login Admin"
2. Tambah nasyid baru dengan tombol FAB (+)
3. Edit atau hapus nasyid yang sudah ada
4. Logout ketika selesai mengelola konten

## 📁 Struktur Project

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/nasyidasshafa/
│   │   │   ├── activities/          # Activity classes
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── AddEditNasyidActivity.java
│   │   │   │   └── DetailNasyidActivity.java
│   │   │   ├── adapters/           # RecyclerView adapters
│   │   │   │   ├── NasyidAdapter.java
│   │   │   │   └── ImageSliderAdapter.java
│   │   │   ├── models/             # Data models
│   │   │   │   └── Nasyid.java
│   │   │   ├── viewmodels/         # ViewModels
│   │   │   │   └── NasyidListViewModel.java
│   │   │   ├── repositories/       # Data repositories
│   │   │   └── utils/              # Utility classes
│   │   ├── res/                    # Resources
│   │   │   ├── layout/             # XML layouts
│   │   │   ├── values/             # Strings, colors, etc.
│   │   │   └── drawable/           # Images and drawables
│   │   └── AndroidManifest.xml
```

## 🔐 Keamanan

✅ **Keamanan yang telah diimplementasikan:**

- Cloudinary credentials disimpan di `local.properties` (tidak di-commit ke GitHub)
- File `CloudinaryHelper.java` menggunakan `BuildConfig` untuk membaca credentials dengan aman
- File `google-services.json` sudah di .gitignore dan tidak di-commit ke repository
- File `.gitignore` sudah dikonfigurasi untuk mengecualikan file sensitif

⚠️ **Rekomendasi untuk Production:**

- Atur Firestore Security Rules yang proper (jangan gunakan Test Mode di production)
- Aktifkan ProGuard/R8 untuk obfuscation kode di build release
- Pertimbangkan untuk menggunakan Firebase Remote Config untuk manajemen credentials yang lebih dinamis
- Regenerate API keys Cloudinary jika pernah di-expose sebelumnya

## 🤝 Kontribusi

Kontribusi sangat diterima! Jika Anda ingin berkontribusi:

1. Fork repository ini
2. Buat branch untuk fitur baru (`git checkout -b feature/fitur-baru`)
3. Commit perubahan Anda (`git commit -m 'Menambahkan fitur baru'`)
4. Push ke branch (`git push origin feature/fitur-baru`)
5. Buka Pull Request

## 📝 Lisensi

Project ini dilisensikan under MIT License - lihat file [LICENSE](LICENSE) untuk detail

## 👥 Pengembang

Dikembangkan oleh Daffa Khoirut Tamam

## 📧 Kontak

Jika ada pertanyaan atau saran, hubungi:
- Email: arset.saklawase@gmail.com
- GitHub: [@dhaphreddd](https://github.com/dhaphreddd)

## 🙏 Terima Kasih

- Firebase untuk layanan backend
- Cloudinary untuk layanan penyimpanan gambar
- Komunitas open source Android

---