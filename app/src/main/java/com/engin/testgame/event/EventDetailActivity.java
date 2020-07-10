package com.engin.testgame.event;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.engin.testgame.R;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";
    public ImageView eventImage;

    public ImageView eventImage2;

    public TextView eventName;

    public TextView eventDes;


    public TextView eventStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        eventImage = findViewById(R.id.event_image);
        eventImage2 = findViewById(R.id.event_image2);
        eventName = findViewById(R.id.event_name);
        eventDes = findViewById(R.id.event_des);
        eventStep = findViewById(R.id.event_step);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("achievementName");
        String des = intent.getStringExtra("achievementDes");
        Uri unlockedImageUri =(Uri)intent.getParcelableExtra("unlockedImageUri");
        Uri rvealedImageUri =(Uri)intent.getParcelableExtra("rvealedImageUri");
        Glide.with(this).load(unlockedImageUri).into(eventImage);
        Glide.with(this).load(rvealedImageUri).into(eventImage2);
        eventName.setText(name);
        eventDes.setText(des);
    }
}
