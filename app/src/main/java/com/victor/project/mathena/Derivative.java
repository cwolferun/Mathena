package com.victor.project.mathena;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import static com.victor.project.mathena.Permission.MY_PERMISSIONS_CAMERA;

public class Derivative extends AppCompatActivity {
    Button solveIt;
    EditText function;
    EditText atAPoint;
    TextView answer;
    String result = "";
    SharedPreferences preferences;
    SharedPreferences myHistory;

    SharedPreferences.Editor sEditor;
    boolean callSuccess;
    static int MY_PERMISSIONS_CAMERA;
    Set<String> put1;




    @Override
    protected void onResume() {
        super.onResume();
        function =(EditText) findViewById(R.id.derivFuncText);
        atAPoint =(EditText) findViewById(R.id.DerivPointText);
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
        setContentView(R.layout.activity_derivative);


        myHistory = getSharedPreferences("History", 0);
        callSuccess = false;
        preferences = getSharedPreferences("share",0);
        sEditor = preferences.edit();
        //Permission permission = new Permission(getApplicationContext(),this);
        //permission.camera();
        //put1 = new ArraySet<>();

        put1 = myHistory.getStringSet("derivative", null);
        if(put1 == null) {
            put1 = new ArraySet<>();
        }

        solveIt = (Button) findViewById(R.id.solveDerivBtn);
        answer = (TextView) findViewById(R.id.derivAnser);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_CAMERA);

        }

        solveIt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                String sfunc = function.getText().toString();
                String spoint = atAPoint.getText().toString();


                String sendToServer = sfunc + " " + spoint;

                NetworkThread nt = new NetworkThread(sendToServer);
                nt.start();
                try {
                    nt.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                answer.setText("");
                answer.setText(result);

                savehistory();




            }
        });
    }

    private void savehistory() {

        if(callSuccess){



            StringBuilder builder = new StringBuilder(function.getText().toString());


            builder.append(" ").append(atAPoint.getText().toString())
            .append("\n").append(answer.getText().toString());

            put1.add(builder.toString());
            myHistory.edit().putStringSet("derivative",put1).apply();
           // myHistory.edit().apply();

            callSuccess = false;
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
            case R.id.history:
                Intent historyIntent;
                historyIntent = new Intent(this,HistoryActivity.class);
                historyIntent.putExtra("caller","derivative");
                startActivity(historyIntent);
                return true;
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
                Intent matrixIntent;
                matrixIntent = new Intent(this,MatrixActivity.class);
                startActivity(matrixIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class NetworkThread extends Thread{
        String send;
        NetworkThread(String send) {
            this.send = send;
        }

        public void run() {
            result="";

            try{
                String solverURL = "Http://192.168.1.65/derivative.php";        //will need to be changed to public ip
                //String solverURL = "Http://172.12.2.86/derivative.php";
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


                //input thread for connection time out


                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line="";
                while ((line = bufferedReader.readLine())!= null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                callSuccess = true;


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
    public void onRequestPermissionsResult(int requestcode, String[] permissions,
                                           int[] grantResults){
        if(requestcode == MY_PERMISSIONS_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"request granted",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"request granted",Toast.LENGTH_LONG).show();

               // finish();
            }
        }

    }

    public void fillFields(String string){

        String functionName;
        String singleBound;

        int arguments=0;
        String[] boxes = new String[2];
        StringTokenizer st = new StringTokenizer(string," \n");
        while (st.hasMoreTokens()) {
            if(arguments>=2)
            {
                //there are too many arguments
                return;
            }
            boxes[arguments]=st.nextToken();
            arguments++;

        }
        if(boxes[0].contains("="))
        {
            functionName= boxes[0].split("=")[1];

        }
        else
        {
            functionName=boxes[0];
        }
        singleBound=boxes[1];

        function.setText(functionName);
        atAPoint.setText(singleBound);



    }


}
