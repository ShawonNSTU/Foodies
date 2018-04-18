package com.example.shawon.foodies;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private List<FavouriteFood> favouriteFoodArrayList = new ArrayList<>();

    private FavouritesAdapter favouritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Favourites");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar1));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_favourite);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    loadFavouriteFood();
                }
                else {
                    Toast.makeText(FavouritesActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    loadFavouriteFood();
                } else {
                    Toast.makeText(FavouritesActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    private void loadFavouriteFood() {

        favouriteFoodArrayList = new SQLiteDatabase(this).getFavourites(CurrentUser.currentUser.getPhone());
        favouritesAdapter = new FavouritesAdapter(this,favouriteFoodArrayList);
        favouritesAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(favouritesAdapter);

    }
}
