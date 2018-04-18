package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodListServerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton floatingActionButton;

    private DatabaseReference mDatabaseFood;
    private StorageReference mStorage;

    boolean flag = false;

    private  String CategoryID = "";

    FirebaseRecyclerAdapter<Food,FoodViewHolderServer> mSerachAdapter;
    List<String> mSuggestList = new ArrayList<>();
    private MaterialSearchBar materialSearchBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    FirebaseRecyclerAdapter<Food,FoodViewHolderServer> adapter;

    private MaterialEditText mFoodName,mFoodDescription,mFoodPrice,mFoodDiscount;

    private FButton mButtonSelect,mButtonUpload;

    private Food newFood;

    private static final int PICK_IMAGE_REQUEST = 71;

    private Uri mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list_server);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar1));

        mDatabaseFood = FirebaseDatabase.getInstance().getReference("Food");
        mStorage = FirebaseStorage.getInstance().getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null){
                    CategoryID = getIntent().getStringExtra("CategoryID");
                    if(!CategoryID.isEmpty() && CategoryID != null){
                        if(CurrentUser.isConnectedToInternet(getApplicationContext())) {
                            loadListFood(CategoryID);
                        }
                        else {
                            Toast.makeText(FoodListServerActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
                materialSearchBar.setHint("Search");
                loadSuggestFood(CategoryID);
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
                            flag = false;
                        }

                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {

                        // When search finish, Show result of Search Adapter...
                        flag = false;
                        startSearchFood(text);
                        flag = true;

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
                    loadListFood(CategoryID);
                } else {
                    Toast.makeText(FoodListServerActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    private void startSearchFood(CharSequence text) {

        mSerachAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolderServer>(
                Food.class,
                R.layout.food_item_server,
                FoodViewHolderServer.class,
                mDatabaseFood.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolderServer viewHolder, Food model, int position) {

                viewHolder.foodName.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.foodImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(mSerachAdapter);

    }

    private void loadSuggestFood(String categoryID) {

        mDatabaseFood.orderByChild("menu_id").equalTo(categoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot getdataSnapshot : dataSnapshot.getChildren()){

                    Food item = getdataSnapshot.getValue(Food.class);
                    mSuggestList.add(item.getName());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FoodListServerActivity.this);

        alertDialogBuilder.setTitle("Add New Food");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View addFoodLayout = layoutInflater.inflate(R.layout.add_new_food_layout_server,null);

        mFoodName = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_name);
        mFoodDescription = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_description);
        mFoodPrice = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_price);
        mFoodDiscount = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_discount);
        mButtonSelect = (FButton) addFoodLayout.findViewById(R.id.image_select_btn);
        mButtonUpload = (FButton) addFoodLayout.findViewById(R.id.upload_btn);

        mButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialogBuilder.setView(addFoodLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_restaurant_black_24dp);

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if(newFood != null){

                    mDatabaseFood.push().setValue(newFood);
                    Snackbar.make(swipeRefreshLayout,"New food "+newFood.getName()+" is added",Snackbar.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(FoodListServerActivity.this,"Please fill up all information about to the new food!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Snackbar.make(swipeRefreshLayout,"You canceled to add new food! ",Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialogBuilder.show();
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            mImageUri = data.getData();
            mButtonSelect.setText("SELECTED");

        }

    }

    private void uploadImage() {

        if(mImageUri != null && !TextUtils.isEmpty(mFoodName.getText().toString()) && !TextUtils.isEmpty(mFoodDescription.getText().toString())
                && !TextUtils.isEmpty(mFoodPrice.getText().toString()) && !TextUtils.isEmpty(mFoodDiscount.getText().toString())){

            final ProgressDialog mProgress = new ProgressDialog(this);
            mProgress.show();

            String imageName = UUID.randomUUID().toString();

            final StorageReference foodImage = mStorage.child("Food Images/"+imageName);

            foodImage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Toast.makeText(FoodListServerActivity.this,"Uploaded!",Toast.LENGTH_SHORT).show();

                    foodImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            // Set value for New Food if image uploaded...

                            newFood = new Food();
                            newFood.setName(mFoodName.getText().toString());
                            newFood.setDescription(mFoodDescription.getText().toString());
                            newFood.setPrice(mFoodPrice.getText().toString());
                            newFood.setDiscount(mFoodDiscount.getText().toString());
                            newFood.setMenu_id(CategoryID);
                            newFood.setImage(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mProgress.dismiss();

                    Toast.makeText(FoodListServerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgress.setMessage("Uploading "+progress+"%");

                }
            });

        }

        else {
            Toast.makeText(FoodListServerActivity.this,"Please fill up all information about to the new food!",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(CurrentUser.UPDATE) && flag == false){
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(CurrentUser.DELETE) && flag == false){
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        else if (item.getTitle().equals(CurrentUser.UPDATE) && flag == true){
            showUpdateFoodDialog(mSerachAdapter.getRef(item.getOrder()).getKey(),mSerachAdapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(CurrentUser.DELETE) && flag == true){
            deleteFood(mSerachAdapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {

        mDatabaseFood.child(key).removeValue(); // To remove a Child and its value

        Snackbar.make(swipeRefreshLayout,"Deleted! ",Snackbar.LENGTH_SHORT).show();

    }

    private void showUpdateFoodDialog(final String key, final Food item) {

        mImageUri = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FoodListServerActivity.this);

        alertDialogBuilder.setTitle("Update Food");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View addFoodLayout = layoutInflater.inflate(R.layout.add_new_food_layout_server,null);

        mFoodName = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_name);
        mFoodDescription = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_description);
        mFoodPrice = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_price);
        mFoodDiscount = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_food_discount);
        mButtonSelect = (FButton) addFoodLayout.findViewById(R.id.image_select_btn);
        mButtonUpload = (FButton) addFoodLayout.findViewById(R.id.upload_btn);

        // Set Default View...

        mFoodName.setText(item.getName());
        mFoodDescription.setText(item.getDescription());
        mFoodPrice.setText(item.getPrice());
        mFoodDiscount.setText(item.getDiscount());

        mButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage(item);
            }
        });

        alertDialogBuilder.setView(addFoodLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_restaurant_black_24dp);

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                mDatabaseFood.child(key).setValue(item);
                Snackbar.make(swipeRefreshLayout,"Food "+item.getName()+" is updated",Snackbar.LENGTH_SHORT).show();

            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Snackbar.make(swipeRefreshLayout,"You canceled to update the food! ",Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialogBuilder.show();

    }

    private void updateImage(final Food item) {

        if(mImageUri != null){

            final ProgressDialog mProgress = new ProgressDialog(this);
            mProgress.show();

            String imageName = UUID.randomUUID().toString();

            final StorageReference foodImage = mStorage.child("Food Images/"+imageName);

            foodImage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Toast.makeText(FoodListServerActivity.this,"Uploaded!",Toast.LENGTH_SHORT).show();

                    foodImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mProgress.dismiss();

                    Toast.makeText(FoodListServerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgress.setMessage("Uploading "+progress+"%");

                }
            });

        }

        else {
            item.setName(mFoodName.getText().toString());
            item.setDescription(mFoodDescription.getText().toString());
            item.setPrice(mFoodPrice.getText().toString());
            item.setDiscount(mFoodDiscount.getText().toString());
            Toast.makeText(FoodListServerActivity.this,"To update click YES",Toast.LENGTH_SHORT).show();
        }


    }

    private void loadListFood(String categoryID) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolderServer>(
                Food.class,
                R.layout.food_item_server,
                FoodViewHolderServer.class,
                mDatabaseFood.orderByChild("menu_id").equalTo(categoryID)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolderServer viewHolder, Food model, int position) {

                viewHolder.foodName.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.foodImage);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
}