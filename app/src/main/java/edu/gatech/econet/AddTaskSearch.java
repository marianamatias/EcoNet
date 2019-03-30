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

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static android.view.View.VISIBLE;

public class AddTaskSearch extends AppCompatActivity {
    SearchView searchView;
    public static ArrayList<String> itemsLoc;
    ListView listTasks;
    TextView hint;
    Button filterButton;
    //edu.gatech.econet.CustomTextView searchLabel;
    EditText searchLabel;
    // Hard coded listview to get to use the bundle and retrieve information to next activity
    String rawTasks[] = new String [] {
            "Use a steel straw","Use a glass straw","Make my cleaning products","Make my laundry products","Eat vegetarian meals","Compost","Recycle at home",
            "Get a reusable water bottle","Turn off lights that aren't being used","Car pooling for the atmosphere","Avoid useless commuting","Other1","Other2"};
    String difficultyTasks[] = new String [] {
            "Easy","Easy","Intermediate","Intermediate","Intermediate","Difficult","Intermediate","Easy","Easy","Intermediate","Easy","Difficult","Dfficult"};
    String topicTasks[] = new String [] {
            "Zero Waste","Zero Waste","Zero Waste","Zero Waste","Alimentation","Zero Waste","Zero Waste","Zero Waste","Energy","Transportation",
            "Transportation","Animal","Animal"};
    String [] filterList = new String [] {"Easy","Intermediate","Difficult","Energy","Zero Waste","Transportation","Animal","Alimentation"};
    //Used after filter
    String [] proposedTasks = rawTasks;
    //Displayed list
    String localTasks[] = proposedTasks;
    InputMethodManager imm ;
    int cntHide;
    ArrayAdapter<String> adapter;

    boolean [] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();


    public void show(View view){
        listTasks.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_search);

        //final Bundle bundleIn = getIntent().getExtras();
        hint = (TextView) findViewById(R.id.hintSearch);
        //searchLabel = (edu.gatech.econet.CustomTextView)findViewById(R.id.searchLabel);
        searchLabel = (EditText) findViewById(R.id.searchLabel);
        listTasks = (ListView) findViewById(R.id.listFound);
        filterButton = (Button) findViewById(R.id.filterButton);
        listTasks.getBackground().setAlpha(80);
        checkedItems = new boolean[filterList.length];

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
                        for (int j =0;j<rawTasks.length; j++){
                            for (int k=0;k<mUserItems.size();k++){
                                String cmp = filterList[mUserItems.get(k)];
                                if((topicTasks[j].equals(cmp)) || (difficultyTasks[j].equals(cmp))){
                                    proposedTasks = increaseArray(proposedTasks, rawTasks[j]);
                                    localTasks = proposedTasks;
                                }
                            }
                        }
                        adapter = new ArrayAdapter<String>(AddTaskSearch.this, android.R.layout.simple_list_item_1, localTasks){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent){
                                View view = super.getView(position,convertView,parent);
                                if(itemsLoc!=null){
                                    if(itemsLoc.contains(proposedTasks[position])){
                                        view.setBackgroundColor(getResources().getColor(R.color.lightGreyTransparent));
                                    }
                                }
                                return view;
                            }
                        };
                        listTasks.setAdapter(adapter);
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
                        }
                        adapter = new ArrayAdapter<String>(AddTaskSearch.this, android.R.layout.simple_list_item_1, localTasks){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent){
                                View view = super.getView(position,convertView,parent);
                                if(itemsLoc!=null){
                                    if(itemsLoc.contains(proposedTasks[position])){
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
//        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Hide:
//        //imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
//        cntHide=0;
//        //Show
//        searchLabel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //imm.hideSoftInputFromWindow(searchLabel.getApplicationWindowToken(), 0);
//                cntHide++;
//                if (cntHide %2==0){
//                    //imm.hideSoftInputFromWindow(searchLabel.getApplicationWindowToken(), 0);
//                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//                }
//                else{
//                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//                }
//            }
//        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, localTasks){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position,convertView,parent);
                if(itemsLoc!=null){
                    if(itemsLoc.contains(proposedTasks[position])){
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
                    OpenNewActivityWithParam(localTasks[position]);
                }
                else if(itemsLoc.contains(localTasks[position])){
                    Toast toast = Toast.makeText(getApplicationContext(),"Task already in your habit tracker",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    OpenNewActivityWithParam(localTasks[position]);
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

    private void OpenNewActivityWithParam(String data1){
        itemsLoc = habitTracker.itemsSent;
        Intent intent = new Intent(this, ParamNewTask.class);
        Bundle bundleOut = new Bundle();
        bundleOut.putString("new_task",data1);
        intent.putExtras(bundleOut);
        startActivity(intent);
    }
    public static String[] removeTheElement(String[] arr, String seek){
        if (arr == null) {
            return arr;
        }
        String[] anotherArray = new String[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (arr[i].equals(seek)) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }
    private static String[] search(TextView searchLabel, String localTasks[] ){
        if((searchLabel.getText()!=null)&&(localTasks!=null)){
            for (int i = 0;i<localTasks.length;i++){
                if((searchLabel.getText().toString()).equals(localTasks[i])){
                    localTasks=removeTheElement(localTasks,localTasks[i]);
                }
            }
        }
        return localTasks;
    }
    public String[] increaseArray(String[] input, String newElem){
        String [] nullList = new String []{};
        if (input!=nullList) {
            int i = input.length;
            String[] newArray = new String[i + 1];
            for (int cnt = 0; cnt < i; cnt++) {
                newArray[cnt] = input[cnt];
            }
            newArray[i] = newElem;
            return newArray;
        }
        else {
            String [] returnList = new String[] {newElem};
            return returnList;
        }
    }
}

