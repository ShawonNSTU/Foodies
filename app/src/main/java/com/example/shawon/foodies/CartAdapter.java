package com.example.shawon.foodies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHAWON on 2/14/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();

    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(cart);
        View view = layoutInflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(view);

    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, final int position) {

        /*TextDrawable textDrawable = TextDrawable.builder().buildRound(
                ""+listData.get(position).getQuantity(), Color.RED
        );

        holder.mCartItemCount.setImageDrawable(textDrawable);*/

        holder.mCartItemQuantity.setNumber(listData.get(position).getQuantity());

        holder.mCartItemQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new SQLiteDatabase(cart).updateCart(order);

                // Update Total Price...
                // Calculate Total Price...

                int total = 0;

                List<Order> orders = new SQLiteDatabase(cart).getCarts(CurrentUser.currentUser.getPhone());

                for(Order item : orders){
                    total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                }

                Locale locale = new Locale("en","US");

                NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
                cart.mTotal.setText(numberFormat.format(total));

            }
        });

        Locale locale = new Locale("en","US");

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

        int price = (Integer.parseInt(listData.get(position).getPrice()))*Integer.parseInt(listData.get(position).getQuantity());

        holder.mCartItemPrice.setText(numberFormat.format(price));

        Picasso.with(cart).load(listData.get(position).getProductImage())
                .into(holder.mCartItemImage);


      /* String s = Integer.toString(price);*/

    /*   holder.mCartItemPrice.setText(s);*/

        holder.mCartItemName.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position){
        return listData.get(position);
    }

    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item,int position){
        listData.add(position,item);
        notifyItemInserted(position);
    }

}
