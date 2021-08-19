package com.example.mypresence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.dynamic.IFragmentWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email_login, pass_login;
    private TextView txt_cracount;
    private Button btnlogin;
    private static String URL_LOGIN = "http://192.168.1.220:808/mypresence/login.php";
    SessionManager sessionManager;
    private long exitTime=0;
    private static final int REQUEST_CODE = 101;
    String IMEINumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        deleteCache();

        email_login = findViewById(R.id.edt_emlogin);
        pass_login = findViewById(R.id.edt_pass_login);
        btnlogin = findViewById(R.id.btn_login);
        txt_cracount = findViewById(R.id.txt_cracount);
        txt_cracount.setVisibility(View.GONE);
        getIMEPhone();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkInputLogin();
            }
        });
    }
    /*--Mengakses IMEI--*/
    private void getIMEPhone(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_CODE);
            return;
        }
        IMEINumber = telephonyManager.getDeviceId();
        txt_cracount.setText(IMEINumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(LoginActivity.this, "Akses Disetujui", Toast.LENGTH_SHORT).show();
                    getIMEPhone();
                }else {
                    Toast.makeText(LoginActivity.this, "Akses Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*--Check Input Login--*/
    private void checkInputLogin(){
        String mmail = email_login.getText().toString().trim();
        String mpassword = pass_login.getText().toString().trim();

        if (mmail.isEmpty()) {
            email_login.setError("Email Kosong");
            Toast.makeText(LoginActivity.this, "Email Kosong", Toast.LENGTH_SHORT).show();
        } else if (mpassword.isEmpty()) {
            pass_login.setError("Password Kosong");
            Toast.makeText(LoginActivity.this, "Password Kosong", Toast.LENGTH_SHORT).show();
        } else {
            Login(mmail, mpassword);
        }
    }

    private void Login(final String emlogin, final String passlogin) {
        btnlogin.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String email = object.getString("email").trim();
                                    String nama = object.getString("nama").trim();
                                    String pwd = object.getString("pwd").trim();
//                                    String photo = object.getString("photo").trim();
                                    String jabatan = object.getString("jabatan").trim();
                                    String imei = object.getString("imeiPhone").trim();
                                    String changePw = object.getString("changePassword").trim();

                                    sessionManager.createSession(email, nama, jabatan, imei, changePw);
                                    if (changePw.equals("1") && imei.equals(IMEINumber)){
                                        Toast.makeText(LoginActivity.this, "Login Berhasil\n Username : " + email + "\nSelamat Datang " + nama, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if (changePw.equals("1") && !imei.equals(IMEINumber)){
                                        Toast.makeText(LoginActivity.this, "Silahkan Gunakan Ponsel Anda Sendiri \n atau Hubungi Administrator", Toast.LENGTH_SHORT).show();
                                        btnlogin.setVisibility(View.VISIBLE);
                                    }else if (!changePw.equals("1") && imei.equals(IMEINumber)){
                                        Toast.makeText(LoginActivity.this, "Silahkan Gunakan Ponsel Anda Sendiri \n atau Hubungi Administrator", Toast.LENGTH_SHORT).show();
                                        btnlogin.setVisibility(View.VISIBLE);
                                    }else if (!changePw.equals("1") && !imei.equals(IMEINumber)){
                                        Intent intent = new Intent(LoginActivity.this, VLoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
//                            pglogin_loading.setVisibility(View.GONE);
                            btnlogin.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Username atau Password Salah \n\n " + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        pglogin_loading.setVisibility(View.GONE);
                        btnlogin.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", emlogin);
                params.put("password", passlogin);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*-- Aksi Tombol Back --*/
    @Override
    public void onBackPressed() {
//       if ((System.currentTimeMillis() - exitTime) > 2000){
//           Toast.makeText(this, "Tekan Sekali Lagi Untuk Keluar", Toast.LENGTH_SHORT).show();
//           exitTime = System.currentTimeMillis();
//       }else {
//           android.os.Process.killProcess(android.os.Process.myPid());
//           finish();
//       }
        new AlertDialog.Builder(this)
                .setMessage("Yakin Keluar Aplikasi ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /*Clear Chache*/
    private void deleteCache(){
        try {
            File dir = this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}