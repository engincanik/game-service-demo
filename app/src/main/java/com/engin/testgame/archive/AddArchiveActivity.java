package com.engin.testgame.archive;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.engin.testgame.R;
import com.engin.testgame.common.BaseActivity;
import com.engin.testgame.common.SignInCenter;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.ArchivesClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.GamesStatusCodes;
import com.huawei.hms.jos.games.archive.Archive;
import com.huawei.hms.jos.games.archive.ArchiveDetails;
import com.huawei.hms.jos.games.archive.ArchiveSummary;
import com.huawei.hms.jos.games.archive.ArchiveSummaryUpdate;
import com.huawei.hms.jos.games.archive.OperationResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.engin.testgame.MainActivity.TAG;

public class AddArchiveActivity extends BaseActivity {
    private boolean hasThumbnail;
    private Bitmap bitmap;
    private ArchivesClient archivesClient;
    private String archiveId;

    private String description;

    private long playedTime;

    private long progress;
    private String id;

    @BindView(R.id.not_support_image)
    public RadioButton notSupportImage;

    @BindView(R.id.support_image)
    public RadioButton supportImageRadio;

    @BindView(R.id.not_support)
    public RadioButton notSupport;

    @BindView(R.id.support)
    public RadioButton support;

    @BindView(R.id.metadata_description)
    public EditText editTextDescription;

    @BindView(R.id.metadata_playedTime)
    public EditText editTextPlayedTime;

    @BindView(R.id.metadata_progress)
    public EditText editTextProgress;

    @BindView(R.id.image_cover)
    public ImageView coverImage;

