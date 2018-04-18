package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SHAWON on 2/22/2018.
 */

public class FoodViewHolderServer extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {


    public TextView foodName;
    public ImageView foodImage;

    private ItemClickListener itemClickListener;


    public FoodViewHolderServer(View itemView) {
        super(itemView);

        foodName = (TextView) itemView.findViewById(R.id.food_name);
        foodImage = (ImageView) itemView.findViewById(R.id.food_image);

        itemView.setOnClickListener(this);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select an action");
        menu.setHeaderIcon(R.drawable.ic_shopping_cart_black_24dp);
        menu.add(0,0,getAdapterPosition(),CurrentUser.UPDATE);
        menu.add(0,1,getAdapterPosition(),CurrentUser.DELETE);

    }
}