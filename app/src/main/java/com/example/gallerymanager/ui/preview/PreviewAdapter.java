package com.example.gallerymanager.ui.preview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallerymanager.R;
import com.example.gallerymanager.view.MyImageView;

import java.util.ArrayList;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private ArrayList<String> imageList;

    private Context mContext;
    private ScaleGestureDetector scaleGestureDetector;

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public PreviewAdapter(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public PreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_preview_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewAdapter.ViewHolder holder, int position) {
        holder.bindData(holder.itemView,imageList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(View itemView, String imagePath, int position) {
            MyImageView image = itemView.findViewById(R.id.preview_image);
            image.bindData(imagePath);
            Glide.with(mContext)
                    .load(imagePath)
                    .into(image);
            image.setTransitionName("image"+position);
        }
    }
}
