package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class HomeServerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DatabaseReference mDatabaseCatagoty;
    private TextView mUserName,mSetText;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<CatagotyModel,MenuViewHolderServer> adapter;

    private MaterialEditText mAddMenuName,mNewPassword,mCurrentPassword,mRetypeNewPassword;
    private FButton mButtonSelect,mButtonUpload;

    private StorageReference mStorage;

    private CatagotyModel catagotyModel = null;

    private Uri mImageUri = null;
    private static final int PICK_IMAGE_REQUEST = 71;

    private ProgressDialog mProgessBar;

    private DrawerLayout drawer;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_server);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar1));

        mDatabaseCatagoty = FirebaseDatabase.getInstance().getReference().child("Catagory");
        mStorage = FirebaseStorage.getInstance().getReference();

        mProgessBar = new ProgressDialog(this);

        // Floating Action Button...

        CounterFab fab = (CounterFab) findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_playlist_add_black_24dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        // For Navigation Drawer...

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // To set username on the header layout of Navigation Drawer...

        View headerView = navigationView.getHeaderView(0);
        mUserName = (TextView) headerView.findViewById(R.id.textViewName);
        mUserName.setText(CurrentServerUser.currentServerUser.getName());
        mSetText = (TextView) headerView.findViewById(R.id.setText);
        mSetText.setText("Foodies Admin");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        /*layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);*/

        // Recycler view with GridLayout...

        recyclerView.setLayoutManager(new GridLayoutManager(this,2)); // Here 2 is number of columns...

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light);

        // For first time load...

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                loadMenu();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    loadMenu();
                }
                else{
                    Toast.makeText(HomeServerActivity.this,"Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Intent intent = new Intent(HomeServerActivity.this,ListenOrderServer.class);
        startService(intent);

    }

    private void showDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeServerActivity.this);

        alertDialogBuilder.setTitle("Add New Category");

        alertDialogBuilder.setMessage("Please fill Category Name and Image");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View addMenuLayout = layoutInflater.inflate(R.layout.add_new_menu_layout_server,null);

        mAddMenuName = (MaterialEditText) addMenuLayout.findViewById(R.id.edit_menu_name);
        mButtonSelect = (FButton) addMenuLayout.findViewById(R.id.image_select_btn);
        mButtonUpload = (FButton) addMenuLayout.findViewById(R.id.upload_btn);

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

        alertDialogBuilder.setView(addMenuLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_restaurant_black_24dp);

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if(catagotyModel != null){

                    mDatabaseCatagoty.push().setValue(catagotyModel);
                    Snackbar.make(drawer,"New Category "+catagotyModel.getName()+" is added",Snackbar.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(HomeServerActivity.this,"Please fill up all information about to the new category!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Snackbar.make(drawer,"You canceled to add new Category! ",Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialogBuilder.show();
    }

    private void uploadImage() {

        if(mImageUri != null && !TextUtils.isEmpty(mAddMenuName.getText().toString())){

            final ProgressDialog mProgress = new ProgressDialog(this);
            mProgress.show();

             String imageName = UUID.randomUUID().toString();

            final StorageReference catogoryImage = mStorage.child("Category Images/"+imageName);

            catogoryImage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Toast.makeText(HomeServerActivity.this,"Uploaded!",Toast.LENGTH_SHORT).show();

                    catogoryImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            // Set value for New Catagory if image uploaded...

                            catagotyModel = new CatagotyModel(mAddMenuName.getText().toString(),uri.toString(),"true");

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mProgress.dismiss();

                    Toast.makeText(HomeServerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgress.setMessage("Uploading "+progress+"%");

                }
            });

        }

        else {
            Toast.makeText(HomeServerActivity.this,"Please fill up all information about to the new catagory!",Toast.LENGTH_SHORT).show();
        }

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

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<CatagotyModel, MenuViewHolderServer>(
                CatagotyModel.class,
                R.layout.menu_item,
                MenuViewHolderServer.class,
                mDatabaseCatagoty
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolderServer viewHolder, final CatagotyModel model, final int position) {

                viewHolder.menuName.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.menuImage);
                final CatagotyModel clickItem = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent foodIntent = new Intent(HomeServerActivity.this,FoodListServerActivity.class);
                        foodIntent.putExtra("CategoryID",adapter.getRef(position).getKey());
                        startActivity(foodIntent);

                    }
                });
            }
        };
        adapter.notifyDataSetChanged(); // Refresh data if data have changed...
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(CurrentUser.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(CurrentUser.DELETE)){
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);

    }

    private void deleteCategory(String key) {

        DatabaseReference mDatabaseFood = FirebaseDatabase.getInstance().getReference().child("Food");
        Query query = mDatabaseFood.orderByChild("menu_id").equalTo(key);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot getSnapshot : dataSnapshot.getChildren()){
                    getSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseCatagoty.child(key).removeValue(); // To remove a Child and its value

        Snackbar.make(drawer,"Category deleted! ",Snackbar.LENGTH_SHORT).show();

    }

    private void showUpdateDialog(final String key, final CatagotyModel item) {

        mImageUri = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeServerActivity.this);

        alertDialogBuilder.setTitle("Update Category");

        alertDialogBuilder.setMessage("Please fill up information about the Category");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View addMenuLayout = layoutInflater.inflate(R.layout.add_new_menu_layout_server,null);

        mAddMenuName = (MaterialEditText) addMenuLayout.findViewById(R.id.edit_menu_name);
        mButtonSelect = (FButton) addMenuLayout.findViewById(R.id.image_select_btn);
        mButtonUpload = (FButton) addMenuLayout.findViewById(R.id.upload_btn);

        // Set Default Name...

        mAddMenuName.setText(item.getName());

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

        alertDialogBuilder.setView(addMenuLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_restaurant_black_24dp);

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                mDatabaseCatagoty.child(key).setValue(item);

                Snackbar.make(drawer,"Category updated! ",Snackbar.LENGTH_SHORT).show();

            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Snackbar.make(drawer,"You canceled to add new Category! ",Snackbar.LENGTH_SHORT).show();

            }
        });
        alertDialogBuilder.show();

    }

    private void updateImage(final CatagotyModel item) {

        if(mImageUri != null){

            final ProgressDialog mProgress = new ProgressDialog(this);
            mProgress.show();

             String imageName = UUID.randomUUID().toString();

            final StorageReference catogoryImage = mStorage.child("Category Images/"+imageName);

            catogoryImage.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.dismiss();

                    Toast.makeText(HomeServerActivity.this,"Uploaded!",Toast.LENGTH_SHORT).show();

                    catogoryImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

                    Toast.makeText(HomeServerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgress.setMessage("Uploading "+progress+"%");

                }
            });

        }

        else {
            item.setName(mAddMenuName.getText().toString());
            Toast.makeText(HomeServerActivity.this,"Name changes, To update click YES",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_server, menu);

        Drawable refresh = menu.getItem(0).getIcon();
        refresh.mutate();
        refresh.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_refresh){
            loadMenu();
        }

        if (item.getItemId() == R.id.menu_refresh){

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_order) {

            startActivity(new Intent(HomeServerActivity.this,OrderStatusServerActivity.class));

        } else if (id == R.id.nav_settings){

        } else if (id == R.id.nav_sign_out) {

            Paper.book().destroy();

            Intent signinIntent = new Intent(HomeServerActivity.this,SigninActivity.class);

            signinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(signinIntent);


        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_info) {

        } /*else if (id == R.id.nav_change_password) {
            showPasswordDialog();
        }*/
        else if (id == R.id.nav_favourites){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*private void showPasswordDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeServerActivity.this);
        alertDialogBuilder.setTitle("Change Password");
        alertDialogBuilder.setMessage("Please fill up full information to change password");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View changePasswordLayout = layoutInflater.inflate(R.layout.change_password_layout,null);

        mCurrentPassword = (MaterialEditText) changePasswordLayout.findViewById(R.id.current_password);
        mNewPassword = (MaterialEditText) changePasswordLayout.findViewById(R.id.new_password);
        mRetypeNewPassword = (MaterialEditText) changePasswordLayout.findViewById(R.id.retype_password);

        alertDialogBuilder.setView(changePasswordLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_security_black_24dp);

        alertDialogBuilder.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                mProgessBar.setMessage("Processing");
                mProgessBar.show();

                if (mCurrentPassword.getText().toString().equals(CurrentServerUser.currentServerUser.getPassword())){

                    if(mNewPassword.getText().toString().equals(mRetypeNewPassword.getText().toString())){

                        Map<String,Object> passwordUpdate = new HashMap<>();

                        passwordUpdate.put("password",mNewPassword.getText().toString());

                        DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

                        mUsers.child(CurrentServerUser.currentServerUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        mProgessBar.dismiss();

                                        CurrentServerUser.currentServerUser.setPassword(mNewPassword.getText().toString());

                                        Paper.book().write(CurrentUser.USER,CurrentServerUser.currentServerUser.getPhone());
                                        Paper.book().write(CurrentUser.PASSWORD,CurrentServerUser.currentServerUser.getPassword());
                                        Paper.book().write(CurrentUser.TYPE,"Admin");

                                        Snackbar.make(drawer,"Password updated! ",Snackbar.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                mProgessBar.dismiss();

                                Snackbar.make(drawer,""+e.getMessage(),Snackbar.LENGTH_SHORT).show();

                            }
                        });

                    }
                    else {
                        mProgessBar.dismiss();
                        Toast.makeText(HomeServerActivity.this,"Please type the new password twice correctly!",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    mProgessBar.dismiss();
                    Toast.makeText(HomeServerActivity.this,"You have typed wrong current password!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }*/

    }