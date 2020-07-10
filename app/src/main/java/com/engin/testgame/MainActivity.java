package com.engin.testgame;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.engin.testgame.achievement.AchievementActivity;
import com.engin.testgame.common.BaseActivity;
import com.engin.testgame.common.ConnectClientSupport;
import com.engin.testgame.common.SignInCenter;
import com.engin.testgame.event.EventListActivity;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.AppPlayerInfo;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.GamesStatusCodes;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.jos.games.player.PlayersClientImpl;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.game.GameUserData;
import com.huawei.hms.support.api.game.GameLoginHandler;
import com.huawei.hms.support.api.game.GameLoginResult;
import com.huawei.hms.support.api.game.HuaweiGame;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    private final static int SIGN_IN_INTENT = 3000;
    private final static int PAY_PROTOCOL_INTENT = 3001;
    private final static int PAY_INTENT = 3002;
    private final static int HEARTBEAT_TIME = 15 * 60 * 1000;

    private String playerId;
    private String sessionId = null;
    private boolean hasInit = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "Started");
        if (!hasInit) {
            TextView initTextView = findViewById(R.id.initTv);
            initTextView.setText("Init: False");
        }

//        findViewById(R.id.btn_init).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                init();
//            }
//        });

//        findViewById(R.id.btn_startGame).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(playerId)) {
//                    Toast.makeText(MainActivity.this, "Get the current user first", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
//                    intent.putExtra("playerId", playerId);
//                    startActivity(intent);
//                }
//            }
//        });

//        findViewById(R.id.btn_achievement).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, AchievementActivity.class);
//                startActivity(intent);
//            }
//        });

//        findViewById(R.id.btn_events).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, EventListActivity.class);
//                startActivity(intent);
//            }
//        });

//        findViewById(R.id.btn_leaderboard).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, LeaderBoardActivity.class);
//                intent.putExtra("LB", 1);
//                startActivity(intent);
//            }
//        });

//        findViewById(R.id.btn_detail).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // back
//        gameEnd();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // hideFloatWindow();
//        hideFloatWindowNewWay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // front
//        gameBegin();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        showFloatWindowNewWay();
        Log.e(TAG, "onResume");
    }

//    public void init() {
//        JosAppsClient appsClient = JosApps.getJosAppsClient(this, getAuthHuaweiId());
//        appsClient.init();
//        showLog("init success");
//        hasInit = true;
//    }

//    private void showFloatWindowNewWay() {
//        if (!hasInit) {
//            init();
//        }
//        Games.getBuoyClient(this).showFloatWindow();
//    }
//
//    private void hideFloatWindowNewWay() {
//        Games.getBuoyClient(this).hideFloatWindow();
//    }


//    public void signIn(View view) {
//        Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.getService(this, getHuaweiIdParams()).silentSignIn();
//        authHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
//            @Override
//            public void onSuccess(AuthHuaweiId authHuaweiId) {
//                showLog("signIn success");
//                showLog("display:" + authHuaweiId.getDisplayName());
//                SignInCenter.get().updateAuthHuaweiId(authHuaweiId);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                if (e instanceof ApiException) {
//                    ApiException apiException = (ApiException) e;
//                    showLog("signIn failed:" + apiException.getStatusCode());
//                    showLog("start getSignInIntent");
//                    signInNewWay();
//                }
//            }
//        });
//    }

