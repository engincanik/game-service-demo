package com.engin.testgame.savedGames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.engin.testgame.HomePageActivity;
import com.engin.testgame.R;
import com.engin.testgame.SignInActivity;
import com.engin.testgame.archive.AddArchiveActivity;
import com.engin.testgame.archive.ArchiveActivity;
import com.engin.testgame.archive.ArchiveListActivity;
import com.engin.testgame.common.BaseActivity;
import com.engin.testgame.common.SignInCenter;
import com.huawei.cloud.base.auth.DriveCredential;
import com.huawei.cloud.base.util.StringUtils;
import com.huawei.cloud.client.exception.DriveCode;
import com.huawei.cloud.services.drive.Drive;
import com.huawei.cloud.services.drive.DriveScopes;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.framework.common.Logger;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.ArchivesClient;
import com.huawei.hms.jos.games.GameScopes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.archive.ArchiveConstants;
import com.huawei.hms.jos.games.archive.ArchiveSummary;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.huawei.hms.support.hwid.request.HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM;

public class SavedGamesActivity extends BaseActivity {
    ArchivesClient archivesClient;
    public static final String TAG = "SavedGames";
    private static int REQUEST_SIGN_IN_LOGIN = 1002;
    AchievementsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);
        ButterKnife.bind(this);
        client = Games.getAchievementsClient(this, SignInCenter.get().getAuthHuaweiId());
        client.reachWithResult("90B367308A00F96A7CA42DBFFEF27CE6F6C1EEB3BBB9A3A1F1ED4B3A1C44310D");
    }


    @OnClick(R.id.getSaves)
    public void displaySavesAppAssistant() {
        archivesClient = Games.getArchiveClient(this, getAuthHuaweiId());
//        Task<Intent> task = client.getShowArchiveListIntent(title, allowAddBtn, allowDeleteBtn, maxArchive);
        Task<Intent> task = archivesClient.getShowArchiveListIntent("Saves", true, true, 2);
        task.addOnSuccessListener(new OnSuccessListener<Intent>() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    startActivityForResult(intent, 1);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e("archive", "statusCode:" + apiException.getStatusCode());
                }
            }
        });
    }

    @OnClick(R.id.saveBtn)
    public void openCommitPage() {
        Intent intent = new Intent(SavedGamesActivity.this, AddArchiveActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.loginBtn)
    void driveLogin() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(GameScopes.DRIVE_APP_DATA);

        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(DEFAULT_AUTH_REQUEST_PARAM)
                .setAccessToken()
                .setIdToken()
                .setScopeList(scopeList)
                .createParams();
        // Call the account API to get account information.
        HuaweiIdAuthService client = HuaweiIdAuthManager.getService(this, authParams);
        startActivityForResult(client.getSignInIntent(), REQUEST_SIGN_IN_LOGIN);

    }

}
