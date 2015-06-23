package com.fitaleks.instafeed.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.fitaleks.instafeed.R;

/**
 * Created by alexanderkulikovskiy on 20.06.15.
 */
public class Utils {
    public static void setName(@NonNull final Context context, @NonNull String userName) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putString(context.getString(R.string.pref_user_name), userName)
                .apply();
    }

    public static String getName(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_user_name), "");
    }

    public static void setUserId(@NonNull final Context context, long userId) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putLong(context.getString(R.string.pref_user_id), userId)
                .apply();
    }

    public static long getUserId(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(context.getString(R.string.pref_user_id), -1);
    }

    public static void setNextPageUrl(@NonNull final Context context, String nextUrl) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putString(context.getString(R.string.pref_next_url), nextUrl)
                .apply();
    }

    public static String getNextPageUrl(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_next_url), "");
    }

    public static void setIsNewUser(@NonNull final Context context, boolean isNewUser) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit()
                .putBoolean(context.getString(R.string.pref_is_new_user), isNewUser)
                .apply();
    }

    public static boolean getIsNewUSer(@NonNull final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_next_url), false);
    }
}
