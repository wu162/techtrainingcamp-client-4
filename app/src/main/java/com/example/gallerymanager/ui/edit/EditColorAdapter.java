package com.example.gallerymanager.ui.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerymanager.R;
import com.example.gallerymanager.view.ColorItemView;

import java.util.ArrayList;

public class EditColorAdapter extends RecyclerView.Adapter<EditColorAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> colorList;
    private int selectedPos=0;
    private onItemClick mOnItemClick;

    public EditColorAdapter(ArrayList<String> colorList) {
        this.colorList = colorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext=parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_color_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(holder.itemView,colorList.get(position));
        holder.itemView.setSelected(selectedPos==position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedPos);
                selectedPos=position;
                notifyItemChanged(selectedPos);
                mOnItemClick.onClick(colorList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public void setOnItemClick(onItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(View itemView, String color) {
            ColorItemView view = itemView.findViewById(R.id.edit_color_list_item);
            view.setColor(color);
//            GradientDrawable drawable = (GradientDrawable)view.getBackground();
//            drawable.setColor(Color.parseColor(color));
        }
    }

    public interface onItemClick{
        public void onClick(String color);
    }
}
