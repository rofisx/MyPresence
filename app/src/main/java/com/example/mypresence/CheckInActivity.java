package com.example.mypresence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckInActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imgppCheckIn,imgStatusFc;
    private TextView nmCheckIn,depCheckIn,jabCheckIn, tvLatCheck, tvLonCheck, tvAlamatCheckIn, statusCheckIn;
    private EditText edtKCheckIn;
    private Button btnFaceAuth, btnSincAddress, btnVCheckIn;
    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap;
    Location mLastLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    SessionManager sessionManager;
    private static String URL_READ_CIN = "http://192.168.1.220:808/mypresence/read_maindetail.php";
    private static String URL_UPLOAD_CIN = "http://192.168.1.220:808/mypresence/checkIn.php";
    String getEmailUser,url, TAG = "AddPerson", TrsCheckIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        imgppCheckIn = findViewById(R.id.imgppin);
        nmCheckIn = findViewById(R.id.tvNamauserIn);
        depCheckIn = findViewById(R.id.tvDepart);
        jabCheckIn = findViewById(R.id.tvJab);
        statusCheckIn = findViewById(R.id.msgFaceAuthIn);
        imgStatusFc = findViewById(R.id.imgStatus);
        tvLatCheck = findViewById(R.id.tvLat);
        tvLonCheck = findViewById(R.id.tvLon);
        tvAlamatCheckIn = findViewById(R.id.tvLokasi);
        edtKCheckIn = findViewById(R.id.edtKeterangan);
        btnVCheckIn = findViewById(R.id.btnVCheckIn);
        btnSincAddress = findViewById(R.id.checkInAddr);
        btnFaceAuth = findViewById(R.id.checkinFace);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmailUser = user.get(sessionManager.EMAIL);
        msgFaceAuth();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btnSincAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBtnFace();
            }
        });
        btnFaceAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckInActivity.this, CameraInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnVCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnValCheckIn();
            }
        });
    }

    /*------Back Arrow Action Bar ----*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(myIntent,0);
        finish();
        return true;
    }

    /*------Tampilkan Status Wajah----*/
    private void msgFaceAuth(){
        String namauser,status;
        namauser = nmCheckIn.getText().toString().trim();
        statusCheckIn.setText(getIntent().getStringExtra("fverified"));
        status = statusCheckIn.getText().toString().trim();
        if (status.equals("")){
            imgStatusFc.setVisibility(View.GONE);
            statusCheckIn.setText("Status");
        }else if (status.equals("Berhasil")){
            imgStatusFc.setVisibility(View.VISIBLE);
            //statusCheckIn.setText("Berhasil");
            imgStatusFc.setImageResource(R.drawable.ic_check);
        }else{
            //statusCheckIn.setText("Gagal");
            imgStatusFc.setVisibility(View.VISIBLE);
            imgStatusFc.setImageResource(R.drawable.ic_salah);
        }
    }

    /*------Cek Button Singkron Wajah----*/
    private void checkBtnFace(){
        String status;
        status = statusCheckIn.getText().toString().trim();
        if (status.equals("")||status.equals("Status")){
            Toast.makeText(CheckInActivity.this, "Silahkan Verifikasi Wajah Dahulu", Toast.LENGTH_SHORT).show();
        }else {
            fetchLastLocation();
        }
    }

    /*------Get User---------*/
    private void getUserDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_CIN,
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
                                    nmCheckIn.setText(strName);
                                    jabCheckIn.setText(strJab);
                                    depCheckIn.setText(strDep);
                                    url  = "http://192.168.1.220:808/mypresence/photo_profile/"+strEmail+".jpg";
                                    ImageRetriveWithPicasso();
