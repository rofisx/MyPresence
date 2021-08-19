package com.example.mypresence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.developer.kalert.KAlertDialog;
import com.developers.imagezipper.ImageZipper;
import com.example.mypresence.Utils.FaceRecognizer;
import com.example.mypresence.Utils.FileUtils;
import com.squareup.picasso.Picasso;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.VisionDetRet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edt_epname,edt_epwd;
    private TextView tv_epmail,tv_epjab,tv_epdep;
    Spinner spin_epmail,spin_epdep,spin_epjab;
    private Button btn_simpan, btn_changephoto, btn_trainfaces;
    private ImageView img_epp;
    SessionManager sessionManager;

    private static String URL_MAINREAD = "http://192.168.1.220:808/mypresence/read_eprofiledetail.php";
    private static String URL_UPDATE_USER = "http://192.168.1.220:808/mypresence/edit_user.php";
    private static final String URL_IMAGE="http://192.168.1.220:808/mypresence/upload_foto_profile.php";
    String getEmailUser, url;
    String TAG = "AddPerson";

    InputStream is = null;
    String result = null, line = null;
    String[] namauser,departemen,jabatan;
    HttpURLConnection urlConnection = null;

    int BITMAP_QUALITY = 100;
    File file;
    private Bitmap bitmap;
    private File destination = null;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    ArrayList<File> fileArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    Uri imageUri;

    Handler handler = new Handler();
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edt_epname = findViewById(R.id.edt_epnama);
        edt_epwd = findViewById(R.id.edt_eppword);
        btn_simpan = findViewById(R.id.btn_eplogin);
        img_epp = findViewById(R.id.imgepp);
        tv_epdep = findViewById(R.id.tv_dept);
        tv_epjab = findViewById(R.id.tv_epjab);
        tv_epmail = findViewById(R.id.tv_epemail);
        spin_epmail = findViewById(R.id.spinner_epemail);
        spin_epdep = findViewById(R.id.spinner_epdept);
        spin_epjab = findViewById(R.id.spinner_epjab);
        btn_changephoto = findViewById(R.id.btnchangephoto);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmailUser = user.get(sessionManager.EMAIL);
        deleteCache();

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdateUser();
            }
        });
        btn_changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            selectImage();
            }
        });

        // Membuat Directory Training
        File folder = new File(Constants.getDLibDirectoryPath());
        boolean success = true;
        if (!folder.exists()){
            success = folder.mkdirs();
        }

        if (success){
            File image_folder = new File(Constants.getDLibImageDirectoryPath());
            image_folder.mkdirs();
            if (!new File(Constants.getFaceShapeModelPath()).exists()){
                FileUtils.copyFileFromRawToOthers(EditProfileActivity.this, R.raw.shape_predictor_5_face_landmarks, Constants.getFaceShapeModelPath());
            }
            if (!new File(Constants.getFaceDescriptorModelPath()).exists()){
                FileUtils.copyFileFromRawToOthers(EditProfileActivity.this, R.raw.dlib_face_recognition_resnet_model_v1, Constants.getFaceDescriptorModelPath());
            }
        }
    }

    /*------Back Arrow Action Bar ----*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(myIntent,0);
        finish();
        return true;
    }

    private void checkUpdateUser(){
        String epname = edt_epname.getText().toString().trim();
        String epassword = edt_epwd.getText().toString().trim();

        if (epname.isEmpty()) {
            edt_epname.setError("Nama Kosong");
            Toast.makeText(EditProfileActivity.this, "Nama Kosong", Toast.LENGTH_SHORT).show();
        } else if (epassword.isEmpty()) {
            edt_epwd.setError("Password Kosong");
            Toast.makeText(EditProfileActivity.this, "Password Kosong", Toast.LENGTH_SHORT).show();
        } else {
            UpdateUser();
        }
    }

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
                                    String strPwd = object.getString("pwd").trim();
                                    String strJab = object.getString("jab").trim();
                                    String strDep = object.getString("dep").trim();
                                    //nama object string yang menghubungkan ke file read_detail.php
                                    edt_epname.setText(strName);
                                    url  = "http://192.168.1.220:808/mypresence/photo_profile/"+strEmail+".jpg";
                                    ImageRetriveWithPicasso();
//                                    if (strDep.equals("HRD")){
//                                        tv_epmail.setVisibility(View.VISIBLE);
//                                        tv_epdep.setVisibility(View.VISIBLE);
//                                        tv_epjab.setVisibility(View.VISIBLE);
//                                        spin_epmail.setVisibility(View.VISIBLE);
//                                        spin_epdep.setVisibility(View.VISIBLE);
//                                        spin_epjab.setVisibility(View.VISIBLE);
//                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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
                .into(img_epp);
    }

//    private void connection_spinnamausr(){
//        StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        final List<String> listdept = new ArrayList<>();
//        try {
//            URL url = new URL("http://192.168.1.220:808/mypresence/listnamauser.php");
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.connect();
//            is = urlConnection.getInputStream();
//        }catch (Exception e)
//        {
//            Log.e("Failed", e.toString());
//            Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((line = reader.readLine()) != null)
//            {
//                stringBuilder.append(line+"\n\n\n");
//            }
//            is.close();
//            result = stringBuilder.toString();
//        }
//        catch (Exception e){
//            Log.e("Failed", e.toString());
//        }
//
//        try {
//            JSONArray jsonArray = new JSONArray(result);
//            JSONObject jsonObject = null;
//            namauser = new String[jsonArray.length()];
//            for (int i=0; i<jsonArray.length();i++)
//            {
//                jsonObject = jsonArray.getJSONObject(i);
//                namauser[i]=jsonObject.getString("Nama");
//            }
//            //Toast.makeText(getApplicationContext(), "Data Loaded", Toast.LENGTH_SHORT).show();
//            for (int i=0; i<namauser.length;i++)
//            {
//                listdept.add(namauser[i]);
//            }
//            //Toast.makeText(getApplicationContext(), "Tampil Nama Departemen", Toast.LENGTH_SHORT).show();
//            spinner_namauser();
//        }catch (Exception e)
//        {
//            Log.e("Fail",e.toString());
//        }
//    }
//    // Call Spinner Departemen
//    private void spinner_namauser(){
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item,departemen);
//        spin_epmail.setAdapter(arrayAdapter);
//        spin_epmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spin_epmail.setSelection(position);
//                //edt_epname.setText(spin_epmail.getSelectedItem().toString().trim());
//                String selectepmail = String.valueOf(spin_epmail.getSelectedItem());
//                edt_epname.setText(selectepmail);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }


/*---Upload Hasil Update User*/
    private void UpdateUser(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        //Toast.makeText(CheckInActivity.this, "Presensi Berhasil", Toast.LENGTH_SHORT).show();
        final String epname = this.edt_epname.getText().toString().trim();
        final String epwd = this.edt_epwd.getText().toString().trim();
        final String email = getEmailUser;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                //Toast.makeText(EditProfileActivity.this, "Update Berhasil", Toast.LENGTH_LONG).show();
                                Up_FProfile();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            new KAlertDialog(EditProfileActivity.this,KAlertDialog.ERROR_TYPE)
                                    .setTitleText("Presensi Gagal " + e.toString())
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "Edit Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                        //progressBarloading.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Email", email);
                params.put("Nama", epname);
                params.put("Password", epwd);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //Permissions Akses Camera
    private boolean checkPermissions(){
        int result;
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String p : permissions){
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED){
                listPermissionNeeded.add(p);
            }
        }
        if (!listPermissionNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),100);
            return false;
        }
        return true;
    }
    File capturedFile;
    Uri outPutfileUri;

    //Setting Akses Button Pilih Gambar
    private void selectImage(){
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());

            if (hasPerm ==  PackageManager.PERMISSION_GRANTED)
            {
                if (!edt_epname.getText().toString().equals(""))
                {
                    final CharSequence[] options = {"Foto Kamera (Posisi Landscape)","Gallery (Posisi Landscape)","Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setTitle("Pilih Gambar");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Foto Kamera (Posisi Landscape)")){
                                dialog.dismiss();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                capturedFile = new File(Environment.getExternalStorageDirectory(),"FotoKamera.jpg");

                                outPutfileUri = FileProvider.getUriForFile(EditProfileActivity.this,
                                        "com.example.mypresence",
                                        capturedFile);

                                intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);

                                startActivityForResult(intent, PICK_IMAGE_CAMERA);


                            }else if (options[item].equals("Gallery (Posisi Landscape)"))
                            {
                                dialog.dismiss();
                                Intent intent = new Intent(EditProfileActivity.this, AlbumSelectActivity.class);
                                intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT,1);
                                startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                            } else if (options[item].equals("Cancel"))
                            {
                                dialog.dismiss();
                            }
                        }
                    });

                    builder.show();
                }else {
                    Toast.makeText(getApplicationContext(), "Nama tidak Ada", Toast.LENGTH_LONG).show();
                }
            }else
                Toast.makeText(this, "Camera Permission Error", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Camera Permission Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA &&  resultCode == RESULT_OK){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri);
                Bitmap scaledBitmap = new ImageZipper(EditProfileActivity.this).compressToBitmap(capturedFile);
                if (scaledBitmap != null)
                {
                    new detectAsync().execute(scaledBitmap);
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == PICK_IMAGE_GALLERY && data != null && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                file = new File(getRealPathFromURI(selectedImage));
                Bitmap scaledBitmap =  new ImageZipper(EditProfileActivity.this).compressToBitmap(file);
                if (scaledBitmap != null)
                {
                    new detectAsync().execute(scaledBitmap);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            fileArrayList.clear();

            for (int i = 0; i < images.size(); i++){

                Uri selectedImage = Uri.fromFile(new File(images.get(i).path));

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    Bitmap scaledBitmap = new ImageZipper(EditProfileActivity.this).compressToBitmap(new File(images.get(i).path));

                    fileArrayList.add(new File(images.get(i).path));
                    bitmapArrayList.add(scaledBitmap);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            Toast.makeText(getApplicationContext(), bitmapArrayList.size()+"Image Selected", Toast.LENGTH_LONG).show();

            new EditProfileActivity.detectAsyncMultipleImages().execute(bitmapArrayList);
        }
    }

    private String getRealPathFromURI(Uri contentURI){
        String filePath;
        Cursor cursor = getApplicationContext().getContentResolver().query(contentURI,null,null,null,null);
        if (cursor == null){
            filePath = contentURI.getPath();
        }else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(idx);
            cursor.close();
        }
        return filePath;
    }

    //Deteksi Wajah dari Image dari Photo
    private class detectAsync extends AsyncTask<Bitmap,Void,String>{
        ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Mendeteksi Wajah...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Bitmap... bp) {
            List<VisionDetRet> results;
            results = FaceRecognizer.getInstance().detect(bp[0]);
            String msg =  null;
            if (results.size()==0)
            {
                msg = "Wajah tidak terdeteksi atau terlalu kecil. Silahkann pilih gambar lainnya";
            }else if (results.size()>1){
                msg = "Lebih dari satu Wajah terdeteksi. Silahkan pilih satu Gambar saja";
            }else {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bp[0].compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, bytes);
                FileOutputStream fo;
                try {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();

                    destination = new File(Constants.getDLibImageDirectoryPath() +"/"+ edt_epname.getText().toString()+ts+".jpg");
                    destination.createNewFile();

                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imgPath =  destination.getAbsolutePath();
            }
            return msg;
        }

        protected void onPostExecute(String result){
            if (dialog != null && dialog.isShowing()){
                dialog.dismiss();
                if (result != null){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProfileActivity.this);
                    builder1.setMessage(result);
                    builder1.setCancelable(true);
                    AlertDialog alert11 =  builder1.create();
                    alert11.show();
                    imgPath =  null;
                    //peasan image sebelum terpilih
//                    tv_Namevlogin.setText("");
                }else {
                    Toast.makeText(getApplicationContext(), "Foto Berhasil Diganti", Toast.LENGTH_LONG).show();
                    img_epp.setImageBitmap(bitmap);
                }
            }
        }
    }

    //Deteksi Wajah dari Image Gallery
    private class detectAsyncMultipleImages extends AsyncTask<ArrayList<Bitmap>, Void, String>{
        ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);

        Handler handler = new Handler();

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Mendeteksi Wajah...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ArrayList<Bitmap>... bp) {

            List<VisionDetRet> results;
            String msg = null;
            ArrayList<Bitmap> bitmapArr = bp[0];

            for (int i=0; i<bitmapArr.size();i++)
            {
                results = FaceRecognizer.getInstance().detect(bitmapArr.get(i));

                if (results.size()==0)
                {
                    msg = "Wajah tidak terdeteksi atau terlalu kecil. Silahkann pilih gambar lainnya";
                    Log.e("Face Detector",msg);
                }
                else if (results.size() > 1){
                    msg = "Lebih dari satu Wajah terdeteksi. Silahkan pilih satu Gambar saja";
                    Log.e("Face Detector",msg);
                }else {
                    Long tsLong = System.currentTimeMillis()/1000;
                    String ts = tsLong.toString();

                    String targetPath = Constants.getDLibImageDirectoryPath()+"/"+edt_epname.getText().toString()+ts+".jpg";
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                    bitmapArr.get(i).compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, bytes);
                    FileOutputStream fo;

                    try {
                        destination = new File(targetPath);
                        destination.createNewFile();

                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    imgPath = destination.getAbsolutePath();
                }
            }

            return msg;
        }

        protected void onPostExecute(String result){
            if (dialog != null && dialog.isShowing()){
                dialog.dismiss();

                if (result != null)
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProfileActivity.this);
                    builder1.setMessage(result);
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {
                    Toast.makeText(getApplicationContext(), "Foto Berhasil Diganti", Toast.LENGTH_LONG).show();
                    img_epp.setImageBitmap(bitmap);
                }
            }

            //finish();
        }
    }

    /*--Upload Foto Profile*/
    private void Up_FProfile(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final String email = getEmailUser;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_IMAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                String s = response.trim();
                if (!s.equalsIgnoreCase("Loi")) {
                    Toast.makeText(EditProfileActivity.this, "Edit Profile Berhasil", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to Upload", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Edit Berhasil \nFoto Tidak Diganti", Toast.LENGTH_LONG).show();
                startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                String image = getStringImage(bitmap);
                Map<String,String> params = new HashMap<String, String>();
                params.put("IMG",image);
                params.put("Nama",email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
        requestQueue.add(stringRequest);
    }

    private String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
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

    /*-- Aksi Tombol Back --*/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}