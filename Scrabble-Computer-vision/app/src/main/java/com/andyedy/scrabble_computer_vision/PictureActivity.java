package com.andyedy.scrabble_computer_vision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.andyedy.scrabble_computer_vision.Util.Letter;
import com.andyedy.scrabble_computer_vision.Util.LetterScanner;
import com.andyedy.scrabble_computer_vision.Util.MockScanner;

import java.util.ArrayList;

public class PictureActivity extends AppCompatActivity {

    LetterScanner ls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        ls = new MockScanner();
    }

    public void confirmPicture(View view) {
        Intent reviewIntent = new Intent(this, ReviewScanActivity.class);
        reviewIntent.putParcelableArrayListExtra("LetterList", (ArrayList<Letter>)ls.getLetters());
        startActivity(reviewIntent);
    }
}