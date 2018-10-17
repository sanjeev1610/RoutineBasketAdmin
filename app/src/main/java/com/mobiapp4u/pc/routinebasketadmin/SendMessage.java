package com.mobiapp4u.pc.routinebasketadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketadmin.Common.Common;
import com.mobiapp4u.pc.routinebasketadmin.Modal.DataMessage;
import com.mobiapp4u.pc.routinebasketadmin.Modal.MyResponse;
import com.mobiapp4u.pc.routinebasketadmin.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {
    FButton btn_send_msg;
    MaterialEditText edit_title,edit_msg;
    APIService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mService = Common.getFCMService();
        edit_title = (MaterialEditText)findViewById(R.id.edit_title);
        edit_msg = (MaterialEditText)findViewById(R.id.edit_message);
        btn_send_msg = (FButton)findViewById(R.id.btn_send_msg);
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Notification notification = new Notification(edit_title.getText().toString(),edit_msg.getText().toString());
//                Sender sender = new Sender();
//                sender.to = new StringBuilder("/topics/").append(Common.TOPIC_NEWS).toString();
//                sender.notification = notification;
                Map<String,String> dataSend = new HashMap<>();
                dataSend.put("title",edit_title.getText().toString());
                dataSend.put("message",edit_msg.getText().toString());
                DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(Common.TOPIC_NEWS).toString(),dataSend);

                mService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(SendMessage.this,"Message Sent Successfully",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(SendMessage.this,"Message Not Sent"+t.getMessage(),Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        });

    }
}
