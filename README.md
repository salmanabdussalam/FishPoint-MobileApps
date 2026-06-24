<p align="center">
  <img src="assets/logo_fishpoint.png" alt="FishPoint Logo" width="120"/>
</p>

<h1 align="center"> FishPoint</h1>

<p align="center">
  <strong>Temukan spot memancing terbaik di sekitarmu!</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/PHP-777BB4?style=for-the-badge&logo=php&logoColor=white" alt="PHP"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
</p>

---

## 📖 Tentang Aplikasi

**FishPoint** adalah aplikasi Android yang dirancang khusus untuk para pemancing. Aplikasi ini memudahkan komunitas pemancing untuk saling berbagi, mencari, dan mengelola informasi lokasi spot memancing terbaik di seluruh Indonesia.

Dibangun menggunakan arsitektur modern Android (**Jetpack Compose**, **MVVM**, **Retrofit**) di sisi *frontend* dan **PHP & MySQL** di sisi *backend* (REST API).

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
| :--- | :--- |
| 🔐 **Otentikasi** | Login & Registrasi pengguna dengan role **Member** dan **Admin** |
| 🗺️ **Eksplorasi Spot** | Telusuri spot memancing di peta interaktif, lihat detail lengkap beserta foto |
| 📸 **Multi-Upload Foto** | Upload hingga beberapa foto sekaligus untuk setiap spot |
| ✏️ **Kelola Spot** | Tambah, edit, dan hapus spot memancing (untuk pemilik spot & admin) |
| ⭐ **Review & Rating** | Berikan ulasan dan rating pada spot yang pernah dikunjungi |
| 🛡️ **Admin Dashboard** | Panel admin untuk mengelola seluruh data: statistik, hapus spot/ulasan, ban/unban pengguna |
| 🔄 **Auto-Refresh Data** | Data otomatis diperbarui tanpa perlu logout/restart aplikasi |

---

## 🛠️ Teknologi yang Digunakan

### Frontend (Android)
| Teknologi | Keterangan |
| :--- | :--- |
| **Kotlin** | Bahasa pemrograman utama |
| **Jetpack Compose** | Modern UI toolkit untuk membangun antarmuka |
| **MVVM** | Arsitektur Model-View-ViewModel |
| **Retrofit & OkHttp** | HTTP client untuk komunikasi dengan REST API |
| **Kotlin Coroutines** | Asynchronous programming |
| **Coil Compose** | Image loading library |
| **OSMDroid** | Peta interaktif (OpenStreetMap) |

### Backend (REST API)
| Teknologi | Keterangan |
| :--- | :--- |
| **PHP 8.x** | Bahasa pemrograman server-side |
| **MySQL / MariaDB** | Relational database (PDO) |
| **JSON** | Format pertukaran data |
| **Multipart Form Data** | Upload file gambar |

---

## 📁 Struktur Direktori

```
FishPoint/
├── app/src/main/java/com/app/fishpoint/
│   ├── data/
│   │   ├── api/            # Definisi API Retrofit & model request/response
│   │   ├── model/          # Data classes (Spot, User, Review, dll)
│   │   └── repository/     # Repository pattern untuk akses data
│   ├── navigation/         # NavGraph & Screen routes
│   └── ui/
│       ├── components/     # Reusable UI components
│       ├── screens/        # Halaman-halaman aplikasi (Compose)
│       ├── theme/          # Tema, warna, dan tipografi
│       └── viewmodel/      # ViewModels (Auth & Spot)
│
├── backend_api/
│   ├── api/
│   │   ├── db.php          # Konfigurasi koneksi database
│   │   ├── auth.php        # Endpoint login & register
│   │   ├── spots.php       # Endpoint CRUD spot memancing
│   │   ├── reviews.php     # Endpoint ulasan
│   │   └── users.php       # Endpoint manajemen pengguna
│   ├── uploads/            # Penyimpanan foto yang diunggah
│   └── fishpoint_db.sql    # Skema database & data awal
│
└── assets/
    └── logo_fishpoint.png  # Logo aplikasi
```

---

## 🚀 Instalasi dan Setup

### 1. Setup Backend

#### Opsi A: Hosting Online (Produksi)
1. Upload folder `backend_api` ke `public_html` di hosting Anda.
2. Buat database MySQL baru melalui cPanel.
3. Import file `backend_api/fishpoint_db.sql` via **phpMyAdmin**.
4. Edit file `backend_api/api/db.php` dengan kredensial database hosting Anda:
   ```php
   $host = 'localhost';
   $db   = 'nama_database_anda';
   $user = 'username_database_anda';
   $pass = 'password_database_anda';
   ```

#### Opsi B: Lokal (Development dengan Laragon)
1. Salin folder `backend_api` ke `C:\laragon\www\`.
2. Buat database `fishpoint_db` di HeidiSQL/phpMyAdmin.
3. Import `fishpoint_db.sql`.
4. Sesuaikan `db.php`:
   ```php
   $host = 'localhost';
   $db   = 'fishpoint_db';
   $user = 'root';
   $pass = '';
   ```

### 2. Setup Android App
1. Buka project menggunakan **Android Studio**.
2. Sesuaikan `BASE_URL` di file `FishPointApi.kt`:
   ```kotlin
   // Untuk hosting online:
   private const val BASE_URL = "https://domain-anda.com/backend_api/"

   // Untuk emulator Android Studio (Laragon):
   private const val BASE_URL = "http://10.0.2.2/backend_api/"

   // Untuk real device (WiFi lokal, ganti dengan IP komputer Anda):
   private const val BASE_URL = "http://192.168.x.x/backend_api/"
   ```
3. Lakukan **Sync Project with Gradle Files**.
4. Klik **Run ▶️** untuk install ke perangkat/emulator.

---

## 🔑 Akun Default

Setelah import database, Anda bisa langsung login dengan akun berikut:

| Username | Password | Role |
| :--- | :--- | :--- |
| `admin` | `123456` | Admin |
| `user1` | `123456` | Member |

---

## 👥 Tim Pengembang

<table>
  <tr>
    <td align="center"><strong>Andradhi Bondan P.</strong><br/><sub>Backend Developer</sub></td>
    <td align="center"><strong>Ataa Arkan Tsany</strong><br/><sub>Database & QA/Tester</sub></td>
    <td align="center"><strong>Imam Dian F.</strong><br/><sub>Frontend Developer</sub></td>
    <td align="center"><strong>Salman Abdussalam</strong><br/><sub>Backend Developer</sub></td>
  </tr>
</table>

---

<p align="center">
  <img src="assets/logo_fishpoint.png" alt="FishPoint" width="40"/>
  <br/>
  <strong>FishPoint</strong> © 2026 — <em>Catch the best!</em> 🎣
</p>
