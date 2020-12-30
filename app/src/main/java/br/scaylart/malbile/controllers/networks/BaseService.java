package br.scaylart.malbile.controllers.networks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BaseService {
    public static final String BASE_HOST = "http://myanimelist.net";
    public static final String USER_AGENT =  "api-indiv-0A82E1C01531EA0C7E8349A8B82803BA";

    public enum ListType {
        ANIME,
        MANGA
    }

    public static String getListTypeString(ListType type) {
        return type.name().toLowerCase();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
