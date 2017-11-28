package com.victor.project.mathena;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    SharedPreferences preferences;
    SharedPreferences.Editor sEditor;


    @Override
    protected void onResume() {
        super.onResume();
        String received = preferences.getString("Function","");
        if(!received.isEmpty()){
            sEditor.clear();
            sEditor.commit();
            fillFields(received);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_integral);

        preferences = getSharedPreferences("share",0);
        sEditor = preferences.edit();

        solveIt = (Button) findViewById(R.id.diSolve);
        answer = (TextView) findViewById(R.id.diAnswer);

        function =(EditText) findViewById(R.id.dIntFunc);
        atAPointA =(EditText) findViewById(R.id.diPointA);
        atAPointB =(EditText) findViewById(R.id.diPointB);
        atAPointC =(EditText) findViewById(R.id.diPointC);
        atAPointD =(EditText) findViewById(R.id.diPointD);
        wrtInner =(EditText) findViewById(R.id.innerVar);
        wrtOuter =(EditText) findViewById(R.id.outerVar);

        solveIt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


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
                //String solverURL = "Http://192.168.1.65/doubleIntegral.php";        //uncomment when working in private
                String solverURL = "Http://172.12.2.86/doubleIntegral.php";        //uncomment when working from public

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
            case R.id.cam:
                Intent camIntent;
                camIntent = new Intent(this,CameraActivity.class);
                startActivity(camIntent);
                return true;
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
    public void fillFields(String string){
        //string may equal something like "5x^3+4x-2y^2+5y dx dy\nx=[3,6]\ny=[2,5]"


        try {
            String[] parts = string.split("\n"); //3 elements: function w/ d's, x interval, y interval
            String[] header = parts[0].split(" "); //3elements: function, dy, dx

            String firstvar= header[1].substring(1); //firstvar = x
            String secvar= header[2].substring(1);  //secvar = y

            String[] subparts1 = parts[1].split("="); //={"x", "[3,6]"}
            String[] subparts2 = parts[2].split("="); //={"y", "[2,5]"}
            String firstInterval = subparts1[1].substring(1,subparts1[1].length()-1); //= "3,6"
            String secondInterval = subparts2[1].substring(1,subparts2[1].length()-1); //= "2,5"

            String[] firstBounds=firstInterval.split(",");          //={"3","6"}
            String[] secondBounds=secondInterval.split(",");          //={"2","5"}

            wrtInner.setText(firstvar);
            wrtOuter.setText(secvar);
            function.setText(header[0]);
            atAPointA.setText(firstvar+"="+firstBounds[0]);
            atAPointB.setText(firstvar+"="+firstBounds[1]);
            atAPointC.setText(secvar+"="+secondBounds[0]);
            atAPointD.setText(secvar+"="+secondBounds[1]);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Something Went Wrong. Try Another Picture.", Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }

    }


}
