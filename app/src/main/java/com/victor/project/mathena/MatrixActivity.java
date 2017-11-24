package com.victor.project.mathena;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MatrixActivity extends AppCompatActivity {
    Button addOne;
    LinearLayout ll;
    int numOfEqs = 0;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
        context = getApplicationContext();
        addOne = (Button) findViewById(R.id.addEquation);
        ll = (LinearLayout) findViewById(R.id.linLay);

        addOne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (numOfEqs<=5) {
                    final TextView rowTextView = new TextView(context);
                    rowTextView.setId(numOfEqs);
                    LinearLayout.LayoutParams layoutParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ll.addView(rowTextView, layoutParams);
                    rowTextView.setText("rent money");
                    numOfEqs++;
                }
                else {
                    Toast.makeText(context,"Take it the fuck easy",Toast.LENGTH_LONG).show();
                }


            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cam:
                Intent camIntent;
                camIntent = new Intent(this,CameraActivity.class);
                startActivity(camIntent);
                return true;
            case R.id.der_menu:
                //intentionally left blank
                return true;
            case R.id.integ_menu:
                Intent integIntent;
                integIntent = new Intent(this,Integral.class);
                startActivity(integIntent);
                finish();
                return true;
            case R.id.dubInt_menu:
                Intent dIntegIntent;
                dIntegIntent = new Intent(this,DoubleIntegral.class);
                startActivity(dIntegIntent);
                finish();
                return true;
            case R.id.lineEq_menu:
                //TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
