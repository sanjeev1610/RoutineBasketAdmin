package com.mobiapp4u.pc.routinebasketadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasketadmin.Common.Common;
import com.mobiapp4u.pc.routinebasketadmin.ViewHolder.OrderDetailAdaptor;

public class OrderDetail extends AppCompatActivity {
    TextView order_id,order_phone,order_address,order_total,order_comment,order_status;
    String order_id_value = "";
    RecyclerView lstFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        order_id = (TextView)findViewById(R.id.order_id);
        order_phone = (TextView)findViewById(R.id.order_phone);
        order_address = (TextView)findViewById(R.id.order_address);
        order_total = (TextView)findViewById(R.id.order_total);
        order_comment = (TextView)findViewById(R.id.order_comment);
        order_status = (TextView)findViewById(R.id.order_status);

        lstFoods = (RecyclerView)findViewById(R.id.recycle_orderDetail);
        lstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);

        if(getIntent() != null){
            order_id_value = getIntent().getStringExtra("orderId");
        }
        System.out.println(order_id_value);

        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());
        order_status.setText(Common.converCodeToStatus(Common.currentRequest.getStatus()));

        OrderDetailAdaptor adaptor = new OrderDetailAdaptor(Common.currentRequest.getFoods());
        adaptor.notifyDataSetChanged();
        lstFoods.setAdapter(adaptor);
    }
}
