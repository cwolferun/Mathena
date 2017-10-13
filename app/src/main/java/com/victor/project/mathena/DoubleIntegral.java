package com.victor.project.mathena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class DoubleIntegral extends AppCompatActivity {


    Button solveIt;
    EditText function;
    EditText atAPointA;
    EditText atAPointB;
    EditText atAPointC;
    EditText atAPointD;
    EditText wrtInner;
    EditText wrtOuter;
    TextView answer;
    String result = "";
    String sendToServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_integral);

        solveIt = (Button) findViewById(R.id.diSolve);
        answer = (TextView) findViewById(R.id.diAnswer);

        solveIt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                function =(EditText) findViewById(R.id.dIntFunc);
                atAPointA =(EditText) findViewById(R.id.diPointA);
                atAPointB =(EditText) findViewById(R.id.diPointB);
                atAPointC =(EditText) findViewById(R.id.diPointC);
                atAPointD =(EditText) findViewById(R.id.diPointD);

                String inner = wrtInner.getText().toString();
                String sfunc = function.getText().toString();
                String Apoint = atAPointA.getText().toString();
                String Bpoint = atAPointB.getText().toString();
                String Cpoint = atAPointC.getText().toString();
                String Dpoint = atAPointD.getText().toString();

                String[] var = Apoint.split("=");
                 if (inner.equals(var[1])){
                      sendToServer = sfunc + " " + Apoint + " " + Bpoint + " " + Cpoint + " " + Dpoint;

                 }else {
                      sendToServer = sfunc + " " + Cpoint + " " + Dpoint + " " + Apoint + " " + Bpoint;

                 }


                DoubleIntegral.NetworkThread nt = new DoubleIntegral.NetworkThread(sendToServer);
                nt.start();
                try {
                    nt.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                answer.setText("");
                answer.setText(result);




            }
        });


    }

    class NetworkThread extends Thread{
        String send;
        NetworkThread(String send) {
            this.send = send;
        }

        public void run() {
            result="";

            try{
                String solverURL = "Http://192.168.1.65/integral/index.php";        //uncomment when working in private
                //String solverURL = "Http://172.12.2.86/integral/index.php";        //uncomment when working from public

                URL url = new URL(solverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("package","UTF-8")+"="+URLEncoder.encode(send,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line="";
                while ((line = bufferedReader.readLine())!= null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



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
            case R.id.der_menu:
                Intent derivIntent;
                derivIntent = new Intent(this,Derivative.class);
                startActivity(derivIntent);
                finish();                return true;
            case R.id.integ_menu:
                Intent integIntent;
                integIntent = new Intent(this,Integral.class);
                startActivity(integIntent);
                finish();
                return true;
            case R.id.dubInt_menu:
                //left blank
                return true;
            case R.id.lineEq_menu:
                //TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
