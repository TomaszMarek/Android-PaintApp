package com.example.nazwaprojektu4;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDetails extends Fragment {
    private Image image;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            image = getArguments().getParcelable("image");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (image != null) {
            setImage(image);
        }
    }
    public static FragmentDetails setInstance(Image image){
        //przekazywanie danych do fragmentu
        FragmentDetails fragmentDetails = new FragmentDetails();
        Bundle bundle = new Bundle();
        bundle.putParcelable("image", image);
        fragmentDetails.setArguments(bundle);
        return fragmentDetails;

    }
    public void setImage(Image image) {
        Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(image.id));
        ImageView imageView = getView().findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);
    }
}