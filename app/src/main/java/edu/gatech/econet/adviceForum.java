package edu.gatech.econet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class adviceForum extends AppCompatActivity {

    TextView receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_forum);
        receive = (TextView)findViewById(R.id.questionShowText);
        receive.setText(getIntent().getStringExtra("question text"));

    }
}
