package com.example.mypresence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class VLoginActivity extends AppCompatActivity {
    private Button btn_changephoto, btntrainfaces, btn_saveVlogin;
    private EditText edt_pwvlogin;
    private ImageView imgProfileVlogin,imgGProfileVlogin;
    private TextView tv_Namevlogin,tv_Emailvlogin;
    int BITMAP_QUALITY = 100;
    File file;
    String TAG = "AddPerson";
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

    SessionManager sessionManager;
    private static String URL_READ = "http://192.168.1.220:808/mypresence/read_detail.php";
    private static String URL_EDIT = "http://192.168.1.220:808/mypresence/edit_detail.php";
    private static final int REQUEST_CODE = 101;
    String getEmailUser,IMEINumber,url;

    private static final String URL_IMAGE="http://192.168.1.220:808/mypresence/upload_foto_profile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_login);
        checkPermissions();
        getIMEPhone();

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        getEmailUser = user.get(sessionManager.EMAIL);
        deleteCache();

        btn_changephoto = findViewById(R.id.btnchangephoto);
        btn_saveVlogin = findViewById(R.id.btnsave_vlogin);
        tv_Namevlogin = findViewById(R.id.tvname_vlogin);
        tv_Emailvlogin = findViewById(R.id.tvemail_vlogin);
        edt_pwvlogin = findViewById(R.id.edt_vpassword);
        btntrainfaces = findViewById(R.id.btn_trainfaces);
        imgProfileVlogin = findViewById(R.id.imgvppfoto);
        //imgGProfileVlogin = findViewById(R.id.imgvppgallery);
        btn_changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btn_saveVlogin.setVisibility(View.GONE);
        btn_saveVlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveChangeProfile();
            }
        });

        btntrainfaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new initRecAsync().execute();
            }
        });

        //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);

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
                FileUtils.copyFileFromRawToOthers(VLoginActivity.this, R.raw.shape_predictor_5_face_landmarks, Constants.getFaceShapeModelPath());
            }
            if (!new File(Constants.getFaceDescriptorModelPath()).exists()){
                FileUtils.copyFileFromRawToOthers(VLoginActivity.this, R.raw.dlib_face_recognition_resnet_model_v1, Constants.getFaceDescriptorModelPath());
            }
        }
    }

    /*--Mengakses IMEI--*/
    private void getIMEPhone(){

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(VLoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(VLoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_CODE);
            return;
        }
        IMEINumber = telephonyManager.getDeviceId();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(VLoginActivity.this, "Akses Disetujui", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(VLoginActivity.this, "Akses Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
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
                                    //nama object string yang menghubungkan ke file read_detail.php
                                    tv_Namevlogin.setText(strName);
                                    tv_Emailvlogin.setText(strEmail);
                                    url  = "http://192.168.1.220:808/mypresence/photo_profile/"+strEmail+".jpg";
                                    ImageRetriveWithPicasso();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(VLoginActivity.this, "Error Reading Detail" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(VLoginActivity.this, "Error Reading Detail" + error.toString(), Toast.LENGTH_SHORT).show();
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
                .into(imgProfileVlogin);
    }
    /*------Simpan Hasil Edit---------*/
    private void SaveChangeProfile() {
        final String changePwd = this.edt_pwvlogin.getText().toString().trim();
        final String imei = IMEINumber.trim();
        final String email = getEmailUser;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Up_FProfile();
                                //Toast.makeText(VLoginActivity.this, "Edit Password Berhasil", Toast.LENGTH_SHORT).show();
                                //sessionManager.createSession(nikprofile, namaprofile, jabatanprofile, emailprofile, notelprofile);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(VLoginActivity.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(VLoginActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("changePassword", changePwd);
                params.put("imeiPhone", imei);
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

    //Button Memilih Gambar Profil
//    private View.OnClickListener vOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()){
//                case R.id.btnchangephoto:
//                    selectImage();
//                    break;
//            }
//        }
//    };
    File capturedFile;
    Uri outPutfileUri;

    //Setting Akses Button Pilih Gambar
    private void selectImage(){
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());

            if (hasPerm ==  PackageManager.PERMISSION_GRANTED)
            {
                if (!tv_Namevlogin.getText().toString().equals(""))
                {
                    final CharSequence[] options = {"Foto Kamera (Posisi Landscape)","Gallery (Posisi Landscape)","Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(VLoginActivity.this);
                    builder.setTitle("Pilih Gambar");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Foto Kamera (Posisi Landscape)")){
                                dialog.dismiss();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                capturedFile = new File(Environment.getExternalStorageDirectory(),"FotoKamera.jpg");

                                outPutfileUri = FileProvider.getUriForFile(VLoginActivity.this,
                                        "com.example.mypresence",
                                        capturedFile);

                                intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);

                                startActivityForResult(intent, PICK_IMAGE_CAMERA);


                            }else if (options[item].equals("Gallery (Posisi Landscape)"))
                            {
                                dialog.dismiss();
                                Intent intent = new Intent(VLoginActivity.this, AlbumSelectActivity.class);
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
                Bitmap scaledBitmap = new ImageZipper(VLoginActivity.this).compressToBitmap(capturedFile);
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
                Bitmap scaledBitmap =  new ImageZipper(VLoginActivity.this).compressToBitmap(file);
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
                    Bitmap scaledBitmap = new ImageZipper(VLoginActivity.this).compressToBitmap(new File(images.get(i).path));
                    fileArrayList.add(new File(images.get(i).path));
                    bitmapArrayList.add(scaledBitmap);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            Toast.makeText(getApplicationContext(), bitmapArrayList.size()+"Image Selected", Toast.LENGTH_LONG).show();

            new detectAsyncMultipleImages().execute(bitmapArrayList);
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
        ProgressDialog dialog = new ProgressDialog(VLoginActivity.this);

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

                    destination = new File(Constants.getDLibImageDirectoryPath() +"/"+ tv_Namevlogin.getText().toString()+ts+".jpg");
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
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VLoginActivity.this);
                    builder1.setMessage(result);
                    builder1.setCancelable(true);
                    AlertDialog alert11 =  builder1.create();
                    alert11.show();
                    imgPath =  null;
                }else {
                    Toast.makeText(getApplicationContext(), "Foto berhasil ditambahkan", Toast.LENGTH_LONG).show();
                    imgProfileVlogin.setImageBitmap(bitmap);
                }
            }
        }
    }

    //Deteksi Wajah dari Image Gallery
    private class detectAsyncMultipleImages extends AsyncTask<ArrayList<Bitmap>, Void, String>{
        ProgressDialog dialog = new ProgressDialog(VLoginActivity.this);

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

                    String targetPath = Constants.getDLibImageDirectoryPath()+"/"+tv_Namevlogin.getText().toString()+ts+".jpg";
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
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VLoginActivity.this);
                    builder1.setMessage(result);
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {
                    Toast.makeText(getApplicationContext(), "Success Menambahkan Semua Foto", Toast.LENGTH_LONG).show();
                    imgProfileVlogin.setImageBitmap(bitmap);
                }
            }

            //finish();
        }
    }

    //Method Training Faces
    private class initRecAsync extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog = new ProgressDialog(VLoginActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Initializing...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... args) {
            File folder = new File(Constants.getDLibDirectoryPath());
            boolean success = true;
            if (!folder.exists()){
                success = folder.mkdirs();

            }

            if (success){
                File image_folder = new File(Constants.getDLibImageDirectoryPath());
                image_folder.mkdirs();
                if (!new File(Constants.getFaceShapeModelPath()).exists())
                {
                    FileUtils.copyFileFromRawToOthers(VLoginActivity.this, R.raw.dlib_face_recognition_resnet_model_v1, Constants.getFaceDescriptorModelPath());
                }
            }

            final long startTime = System.currentTimeMillis();
            changeProgressDialogMessage(dialog, "Mendaftarkan Wajah...");
            FaceRecognizer.getInstance().train();

            final long endTime = System.currentTimeMillis();
            Log.d("TimeCost", "Time Cost: " + (endTime - startTime) / 1000f + "sec");
            handler.post(new Runnable(){
                @Override
                public void run(){
                    Toast.makeText(getApplicationContext(), "Time Cost: "+ (endTime - startTime) / 1000f + "sec", Toast.LENGTH_LONG).show();
                    btn_saveVlogin.setVisibility(View.VISIBLE);
                }
            });
            return null;
        }

        protected void onPostExecute(Void result){
            if (dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    private void changeProgressDialogMessage(final ProgressDialog pd, final String msg){
      Runnable changeMessage = new Runnable() {
          @Override
          public void run() {
              pd.setMessage(msg);
          }
      };
      runOnUiThread(changeMessage);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        FaceRecognizer.getInstance().release();
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
                    Toast.makeText(VLoginActivity.this, "Edit Profile Sukses", Toast.LENGTH_SHORT).show();
                    finish();
                    deleteCache();
                    startActivity(new Intent(VLoginActivity.this,LoginActivity.class));
                } else {
                    Toast.makeText(VLoginActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(VLoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(VLoginActivity.this,LoginActivity.class));
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

        RequestQueue requestQueue = Volley.newRequestQueue(VLoginActivity.this);
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
}