package com.victor.project.mathena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button goToDeriv;
    Button goToInteg;

    Intent derivIntent;
    Intent integIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goToDeriv = (Button) findViewById(R.id.deriv_button);
        goToInteg = (Button) findViewById(R.id.integ_button);

        derivIntent = new Intent(this,Derivative.class);
        integIntent = new Intent(this,Integral.class);


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
    }
}
