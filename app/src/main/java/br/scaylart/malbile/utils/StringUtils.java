package br.scaylart.malbile.utils;

import android.content.Context;
import android.content.res.Resources;

import br.scaylart.malbile.MalbileApplication;
import br.scaylart.malbile.R;

public class StringUtils {

    private StringUtils() {
        throw new AssertionError();
    }

    private static Context getContext() {
        return MalbileApplication.getInstance();
    }

    public static String getStringFromResourceArray(int resArrayId, int notFoundStringId, int index) {
        Resources res = getContext().getResources();
        try {
            String[] types = res.getStringArray(resArrayId);
            if (index < 0 || index >= types.length) // make sure to have a valid array index
                return res.getString(notFoundStringId);
            else
                return types[index];
        } catch (Resources.NotFoundException e) {
            return res.getString(notFoundStringId);
        }
    }

    public static String getString(int string) {
        return getContext().getString(string);
    }

    public static String nullCheck(String string) {
        return isEmpty(string) ? getContext().getString(R.string.unknown) : string;
    }

    public static String nullCheck(String string, int nullString) {
        return isEmpty(string) ? getContext().getString(nullString) : string;
    }

    public static String nullCheck(int number) {
        return (number == 0 ? "?" : Integer.toString(number));
    }

    public static String getDate(String string) {
        return (isEmpty(string) ? getContext().getString(R.string.unknown) : DateTools.formatDateString(string, getContext(), false));
    }

    private static boolean isEmpty(String string) {
        return ((string == null || string.equals("") || string.equals("0-00-00")));
    }
}
