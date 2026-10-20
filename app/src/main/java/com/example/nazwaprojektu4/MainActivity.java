package com.example.nazwaprojektu4;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;

    private PowierzchniaRysunku powierzchniaRysunku;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        powierzchniaRysunku = findViewById(R.id.powierzchnia_rysunku);

        View redBtn = findViewById(R.id.redBtn);
        View celadonBtn = findViewById(R.id.celadonBtn);
        View pinkBtn = findViewById(R.id.pinkBtn);
        View blueBtn = findViewById(R.id.blueBtn);
        View xBtn = findViewById(R.id.xBtn);

        redBtn.setBackgroundColor(Color.RED);
        celadonBtn.setBackgroundColor(Color.rgb(172, 225, 175));
        pinkBtn.setBackgroundColor(Color.rgb(255, 204, 221));
        blueBtn.setBackgroundColor(Color.BLUE);
        xBtn.setBackgroundColor(Color.BLACK);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Malowanki TM");
        }

        redBtn.setOnClickListener(v -> powierzchniaRysunku.setKolorRysowania(Color.RED));
        celadonBtn.setOnClickListener(v -> powierzchniaRysunku.setKolorRysowania(Color.rgb(172, 225, 175)));
        pinkBtn.setOnClickListener(v -> powierzchniaRysunku.setKolorRysowania(Color.rgb(255, 204, 221)));
        blueBtn.setOnClickListener(v -> powierzchniaRysunku.setKolorRysowania(Color.BLUE));
        xBtn.setOnClickListener(v -> powierzchniaRysunku.wyczyscRysunek());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveImage) {
            saveImagePermissions();
            return true;
        } else if (id == R.id.browseImage) {
            browseImagesPermissions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void browseImagesPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            browseImages();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                browseImages();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void saveImagePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImage();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void saveImage() {
        Bitmap bitmapToSave = powierzchniaRysunku.getmBitmapa();
        if (bitmapToSave == null) {
            Toast.makeText(this, "Brak rysunku do zapisania", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmapCopy;
        synchronized (powierzchniaRysunku) {
            bitmapCopy = bitmapToSave.copy(bitmapToSave.getConfig(), false);
        }

        executorService.execute(() -> {
            ContentResolver resolver = getApplicationContext().getContentResolver();
            Uri imageCollection;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else {
                imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".png";

            ContentValues imageDetails = new ContentValues();
            imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            imageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
            }

            Uri imageUri = resolver.insert(imageCollection, imageDetails);
            boolean success = false;

            if (imageUri != null) {
                try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(imageUri, "w", null);
                     FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {
                    bitmapCopy.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    success = true;
                } catch (IOException e) {
                    resolver.delete(imageUri, null, null);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && success) {
                    imageDetails.clear();
                    imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(imageUri, imageDetails, null, null);
                }
            }

            final boolean finalSuccess = success;
            runOnUiThread(() -> {
                if (finalSuccess) {
                    Toast.makeText(MainActivity.this, "Rysunek został zapisany pomyślnie", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Błąd podczas zapisu rysunku", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
                browseImages();
            } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
                saveImage();
            }
        } else {
            Toast.makeText(this, "Wymagane uprawnienie nie zostało przyznane", Toast.LENGTH_SHORT).show();
        }
    }

    private void browseImages() {
        Intent intent = new Intent(this, BrowseImage.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
