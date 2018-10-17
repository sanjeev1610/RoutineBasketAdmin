package com.mobiapp4u.pc.routinebasketadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketadmin.Common.Common;
import com.mobiapp4u.pc.routinebasketadmin.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasketadmin.Modal.DataMessage;
import com.mobiapp4u.pc.routinebasketadmin.Modal.MyResponse;
import com.mobiapp4u.pc.routinebasketadmin.Modal.Request;
import com.mobiapp4u.pc.routinebasketadmin.Modal.Token;
import com.mobiapp4u.pc.routinebasketadmin.Remote.APIService;
import com.mobiapp4u.pc.routinebasketadmin.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    MaterialSpinner editOrderStatus,shipper_spinner;

    String pos;
    Request Rmodel;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        mService = Common.getFCMService();

        recyclerView = (RecyclerView)findViewById(R.id.recycler_order_status);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();
    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class, R.layout.layout_order_status, OrderViewHolder.class, requests) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderStatus.setText(Common.converCodeToStatus(model.getStatus()));
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));

                    }
                });
                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteCategory(adapter.getRef(position).getKey());

                    }
                });
                viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = adapter.getItem(position);
                        intent.putExtra("orderId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
                Rmodel = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                       pos=  adapter.getRef(position).getKey();
                       if(!isLongClick){
                           Intent intent = new Intent(OrderStatus.this, TrackOrder.class);
                           Common.currentRequest = model;
                           startActivity(intent);
                       }//else{
//                           Intent intent = new Intent(OrderStatus.this, OrderDetail.class);
//                           Common.currentRequest = model;
//                           intent.putExtra("orderId",adapter.getRef(position).getKey());
//                           startActivity(intent);
//                       }

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        if(item.getTitle().equals(Common.UPDATE)){
//            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
//        }else
//        if(item.getTitle().equals((Common.DELETE))){
//            deleteCategory(adapter.getRef(item.getOrder()).getKey());
//        }else
//        if(item.getTitle().equals((Common.DETAILS))){
//            Intent intent = new Intent(OrderStatus.this, OrderDetail.class);
//                           Common.currentRequest = adapter.getItem(item.getOrder());
//                           intent.putExtra("orderId",adapter.getRef(item.getOrder()).getKey());
//                           startActivity(intent);
//        }
//        return super.onContextItemSelected(item);
//    }

    private void deleteCategory(String key) {
      requests.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Request item) {
        AlertDialog.Builder alrt = new AlertDialog.Builder(this);
        alrt.setTitle("Update Order Status");
        alrt.setMessage("Please fill Full Information");

        final LayoutInflater inflater = this.getLayoutInflater();
        final View add_order_layout = inflater.inflate(R.layout.update_order_status_layout, null);

        editOrderStatus = (MaterialSpinner)add_order_layout.findViewById(R.id.spinner);
        editOrderStatus.setItems("Placed","On My way","Shipping");
        shipper_spinner = (MaterialSpinner)add_order_layout.findViewById(R.id.spinner_shipper);
       // editOrderStatus.setText(Common.converCodeToStatus(item.getStatus()));
        final List<String> shipperList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.SHIPPER_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot shipSnapshot:dataSnapshot.getChildren()){
                            shipperList.add(shipSnapshot.getKey());
                            shipper_spinner.setItems(shipperList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        alrt.setView(add_order_layout);
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        final String localKey = key;
        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(editOrderStatus.getSelectedIndex()));
                if(item.getStatus().equals("2")) {
                    //copy item to table orderNeedShip
                    FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIPPER)
                            .child(shipper_spinner.getItems().get(shipper_spinner.getSelectedIndex()).toString())
                            .child(localKey)
                            .setValue(item);

                    requests.child(localKey).setValue(item);

                    sendNotificationToUser(localKey, item);
                    sendOrderShipRequestToShipper(shipper_spinner.getItems().get(shipper_spinner.getSelectedIndex()).toString(), item);
                }else{
                    requests.child(localKey).setValue(item);

                    sendNotificationToUser(localKey, item);
                }
//                if(newCategory!=null){
//                    category.push().setValue(newCategory);
//                    Snackbar.make(drawer, "New category"+newCategory.getName()+"was added", Snackbar.LENGTH_SHORT ).show();
//                }
            }
        });
        alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alrt.show();

    }

    private void sendOrderShipRequestToShipper(String shipperPhone, Request item) {
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Token token = postSnapshot.getValue(Token.class);

                            Map<String,String> dataSend = new HashMap<>();
                            dataSend.put("title","Routine Basket");
                            dataSend.put("message","Your have new Order need ship");
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);


                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(retrofit2.Call<MyResponse> call, Response<MyResponse> response) {
                                            if(response.body().success == 1){
                                                Toast.makeText(OrderStatus.this, "Sent to shipper", Toast.LENGTH_LONG).show();

                                            }else {
                                                Toast.makeText(OrderStatus.this, "Failed to send Notification", Toast.LENGTH_LONG).show();

                                            }
                                        }

                                        @Override
                                        public void onFailure(retrofit2.Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void sendNotificationToUser(final String key, final Request item) {
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                     for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                         Token token = postSnapshot.getValue(Token.class);

//                         //Make new Payload
//                         Notification notification = new Notification("Your Order"+key+" was updated","Routine basket");
//                         Sender content = new Sender(token.getToken(), notification);
                         Map<String,String> dataSend = new HashMap<>();
                         dataSend.put("title","Routine Basket");
                         dataSend.put("message","Your order "+key+"was updated");
                         DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);


                         mService.sendNotification(dataMessage)
                                 .enqueue(new Callback<MyResponse>() {
                                     @Override
                                     public void onResponse(retrofit2.Call<MyResponse> call, Response<MyResponse> response) {
                                         if(response.body().success == 1){
                                             Toast.makeText(OrderStatus.this, "Your Order Was Updated", Toast.LENGTH_LONG).show();

                                         }else {
                                             Toast.makeText(OrderStatus.this, "Your Order Was Updated but faailedto send Notification", Toast.LENGTH_LONG).show();

                                         }
                                     }

                                     @Override
                                     public void onFailure(retrofit2.Call<MyResponse> call, Throwable t) {
                                         Log.e("ERROR",t.getMessage());
                                     }
                                 });
                     }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
