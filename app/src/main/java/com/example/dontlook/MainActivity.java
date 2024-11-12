package com.example.dontlook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.AdapterView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private boolean isOverlayActive = false;
    private Button toggleOverlayButton;
    private Spinner patternSpinner;
    private int selectedPattern = R.drawable.pattern_overlay; // 기본ㄴ 패턴

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleOverlayButton = findViewById(R.id.btn_toggle_overlay);
        patternSpinner = findViewById(R.id.spinner_pattern);

        toggleOverlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOverlay();
            }
        });

        // 패턴 스피너의 선택 이벤트 처리
        patternSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedPattern = R.drawable.pattern_overlay;
                        break;
                    case 1:
                        selectedPattern = R.drawable.pattern_overlay2;
                        break;
                    case 2:
                        selectedPattern = R.drawable.pattern_overlay3;
                        break;
                }
                // 패턴을 선택할 때 오버레이가 활성화된 상태라면 서비스에 업데이트
                if (isOverlayActive) {
                    startOverlayService();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void toggleOverlay() {
        if (isOverlayActive) {
            stopOverlayService();
            toggleOverlayButton.setText("보호 활성화");
        } else {
            startOverlayService();
            toggleOverlayButton.setText("보호 비활성화");
        }
        isOverlayActive = !isOverlayActive;
    }

    private void startOverlayService() {
        Intent serviceIntent = new Intent(this, OverlayService.class);
        serviceIntent.putExtra("selectedPattern", selectedPattern);
        startService(serviceIntent);
    }

    private void stopOverlayService() {
        stopService(new Intent(this, OverlayService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isOverlayActive) {
            stopOverlayService();
            isOverlayActive = false;
        }
    }
}
