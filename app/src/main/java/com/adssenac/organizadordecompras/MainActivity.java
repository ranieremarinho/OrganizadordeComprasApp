package com.adssenac.organizadordecompras;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    TodosProdutosActivity.class
            );

            startActivity(intent);
            finish();

        }, 2000); // tempo da splash (2 segundos)
    }
}