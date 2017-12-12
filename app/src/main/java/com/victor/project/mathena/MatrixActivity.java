package com.victor.project.mathena;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.StringTokenizer;


public class MatrixActivity extends AppCompatActivity {
    Button addOne;
    Button removeOne;

    Button solveit;
    TextView answer;
    EditText first;
    LinearLayout ll;
    int numOfEqs = 1;
    Context context;
    List<EditText> allTex;
    StringBuilder allInputs;
    String result;
    SharedPreferences preferences;
    SharedPreferences.Editor sEditor;
    boolean callSuccess;
    Set<String> entries;
    SharedPreferences myHistory;



    @Override
    protected void onResume() {
        super.onResume();
//        numOfEqs=1;
//        //ll.removeAllViews();
//
//        first = (EditText) findViewById(R.id.firstEq);
//        ll = (LinearLayout) findViewById(R.id.linLay);
//        ll.add(first);
        String received = preferences.getString("Function","");
        if(!received.isEmpty()){
            sEditor.clear();
            sEditor.commit();
            fillFields(received);

        }
    }

    public void addEditText(){

        if (numOfEqs<=4) {
            final EditText rowEditText = new EditText(context);
            rowEditText.setId(numOfEqs);
            LinearLayout.LayoutParams layoutParams = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.addView(rowEditText, layoutParams);

            rowEditText.setTextColor(0xff000000);
            //rowEditText.setText("rent money");
            allTex.add(rowEditText);
            numOfEqs++;

        }
        else {
            Toast.makeText(context,"I think that's plenty.",Toast.LENGTH_LONG).show();
        }



    }
    public void removeEditText(){

        if (numOfEqs>=2) {

            int size = ll.getChildCount()-1;
            ll.removeViewAt(size);
            allTex.remove(size-1);
            numOfEqs -=1;


        }




    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        first = null;
//        ll.removeAllViews();
//        ll = null;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
        preferences = getSharedPreferences("share", 0);
        myHistory = getSharedPreferences("History", 0);

        sEditor = preferences.edit();
        context = getApplicationContext();
        addOne = (Button) findViewById(R.id.addEquation);
        removeOne = (Button) findViewById(R.id.removeEquation);
        first = (EditText) findViewById(R.id.firstEq);
        ll = (LinearLayout) findViewById(R.id.linLay);

        solveit = (Button) findViewById(R.id.matrixSolv);
        answer = (TextView) findViewById(R.id.matrixAns);
        allInputs = new StringBuilder();
        callSuccess =false;
        allTex = new ArrayList<>();

        entries = myHistory.getStringSet("matrix", null);
        if(entries == null) {
            entries = new ArraySet<>();
        }

        removeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEditText();
            }
        });

        addOne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                addEditText();

            }
        });
        //allStrings="";
        //stringBuilder.append(first.getText().toString());


        solveit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allInputs.append(first.getText().toString());
                for(int t=1; t<numOfEqs;t++)
                {
                    allInputs.append(" ").append(allTex.get(t-1).getText().toString());
                }

                MatrixActivity.NetworkThread nt = new MatrixActivity.NetworkThread(allInputs.toString());
                nt.start();
                try {
                    nt.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                allInputs.setLength(0);
                //result.replaceAll(" ","\n");
                answer.setText(result.replaceAll(" ","\n"));
                savehistory();

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
                historyIntent.putExtra("caller","matrix");
                startActivity(historyIntent);
                return true;
            case R.id.cam:
                Intent camIntent;
                camIntent = new Intent(this,CameraActivity.class);
                startActivity(camIntent);
                return true;
            case R.id.der_menu:
                Intent derivIntent;
                derivIntent = new Intent(this,Derivative.class);
                startActivity(derivIntent);
                finish();
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
                //intentionally left blank
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void savehistory() {

        if(callSuccess){




            StringBuilder builder = new StringBuilder();
            builder.append(first.getText().toString());
            for(int t=1; t<numOfEqs;t++)
            {
                builder.append(" ").append(allTex.get(t-1).getText().toString());
            }

            builder.append("\n").append(answer.getText().toString());

            entries.add(builder.toString());
            myHistory.edit().putStringSet("matrix",entries).apply();
            // myHistory.edit().apply();

            callSuccess = false;
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
                //String solverURL = "Http://192.168.1.65/matrix.php";        //will need to be changed to public ip
                String solverURL = "Http://172.12.2.86/matrix.php";        //is public ip

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
    public void fillFields(String string){


        String[] equations = string.split("\n");
        int systemSize = equations.length;

        for (int t =numOfEqs; t<systemSize;t++){

            addEditText();
        }

        for (int e =1; e<systemSize;e++){

            //addEditText();
            allTex.get(e-1).setText(equations[e]);
        }

        first.setText(equations[0]);


    }

}
