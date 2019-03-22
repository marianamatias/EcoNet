package edu.gatech.econet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

public class ParamNewTask extends AppCompatActivity {
    Button moreButton;
    Button lessButton;
    TextView setFrequency;
    TextView textFrequency;
    Button goHT;
    private int frequency = 3;
    public static ArrayList<String> itemsLoc2;
    public static String tag;

    String receivedTask = null;
    String [] spinnerlist={"Zero waste","Recycling","Climate","Animal protection"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param_new_task);
        moreButton = findViewById(R.id.buttonMore);
        lessButton = findViewById(R.id.buttonLess);
        setFrequency = findViewById(R.id.setFrequency);
        textFrequency = findViewById(R.id.textFrequency);
        goHT = findViewById(R.id.goHabitTracker);
        final Bundle bundleIn = getIntent().getExtras();

        if (bundleIn!=null){
            receivedTask = bundleIn.getString("new_task");
        }
        TextView selectedTaskText = (TextView) findViewById(R.id.taskSelected);
        String sentence = "Set the parameters for the task " + receivedTask;
        selectedTaskText.setText(sentence);
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
        goHT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenNewActivity(receivedTask,bundleIn);
            }
        });
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,spinnerlist);
        MaterialBetterSpinner betterSpinner=(MaterialBetterSpinner)findViewById(R.id.spinner);
        betterSpinner.setAdapter(arrayAdapter);
        betterSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tag = spinnerlist[i];
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
    private void OpenNewActivity(String taskName, Bundle bundleInn){
        itemsLoc2 = AddTaskSearch.itemsLoc;
        itemsLoc2.add(receivedTask);
        Intent intent = new Intent(this, habitTracker.class);
        //Bundle bundleOut = new Bundle();
        //for (int i=0; i< bundleInn.size();i++){
        //    bundleOut.putString("Task_List"+Integer.toString(i), bundleInn.getString("Task_List"+Integer.toString(i)));
        //}
        //bundleOut.putString("Task_List"+Integer.toString(bundleInn.size()+1),taskName);
        //intent.putExtras(bundleOut);
        intent.putExtra("FROM_ACTIVITY", "ParamNewTask");
        startActivity(intent);
    }

}
