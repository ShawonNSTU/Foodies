package com.example.shawon.foodies;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    public TextView mTotal;
    private FButton mPlaceOrderButton;
    private ProgressDialog mProgress;

    private DatabaseReference mRequest;

    private List<Order> cart = new ArrayList<>();

    private int total;

    private CartAdapter cartAdapter;

    private RelativeLayout relativeLayout;

    private Place mShippingAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        recyclerView = (RecyclerView) findViewById(R.id.list_cart);
        mTotal = (TextView) findViewById(R.id.total);
        mPlaceOrderButton = (FButton) findViewById(R.id.place_order_button);

        mProgress = new ProgressDialog(this);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        mRequest = FirebaseDatabase.getInstance().getReference("Request");

        mPlaceOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size() > 0) {
                    showAlertDialog();
                }
                else{
                    Snackbar.make(relativeLayout,"Your cart is empty",Snackbar.LENGTH_SHORT).show();
                }
            }

        });

        loadListOfFood();

    }

    private void showAlertDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Cart.this);

        alertDialogBuilder.setTitle("One more step!");

        alertDialogBuilder.setMessage("Enter your address and leave any requirement related to this order");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View order_address_comment = layoutInflater.inflate(R.layout.order_address_comment,null);

        /* you can not add meta data with same name...
        it will show you manifest merger failed...
        so it can not be placed both MAP API_KEY and PLACES API_KEY...
        adding PLACES api_key allows also the map... */

       /* final MaterialEditText mAddress = (MaterialEditText) order_address_comment.findViewById(R.id.edit_address);*/

        PlaceAutocompleteFragment mAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Hide search icon before fragment...

        mAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        // Set Hint for Autocomplete Edit Text...

        ((EditText) mAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter your address");

        // Set Text Size...
        ((EditText) mAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(15);

        // Get address from Autocomplete...

        mAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mShippingAddress = place;
            }
            @Override
            public void onError(Status status) {
                Log.e("ERROR",status.getStatusMessage());
            }
        });


        final MaterialEditText mComment = (MaterialEditText) order_address_comment.findViewById(R.id.edit_comment);

        alertDialogBuilder.setView(order_address_comment);
        alertDialogBuilder.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mProgress.setMessage("Confirming your order");
                mProgress.show();
                if (cart.isEmpty()) {
                    mProgress.dismiss();
                    Toast.makeText(Cart.this, "You have not added any food to cart!", Toast.LENGTH_SHORT).show();
                    // Remove fragment...
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    finish();
                }

                else if(mShippingAddress == null){
                    mProgress.dismiss();
                    Toast.makeText(Cart.this, "Please enter your address!", Toast.LENGTH_SHORT).show();
                    // Remove fragment...
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    finish();
                }

                else {
                    Request request = new Request(
                            CurrentUser.currentUser.getPhone(),
                            CurrentUser.currentUser.getName(),
                            mShippingAddress.getAddress().toString(),
                            mTotal.getText().toString(),
                            "0", // Status
                            mComment.getText().toString(),
                            cart
                    );

                    String orderNumber = String.valueOf(System.currentTimeMillis());

                    mRequest.child(orderNumber).setValue(request);

                    // The static method System.currentTimeMillis() returns the time since January 1st 1970 in milliseconds...

                    new SQLiteDatabase(getApplicationContext()).deleteCart(CurrentUser.currentUser.getPhone());

                    mProgress.dismiss();
                    /*Toast.makeText(Cart.this, "Thank you, soon you will get your order back!", Toast.LENGTH_SHORT).show();*/
                    showNotification();
                    // Remove fragment...
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    finish();
                }
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Remove fragment...
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialogBuilder.show();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(CurrentUser.DELETE)){
            deleteCart(item.getOrder());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCart(int order) {

        cart.remove(order);

        new SQLiteDatabase(this).deleteCart(CurrentUser.currentUser.getPhone());

        for (Order item : cart){
            new SQLiteDatabase(this).addCart(item);
        }

        loadListOfFood();

    }

    private void showNotification() {

        Intent intent = new Intent(Cart.this,OrderStatusActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_restaurant_black_24dp)
                .setContentTitle("Foodies")
                .setContentText("Your order has been listened. Keep stay with us. Thank You.")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());

    }

    private void loadListOfFood() {

        cart = new SQLiteDatabase(this).getCarts(CurrentUser.currentUser.getPhone());
        cartAdapter = new CartAdapter(cart,this);
        cartAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cartAdapter);

        total = 0;
        for(Order order:cart){
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("en","US");

      /*  String s  = Integer.toString(total);*/

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        mTotal.setText(numberFormat.format(total));
   /*   mTotal.setText(s);*/

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof CartViewHolder){

            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            cartAdapter.removeItem(deleteIndex);

            new SQLiteDatabase(getApplicationContext()).removeFromCart(deleteItem.getProductID(),CurrentUser.currentUser.getPhone());

            int total = 0;

            List<Order> orders = new SQLiteDatabase(this).getCarts(CurrentUser.currentUser.getPhone());

            for(Order item : orders){
                total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            }

            Locale locale = new Locale("en","US");

            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

            mTotal.setText(numberFormat.format(total));

            Snackbar snackbar = Snackbar.make(relativeLayout,name + " removed from cart",Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cartAdapter.restoreItem(deleteItem,deleteIndex);

                    new SQLiteDatabase(getApplicationContext()).addCart(deleteItem);

                    int total = 0;

                    List<Order> orders = new SQLiteDatabase(getApplicationContext()).getCarts(CurrentUser.currentUser.getPhone());

                    for(Order items : orders){
                        total+=(Integer.parseInt(items.getPrice()))*(Integer.parseInt(items.getQuantity()));
                    }

                    Locale locale = new Locale("en","US");

                    NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

                    mTotal.setText(numberFormat.format(total));

                }
            });

            snackbar.setActionTextColor(Color.YELLOW);

            snackbar.show();
        }

    }
}