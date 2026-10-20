# Android PaintApp
Aplikacja mobilna na platformę Android umożliwiająca swobodne rysowanie na dedykowanej powierzchni graficznej oraz przeglądanie zapisanych obrazów z pamięci urządzenia w dedykowanej galerii opartej na architekturze Master-Detail.

---

## Spis treści
1. [Funkcjonalności](#funkcjonalności)
2. [Architektura i technologie](#architektura-i-technologie)
3. [Struktura modułów](#struktura-modułów)
4. [Wymagania i uprawnienia](#wymagania-i-uprawnienia)
5. [Instalacja i uruchomienie](#instalacja-i-uruchomienie)
6. [Instrukcja użytkowania](#instrukcja-użytkowania)
7. [Ograniczenia i stan implementacji](#ograniczenia-i-stan-implementacji)

---

## Funkcjonalności

### 1. Panel rysowania (`MainActivity` & `PowierzchniaRysunku`)
* **Rysowanie wielowątkowe**: Silnik renderowania oparty na `SurfaceView` i dedykowanym wątku roboczym (`Runnable`) odświeżającym kanwę w pętli (częstotliwość ~25 FPS).
* **Wybór kolorów**:
  * Czerwony (`Color.RED`)
  * Seledynowy (`Color.rgb(172, 225, 175)`)
  * Różowy (`Color.rgb(255, 204, 221)`)
  * Niebieski (`Color.BLUE`)
* **Czyszczenie płótna**: Przycisk resetujący stan bufora bitmapy do jednolitego białego tła (`Color.WHITE`) i usuwający dotychczasowe ścieżki.
* **Zapisywanie rysunku**:
  * Eksport bieżącego stanu bufora bitmapy do formatu **PNG** (jakość 100%).
  * Generowanie unikalnej nazwy pliku na podstawie daty systemowej: `IMG_yyyyMMdd_HHmmss.png`.
  * Integracja z `MediaStore.Images.Media` – obsługa flagi `IS_PENDING` dla systemów Android 10+ (API 29+).

### 2. Przeglądarka zapisanych obrazów (`BrowseImage`)
* **Pobieranie plików**: Odczyt identyfikatorów oraz nazw obrazów z zewnętrznej pamięci masowej (`MediaStore.Images.Media.EXTERNAL_CONTENT_URI`) za pomocą `ContentResolver`.
* **Lista elementów (`FragmentList`)**: Wyświetlanie listy miniaturek/danych obiektów w komponencie `RecyclerView`.
* **Podgląd szczegółów (`FragmentDetails`)**: Prezentacja wybranego pliku graficznego za pośrednictwem `ImageView` na podstawie skonstruowanego `Uri`.
* **Responsywność i adaptacyjny układ UI**:
  * **Układ pionowy (Portrait)**: Wybór elementu z listy ukrywa listę (`View.GONE`) i prezentuje szczegóły w pełnym oknie z obsługą stosu cofania (`BackStack` / `onBackPressed`).
  * **Układ poziomy (Landscape)**: Możliwość jednoczesnego wyświetlania listy oraz podglądu (układ Master-Detail).

---

## Architektura i technologie

* **Język**: Java
* **Platforma bazowa**: Android SDK (AndroidX, AppCompat)
* **Kluczowe komponenty**:
  * `SurfaceView` + `SurfaceHolder.Callback`: Niezależne renderowanie grafiki 2D poza głównym wątkiem UI (brak zacięć interfejsu).
  * `Canvas`, `Paint`, `Path`: Narzędzia graficzne do rysowania krzywych wektorowych i rasteryzacji.
  * `ContentResolver` / `MediaStore API`: Bezpieczna komunikacja z magazynem multimediów systemu Android.
  * `Fragments API` & `FragmentManager`: Zarządzanie dynamicznymi widokami i transakcjami ekranów.
  * `RecyclerView` + `Adapter` + `ViewHolder`: Wydajna prezentacja kolekcji danych.
  * `Parcelable`: Serializacja modelu danych `Image` oraz wektorów `ColoredPath`.

---

## Struktura modułów

```text
com.example.nazwaprojektu4/
├── MainActivity.java        # Główny ekran aplikacji, pasek narzędzi, obsługa uprawnień i zapisu
├── PowierzchniaRysunku.java  # Niestandardowy widok SurfaceView, pętla wątku rysującego, obsługa dotyku
├── BrowseImage.java         # Aktywność przeglądarki plików (zarządzanie fragmentami i orientacją)
├── FragmentList.java        # Fragment pobierający kolekcję z MediaStore i obsługujący RecyclerView
├── FragmentListAdapter.java # Adapter wiążący dane obrazów z widokami wierszy listy
├── FragmentDetails.java     # Fragment prezentujący szczegółowy podgląd wybranego obrazu
└── Image.java               # Model danych reprezentujący pojedynczy plik graficzny (Parcelable)
```

---

## Wymagania i uprawnienia

### Wymagane uprawnienia (`AndroidManifest.xml`)
* `android.permission.READ_EXTERNAL_STORAGE` – wymagane dla wersji poniżej Android 10 (API < 29) do odczytu listy i miniatur obrazów.
* `android.permission.WRITE_EXTERNAL_STORAGE` – wymagane dla wersji poniżej Android 10 (API < 29) do zapisu plików na dysku.
* *Dla wersji Android 10 (API 29) i nowszych aplikacja wykorzystuje model Scoped Storage za pośrednictwem MediaStore API.*

---

## Instalacja i uruchomienie

1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/TomaszMarek/Android-PaintApp.git
   ```
2. Otwórz projekt w środowisku **Android Studio** (wersja Hedgehog / Iguana lub nowsza).
3. Poczekaj na zakończenie synchronizacji Gradle (`Sync Project with Gradle Files`).
4. Uruchom aplikację na emulatorze lub fizycznym urządzeniu z systemem Android (zalecane API 26+):
   * Skrót: `Shift + F10` lub kliknięcie ikony **Run**.

---

## Instrukcja użytkowania

1. **Rysowanie**: Dotknij ekranu i przeciągnij palcem po powierzchni `PowierzchniaRysunku`.
2. **Zmiana koloru**: Użyj kolorowych przycisków umieszczonych na dole ekranu (czerwony, seledynowy, różowy, niebieski).
3. **Czyszczenie**: Naciśnij czarny przycisk (`xBtn`), aby zresetować zawartość ekranu do pustego arkusza.
4. **Zapis do galerii**: Rozwiń menu górnego paska zadań (`Toolbar`) i wybierz opcję **Zapisz** (`R.id.saveImage`). Rysunek zostanie zapisany w pamięci urządzenia.
5. **Przeglądanie galerii**: W menu paska zadań wybierz opcję **Przeglądaj** (`R.id.browseImage`). Zostaniesz przekierowany do widoku listy plików. Kliknięcie dowolnej pozycji wyświetli pełny podgląd obrazu.

---

## Ograniczenia i stan implementacji

* **Grubość pędzla**: Zdefiniowana statycznie na poziomie 5 pikseli (`mFarba.setStrokeWidth(5)`), brak suwaka płynnej regulacji.
* **Przywracanie stanu po obrocie ekranu**: Metody `restorePaths` oraz obsługa odtwarzania ścieżek z `savedInstanceState` w `MainActivity` i `PowierzchniaRysunku` są zakomentowane w kodzie źródłowym – obrót ekranu podczas rysowania może skutkować utratą niezapisanego szkicu.
* **Nazwa pakietu**: Kod korzysta z domyślnej nazwy bazowej `com.example.nazwaprojektu4`.
