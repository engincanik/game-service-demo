package com.engin.testgame.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.engin.testgame.MainActivity;
import com.huawei.hms.jos.games.archive.ArchiveSummary;
import com.huawei.hms.jos.games.gamesummary.GameSummary;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BaseActivity extends Activity {
    StringBuffer sbLog = new StringBuffer();

    public HuaweiIdAuthParams getHuaweiIdParams() {
        return new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).createParams();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    public void clickMainMenu() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0,0);
        finish();
    }


    public void showLog(String logLine) {
        show(logLine);
    }

    protected void show(String logLine) {
        DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.ENGLISH);
        String time = format.format(new Date());

        sbLog.append(time).append(":").append(logLine);
        sbLog.append('\n');
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.i("LOGGING", sbLog.toString());
            }
        });
    }

    protected AuthHuaweiId getAuthHuaweiId() {
        return SignInCenter.get().getAuthHuaweiId();
    }

    public void guideToAgreeDriveProtocol() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle("Turn On Drive First");
        builder.setMessage("Use game save, you need to turn On Drive First");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("https://lfcloudtestbedportal.hwcloudtest.cn:18447/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BaseActivity.this, "reject drive protocol", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void showPlayerAndGame(ArchiveSummary archiveSummary) {
        Player player = archiveSummary.getGamePlayer();
        if (player != null) {
            showLog("Player");
            showLog("PlayerId:" + player.getPlayerId());
            showLog("displayName:" + player.getDisplayName());
            showLog("playerLevel:" + player.getLevel());
            showLog("iconImageUri:" + player.getIconImageUri());
            showLog("hiResImageUri:" + player.getHiResImageUri());
            showLog("signTs:" + player.getSignTs());
        } else {
            showLog("Player:null");
        }

        GameSummary gameSummary = archiveSummary.getGameSummary();
        if (gameSummary != null) {
            showLog("gameSummary");
            showLog("achievementTotalCount:" + gameSummary.getAchievementCount());
            showLog("rankingCount:" + gameSummary.getRankingCount());
            showLog("appId:" + gameSummary.getAppId());
            showLog("game displayName:" + gameSummary.getGameName());
            showLog("primaryCategory:" + gameSummary.getFirstKind());
            showLog("secondaryCategory:" + gameSummary.getSecondKind());
            showLog("Description:" + gameSummary.getDescInfo());
            showLog("iconImageUri:" + gameSummary.getGameIconUri());
            showLog("hiResImageUri:" + gameSummary.getGameHdImgUri());
        } else {
            showLog("gameSummary:null");
        }
    }
}
