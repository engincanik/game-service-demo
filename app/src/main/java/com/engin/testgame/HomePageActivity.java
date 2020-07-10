package com.engin.testgame;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.engin.testgame.achievement.AchievementActivity;
import com.engin.testgame.archive.ArchiveActivity;
import com.engin.testgame.archive.ArchiveListActivity;
import com.engin.testgame.common.BaseActivity;
import com.engin.testgame.common.SignInCenter;
import com.engin.testgame.event.EventListActivity;
import com.engin.testgame.savedGames.SavedGamesActivity;
import com.huawei.cloud.services.drive.DriveScopes;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.GameScopes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.jos.games.player.PlayersClientImpl;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePageActivity extends BaseActivity {
    private String playerId;
    private boolean hasInit = false;
    private final static int SIGN_IN_INTENT = 3000;
    public static final String TAG = "HomePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
//        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
        ButterKnife.bind(this);
        init();
        signIn();
//        getCurrentPlayer();
    }

    public void signIn() {
        List<Scope> scopes = new ArrayList<>();
        scopes.add(GameScopes.DRIVE_APP_DATA);
        HuaweiIdAuthParams huaweiIdAuthParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).setScopeList(scopes).createParams();
        Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.getService(this,
                huaweiIdAuthParams).silentSignIn();
        authHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId authHuaweiId) {
                Log.i(TAG, "Sign in successful");
                SignInCenter.get().updateAuthHuaweiId(authHuaweiId);
                getCurrentPlayer();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e(TAG, "Sign in failed: " + apiException.getStatusCode());
                    signInNewWay();
                }
            }
        });
    }

    public void signInNewWay() {
        Intent intent = HuaweiIdAuthManager.getService(HomePageActivity.this, getHuaweiIdParams())
                .getSignInIntent();
        startActivityForResult(intent, SIGN_IN_INTENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentPlayer();
    }

    public void init() {
        JosAppsClient appsClient = JosApps.getJosAppsClient(this, getAuthHuaweiId());
        appsClient.init();
        showLog("init success");
        hasInit = true;
    }

    public void getCurrentPlayer() {
        PlayersClientImpl client = (PlayersClientImpl) Games.getPlayersClient(this, getAuthHuaweiId());
        Task<Player> task = client.getCurrentPlayer();
        task.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                String result = "Hello, " + player.getDisplayName();
                TextView playerInfo = findViewById(R.id.playerTv);
                playerInfo.setText(result);
                playerId = player.getPlayerId();

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


    @OnClick(R.id.btn_achievement)
    public void openAchievements() {
        Intent intent = new Intent(HomePageActivity.this, AchievementActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_leaderboard)
    public void openLeaderboard() {
        Intent intent = new Intent(HomePageActivity.this, LeaderBoardActivity.class);
        intent.putExtra("LB", 1);
        startActivity(intent);
    }

    @OnClick(R.id.btn_startGame)
    public void startGame() {
        if (TextUtils.isEmpty(playerId)) {
            Toast.makeText(HomePageActivity.this, "Get the current user first", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(HomePageActivity.this, GameActivity.class);
            intent.putExtra("playerId", playerId);
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_events)
    public void openEvents() {
        Intent intent = new Intent(HomePageActivity.this, EventListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_detail)
    public void openDetails() {
        Intent intent = new Intent(HomePageActivity.this, DetailActivity.class);
        startActivity(intent);
    }

//    @OnClick(R.id.btn_saves)
//    public void openSaves() {
//        Intent intent = new Intent(HomePageActivity.this, SavedGamesActivity.class);
//        startActivity(intent);
//    }
}