    @BindView(R.id.et_image_type)
    public EditText editTextImageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive_add_dialog);
        ButterKnife.bind(this);
        initData();
    }

    private synchronized ArchivesClient getArchivesClient() {
        if (archivesClient == null) {
            archivesClient = Games.getArchiveClient(AddArchiveActivity.this, SignInCenter.get().getAuthHuaweiId());
        }
        return archivesClient;
    }

    private void initData() {
        if (!TextUtils.isEmpty(description)) {
            editTextDescription.setText(description);
        }

        if (playedTime != 0) {
            editTextPlayedTime.setText(String.valueOf(playedTime));
        }

        if (progress != 0) {
            editTextProgress.setText(String.valueOf(progress));
        }

        if (progress != 0) {
            editTextProgress.setText(String.valueOf(progress));
        }

        if (hasThumbnail) {
            final RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);
            Task<Bitmap> coverImageTask = getArchivesClient().getThumbnail(id);
            coverImageTask.addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap1) {
                    bitmap = bitmap1;
                    Glide.with(getApplicationContext()).load(bitmap1).apply(options).into(coverImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    if (e instanceof ApiException) {
                        Toast.makeText(getApplicationContext(),"load image failed"+ ((ApiException) e).getStatusCode(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.commit)
    public void commit() {
        String description = editTextDescription.getText().toString();
        long playedTime = String2Long(editTextPlayedTime.getText().toString());
        long progress = String2Long(editTextProgress.getText().toString());
        boolean support = !notSupport.isChecked();
        boolean supportImage = supportImageRadio.isChecked();

        if (TextUtils.isEmpty(description) && playedTime == 0 && progress == 0 && bitmap == null) {
            Log.w(TAG, "add archive failed, params is null");
        } else {
            if (bitmap == null && hasThumbnail) {
                try {
                    bitmap = returnBitMap();
                } catch (InterruptedException e) {
                    Log.w(TAG, "add archive failed, params is null InterruptedException");
                }
            }
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
            }

            String imageType = editTextImageType.getText().toString();
            ArchiveSummaryUpdate.Builder builder = new ArchiveSummaryUpdate.Builder().setActiveTime(playedTime)
                    .setCurrentProgress(progress)
                    .setDescInfo(description);
            if (supportImage) {
                builder.setThumbnail(bitmap).setThumbnailMimeType(imageType);
            }
            ArchiveSummaryUpdate archiveMetadataChange = builder.build();
            ArchiveDetails archiveContents = new ArchiveDetails.Builder().build();
            archiveContents.set((progress + description + playedTime).getBytes());

            if (TextUtils.isEmpty(archiveId)) {
                Task<ArchiveSummary> task = getArchivesClient().addArchive(archiveContents, archiveMetadataChange, support);
                task.addOnSuccessListener(new OnSuccessListener<ArchiveSummary>() {
                    @Override
                    public void onSuccess(ArchiveSummary archiveSummary) {
                        if (archiveSummary != null) {
                            showLog("UniqueName:" + archiveSummary.getFileName());
                            showLog("PlayedTime:" + archiveSummary.getActiveTime());
                            showLog("ProgressValue:" + archiveSummary.getCurrentProgress());
                            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String formatedDate = newFormat.format(archiveSummary.getRecentUpdateTime());
                            showLog("ModifiedTimestamp:" + formatedDate);
                            showLog("CoverImageAspectRatio:" + archiveSummary.getThumbnailRatio());
                            showLog("hasThumbnail:" + archiveSummary.hasThumbnail());
                            showLog("ArchiveId:" + archiveSummary.getId());
                            showLog("Description:" + archiveSummary.getDescInfo());

                            showPlayerAndGame(archiveSummary);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        ApiException apiException = (ApiException) e;
                        final String content = "add result:" + apiException.getStatusCode();
                        Toast.makeText(AddArchiveActivity.this, content, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Task<OperationResult> task =
                        getArchivesClient().updateArchive(archiveId, archiveMetadataChange, archiveContents);

                task.addOnSuccessListener(new OnSuccessListener<OperationResult>() {
                    @Override
                    public void onSuccess(OperationResult archiveDataOrConflict) {
                        showLog("isDifference:"
                                + ((archiveDataOrConflict == null) ? "" : archiveDataOrConflict.isDifference()));
                        if (archiveDataOrConflict != null && !archiveDataOrConflict.isDifference()) {
                            Archive archive = archiveDataOrConflict.getArchive();
                            if (archive != null && archive.getSummary() != null) {
                                showLog("ArchiveId:" + archive.getSummary().getId());
                                try {
                                    showLog("content:" + new String(archive.getDetails().get(), "UTF-8"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                showLog("UniqueName:" + archive.getSummary().getFileName());
                                showLog("PlayedTime:" + archive.getSummary().getActiveTime());
                                showLog("ProgressValue:" + archive.getSummary().getCurrentProgress());
                                SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
                                String formatedDate = newFormat.format(archive.getSummary().getRecentUpdateTime());
                                showLog("ModifiedTimestamp:" + formatedDate);
                                showLog("CoverImageAspectRatio:" + archive.getSummary().getThumbnailRatio());
                                showLog("Description:" + archive.getSummary().getDescInfo());
                                showLog("hasThumbnail:" + archive.getSummary().hasThumbnail());

                                showPlayerAndGame(archive.getSummary());
                            }
                        } else {
                            // Conflict resolution
//                            handleConflict(archiveDataOrConflict);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        ApiException apiException = (ApiException) e;
                        showLog("loadArchiveDetails result:" + apiException.getStatusCode());
                    }
                });
            }

        }
    }
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public Bitmap returnBitMap() throws InterruptedException {
        if (hasThumbnail) {
            Task<Bitmap> coverImageTask = getArchivesClient().getThumbnail(id);
            coverImageTask.addOnSuccessListener(new OnSuccessListener<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap1) {
                    bitmap = bitmap1;
                    countDownLatch.countDown();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    if (e instanceof ApiException) {
                        Toast.makeText(getApplicationContext(),"load image failed"+ ((ApiException) e).getStatusCode(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        countDownLatch.await(3000, TimeUnit.MILLISECONDS);
        return bitmap;
    }

    private long String2Long(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
