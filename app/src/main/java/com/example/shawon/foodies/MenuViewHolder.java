package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SHAWON on 2/9/2018.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView menuName;
    public ImageView menuImage;
    public TextView newMenu;

    private ItemClickListener itemClickListener;


    public MenuViewHolder(View itemView) {
        super(itemView);

        menuName = (TextView) itemView.findViewById(R.id.menu_name);
        menuImage = (ImageView) itemView.findViewById(R.id.menu_image);
        newMenu = (TextView) itemView.findViewById(R.id.new_category);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}