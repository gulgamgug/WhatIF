# 📋 Development Plan: WhatIF (Intermittent Fasting Tracker)

Aplikasi tracking intermittent fasting modern berbasis Android Jetpack Compose dengan Material 3 Expressive, Predictive Back Gesture, Google Sans font, pencatatan mokel harian, kalender bulanan, ekspor/impor database lokal, dan notifikasi sistem.

---

## 🎯 Aturan Kerja & Komitmen Git
- **Atomic Commits**: Setiap penambahan fitur, perubahan komponen, atau perbaikan bug harus di-commit secara terpisah dengan pesan commit yang jelas dan deskriptif.
- **Sprint-by-Sprint Execution**: Pengembangan dikerjakan secara berurutan per sprint untuk memastikan setiap modul teruji dan stabil sebelum melangkah ke tahap berikutnya.
- **Status Tracking**: Dokumen ini diperbarui secara berkala dengan status progress tiap sprint (`[ ] Pending`, `[🔄] In Progress`, `[✅] Completed`).

---

## 🚀 Rencana Sprint (Total: 17 Sprint)

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

### [✅] Sprint 6: Kalender Bulanan & Detail Mokel
- Kalender interaktif bulanan (`CalendarScreen`) dengan navigasi antar bulan (`< Bulan >`).
- Indikator status tiap tanggal:
  - 🟢 **Berhasil IF**: hari di mana puasa berjalan tanpa mokel.
  - 🔴/🟠 **Ada Mokel**: hari di mana terdapat catatan mokel (disertai badge jumlah mokel).
  - ⚪ **Hari ini / Netral**: penanda hari saat ini dengan border aksen.
- Interaksi klik tanggal:
  - Membuka `DayDetailBottomSheet` dengan dukungan predictive back gesture.
  - Rincian status puasa pada tanggal tersebut (Berhasil IF vs Ada Mokel).
  - Daftar lengkap mokel pada tanggal tersebut (jam kejadian, makanan, catatan).
  - Opsi tambah / hapus mokel langsung dari detail tanggal.

### [✅] Sprint 7: Profil Pengguna & Ekspor/Impor Database Lokal
- Form profil sederhana: ubah dan simpan nama pengguna.
- Pengaturan Appearance: toggle Material You Dynamic Theme vs Expressive Theme.
- Fitur **Ekspor Database**:
  - Mengekspor seluruh konfigurasi jadwal, profil, dan riwayat mokel ke file JSON menggunakan Storage Access Framework (`ActivityResultContracts.CreateDocument`).
  - Opsi salin langsung JSON ke clipboard untuk pencadangan cepat.
- Fitur **Impor Database**:
  - Membaca file backup JSON menggunakan SAF (`ActivityResultContracts.OpenDocument`) atau tempel teks JSON secara aman dengan validasi format.
  - Pemulihan seluruh data aplikasi secara atomik ke local storage.
- Opsi reset data ke kondisi awal dengan dialog konfirmasi aman.

### [✅] Sprint 8: Notifikasi Sistem Pengingat Puasa & Makan
- Setup Notification Channel Android (High Priority `whatif_fasting_channel`).
- Penjadwalan notifikasi menggunakan `AlarmManager` & `BroadcastReceiver` (`FastingScheduler` & `FastingAlarmReceiver`):
  - Notifikasi saat **Waktunya Puasa Dimulai** 🌙 (menampilkan jam target selesai).
  - Notifikasi saat **Waktunya Boleh Makan Dibuka** 🥗 (menampilkan jam mulai puasa kembali).
  - Penjadwalan otomatis berulang tiap hari dan saat reboot device (`BOOT_COMPLETED`).
- Penanganan izin runtime `POST_NOTIFICATIONS` untuk Android 13+.
- Tombol uji coba pengiriman notifikasi instan di menu Profil.

