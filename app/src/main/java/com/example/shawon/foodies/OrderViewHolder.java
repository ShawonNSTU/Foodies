package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by SHAWON on 2/16/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView mOrderID, mOrderStatus, mOrderPhone, mOrderAddress;

    ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        mOrderID = (TextView) itemView.findViewById(R.id.order_id);
        mOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        mOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        mOrderAddress = (TextView) itemView.findViewById(R.id.order_address);

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
