package com.example.kiosk;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.kiosk.Response.LoginResponse;
import com.example.kiosk.Service.Api_interface;
import com.example.kiosk.Service.Retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText shopID_et, password_et;
    AppCompatButton login;
//    Integer shopID;
    String password, shopID;
    Api_interface retrofitInterface;
    ProgressDialog progressDialog;
    private SessionManager sessionManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        shopID_et = findViewById(R.id.shopID_et);
        password_et = findViewById(R.id.password_et);
        login = findViewById(R.id.login);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            // User is already logged in, go to main activity
            Intent intent = new Intent(LoginActivity.this, StartingActivity.class);
            startActivity(intent);
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Kiosk App");
        progressDialog.setMessage("Please Wait");


        login.setOnClickListener(v -> {
            if (!shopID_et.getText().toString().isEmpty()) {
                if (!password_et.getText().toString().isEmpty()) {
                    shopID = shopID_et.getText().toString();
                    password = password_et.getText().toString();
                    progressDialog.show();
                    login(Integer.parseInt(shopID), password);
                } else {
                    password_et.setError("Enter password");
                }
            } else {
                shopID_et.setError("Enter Shop id");
            }
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

    private void login(int shopID, String password) {
        retrofitInterface = Retrofit.getRetrofit().create(Api_interface.class);
        Call<LoginResponse> call = retrofitInterface.login(shopID, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getData() != null) {
                        Integer ShopID = response.body().getData().getUser().getShopId();
                        String Token = response.body().getData().getToken();
                        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("ShopID", ShopID);
                        editor.putString("Token", Token);
                        sessionManager.saveUserInfo(shopID, password);
                        sessionManager.setLoggedIn(true);
                        editor.apply();
                        progressDialog.dismiss();
                        Intent i = new Intent(LoginActivity.this, StartingActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}