package com.yes_u_du.zuyger.photo_utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yes_u_du.zuyger.dialog.EditPhotoDialog;
import com.yes_u_du.zuyger.R;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private Context context;
    private ArrayList<String> urlPhotos;
    private String userId;
    private FragmentManager manager;
    private int viewType;

    public PhotoAdapter(Context context, ArrayList<String> urlPhotos,String userId, FragmentManager manager, int viewType){
        this.context = context;
        this.urlPhotos = urlPhotos;
        this.userId = userId;
        this.manager = manager;
        this.viewType = viewType;
    }

    public ArrayList<String> getUrlPhotos() {
        return urlPhotos;
    }

    public void setUrlPhotos(ArrayList<String> urlPhotos) {
        this.urlPhotos = urlPhotos;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.photo_item,null);
        switch (viewType) {
            case PhotoHolder.VIEW_TYPE: return new  PhotoHolder(v);
            case GalleryHolder.VIEW_TYPE: return new GalleryHolder(v);
            case UserGalleryHolder.VIEW_TYPE: return new UserGalleryHolder(v);
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

    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public static final int VIEW_TYPE = 0;
        protected ImageView photoImage;
        protected String url;
        protected int i;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.photo_item);
            photoImage.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {

        }
    }

    public class GalleryHolder extends PhotoHolder implements View.OnLongClickListener{
        public static final int VIEW_TYPE = 1;
        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            photoImage.setOnLongClickListener(this);
            photoImage.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId()==R.id.photo_item){
                if (urlPhotos.get(0).equals(url)){
                    Toast.makeText(context,R.string.impossible_main_photo, Toast.LENGTH_SHORT).show();
                }
                else if (urlPhotos.size()<3){
                    Toast.makeText(context,context.getString(R.string.you_cannot_delete_photo),Toast.LENGTH_SHORT).show();
                }
                else {
                    EditPhotoDialog deletePhotoDialog = new EditPhotoDialog(url, userId, i);
                    //Log.e("GALLERY HOLDER TARGET", String.valueOf(manager.findFragmentById(R.id.fragment_container) instanceof MyGalleryFragment));
                    deletePhotoDialog.setTargetFragment(manager.findFragmentById(R.id.fragment_container), MyGalleryFragment.DELETE_IMAGE_REQUEST);
                    deletePhotoDialog.show(manager, null);
                }
            }
            return true;
        }

        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.photo_item){
                Intent intent = PhotoViewPager.newIntent(context,urlPhotos,i);
                context.startActivity(intent);
            }
        }
    }
    public class UserGalleryHolder extends PhotoHolder{
        public static final int VIEW_TYPE = 2;
        public UserGalleryHolder(@NonNull View itemView) {
            super(itemView);
            photoImage.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.photo_item){
                Intent intent =PhotoViewPager.newIntent(context,urlPhotos,i);
                context.startActivity(intent);
            }
        }
    }
}
