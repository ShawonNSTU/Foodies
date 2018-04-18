package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import info.hoang8f.widget.FButton;

public class FoodDetailsActivity extends AppCompatActivity implements RatingDialogListener{

    private TextView mFoodName,mFoodPrice,mFoodDescription;
    private ImageView mFoodImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CounterFab floatingActionButton;
    private FloatingActionButton mButtonRating;
    private ElegantNumberButton elegantNumberButton;
    private FButton mShowCommentButton;

    private RatingBar mRatingBar;
    DatabaseReference mDatabaseRating;
    CoordinatorLayout mViewLayout;
    String mFoodNameShow;

    private String foodID = "";

    private DatabaseReference mDatabaseFood;

    private Food food;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar1));


        mDatabaseFood = FirebaseDatabase.getInstance().getReference("Food");

        elegantNumberButton = (ElegantNumberButton) findViewById(R.id.number_count);
        floatingActionButton = (CounterFab) findViewById(R.id.fab);
        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
        mButtonRating = (FloatingActionButton) findViewById(R.id.rating_btn);

        Drawable drawable = mRatingBar.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#39796b"), PorterDuff.Mode.SRC_ATOP);

        mFoodDescription = (TextView) findViewById(R.id.food_description);
        mFoodPrice = (TextView) findViewById(R.id.food_price);
        mFoodName = (TextView) findViewById(R.id.food_name);
        mShowCommentButton = (FButton) findViewById(R.id.btn_show_comment);

        mShowCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(FoodDetailsActivity.this);
                progressDialog.setMessage("Loading");
                progressDialog.show();
                final DatabaseReference mDatabaseRating = FirebaseDatabase.getInstance().getReference().child("Rating");
                mDatabaseRating.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(foodID)){
                            progressDialog.dismiss();
                            Intent intent = new Intent(FoodDetailsActivity.this,ShowCommentActivity.class);
                            intent.putExtra("FoodID",foodID);
                            startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                            Snackbar.make(mViewLayout,"This food item has no review",Snackbar.LENGTH_SHORT).show();
                        }
                        mDatabaseRating.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mViewLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mFoodImage = (ImageView) findViewById(R.id.food_image);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);

        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        if(getIntent() !=null){
            foodID = getIntent().getStringExtra("FoodID");
            mDatabaseRating = FirebaseDatabase.getInstance().getReference().child("Rating").child(foodID);
            if(!foodID.isEmpty() && foodID != null){
                if(CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    getDetailFood(foodID);
                    getAllRatingOfThisFood(foodID);
                }
                else {
                    Toast.makeText(FoodDetailsActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        }

        floatingActionButton.setCount(new SQLiteDatabase(this).getCountCartItem(CurrentUser.currentUser.getPhone()));

        mButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean flag = new SQLiteDatabase(getApplicationContext())
                        .checkFoodExist(foodID,CurrentUser.currentUser.getPhone());

                if (flag == false) {

                    new SQLiteDatabase(getApplicationContext()).addCart(new Order(
                            CurrentUser.currentUser.getPhone(),
                            foodID,
                            food.getName(),
                            elegantNumberButton.getNumber(),
                            food.getPrice(),
                            food.getDiscount(),
                            food.getImage()
                    ));

                    Toast.makeText(FoodDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }

                else{
                    String quantity = new SQLiteDatabase(getApplicationContext())
                            .getFoodQuantity(CurrentUser.currentUser.getPhone(),foodID);
                    new SQLiteDatabase(getApplicationContext())
                            .increaseCartItem(CurrentUser.currentUser.getPhone(),foodID,quantity,elegantNumberButton.getNumber());
                    Toast.makeText(FoodDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                }

                floatingActionButton.setCount(new SQLiteDatabase(FoodDetailsActivity.this).getCountCartItem(CurrentUser.currentUser.getPhone()));

            }
        });
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        floatingActionButton.setCount(new SQLiteDatabase(this).getCountCartItem());
    }*/

    private void getAllRatingOfThisFood(String foodID) {

        Query query = mDatabaseRating.orderByChild("foodID").equalTo(foodID);

        query.addValueEventListener(new ValueEventListener() {

            int count = 0, sum = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot getSnapshot : dataSnapshot.getChildren()){

                    Rating rating = getSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(rating.getRatingValue());
                    count++;
                }

                if(count != 0) {

                    float average = sum / count;

                    mRatingBar.setRating(average);

                    // To change color of STAR...

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setNoteDescriptionTextColor(R.color.colorAccent)
                .setDefaultRating(1)
                .setStarColor(R.color.colorAccent)
                .setTitle("Rate "+mFoodNameShow)
                .setDescription("Please rate "+mFoodNameShow+" and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please leave a comment")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnimation)
                .create(FoodDetailsActivity.this)
                .show();

    }

    private void getDetailFood(final String foodID) {
        mDatabaseFood.child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                food = dataSnapshot.getValue(Food.class);

                Picasso.with(getApplicationContext()).load(food.getImage()).into(mFoodImage);

                collapsingToolbarLayout.setTitle(food.getName());

                mFoodPrice.setText(food.getPrice());

                mFoodName.setText(food.getName());

                mFoodNameShow = food.getName();

                mFoodDescription.setText(food.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int ratingValue, @NotNull String comment) {

        final Rating rating = new Rating(CurrentUser.currentUser.getPhone(),foodID,String.valueOf(ratingValue),comment,CurrentUser.currentUser.getName());

        mDatabaseRating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(CurrentUser.currentUser.getPhone())){

                    mDatabaseRating.child(CurrentUser.currentUser.getPhone()).removeValue();
                    mDatabaseRating.child(CurrentUser.currentUser.getPhone()).setValue(rating);

                }
                else{

                    mDatabaseRating.child(CurrentUser.currentUser.getPhone()).setValue(rating);

                }

                Snackbar.make(mViewLayout,"Thank you for submit rating and comment!",Snackbar.LENGTH_SHORT).show();

                getAllRatingOfThisFood(foodID);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}