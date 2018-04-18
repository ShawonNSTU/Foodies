package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowCommentActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference mDatabaseRating;

    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;

    private String foodID = "";

    ProgressDialog mProgress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));


        if (getIntent() != null) {
            foodID = getIntent().getStringExtra("FoodID");
        }

        mProgress = new ProgressDialog(this);

        mDatabaseRating = FirebaseDatabase.getInstance().getReference().child("Rating").child(foodID);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_comment);
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
                mProgress.setMessage("Loading");
                mProgress.show();
                loadComment();
                mProgress.dismiss();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    loadComment();
                }
                else{
                    Toast.makeText(ShowCommentActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadComment() {

        adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(
                Rating.class,
                R.layout.show_comment_layout,
                ShowCommentViewHolder.class,
                mDatabaseRating
        ) {
            @Override
            protected void populateViewHolder(ShowCommentViewHolder viewHolder, Rating model, int position) {

                /*Drawable drawable = viewHolder.mRatingBar.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#39796b"), PorterDuff.Mode.SRC_ATOP);*/

                viewHolder.mRatingBar.setRating(Float.parseFloat(model.getRatingValue()));

                viewHolder.mUserName.setText(model.getName());

                viewHolder.mUserComment.setText(model.getComment());

            }
        };

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);

    }
}