### [✅] Sprint 9: Polish, Predictive Back Validation, Edge Cases & Verification
- Validasi Predictive Back Gesture di semua screen, modal, dan bottom sheet (`ModalBottomSheet` built-in predictive swipe, `BackHandler` navigasi kembali ke Home sebelum keluar aplikasi).
- Pengujian kasus edge case: jam puasa melewati pergantian hari (lintas tengah malam misal 20:00 - 12:00), pergantian jam sama, transisi menit persis, dan validasi data kosong (`FastingCalculatorTest` 100% passed).
- Final build assembly (`./gradlew assembleDebug` sukses menghasilkan `app-debug.apk`).
- Dukungan penuh Material You Dynamic Theming di Android 12+.

### [✅] Sprint 10: Variable Fonts Integration (Google Sans Flex Universal)
- **Problem Resolution**: Sebelumnya seluruh weight font tampak seragam/identik karena resource font TTF variable tidak di-interpolasi axis `wght`-nya oleh engine teks Compose jika hanya memakai `FontWeight` standar tanpa axis settings.
- **Universal Typography Migration**: Mengganti seluruh hierarki tipografi aplikasi (Display, Headline, Title, Body, Label) menjadi font variable TrueType **Google Sans Flex** (`google_sans_flex.ttf`, wght 100 - 1000) untuk tampilan modern, konsisten, dan sangat nyaman dibaca.
- **Fine-grained Weight Interpolation**: Menggunakan `FontVariation.Settings(FontVariation.weight(...))` untuk memastikan interpolasi sumbu bobot benar-benar aktif di engine teks Compose:
  - `Light`: 300
  - `Normal`: 400
  - `Medium`: 500
  - `SemiBold`: 600
  - `Bold`: 750
  - `ExtraBold`: 850
  - `Black`: 950
- **Timer Countdown Typography**: Penyesuaian `CircularFastingTimer` dengan `GoogleSansFlexFontFamily` dan weight ExtraBold (850) berukuran 38sp agar pembacaan sisa waktu puasa/makan kontras, tebal, dan sangat jelas terbaca.
- **Cleanup**: Menghapus resource Playfair Display yang tidak lagi digunakan untuk menjaga ukuran APK tetap ramping.
- Verifikasi kompilasi dan uji coba unit test (`./gradlew testDebugUnitTest` dan `./gradlew assembleDebug` 100% sukses).

### [✅] Sprint 11: Tab Navigation Slide Animation (Snappy EaseOut 200ms / 75ms)
- **Directional Slide Motion**: Menerapkan animasi transisi geser horizontal (`slideIntoContainer` & `slideOutOfContainer`) saat berpindah tab antar Beranda, Kalender, dan Profil pada `AnimatedContent`.
- **Direction Awareness**: Arah geser ditentukan secara natural berdasarkan ordinal tab:
  - Navigasi ke kanan (misal Beranda -> Kalender atau Kalender -> Profil): halaman baru bergeser masuk dari kanan ke kiri (`SlideDirection.Left`).
  - Navigasi ke kiri (misal Profil -> Kalender atau kembali ke Beranda via back gesture): halaman baru bergeser masuk dari kiri ke kanan (`SlideDirection.Right`).
- **Snappy Timing & EaseOut Easing**: Dioptimalkan dari 500ms menjadi transisi yang jauh lebih cepat, gesit, dan responsif:
  - Total durasi transisi: **200ms**.
  - Kurva **`EaseOut`** (tanpa delay akselerasi awal ease-in).
  - Layar aktif yang keluar (`slideOutOfContainer`) bergeser keluar cepat hanya dalam **75ms** dengan `EaseOut`.
  - Layar tujuan yang masuk (`slideIntoContainer`) meluncur masuk dan mendarat mulus dalam total durasi **200ms** dengan `EaseOut`.
- Verifikasi kompilasi dan kelulusan build (`./gradlew testDebugUnitTest` dan `./gradlew assembleDebug` sukses).

