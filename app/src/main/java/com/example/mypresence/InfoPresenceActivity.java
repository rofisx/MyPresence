package com.example.mypresence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import retrofit2.Call;
//import retrofit2.Callback;

public class InfoPresenceActivity extends AppCompatActivity {


    private static String URL_READ = "http://192.168.1.220:808/mypresence/read_maindetail.php";
    private static String URL_R_CO_OT = "http://192.168.1.220:808/mypresence/read_infopresence_co_ot.php";
    private static String URL_R_CO_FR = "http://192.168.1.220:808/mypresence/read_infopresence_co_fr.php";
    private static String URL_R_CI_LT = "http://192.168.1.220:808/mypresence/read_infopresence_ci_lt.php";
    private static String URL_R_CI_OT = "http://192.168.1.220:808/mypresence/read_infopresence_ci_ot.php";
    private TextView tv_Nama,tv_Dep,tv_Jab,tv_infonama;
    private Button btn_ShowBarChart;
    String TAG = "AddPerson";
    SessionManager sessionManager;
    String getEmailUser,strCO_OT,strCO_FR,strCI_OT,strCI_LT,month;
    BarChart barChartInfo;

    private MaterialSpinner spininf_bulan,spininf_tahun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_presence);
        deleteCache();
        tv_Nama = findViewById(R.id.tv_namauser);
        tv_Dep = findViewById(R.id.tv_deptuser);
        tv_Jab = findViewById(R.id.tv_jabuser);
        spininf_bulan = findViewById(R.id.spininf_bulan);
        spininf_tahun = findViewById(R.id.spininf_tahun);
        spinner_mothyear();
        //tv_infonama = findViewById(R.id.tv_infonama);
        btn_ShowBarChart = findViewById(R.id.btnShowbar);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmailUser = user.get(sessionManager.EMAIL);
        barChartInfo = findViewById(R.id.barChartInfo);
        //btn_ShowBarChart.setVisibility(View.GONE);
        btn_ShowBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                condition_MY();
                getCO_OTDetail();
            }
        });
    }
    private void spinner_mothyear(){
        spininf_bulan.setItems("Desember","November","Oktober","September","Agustus","Juli","Juni","Mei","April","Maret","Februari","Januari");
        spininf_tahun.setItems("2025","2024","2023","2022","2021","2020","2019","2018","2017");
    }
    private void condition_MY(){
        String bulan = spininf_bulan.getText().toString().trim();
        if (bulan.equals("Desember")){
            month = "12";
        }else if (bulan.equals("November")){
            month = "11";
        }else if (bulan.equals("Oktober")){
            month = "10";
        }else if (bulan.equals("September")){
            month = "9";
        }else if (bulan.equals("Agustus")){
            month = "8";
        }else if (bulan.equals("Juli")){
            month = "7";
        }else if (bulan.equals("Juni")){
            month = "6";
        }else if (bulan.equals("Mei")){
            month = "5";
        }else if (bulan.equals("April")){
            month = "4";
        }else if (bulan.equals("Maret")){
            month = "3";
        }else if (bulan.equals("Februari")){
            month = "2";
        }else if (bulan.equals("Januari")){
            month = "1";
        }
    }


    private  void  getBarChart(){
        ArrayList<BarEntry> kehadiran = new ArrayList<>();
        kehadiran.add(new BarEntry(4,Integer.parseInt(strCO_FR)));
        kehadiran.add(new BarEntry(3,Integer.parseInt(strCO_OT)));
        kehadiran.add(new BarEntry(2,Integer.parseInt(strCI_LT)));
        kehadiran.add(new BarEntry(1,Integer.parseInt(strCI_OT)));
        BarDataSet barDataKehadiran = new BarDataSet(kehadiran,"Kehadiran");
        barDataKehadiran.setColors(new int[]{Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE});
        barDataKehadiran.setValueTextColor(Color.BLACK);
        barDataKehadiran.setValueTextSize(16f);
        BarData barData = new BarData(barDataKehadiran);
        //format angka
        barData.setValueFormatter(new MyValueFormatter());
        barChartInfo.setFitBars(true);
        barChartInfo.setData(barData);
        barChartInfo.getDescription().setText("Bar Chart Kehadiran");
        barChartInfo.animateY(1000);
        //format angka
        barChartInfo.getXAxis().setValueFormatter(new MyValueFormatter());


        Legend l = barChartInfo.getLegend();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        LegendEntry l1=new LegendEntry("Datang", Legend.LegendForm.DEFAULT,10f,2f,null, Color.BLUE);
        LegendEntry l2=new LegendEntry("Datang Terlambat", Legend.LegendForm.DEFAULT,10f,2f,null, Color.YELLOW);
        LegendEntry l3=new LegendEntry("Pulang", Legend.LegendForm.DEFAULT,10f,2f,null, Color.GREEN);
        LegendEntry l4=new LegendEntry("Pulang Awal", Legend.LegendForm.DEFAULT,10f,2f,null, Color.RED);
        l.setCustom(new LegendEntry[]{l1,l2,l3,l4});
    }

    public class MyValueFormatter extends ValueFormatter {
        private DecimalFormat mFormatB;
        public MyValueFormatter(){
            mFormatB = new DecimalFormat("#");
        }

        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) Math.floor(value));
        }
    }



    /*------Get User---------*/
    private void getUserDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
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
//                                    tvEmailUser.setText(strEmail);
                                    tv_Nama.setText(strName);
                                    tv_Jab.setText(strJab);
                                    tv_Dep.setText(strDep);
                                }
                                //getCO_OTDetail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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

    /*------Get Value Bar---------*/
    private void getCO_OTDetail() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        final String year = spininf_tahun.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CO_OT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    strCO_OT = object.getString("checkout_ot").trim();
                                    //getBarChart();
                                    getCO_FRDetail();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCO_FRDetail() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        final String year = spininf_tahun.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CO_FR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    strCO_FR = object.getString("checkout_fr").trim();
                                    //getBarChart();
                                    getCI_LTDetail();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_LTDetail() {
        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Loading...");
        //progressDialog.show();
        final String year = spininf_tahun.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CI_LT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    strCI_LT = object.getString("checkin_lt").trim();
                                    //getBarChart();
                                    getCI_OTDetail();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_OTDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final String year = spininf_tahun.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CI_OT,
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
                                    strCI_OT = object.getString("checkin_ot").trim();
                                    getBarChart();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(InfoPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("bulan", month);
                params.put("tahun", year);
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
/*
    private void getTestBarChart(){
        Call<List<com.example.mypresence.Chart.BarChart>> call = ConnectionAPI.getApiClient().create(ChartAPIInterFaces.class).getBarChartInfo();
        call.enqueue(new Callback<List<com.example.mypresence.Chart.BarChart>>() {
            @Override
            public void onResponse(Call<List<com.example.mypresence.Chart.BarChart>> call, retrofit2.Response<List<com.example.mypresence.Chart.BarChart>> response) {
                if (response.body()!=null)
                {
                        List<BarEntry> barEntries = new ArrayList<>();
                        for (com.example.mypresence.Chart.BarChart barChart : response.body())
                        {
                            barEntries.add(new BarEntry(barChart.getTahun(),barChart.getPertumbuhan()));

                        }
                        BarDataSet barDataSet = new BarDataSet(barEntries,"Pertumbuhan Bar");
                        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                        BarData barData = new BarData(barDataSet);
                        barData.setBarWidth(0.9f);
                        barChartInfo.setVisibility(View.VISIBLE);
                        barChartInfo.animateY(1000);
                        barChartInfo.setData(barData);
                        barChartInfo.setFitBars(true);

                        Description description = new Description();
                        description.setText("Pertumbuhan Pertahun");
                        barChartInfo.setDescription(description);
                        barChartInfo.invalidate();

                        Legend legend = barChartInfo.getLegend();
                }
            }

            @Override
            public void onFailure(Call<List<com.example.mypresence.Chart.BarChart>> call, Throwable t) {
            }
        });

    } */

    /*-- Aksi Tombol Back --*/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    /*------Back Arrow Action Bar ----*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(myIntent,0);
        finish();
        return true;
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