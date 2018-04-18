package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHAWON on 3/28/2018.
 */

class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView foodName;
    public ImageView foodImage;
    public ImageView foodReview;
    public TextView foodPrice;
    public ImageView quickCart;

    ItemClickListener itemClickListener;

    public FavouriteViewHolder(View itemView) {
        super(itemView);

        foodName = (TextView) itemView.findViewById(R.id.favourite_food_name);
        foodImage = (ImageView) itemView.findViewById(R.id.favourite_food_image);
        quickCart = (ImageView) itemView.findViewById(R.id.quick_cart);
        foodReview = (ImageView) itemView.findViewById(R.id.btn_review);
        foodPrice = (TextView) itemView.findViewById(R.id.favourite_food_price);

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

public class FavouritesAdapter extends RecyclerView.Adapter<FavouriteViewHolder>{

    private FavouritesActivity favouritesActivity;
    private List<FavouriteFood> favouriteFoodList = new ArrayList<>();

    public FavouritesAdapter(FavouritesActivity favouritesActivity, List<FavouriteFood> favouriteFoodList) {
        this.favouritesActivity = favouritesActivity;
        this.favouriteFoodList = favouriteFoodList;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(favouritesActivity);
        View view = layoutInflater.inflate(R.layout.favourites_item,parent,false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, final int position) {

        holder.foodName.setText(favouriteFoodList.get(position).getFoodName());
        holder.foodPrice.setText(String.format("$ %s",favouriteFoodList.get(position).getFoodPrice()));
        holder.foodPrice.setBackgroundColor(Color.parseColor("#7f333639"));
        Picasso.with(favouritesActivity).load(favouriteFoodList.get(position).getFoodImage()).into(holder.foodImage);

        holder.foodReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(favouritesActivity);
                progressDialog.setMessage("Loading");
                progressDialog.show();

                final DatabaseReference mDatabaseRating = FirebaseDatabase.getInstance().getReference().child("Rating");
                mDatabaseRating.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(favouriteFoodList.get(position).getFoodID())){
                            progressDialog.dismiss();
                            Intent intent = new Intent(favouritesActivity,ShowCommentActivity.class);
                            intent.putExtra("FoodID",favouriteFoodList.get(position).getFoodID());
                            favouritesActivity.startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                            Snackbar.make(favouritesActivity.swipeRefreshLayout,"This food item has no review",Snackbar.LENGTH_SHORT).show();
                        }
                        mDatabaseRating.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        holder.quickCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = new SQLiteDatabase(favouritesActivity)
                        .checkFoodExist(favouriteFoodList.get(position).getFoodID(),CurrentUser.currentUser.getPhone());
                if (flag == false) {
                    new SQLiteDatabase(favouritesActivity).addCart(new Order(
                            CurrentUser.currentUser.getPhone(),
                            favouriteFoodList.get(position).getFoodID(),
                            favouriteFoodList.get(position).getFoodName(),
                            "1",
                            favouriteFoodList.get(position).getFoodPrice(),
                            favouriteFoodList.get(position).getFoodDiscount(),
                            favouriteFoodList.get(position).getFoodImage()
                    ));

                    Toast.makeText(favouritesActivity, "Added to Cart", Toast.LENGTH_SHORT).show();
                }

                else{
                    String quantity = new SQLiteDatabase(favouritesActivity)
                            .getFoodQuantity(CurrentUser.currentUser.getPhone(),favouriteFoodList.get(position).getFoodID());
                    new SQLiteDatabase(favouritesActivity)
                            .increaseCartItem(CurrentUser.currentUser.getPhone(),favouriteFoodList.get(position).getFoodID(),quantity,"1");
                    Toast.makeText(favouritesActivity, "Added to Cart", Toast.LENGTH_SHORT).show();
                }

            }
        });
        FavouriteFood favouriteFood = favouriteFoodList.get(position);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetails = new Intent(favouritesActivity,FoodDetailsActivity.class);
                foodDetails.putExtra("FoodID",favouriteFoodList.get(position).getFoodID());
                favouritesActivity.startActivity(foodDetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteFoodList.size();
    }
}