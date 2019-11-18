package com.misterc.encodedecode;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

    public static final String APP_PREFERENCES = "AppSettings";

    public static void savePreferences(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int loadPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }
}
