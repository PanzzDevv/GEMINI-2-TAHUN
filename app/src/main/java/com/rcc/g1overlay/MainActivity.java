package com.rcc.g1overlay;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private TextView tvStatus;
    private Button btnToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        btnToggle = findViewById(R.id.btnToggle);

        updateStatus();

        btnToggle.setOnClickListener(v -> {
            if (checkOverlayPermission()) {
                startOverlayService();
            } else {
                requestOverlayPermission();
            }
        });
    }

    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
    }

    private void updateStatus() {
        if (checkOverlayPermission()) {
            tvStatus.setText("Izin Overlay: Diberikan ✅");
            btnToggle.setText("Aktifkan Tombol Mengambang (G1)");
        } else {
            tvStatus.setText("Izin Overlay: Belum Diberikan ⚠️");
            btnToggle.setText("Beri Izin Aplikasi Mengambang");
        }
    }

    private void startOverlayService() {
        Intent intent = new Intent(this, FloatingOverlayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        Toast.makeText(this, "Aplikasi mengambang aktif! Buka Google One.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            updateStatus();
            if (checkOverlayPermission()) {
                startOverlayService();
            } else {
                Toast.makeText(this, "Izin diperlukan untuk menampilkan tombol mengambang.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
