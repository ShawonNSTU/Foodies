package com.example.shawon.foodies;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by SHAWON on 2/18/2018.
 */

public class CurrentServerUser {

    public static GetServerUserInfo currentServerUser;
    public static Request currentRequest;

    public static final String baseUrl = "https://maps.googleapis.com";

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight){

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float) bitmap.getWidth();
        float scaleY = newHeight/(float) bitmap.getHeight();

        float pivotX = 0, pivotY = 0;

        Matrix matrix = new Matrix();

        matrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);

        canvas.setMatrix(matrix);

        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

}
