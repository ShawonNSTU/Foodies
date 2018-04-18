package com.example.shawon.foodies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by SHAWON on 2/9/2018.
 */

public class CurrentUser {

    public static GetUserInfo currentUser;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final String USER = "User";
    public static final String PASSWORD = "Password";
    public static final String TYPE = "Type";

    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if(info != null){

                for(int i=0; i<info.length;i++){

                    if(info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }

                }

            }

        }

        return false;

    }

}
