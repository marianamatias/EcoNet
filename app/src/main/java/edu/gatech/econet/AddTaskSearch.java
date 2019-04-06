package edu.gatech.econet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static android.view.View.VISIBLE;

public class AddTaskSearch extends AppCompatActivity {
    SearchView searchView;
    public static String[] itemsLoc = new String[] {};
    ListView listTasks;
    TextView hint;
    Button filterButton;
    EditText searchLabel;
    // Hard coded listview to get to use the bundle and retrieve information to next activity
    public static String rawTasks[] = WelcomeActivity.taskList;
            //new String [] {
            //"Use a bamboo straw","Use a steel straw","Use natural cleaning products","Air dry clothes","Eat vegetarian meals","Compost","Recycle at home",
            //"Get a reusable water bottle","Turn off lights that aren't being used","Carpool for the atmosphere","Take quick showers","Bring a reusable bag to the grocery store","Turn off water when brushing teeth"};
    public static String difficultyTasks[] = new String [] {};
            //"Easy","Easy","Intermediate","Intermediate","Intermediate","Difficult","Intermediate","Easy","Easy","Intermediate","Easy","Difficult","Dfficult"};
    public static String topicTasks[] = WelcomeActivity.topicList;
                    //new String [] {
            //"Zero Waste","Zero Waste","Zero Waste","Zero Waste","Vegetarianism","Zero Waste","Zero Waste","Zero Waste","Energy","Transportation",
            //"Transportation","Animal","Animal"};
    String [] filterList = new String [] {"Easy","Intermediate","Difficult","Energy","Zero Waste","Transportation","Animal","Vegetarianism"};
    //Used after filter
    String [] proposedTasks = rawTasks;
    String [] proposedTopic = topicTasks;
    //Displayed list
    String localTasks[] = proposedTasks;
    InputMethodManager imm ;
    int cntHide;
    ArrayAdapter<String> adapter;
    public static String sentTask=null;
    public static String sentTopic=null;

    FileCacher<String []> taskCacher = new FileCacher<>(AddTaskSearch.this, "taskCacher.txt");
    boolean [] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();


    public void show(View view){
        listTasks.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_search);
        if (taskCacher.hasCache()){
            try{
                itemsLoc=taskCacher.readCache();
            } catch (IOException e ){
                e.printStackTrace();
            }
        }
        //final Bundle bundleIn = getIntent().getExtras();
        for(int i =0; i<rawTasks.length;i++){
            difficultyTasks = Methods.increaseArray(difficultyTasks,"Easy");
        }
        hint = (TextView) findViewById(R.id.hintSearch);

        searchLabel = (EditText) findViewById(R.id.searchLabel);
        listTasks = (ListView) findViewById(R.id.listFound);
        filterButton = (Button) findViewById(R.id.filterButton);
        listTasks.getBackground().setAlpha(80);
        checkedItems = new boolean[filterList.length];
        ArrayList<String> fullListLoc = new ArrayList<>();


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder=new AlertDialog.Builder(AddTaskSearch.this);
                mBuilder.setTitle("Filters available for the search");
                mBuilder.setMultiChoiceItems(filterList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserItems.contains(position)) {
                                mUserItems.add(position);
                            }
                        }
                        else if (mUserItems.contains(position)){
                            int index;
                            for (int m =0 ;m<mUserItems.size();m++){
                                if (mUserItems.get(m)==position){
                                    mUserItems.remove(m);
                                }
                            }
                        }

                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        proposedTasks = new String [] {};
                        proposedTopic = new String [] {};
                        for (int j =0;j<rawTasks.length; j++){
                            for (int k=0;k<mUserItems.size();k++){
                                String cmp = filterList[mUserItems.get(k)];
                                if((topicTasks[j].equals(cmp)) || (difficultyTasks[j].equals(cmp))){
                                    proposedTasks = Methods.increaseArray(proposedTasks, rawTasks[j]);
                                    localTasks = proposedTasks;
                                    proposedTopic = Methods.increaseArray(proposedTopic,topicTasks[j]);
                                }
                            }
                        }
                        if (proposedTasks.length!=0){
                            listTasks.setVisibility(View.VISIBLE);
                            adapter = new ArrayAdapter<String>(AddTaskSearch.this, android.R.layout.simple_list_item_1, localTasks){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent){
                                    View view = super.getView(position,convertView,parent);
                                    if(itemsLoc!=null){
                                        if(Methods.isInArray(itemsLoc,proposedTasks[position])){
                                            view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                                        }
                                    }
                                    return view;
                                }
                            };
                            listTasks.setAdapter(adapter);
                        }
                        else {
                            listTasks.setVisibility(View.GONE);
                        }

                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k=0; k<checkedItems.length; k++){
                            checkedItems[k]=false;
                            mUserItems.clear();
                            proposedTasks=rawTasks;
                            localTasks=rawTasks;
                            proposedTopic = topicTasks;
                        }
                        adapter = new ArrayAdapter<String>(AddTaskSearch.this, android.R.layout.simple_list_item_1, localTasks){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent){
                                View view = super.getView(position,convertView,parent);
                                if(itemsLoc!=null){
                                    if(Methods.isInArray(itemsLoc,proposedTasks[position])){
                                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                                    }
                                }
                                return view;
                            }
                        };
                        listTasks.setAdapter(adapter);
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, localTasks){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position,convertView,parent);
                if(itemsLoc!=null){
                    if(Methods.isInArray(itemsLoc,proposedTasks[position])){
                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                    }
                }
                return view;
            }
        };
        listTasks.setAdapter(adapter);
        listTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long is) {
                //Toast.makeText(AddTaskSearch.this,proposedTasks[position],Toast.LENGTH_SHORT).show();
                if (itemsLoc==null){
                    sentTask=localTasks[position];
                    sentTopic=proposedTopic[position];
                    OpenNewActivityWithParam();
                    }
                else if(Methods.isInArray(itemsLoc,localTasks[position])){
                    Toast toast = Toast.makeText(getApplicationContext(),"Task already in your habit tracker",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    sentTask=localTasks[position];
                    sentTopic=proposedTopic[position];
                    OpenNewActivityWithParam();
                }

            }
        });
        searchLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                AddTaskSearch.this.adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private void OpenNewActivityWithParam(){
        itemsLoc = null;
        Intent intent = new Intent(this, ParamNewTask.class);
        startActivity(intent);
    }

    private static String[] search(TextView searchLabel, String localTasks[] ){
        if((searchLabel.getText()!=null)&&(localTasks!=null)){
            for (int i = 0;i<localTasks.length;i++){
                if((searchLabel.getText().toString()).equals(localTasks[i])){
                    localTasks=Methods.removeTheElement(localTasks,localTasks[i]);
                }
            }
        }
        return localTasks;
    }

}

