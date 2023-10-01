package vn.edu.usth.flickrapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.usth.flickrapp.Adapter.ViewPager_MenuBottom_Adapter;
import vn.edu.usth.flickrapp.Model.User;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    private static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (User) getIntent().getSerializableExtra("user");
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        viewPager.setAdapter(new ViewPager_MenuBottom_Adapter(getSupportFragmentManager(), user));
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_news) {
                    viewPager.setCurrentItem(0);
                    return true;
                }

                if (menuItem.getItemId() == R.id.menu_search) {
                    viewPager.setCurrentItem(1);
                    return true;
                }

                if (menuItem.getItemId() == R.id.menu_camera_access) {
                    viewPager.setCurrentItem(2);
                    return true;
                }

                if (menuItem.getItemId() == R.id.menu_notifications) {
                    viewPager.setCurrentItem(3);
                    return true;
                }

                if (menuItem.getItemId() == R.id.menu_profile) {
                    viewPager.setCurrentItem(4);
                    return true;
                }
                return false;
            };
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}