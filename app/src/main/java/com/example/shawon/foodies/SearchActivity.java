package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    FirebaseRecyclerAdapter<Food,FoodViewHolder> mSearchAdapter;
    List<String> mSuggestList = new ArrayList<>();
    private MaterialSearchBar materialSearchBar;

    SQLiteDatabase sqLiteDatabase;

    private DatabaseReference mDatabaseFood;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar1));

        mDatabaseFood = FirebaseDatabase.getInstance().getReference().child("Food");
        sqLiteDatabase = new SQLiteDatabase(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);

        // For first time load...

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                if(CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    loadListFood();
                }
                else {
                    Toast.makeText(SearchActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
                materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
                materialSearchBar.enableSearch(); // Enable Search Bar for first time
                materialSearchBar.setHint("Search");
                loadSuggestFood();
                materialSearchBar.setLastSuggestions(mSuggestList);
                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        // When user type their text, we have to change suggest list...

                        List<String> suggest = new ArrayList<String>();

                        for(String search : mSuggestList){

                            if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                                suggest.add(search);
                            }

                        }

                        materialSearchBar.setLastSuggestions(suggest);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {

                        // When search bar is close, restore original adapter...

                        if(!enabled){
                            recyclerView.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {

                        // When search finish, Show result of Search Adapter...

                        startSearchFood(text);

                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    loadListFood();
                } else {
                    Toast.makeText(SearchActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void loadListFood() {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                mDatabaseFood
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.foodPrice.setText(String.format("$ %s",model.getPrice().toString()));
                viewHolder.foodPrice.setBackgroundColor(Color.parseColor("#7f333639"));
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.foodImage);

                if(sqLiteDatabase.isFavourite(adapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone())){

                    viewHolder.favourite.setImageResource(R.drawable.ic_favorite_black_24dp);

                    // To set Tint color Programmatically...

                    viewHolder.favourite.setColorFilter(ContextCompat.getColor(getApplicationContext(),android.R.color.holo_red_light));

                }

                /*viewHolder.shareToFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     *//*   Picasso.with(getBaseContext())
                                .load(model.getImage())
                                .into(target);*//*
                    }
                });*/

                viewHolder.foodReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progressDialog = new ProgressDialog(SearchActivity.this);
                        progressDialog.setMessage("Loading");
                        progressDialog.show();

                        final DatabaseReference mDatabaseRating = FirebaseDatabase.getInstance().getReference().child("Rating");
                        mDatabaseRating.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(adapter.getRef(position).getKey())){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SearchActivity.this,ShowCommentActivity.class);
                                    intent.putExtra("FoodID",adapter.getRef(position).getKey());
                                    startActivity(intent);
                                }
                                else{
                                    progressDialog.dismiss();
                                    Snackbar.make(swipeRefreshLayout,"This food item has no review",Snackbar.LENGTH_SHORT).show();
                                }
                                mDatabaseRating.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = new SQLiteDatabase(getApplicationContext())
                                .checkFoodExist(adapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone());
                        if (flag == false) {
                            new SQLiteDatabase(getApplicationContext()).addCart(new Order(
                                    CurrentUser.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()
                            ));

                            Toast.makeText(SearchActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String quantity = new SQLiteDatabase(getApplicationContext())
                                    .getFoodQuantity(CurrentUser.currentUser.getPhone(),adapter.getRef(position).getKey());
                            new SQLiteDatabase(getApplicationContext())
                                    .increaseCartItem(CurrentUser.currentUser.getPhone(),adapter.getRef(position).getKey(),quantity,"1");
                            Toast.makeText(SearchActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!sqLiteDatabase.isFavourite(adapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone())){

                            FavouriteFood favouriteFood = new FavouriteFood();
                            favouriteFood.setFoodID(adapter.getRef(position).getKey());
                            favouriteFood.setUserPhone(CurrentUser.currentUser.getPhone());
                            favouriteFood.setFoodName(model.getName());
                            favouriteFood.setFoodImage(model.getImage());
                            favouriteFood.setFoodPrice(model.getPrice());
                            favouriteFood.setFoodDiscount(model.getDiscount());
                            favouriteFood.setFoodDescription(model.getDescription());
                            favouriteFood.setFoodMenuID(model.getMenu_id());

                            sqLiteDatabase.addToFavourites(favouriteFood);
                            viewHolder.favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            viewHolder.favourite.setColorFilter(ContextCompat.getColor(getApplicationContext(),android.R.color.holo_red_light));
                            Snackbar.make(swipeRefreshLayout,""+model.getName()+" is added to your favourites",Snackbar.LENGTH_SHORT).show();

                        }
                        else{

                            sqLiteDatabase.removeFromFavourites(adapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone());
                            viewHolder.favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            viewHolder.favourite.setColorFilter(ContextCompat.getColor(getApplicationContext(),android.R.color.white));
                            Snackbar.make(swipeRefreshLayout,""+model.getName()+" is removed from your favourites",Snackbar.LENGTH_SHORT).show();

                        }

                    }
                });


                final Food details = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(SearchActivity.this,FoodDetailsActivity.class);
                        foodDetails.putExtra("FoodID",adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    private void startSearchFood(CharSequence text) {

        mSearchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                mDatabaseFood.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.foodName.setText(model.getName());
                viewHolder.foodPrice.setText(String.format("$ %s",model.getPrice().toString()));
                viewHolder.foodPrice.setBackgroundColor(Color.parseColor("#7f333639"));
                Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.foodImage);
                final Food details = model;

                if(sqLiteDatabase.isFavourite(mSearchAdapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone())){

                    viewHolder.favourite.setImageResource(R.drawable.ic_favorite_black_24dp);

                    // To set Tint color Programmatically...

                    viewHolder.favourite.setColorFilter(ContextCompat.getColor(getApplicationContext(),android.R.color.holo_red_light));

                }

                viewHolder.foodReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final ProgressDialog progressDialog = new ProgressDialog(SearchActivity.this);
                        progressDialog.setMessage("Loading");
                        progressDialog.show();

                        final DatabaseReference mDatabaseRating = FirebaseDatabase.getInstance().getReference().child("Rating");
                        mDatabaseRating.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(mSearchAdapter.getRef(position).getKey())){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SearchActivity.this,ShowCommentActivity.class);
                                    intent.putExtra("FoodID",mSearchAdapter.getRef(position).getKey());
                                    startActivity(intent);
                                }
                                else{
                                    progressDialog.dismiss();
                                    Snackbar.make(swipeRefreshLayout,"This food item has no review",Snackbar.LENGTH_SHORT).show();
                                }
                                mDatabaseRating.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = new SQLiteDatabase(getApplicationContext())
                                .checkFoodExist(mSearchAdapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone());
                        if (flag == false) {
                            new SQLiteDatabase(getApplicationContext()).addCart(new Order(
                                    CurrentUser.currentUser.getPhone(),
                                    mSearchAdapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()
                            ));

                            Toast.makeText(SearchActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            String quantity = new SQLiteDatabase(getApplicationContext())
                                    .getFoodQuantity(CurrentUser.currentUser.getPhone(),mSearchAdapter.getRef(position).getKey());
                            new SQLiteDatabase(getApplicationContext())
                                    .increaseCartItem(CurrentUser.currentUser.getPhone(),mSearchAdapter.getRef(position).getKey(),quantity,"1");
                            Toast.makeText(SearchActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                viewHolder.favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!sqLiteDatabase.isFavourite(mSearchAdapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone())){

                            FavouriteFood favouriteFood = new FavouriteFood();
                            favouriteFood.setFoodID(mSearchAdapter.getRef(position).getKey());
                            favouriteFood.setUserPhone(CurrentUser.currentUser.getPhone());
                            favouriteFood.setFoodName(model.getName());
                            favouriteFood.setFoodImage(model.getImage());
                            favouriteFood.setFoodPrice(model.getPrice());
                            favouriteFood.setFoodDiscount(model.getDiscount());
                            favouriteFood.setFoodDescription(model.getDescription());
                            favouriteFood.setFoodMenuID(model.getMenu_id());

                            sqLiteDatabase.addToFavourites(favouriteFood);
                            viewHolder.favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            viewHolder.favourite.setColorFilter(ContextCompat.getColor(getApplicationContext(),android.R.color.holo_red_light));
                            Snackbar.make(swipeRefreshLayout,""+model.getName()+" is added to your favourites",Snackbar.LENGTH_SHORT).show();

                        }
                        else{

                            sqLiteDatabase.removeFromFavourites(mSearchAdapter.getRef(position).getKey(),CurrentUser.currentUser.getPhone());
                            viewHolder.favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            viewHolder.favourite.setColorFilter(ContextCompat.getColor(getApplicationContext(),android.R.color.white));
                            Snackbar.make(swipeRefreshLayout,""+model.getName()+" is removed from your favourites",Snackbar.LENGTH_SHORT).show();

                        }

                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(SearchActivity.this,FoodDetailsActivity.class);
                        foodDetails.putExtra("FoodID",mSearchAdapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });
            }
        };
        mSearchAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mSearchAdapter);

    }

    private void loadSuggestFood() {

        mDatabaseFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot getDataSnapshot : dataSnapshot.getChildren()){

                    Food item = getDataSnapshot.getValue(Food.class);
                    mSuggestList.add(item.getName());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
