package com.app.cloudchat_volunteer.ui.live;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.cloudchat_volunteer.R;

import java.util.concurrent.TimeUnit;

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

public class InteractiveActivity extends AppCompatActivity {

    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private WebSocket webSocket;
    private OkHttpClient client;
    private static final String WS_URL = "ws://47.94.207.38:6000";
    private static final String TAG = "InteractiveActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_interactive);

        danmakuView = findViewById(R.id.danmaku_view);
        initDanmaku();
        connectWebSocket();
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

    private void addDanmaku(String text, boolean isSelf) {
        if (danmakuView == null || !danmakuView.isPrepared()) return;

        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null) return;

        danmaku.text = text;
        danmaku.textSize = 60f;
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        danmaku.priority = 1;
        danmaku.borderColor = isSelf ? Color.YELLOW : Color.TRANSPARENT;

        danmakuView.addDanmaku(danmaku);
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
                Log.e(TAG, "WebSocket error", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
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
