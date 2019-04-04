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
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class askQuestion extends AppCompatActivity {
    public static String suggestedTopic;
    public static String suggestedTask;
    String [] spinnerTask;
    String [] spinnerTopic;
    Button sendButton;
    EditText questionText;
    public static String questionSent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question);
        sendButton = (Button)findViewById(R.id.sendButton);
        questionText = (EditText)findViewById(R.id.questionText);
        spinnerTopic = ForumTopicSelect.localTopic;
        spinnerTask = AddTaskSearch.rawTasks;
        final ArrayAdapter<String> arrayAdapterTopic=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinnerTopic);
        MaterialBetterSpinner betterSpinnerTopic=(MaterialBetterSpinner)findViewById(R.id.topicSpinner);
        final ArrayAdapter<String> arrayAdapterTask=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinnerTask);
        MaterialBetterSpinner betterSpinnerTask=(MaterialBetterSpinner)findViewById(R.id.taskSpinner);
        //From what activity you're from ?
        Intent mIntent = getIntent();
        String previousActivity = mIntent.getStringExtra("FROM");
        if (previousActivity.equals("adviceForum")){
            suggestedTask = null;
            suggestedTopic = adviceForum.selectedTopic;
            betterSpinnerTopic.setText(suggestedTopic);
        }
        else if (previousActivity.equals("habitTracker_swipe")){
            suggestedTask = habitTracker.taskSwiped;
            suggestedTopic = habitTracker.topicSwiped;
            betterSpinnerTopic.setText(suggestedTopic);
            betterSpinnerTask.setText(suggestedTask);
        }
        else if (previousActivity.equals("habitTracker_menu")){
            suggestedTask = null;
            suggestedTopic = null;
        }
        //Toast toast = Toast.makeText(this,"Found "+suggestedTask+" and "+suggestedTopic,Toast.LENGTH_LONG);
        //toast.show();
        betterSpinnerTopic.setAdapter(arrayAdapterTopic);
        betterSpinnerTopic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                suggestedTopic = spinnerTopic[i];
            }
        });
        betterSpinnerTask.setAdapter(arrayAdapterTask);
        betterSpinnerTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                suggestedTask = spinnerTask[i];
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionSent = questionText.getText().toString();
                OpenNewActivityWithParam();
            }
        });
    }

    private void OpenNewActivityWithParam(){
        Intent intent = new Intent(this, adviceForum.class);
        intent.putExtra("FROM2", "askQuestion");

        startActivity(intent);
    }

}
