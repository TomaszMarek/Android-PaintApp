package com.example.nazwaprojektu4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FragmentListAdapter extends RecyclerView.Adapter<FragmentListAdapter.ImagesViewHolder>{
    private List<Image> mListPictures;
    private LayoutInflater mPompka;
    private OnImageClickListener imageClickListener;

    public interface OnImageClickListener {
        void onItemClick(Image image);
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder{
        TextView mIdPhoto;
        TextView mNamePhoto;

        public ImagesViewHolder(@NonNull View glownyElementWiersza) {
            super(glownyElementWiersza);
            mIdPhoto = glownyElementWiersza.findViewById(R.id.imageId);
            mNamePhoto = glownyElementWiersza.findViewById(R.id.imageName);
        }

        public void bind(final Image image, final OnImageClickListener listener) {
            mIdPhoto.setText(image.name);
            mNamePhoto.setText(String.valueOf(image.id));
            itemView.setOnClickListener(v -> listener.onItemClick(image));
        }
    }

    public FragmentListAdapter(List<Image> mListPictures, OnImageClickListener imageClickListener, Context context) {
        this.mListPictures = mListPictures;
        this.imageClickListener = imageClickListener;
        this.mPompka = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View wiersz = mPompka.inflate(R.layout.imagedate, parent, false);
        return new ImagesViewHolder(wiersz);
    }
    //tworzy miejsce

    @Override
    public void onBindViewHolder(@NonNull FragmentListAdapter.ImagesViewHolder holder, int position) {
        holder.bind(mListPictures.get(position), imageClickListener);
    }
    //uzupelnia danymi, miejsce dalej istnieje i jest tym uzupelniane

    @Override
    public int getItemCount() {
        return mListPictures.size();
    }

    public void setmListPictures(List<Image> imageList) { //podmiana listy obrazow
        this.mListPictures = imageList;
        notifyDataSetChanged();
    }


}
