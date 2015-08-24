package com.example.myRoundCornerProgressBar;

import android.app.Activity;
import android.os.Bundle;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

public class MyActivity extends Activity {

    private RoundCornerProgressBar progressTwo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        progressTwo = (RoundCornerProgressBar) findViewById(R.id.progress_two);
//        progressTwo.setBackgroundColor(getResources().getColor(R.color.custom_progress_background));
//        progressTwo.setProgressColor(getResources().getColor(R.color.custom_progress_blue_progress));
//        progressTwo.setSecondaryProgressColor(getResources().getColor(R.color.custom_progress_blue_progress_half));
    }
}
