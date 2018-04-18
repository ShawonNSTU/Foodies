package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SHAWON on 3/11/2018.
 */

class OrderDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mFoodName,mFoodQuantity,mFoodPrice,mFoodDiscount;

    ItemClickListener itemClickListener;

    public OrderDetailViewHolder(View itemView) {
        super(itemView);

        mFoodName = (TextView) itemView.findViewById(R.id.product_name);
        mFoodQuantity = (TextView) itemView.findViewById(R.id.product_quantity);
        mFoodPrice = (TextView) itemView.findViewById(R.id.product_price);
        mFoodDiscount = (TextView) itemView.findViewById(R.id.product_discount);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder>{


    List<Order> listData;

    public OrderDetailAdapter(List<Order> listData) {
        this.listData = listData;
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_detail,parent,false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {

        Order order = listData.get(position);

        holder.mFoodName.setText(String.format("Name : %s",order.getProductName()));
        holder.mFoodQuantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.mFoodPrice.setText(String.format("Price : %s",order.getPrice()));
        holder.mFoodDiscount.setText(String.format("Discount : %s",order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
