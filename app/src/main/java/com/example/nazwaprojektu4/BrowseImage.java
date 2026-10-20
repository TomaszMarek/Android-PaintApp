package com.example.nazwaprojektu4;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    FragmentManager fm = getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                        View pictureView = findViewById(R.id.pictureFragment);
                        View infoView = findViewById(R.id.infoFragment);
                        if (pictureView != null) pictureView.setVisibility(View.GONE);
                        if (infoView != null) infoView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    @Override
    public void onItemSelected(Image image) {
        FragmentDetails imageFragment = FragmentDetails.setInstance(image);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pictureFragment, imageFragment, PICTURE_FRAGMENT_TAG);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            transaction.addToBackStack(null);
            View infoView = findViewById(R.id.infoFragment);
            View pictureView = findViewById(R.id.pictureFragment);
            if (infoView != null) infoView.setVisibility(View.GONE);
            if (pictureView != null) pictureView.setVisibility(View.VISIBLE);
        }
        transaction.commit();
    }
}
