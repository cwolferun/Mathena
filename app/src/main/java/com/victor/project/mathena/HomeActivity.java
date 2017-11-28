package com.victor.project.mathena;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button goToDeriv;
    Button goToInteg;
    Button goToDInteg;
    Button goToMatrix;


    Intent derivIntent;
    Intent integIntent;
    Intent dIntegIntent;
    Intent matrixIntent;

    SharedPreferences preferences;
    SharedPreferences.Editor sEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferences = getSharedPreferences("share",0);
        sEditor = preferences.edit();
        sEditor.clear();
        sEditor.apply();


        goToDeriv = (Button) findViewById(R.id.deriv_button);
        goToInteg = (Button) findViewById(R.id.integ_button);
        goToDInteg = (Button) findViewById(R.id.dInteg_button);
        goToMatrix = (Button) findViewById(R.id.matrix_button);

        derivIntent = new Intent(this,Derivative.class);
        integIntent = new Intent(this,Integral.class);
        dIntegIntent = new Intent(this,DoubleIntegral.class);
        matrixIntent = new Intent(this,MatrixActivity.class);


        goToDeriv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(derivIntent);
            }
        });
        goToInteg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(integIntent);
            }
        });
        goToDInteg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(dIntegIntent);
            }
        });
        goToMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(matrixIntent);
            }
        });

    }
}
