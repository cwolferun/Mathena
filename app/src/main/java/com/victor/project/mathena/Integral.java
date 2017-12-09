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

public class Integral extends AppCompatActivity {

    Button solveIt;
    EditText function;
    EditText atAPointA;
    EditText atAPointB;
    TextView answer;
    String result = "";
    boolean callSuccess;
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
        setContentView(R.layout.activity_integral);
        callSuccess =false;
        preferences = getSharedPreferences("share",0);
        sEditor = preferences.edit();
        solveIt = (Button) findViewById(R.id.solveIntegBtn);
        answer = (TextView) findViewById(R.id.IntegAnser);


        solveIt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                function =(EditText) findViewById(R.id.IntegFuncText);
                atAPointA =(EditText) findViewById(R.id.IntegPointA);
                atAPointB =(EditText) findViewById(R.id.IntegPointB);

                String sfunc = function.getText().toString();
                String Apoint = atAPointA.getText().toString();
                String Bpoint = atAPointB.getText().toString();


                String sendToServer = sfunc + " " + Apoint + " " + Bpoint;

                Integral.NetworkThread nt = new Integral.NetworkThread(sendToServer);
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
            case R.id.history:
                Intent historyIntent;
                historyIntent = new Intent(this,HistoryActivity.class);
                historyIntent.putExtra("caller","integral");
                startActivity(historyIntent);
                return true;
            case R.id.cam:
                Intent camIntent;
                camIntent = new Intent(this,CameraActivity.class);
                startActivity(camIntent);
            case R.id.der_menu:
                Intent derivIntent;
                derivIntent = new Intent(this,Derivative.class);
                startActivity(derivIntent);
                finish();
                return true;
            case R.id.integ_menu:
                //blank
                return true;
            case R.id.dubInt_menu:
                Intent dIntegIntent;
                dIntegIntent = new Intent(this,DoubleIntegral.class);
                startActivity(dIntegIntent);
                finish();
                return true;
            case R.id.lineEq_menu:
                Intent matrixIntent;
                matrixIntent = new Intent(this,MatrixActivity.class);
                startActivity(matrixIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


  /*  private void savehistory() {

        if(callSuccess){



            StringBuilder builder = new StringBuilder(function.getText().toString());


            builder.append(" ").append(atAPoint.getText().toString())
                    .append("\n").append(answer.getText().toString());

            put1.add(builder.toString());
            myHistory.edit().putStringSet("derivative",put1).apply();
            // myHistory.edit().apply();

            callSuccess = false;
        }

    }*/
    class NetworkThread extends Thread{
        String send;
        NetworkThread(String send) {
            this.send = send;
        }

        public void run() {
            result="";

            try{
                //String solverURL = "Http://192.168.1.65/integral.php";        //will need to be changed to public ip
                String solverURL = "Http://172.12.2.86/integral.php";        //is public ip

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

    public void fillFields(String string){
        //string may equal something like "5x^3+4x\nx=[3,6]"


        try {
            String[] parts = string.split("\n"); //returns 2 elements: function and x interval
           // String[] header = parts[0].split(" "); //returns 3elements: function, dy, dx

           // String firstvar= header[1].substring(1); //firstvar = x
           // String secvar= header[2].substring(1);  //secvar = y

            String[] subparts1 = parts[1].split("="); //={"x", "[3,6]"}
            //String[] subparts2 = parts[2].split("="); //={"y", "[2,5]"}
            String firstInterval = subparts1[1].substring(1,subparts1[1].length()-1); //= "3,6"
           // String secondInterval = subparts2[1].substring(1,subparts2[1].length()-1); //= "2,5"

            String[] firstBounds=firstInterval.split(",");          //={"3","6"}
           // String[] secondBounds=secondInterval.split(",");          //={"2","5"}

          //  wrtInner.setText(firstvar);
           // wrtOuter.setText(secvar);
            function.setText(parts[0]);
            atAPointA.setText(subparts1[0]+"="+firstBounds[0]);
            atAPointB.setText(subparts1[0]+"="+firstBounds[1]);
            //atAPointC.setText(secvar+"="+secondBounds[0]);
            //atAPointD.setText(secvar+"="+secondBounds[1]);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Something Went Wrong. Try Another Picture.", Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }

    }
}
