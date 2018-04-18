package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabaseCatagoty;
    private TextView mUserName, mSetText;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<CatagotyModel, MenuViewHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog mProgess;
    private NavigationView navigationView;

    private CounterFab fab;

    private DrawerLayout mDrawer;

    private MaterialEditText mCurrentPassword,mNewPassword,mRetypeNewPassword;

    private Menu menu;
    private MenuItem menuItem;
    private String before;

    // Slider

  /*  HashMap<String,String> image_list;
    SliderLayout mSliderLayout;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar1));

        /*Paper.init(this);*/
        mProgess = new ProgressDialog(this);

        mDatabaseCatagoty = FirebaseDatabase.getInstance().getReference().child("Catagory");
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Floating Action Button...

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_shopping_cart_black_24dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, Cart.class));
            }
        });
        fab.setCount(new SQLiteDatabase(this).getCountCartItem(CurrentUser.currentUser.getPhone()));

        // For Navigation Drawer...

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);

        navigationView.getMenu()
                .findItem(R.id.nav_menu)
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorSigninButton),PorterDuff.Mode.SRC_IN);
        navigationView.getMenu()
                .findItem(R.id.nav_favourites)
                .getIcon()
                .setColorFilter(getResources().getColor(android.R.color.holo_red_light),PorterDuff.Mode.SRC_IN);
        navigationView.getMenu()
                .findItem(R.id.nav_order)
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorSigninButton),PorterDuff.Mode.SRC_IN);
        navigationView.getMenu()
                .findItem(R.id.nav_cart)
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorSigninButton),PorterDuff.Mode.SRC_IN);
        navigationView.getMenu()
                .findItem(R.id.nav_settings)
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorSigninButton),PorterDuff.Mode.SRC_IN);
        navigationView.getMenu()
                .findItem(R.id.nav_sign_out)
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorSigninButton),PorterDuff.Mode.SRC_IN);
        navigationView.getMenu()
                .findItem(R.id.nav_help)
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorSigninButton),PorterDuff.Mode.SRC_IN);
        navigationView.getMenu()
                .findItem(R.id.nav_info)
                .getIcon()
                .setColorFilter(getResources().getColor(R.color.colorSigninButton),PorterDuff.Mode.SRC_IN);

        // To set username on the header layout of Navigation Drawer...

        View headerView = navigationView.getHeaderView(0);
        mUserName = (TextView) headerView.findViewById(R.id.textViewName);
        mUserName.setText(CurrentUser.currentUser.getName());
        mSetText = (TextView) headerView.findViewById(R.id.setText);
        mSetText.setText("Foodies User");

        menu = navigationView.getMenu();
        menuItem = menu.findItem(R.id.nav_cart);
        before = menuItem.getTitle().toString();

        countCurrentCartItem(new SQLiteDatabase(this).getCountCartItem(CurrentUser.currentUser.getPhone()));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        /*layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);*/

        // Recycler view with GridLayout...

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Here 2 is number of columns...

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
              /*  setupSlider();*/


            }
        });

        /*Runnable r = new Runnable() {
            @Override
            public void run() {
                countCurrentCartItem(new SQLiteDatabase(HomeActivity.this).getCountCartItem());
            }
        };*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (CurrentUser.isConnectedToInternet(getApplicationContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void countCurrentCartItem(int countCartItem) {

        if(countCartItem == 0){

            String counter = "";
            String s = before + "                                     " + counter + " ";
            SpannableString spannableString = new SpannableString(s);
            menuItem.setTitle(spannableString);

        }

        if(countCartItem > 0 && countCartItem < 10) {

            String counter = Integer.toString(countCartItem);

            String s = before + "                                     " + counter + " ";

            SpannableString spannableString = new SpannableString(s);

            /*spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#DC143C")),s.length()-2,s.length(),0);*/

            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#DC143C")),s.length()-3,s.length(),0);

            menuItem.setTitle(spannableString);
        }

        else if(countCartItem == 10){

            String counter = Integer.toString(countCartItem);

            String s = before + "                                   " + counter + " ";

            SpannableString spannableString = new SpannableString(s);

            /*spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#DC143C")),s.length()-2,s.length(),0);*/

            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#DC143C")),s.length()-3,s.length(),0);

            menuItem.setTitle(spannableString);

        }

        else if(countCartItem > 10){

            countCartItem = 10;

            String counter = Integer.toString(countCartItem);

            String s = before + "                                 " + counter + "+";

            SpannableString spannableString = new SpannableString(s);

//            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#DC143C")),s.length()-2,s.length(),0);

            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#DC143C")),s.length()-3,s.length(),0);

            menuItem.setTitle(spannableString);

        }

    }

  /*  private void setupSlider() {

        mSliderLayout = (SliderLayout) findViewById(R.id.slider);

        image_list = new HashMap<>();

        final DatabaseReference mDatabaseBanner = FirebaseDatabase.getInstance().getReference().child("Banner");

        mDatabaseBanner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Banner banner = postSnapshot.getValue(Banner.class);

                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());

                }

                for (String key : image_list.keySet()){

                    String[] keySplit = key.split("@@@");
                    String nameOfFood = keySplit[0];
                    final String foodID = keySplit[1];

                    // Create Slider

                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());

                    textSliderView.description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {

                                    Intent intent = new Intent(HomeActivity.this,FoodDetailsActivity.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);

                                }
                            });

                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodID",foodID);

                    mSliderLayout.addSlider(textSliderView);
                    mDatabaseBanner.removeEventListener(this);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);

        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mSliderLayout.setCustomAnimation(new DescriptionAnimation());

        mSliderLayout.setDuration(4000);
    }*/

    @Override
    protected void onStop() {
        super.onStop();
/*
        mSliderLayout.stopAutoCycle();*/
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<CatagotyModel, MenuViewHolder>(
                CatagotyModel.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                mDatabaseCatagoty
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, CatagotyModel model, final int position) {

                viewHolder.menuName.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImage()).into(viewHolder.menuImage);

               /* Display display = getWindowManager().getDefaultDisplay();
                float width = display.getWidth();
                TranslateAnimation translateAnimation = new TranslateAnimation(0,width-50,0,0);
                translateAnimation.setDuration(800);
                translateAnimation.setRepeatCount(1);
                translateAnimation.setRepeatMode(2);
                viewHolder.menuImage.startAnimation(translateAnimation);*/


                final CatagotyModel clickItem = model;

                if (model.getIsNew().equals("true")) {
                    viewHolder.newMenu.setText("New");
                    viewHolder.newMenu.setBackgroundColor(Color.parseColor("#DC143C"));
                }

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodIntent = new Intent(HomeActivity.this, FoodListActivity.class);
                        // To get the CategotyID from Menu Item and as CategoryID is the key so we can get the ke from position.getKey()
                        foodIntent.putExtra("CategoryID", adapter.getRef(position).getKey());
                        startActivity(foodIntent);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
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
    protected void onResume() {
        super.onResume();
        countCurrentCartItem(new SQLiteDatabase(this).getCountCartItem(CurrentUser.currentUser.getPhone()));
        fab.setCount(new SQLiteDatabase(this).getCountCartItem(CurrentUser.currentUser.getPhone()));
        mUserName.setText(CurrentUser.currentUser.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        countCurrentCartItem(new SQLiteDatabase(this).getCountCartItem(CurrentUser.currentUser.getPhone()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        Drawable search = menu.getItem(1).getIcon();
        search.mutate();
        search.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        Drawable refresh = menu.getItem(0).getIcon();
        refresh.mutate();
        refresh.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_search) {
            startActivity(new Intent(HomeActivity.this,SearchActivity.class));
        }

        if (item.getItemId() == R.id.menu_refresh){
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_order) {

            startActivity(new Intent(HomeActivity.this, OrderStatusActivity.class));

        } else if (id == R.id.nav_settings){
            startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
        } else if (id == R.id.nav_sign_out) {

            Paper.book().destroy();

            Intent signinIntent = new Intent(HomeActivity.this, SigninActivity.class);

            signinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(signinIntent);

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_info) {

        } else if (id == R.id.nav_cart) {

            startActivity(new Intent(HomeActivity.this, Cart.class));

        } else if (id == R.id.nav_favourites){

            ProgressDialog mProgress = new ProgressDialog(HomeActivity.this);
            mProgress.setMessage("Loading");
            mProgress.show();
            List<FavouriteFood> result = new SQLiteDatabase(HomeActivity.this).getFavourites(CurrentUser.currentUser.getPhone());
            if (!result.isEmpty()){
                startActivity(new Intent(HomeActivity.this,FavouritesActivity.class));
            }
            else {
                Snackbar.make(mDrawer, "You have no favourite food items!", Snackbar.LENGTH_SHORT).show();
            }
            mProgress.dismiss();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
