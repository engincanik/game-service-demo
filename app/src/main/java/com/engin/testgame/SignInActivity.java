package com.engin.testgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.engin.testgame.common.BaseActivity;
import com.engin.testgame.common.SignInCenter;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.ArchivesClient;
import com.huawei.hms.jos.games.GameScopes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity {
    public static final String TAG = "SignIn";
    private String playerId;
    private boolean hasInit = false;
    private final static int SIGN_IN_INTENT = 3000;
    private static int TIME_OUT = 2500;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    TextView textView;
    final LoadingDialog loadingDialog = new LoadingDialog(SignInActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("com.engin.testgame", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("currentID", null) != null) {
//            signIn();
            Intent intent = new Intent(SignInActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick(R.id.btn_huawei_sign)
    public void signIn() {
        List<Scope> scopes = new ArrayList<>();
        scopes.add(GameScopes.DRIVE_APP_DATA);
        HuaweiIdAuthParams huaweiIdAuthParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).setScopeList(scopes).createParams();
        Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.getService(this,
                huaweiIdAuthParams).silentSignIn();
        authHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId authHuaweiId) {
                ArchivesClient client = Games.getArchiveClient(SignInActivity.this, authHuaweiId);
                Log.i(TAG, "Sign in successful");
                loadingDialog.startLoadingDialog();
                SignInCenter.get().updateAuthHuaweiId(authHuaweiId);
                if (SignInCenter.get().getAuthHuaweiId() != null) {
                    sharedPreferences = getSharedPreferences("com.engin.testgame", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("currentID", SignInCenter.get().getAuthHuaweiId().toString()).apply();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                        Intent intent = new Intent(SignInActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, TIME_OUT);
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
        Intent intent = HuaweiIdAuthManager.getService(SignInActivity.this, getHuaweiIdParams())
                .getSignInIntent();
        startActivityForResult(intent, SIGN_IN_INTENT);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SIGN_IN_INTENT == requestCode) {
            handleSignInResult(data);
        }
    }

    private void handleSignInResult (Intent data) {
        if (null == data) {
            Log.e(TAG, "Sign in intent is null");
            return;
        }
        String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
        if (TextUtils.isEmpty(jsonSignInResult)) {
            Log.e(TAG, "Sign in result is empty");
            return;
        }
        try {
            HuaweiIdAuthResult signInResult = new HuaweiIdAuthResult().fromJson(jsonSignInResult);
            if (signInResult.getStatus().getStatusCode() == 0) {
                Log.i(TAG, "Sign in successful");
                SignInCenter.get().updateAuthHuaweiId(signInResult.getHuaweiId());
            } else {
                Log.e(TAG, "Sign in failed: " +signInResult.getStatus().getStatusCode());
            }
        } catch (JSONException var) {
            Log.i(TAG, "Failed to convert json from sign in result");
        }
    }
}
