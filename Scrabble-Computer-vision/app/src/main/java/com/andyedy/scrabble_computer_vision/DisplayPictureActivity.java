package com.andyedy.scrabble_computer_vision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayPictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        Bitmap snapped_picture = (Bitmap)intent.getParcelableExtra("snapped_picture");

        ImageView iv = findViewById(R.id.result_imageView);
        iv.setImageBitmap(snapped_picture);
    }
}