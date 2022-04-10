package com.andyedy.scrabble_computer_vision;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andyedy.scrabble_computer_vision.Util.Letter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReviewScanActivity extends AppCompatActivity {

    List<Letter> ls;
    ArrayList<String> words;
    private static String urlString = "http://185.104.48.59:5000/";
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_scan);

        Intent i = getIntent();
        ls = i.getParcelableArrayListExtra("LetterList");

        words = new ArrayList<>();
    }

    public void displayLetters(View view) throws ExecutionException, InterruptedException {
        for(Letter l:ls) {
            Log.d("scannedLetters", l.getValue() + " - " + l.getFrequency());
        }

        GetAPI ga = new GetAPI();
        String res = ga.execute().get();
        Toast.makeText(this, res, Toast.LENGTH_LONG);

        Intent wordsActivityIntent = new Intent(this, WordsActivity.class);
        //wordsActivityIntent.putExtra("WordList", words.toArray());
        wordsActivityIntent.putExtra("WordList", Arrays.copyOf(words.toArray(), words.size(), String[].class));
        startActivity(wordsActivityIntent);
    }

    private String getMyLetters() {
        StringBuilder sb = new StringBuilder();
        for(Letter l: ls) {
            for(int i=0; i<l.getFrequency();i++)
                sb.append(l.getValue());
        }

        return sb.toString();
    }

    public class GetAPI extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(ReviewScanActivity.this);
            progressDialog.setMessage("Loading words...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(String.format("%s%s", urlString, getMyLetters()));
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";

                StringBuffer buffer = new StringBuffer();

                while((line= reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                reader.close();
                String jsonString =  buffer.toString();

                JSONObject jsonReader = new JSONObject(jsonString);
                JSONArray wordsArray = jsonReader.getJSONArray("words");

                for (int i=0; i<wordsArray.length(); i++) {
                    words.add(wordsArray.getString(i));
                }

                return "success";
            }
            catch (MalformedURLException e) {
                Log.d("scannedLetters", e.toString());
            }
            catch (IOException e) {
                Log.d("scannedLetters", e.toString());
            } catch (JSONException e) {
                Log.d("scannedLetters", e.toString());
                return "Error";
            } finally {
                if(connection!=null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException e) {
                    Log.d("scannedLetters", e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            TextView resTextView = findViewById(R.id.textView);
            resTextView.setText(s);
        }


    }
}