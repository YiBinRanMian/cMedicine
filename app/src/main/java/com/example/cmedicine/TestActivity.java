package com.example.cmedicine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.drm.ProcessedData;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.hardware.Camera;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.cmedicine.util.ImageProcessing;
import com.example.cmedicine.util.ToastUtil;
import com.example.cmedicine.util.UIHelper;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * Created by 41850 on 2018/2/28.
 */

@RuntimePermissions
public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TestActivity";

    private AtomicBoolean processing = new AtomicBoolean(true);
    private LinkedList<Integer> averageData = new LinkedList<>();
    private long endTime = 0;

    private SurfaceHolder mPreviewHolder;
    private Camera mCamera = null;
    private PowerManager.WakeLock mWakeLock = null;
    //保持Activity界面常量 需要权限"WAKE_LOCK", "DEVICE_POWER"
    private Camera.Parameters parameters;
    private boolean canNeedPermission = true;

    private SurfaceView mPreview;
    private HeartRateChart heartRateChart;
    private Button btnActionStart;
    private Snackbar mSnackbar;

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            //在onPreviewFrame中得到每一帧的数据:data
            if (!processing.compareAndSet(false, true)) {
                return;
            }
            Camera.Size size = camera.getParameters().getPreviewSize();
            int width = size.width;
            int height = size.height;
            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            Log.e(TAG, "onPreviewFrame: ");
            heartRateChart.lineTo(imgAvg);
            if (imgAvg == 0 || imgAvg == 255 || imgAvg < 150) {
                ToastUtil.showToast(TestActivity.this, "请用你的手指盖住摄像头");
                reStart();
                return;
            }
            if (averageData.peekLast() == null || averageData.peekLast() != imgAvg) {
                averageData.add(imgAvg);
            }
            if (endTime == 0) {
                endTime = System.currentTimeMillis() + 10000;
            } else if (System.currentTimeMillis() >= endTime) {
                ToastUtil.showToast(TestActivity.this, "心脏跳动" + processData(averageData) + "次" + ", 心率：" + processData(averageData) * 6);
                return;
            }
            processing.set(false);
        }
    };

    private int processData(LinkedList<Integer> averageData) {
        int dInt = 0;
        int count = 0;
        boolean isRise = false;
        for (Integer integer : averageData) {
            if (dInt == 0) {
                dInt = integer;
                continue;
            }
            if (integer > dInt) {
                if (!isRise) {
                    count++;
                    isRise = true;
                }
            } else {
                isRise = false;
            }
            dInt = integer;
        }
        return count;
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(mPreviewHolder);
                mCamera.setPreviewCallback(mPreviewCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    private Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG, Log.getStackTraceString(throwable));
                System.exit(0);
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        heartRateChart = (HeartRateChart) findViewById(R.id.heart_rate_chart);
        btnActionStart = (Button) findViewById(R.id.btn_action_start);
        btnActionStart.setOnClickListener(this);
        setSupportActionBar(toolbar);
        //将toolbar转换成ActionBar
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canNeedPermission) {
            TestActivityPermissionsDispatcher.showCameraWithCheck(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        canNeedPermission = true;
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    private void reStart() {
        endTime = 0;
        averageData.clear();
        //heartRateChart.clear();
        processing.set(false);//开启检测
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_action_start:
                reStart();
                break;
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        //调用setDisplayOrientation来设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示。
        if (parameters == null) {
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            //设置闪光灯
        }
        if (mPreview == null) {
            mPreview = (SurfaceView) findViewById(R.id.sv_preview);
            mPreviewHolder = mPreview.getHolder();
            mPreviewHolder.addCallback(mSurfaceCallback);
            mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            //SurfaceView本身不包含原生数据，其数据来源于其它对象，比如Camera预览，就是由Camera对象向SurfaceView提供数据。
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        }
        mWakeLock.acquire();

        if (parameters != null) {
            try {
                mCamera.setPreviewDisplay(mPreviewHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void onCameraDenied() {
        ToastUtil.showToast(TestActivity.this, R.string.permission_camera_denied_msg);
        finish();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void OnCameraNeverAskAgain() {
        canNeedPermission = false;
        mSnackbar = Snackbar.make(findViewById(android.R.id.content), R.string.permission_camera_never_ask_again, Snackbar.LENGTH_INDEFINITE).setAction(R.string.setting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showAppDetailSetting(TestActivity.this);
            }
        });
        mSnackbar.show();

    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        UIHelper.getRationaleDialog(this, R.string.permission_camera_dialog_title, R.string.permission_camera_dialog_msg_scanner, request).show();
    }

    //动态授权
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TestActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
