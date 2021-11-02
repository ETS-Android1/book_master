package com.example.book_master_2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class conn_check {
    public static boolean isConnectionAvailable(Context context) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()
                        && netInfo.isConnectedOrConnecting()
                        && netInfo.isAvailable()) {
                    return true;
                }
            }

        }catch (Exception e){}

        return false;
    }
}
