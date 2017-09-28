package com.example.driverdelight;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton phoneButton = (ImageButton)findViewById(R.id.phoneButton);
        ImageButton spotifyButton = (ImageButton)findViewById(R.id.spotifyButton);
        phoneButton.setOnClickListener(this);
        spotifyButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {

            case R.id.phoneButton:
                intent.setClass(getBaseContext(), PhoneActivity.class);
                break;


            case R.id.spotifyButton:
                try {
                    intent.setComponent(new ComponentName("com.spotify.music", "com.spotify.music.MainActivity"));
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                } catch ( ActivityNotFoundException e ) {
                    Log.d("ONCLICK", e.toString());
                }
                break;

        }
        startActivity(intent);
    }
}
