package com.engin.testgame.archive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.engin.testgame.R;
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
import com.huawei.hms.jos.games.GamesStatusCodes;
import com.huawei.hms.jos.games.archive.Archive;
import com.huawei.hms.jos.games.archive.ArchiveSummary;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArchiveListActivity extends BaseActivity implements ArchiveListAdapter.OnBtnClickListener {
    private final static int SIGN_IN_INTENT = 3000;

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;

    private ArrayList<ArchiveSummary> archiveSummaries = new ArrayList<>();
    private ArchivesClient client;
    private ArchiveListAdapter adapter;

    private synchronized ArchivesClient getClient() {
        if (client == null) {
            client = Games.getArchiveClient(this, SignInCenter.get().getAuthHuaweiId());
        }
        return client;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_list);
        ButterKnife.bind(this);
        requestData();

        recyclerView.requestLayout();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(1);
        adapter = new ArchiveListAdapter(this, archiveSummaries, this);
        recyclerView.setAdapter(adapter);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private synchronized void requestData() {
        boolean isRealTime = getIntent().getBooleanExtra("isRealTime", false);
        Task<List<ArchiveSummary>> task = getClient().getArchiveSummaryList(isRealTime);
        task.addOnSuccessListener(new OnSuccessListener<List<ArchiveSummary>>() {
            @Override
            public void onSuccess(List<ArchiveSummary> buffer) {
                archiveSummaries.clear();
                if (buffer == null){
                    showLog("archives is null");
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (ArchiveSummary archiveSummary : buffer) {
                    archiveSummaries.add(archiveSummary);
                }

                adapter.notifyDataSetChanged();
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

    private void loadThumbnail(Archive archive){
        if (archive.getSummary().hasThumbnail()) {
            Task<Bitmap> coverImageTask = Games.getArchiveClient(this, getAuthHuaweiId()).getThumbnail(archive.getSummary().getId());
            coverImageTask.addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
//                    Glide.with(getApplicationContext()).load(bitmap).into(archiveImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    if (e instanceof ApiException) {
                        Log.e("archive","load image failed"+ ((ApiException) e).getStatusCode());
                    }
                }
            });
        }
    }

    @OnClick(R.id.iv_back)
    public void backHome() {
        finish();
    }

    private long lastClickTime;

    @OnClick(R.id.iv_refresh)
    public synchronized void refresh() {
        if (lastClickTime == 0) {
            lastClickTime = System.currentTimeMillis();
            requestData();
        } else {
            if (System.currentTimeMillis() - lastClickTime > 1000) {
                requestData();
                lastClickTime = System.currentTimeMillis();
            }
        }
    }


    @Override
    public synchronized  void onItemClick(int position) {
        Intent intent = new Intent(this, ArchiveDetailActivity.class);
        if (archiveSummaries.size() > 0) {
            ArchiveSummary archiveSummary = archiveSummaries.get(position);
            Bundle data = new Bundle();
            data.putParcelable("archiveSummary", archiveSummary);
            intent.putExtras(data);
            startActivityForResult(intent, 8000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public HuaweiIdAuthParams getHuaweiIdParams() {
        List<Scope> scopes = new ArrayList<>();
        scopes.add(GameScopes.DRIVE_APP_DATA);
        return new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).setScopeList(scopes).createParams();
    }

    public void init() {
        JosAppsClient appsClient = JosApps.getJosAppsClient(this, getAuthHuaweiId());
        appsClient.init();
        showLog("init success");

        Task<AuthHuaweiId> AuthHuaweiIdTask =
                HuaweiIdAuthManager.getService(this, getHuaweiIdParams()).silentSignIn();
        AuthHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId AuthHuaweiId) {
                showLog("signIn success");
                showLog("display:" + AuthHuaweiId.getDisplayName());
                SignInCenter.get().updateAuthHuaweiId(AuthHuaweiId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    showLog("signIn failed:" + apiException.getStatusCode());
                    signInNewWay();
                }
            }
        });
    }

    public void signInNewWay() {
        Intent intent = HuaweiIdAuthManager.getService(ArchiveListActivity.this, getHuaweiIdParams()).getSignInIntent();
        startActivityForResult(intent, SIGN_IN_INTENT);
    }

}
