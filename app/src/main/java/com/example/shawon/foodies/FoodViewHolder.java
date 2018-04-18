package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SHAWON on 2/10/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView foodName;
    public ImageView foodImage;
    public ImageView favourite;
    public ImageView foodReview;
    public TextView foodPrice;
    public ImageView quickCart;

    ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        foodName = (TextView) itemView.findViewById(R.id.food_name);
        foodImage = (ImageView) itemView.findViewById(R.id.food_image);
        favourite = (ImageView) itemView.findViewById(R.id.favourite_food);
        foodReview = (ImageView) itemView.findViewById(R.id.btn_review);
        foodPrice = (TextView) itemView.findViewById(R.id.food_price);
        quickCart = (ImageView) itemView.findViewById(R.id.quick_cart);

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
