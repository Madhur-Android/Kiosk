package com.example.kiosk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.kiosk.Room.DatabaseHelper;

public class OrderSummaryActivity extends AppCompatActivity {

    AppCompatButton pay;
    ImageButton back;
    TextView apply;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        databaseHelper = DatabaseHelper.getInstance(OrderSummaryActivity.this);

        pay = findViewById(R.id.pay);
        pay.setOnClickListener(v -> {
            databaseHelper.cartDao().deleteAll();
            Intent i = new Intent(OrderSummaryActivity.this, PaymentActivity.class);
            startActivity(i);
            finish();
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent i = new Intent(OrderSummaryActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        apply = findViewById(R.id.apply);
        apply.setOnClickListener(v -> {
            Intent i = new Intent(OrderSummaryActivity.this, QRcodeActivity.class);
            startActivity(i);
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}