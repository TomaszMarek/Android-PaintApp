package com.example.nazwaprojektu4;

import android.app.Activity;
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
import android.os.Parcelable;
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
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final String TAG = "MainActivity";

    private PowierzchniaRysunku powierzchniaRysunku;
    private int obecnyKolor = Color.RED;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        powierzchniaRysunku = findViewById(R.id.powierzchnia_rysunku);

        if (savedInstanceState != null) {
            obecnyKolor = savedInstanceState.getInt("obecnyKolor", Color.RED);
            ArrayList<Parcelable> paths = savedInstanceState.getParcelableArrayList("paths");
            if (paths != null) {
//                powierzchniaRysunku.restorePaths(paths);
            }
        }

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
        getSupportActionBar().setTitle("Malowanki TM");


        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powierzchniaRysunku.setKolorRysowania(Color.RED);
            }
        });

        celadonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powierzchniaRysunku.setKolorRysowania(Color.rgb(172, 225, 175));
            }
        });

        pinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powierzchniaRysunku.setKolorRysowania(Color.rgb(255, 204, 221));
            }
        });

        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powierzchniaRysunku.setKolorRysowania(Color.BLUE);
            }
        });

        xBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powierzchniaRysunku.wyczyscRysunek();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu, menu);
        return true;
    }




    private void browseImagesPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            browseImages();
        }else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                browseImages();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveImage) {
            Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show();
            saveImagePermissions();
            return true;
        } else if (id == R.id.browseImage) {
            browseImagesPermissions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImagePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            saveImage();
        }else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void saveImage() {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues imageDetails = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp + ".png";
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        Uri imageUri = resolver.insert(imageCollection, imageDetails);
        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(imageUri, "w", null);
             FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {
            synchronized (powierzchniaRysunku) {
                powierzchniaRysunku.getmBitmapa().compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear();
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, imageDetails, null, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    browseImages();
                } else {
                    Toast.makeText(this, "Brak uprawnień do odczytu", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    Toast.makeText(this, "Brak uprawnień do zapisu", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void browseImages() {
        Intent intent = new Intent(this, BrowseImage.class);
        startActivity(intent);
    }

}