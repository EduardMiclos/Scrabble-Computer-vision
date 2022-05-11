package com.andyedy.scrabble_computer_vision;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = getApplicationContext();
        setContentView(R.layout.activity_main);

        if (OpenCVLoader.initDebug()) {
            Log.d("myTag", "OpenCV loaded");
        }
    }

    public void startScanActivity(View view) {
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }

    public static Context getMyContext() {
        return myContext;
    }
}