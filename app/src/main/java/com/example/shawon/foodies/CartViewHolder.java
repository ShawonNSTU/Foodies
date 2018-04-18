package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

/**
 * Created by SHAWON on 4/3/2018.
 */

public class CartViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener{

    public TextView mCartItemName,mCartItemPrice;
    /* public ImageView mCartItemCount;*/
    public ElegantNumberButton mCartItemQuantity;
    public ImageView mCartItemImage;

    public RelativeLayout mViewBackground;
    public LinearLayout mViewForground;

    ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);

        mCartItemName = (TextView) itemView.findViewById(R.id.cart_item_name);
        mCartItemPrice = (TextView) itemView.findViewById(R.id.cart_item_price);
        /*mCartItemCount = (ImageView) itemView.findViewById(R.id.cart_item_count);*/
        mCartItemQuantity = (ElegantNumberButton) itemView.findViewById(R.id.quick_quantity);
        mCartItemImage = (ImageView) itemView.findViewById(R.id.cart_image);
        mViewBackground = (RelativeLayout) itemView.findViewById(R.id.view_background);
        mViewForground = (LinearLayout) itemView.findViewById(R.id.view_forground);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select the action");
        menu.setHeaderIcon(R.drawable.ic_shopping_cart_black_24dp);
        menu.add(0,0,getAdapterPosition(),CurrentUser.DELETE);

    }
}
