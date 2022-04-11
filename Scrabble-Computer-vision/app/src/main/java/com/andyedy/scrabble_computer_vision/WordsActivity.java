package com.andyedy.scrabble_computer_vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.andyedy.scrabble_computer_vision.Util.WordAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class WordsActivity extends AppCompatActivity {

    ArrayList<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        String[] wordArray;
        wordArray = (String[]) b.get("WordList");
        words = new ArrayList<String>(Arrays.asList(wordArray));

        RecyclerView rv = findViewById(R.id.wordsRecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new WordAdapter(this, words));
    }
}