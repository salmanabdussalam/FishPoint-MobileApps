<div align="center">

# 🐟 FishPoint

**Aplikasi komunitas pemancing — temukan dan bagikan spot mancing terbaik.**

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Status](https://img.shields.io/badge/Status-Done-brightgreen)

*Rekayasa Perangkat Lunak · Kelompok 1 · Informatika UNS 2025/2026*

</div>

---

# FishPoint

FishPoint adalah aplikasi Android yang dirancang khusus untuk para pemancing. Aplikasi ini memudahkan komunitas pemancing untuk saling berbagi, mencari, dan mengelola informasi lokasi spot memancing terbaik. 

Aplikasi ini menggunakan arsitektur modern Android (Jetpack Compose, ViewModel, Retrofit) di sisi *frontend* dan menggunakan PHP & MySQL di sisi *backend* (API).

## Fitur Utama

- **Otentikasi**: Login dan Registrasi pengguna (MEMBER & ADMIN).
- **Eksplorasi Spot**: Mencari dan melihat detail lokasi spot memancing.
- **Kelola Spot (Admin & Owner)**: Menambah, mengedit, dan menghapus spot memancing, lengkap dengan kemampuan multi-upload foto.
- **Review & Ulasan**: Pengguna dapat memberikan *rating* dan ulasan pada spot yang pernah dikunjungi.
- **Admin Dashboard**: Panel khusus admin untuk mengelola seluruh data aplikasi (statistik, hapus spot/ulasan, *ban/unban* pengguna).

## Teknologi yang Digunakan

### Frontend (Android)
- **Bahasa**: Kotlin
- **UI Toolkit**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit & OkHttp
- **Asynchronous**: Kotlin Coroutines
- **Image Loading**: Coil Compose

### Backend (API)
- **Bahasa**: PHP 8.x
- **Database**: MySQL / MariaDB (menggunakan PDO)
- **Format Data**: JSON & Multipart Form Data
- **Environment**: Laragon (Local Development)

## Tim Pengembang

Aplikasi ini dikembangkan oleh tim yang berdedikasi:

| Nama | Role |
| :--- | :--- |
| **Andradhi Bondan Pamungkas** | Backend Developer |
| **Ataa Arkan Tsany** | Database & QA/Tester |
| **Imam Dian Firmansyah** | Frontend Developer |
| **Salman Abdussalam** | Backend Developer |

## Instalasi dan Setup

### 1. Setup Backend (Laragon)
1. *Clone* repositori ini.
2. Salin folder `backend_api` ke dalam direktori Laragon (contoh: `C:\laragon\www\backend_api`).
3. Buat database baru di MySQL/HeidiSQL, misal `fishpoint_db`.
4. *Import* struktur tabel dan *dummy data* (jika ada).
5. Sesuaikan konfigurasi koneksi *database* di dalam `backend_api/api/db.php`:
   ```php
   $host = '127.0.0.1';
   $db   = 'fishpoint_db';
   $user = 'root';
   $pass = ''; // Sesuaikan dengan password MySQL kamu
   ```

### 2. Setup Android App
1. Buka folder `FishPoint` menggunakan **Android Studio**.
2. Sesuaikan `BASE_URL` di `app/src/main/java/com/app/fishpoint/data/api/FishPointApi.kt`.
   - Jika kamu menggunakan **Emulator Android Studio**, ganti IP menjadi `10.0.2.2`:
     ```kotlin
     private const val BASE_URL = "http://10.0.2.2/backend_api/"
     ```
   - Jika kamu *running* langsung di perangkat HP *real device* yang terkoneksi ke jaringan WiFi yang sama dengan laptopmu, gunakan IP IPv4 komputermu (bisa dicek melalui `ipconfig`):
     ```kotlin
     private const val BASE_URL = "http://192.168.x.x/backend_api/"
     ```
3. Lakukan *Sync Project with Gradle Files*.
4. Klik **Run** atau **Debug** untuk meng-install aplikasi ke perangkat/emulator.

## Dokumentasi Struktur Direktori

- `app/src/main/java/com/app/fishpoint/ui/` - Berisi *screens*, *components*, dan *viewmodels* (Jetpack Compose).
- `app/src/main/java/com/app/fishpoint/data/` - Model data (*data classes*), definisi API Retrofit, dan *Repository*.
- `backend_api/api/` - Endpoint *backend* PHP (`auth.php`, `spots.php`, `reviews.php`, `users.php`).
- `backend_api/uploads/` - Tempat penyimpanan file gambar yang diunggah.

---
*FishPoint © 2026 - Catch the best!*
