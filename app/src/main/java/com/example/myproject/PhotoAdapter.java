package com.example.myproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private Context context;
    private List<String> urlPhotos;
    private String userId;
    private FragmentManager manager;
    private int viewType;

    public PhotoAdapter(Context context, List<String> urlPhotos,String userId, FragmentManager manager, int viewType){
        this.context = context;
        this.urlPhotos = urlPhotos;
        this.userId = userId;
        this.manager = manager;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.photo_item,null);
        switch (viewType) {
            case PhotoHolder.VIEW_TYPE: return new  PhotoHolder(v);
            case GalleryHolder.VIEW_TYPE: return new GalleryHolder(v);
            default: throw new NullPointerException("HOLDER TYPE IS INVALID");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        holder.setUrl(urlPhotos.get(position));
        Glide.with(context).load(holder.getUrl()).into(holder.photoImage);
        holder.setI(position);
    }

    @Override
    public int getItemViewType(int position) {
        return  viewType;
    }

    @Override
    public int getItemCount() {
        return urlPhotos.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder{
        public static final int VIEW_TYPE = 0;
        private ImageView photoImage;
        private String url;
        protected int i;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.photo_item);
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    class GalleryHolder extends PhotoHolder implements View.OnLongClickListener{
        public static final int VIEW_TYPE = 1;
        private ImageView photoImage;
        private String url;
        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            photoImage=itemView.findViewById(R.id.photo_item);
            photoImage.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId()==R.id.photo_item){
                DeletePhotoDialog deletePhotoDialog = new DeletePhotoDialog(url, userId,i+1);
                deletePhotoDialog.show(manager,null);
            }
            return true;
        }
    }
}