### [✅] Sprint 12: Zero-Lag WhatsApp-Style Tab Navigation (HorizontalPager & Offscreen Pre-rendering)
- **Root Cause Analysis (Stutter & 300ms Delay)**: `AnimatedContent` sebelumnya men-dispose layar aktif dan baru me-layout/me-render layar tujuan dari nol persis saat tombol tab ditekan pada UI main thread, menyebabkan frame drop (*jank*) dan jeda render ~300ms.
- **HorizontalPager Architecture**: Mengganti `AnimatedContent` dengan `HorizontalPager` (`androidx.compose.foundation.pager.HorizontalPager`) yang didukung hardware-accelerated GPU layer translation, sama persis seperti arsitektur tab WhatsApp/ViewPager.
- **Offscreen Pre-rendering (`beyondViewportPageCount = 1`)**: Layar adjacent (Beranda, Kalender, Profil) telah siap dikomposisikan dan di-layout di memori, sehingga saat tab di-tap atau digeser, tidak ada beban inflasi layout dadakan di main thread.
- **Instant Touch Feedback**: State seleksi tab indikator mengamati `targetPage` saat `isScrollInProgress` aktif, sehingga indikator tab menyala secara instan pada milidetik pertama saat disentuh.
- **Gesture Swipe Support**: Selain tap navigasi instan via `pagerState.animateScrollToPage(...)`, pengguna kini dapat langsung melakukan gesture geser/swipe jari antar tab secara natural 1:1 seperti WhatsApp.
- **Predictive Back Integration**: Tetap mendukung `BackHandler` mulus yang menggeser kembali ke Beranda jika tombol back ditekan di tab Kalender/Profil.
- Verifikasi penuh (`./gradlew testDebugUnitTest` dan `./gradlew assembleDebug` 100% sukses).

### [✅] Sprint 13: Vertical Scrollable Calendar & True-to-Database Fasting History
- **Vertical Scrollable Calendar Timeline**:
  - Mengubah tampilan kalender dari navigasi bulan tunggal menjadi daftar bulan yang dapat di-scroll vertikal secara kontinu (`LazyColumn`) dimulai langsung dari Bulan Ini hingga 24 bulan ke belakang (menghapus bulan masa depan `+1` yang tidak relevan).
  - Setiap bulan disajikan dalam Card dengan header nama bulan, badge ringkasan bulanan, dan grid hari.
  - Tombol aksi melayang *"Kembali ke Bulan Ini"* yang otomatis muncul ketika pengguna melakukan scroll jauh ke masa lalu.
- **True to Database Fasting Status**:
  - Memperbaiki logika status kalender agar benar-benar merefleksikan data di database: saat aplikasi baru diinstal, tanggal-tanggal terdahulu **TIDAK lagi otomatis dianggap berhasil IF** (sebelumnya menampilkan dot hijau palsu).
  - Menambahkan entitas `completedFastDates` pada `WhatIFData` yang disimpan dan diekspor/diimpor secara aman via JSON database.
  - Tanggal tanpa catatan di database kini berstatus netral (**Tanpa Data / Belum Ada Catatan ⚪**) tanpa dot hijau maupun merah.
  - Tanggal dengan mokel tetap berstatus **Ada Mokel 🍩** (warna coral dengan badge jumlah mokel).
  - Pengguna dapat menandai tanggal sebagai **Berhasil Puasa IF 🌟** atau membatalkannya langsung melalui `DayDetailBottomSheet`.
- Unit test baru `CalendarDataModelTest` untuk memvalidasi inisialisasi state bersih, pengelompokan tanggal mokel, dan mekanisme toggle puasa berhasil.
- Verifikasi kompilasi dan kelulusan unit test 100% sukses.

### [✅] Sprint 14: Auto-Complete Fasting Recording & Background Reliability (OneUI / HyperOS)
- **Automatic Fasting Completion Trigger**:
  - Saat jadwal puasa berakhir dan jendela makan dibuka (`ACTION_EAT_START`), `FastingAlarmReceiver` secara otomatis memeriksa apakah ada catatan mokel hari ini.
  - Jika tidak ada mokel (*clean fast*), tanggal hari ini otomatis didaftarkan ke `completedFastDates` di database lokal dan notifikasi perayaan kesuksesan puasa langsung dikirimkan.
  - Saat aplikasi dibuka (`WhatIFViewModel.checkAndAutoCompleteFasting`), sistem memvalidasi apakah fase aktif adalah `EATING` tanpa catatan mokel dan otomatis mencatat status berhasil tanpa perlu input manual.
  - Jika pengguna mencatat mokel kemudian hari, `addMokel` secara otomatis menganulir status berhasil hari itu dan mengalihkannya menjadi *Ada Mokel*.
