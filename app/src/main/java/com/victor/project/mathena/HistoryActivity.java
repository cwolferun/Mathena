package com.victor.project.mathena;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout ll;
    SharedPreferences hPreferences;
    Context context;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        context = getApplicationContext();
        intent = getIntent();
        String whoCalledMe = intent.getStringExtra("caller");

        ll = (LinearLayout) findViewById(R.id.historyView);
        hPreferences = getSharedPreferences("History", 0);
        Set<String> set = new ArraySet<>();


        switch (whoCalledMe){
            case("derivative"):
                set = hPreferences.getStringSet("derivative",null);
                break;
            case("integral"):
                set = hPreferences.getStringSet("integral",null);
                break;
            case("doubleIntegral"):
                set = hPreferences.getStringSet("doubleIntegral",null);
                break;
            case("matrix"):
                set = hPreferences.getStringSet("matrix",null);
                break;


        }



        LinearLayout.LayoutParams layoutParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        try {
            for(String s : set){
                final TextView textView = new TextView(context);
                textView.setText(s);
                textView.setTextSize(16);
                textView.setTextColor(0xff081c99);

                ll.addView(textView, layoutParams);

            }
        } catch (Exception e) {
            e.printStackTrace();
            //TODO toast for empty history
        }


    }
}
