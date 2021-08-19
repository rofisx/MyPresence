package com.example.mypresence;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edt_rnama, edt_remail, edt_rpassword;
    private TextView tv_dept;
    private Button btn_rdaftar;
    private Spinner spinnerdept, spinnerjab;
    private ProgressBar r_loading;
    private static String URL_REGIST = "http://192.168.1.220:808/mypresence/register.php";
    InputStream is = null;
    String result = null;
    String line = null;
    String[] departemen,jabatan;
    HttpURLConnection urlConnection = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_remail = findViewById(R.id.edt_regemail);
        edt_rnama = findViewById(R.id.edt_regnama);
        edt_rpassword =findViewById(R.id.edt_regpassword);
        r_loading = findViewById(R.id.loading);
        btn_rdaftar = findViewById(R.id.btn_daftar);
        spinnerdept = findViewById(R.id.spinner_dept);
        spinnerjab = findViewById(R.id.spinner_jab);
        //String selectdept = String.valueOf(spinnerdept.getSelectedItem());
        connection_spindept();

        btn_rdaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               btn_ValRegist();
            }
        });

    }

    private void btn_ValRegist(){
        if (edt_remail.getText().toString().trim().isEmpty()) {
            edt_remail.setError("Email Kosong");
        } else if (edt_rnama.getText().toString().trim().isEmpty()) {
            edt_rnama.setError("Nama Kosong");
        }else if (edt_rpassword.getText().toString().trim().isEmpty()) {
            edt_rpassword.setError("Password Kosong");
        } else {
            Regist();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    }
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item,departemen);
        spinnerdept.setAdapter(arrayAdapter);
        spinnerdept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerdept.setSelection(position);
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
        String selectdept = String.valueOf(spinnerdept.getSelectedItem());
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_item,jabatan);
        spinnerjab.setAdapter(arrayAdapter);
        spinnerjab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerjab.setSelection(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void Regist() {
        r_loading.setVisibility(View.VISIBLE);
        btn_rdaftar.setVisibility(View.GONE);

        final String remail = this.edt_remail.getText().toString().trim();
        final String rnama = this.edt_rnama.getText().toString().trim();
        final String rpass = this.edt_rpassword.getText().toString().trim();
        final String rdepartemen = this.spinnerdept.getSelectedItem().toString().trim();
        final String rjabatan = this.spinnerjab.getSelectedItem().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toast.makeText(RegisterActivity.this, "Mendaftar Berhasil", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Mendaftar Error!" + e.toString(), Toast.LENGTH_LONG).show();
                            r_loading.setVisibility(View.GONE);
                            btn_rdaftar.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Mendaftar Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                        r_loading.setVisibility(View.GONE);
                        btn_rdaftar.setVisibility(View.VISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", remail);
                params.put("password", rpass);
                params.put("nama", rnama);
                params.put("dept", rdepartemen);
                params.put("jab", rjabatan);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}