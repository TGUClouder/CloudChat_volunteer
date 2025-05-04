package com.app.cloudchat_volunteer.ui.live;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.AlivcLiveBaseListener;
import com.alivc.live.pusher.AlivcLivePushConstants;
import com.app.cloudchat_volunteer.R;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.AlivcFpsEnum;
import com.alivc.live.pusher.AlivcVideoEncodeGopEnum;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.app.cloudchat_volunteer.dao.ReplayDao;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class LiveActivity extends AppCompatActivity {

    private static final String TAG = "LiveActivity";
    private SurfaceView surfaceView;
    private Button startPushButton;
    private ImageButton switchCameraButton;

    private boolean isPushing = false;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private WebSocket webSocket;
    private OkHttpClient client;
    private AlivcLivePushConfig mPushConfig;
    private AlivcLivePusher mAliLivePusher;
    private static final String WS_URL = "ws://47.94.207.38:6000";
    private static final int REQUEST_MEDIA_PERMISSION = 300; // 权限请求码
    String theme;
    long mil_sec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏（如果使用的是支持 ActionBar 的主题）
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_live);
        danmakuView = findViewById(R.id.danmaku_view);
        switchCameraButton = findViewById(R.id.interactiveCameraButton);
        surfaceView = findViewById(R.id.interactiveView);
        startPushButton = findViewById(R.id.interactiveStreaming);
        switchCameraButton.setOnClickListener(v -> switchCamera_());
        startPushButton.setOnClickListener(v -> togglePushing());
        Intent intent = getIntent();
        theme = intent.getStringExtra("theme_");


        // 设置按钮事件
        // 注册 License 回调并注册 SDK
        AlivcLiveBase.setListener(new AlivcLiveBaseListener() {
            @Override
            public void onLicenceCheck(AlivcLivePushConstants.AlivcLiveLicenseCheckResultCode result, String reason) {
                Log.i(TAG, "onLicenceCheck: " + result + ", " + reason);
            }
        });
        AlivcLiveBase.registerSDK();

        // 添加 SurfaceHolder 回调，确保 Surface 可用后启动预览
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mAliLivePusher != null) {
                    try {
                        mAliLivePusher.startPreview(surfaceView);
                        Log.i(TAG, "Surface created, start preview.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LiveActivity.this, "预览启动失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mAliLivePusher != null) {
                    try {
                        mAliLivePusher.stopPreview();
                        Log.i(TAG, "Surface destroyed, stop preview.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        initDanmaku();
        connectWebSocket();
        initAliPush();
    }

    /**
     * 初始化 AliVCSDK 推流器与配置
     */
    private void initAliPush() {
        mPushConfig = new AlivcLivePushConfig();
        mPushConfig.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
        mPushConfig.setFps(AlivcFpsEnum.FPS_25);
        mPushConfig.setVideoEncodeGop(AlivcVideoEncodeGopEnum.GOP_TWO);
        mPushConfig.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT);

        mAliLivePusher = new AlivcLivePusher();
        try {
            mAliLivePusher.init(getApplicationContext(), mPushConfig);
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "推流初始化失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void addDanmaku(String text, boolean isSelf) {
        if (danmakuView == null || !danmakuView.isPrepared()) {
            Log.d(TAG, "DanmakuView is not prepared.");
            return;
        }

        // 创建弹幕
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) return;

        // 设置弹幕属性
        danmaku.text = text;
        danmaku.textSize = 60f;
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        danmaku.priority = 1;
        danmaku.borderColor = isSelf ? Color.YELLOW : Color.TRANSPARENT;

        danmakuView.addDanmaku(danmaku);
        Log.d(TAG, "Danmaku added: " + text);
    }

    private void initDanmaku() {
        danmakuContext = DanmakuContext.create();
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override public void prepared() { danmakuView.start(); }
            @Override public void updateTimer(DanmakuTimer timer) {}
            @Override public void drawingFinished() {}
            @Override public void danmakuShown(BaseDanmaku danmaku) {}
        });

        danmakuView.prepare(new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        }, danmakuContext);

        danmakuView.enableDanmakuDrawingCache(true);
    }
    private void connectWebSocket() {
        client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(WS_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override public void onOpen(WebSocket webSocket, Response response) {}

            @Override public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> addDanmaku(text, false));
            }

            @Override public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
            }

            @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e("PlayerActivity", "WebSocket error", t);
            }
        });
    }

    /**
     * 切换推流状态：未推流时启动推流；已推流时结束推流
     */
    private void togglePushing() {
        if (!isPushing) {
            String pushUrl = "rtmp://59.110.173.159/live/livestream";
            try {
                long timestampInMilliseconds = System.currentTimeMillis();
                Toast.makeText(this, "推流已启动-"+theme+":"+timestampInMilliseconds, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mil_sec = ReplayDao.get_mil_sec(theme);
                            if(mil_sec==-1){
                                ReplayDao.set_replay(theme, String.valueOf(timestampInMilliseconds));
                                Log.e("LiveActivity","insert");
                            } else if (mil_sec==-2) {
                                throw new Exception("Connection Failed!");
                            }else {
                                ReplayDao.update_replay(theme, String.valueOf(timestampInMilliseconds));
                                Log.e("LiveActivity","update");
                            }
                        } catch (Exception e) {
                            Log.e("LiveActivity",e.toString());
                        }
                    }
                }).start();
                mAliLivePusher.startPush(pushUrl);
                startPushButton.setText("结束推流");
                isPushing = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "推流失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                mAliLivePusher.stopPush();
                mAliLivePusher.stopPreview();
                Toast.makeText(this, "推流已停止", Toast.LENGTH_SHORT).show();
                startPushButton.setText("开始推流");
                isPushing = false;
                mAliLivePusher.startPreview(surfaceView);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "结束推流失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void switchCamera_(){
        if(mAliLivePusher!=null){
            mAliLivePusher.switchCamera();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT) {{
                setMargins(0, getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin), 0, 0);
            }});
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MEDIA_PERMISSION) {
            boolean allGranted = true;
            StringBuilder deniedPermissions = new StringBuilder();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allGranted = false;
                    deniedPermissions.append(permissions[i].substring(permissions[i].lastIndexOf('.') + 1))
                            .append(", ");
                }
            }
            if (!allGranted) {
                String message = "需要以下权限才能继续: \n" +
                        deniedPermissions.toString().replaceAll(", $", "");
                new AlertDialog.Builder(this)
                        .setTitle("权限请求")
                        .setMessage(message)
                        .setPositiveButton("去设置", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliLivePusher != null) {
            try {
                mAliLivePusher.startPreview(surfaceView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAliLivePusher != null) {
            try {
                mAliLivePusher.stopPush();
                mAliLivePusher.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAliLivePusher != null) {
            try {
                mAliLivePusher.destroy();
                mAliLivePusher = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (danmakuView != null) {
            danmakuView.release();
        }
        if (webSocket != null) {
            webSocket.close(1000, "Activity stopped");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
}
