package com.example.mypresence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
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

public class CheckOutActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView imgppCheckOut,imgStatusFc;
    private TextView nmCheckOut,depCheckOut,jabCheckOut, tvLatCheck, tvLonCheck, tvAlamatCheckOut, statusCheckOut;
    private EditText edtKCheckOut;
    private Button btnFaceAuth, btnSincAddress, btnVCheckOut;
    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap;
    Location mLastLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    SessionManager sessionManager;
    private static String URL_READ_COUT = "http://192.168.1.220:808/mypresence/read_maindetail.php";
    private static String URL_UPLOAD_COUT = "http://192.168.1.220:808/mypresence/checkOut.php";
    String getEmailUser,url, TAG = "AddPerson", TrsCheckOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        nmCheckOut = findViewById(R.id.tvNamauserOut);
        depCheckOut = findViewById(R.id.tvDepart);
        jabCheckOut = findViewById(R.id.tvJab);
        tvAlamatCheckOut = findViewById(R.id.tvLokasi);
        tvLatCheck = findViewById(R.id.tvLat);
        tvLonCheck = findViewById(R.id.tvLon);
        edtKCheckOut = findViewById(R.id.edtKeterangan);
        btnFaceAuth = findViewById(R.id.checkOutFace);
        btnSincAddress = findViewById(R.id.checkOutAddr);
        btnVCheckOut = findViewById(R.id.btnVCheckOut);
        statusCheckOut = findViewById(R.id.msgFaceAuthOut);
        imgStatusFc = findViewById(R.id.imgStatus);
        imgppCheckOut = findViewById(R.id.imgppout);

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
                Intent intent = new Intent(CheckOutActivity.this, CameraOutActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnVCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnValCheckOut();
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
        String status;
        statusCheckOut.setText(getIntent().getStringExtra("fverified"));
        status = statusCheckOut.getText().toString().trim();
        if (status.equals("")){
            imgStatusFc.setVisibility(View.GONE);
            statusCheckOut.setText("Status");
        }else if (status.equals("Berhasil")){
            imgStatusFc.setVisibility(View.VISIBLE);
            imgStatusFc.setImageResource(R.drawable.ic_check);
        }else{
            imgStatusFc.setVisibility(View.VISIBLE);
            imgStatusFc.setImageResource(R.drawable.ic_salah);
        }
    }

    /*------Cek Button Singkron Wajah----*/
    private void checkBtnFace(){
        String status;
        status = statusCheckOut.getText().toString().trim();
        if (status.equals("")||status.equals("Status")){
            Toast.makeText(CheckOutActivity.this, "Silahkan Verifikasi Wajah Dahulu", Toast.LENGTH_SHORT).show();
        }else {
            fetchLastLocation();
        }
    }

    /*------Get User---------*/
    private void getUserDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ_COUT,
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
                                    nmCheckOut.setText(strName);
                                    jabCheckOut.setText(strJab);
                                    depCheckOut.setText(strDep);
                                    url  = "http://192.168.1.220:808/mypresence/photo_profile/"+strEmail+".jpg";
                                    ImageRetriveWithPicasso();
//                                    String urs = Config.DATA_URL + strPhoto;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(CheckOutActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckOutActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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
                .into(imgppCheckOut);
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
            List<Address> list = geocoder.getFromLocation(lat,lon,1);
            //Alamat = list.get(0).getAddressLine(0)+","+list.get(0).getCountryName();
            Alamat = list.get(0).getAdminArea();
            tvAlamatCheckOut.setText(list.get(0).getAddressLine(0));

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
                    SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fr_mapCheckOut);
                    supportMapFragment.getMapAsync(CheckOutActivity.this);
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
    private void btnValCheckOut(){
        String status,lokasi,keterangan;
        status = statusCheckOut.getText().toString().trim();
        lokasi = tvAlamatCheckOut.getText().toString().trim();
        keterangan = edtKCheckOut.getText().toString().trim();
        if (status.equals("")||status.equals("Status")){
            Toast.makeText(CheckOutActivity.this, "Silahkan Verifikasi Wajah Dahulu", Toast.LENGTH_SHORT).show();
        }else if (lokasi.isEmpty()||lokasi.equals("")){
            Toast.makeText(CheckOutActivity.this, "Silahkan Lakukan Deteksi Lokasi", Toast.LENGTH_SHORT).show();
        }
        else if (keterangan.isEmpty()){
            Toast.makeText(CheckOutActivity.this, "Silahkan Masukan Keterangan", Toast.LENGTH_SHORT).show();
            edtKCheckOut.setError("Keterangan Kosong");
        }else {
            UploadCheckOut();
        }
    }

    /*--Upload Check Out*/
    private void UploadCheckOut(){
        //Toast.makeText(CheckInActivity.this, "Presensi Berhasil", Toast.LENGTH_SHORT).show();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = simpleDateFormat.format(new Date());
        TrsCheckOut = "OUT"+nmCheckOut.getText().toString()+format;
        final String dept = this.depCheckOut.getText().toString().trim();
        final String jab = this.jabCheckOut.getText().toString().trim();
        final String lokasi = this.tvAlamatCheckOut.getText().toString().trim();
        final String stwajah = this.statusCheckOut.getText().toString().trim();
        final String lat = this.tvLatCheck.getText().toString().trim();
        final String lon = this.tvLonCheck.getText().toString().trim();
        final String ket = this.edtKCheckOut.getText().toString().trim();
        final String email = getEmailUser;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_COUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                new KAlertDialog (CheckOutActivity.this,KAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Presensi Berhasil")
                                        .setConfirmText("OK")
                                        .confirmButtonColor(R.color.blue_color)
                                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                            @Override
                                            public void onClick(KAlertDialog kAlertDialog) {
                                                startActivity(new Intent(CheckOutActivity.this,MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            new KAlertDialog(CheckOutActivity.this,KAlertDialog.ERROR_TYPE)
                                    .setTitleText("Presensi Gagal " + e.toString())
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new KAlertDialog(CheckOutActivity.this,KAlertDialog.ERROR_TYPE)
                                .setTitleText("Presensi Gagal " + error.toString())
                                .show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("TrsID", TrsCheckOut);
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