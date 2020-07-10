package com.engin.testgame.event;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.engin.testgame.R;
import com.engin.testgame.common.SignInCenter;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.EventsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.event.Event;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventListActivity extends Activity implements EventListAdapter.OnBtnClickListener {


    public RecyclerView recyclerView;

    public TextView tvTitle;

    private ArrayList<Event> events = new ArrayList<>();
    private EventsClient client;
    private EventListActivity mContext;
    com.huawei.hms.support.hwid.result.AuthHuaweiId AuthHuaweiId =null;
    private boolean forceReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        recyclerView = findViewById(R.id.recycler_view);
        tvTitle = findViewById(R.id.tv_title);
        mContext = this;
        ButterKnife.bind(mContext);
        AuthHuaweiId = getAuthHuaweiId();
        tvTitle.setText("Events list");
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        forceReload = intent.getBooleanExtra("forceReload", false);
        String mSignString =intent.getStringExtra("mSign");
        String idsString =intent.getStringExtra("idsString");

        try {
            AuthHuaweiId = AuthHuaweiId.fromJson(mSignString);
        } catch (JSONException e) {
        }
        client = Games.getEventsClient(this, AuthHuaweiId);
        Task<List<Event>> task =null;
        if (TextUtils.isEmpty(idsString)) {
            task = client.getEventList(forceReload);
        }else {
            String[] eventIds = idsString.split(",");
            task = client.getEventListByIds(forceReload, eventIds);
        }
        addResultListener(task);
    }

    private void addResultListener(Task<List<Event>> task) {
        if (task == null){ return;}
        task.addOnSuccessListener(new OnSuccessListener<List<Event>>() {
            @Override
            public void onSuccess(List<Event> data) {
                if (data == null) {
                    showLog("eventBuffer is null");
                    return;
                }

                Iterator<Event> iterator = data.iterator();
                events.clear();
                while (iterator.hasNext()) {
                    Event event = iterator.next();
                    events.add(event);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                recyclerView.setLayoutManager(layoutManager);
                EventListAdapter adapter = new EventListAdapter(mContext, events, mContext);
                recyclerView.setAdapter(adapter);

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

    @OnClick(R.id.iv_back)
    public void backHome(View view) {
        finish();
    }

    private void showLog(String result) {
        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int postion) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        Event event = events.get(postion);
        intent.putExtra("achievementName",event.getName());
        intent.putExtra("achievementDes",event.getDescription());
        intent.putExtra("unlockedImageUri",event.getThumbnailUri());
        startActivity(intent);

    }

    protected com.huawei.hms.support.hwid.result.AuthHuaweiId getAuthHuaweiId() {
        return SignInCenter.get().getAuthHuaweiId();
    }

    @Override
    public void reportEvent(String eventId) {
        client.grow(eventId, 1);
    }
}