//                                    String urs = Config.DATA_URL + strPhoto;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(CheckInActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckInActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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

    /*------Get Photo Profile---------*/
    private void ImageRetriveWithPicasso() {
        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgppCheckIn);
    }


    /*------Call Back Map---------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        String Alamat;
        Double lat = mLastLocation.getLatitude();
        Double lon = mLastLocation.getLongitude();
        mMap = googleMap;
        Geocoder geocoder =  new Geocoder(this, Locale.getDefault());
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        try {
            List<android.location.Address> list = geocoder.getFromLocation(lat,lon,1);
            //Alamat = list.get(0).getAddressLine(0)+","+list.get(0).getCountryName();
            Alamat = list.get(0).getAdminArea();
            tvAlamatCheckIn.setText(list.get(0).getAddressLine(0));

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Lokasi Ku").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(Alamat);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            googleMap.addMarker(markerOptions);

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /*------Deteksi Lokasi---------*/
    private void fetchLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mLastLocation = location;
                    Toast.makeText(getApplicationContext(), mLastLocation.getLatitude()+""+mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fr_mapCheckIn);
                    supportMapFragment.getMapAsync(CheckInActivity.this);
                    tvLatCheck.setText(String.valueOf(mLastLocation.getLatitude()));
                    tvLonCheck.setText(String.valueOf(mLastLocation.getLongitude()));
                }
            }
        });
    }

    /*-----Permission Akses Lokasi------*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    /*--Button Validasi Check In*/
    private void btnValCheckIn(){
        String status,lokasi,keterangan;
        status = statusCheckIn.getText().toString().trim();
        lokasi = tvAlamatCheckIn.getText().toString().trim();
        keterangan = edtKCheckIn.getText().toString().trim();
        if (status.equals("")||status.equals("Status")){
            Toast.makeText(CheckInActivity.this, "Silahkan Verifikasi Wajah Dahulu", Toast.LENGTH_SHORT).show();
        }else if (lokasi.isEmpty()||lokasi.equals("")){
            Toast.makeText(CheckInActivity.this, "Silahkan Lakukan Deteksi Lokasi", Toast.LENGTH_SHORT).show();
        }
        else if (keterangan.isEmpty()){
            Toast.makeText(CheckInActivity.this, "Silahkan Masukan Keterangan", Toast.LENGTH_SHORT).show();
            edtKCheckIn.setError("Keterangan Kosong");
        }else {
            UploadCheckIn();
        }
    }

    private void UploadCheckIn(){
        //Toast.makeText(CheckInActivity.this, "Presensi Berhasil", Toast.LENGTH_SHORT).show();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = simpleDateFormat.format(new Date());
        TrsCheckIn = "IN"+nmCheckIn.getText().toString()+format;
        final String dept = this.depCheckIn.getText().toString().trim();
        final String jab = this.jabCheckIn.getText().toString().trim();
        final String lokasi = this.tvAlamatCheckIn.getText().toString().trim();
        final String stwajah = this.statusCheckIn.getText().toString().trim();
        final String lat = this.tvLatCheck.getText().toString().trim();
        final String lon = this.tvLonCheck.getText().toString().trim();
        final String ket = this.edtKCheckIn.getText().toString().trim();
        final String email = getEmailUser;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_CIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                //startActivity(new Intent(CheckInActivity.this,MainActivity.class));
                                //finish();
                                new KAlertDialog(CheckInActivity.this, KAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Presensi Berhasil")
                                        .confirmButtonColor(R.color.blue_color)
                                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                            @Override
                                            public void onClick(KAlertDialog kAlertDialog) {
                                                startActivity(new Intent(CheckInActivity.this,MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            new KAlertDialog(CheckInActivity.this,KAlertDialog.ERROR_TYPE)
                                    .setTitleText("Presensi Gagal " + e.toString())
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, "Mendaftar Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                        //progressBarloading.setVisibility(View.GONE);
                        new KAlertDialog(CheckInActivity.this,KAlertDialog.ERROR_TYPE)
                                .setTitleText("Presensi Gagal " + error.toString())
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("TrsID", TrsCheckIn);
                params.put("Email", email);
                params.put("DepID", dept);
                params.put("JabID", jab);
                params.put("StWajah", stwajah);
                params.put("Lokasi", lokasi);
                params.put("Lat", lat);
                params.put("Lon", lon);
                params.put("Ket", ket);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /*-- Aksi Tombol Back --*/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}