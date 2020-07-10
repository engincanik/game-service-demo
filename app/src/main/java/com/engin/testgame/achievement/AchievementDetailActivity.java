package com.engin.testgame.achievement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.engin.testgame.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AchievementDetailActivity extends Activity {
    private static final String TAG = "AchievementDetaile";
    @BindView(R.id.achievement_image)
    public ImageView achievementImage;

    @BindView(R.id.achievement_image2)
    public ImageView achievementImage2;

    @BindView(R.id.achievement_name)
    public TextView achievementName;

    @BindView(R.id.achievement_des)
    public TextView achievementDes;

    @BindView(R.id.achievement_step)
    public TextView achievementStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_detail);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("achievementName");
        String des = intent.getStringExtra("achievementDes");
        Uri unlockedImageUri =(Uri)intent.getParcelableExtra("unlockedImageUri");
        Uri rvealedImageUri =(Uri)intent.getParcelableExtra("rvealedImageUri");
        Glide.with(this).load(unlockedImageUri).into(achievementImage);
        Glide.with(this).load(rvealedImageUri).into(achievementImage2);
        achievementName.setText(name);
        achievementDes.setText(des);
    }

    @OnClick(R.id.iv_back)
    public void backHome() {
        finish();
    }
}
