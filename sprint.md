# 📋 Development Plan: WhatIF (Intermittent Fasting Tracker)

Aplikasi tracking intermittent fasting modern berbasis Android Jetpack Compose dengan Material 3 Expressive, Predictive Back Gesture, Google Sans font, pencatatan mokel harian, kalender bulanan, ekspor/impor database lokal, dan notifikasi sistem.

---

## 🎯 Aturan Kerja & Komitmen Git
- **Atomic Commits**: Setiap penambahan fitur, perubahan komponen, atau perbaikan bug harus di-commit secara terpisah dengan pesan commit yang jelas dan deskriptif.
- **Sprint-by-Sprint Execution**: Pengembangan dikerjakan secara berurutan per sprint untuk memastikan setiap modul teruji dan stabil sebelum melangkah ke tahap berikutnya.
- **Status Tracking**: Dokumen ini diperbarui secara berkala dengan status progress tiap sprint (`[ ] Pending`, `[🔄] In Progress`, `[✅] Completed`).

---

## 🚀 Rencana Sprint (Total: 9 Sprint)

### [✅] Sprint 1: Setup Foundation, Material 3 Expressive & Typography
- Inisialisasi Git repository & commit initial template.
- Konfigurasi `AndroidManifest.xml` untuk Predictive Back Gesture (`android:enableOnBackInvokedCallback="true"`).
- Setup Google Sans Typography & Font Family.
- Desain Material 3 Expressive Theme: color palette (vibrant fasting & eating tones), expressive shapes (rounded pills, cards), dan dynamic motion tokens.
- Struktur navigasi dasar (Home, Kalender, Profil) dengan dukungan Predictive Back.

### [✅] Sprint 2: Data Models & Local Storage Engine
- Entitas data:
  - `FastingSchedule`: waktu mulai & selesai puasa (jam & menit).
  - `FastingPhase`: status `FASTING` (puasa) vs `EATING` (makan).
  - `MokelRecord`: id, nama makanan/minuman, timestamp, catatan opsional.
  - `UserProfile`: nama pengguna, preferensi durasi puasa.
- Repository lokal:
  - `WhatIFRepository` berbasis JSON terenkapsulasi di storage lokal app (`filesDir`).
  - reactive StateFlow untuk perubahan data otomatis di UI.
  - Kalkulator fase puasa & hitung mundur waktu ke fase berikutnya (mendukung rotasi lintas tengah malam).
- Unit test untuk kalkulasi durasi & transisi fase puasa (`FastingCalculatorTest` passed).

### [✅] Sprint 3: Home Screen & Expressive Fasting Timer
- Circular Progress Ring dengan visual expressive (animasi gradient, indicator bead, dynamic colors).
- Status fase aktif: **"WAKTU PUASA 🌙"** vs **"WAKTU MAKAN 🥗"**.
- Countdown waktu real-time: **"X jam Y menit Z detik lagi ke fase berikutnya"**.
- Kartu informasi jadwal hari ini (jam mulai, jam target selesai, durasi puasa).
- Quick Action Button: **"Catat Mokel"** (tombol pill aksen tegas) & **"Atur Jadwal"**.
- Tampilan ringkasan mokel hari ini di Home.
- Integrasi `WhatIFViewModel` dengan dukungan Material You dynamic theming.

### [✅] Sprint 4: Fasting Schedule Settings ("Atur Jam Puasa")
- Dialog / Bottom Sheet pengaturan jam puasa menggunakan Material 3 TimePicker.
- Dukungan template preset IF populer:
  - 16:8 (LeanGains - Default)
  - 18:6 (Moderate)
  - 20:4 (Warrior Diet)
  - 14:10 (Beginner)
  - Custom (bebas pilih jam mulai dan jam selesai).
- Kalkulasi otomatis durasi puasa vs jendela makan.
- Penyimpanan jadwal dan sinkronisasi instan ke timer Home via `WhatIFViewModel`.

### [✅] Sprint 5: Fitur Catat Mokel ("Pencatatan Mokel")
- Form Modal / Bottom Sheet "Catat Mokel" (`MokelLoggerBottomSheet`):
  - Input nama makanan/minuman yang dikonsumsi ("Apa yang dimakan").
  - Jam konsumsi otomatis terisi jam saat ini (`LocalTime.now()`), dapat disesuaikan dengan TimePicker.
  - Quick-tags / chips untuk input cepat (Kopi Manis, Gorengan, Boba, Camilan, Nasi, Roti).
  - Catatan tambahan opsional.
- Menampilkan daftar riwayat mokel hari ini di Home Screen secara real-time.
- Fitur hapus catatan mokel langsung dari kartu riwayat harian.

### [ ] Sprint 6: Kalender Bulanan & Detail Mokel
- Kalender interaktif bulanan dengan navigasi antar bulan (`< Bulan >`).
- Indikator status tiap tanggal:
  - 🟢 **Berhasil IF**: hari di mana puasa berjalan tanpa mokel.
  - 🔴/🟠 **Ada Mokel**: hari di mana terdapat catatan mokel (disertai badge jumlah mokel).
  - ⚪ **Hari ini / Netral**: penanda hari saat ini.
- Interaksi klik tanggal:
  - Membuka bottom sheet / dialog detail hari yang dipilih.
  - Rincian status puasa pada tanggal tersebut.
  - Daftar lengkap mokel pada tanggal tersebut (jam kejadian, makanan, catatan).
  - Opsi tambah / hapus mokel langsung dari detail tanggal.

### [ ] Sprint 7: Profil Pengguna & Ekspor/Impor Database Lokal
- Form profil sederhana: ubah dan simpan nama pengguna.
- Ringkasan statistik IF: total hari puasa, rekor streak puasa, total kejadian mokel.
- Fitur **Ekspor Database**:
  - Mengekspor seluruh konfigurasi jadwal, profil, dan riwayat mokel ke file JSON.
  - Memanfaatkan Storage Access Framework / Share Sheet agar file bisa disimpan atau dibagikan.
- Fitur **Impor Database**:
  - Membaca file backup JSON dan memulihkan seluruh data aplikasi secara aman (validasi skema).
  - Opsi reset data ke kondisi awal dengan dialog konfirmasi.

### [ ] Sprint 8: Notifikasi Sistem Pengingat Puasa & Makan
- Setup Notification Channel Android (High Priority).
- Penjadwalan notifikasi menggunakan `AlarmManager` & `BroadcastReceiver`:
  - Notifikasi saat **Waktunya Puasa Dimulai** 🌙.
  - Notifikasi saat **Waktunya Boleh Makan Dibuka** 🥗.
- Penanganan izin runtime `POST_NOTIFICATIONS` untuk Android 13+.
- Tombol uji coba pengiriman notifikasi langsung di menu Profil / Pengaturan.

### [ ] Sprint 9: Polish, Predictive Back Validation, Edge Cases & Verification
- Validasi Predictive Back Gesture di semua screen, modal, dan bottom sheet (kembali ke tab utama sebelum keluar).
- Pengujian kasus edge case: jam puasa melewati pergantian hari (lintas tengah malam misal 20:00 - 12:00), pergantian tanggal otomatis.
- Final build assembly (`./gradlew assembleDebug`) dan verifikasi kebersihan kode.
