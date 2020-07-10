package com.engin.testgame.achievement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.engin.testgame.R;
import com.engin.testgame.common.BaseActivity;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.Games;

import org.json.JSONException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AchievementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.btn_get_achieve_intent)
    public void getAchievementIntent() {
        AchievementsClient client = Games.getAchievementsClient(this, getAuthHuaweiId());
        Task<Intent> task = client.getShowAchievementListIntent();
        task.addOnSuccessListener(new OnSuccessListener<Intent>() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent == null) {
                    showLog("intent = null");
                } else {
                    try {
                        startActivityForResult(intent, 1);
                    } catch (Exception e) {
                        showLog("Achievement Activity is Invalid");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    showLog(result);
                }
            }
        });
    }

    @OnClick(R.id.btn_load_achievement)
    public void loadAchievement() {
        loadAchievement(true);
    }

    @OnClick(R.id.btn_load_achievement_off)
    public void loadAchievementOff() {
        loadAchievement(false);
    }

    private void loadAchievement(boolean forceReload) {
        if (getAuthHuaweiId() == null) {
            showLog("signIn first");
            return;
        }
        String jString = "";
        try {
            jString = getAuthHuaweiId().toJson();
        } catch (JSONException e) {
            showLog("signIn first");
        }
        Intent intent = new Intent(this, AchievementListActivity.class);
        intent.putExtra("forceReload", forceReload);
        intent.putExtra("mSign", jString);
        startActivity(intent);
    }

}
