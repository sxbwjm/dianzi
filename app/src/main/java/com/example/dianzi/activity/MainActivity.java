package com.example.dianzi.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.dianzi.MainApplication;
import com.example.dianzi.Migration;
import com.example.dianzi.R;
import com.example.dianzi.common.PhotoViewCommon;
import com.example.dianzi.db.AppDatabase;
import com.example.dianzi.entity.TransactionData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.dianzi.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private AppDatabase db;

    static public List<TransactionData> transactions = new ArrayList<TransactionData>();

    ActivityResultLauncher fileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData = result.getData();
                        if(resultData != null) {
                            Uri uri = resultData.getData();

                            try {
                                Migration.import_excel(MainActivity.this.getContentResolver().openInputStream(uri));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
       // appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_top, R.id.nav_payment, R.id.nav_list).build();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);


        binding.buttonProcessImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ImageProcessActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button_pay, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id ==  R.id.action_settings) {
            Intent intent = new Intent(this, MainSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id ==  R.id.action_import) {
            //MainApplication.instance.importFromExcel("");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            fileLauncher.launch(intent);

        }
        else if (id ==  R.id.action_export) {

        }
        else if(id == R.id.action_bankflow_principle) {
            Intent intent = new Intent(this, BankflowPrincipleActivity.class);
            startActivity(intent);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void changeTitle(String title) {
        binding.toolbar.setTitle(title);
    }
}