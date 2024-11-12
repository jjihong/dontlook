package com.example.dontlook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private boolean isOverlayActive = false;
    private Button toggleOverlayButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleOverlayButton = findViewById(R.id.btn_toggle_overlay);
        toggleOverlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOverlay();
            }
        });
    }

    private void toggleOverlay() {
        Intent serviceIntent = new Intent(this, OverlayService.class);
        if (isOverlayActive) {
            stopService(serviceIntent);
            toggleOverlayButton.setText("보호 활성화");
        } else {
            startService(serviceIntent);
            toggleOverlayButton.setText("보호 비활성화");
        }
        isOverlayActive = !isOverlayActive;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isOverlayActive) {
            // 앱이 종료될 때 오버레이 서비스를 중지
            stopService(new Intent(this, OverlayService.class));
            isOverlayActive = false;
        }
    }
}
