package com.example.dianzi.activity;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.dianzi.databinding.ActivityImageProcessBinding;

import com.example.dianzi.R;

public class ImageProcessActivity extends AppCompatActivity {

   // private AppBarConfiguration appBarConfiguration;
    private ActivityImageProcessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImageProcessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


      //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_image_process);
     //   AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
      //  NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

      //  NavigationView navigationView = findViewById(R.id.nav_graph_image_process);
     //   NavigationUI.setupWithNavController(navigationView, navController);

    }
}