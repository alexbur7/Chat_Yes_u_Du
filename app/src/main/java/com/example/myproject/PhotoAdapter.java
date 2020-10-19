package com.example.myproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private Context context;
    private List<String> urlPhotos;

    public PhotoAdapter(Context context, List<String> urlPhotos){
        this.context = context;
        this.urlPhotos = urlPhotos;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.photo_item,null);
        PhotoHolder photoHolder= new PhotoHolder(v);
        return photoHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        holder.setUrl(urlPhotos.get(position));
        Glide.with(context).load(holder.getUrl()).into(holder.photoImage);
    }

    @Override
    public int getItemCount() {
        return urlPhotos.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView photoImage;
        private String url;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.photo_item);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
