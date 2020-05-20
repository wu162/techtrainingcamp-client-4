package com.example.gallerymanager.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerymanager.databinding.LayoutItemImageBinding;

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
        LayoutItemImageBinding binding = LayoutItemImageBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding.getRoot(),binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        holder.bindData(imageList.get(position));
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
        private LayoutItemImageBinding binding;

        public ViewHolder(@NonNull View itemView, LayoutItemImageBinding binding) {
            super(itemView);
            this.binding=binding;
        }

        public void bindData(String imagePath) {
            binding.setImagePath(imagePath);
        }
    }

    public interface onItemClickListener{
        public void onClick(View v,ArrayList<String> images,int index);
    }
}
