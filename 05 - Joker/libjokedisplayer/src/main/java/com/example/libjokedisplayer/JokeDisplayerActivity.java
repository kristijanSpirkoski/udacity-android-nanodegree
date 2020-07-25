package com.example.libjokedisplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class JokeDisplayerActivity extends AppCompatActivity {

    private TextView jokeDisplayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_displayer);

        Intent intent = getIntent();
        String joke = intent.getStringExtra( Intent.EXTRA_TEXT );


        jokeDisplayView = findViewById(R.id.joke);
        jokeDisplayView.setText(joke);
    }
}