package com.engin.testgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.engin.testgame.common.BaseActivity;
import com.engin.testgame.common.SignInCenter;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.GameSummaryClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.buoy.BuoyClient;
import com.huawei.hms.jos.games.gamesummary.GameSummary;
import com.huawei.hms.jos.games.playerstats.GamePlayerStatistics;
import com.huawei.hms.jos.games.playerstats.GamePlayerStatisticsClient;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity {
    StringBuilder builder;
    TextView logTv;
    private static boolean ISREALTIME = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        logTv = findViewById(R.id.logTv);
    }

    @OnClick(R.id.btn_server)
    public void getGameSummaryFromHuaweiServer() {
        GameSummaryClient client = Games.getGameSummaryClient(this, getAuthHuaweiId());
        Task<GameSummary> task = client.getGameSummary();
        task.addOnSuccessListener(new OnSuccessListener<GameSummary>() {
            @Override
            public void onSuccess(GameSummary gameSummary) {
                builder = new StringBuilder();
                builder.append("achievementCount:" + gameSummary.getAchievementCount()+"\n");
                builder.append("appId:" + gameSummary.getAppId()+"\n"  );
                builder.append("descInfo:" + gameSummary.getDescInfo() +"\n" );
                builder.append("gameName:" + gameSummary.getGameName() +"\n");
                builder.append("gameHdImgUri:" + gameSummary.getGameHdImgUri().toString()+"\n" );
                builder.append("gameIconUri:" + gameSummary.getGameIconUri().toString() +"\n");
                builder.append("rankingCount:" + gameSummary.getRankingCount()+"\n");
                builder.append("firstKind:" + gameSummary.getFirstKind()+"\n");
                builder.append("secondKind:" + gameSummary.getSecondKind()+"\n");
                logTv.setText(builder.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
//                    showLog(result);
                }
            }
        });
    }

    @OnClick(R.id.btn_local)
    public void getLocalGameSummary() {
        GameSummaryClient client = Games.getGameSummaryClient(this, getAuthHuaweiId());
        Task<GameSummary> task = client.getLocalGameSummary();
        task.addOnSuccessListener(new OnSuccessListener<GameSummary>() {
            @Override
            public void onSuccess(GameSummary gameSummary) {
                builder = new StringBuilder();
                builder.append("achievementCount:" + gameSummary.getAchievementCount()  +"\n");
                builder.append("appId:" + gameSummary.getAppId() +"\n" );
                builder.append("descInfo:" + gameSummary.getDescInfo() +"\n" );
                builder.append("gameName:" + gameSummary.getGameName()+"\n" );
                builder.append("gameHdImgUri:" + gameSummary.getGameHdImgUri().toString()+"\n" );
                builder.append("gameIconUri:" + gameSummary.getGameIconUri().toString() +"\n");
                builder.append("rankingCount:" + gameSummary.getRankingCount()+"\n");
                builder.append("firstKind:" + gameSummary.getFirstKind()+"\n");
                builder.append("secondKind:" + gameSummary.getSecondKind()+"\n");
                logTv.setText(builder.toString());
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

    @OnClick(R.id.btn_stats)
    public void getCurrentPlayerStats() {
//        initIsRealTime();
        GamePlayerStatisticsClient playerStatsClient = Games.getGamePlayerStatsClient(this, getAuthHuaweiId());
        Task<GamePlayerStatistics> task = playerStatsClient.getGamePlayerStatistics(ISREALTIME);
        task.addOnSuccessListener(new OnSuccessListener<GamePlayerStatistics>() {
            @Override
            public void onSuccess(GamePlayerStatistics gamePlayerStatistics) {
                if (gamePlayerStatistics == null) {
                    showLog("playerStatsAnnotatedData is null, inner error");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("IsRealTime:" + ISREALTIME +"\n");
                sb.append("AverageSessionLength: " + gamePlayerStatistics.getAverageOnLineMinutes()+"\n" );
                sb.append("DaysSinceLastPlayed: " + gamePlayerStatistics.getDaysFromLastGame()+"\n" );
                sb.append("NumberOfPurchases: " + gamePlayerStatistics.getPaymentTimes() +"\n");
                sb.append("NumberOfSessions: " + gamePlayerStatistics.getOnlineTimes() +"\n");
                sb.append("TotalPurchasesAmountRange: " + gamePlayerStatistics.getTotalPurchasesAmountRange()+"\n");
                logTv.setText(sb.toString());

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

}
