package com.mobiapp4u.pc.routinebasketadmin;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketadmin.Modal.Shipper;
import com.mobiapp4u.pc.routinebasketadmin.ViewHolder.ShipperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagement extends AppCompatActivity {

    FirebaseRecyclerAdapter<Shipper,ShipperViewHolder> adapter;
    FirebaseDatabase fd;
    DatabaseReference shippers;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);
        fd = FirebaseDatabase.getInstance();
        shippers = fd.getReference("Shippers");
        recyclerView = (RecyclerView)findViewById(R.id.recycler_shipper_mgmnt);
        fab = (FloatingActionButton)findViewById(R.id.floating_add_shipper);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateShipper();
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        loadShippers();
    }

    private void showCreateShipper() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(R.drawable.ic_local_shipping_black_24dp)
                .setTitle("Create Shipper")
                .setMessage("Please Fill details..")
                .setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View createShipper = inflater.inflate(R.layout.shipper_create_view,null);
        final MaterialEditText shipperName = (MaterialEditText)createShipper.findViewById(R.id.edit_shipper_name);
        final MaterialEditText shipperPhone = (MaterialEditText)createShipper.findViewById(R.id.edit_shipper_phone);
        final MaterialEditText shipperPwd = (MaterialEditText)createShipper.findViewById(R.id.edit_shipper_pwd);
        alert.setView(createShipper);
        alert.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Shipper shipper1 = new Shipper();
                shipper1.setName(shipperName.getText().toString());
                shipper1.setPassword(shipperPwd.getText().toString());
                shipper1.setPhone(shipperPhone.getText().toString());

                shippers.child(shipperPhone.getText().toString())
                        .setValue(shipper1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this,"Creating Shipper Successfully",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShipperManagement.this,"Creating Shipper failure"+e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();

    }

    private void loadShippers() {
        adapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(Shipper.class,R.layout.shipper_layout,
                ShipperViewHolder.class, shippers) {
            @Override
            protected void populateViewHolder(ShipperViewHolder viewHolder, final Shipper model, final int position) {
             viewHolder.txtShipName.setText(model.getName());
             viewHolder.txtShipPhone.setText(model.getPhone());
             viewHolder.btnedit.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     showEditDialog(adapter.getRef(position).getKey(),model);

                 }
             });
             viewHolder.btnremove.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     removeShipper(adapter.getRef(position).getKey());
                 }
             });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void removeShipper(String key) {
        shippers.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagement.this,"Edit Shipper Successfully",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShipperManagement.this,"Edit Shipper Failure",Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void showEditDialog(String key,Shipper modal) {
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setIcon(R.drawable.ic_local_shipping_black_24dp)
                    .setTitle("Craate Shipper")
                    .setMessage("Please Fill details..")
                    .setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(this);
            View createShipper = inflater.inflate(R.layout.shipper_create_view,null);
            final MaterialEditText shipperName = (MaterialEditText)createShipper.findViewById(R.id.edit_shipper_name);
            final MaterialEditText shipperPhone = (MaterialEditText)createShipper.findViewById(R.id.edit_shipper_phone);
            final MaterialEditText shipperPwd = (MaterialEditText)createShipper.findViewById(R.id.edit_shipper_pwd);

            shipperName.setText(modal.getName());
            shipperPhone.setText(modal.getPhone());
            shipperPwd.setText(modal.getPassword());

            alert.setView(createShipper);
            alert.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Map<String,Object> update = new HashMap<>();
                    update.put("name",shipperName.getText().toString());
                    update.put("phone",shipperPhone.getText().toString());
                    update.put("password",shipperPwd.getText().toString());

                    shippers.child(shipperPhone.getText().toString())
                            .updateChildren(update)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ShipperManagement.this,"Creating Shipper Successfully",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ShipperManagement.this,"Creating Shipper failure"+e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });

                }
            });
            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();

        }

    }
}
