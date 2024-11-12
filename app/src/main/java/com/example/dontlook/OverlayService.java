package com.example.dontlook;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class OverlayService extends Service implements SensorEventListener {

    private WindowManager windowManager;
    private View overlayPatternView;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayPatternView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        windowManager.addView(overlayPatternView, params);

        // 센서 설정
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];  // x축 값 (좌우 기울기)
            float y = event.values[1];  // y축 값 (상하 기울기)
            float z = event.values[2];  // z축 값 (앞뒤 기울기)

            // y가 낮거나 x 값이 작은 각도에서도 즉시 반응하도록 하고, z는 덜 예민하게 설정
            if (y < 5 || Math.abs(x) > 3 || Math.abs(z) < 3) {  // 폰이 기울어진 상태
                overlayPatternView.setAlpha(0.8f);  // 필터를 진하게 설정
            } else {
                // 폰이 정면을 향해 세워진 상태로 간주하여 약하게 설정
                overlayPatternView.setAlpha(0.3f);  // 필터를 약하게 설정
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 정확도 변경 시 처리 필요 없음
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayPatternView != null) {
            windowManager.removeView(overlayPatternView);
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
