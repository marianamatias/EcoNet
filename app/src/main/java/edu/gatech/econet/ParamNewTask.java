package edu.gatech.econet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class ParamNewTask extends AppCompatActivity {
    Button moreButton;
    Button lessButton;
    TextView setFrequency;
    TextView textFrequency;
    Button goHT;
    public static int frequency ;
    public static  String key ;
    public static String tag =null;
    public static String receivedTask = null;
    public static String receivedTopic=null;
    String [] spinnerlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param_new_task);
        moreButton = findViewById(R.id.buttonMore);
        lessButton = findViewById(R.id.buttonLess);
        setFrequency = findViewById(R.id.setFrequency);
        textFrequency = findViewById(R.id.textFrequency);
        goHT = findViewById(R.id.goHabitTracker);
        spinnerlist = ForumTopicSelect.localTopic;
        receivedTask=AddTaskSearch.sentTask;
        receivedTopic=AddTaskSearch.sentTopic;
        //Retrieve freq and key of the selected task
        int index = Methods.find(WelcomeActivity.taskList,receivedTask);
        frequency = Integer.parseInt(WelcomeActivity.freqList[index]);
        key = WelcomeActivity.keysList[index];

        TextView selectedTaskText = (TextView) findViewById(R.id.taskSelected);
        selectedTaskText.setText(receivedTask);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more();
            }
        });
        lessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                less();
            }
        });
        tag=receivedTopic;
        goHT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag.equals(receivedTopic)){
                    OpenNewActivity();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),"Please select a related Topic",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinnerlist);
        MaterialBetterSpinner betterSpinner=(MaterialBetterSpinner)findViewById(R.id.topicScroll);
        betterSpinner.setText(receivedTopic);
        betterSpinner.setAdapter(arrayAdapter);
        betterSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerlist[i].equals(receivedTopic)){
                    Toast toast = Toast.makeText(getApplicationContext(),"Topic do not match the task, please change",Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    tag = spinnerlist[i];
                }
            }
        });
    }
    private void more(){
        if (frequency<7){
            frequency++;
            setFrequency.setText((String.valueOf(frequency)));
        }
    }
    private void less(){
        if(frequency>1){
            frequency--;
            setFrequency.setText((String.valueOf(frequency)));
        }
    }
    private void OpenNewActivity(){
        Intent intent = new Intent(this, habitTracker.class);
        intent.putExtra("FROM_ACTIVITY", "ParamNewTask");
        startActivity(intent);
    }

}
