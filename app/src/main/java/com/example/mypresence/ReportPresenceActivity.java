package com.example.mypresence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mypresence.ListView.AdapterCILList;
import com.example.mypresence.ListView.AdapterCIOList;
import com.example.mypresence.ListView.AdapterCOEList;
import com.example.mypresence.ListView.AdapterCOOList;
import com.example.mypresence.ListView.AdapterList;
import com.example.mypresence.ListView.CILEmployee;
import com.example.mypresence.ListView.CIOEmployee;
import com.example.mypresence.ListView.COEEmployee;
import com.example.mypresence.ListView.COOEmployee;
import com.example.mypresence.ListView.Employee;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPresenceActivity extends AppCompatActivity {
    private static String URL_R_CO_OT = "http://192.168.1.220:808/mypresence/read_rptpresence_co_ot.php";
    private static String URL_R_CO_FR = "http://192.168.1.220:808/mypresence/read_rptpresence_co_fr.php";
    private static String URL_R_CI_LT = "http://192.168.1.220:808/mypresence/read_rptpresence_ci_lt.php";
    private static String URL_R_CI_OT = "http://192.168.1.220:808/mypresence/read_rptpresence_ci_ot.php";

    private static String URL_RALL_CO_OT = "http://192.168.1.220:808/mypresence/read_rpt_allpresence_co_ot.php";
    private static String URL_RALL_CO_FR = "http://192.168.1.220:808/mypresence/read_rpt_allpresence_co_fr.php";
    private static String URL_RALL_CI_LT = "http://192.168.1.220:808/mypresence/read_rpt_allpresence_ci_lt.php";
    private static String URL_RALL_CI_OT = "http://192.168.1.220:808/mypresence/read_rpt_allpresence_ci_ot.php";

    private static String URL_RALL_DEP_CO_OT = "http://192.168.1.220:808/mypresence/read_rpt_alldep_presence_co_ot.php";
    private static String URL_RALL_DEP_CO_FR = "http://192.168.1.220:808/mypresence/read_rpt_alldep_presence_co_fr.php";
    private static String URL_RALL_DEP_CI_LT = "http://192.168.1.220:808/mypresence/read_rpt_alldep_presence_ci_lt.php";
    private static String URL_RALL_DEP_CI_OT = "http://192.168.1.220:808/mypresence/read_rpt_alldep_presence_ci_ot.php";

    private static String URL_READ = "http://192.168.1.220:808/mypresence/read_maindetail.php";
    private static String URL_EMPLOYEE = "http://192.168.1.220:808/mypresence/listemployee.php";
    private static String URL_CIO_EMPLOYEE = "http://192.168.1.220:808/mypresence/listcio_employee.php";
    private static String URL_CIL_EMPLOYEE = "http://192.168.1.220:808/mypresence/listcil_employee.php";
    private static String URL_COO_EMPLOYEE = "http://192.168.1.220:808/mypresence/listcoo_employee.php";
    private static String URL_COE_EMPLOYEE = "http://192.168.1.220:808/mypresence/listcoe_employee.php";

    private Button btn_Showrpt, btn_resetbg;
    private TextView tv_rptDept,tv_rptJab,tv_ov_NmDep, tv_itemlist_nama, tv_itemlist_qty;
    private Spinner Spin_Dep, Spin_Jab;
    private MaterialSpinner Spin_Mont, Spin_Year;
    private RadioGroup option_Rb;
    private RadioButton rb_dep,rb_jab;

    LinearLayout lay_ciolist, lay_cillist, lay_coolist, lay_coelist, lay_induk_notelist;
    RelativeLayout lay_list_relative;
    BarChart barChart_Rpt;
    InputStream is = null;
    String result = null;
    String line = null;
    String[] departemen,jabatan;
    HttpURLConnection urlConnection = null;

    String getEmailUser,strCO_OT,strCO_FR,strCI_OT,strCI_LT;
    String TAG = "AddPerson", DepM = "Marketing", DepIT = "IT Development",month;
    SessionManager sessionManager;

    ListView lv_cio, lv_cil, lv_coo, lv_coe;
    AdapterList adapterList;
    AdapterCIOList adapterCIOList;
    AdapterCILList adapterCILList;
    AdapterCOOList adapterCOOList;
    AdapterCOEList adapterCOEList;
    public static ArrayList<Employee> employeeArrayList = new ArrayList<>();
    public static ArrayList<CIOEmployee> cioEmployeeArrayList = new ArrayList<>();
    public static ArrayList<CILEmployee> cilEmployeeArrayList = new ArrayList<>();
    public static ArrayList<COOEmployee> cooEmployeeArrayList = new ArrayList<>();
    public static ArrayList<COEEmployee> coeEmployeeArrayList = new ArrayList<>();
    Employee employee;
    CIOEmployee cioEmployee;
    CILEmployee cilEmployee;
    COOEmployee cooEmployee;
    COEEmployee coeEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_presence);
        lay_induk_notelist = findViewById(R.id.lay_induk_notelist);
        lay_induk_notelist.setVisibility(View.GONE);
        lay_list_relative = findViewById(R.id.lay_rel_list);
        lay_list_relative.setVisibility(View.GONE);

        lay_ciolist = findViewById(R.id.lay_liv_cio);
        lay_ciolist.setVisibility(View.GONE);
        lay_cillist = findViewById(R.id.lay_liv_cil);
        lay_cillist.setVisibility(View.GONE);

        lay_coolist = findViewById(R.id.lay_liv_coo);
        lay_coolist.setVisibility(View.GONE);
        lay_coelist = findViewById(R.id.lay_liv_coe);
        lay_coelist.setVisibility(View.GONE);

        barChart_Rpt = findViewById(R.id.barChartrpt);
        btn_Showrpt = findViewById(R.id.btnShowBarpt);
        Spin_Dep = findViewById(R.id.spin_rptDep);
        Spin_Jab = findViewById(R.id.spin_rptJab);
        Spin_Mont = findViewById(R.id.spin_bulan);
        Spin_Year = findViewById(R.id.spin_tahun);
        spinner_mothyear();

        tv_rptDept = findViewById(R.id.tv_rptDep);
        tv_rptJab = findViewById(R.id.tv_rptJab);
        tv_ov_NmDep = findViewById(R.id.tv_rpt_NamaDep);
        tv_itemlist_nama = findViewById(R.id.tv_nama_ci_co);
        tv_itemlist_qty = findViewById(R.id.tv_qty_ci_co);
        option_Rb = findViewById(R.id.rg_1);
        rb_dep = findViewById(R.id.radiobtn_dep);
        rb_jab = findViewById(R.id.radiobtn_jab);
        btn_resetbg = findViewById(R.id.reset_bg);

        //list check In Ontime
        lv_cio = findViewById(R.id.list_emp_cio);
        adapterCIOList = new AdapterCIOList(this, cioEmployeeArrayList);
        lv_cio.setAdapter(adapterCIOList);
        lv_cio.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        //list check In Terlambat
        lv_cil = findViewById(R.id.list_emp_cil);
        adapterCILList = new AdapterCILList(this, cilEmployeeArrayList);
        lv_cil.setAdapter(adapterCILList);
        lv_cil.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        //list check Out Ontime
        lv_coo = findViewById(R.id.list_emp_coo);
        adapterCOOList = new AdapterCOOList(this, cooEmployeeArrayList);
        lv_coo.setAdapter(adapterCOOList);
        lv_coo.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        //list check Out Awal
        lv_coe = findViewById(R.id.list_emp_coe);
        adapterCOEList = new AdapterCOEList(this, coeEmployeeArrayList);
        lv_coe.setAdapter(adapterCOEList);
        lv_coe.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        sessionManager.checkLogin();
        getEmailUser = user.get(sessionManager.EMAIL);
        //connection_spindept();
        btn_Showrpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                condition_MY();
                Option_Btn_Spinner();
                //getCI_OTDetail();
                //getbarChartRpt();
            }
        });
        btn_resetbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option_Rb.clearCheck();
                Spin_Dep.setEnabled(true);
                Spin_Jab.setEnabled(true);
            }
        });

        Opsi_RadioGroup();

    }
    // Setting material Spinner Bulan & Tahun
    private void spinner_mothyear(){
        Spin_Mont.setItems("Desember","November","Oktober","September","Agustus","Juli","Juni","Mei","April","Maret","Februari","Januari");
        Spin_Year.setItems("2025","2024","2023","2022","2021","2020","2019","2018","2017");
//        Spin_Mont.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {//
//            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
//            }
//        });
//        Spin_Year.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
//
//            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
//            }
//        });
    }

    private void condition_MY(){
        String bulan = Spin_Mont.getText().toString().trim();
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

    private void Option_Btn_Spinner(){
        if (rb_dep.isChecked()){
            getCI_ALLOTDetail();
            lay_induk_notelist.setVisibility(View.GONE);
            lay_list_relative.setVisibility(View.GONE);
        }else if (tv_ov_NmDep.getText().toString().equals("Marketing") && rb_jab.isChecked()){
            getCI_MJALLDEP_OTDetail();
            lay_induk_notelist.setVisibility(View.GONE);
            lay_list_relative.setVisibility(View.GONE);
        }else if (tv_ov_NmDep.getText().toString().equals("IT Development") && rb_jab.isChecked()){
            getCI_MJALLDEP_OTDetail();
            lay_induk_notelist.setVisibility(View.GONE);
            lay_list_relative.setVisibility(View.GONE);
        }else if (tv_ov_NmDep.getText().toString().equals("Marketing") || tv_ov_NmDep.getText().toString().equals("IT Development")){
            //extend list
            getCI_MJ_OTDetail();
        }else if (rb_jab.isChecked()){
            getCI_ALLDEP_OTDetail();
            lay_induk_notelist.setVisibility(View.GONE);
            lay_list_relative.setVisibility(View.GONE);
        }else{
            //extend list
            getCI_OTDetail();
        }
    }

    private void Opsi_RadioGroup(){
        option_Rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radiobtn_dep:
                        Spin_Dep.setEnabled(false);
                        Spin_Jab.setEnabled(false);
                        break;
                    case R.id.radiobtn_jab:
                        Spin_Dep.setEnabled(true);
                        Spin_Jab.setEnabled(false);
                }
            }
        });
    }

    //Get User Detail Login
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
                                    if(strJab.equals("Manager Marketing")){
                                        Spin_Dep.setEnabled(false);
                                        Spin_Dep.setVisibility(View.GONE);
                                        tv_ov_NmDep.setVisibility(View.VISIBLE);
                                        tv_ov_NmDep.setText(DepM);
                                        rb_dep.setVisibility(View.GONE);
                                        connection_spinjab_marketing();
                                    }else if (strJab.equals("IT Project Manager")){
                                        Spin_Dep.setEnabled(false);
                                        Spin_Dep.setVisibility(View.GONE);
                                        tv_ov_NmDep.setVisibility(View.VISIBLE);
                                        tv_ov_NmDep.setText(DepIT);
                                        rb_dep.setVisibility(View.GONE);
                                        connection_spinjab_IT();
                                    }else if (strDep.equals("Human Resource Department")){
                                        Spin_Dep.setEnabled(true);
                                        Spin_Dep.setVisibility(View.VISIBLE);
                                        rb_dep.setVisibility(View.VISIBLE);
                                        connection_spindept();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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

    //ListKaryawanPercobaan
    public void datalistEmployee(){
        final String year = Spin_Year.getText().toString().trim();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        final String spinJab = Spin_Jab.getSelectedItem().toString().trim();
        StringRequest request = new StringRequest(Request.Method.POST, URL_EMPLOYEE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        employeeArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(sucess.equals("1")){
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String name = object.getString("nama");
                                    String come = object.getString("datang");
                                    String come_late = object.getString("datangterlambat");
                                    String go_home = object.getString("pulang");
                                    String go_home_late = object.getString("pulangawal");

                                    employee = new Employee(name,come,come_late,go_home,go_home_late);
                                    employeeArrayList.add(employee);
                                    adapterList.notifyDataSetChanged();
                                }
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading, Silahkan Coba Lagi " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ReportPresenceActivity.this, "Data Tidak Tampil "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){//Tambahan
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("jab", spinJab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    /*==========List Karyawan=============*/
    //ListKaryawan_CIO
    public void cio_datalistEmployee(){
        final String year = Spin_Year.getText().toString().trim();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        lay_induk_notelist.setVisibility(View.VISIBLE);
        lay_list_relative.setVisibility(View.VISIBLE);
        lay_ciolist.setVisibility(View.VISIBLE);
        final String spinJab = Spin_Jab.getSelectedItem().toString().trim();
        StringRequest request = new StringRequest(Request.Method.POST, URL_CIO_EMPLOYEE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        cioEmployeeArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(sucess.equals("1")){
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String nama_cio = object.getString("namacio");
                                    String come_cio = object.getString("datangcio");

                                    cioEmployee = new CIOEmployee(nama_cio,come_cio);
                                    cioEmployeeArrayList.add(cioEmployee);
                                    adapterCIOList.notifyDataSetChanged();
                                }
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading, Silahkan Coba Lagi " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                Toast.makeText(ReportPresenceActivity.this, "Data Tidak Tampil "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){//Tambahan
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("jab", spinJab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    //ListKaryawan_CIL
    public void cil_datalistEmployee(){
        final String year = Spin_Year.getText().toString().trim();
        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Loading...");
        //progressDialog.show();
        lay_induk_notelist.setVisibility(View.VISIBLE);
        lay_list_relative.setVisibility(View.VISIBLE);
        lay_cillist.setVisibility(View.VISIBLE);
        final String spinJab = Spin_Jab.getSelectedItem().toString().trim();
        StringRequest request = new StringRequest(Request.Method.POST, URL_CIL_EMPLOYEE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        cilEmployeeArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(sucess.equals("1")){
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String nama_cil = object.getString("namacil");
                                    String come_cil = object.getString("datangcil");

                                    cilEmployee = new CILEmployee(nama_cil,come_cil);
                                    cilEmployeeArrayList.add(cilEmployee);
                                    adapterCILList.notifyDataSetChanged();
                                }
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading, Silahkan Coba Lagi " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                Toast.makeText(ReportPresenceActivity.this, "Data Tidak Tampil "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){//Tambahan
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("jab", spinJab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    //ListKaryawan_COO
    public void coo_datalistEmployee(){
        final String year = Spin_Year.getText().toString().trim();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        lay_induk_notelist.setVisibility(View.VISIBLE);
        lay_list_relative.setVisibility(View.VISIBLE);
        lay_coolist.setVisibility(View.VISIBLE);
        final String spinJab = Spin_Jab.getSelectedItem().toString().trim();
        StringRequest request = new StringRequest(Request.Method.POST, URL_COO_EMPLOYEE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        cooEmployeeArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(sucess.equals("1")){
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String nama_coo = object.getString("namacoo");
                                    String come_coo = object.getString("datangcoo");

                                    cooEmployee = new COOEmployee(nama_coo,come_coo);
                                    cooEmployeeArrayList.add(cooEmployee);
                                    adapterCOOList.notifyDataSetChanged();
                                }
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading, Silahkan Coba Lagi " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                Toast.makeText(ReportPresenceActivity.this, "Data Tidak Tampil "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){//Tambahan
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("jab", spinJab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    //ListKaryawan_COE
    public void coe_datalistEmployee(){
        final String year = Spin_Year.getText().toString().trim();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        lay_induk_notelist.setVisibility(View.VISIBLE);
        lay_list_relative.setVisibility(View.VISIBLE);
        lay_coelist.setVisibility(View.VISIBLE);
        final String spinJab = Spin_Jab.getSelectedItem().toString().trim();
        StringRequest request = new StringRequest(Request.Method.POST, URL_COE_EMPLOYEE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        coeEmployeeArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(sucess.equals("1")){
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String nama_coe = object.getString("namacoe");
                                    String come_coe = object.getString("datangcoe");

                                    coeEmployee = new COEEmployee(nama_coe,come_coe);
                                    coeEmployeeArrayList.add(coeEmployee);
                                    adapterCOEList.notifyDataSetChanged();
                                }
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading, Silahkan Coba Lagi " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ReportPresenceActivity.this, "Data Tidak Tampil "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){//Tambahan
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("jab", spinJab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    /*==========End=============*/

    // Connection Spinner Departemen
    private void connection_spindept(){
        StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final List<String> listdept = new ArrayList<>();
        try {
            URL url = new URL("http://192.168.1.220:808/mypresence/listdepartemen.php");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            is = urlConnection.getInputStream();
        }catch (Exception e)
        {
            Log.e("Failed", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line+"\n\n\n");
            }
            is.close();
            result = stringBuilder.toString();
        }
        catch (Exception e){
            Log.e("Failed", e.toString());
        }

        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = null;
            departemen = new String[jsonArray.length()];
            for (int i=0; i<jsonArray.length();i++)
            {
                jsonObject = jsonArray.getJSONObject(i);
                departemen[i]=jsonObject.getString("NamaDepartemen");
            }
            //Toast.makeText(getApplicationContext(), "Data Loaded", Toast.LENGTH_SHORT).show();
            for (int i=0; i<departemen.length;i++)
            {
                listdept.add(departemen[i]);
            }
            //Toast.makeText(getApplicationContext(), "Tampil Nama Departemen", Toast.LENGTH_SHORT).show();
            spinner_departemen();
        }catch (Exception e)
        {
            Log.e("Fail",e.toString());
        }
    }
    // Call Spinner Departemen
    private void spinner_departemen(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ReportPresenceActivity.this, android.R.layout.simple_spinner_item,departemen);
        Spin_Dep.setAdapter(arrayAdapter);
        Spin_Dep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spin_Dep.setSelection(position);
                connection_spinjab();
//                String selectdept = String.valueOf(spinnerdept.getSelectedItem());
//                edt_rnama.setText(selectdept);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Connection Spinner Jabatan
    private void connection_spinjab(){
        StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final List<String> listdept = new ArrayList<>();
        String selectdept = String.valueOf(Spin_Dep.getSelectedItem());
        ContentValues values = new ContentValues();
        values.put("1",selectdept);
        try {
            URL url = new URL("http://192.168.1.220:808/mypresence/listjabatan.php?stringjab="+selectdept);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            is = urlConnection.getInputStream();
        }catch (Exception e)
        {
            Log.e("Failed", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line+"\n\n\n");
            }
            is.close();
            result = stringBuilder.toString();
        }
        catch (Exception e){
            Log.e("Failed", e.toString());
        }

        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = null;
            jabatan = new String[jsonArray.length()];
            for (int i=0; i<jsonArray.length();i++)
            {
                jsonObject = jsonArray.getJSONObject(i);
                jabatan[i]=jsonObject.getString("NamaJabatan");
            }
            //Toast.makeText(getApplicationContext(), "Data Loaded", Toast.LENGTH_SHORT).show();
            for (int i=0; i<jabatan.length;i++)
            {
                listdept.add(jabatan[i]);

            }
            //Toast.makeText(getApplicationContext(), "Tampil Nama Jabatan", Toast.LENGTH_SHORT).show();
            spinner_jabatan();
        }catch (Exception e)
        {
            Log.e("Fail",e.toString());
        }
    }
    // Call Spinner Jabatan
    private void spinner_jabatan(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ReportPresenceActivity.this, android.R.layout.simple_spinner_item,jabatan);
        Spin_Jab.setAdapter(arrayAdapter);
        Spin_Jab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spin_Jab.setSelection(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Connection Spinner Jabatan By Departemen
    private void connection_spinjab_marketing(){
        StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final List<String> listdept = new ArrayList<>();
        String selectdept = DepM;
        ContentValues values = new ContentValues();
        values.put("1",selectdept);
        try {
            URL url = new URL("http://192.168.1.220:808/mypresence/listoption_jabatan.php?stringjab="+selectdept);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            is = urlConnection.getInputStream();
        }catch (Exception e)
        {
            Log.e("Failed", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line+"\n\n\n");
            }
            is.close();
            result = stringBuilder.toString();
        }
        catch (Exception e){
            Log.e("Failed", e.toString());
        }

        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = null;
            jabatan = new String[jsonArray.length()];
            for (int i=0; i<jsonArray.length();i++)
            {
                jsonObject = jsonArray.getJSONObject(i);
                jabatan[i]=jsonObject.getString("NamaJabatan");
            }
            //Toast.makeText(getApplicationContext(), "Data Loaded", Toast.LENGTH_SHORT).show();
            for (int i=0; i<jabatan.length;i++)
            {
                listdept.add(jabatan[i]);

            }
            //Toast.makeText(getApplicationContext(), "Tampil Nama Jabatan", Toast.LENGTH_SHORT).show();
            spinner_jabatan();
        }catch (Exception e)
        {
            Log.e("Fail",e.toString());
        }
    }
//    private void spinner_option_marketing(){
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ReportPresenceActivity.this, android.R.layout.simple_spinner_item,jabatan);
//        Spin_Jab.setAdapter(arrayAdapter);
//        Spin_Jab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Spin_Jab.setSelection(position);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
    private void connection_spinjab_IT(){
        StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final List<String> listdept = new ArrayList<>();
        String selectdept = DepIT;
        ContentValues values = new ContentValues();
        values.put("1",selectdept);
        try {
            URL url = new URL("http://192.168.1.220:808/mypresence/listoption_jabatan.php?stringjab="+selectdept);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            is = urlConnection.getInputStream();
        }catch (Exception e)
        {
            Log.e("Failed", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line+"\n\n\n");
            }
            is.close();
            result = stringBuilder.toString();
        }
        catch (Exception e){
            Log.e("Failed", e.toString());
        }

        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = null;
            jabatan = new String[jsonArray.length()];
            for (int i=0; i<jsonArray.length();i++)
            {
                jsonObject = jsonArray.getJSONObject(i);
                jabatan[i]=jsonObject.getString("NamaJabatan");
            }
            //Toast.makeText(getApplicationContext(), "Data Loaded", Toast.LENGTH_SHORT).show();
            for (int i=0; i<jabatan.length;i++)
            {
                listdept.add(jabatan[i]);

            }
            //Toast.makeText(getApplicationContext(), "Tampil Nama Jabatan", Toast.LENGTH_SHORT).show();
            spinner_jabatan();
        }catch (Exception e)
        {
            Log.e("Fail",e.toString());
        }
    }
//    private void spinner_option_IT(){
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ReportPresenceActivity.this, android.R.layout.simple_spinner_item,jabatan);
//        Spin_Jab.setAdapter(arrayAdapter);
//        Spin_Jab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Spin_Jab.setSelection(position);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }
    //Show Bar Chart
    private  void  getbarChartRpt(){
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
        barChart_Rpt.setFitBars(true);
        barChart_Rpt.setData(barData);
        barChart_Rpt.getDescription().setText("Bar Chart Kehadiran");
        barChart_Rpt.animateY(1000);
        //format angka
        barChart_Rpt.getXAxis().setValueFormatter(new MyValueFormatter());
        barChart_Rpt.getAxisLeft().setValueFormatter(new MyValueFormatter());
        barChart_Rpt.getAxisRight().setValueFormatter(new MyValueFormatter());

        Legend l = barChart_Rpt.getLegend();
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
    /*Selection Bar by Departemen and Jabatan*/
    private void getCO_OTDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

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
                                    getbarChartRpt();
                                    if (strCO_OT.equals("0")){
                                        //lv_cil.setAdapter(null);
                                        cooEmployeeArrayList.clear();
                                        adapterCOOList.notifyDataSetChanged();
                                        coo_datalistEmployee();
                                    }else{
                                        adapterCOOList = new AdapterCOOList(ReportPresenceActivity.this, cooEmployeeArrayList);
                                        lv_coo.setAdapter(adapterCOOList);
                                        coo_datalistEmployee();
                                    }
                                }
                                //datalistEmployee();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCO_FRDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

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
                                    //tv_rptJab.setText(strCO_FR);
                                    getCO_OTDetail();
                                    //getbarChartRpt();
                                    if (strCO_FR.equals("0")){
                                        //lv_cil.setAdapter(null);
                                        coeEmployeeArrayList.clear();
                                        adapterCOEList.notifyDataSetChanged();
                                        coe_datalistEmployee();
                                    }else{
                                        adapterCOEList = new AdapterCOEList(ReportPresenceActivity.this, coeEmployeeArrayList);
                                        lv_coe.setAdapter(adapterCOEList);
                                        coe_datalistEmployee();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_LTDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CI_LT,
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
                                    strCI_LT = object.getString("checkin_lt").trim();
                                    //getbarChartRpt();
                                    getCO_FRDetail();
                                    if (strCI_LT.equals("0")){
                                        //lv_cil.setAdapter(null);
                                        cilEmployeeArrayList.clear();
                                        adapterCILList.notifyDataSetChanged();
                                        cil_datalistEmployee();
                                    }else{
                                        adapterCILList = new AdapterCILList(ReportPresenceActivity.this, cilEmployeeArrayList);
                                        lv_cil.setAdapter(adapterCILList);
                                        cil_datalistEmployee();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_OTDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CI_OT,
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
                                    strCI_OT = object.getString("checkin_ot").trim();
                                    getCI_LTDetail();
//                                    getbarChartRpt();
                                    if (strCI_OT.equals("0")){
                                        //lv_cio.setAdapter(null);
                                        cioEmployeeArrayList.clear();
                                        adapterCIOList.notifyDataSetChanged();
                                        cio_datalistEmployee();
                                    }else{
                                        adapterCIOList = new AdapterCIOList(ReportPresenceActivity.this, cioEmployeeArrayList);
                                        lv_cio.setAdapter(adapterCIOList);
                                        cio_datalistEmployee();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*==========END===========*/

    /*Selection Bar by Departemen and Jabatan*/
    private void getCO_ALLOTDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final String year = Spin_Year.getText().toString().trim();
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_CO_OT,
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
                                    strCO_OT = object.getString("checkout_ot").trim();
//                                    tv_rptJab.setText(strCO_OT);
                                    getbarChartRpt();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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
    private void getCO_ALLFRDetail() {
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_CO_FR,
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
                                    //tv_rptJab.setText(strCO_FR);
                                    getCO_ALLOTDetail();
                                    //getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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
    private void getCI_ALLLTDetail() {
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_CI_LT,
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
                                    strCI_LT = object.getString("checkin_lt").trim();
                                    //getbarChartRpt();
                                    getCO_ALLFRDetail();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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
    private void getCI_ALLOTDetail() {
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_CI_OT,
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
                                    strCI_OT = object.getString("checkin_ot").trim();
                                    getCI_ALLLTDetail();
//                                    getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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
    /*==========END===========*/

    /*Selection Bar ALL Jabatan by Departemen*/
    private void getCO_ALLDEP_OTDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CO_OT,
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
                                    strCO_OT = object.getString("checkout_ot").trim();
                                    getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCO_ALLDEP_FRDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CO_FR,
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
                                    //tv_rptJab.setText(strCO_FR);
                                    getCO_ALLDEP_OTDetail();
                                    //getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_ALLDEP_LTDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CI_LT,
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
                                    strCI_LT = object.getString("checkin_lt").trim();
                                    //getbarChartRpt();
                                    getCO_ALLDEP_FRDetail();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_ALLDEP_OTDetail() {
        final String rptdept = this.Spin_Dep.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CI_OT,
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
                                    strCI_OT = object.getString("checkin_ot").trim();
                                    getCI_ALLDEP_LTDetail();
//                                    getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*==========END===========*/

    /*Selection Bar Manager by All Jabatan*/
    private void getCO_MJ_OTDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

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
                                    getbarChartRpt();
                                    if (strCO_OT.equals("0")){
                                        //lv_cil.setAdapter(null);
                                        cooEmployeeArrayList.clear();
                                        adapterCOOList.notifyDataSetChanged();
                                        coo_datalistEmployee();
                                    }else{
                                        adapterCOOList = new AdapterCOOList(ReportPresenceActivity.this, cooEmployeeArrayList);
                                        lv_coo.setAdapter(adapterCOOList);
                                        coo_datalistEmployee();
                                    }
                                }
                                //datalistEmployee();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCO_MJ_FRDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();

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
                                    //tv_rptJab.setText(strCO_FR);
                                    getCO_MJ_OTDetail();
                                    //getbarChartRpt();
                                    if (strCO_FR.equals("0")){
                                        //lv_cil.setAdapter(null);
                                        coeEmployeeArrayList.clear();
                                        adapterCOEList.notifyDataSetChanged();
                                        coe_datalistEmployee();
                                    }else{
                                        adapterCOEList = new AdapterCOEList(ReportPresenceActivity.this, coeEmployeeArrayList);
                                        lv_coe.setAdapter(adapterCOEList);
                                        coe_datalistEmployee();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_MJ_LTDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CI_LT,
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
                                    strCI_LT = object.getString("checkin_lt").trim();
                                    //getbarChartRpt();
                                    getCO_MJ_FRDetail();
                                    if (strCI_LT.equals("0")){
                                        //lv_cil.setAdapter(null);
                                        cilEmployeeArrayList.clear();
                                        adapterCILList.notifyDataSetChanged();
                                        cil_datalistEmployee();
                                    }else{
                                        adapterCILList = new AdapterCILList(ReportPresenceActivity.this, cilEmployeeArrayList);
                                        lv_cil.setAdapter(adapterCILList);
                                        cil_datalistEmployee();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_MJ_OTDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String rptjab = this.Spin_Jab.getSelectedItem().toString().trim();
        final String year = Spin_Year.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_R_CI_OT,
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
                                    strCI_OT = object.getString("checkin_ot").trim();
                                    getCI_MJ_LTDetail();
//                                    getbarChartRpt();
                                    if (strCI_OT.equals("0")){
                                        //lv_cio.setAdapter(null);
                                        cioEmployeeArrayList.clear();
                                        adapterCIOList.notifyDataSetChanged();
                                        cio_datalistEmployee();
                                    }else{
                                        adapterCIOList = new AdapterCIOList(ReportPresenceActivity.this, cioEmployeeArrayList);
                                        lv_cio.setAdapter(adapterCIOList);
                                        cio_datalistEmployee();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("namajab", rptjab);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*==========END===========*/

    /*Selection Bar by ALL Departemen*/
    private void getCO_MJALLDEP_OTDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CO_OT,
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
                                    strCO_OT = object.getString("checkout_ot").trim();
                                    getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCO_MJALLDEP_FRDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CO_FR,
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
                                    //tv_rptJab.setText(strCO_FR);
                                    getCO_MJALLDEP_OTDetail();
                                    //getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_MJALLDEP_LTDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CI_LT,
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
                                    strCI_LT = object.getString("checkin_lt").trim();
                                    //getbarChartRpt();
                                    getCO_MJALLDEP_FRDetail();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getCI_MJALLDEP_OTDetail() {
        final String rptdept = this.tv_ov_NmDep.getText().toString().trim();
        final String year = Spin_Year.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RALL_DEP_CI_OT,
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
                                    strCI_OT = object.getString("checkin_ot").trim();
                                    getCI_MJALLDEP_LTDetail();
//                                    getbarChartRpt();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(ReportPresenceActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEmailUser);
                params.put("namadept", rptdept);
                params.put("bulan", month);
                params.put("tahun", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*==========END===========*/

    /*------Get User resume---------*/
    @Override
    protected void onResume() {
        super.onResume();
//        getCI_LTDetail();
        getUserDetail();
    }
    /*------Back Arrow Action Bar ----*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(myIntent,0);
        finish();
        return true;
    }
    /*-- Aksi Tombol Back --*/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}