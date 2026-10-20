# Android PaintApp (Malowanki TM)

Aplikacja mobilna na platformę Android umożliwiająca swobodne rysowanie na dedykowanej powierzchni graficznej oraz asynchroniczne przeglądanie zapisanych obrazów z pamięci urządzenia w dedykowanej galerii opartej na architekturze Master-Detail.

---

## Spis treści
1. [Funkcjonalności](#funkcjonalności)
2. [Architektura i technologie](#architektura-i-technologie)
3. [Struktura modułów](#struktura-modułów)
4. [Wymagania i uprawnienia](#wymagania-i-uprawnienia)
5. [Instalacja i uruchomienie](#instalacja-i-uruchomienie)
6. [Instrukcja użytkowania](#instrukcja-użytkowania)
7. [Aktualny stan implementacji](#aktualny-stan-implementacji)

---

## Funkcjonalności

### 1. Panel rysowania (`MainActivity` & `PowierzchniaRysunku`)
* **Płynne renderowanie wielowątkowe**: Silnik oparty na `SurfaceView` z wątkiem roboczym synchronizowanym z cyklem życia widoku, zabezpieczonym przed wyciekami pamięci.
* **Wygładzanie krawędzi**: Zastosowanie flag `ANTI_ALIAS_FLAG` oraz `DITHER_FLAG`, a także zaokrąglonych końcówek linii (`Cap.ROUND`, `Join.ROUND`).
* **Wybór palety kolorów**:
  * Czerwony (`Color.RED`)
  * Seledynowy (`Color.rgb(172, 225, 175)`)
  * Różowy (`Color.rgb(255, 204, 221)`)
  * Niebieski (`Color.BLUE`)
* **Czyszczenie płótna**: Przycisk resetujący stan bufora bitmapy do jednolitego białego tła (`Color.WHITE`).
* **Asynchroniczny zapis rysunku**:
  * Eksport stanu płótna do formatu **PNG** wykonywany w puli wątków w tle (`ExecutorService`), co zapobiega blokowaniu interfejsu (ANR).
  * Generowanie unikalnej nazwy pliku na podstawie znacznika czasu: `IMG_yyyyMMdd_HHmmss.png`.
  * Integracja z `MediaStore API` z obsługą flagi `IS_PENDING` (Android 10+) oraz automatycznym wycofywaniem pustych wpisów w przypadku błędu zapisu.
  * Informowanie użytkownika o rezultacie zapisu za pomocą komunikatów `Toast` po zakończeniu operacji I/O.

### 2. Przeglądarka zapisanych obrazów (`BrowseImage`)
* **Asynchroniczne zapytania do pamięci**: Odczyt identyfikatorów i nazw plików z `MediaStore.Images.Media.EXTERNAL_CONTENT_URI` w osobnym wątku roboczym z sortowaniem malejącym wg daty dodania.
* **Lista elementów (`FragmentList`)**: Prezentacja listy w komponencie `RecyclerView` za pośrednictwem dedykowanego adaptera.
* **Podgląd szczegółów (`FragmentDetails`)**: Wyświetlanie wybranego obrazu na podstawie przekazanego modelu `Image` (`Parcelable`).
* **Obsługa orientacji i gestu wstecz**:
  * **Układ pionowy (Portrait)**: Wybór elementu z listy przełącza widok na pełnoekranowy podgląd; obsługa cofania zrealizowana przez `OnBackPressedCallback`.
  * **Układ poziomy (Landscape)**: Układ Master-Detail (jednoczesna prezentacja listy i szczegółów).

---

## Architektura i technologie

* **Język**: Java
* **Platforma bazowa**: Android SDK (AndroidX, AppCompat)
* **Kluczowe komponenty**:
  * `SurfaceView` + `SurfaceHolder.Callback`: Niezależne renderowanie grafiki 2D.
  * `Canvas`, `Paint`, `Path`: Zestaw narzędzi do wektorowego wykreślania ścieżek i rasteryzacji.
  * `ExecutorService`: Obsługa operacji dyskowych i bazodanowych poza głównym wątkiem UI.
  * `ContentResolver` / `MediaStore API`: Integracja z systemowym magazynem multimediów zgodnie ze Scoped Storage.
  * `Fragments API` & `OnBackPressedDispatcher`: Zarządzanie nawigacją ekranów i stosem wstecznym.
  * `RecyclerView` + `ViewHolder`: Wydajne renderowanie listy elementów.
  * `Parcelable`: Serializacja obiektów przesyłanych między fragmentami.

---

## Struktura modułów

```text
com.example.nazwaprojektu4/
├── MainActivity.java        # Główny ekran, pasek narzędzi, obsługa uprawnień i asynchroniczny zapis
├── PowierzchniaRysunku.java  # Widok SurfaceView z wątkiem rysującym i obsługą dotyku
├── BrowseImage.java         # Ekran galerii, zarządzanie fragmentami i OnBackPressedCallback
├── FragmentList.java        # Pobieranie danych z MediaStore w tle i zasilanie listy
├── FragmentListAdapter.java # Adapter wiążący dane ID i nazwy pliku z widokami wierszy
├── FragmentDetails.java     # Prezentacja szczegółowego podglądu wybranego obrazu
└── Image.java               # Model danych pliku graficznego implementujący Parcelable
```

---

## Wymagania i uprawnienia

### Uprawnienia w `AndroidManifest.xml`
* `android.permission.READ_EXTERNAL_STORAGE` (dla API < 29) – odczyt multimediów.
* `android.permission.WRITE_EXTERNAL_STORAGE` (dla API < 29) – zapis rysunków na dysku.
* *W systemach Android 10 (API 29) i nowszych aplikacja stosuje mechanizm Scoped Storage.*

---

## Instalacja i uruchomienie

1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/TomaszMarek/Android-PaintApp.git
   ```
2. Otwórz projekt w środowisku **Android Studio**.
3. Zsynchronizuj projekt z plikami Gradle (`Sync Project with Gradle Files`).
4. Uruchom aplikację na emulatorze lub fizycznym urządzeniu z systemem Android:
   * Skrót klawiszowy: `Shift + F10`.

---

## Instrukcja użytkowania

1. **Rysowanie**: Dotknij ekranu i przesuwaj palcem po obszarze roboczym.
2. **Zmiana koloru**: Wybierz jeden z przycisków kolorów na dolnym pasku.
3. **Czyszczenie**: Użyj czarnego przycisku (`xBtn`), aby zresetować płótno do czystej bieli.
4. **Zapisywanie**: W menu górnego paska wybierz opcję zapisu – po pomyślnym zapisaniu pliku pojawi się powiadomienie Toast.
5. **Przeglądanie**: Wybierz opcję przeglądania z górnego menu, aby otworzyć listę utworzonych grafik.
