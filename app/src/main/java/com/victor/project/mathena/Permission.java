package com.victor.project.mathena;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Beta on 11/2/2017.
 */

public class Permission {

    static int MY_PERMISSIONS_CAMERA;
    Context mContext;
    Activity mActivity;

    Permission(Context context, Activity activity){
        mContext = context;
        mActivity = activity;
    };

   public void camera(){

       if (ActivityCompat.checkSelfPermission(mContext,android.Manifest.permission.CAMERA)
               != PackageManager.PERMISSION_GRANTED) {

           ActivityCompat.requestPermissions(mActivity,
                   new String[]{android.Manifest.permission.CAMERA},
                   MY_PERMISSIONS_CAMERA);

       }



   }



}