- **Background Execution Reliability (OneUI / HyperOS)**:
  - Penambahan izin `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` di `AndroidManifest.xml`.
  - Penambahan tombol integrasi *"Izin Latar Belakang (OneUI / HyperOS)"* di menu Profil untuk memandu dan membuka pengaturan baterai agar aplikasi tidak dihentikan paksa oleh manajemen baterai agresif OEM (Samsung Sleeping Apps / Xiaomi MIUI Autostart & Battery Saver).
- Penambahan unit test `testMokelInvalidatesCompletedFastDate` di `CalendarDataModelTest`.
- Verifikasi kompilasi dan kelulusan unit test 100% sukses.

### [✅] Sprint 15: Multi-Size Responsive Home Screen AppWidget (2x1, 2x2, 2x3, 3x2, 3x3)
- **Responsive Sizing Matrix**:
  - Implementasi Android AppWidget menggunakan `AppWidgetProvider` dan API responsive `RemoteViews(Map<SizeF, RemoteViews>)` standar Android 12+ (minSdk 31).
  - Mendukung 5 variasi ukuran grid launcher secara adaptif:
    - **2x1**: Baris ringkas horizontal (Ikon fase, status "Waktu Puasa/Makan", hitung mundur live, target selesai).
    - **2x2**: Kartu persegi seimbang standar (Badge chip fase, countdown besar, info target jam, badge mokel).
    - **2x3**: Kartu vertikal tinggi (Header branding, chip fase, countdown besar, ringkasan jam mulai-selesai, badge mokel).
    - **3x2**: Kartu lebar medium (Kolom kiri untuk status fase & countdown, kolom kanan untuk detail jadwal IF & badge mokel).
    - **3x3**: Dashboard luas lengkap (Header status, hero countdown berukuran 34sp, jadwal puasa, badge mokel hari ini).
- **Battery-Friendly Live Countdown via OS Chronometer**:
  - Menggunakan `Chronometer` dengan `setChronometerCountDown(true)` yang di-render langsung oleh SystemUI OS.
  - Ticking setiap detik secara presisi tanpa perlu background service yang boros baterai atau memicu wakelock.
- **Dynamic Theming & Visual States**:
  - Menyesuaikan warna latar belakang dan ikon secara otomatis: Dark Indigo (`#18182E`) untuk fase puasa 🌙 dan Dark Emerald (`#0A2A20`) untuk jendela makan 🥗.
  - Badge catatan mokel hari ini dengan aksen Rose/Coral (`#FB7185`).
  - Ketuk (*tap*) pada area widget langsung membuka aplikasi WhatIF (`MainActivity`).
- **Real-time Widget Synchronization**:
  - Widget diperbarui secara instan saat jadwal diubah, saat mokel dicatat/dihapus, saat notifikasi alarm berbunyi, dan saat perangkat selesai boot.
- Penambahan unit test `AppWidgetLogicTest` untuk memverifikasi kalkulasi milidetik target countdown widget.
- Verifikasi penuh (`./gradlew testDebugUnitTest` dan `./gradlew assembleDebug` 100% sukses).

### [✅] Sprint 16: Fix AppWidget Inflation Crash, Text Overlap & Chronometer Epoch Offset Bug
- **Fix "Couldn't add widget" Crash (Width > 1)**:
  - Mengidentifikasi akar masalah crash: penggunaan tag `<Space>` pada layout `2x2`, `2x3`, `3x2`, dan `3x3` yang secara ketat dilarang dalam Android `RemoteViews`, menyebabkan `android.view.InflateException: Class not allowed to be inflated in RemoteViews: android.widget.Space` saat launcher mencoba me-render ukuran tersebut.
  - Menghapus seluruh tag `<Space>` dan menggantikannya dengan `android:layout_marginTop` / `android:layout_marginBottom` standar pada elemen, serta mengganti tag `<View>` dengan `<FrameLayout>`.
