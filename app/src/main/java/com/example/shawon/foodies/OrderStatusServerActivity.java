package com.example.shawon.foodies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatusServerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private RelativeLayout mRelativeLayout;
    private FirebaseRecyclerAdapter<Request,OrderViewHolderServer> adapter;
    private DatabaseReference mRequest;
    MaterialSpinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status_server);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));


        mRequest = FirebaseDatabase.getInstance().getReference("Request");
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        recyclerView = (RecyclerView) findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrder();

    }

    private void loadOrder() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolderServer>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolderServer.class,
                mRequest
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolderServer viewHolder, final Request model, final int position) {

                viewHolder.mOrderID.setText(String.format("#%s",adapter.getRef(position).getKey()));
                viewHolder.mOrderStatus.setText(String.format("Order Status : %s",getOrderStatus(model.getStatus())));
                viewHolder.mOrderAddress.setText(String.format("Address : %s",model.getAddress()));
                viewHolder.mOrderPhone.setText(String.format("Phone Number : %s",model.getPhone()));

                viewHolder.mOrderAddress.setTextColor(getResources().getColor(R.color.colorStatusBar1));

                viewHolder.mOrderAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trakingOrder = new Intent(OrderStatusServerActivity.this,TrackingOrder.class);
                        CurrentServerUser.currentRequest = model;
                        trakingOrder.putExtra("OrderID",adapter.getRef(position).getKey());
                        startActivity(trakingOrder);
                    }
                });

               viewHolder.mLocation.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent trakingOrder = new Intent(OrderStatusServerActivity.this,TrackingOrder.class);
                       CurrentServerUser.currentRequest = model;
                       trakingOrder.putExtra("OrderID",adapter.getRef(position).getKey());
                       startActivity(trakingOrder);
                   }
               });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent orderDetailActivity = new Intent(OrderStatusServerActivity.this,OrderDetailActivityServer.class);
                        CurrentServerUser.currentRequest = model;
                        orderDetailActivity.putExtra("OrderID",adapter.getRef(position).getKey());
                        startActivity(orderDetailActivity);

                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(CurrentUser.UPDATE)){

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(CurrentUser.DELETE)){
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(String key, final Request item) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderStatusServerActivity.this);
        alertDialogBuilder.setTitle("Update Order Status");
        alertDialogBuilder.setMessage("Please select the status of this order");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View addUpdateLayout = layoutInflater.inflate(R.layout.update_order_layout,null);

        alertDialogBuilder.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        mSpinner = (MaterialSpinner) addUpdateLayout.findViewById(R.id.statusSpinner);
        mSpinner.setItems("Placed","On My Way","Shipped");

        alertDialogBuilder.setView(addUpdateLayout);

        final String localKey = key;

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(mSpinner.getSelectedIndex()));
                mRequest.child(localKey).setValue(item);
                Snackbar.make(mRelativeLayout,"Updated! ",Snackbar.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();

    }

    private void deleteOrder(String key) {

        mRequest.child(key).removeValue();

        Snackbar.make(mRelativeLayout,"Deleted! ",Snackbar.LENGTH_SHORT).show();

    }

    private String getOrderStatus(String status) {

        if (status.equals("0")){
            return "Placed";
        }
        else if(status.equals("1")){
            return "On Way";
        }
        else
            return "Shipped";
    }
}