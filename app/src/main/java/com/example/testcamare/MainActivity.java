package com.example.testcamare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToCamare(View view) {

        Intent intent = new Intent(this, CamareActivity.class);
//        Intent intent = new Intent(this, CamarexActivity.class);
        startActivity(intent);
    }
}
