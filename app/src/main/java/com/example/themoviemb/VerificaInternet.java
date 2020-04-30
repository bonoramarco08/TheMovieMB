package com.example.themoviemb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * classe astratta
 */
public abstract class VerificaInternet {
    /**
     * metoddo statico che controlla se il telefono è o no connesso ad internet
     * @param context il context dell'activity
     * @return restiuisce un boolean (true se il telefono è connesso ad internet , false se il telefono non è connesso ad internet)
     */
    public static boolean getConnectivityStatusString(Context context) {
        String status = null;
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}
