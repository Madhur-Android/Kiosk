package com.example.kiosk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.Adapters.CategoryAdapter;
import com.example.kiosk.Fragments.DefaultMenuFragment;
import com.example.kiosk.Response.CategoryResponse;
import com.example.kiosk.Room.DatabaseHelper;
import com.example.kiosk.Service.Api_interface;
import com.example.kiosk.Service.Retrofit;
import com.example.kiosk.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DatabaseHelper databaseHelper;
    ImageView menu;
    RecyclerView category_rv;
    CategoryAdapter categoryAdapter;
    Integer page, limit, filter;
    Api_interface retrofitInterface;
    FrameLayout container;
    private FragmentManager fragmentManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        databaseHelper = DatabaseHelper.getInstance(MainActivity.this);
//        databaseHelper.cartDao().deleteAll();
        SharedPreferences sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        Integer ShopID = sharedPreferences.getInt("ShopID", 0);
        showCartCount();
        category_rv = findViewById(R.id.category_rv);
        container = findViewById(R.id.container);
        fragmentManager = getSupportFragmentManager();

        menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, OrderSummaryActivity.class);
            startActivity(i);
            finish();
        });

        page = 1;
        limit = 200;
        filter = 0;
        boolean hasMenu = true;

        category_item(ShopID, page, limit, filter, hasMenu);

        DefaultMenuFragment defaultMenuFragment = new DefaultMenuFragment(MainActivity.this);
        loadFrag(defaultMenuFragment, "", fragmentManager);

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

    public void showCartCount() {

        if (databaseHelper.cartDao().getAllCart().size() > 0) {
            binding.menu.setBadgeValue(databaseHelper.cartDao().getAllCart().size());
            binding.menu.visibleBadge(true);
        } else {
            binding.menu.setBadgeValue(0);
            binding.menu.visibleBadge(false);
        }

    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, f1, name);
        ft.commit();
    }


    private void category_item(Integer shopID, Integer page, Integer limit, Integer filter, boolean hasMenu) {
        retrofitInterface = Retrofit.getRetrofit().create(Api_interface.class);
        Call<CategoryResponse> call = retrofitInterface.categoryList(shopID, page, limit, filter, hasMenu);
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getData() != null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        category_rv.setLayoutManager(linearLayoutManager);
                        category_rv.setHasFixedSize(true);
                        categoryAdapter = new CategoryAdapter(MainActivity.this, response.body().getData());
                        category_rv.setAdapter(categoryAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}