package com.example.shawon.foodies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class OrderDetailActivityServer extends AppCompatActivity {

    TextView mOrderID,mUserPhone,mOrderTotalCost,mOrderAddress,mOrderComment;
    String orderID;
    RecyclerView mDetailOrder;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_server);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));


        mOrderID = (TextView) findViewById(R.id.order_id);
        mUserPhone = (TextView) findViewById(R.id.order_phone);
        mOrderTotalCost = (TextView) findViewById(R.id.order_total);
        mOrderAddress = (TextView) findViewById(R.id.order_address);
        mOrderComment = (TextView) findViewById(R.id.order_comment);

        mDetailOrder = (RecyclerView) findViewById(R.id.details_of_order);
        mDetailOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mDetailOrder.setLayoutManager(layoutManager);


        if(getIntent() != null){
            orderID = getIntent().getStringExtra("OrderID");
        }

        // Set Value...

        mOrderID.setText(String.format("#%s",orderID));
        mUserPhone.setText(String.format("Phone Number : %s",CurrentServerUser.currentRequest.getPhone()));
        mOrderTotalCost.setText(String.format("Total Cost : %s",CurrentServerUser.currentRequest.getTotal()));
        mOrderAddress.setText(String.format("Address : %s",CurrentServerUser.currentRequest.getAddress()));
        mOrderComment.setText(String.format("Requirement : %s",CurrentServerUser.currentRequest.getComment()));

        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(CurrentServerUser.currentRequest.getListOrder());

        orderDetailAdapter.notifyDataSetChanged();

        mDetailOrder.setAdapter(orderDetailAdapter);

    }
}
