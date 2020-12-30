package br.scaylart.malbile.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.scaylart.malbile.MalbileApplication;
import br.scaylart.malbile.R;

public class PreferenceUtils {
    private PreferenceUtils() {
        throw new AssertionError();
    }

    public static void initializePreferences() {
        Context context = MalbileApplication.getInstance();

        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getString(context.getString(R.string.preference_download_storage_key), null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(context.getString(R.string.preference_download_storage_key), context.getFilesDir().getAbsolutePath());
            editor.commit();
        }
    }

    public static int getStartupScreen() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Hack Fix: http://stackoverflow.com/questions/5227478/getting-integer-or-index-values-from-a-list-preference
        return Integer.valueOf(sharedPreferences.getString(context.getString(R.string.preference_startup_key), context.getString(R.string.preference_startup_default_value)));
    }

    public static String getSource() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.preference_source_key), context.getString(R.string.preference_source_default_value));
    }

    public static boolean isWiFiOnly() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.preference_download_wifi_key), true);
    }

    public static boolean isExternalStorage() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preferenceDirectory = sharedPreferences.getString(context.getString(R.string.preference_download_storage_key), null);
        String internalDirectory = context.getFilesDir().getAbsolutePath();

        return !(preferenceDirectory != null ? preferenceDirectory.equals(internalDirectory) : false);
    }

    public static String getDownloadDirectory() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.preference_download_storage_key), context.getFilesDir().getAbsolutePath());
    }

    /**
     * @return token da sessão feita durante o login
     */
    public static String getTokenAcess() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.preference_token_key), null);
    }

    /**
     * Seta o cookie da sessão retornado ao fazer login
     *
     * @param token
     */
    public static void setTokenAcess(String token) {
        Context context = MalbileApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.preference_token_key), token);
        editor.commit();
    }

    /**
     * @return Cookie da sessão feita durante o login
     */
    public static String getCookie() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.preference_cookie_key), null);
    }

    /**
     * Seta o cookie da sessão retornado ao fazer login
     *
     * @param cookie
     */
    public static void setCookie(String cookie) {
        Context context = MalbileApplication.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.preference_cookie_key), cookie);
        editor.commit();
    }

    /**
     * Returns the custom share text.
     *
     * @return String The custom share text that the app should use.
     * @see br.scaylart.malbile.views.activities.DetailActivity
     */
    public static String getCustomShareText() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.preference_sharing_key), context.getString(R.string.preference_sharing_default_value));
    }

    public static String getViewType() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.preference_view_type_key), context.getString(R.string.preference_view_type_default_value));
    }

    public static boolean isLazyLoading() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.preference_lazy_loading_key), true);
    }

    public static boolean isRightToLeftDirection() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.preference_direction_key), false);
    }

    public static void setDirection(boolean isRightToLeftDirection) {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_direction_key), isRightToLeftDirection);
        editor.commit();
    }

    public static boolean isLockOrientation() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.preference_orientation_key), false);
    }

    public static void setOrientation(boolean isLockOrientation) {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_orientation_key), isLockOrientation);
        editor.commit();
    }

    public static boolean isLockZoom() {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.preference_zoom_key), false);
    }

    public static void setZoom(boolean isLockZoom) {
        Context context = MalbileApplication.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.preference_zoom_key), isLockZoom);
        editor.commit();
    }
}
