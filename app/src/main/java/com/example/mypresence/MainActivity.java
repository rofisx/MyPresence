package com.example.mypresence;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.developer.kalert.KAlertDialog;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView tvNamaUser, tvEmailUser,tvDepUser, tvJabUser;
    private Button btncheckin, btncheckout, btnrptUser, btnrptEmp;
    private ImageView imgProfile;

    String TAG = "AddPerson",strTrsCIonCO,strTrsCOonCO;
    SessionManager sessionManager;
    private static String URL_MAINREAD = "http://192.168.1.220:808/mypresence/read_maindetail.php";
    private static String URL_MAINCI = "http://192.168.1.220:808/mypresence/read_maincheckin.php";
    private static String URL_MAINCO = "http://192.168.1.220:808/mypresence/read_maincheckout.php";
    String getEmailUser, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deleteCache();
        tvNamaUser = findViewById(R.id.mainName);
        tvEmailUser = findViewById(R.id.emailUser);
        tvDepUser = findViewById(R.id.DepartName);
        tvJabUser = findViewById(R.id.JabName);
        btncheckin = findViewById(R.id.checkIn);
        btncheckout = findViewById(R.id.checkOut);
        btnrptUser = findViewById(R.id.btn_rpt_user);
        btnrptEmp = findViewById(R.id.btn_rpt_karyawan);
        imgProfile = findViewById(R.id.mainPhoto);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmailUser = user.get(sessionManager.EMAIL);

        btncheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateCI();
            }
        });
        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateCIforCO();
            }
        });

        btnrptUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfoPresenceActivity.class));
                finish();
            }
        });

        btnrptEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReportPresenceActivity.class));
                finish();
            }
        });
    }

    /*----Pilihan Menu Main-----*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action, menu);
        return true;
    }

    /*-- Menu Pilihan Edit Profile / Log Out--*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_logout) {
            sessionManager.logout();
        }else if (id == R.id.action_createaccount){
            String NamaDep = tvDepUser.getText().toString().trim();
            if (NamaDep.equals("Human Resource Department")){
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                finish();
            }else {
                Toast.makeText(MainActivity.this, "User Tidak Punya Otoritas", Toast.LENGTH_SHORT).show();
                item.setVisible(false);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    /*----End Pilihan Menu-----*/

    /*------Get User---------*/
    private void getUserDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAINREAD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String strEmail = object.getString("email").trim();
                                    String strName = object.getString("nama").trim();
                                    String strJab = object.getString("jab").trim();
                                    String strDep = object.getString("dep").trim();
                                    //nama object string yang menghubungkan ke file read_detail.php
                                    tvEmailUser.setText(strEmail);
                                    tvNamaUser.setText(strName);
                                    tvJabUser.setText(strJab);
                                    tvDepUser.setText(strDep);
                                    url  = "http://192.168.1.220:808/mypresence/photo_profile/"+strEmail+".jpg";
                                    ImageRetriveWithPicasso();
                                    String NamaDep = tvDepUser.getText().toString().trim();
                                    if (NamaDep.equals("Human Resource Department")){
                                        btnrptEmp.setVisibility(View.VISIBLE);
                                    }else if (NamaDep.equals("Manager")){
                                        btnrptEmp.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        btnrptEmp.setVisibility(View.GONE);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //Method Check In
    private void getDateCI() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAINCI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    strTrsCIonCO = object.getString("trs_ci").trim();
                                    //nama object string yang menghubungkan ke file read_detail.php
                                    if (strTrsCIonCO.equals("1")){
                                        new KAlertDialog(MainActivity.this, KAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Sukses")
                                                .confirmButtonColor(R.color.blue_color)
                                                .setContentText(tvNamaUser.getText().toString() +"\n \n Sudah Check In")
                                                .show();
                                    }else{Intent checkin = new Intent(MainActivity.this,CheckInActivity.class);
                                        startActivity(checkin);
                                        finish();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //Method Check Out
    private void getDateCIforCO() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAINCI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    strTrsCIonCO = object.getString("trs_ci").trim();
                                    //nama object string yang menghubungkan ke file read_detail.php
                                }
                                getDateCO();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getDateCO() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAINCO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //getDateCIforCO();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                     strTrsCOonCO = object.getString("trs_co").trim();
                                    //nama object string yang menghubungkan ke file read_detail.php
                                }
                                if (strTrsCIonCO.equals("1") && strTrsCOonCO.equals("0")){
                                    Intent checkout = new Intent(MainActivity.this,CheckOutActivity.class);
                                    startActivity(checkout);
                                    finish();
                                }else if (strTrsCIonCO.equals("1") && strTrsCOonCO.equals("1")){
                                    new KAlertDialog(MainActivity.this, KAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Sukses")
                                            .confirmButtonColor(R.color.blue_color)
                                            .setContentText(tvNamaUser.getText().toString() +"\n \n Sudah Check Out")
                                            .show();
                                }else if (strTrsCIonCO.equals("0") && strTrsCOonCO.equals("1")){
                                    new KAlertDialog(MainActivity.this, KAlertDialog.WARNING_TYPE)
                                            .setTitleText("Belum Melakukan Check In?")
                                            .confirmButtonColor(R.color.blue_color)
                                            .setContentText(tvNamaUser.getText().toString() +"\n \n Silahkan Check In Dahulu")
                                            .show();
                                }else if (strTrsCIonCO.equals("0") && strTrsCOonCO.equals("0")){
                                    new KAlertDialog(MainActivity.this, KAlertDialog.WARNING_TYPE)
                                            .setTitleText("Belum Melakukan Check In?")
                                            .confirmButtonColor(R.color.blue_color)
                                            .setContentText(tvNamaUser.getText().toString() +"\n \n Silahkan Check In Dahulu")
                                            .show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /*------Get User resume---------*/
    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail();
    }

    @Override
    protected void onDestroy() {
        sessionManager.getUserDetail().clear();
        super.onDestroy();
    }

    /*------Get Photo Profile---------*/
    private void ImageRetriveWithPicasso() {
        Picasso.with(this)
                .load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgProfile);
    }

    /*-- Aksi Tombol Back --*/
    @Override
    public void onBackPressed() {
        sessionManager.logout();
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