//    public void signInNewWay() {
//        Intent intent = HuaweiIdAuthManager.getService(MainActivity.this, getHuaweiIdParams()).getSignInIntent();
//        startActivityForResult(intent, SIGN_IN_INTENT);
//    }

    private void startActivityForResult(Activity activity, Status status, int reqCode) {
        if (status.hasResolution()) {
            try {
                status.startResolutionForResult(activity, reqCode);
            } catch (IntentSender.SendIntentException exp) {
                showLog(exp.getMessage());
            }
        } else {
            showLog("intent is null");
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (SIGN_IN_INTENT == requestCode) {
//            handleSignInResult(data);
//        } else if (PAY_PROTOCOL_INTENT == requestCode) {
////            iapBuy();
//        } else if (PAY_INTENT == requestCode) {
////            handlePayResult(data);
//        } else {
//            showLog("unknown requestCode in onActivityResult");
//        }
//    }

//    private void handleSignInResult(Intent data) {
//        if (null == data) {
//            showLog("signIn inetnt is null");
//            return;
//        }
////        HuaweiIdSignIn.getSignedInAccountFromIntent(data);
//        String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
//        if (TextUtils.isEmpty(jsonSignInResult)) {
//            showLog("SignIn result is empty");
//            return;
//        }
//        try {
//            HuaweiIdAuthResult
//                    signInResult = new HuaweiIdAuthResult
//                    ().fromJson(jsonSignInResult);
//            if (0 == signInResult.getStatus().getStatusCode()) {
//                showLog("Sign in success.");
//                showLog("Sign in result: " + signInResult.toJson());
//                SignInCenter.get().updateAuthHuaweiId(signInResult.getHuaweiId());
//            } else {
//                showLog("Sign in failed: " + signInResult.getStatus().getStatusCode());
//            }
//        } catch (JSONException var7) {
//            showLog("Failed to convert json from signInResult.");
//        }
//    }

    public void gameBegin() {
        if (TextUtils.isEmpty(playerId)) {
            showLog("GetCurrentPlayer first.");
            return;
        }
        String uid = UUID.randomUUID().toString();
        PlayersClient client = Games.getPlayersClient(this, getAuthHuaweiId());
        Task<String> task = client.submitPlayerEvent(playerId, uid, "GAMEBEGIN");
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String jsonRequest) {
                if (jsonRequest == null) {
                    showLog("jsonRequest is null");
                    return;
                }
                try {
                    JSONObject data = new JSONObject(jsonRequest);
                    sessionId = data.getString("transactionId");
                } catch (JSONException e) {
                    showLog("parse jsonArray meet json exception");
                    return;
                }
                showLog("submitPlayerEvent traceId: " + jsonRequest);
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

    public void gameEnd() {
        if (TextUtils.isEmpty(playerId)) {
            showLog("GetCurrentPlayer first.");
            return;
        }
        if (TextUtils.isEmpty(sessionId)) {
            showLog("SessionId is empty.");
            return;
        }
        PlayersClient client = Games.getPlayersClient(this, getAuthHuaweiId());
        Task<String> task = client.submitPlayerEvent(playerId, sessionId, "GAMEEND");
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                showLog("submitPlayerEvent traceId: " + s);
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

    public void login(View view) {
        ConnectClientSupport.get().connect(this, new ConnectClientSupport.IConnectCallBack() {
            @Override
            public void onResult(HuaweiApiClient apiClient) {
                if (apiClient != null) {
                    PendingResult<GameLoginResult> pendingRst = HuaweiGame.HuaweiGameApi.login(apiClient, MainActivity.this, 1, new GameLoginHandler() {
                        @Override
                        public void onResult(int retCode, GameUserData userData) {
                            showLog("login result:" + retCode);
                            if (retCode == GamesStatusCodes.GAME_STATE_SUCCESS && userData != null) {
                                showLog("displayName:" + userData.getDisplayName());
                                showLog("playerId:" + userData.getPlayerId());
                            }
                        }

                        @Override
                        public void onChange() {

                        }
                    });
                    pendingRst.setResultCallback(new ResultCallback<GameLoginResult>() {
                        @Override
                        public void onResult(GameLoginResult result) {
                        }
                    });
                }
            }
        });
    }

//    public void getCurrentPlayer(View view) {
//        PlayersClientImpl client = (PlayersClientImpl) Games.getPlayersClient(this, getAuthHuaweiId());
//
//        Task<Player> task = client.getCurrentPlayer();
//        task.addOnSuccessListener(new OnSuccessListener<Player>() {
//            @Override
//            public void onSuccess(Player player) {
//                String result = "display:" + player.getDisplayName() + "\n"
//                        + "playerId:" + player.getPlayerId() + "\n"
//                        + "playerLevel:" + player.getLevel() + "\n"
//                        + "timestamp:" + player.getSignTs() + "\n"
//                        + "playerSign:" + player.getPlayerSign();
////                showLog(result);
//                TextView playerInfo = findViewById(R.id.playerInfoTv);
//                playerInfo.setText(result);
//                playerId = player.getPlayerId();
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                if (e instanceof ApiException) {
//                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
//                    showLog(result);
//                }
//            }
//        });
//    }


}
