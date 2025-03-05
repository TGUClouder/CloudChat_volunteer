package com.example.cloudchat_volunteer.ui.live;

import static android.view.TouchDelegate.BELOW;
import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.cloudchat_volunteer.R;
import com.google.common.util.concurrent.ListenableFuture;
import android.view.View;



public class LiveActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private boolean isFrontCamera = false; // 变量已正确声明
    private TextView liveText;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        TextView liveText = findViewById(R.id.liveText);
        ImageButton toggleOrientationButton = findViewById(R.id.toggleOrientationButton);//横转
        ImageButton switchCameraButton = findViewById(R.id.switchCameraButton);//反转
        switchCameraButton.setRotation(180); // 旋转 180 度
        previewView = findViewById(R.id.previewView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCameraPreview();
        }

        toggleOrientationButton.setOnClickListener(v -> toggleOrientation());
        switchCameraButton.setOnClickListener(v -> switchCamera());

        startCameraPreview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "You need to enable camera permission to use this feature",
                        Toast.LENGTH_LONG).show();
            } else {
                startCameraPreview();
            }
        }
    }

    private void startCameraPreview() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 隐藏标题栏
            liveText.setVisibility(View.GONE);
            // 让 PreviewView 占满全屏
            previewView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {
            // 显示标题栏
            liveText.setVisibility(View.VISIBLE);
            // 恢复 PreviewView 的布局参数
            previewView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT) {{
                addRule(BELOW, R.id.liveText);
                setMargins(0, getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin), 0, 0);
            }});
        }
    }


    private void bindPreview() {
        if (previewView != null) {
            preview = new Preview.Builder().build();
            CameraSelector cameraSelector = isFrontCamera ?
                    CameraSelector.DEFAULT_FRONT_CAMERA :
                    CameraSelector.DEFAULT_BACK_CAMERA;

            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview);
        }
    }

    private void toggleOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    private void switchCamera() {
        isFrontCamera = !isFrontCamera;
        bindPreview();
    }
    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
