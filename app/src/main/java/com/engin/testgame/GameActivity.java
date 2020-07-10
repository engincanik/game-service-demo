package com.engin.testgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.engin.testgame.common.SignInCenter;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.EventsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.RankingsClient;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    ImageView[] imageList;
    int gameScore = 0;
    TextView scoreText;
    TextView timeText;
    String playerId;
    Intent intent;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ImageView imageView9;

    Handler handler;
    Runnable runnable;

    AchievementsClient client;
    EventsClient eventsClient;
    RankingsClient rankingsClient;

    private static final String TAG = "GameActivity";
    private static final String EVENT_ID = "07C6C1BA96B8BA98E1285106D5E1674E569833384E7A51E8CADAA66E89AE1445";
    private static final String LEADERBOARD_ID = "5C89B528D5B36CC0347769666727CC4ABAA0185828538FD4FD86F889C8E5D392";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        eventsClient = Games.getEventsClient(this, SignInCenter.get().getAuthHuaweiId());
        rankingsClient = Games.getRankingsClient(this, SignInCenter.get().getAuthHuaweiId());
        client = Games.getAchievementsClient(this, SignInCenter.get().getAuthHuaweiId());
        enableRankingSwitchStatus(1);
        gameScore = 0;
        scoreText  = findViewById(R.id.scoreText);
        timeText = findViewById(R.id.timeText);
        intent = getIntent();
        playerId = intent.getStringExtra("playerId");
        initiateImageViews();
        hideImages();
        startGame();
    }


    public void initiateImageViews() {
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageList = new ImageView[] {imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9};
    }

    public void hideImages() {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                for (ImageView image: imageList) {
                    image.setVisibility(View.INVISIBLE);
                }

                Random random = new Random();
                int i = random.nextInt(9);
                imageList[i].setVisibility(View.VISIBLE);
                handler.postDelayed(this, 500);
            }
        };
        handler.post(runnable);
    }

    public void startGame() {
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeText.setText("Time: " + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                timeText.setText("Time's Up");
                handler.removeCallbacks(runnable);
                for (ImageView image: imageList) {
                    image.setVisibility(View.INVISIBLE);
                }
                eventsClient.grow(EVENT_ID, gameScore);
                client.reachWithResult("314A028BA1B4225255A4DB793E228E562F85A4A61687439D5CF6AD0AA608CBA6");
                if (gameScore > 5) {
                    submitRanking(gameScore);
                    client.reachWithResult("53CC61A1E5AB7F7D0856189D1FDF27DFAEC0D34CA79235CDA88430A807102155");
                }


                AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                alert.setCancelable(false);
                alert.setTitle("Time's up");
                alert.setMessage("Your score is: " + gameScore + "\n" + "Do you want to play again?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(GameActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                alert.show();
            }
        }.start();
    }

    public void increaseScore (View view) {
        if (timeText.toString() != "Time's Up") {
            gameScore += 1;
            scoreText.setText("Score: " + gameScore);
        }
    }

    protected AuthHuaweiId getAuthHuaweiId() {
        return SignInCenter.get().getAuthHuaweiId();
    }

    private void submitRanking(int score) {
        rankingsClient.submitRankingScore(LEADERBOARD_ID, score);
    }


    private void enableRankingSwitchStatus (int status) {
        Task<Integer> task = rankingsClient.setRankingSwitchStatus(status);
        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer statusValue) {
                // success to set the value,the server will reponse the latest value.
                Log.d(TAG, "setRankingSwitchStatus success : " +statusValue) ;
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // errCode information
                if (e instanceof ApiException) {
                    String result = "Err Code:" + ((ApiException) e).getStatusCode();
                    Log.e(TAG , "setRankingSwitchStatus error : " + result);
                }
            }
        });
    }

}