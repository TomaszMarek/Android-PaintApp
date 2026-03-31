//package com.example.nazwaprojektu4;
//
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//public class BrowseImage extends AppCompatActivity implements FragmentList.infoFragmentActivityListener {
// //klasa będzie odpowiadać na zdarzenia związane z wyborem elementów w FragmentLi
//    private static final String INFO_FRAGMENT_TAG = "infoFragment";
//    private static final String PICTURE_FRAGMENT_TAG = "pictureFragment";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_browse_image);
//
//        if (savedInstanceState == null) { //aktywnosc tworzona po raz pierwszy
//            FragmentManager fragmentManager = getSupportFragmentManager(); //tworzenie menagera do zarzadzania
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //pozwala na zastepowanie i dodawanie fragmentow
//
//            FragmentList imageListFragment = new FragmentList();
//            fragmentTransaction.add(R.id.infoFragment, imageListFragment, INFO_FRAGMENT_TAG);
//
//            FragmentDetails imageFragment = new FragmentDetails();
//            fragmentTransaction.add(R.id.pictureFragment, imageFragment, PICTURE_FRAGMENT_TAG);
//
//            fragmentTransaction.commit(); //zatwierdzenie transakcji, faktyczne dodanie fragmentow do widoku
//        }
//    }
//
//    @Override
//    public void onItemSelected(Image image) { //wywoływana, gdy użytkownik wybierze obrazek z listy
//        FragmentDetails imageFragment = FragmentDetails.setInstance(image); //nowa instacja, przekazanie obrazka
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        //zastepienie fragmentu nowym fragmenten.
//        transaction.replace(R.id.pictureFragment, imageFragment, PICTURE_FRAGMENT_TAG);
//        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
//            transaction.addToBackStack(null);
//            findViewById(R.id.infoFragment).setVisibility(View.GONE); // chowa liste
//            findViewById(R.id.pictureFragment).setVisibility(View.VISIBLE); // wyswietla obrazek
//        }
//
//        transaction.commit();
//    }
//
//    //wywolywana dla przycisku wstecz
//    @Override
//    public void onBackPressed() {
//        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) { //sprawdzenie orientacji pionowej
//            FragmentManager fm = getSupportFragmentManager();
//            if (fm.getBackStackEntryCount() > 0) {
//                fm.popBackStack();
//                findViewById(R.id.pictureFragment).setVisibility(View.GONE);
//                findViewById(R.id.infoFragment).setVisibility(View.VISIBLE);
//            } else {
//                //wywolywana jest domyslna czynnosc
//                super.onBackPressed();
//            }
//        } else {
//            super.onBackPressed();
//        }
//    }
//}

package com.example.nazwaprojektu4;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BrowseImage extends AppCompatActivity implements FragmentList.infoFragmentActivityListener {
    private static final String INFO_FRAGMENT_TAG = "infoFragment";
    private static final String PICTURE_FRAGMENT_TAG = "pictureFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_image);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            FragmentList imageListFragment = new FragmentList();
            fragmentTransaction.add(R.id.infoFragment, imageListFragment, INFO_FRAGMENT_TAG);

            FragmentDetails imageFragment = new FragmentDetails();
            fragmentTransaction.add(R.id.pictureFragment, imageFragment, PICTURE_FRAGMENT_TAG);

            fragmentTransaction.commit();
        }
    }

    @Override
    public void onItemSelected(Image image) {
        FragmentDetails imageFragment = FragmentDetails.setInstance(image);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pictureFragment, imageFragment, PICTURE_FRAGMENT_TAG);
        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            transaction.addToBackStack(null);
            findViewById(R.id.infoFragment).setVisibility(View.GONE);
            findViewById(R.id.pictureFragment).setVisibility(View.VISIBLE);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                findViewById(R.id.pictureFragment).setVisibility(View.GONE);
                findViewById(R.id.infoFragment).setVisibility(View.VISIBLE);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}