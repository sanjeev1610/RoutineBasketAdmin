package com.mobiapp4u.pc.routinebasketadmin;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    Button signup;
    TextView textLogo;
    FButton signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin = (FButton)findViewById(R.id.signActive);
        textLogo = (TextView)findViewById(R.id.logo_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/dancing_script_regular.ttf");
        textLogo.setTypeface(typeface);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Signin.class));
            }
        });
    }
}
