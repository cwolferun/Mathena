package com.victor.project.mathena;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.victor.project.mathena.Permission.MY_PERMISSIONS_CAMERA;

public class Derivative extends AppCompatActivity {
    Button solveIt;
    EditText function;
    EditText atAPoint;
    TextView answer;
    String result = "";
    SharedPreferences preferences;
    SharedPreferences.Editor sEditor;



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
        preferences = getSharedPreferences("share",0);
        sEditor = preferences.edit();

        solveIt = (Button) findViewById(R.id.solveDerivBtn);
        answer = (TextView) findViewById(R.id.derivAnser);

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
                Permission permission = new Permission(getApplicationContext(),this);
                permission.camera();
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

    class NetworkThread extends Thread{
        String send;
        NetworkThread(String send) {
            this.send = send;
        }

        public void run() {
            result="";

            try{
                //String solverURL = "Http://192.168.1.65/derivative.php";        //will need to be changed to public ip
                String solverURL = "Http://172.12.2.86/derivative.php";
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
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {/*
                CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                try {
                    cameraManager.openCamera(cameraID, mcamcallback, mBackgroundHandler);
                }
                catch (CameraAccessException e){e.printStackTrace();}
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            */
                // openCamm();
            } else {
                finish();
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
