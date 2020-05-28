package com.example.gallerymanager.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.gallerymanager.databinding.LayoutHomeItemBinding;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private ArrayList<String> imageList;

    private onItemClickListener mClickListener;

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public HomeAdapter(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutHomeItemBinding binding = LayoutHomeItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding.getRoot(),binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        holder.bindData(imageList.get(position),position);
        ViewCompat.setTransitionName(holder.itemView,"image"+position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(v,imageList,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void setClickListener(onItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LayoutHomeItemBinding binding;

        public ViewHolder(@NonNull View itemView, LayoutHomeItemBinding binding) {
            super(itemView);
            this.binding=binding;
        }

        public void bindData(String imagePath, int position) {
            binding.setImagePath(imagePath);
//            binding.homeItemImage.setTransitionName("image"+position);
        }
    }

    public interface onItemClickListener{
        public void onClick(View v,ArrayList<String> images,int index);
    }
}
