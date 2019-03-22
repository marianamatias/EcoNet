package edu.gatech.econet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.content.Intent;


public class askQuestion extends AppCompatActivity {

    Button submitQuestionButton;
    String questionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question);
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        submitQuestionButton = (Button) findViewById(R.id.submitQuestionButton);

        submitQuestionButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                EditText editText = (EditText) findViewById(R.id.questionText);
                questionText = editText.getText().toString();

                OpenNewActivityWithParam(questionText);

            }
        });
    }

    private void OpenNewActivityWithParam(String string1){
        Intent intent = new Intent(this, adviceForum.class);
        Bundle bundleOut = new Bundle();
        bundleOut.putString("question text", string1);

        intent.putExtras(bundleOut);
        //intent.putExtra("firstData",data1);
        //intent.putExtra("secondData",data2);
        startActivity(intent);
    }

}
