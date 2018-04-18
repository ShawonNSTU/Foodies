package com.example.shawon.foodies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference mRequest;
    private Query query;

    private FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        mRequest = FirebaseDatabase.getInstance().getReference("Request");

        recyclerView = (RecyclerView) findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        query = mRequest.orderByChild("phone").equalTo(CurrentUser.currentUser.getPhone());
        loadOrder(CurrentUser.currentUser.getPhone());

    }

    private void loadOrder(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.mOrderID.setText(String.format("#%s",adapter.getRef(position).getKey()));
                viewHolder.mOrderStatus.setText(String.format("Order Status : %s",getOrderStatus(model.getStatus())));
                viewHolder.mOrderPhone.setText(String.format("Phone Number : %s",model.getPhone()));
                viewHolder.mOrderAddress.setText(String.format("Address : %s",model.getAddress()));

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
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