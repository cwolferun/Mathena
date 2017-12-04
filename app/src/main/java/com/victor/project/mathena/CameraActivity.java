package com.victor.project.mathena;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v4.content.ContextCompat;


import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Character.isLetter;
import static java.lang.Character.toLowerCase;

public class CameraActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor sEditor;
    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = CameraActivity.class.getSimpleName();
    //private static final String DATA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Tess";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/Tess";

    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //setContentView(R.layout.activity_camera);
        preferences = getSharedPreferences("share", 0);
        sEditor = preferences.edit();
        context = getApplicationContext();

       // btn1 = (Button) findViewById(R.id.testbtn);

        //final Activity activity = this;
        checkPermission();
        startCameraActivity();


//        this.findViewById(R.id.testbtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkPermission();
//
//            }
//        });



    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }//else {Toast.makeText(context,"you can write",Toast.LENGTH_LONG).show();}
    }
    private void startCameraActivity(){
        try{
            String imagePath = DATA_PATH+ "/imgs";
            File dir = new File(imagePath);
            if(!dir.exists()){
                boolean made= dir.mkdir();
                if(made){Toast.makeText(context,"it was made",Toast.LENGTH_LONG).show();}
                else{
                    Toast.makeText(context,"didnt make",Toast.LENGTH_LONG).show();
                }
            }
            String imageFilePath = imagePath+"/ocr.jpg";
            outputFileDir = Uri.fromFile(new File(imageFilePath));
            //outputFileDir = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() +
             //       ".provider", new File(imageFilePath));
            final Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir);
            pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if(pictureIntent.resolveActivity(getPackageManager() ) != null){
                startActivityForResult(pictureIntent, 1024);
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024) {
            if (resultCode == Activity.RESULT_OK) {
                prepareTessData();
                startOCR(outputFileDir);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Result canceled.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Activity result failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void prepareTessData(){
        try{
            File dir = new File(DATA_PATH + TESS_DATA);
            if(!dir.exists()){
                dir.mkdir();
            }
            String fileList[] = getAssets().list("");
            for(String fileName : fileList){
                String pathToDataFile = DATA_PATH+TESS_DATA+"/"+fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void startOCR(Uri imageUri){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 7;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),options);
            String result = this.getText(bitmap);

            result = modifyMathString(result);
            sEditor.putString("Function", result);
            sEditor.commit();
            finish();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private String getText(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.init(DATA_PATH,"eng");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 120:{
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Read permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 121:{
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Write permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    public String modifyMathString(String input){
        char[] cArray = input.toCharArray();
        for(int c = 0; c<cArray.length;c++){


            if(cArray[c]==':'){
                cArray[c] ='=';
            }
            if(cArray[c]=='A'){
                cArray[c] ='^';
            }
            if(cArray[c]=='S'){
                cArray[c] ='5';
            }
            if(cArray[c]==8212){
                cArray[c] ='-';
            }
            if(cArray[c]=='~'){
                cArray[c] ='-';
            }
            if(isLetter(cArray[c])){
                cArray[c] = toLowerCase(cArray[c]);
            }
        }
        input = new String(cArray);
        return input.replaceAll("/\\\\","^");


    }
}