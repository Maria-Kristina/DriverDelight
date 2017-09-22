package com.example.driverdelight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
                intent.setClass(getBaseContext(), SpotifyActivity.class);
                break;
            default:
                break;
        }
        //intent.putExtra("key", "content");
        startActivity(intent);

    }
}