- **Fix Text Overlapping (Size 2x1)**:
  - Mengubah struktur layout `widget_fasting_2x1.xml` dari `RelativeLayout` dengan batas longgar menjadi horizontal `LinearLayout` berbobot (`layout_weight="1"` untuk kolom kiri teks, dan `wrap_content` untuk countdown timer kanan).
  - Menambahkan pembatasan `singleLine="true"` dan `ellipsize="end"` agar teks nama fase dan jam target tidak saling menumpuk dengan timer saat ukuran widget sempit (2 kolom).
- **Fix 498,185 Hours Timer Display Bug**:
  - Mengidentifikasi akar masalah timer aneh (498185 jam): Android `Chronometer` menghitung waktu berbasis `SystemClock.elapsedRealtime()` (uptime milidetik sejak perangkat booting), BUKAN `System.currentTimeMillis()` (Unix epoch milidetik sejak 1970). Penggunaan epoch menghasilkan selisih ~1.79 triliun milidetik (~498.185 jam).
  - Memperbaiki kalkulasi base waktu pada `FastingAppWidgetProvider.kt`:
    `chronometerBase = SystemClock.elapsedRealtime() + (status.secondsRemaining * 1000L)`
    sehingga hitung mundur jam, menit, dan detik berjalan normal dan akurat (misal: 05:29:40 -> 05:29:39).
- **Options Change Lifecycle Listener**:
  - Menambahkan override `onAppWidgetOptionsChanged` pada `FastingAppWidgetProvider` agar widget diperbarui secara instan saat pengguna me-resize widget di home screen.
- Penyesuaian unit test pada `AppWidgetLogicTest`.
- Verifikasi penuh (`./gradlew testDebugUnitTest` dan `./gradlew assembleDebug` 100% sukses).

### [✅] Sprint 17: Developer About Dialog & Open Source Licenses
- **Host PC & Developer Info Injection via BuildConfig**:
  - Mengambil identitas nama host PC pengembang (`cachyos-x8664`) dan username pengembang (`raihanlazuardi`) langsung dari environment mesin.
  - Mengaktifkan `buildFeatures.buildConfig = true` di `app/build.gradle.kts` serta menginjeksi `buildConfigField`:
    - `BUILD_HOST = "cachyos-x8664"`
    - `DEVELOPER_NAME = "raihanlazuardi"`
- **Data Model Lisensi Pihak Ketiga (`OpenSourceLicense.kt`)**:
  - Menyusun katalog pustaka sumber terbuka yang digunakan aplikasi (AndroidX Jetpack Compose, Material 3 Expressive, Material Icons Extended, Adaptive Navigation Suite, Core KTX, Lifecycle & ViewModel Compose, Activity Compose, Kotlin Standard Library & Coroutines, Google Sans Flex Variable Font, dan JUnit 4).
  - Menyertakan ringkasan teks lisensi resmi (Apache License 2.0, SIL Open Font License 1.1, dan Eclipse Public License 1.0).
- **Komponen UI Modern Dialog About & Lisensi (`AboutComponents.kt`)**:
  - `AboutDialog`: Menampilkan ikon WhatIF, versi aplikasi (1.0 Build 1), badge arsitektur modern (Compose, M3 Expressive, Material You), kartu informasi developer (`raihanlazuardi`), host mesin pengembang (`cachyos-x8664`), jaminan privasi 100% offline lokal, serta tombol cepat ke lisensi open source.
  - `OpenSourceLicensesDialog`: Menampilkan daftar scrollable pustaka open source lengkap dengan badge lisensi, deskripsi, dan teks lisensi yang dapat di-expand/collapse secara interaktif.
- **Integrasi di Layar Profile (`ProfileScreen.kt`)**:
  - Menambahkan bagian *"Informasi & Lisensi"* di tab Pengaturan/Profil dengan dua kartu navigasi interaktif: *"Tentang WhatIF"* dan *"Open Source Licenses"*.
- **Unit Testing & Verifikasi**:
  - Penambahan `AboutAndLicenseTest.kt` untuk memverifikasi keutuhan BuildConfig pengembang/host dan kelengkapan katalog metadata lisensi pustaka.
  - Build dan test suite (`./gradlew testDebugUnitTest assembleDebug`) 100% sukses.
