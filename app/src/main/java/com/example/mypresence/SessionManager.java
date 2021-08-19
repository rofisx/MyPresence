package com.example.mypresence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String EMAIL = "EMAIL";
    public static final String NAME = "NAME";
//    public static final String PHOTO = "PHOTO";
    public static final String JABATAN = "JABATAN";
    public static final String IMEI = "IMEI";
    public static final String CHPWD = "CHPWD";



    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String email, String nama, String jabatan, String imei, String changePw) {
        editor.putBoolean(LOGIN, true);
        editor.putString(EMAIL, email);
        editor.putString(NAME, nama);
//        editor.putString(PHOTO, photo);
        editor.putString(JABATAN, jabatan);
        editor.putString(IMEI, imei);
        editor.putString(CHPWD, changePw);
        editor.apply();
    }

    public boolean isLoggin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin() {
        if (!this.isLoggin()) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            ((MainActivity) context).finish();
        }
    }

    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(JABATAN, sharedPreferences.getString(JABATAN, null));
        user.put(IMEI, sharedPreferences.getString(IMEI, null));
        return user;
    }

    public void logout() {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((MainActivity) context).finish();
    }
}
