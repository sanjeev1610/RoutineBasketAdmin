package com.mobiapp4u.pc.routinebasketadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketadmin.Common.Common;
import com.mobiapp4u.pc.routinebasketadmin.Modal.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;

public class Signin extends AppCompatActivity {
    FButton btn_signin;
    EditText edit_phone, edit_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        btn_signin = (FButton) findViewById(R.id.signIn);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_pwd = (EditText) findViewById(R.id.edit_pwd);

        final FirebaseDatabase fd = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = fd.getReference("Users");
        btn_signin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(Signin.this);
                mDialog.setMessage("Please wait......");
                mDialog.show();

                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(edit_phone.getText().toString()).exists()) {
                            mDialog.dismiss();
                            User user = dataSnapshot.child(edit_phone.getText().toString()).getValue(User.class);
                            user.setPhone(edit_phone.getText().toString());
                            if (Boolean.parseBoolean(user.getIsStaff())) {

                                if (user.getPassword().equals(edit_pwd.getText().toString())) {
                                    Common.currentUser = user;

                                    startActivity(new Intent(Signin.this, Home.class));
                                    finish();

                                    Toast.makeText(Signin.this, "Authentication sucess", Toast.LENGTH_LONG).show();

                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(Signin.this, "Authentication password error", Toast.LENGTH_LONG).show();

                                }
                            } else {
                                Toast.makeText(Signin.this, "Authentication  error", Toast.LENGTH_LONG).show();

                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(Signin.this, "Authentication failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }//oncreate

}