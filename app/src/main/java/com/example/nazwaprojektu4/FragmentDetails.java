package com.example.nazwaprojektu4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FragmentListAdapter extends RecyclerView.Adapter<FragmentListAdapter.ImagesViewHolder> {
    private List<Image> mListPictures;
    private final LayoutInflater mInflater;
    private final OnImageClickListener imageClickListener;

    public interface OnImageClickListener {
        void onItemClick(Image image);
    }

    public static class ImagesViewHolder extends RecyclerView.ViewHolder {
        TextView mIdPhoto;
        TextView mNamePhoto;

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            mIdPhoto = itemView.findViewById(R.id.imageId);
            mNamePhoto = itemView.findViewById(R.id.imageName);
        }

        public void bind(final Image image, final OnImageClickListener listener) {
            mIdPhoto.setText(String.valueOf(image.id));
            mNamePhoto.setText(image.name);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(image);
                }
            });
        }
    }

    public FragmentListAdapter(List<Image> listPictures, OnImageClickListener imageClickListener, Context context) {
        this.mListPictures = listPictures;
        this.imageClickListener = imageClickListener;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.imagedate, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        holder.bind(mListPictures.get(position), imageClickListener);
    }

    @Override
    public int getItemCount() {
        return mListPictures != null ? mListPictures.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setmListPictures(List<Image> imageList) {
        this.mListPictures = imageList;
        notifyDataSetChanged();
    }
}
