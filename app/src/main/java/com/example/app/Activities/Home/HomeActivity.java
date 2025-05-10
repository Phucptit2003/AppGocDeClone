package com.example.app.Activities.Home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.app.Activities.Cart_PlaceOrder.CartActivity;
import com.example.app.Activities.Cart_PlaceOrder.EmptyCartActivity;
import com.example.app.Activities.MyShop.MyShopActivity;
import com.example.app.Activities.Order.OrderActivity;
import com.example.app.CustomMessageBox.CustomAlertDialog;
import com.example.app.CustomMessageBox.SuccessfulToast;
import com.example.app.Fragments.Home.FavoriteFragment;
import com.example.app.Fragments.Home.HomeFragment;
import com.example.app.Fragments.NotificationFragment;
import com.example.app.Helpers.FirebaseNotificationHelper;
import com.example.app.Helpers.FirebaseProductHelper;
import com.example.app.Helpers.FirebaseUserInfoHelper;
import com.example.app.Model.Cart;
import com.example.app.Model.Notification;
import com.example.app.Model.User;
import com.example.app.R;
import com.example.app.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String userId;
    private ActivityHomeBinding binding;
    private LinearLayout layoutMain;
    private Fragment selectionFragment;

    private static final int NOTIFICATION_PERMISSION_CODE = 10023;
    private static final int STORAGE_PERMISSION_CODE = 101;
    FirebaseProductHelper firebaseHelper = new FirebaseProductHelper();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }

            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            }

            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
            }
        }

        initUI();
        loadInformationForNavigationBar();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initUI() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        binding.navigationLeft.bringToFront();
        createActionBar();

        layoutMain = binding.layoutMain;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layoutMain.getId(), new HomeFragment(userId))
                .commit();
        setEventNavigationBottom();
        setCartNavigation();
        binding.navigationLeft.setNavigationItemSelectedListener(this);
    }

    private void setCartNavigation() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.message_menu:
                    Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    break;
                case R.id.cart_menu:
                    FirebaseDatabase.getInstance().getReference().child("Carts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Cart cart = ds.getValue(Cart.class);
                                if (cart.getUserId().equals(userId)) {
                                    FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cart.getCartId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() == 0) {
                                                startActivity(new Intent(HomeActivity.this, EmptyCartActivity.class));
                                                return;
                                            } else {
                                                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                                                intent.putExtra("userId", userId);
                                                startActivity(intent);
                                                return;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    return true;
            }
            return true;
        });
    }



    private void setEventNavigationBottom() {
        BottomNavigationView bottomNavigation = binding.bottomNavigation;
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.favorite_menu:
                    selectionFragment = new FavoriteFragment(userId);
                    break;
                case R.id.home_menu:
                    selectionFragment = new HomeFragment(userId);
                    break;
                case R.id.notification_menu:
                    selectionFragment = new NotificationFragment(userId);
                    break;
            }

            if (selectionFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(layoutMain.getId(), selectionFragment).commit();
            }
            return true;
        });

        bottomNavigation.setSelectedItemId(R.id.home_menu);
    }

    private void createActionBar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            binding.drawLayoutHome.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileMenu:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;
            case R.id.orderMenu:
                Intent intent1 = new Intent(this, OrderActivity.class);
                intent1.putExtra("userId", userId);
                startActivity(intent1);
                break;
            case R.id.myShopMenu:
                Intent intent2 = new Intent(this, MyShopActivity.class);
                intent2.putExtra("userId", userId);
                startActivity(intent2);
                break;
            case R.id.logoutMenu:
                new CustomAlertDialog(HomeActivity.this, "Do you want to logout?");
                CustomAlertDialog.binding.btnYes.setOnClickListener(view -> {
                    new SuccessfulToast(HomeActivity.this, "Logout successfully!").showToast();
                    CustomAlertDialog.alertDialog.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                });
                CustomAlertDialog.binding.btnNo.setOnClickListener(view -> CustomAlertDialog.alertDialog.dismiss());
                CustomAlertDialog.showAlertDialog();
                break;
        }
        binding.drawLayoutHome.close();
        return true;
    }

    private void loadInformationForNavigationBar() {
        new FirebaseNotificationHelper(this).readNotification(userId, new FirebaseNotificationHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Notification> notificationList, List<Notification> notificationListToNotify) {
                int count = 0;
                for (Notification notification : notificationList) {
                    if (!notification.isRead()) {
                        count++;
                    }
                }
                BottomNavigationView bottomNavigation = binding.bottomNavigation;
                if (count > 0) {
                    bottomNavigation.getOrCreateBadge(R.id.notification_menu).setNumber(count);
                } else {
                    bottomNavigation.removeBadge(R.id.notification_menu);
                }

                // Hiển thị thông báo
                for (Notification notification : notificationListToNotify) {
                    makeNotification(notification);
                }
            }

            @Override
            public void DataIsInserted() {
            }

            @Override
            public void DataIsUpdated() {
            }

            @Override
            public void DataIsDeleted() {
            }
        });

        new FirebaseUserInfoHelper(this).readUserInfo(userId, new FirebaseUserInfoHelper.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                View headerView = binding.navigationLeft.getHeaderView(0);
                ShapeableImageView imgAvatarInNavigationBar = headerView.findViewById(R.id.imgAvatarInNavigationBar);
                TextView txtNameInNavigationBar = headerView.findViewById(R.id.txtNameInNavigationBar);
                txtNameInNavigationBar.setText("Hi, " + getLastName(user.getUserName()));
                Glide.with(HomeActivity.this).load(user.getAvatarURL()).placeholder(R.drawable.default_avatar).into(imgAvatarInNavigationBar);
            }

            @Override
            public void DataIsInserted() {
            }

            @Override
            public void DataIsUpdated() {
            }

            @Override
            public void DataIsDeleted() {
            }
        });
    }

    private String getLastName(String userName) {
        userName = userName.trim();
        String[] output = userName.split(" ");
        return output[output.length - 1];
    }

    private void makeNotification(Notification notification) {
    }
}