package com.example.nazwaprojektu4;

import static android.content.ContentValues.TAG;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentList extends Fragment {
    private infoFragmentActivityListener listener; //
    private RecyclerView fragmentRecyclerView; //referencja do widoku
    private FragmentListAdapter  mImageListAdapter; //referencja do adaptera
    private List<Image> mImageList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container,false); //tworzenie widoku z latoutu
        fragmentRecyclerView = view.findViewById(R.id.imageRecyclerView);
        fragmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mImageList = new ArrayList<>(); //inicjalizacja nowej puste listy

        mImageListAdapter = new FragmentListAdapter(mImageList, new FragmentListAdapter.OnImageClickListener() {
            @Override
            public void onItemClick(Image image) {
                if (listener != null) {
                    listener.onItemSelected(image);
                }
            }
        }, getContext());

        fragmentRecyclerView.setAdapter(mImageListAdapter);

        getFileList(); //pobranie listy plikow
        return view;
    }

    public interface infoFragmentActivityListener {
        void onItemSelected(Image image);
    }


    //ywoływana, gdy fragment jest dołączany do aktywności
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof infoFragmentActivityListener) {
            listener = (infoFragmentActivityListener) context;
        } else {
            throw new ClassCastException(context.toString() + " musi implementować interfejs infoFragmentActivityListener");
        }
    }

    private void getFileList() {
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
        };
        String selection = null;
        String[] selectionArgs = null;

        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";
        try (Cursor cursor = getContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

                List<Image> imageList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageList.add(new Image(id, name));
                    Image image = new Image(id, name);
                    mImageList.add(image);
                }
                mImageListAdapter.setmListPictures(mImageList);
            }
        }
    }


}
