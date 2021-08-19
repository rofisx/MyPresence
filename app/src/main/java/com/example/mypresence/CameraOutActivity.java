package com.example.mypresence;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mypresence.Utils.FaceRecognizer;
import com.example.mypresence.Utils.FileUtils;
import com.google.android.cameraview.CameraView;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.VisionDetRet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class CameraOutActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "CameraInActivity";
    private static final int INPUT_SIZE = 500;
    //    private static final int[] FLASH_OPTIONS = {
//            CameraView.FLASH_OFF,
//            CameraView.FLASH_AUTO,
//            CameraView.FLASH_ON,
//    };
//
//    private static final int[] FLASH_ICONS = {
//            R.drawable.ic_flash_off,
//            R.drawable.ic_flash_auto,
//            R.drawable.ic_flash_on,
//
//    };
//
//    private static final int[] FLASH_TITLES = {
//            R.string.flash_off,
//            R.string.flash_auto,
//            R.string.flash_on,
//    };
    Handler handler = new Handler();
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    long startTimeLocally;
    //private int mCurrentFlash;
    private CameraView mCameraView;
    private FrameLayout frPict, frSyn;
    private Button btnTpict,btnSync;
    private Handler mBackgroundHandler;
    private ImageView swCam;
    private TextView tvKFoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        frPict = findViewById(R.id.fr_pict);
        frSyn = findViewById(R.id.fr_sync);
        mCameraView = findViewById(R.id.camera);
        btnTpict = findViewById(R.id.btn_tpict);
        btnSync = findViewById(R.id.btn_sync);
        swCam = findViewById(R.id.switch_camera);
        swCam.setOnClickListener(SwitchCam);
        tvKFoto = findViewById(R.id.tvKetFoto);

        frPict.setVisibility(View.GONE);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new initRecAsync().execute();

            }
        });

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowTitleEnabled(false);
//        }
        checkPermissions();

        tvKFoto = findViewById(R.id.tvKetFoto);

        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }

        if (btnTpict != null) {
            btnTpict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCameraView != null) {
                        mCameraView.takePicture();
                    }
                }
            });
        }

        // Membuat Directory Training
        File folder = new File(Constants.getDLibDirectoryPath());
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }

        if (success) {
            File image_folder = new File(Constants.getDLibImageDirectoryPath());
            image_folder.mkdirs();
            if (!new File(Constants.getFaceShapeModelPath()).exists()) {
                FileUtils.copyFileFromRawToOthers(CameraOutActivity.this, R.raw.shape_predictor_5_face_landmarks, Constants.getFaceShapeModelPath());
            }
            if (!new File(Constants.getFaceDescriptorModelPath()).exists()) {
                FileUtils.copyFileFromRawToOthers(CameraOutActivity.this, R.raw.dlib_face_recognition_resnet_model_v1, Constants.getFaceDescriptorModelPath());
            }
        }
    }

    //Method Training Faces
    private class initRecAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(CameraOutActivity.this);

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
            if (!folder.exists()) {
                success = folder.mkdirs();

            }

            if (success) {
                File image_folder = new File(Constants.getDLibImageDirectoryPath());
                image_folder.mkdirs();
                if (!new File(Constants.getFaceShapeModelPath()).exists()) {
                    FileUtils.copyFileFromRawToOthers(CameraOutActivity.this, R.raw.dlib_face_recognition_resnet_model_v1, Constants.getFaceDescriptorModelPath());
                }
            }

            final long startTime = System.currentTimeMillis();
            changeProgressDialogMessage(dialog, "Singkrosinasi File Wajah...");
            FaceRecognizer.getInstance().train();
            final long endTime = System.currentTimeMillis();
            Log.d("TimeCost", "Time Cost: " + (endTime - startTime) / 1000f + "sec");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Time Cost: " + (endTime - startTime) / 1000f + "sec", Toast.LENGTH_LONG).show();
                    frPict.setVisibility(View.VISIBLE);
                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void changeProgressDialogMessage(final ProgressDialog pd, final String msg) {
        Runnable changeMessage = new Runnable() {
            @Override
            public void run() {
                pd.setMessage(msg);
            }
        };
        runOnUiThread(changeMessage);
    }

//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        FaceRecognizer.getInstance().release();
//    }

    /*---Callback Cameraiew*/
    private CameraView.Callback mCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "Camera Terbuka");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "Camera Tertutup");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "Mengambil Gambar" + data.length);
            Bitmap bp = drawResizeBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
            new detectAsync().execute(bp);
        }
    };

    private View.OnClickListener SwitchCam = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mCameraView != null) {
                int facing = mCameraView.getFacing();
                mCameraView.setFacing(facing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
            }
        }
    };

//    Koding Percobaan
//    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.btn_tpict:
//                    if (mCameraView != null) {
//                        mCameraView.takePicture();
//                    }
//                    break;
//            }
//        }
//    };
    /*---Check Permission Camera & External Storage*/
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(p);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause called");
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        super.onDestroy();
        if (mBackgroundHandler != null) {
            mBackgroundHandler.getLooper().quitSafely();
            mBackgroundHandler = null;
            FaceRecognizer.getInstance().release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

// Coding Percobaan
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
////            case R.id.switch_flash:
////                if (mCameraView != null) {
////                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
////
////                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
////                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
////                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
////                }
////                return true;
//            case R.id.switch_camera:
//                if (mCameraView != null) {
//                    int facing = mCameraView.getFacing();
//                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
//                }
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private String getResultMessage(ArrayList<String> names) {
        String msg = "";
        if (names.isEmpty()) {
            msg = "Wajah Tidak Terdeteksi atau Orang Tidak di Kenali";
            Intent intent = new Intent(CameraOutActivity.this,CheckOutActivity.class);
            intent.putExtra("fverified","Gagal");
            startActivity(intent);
            finish();
        } else {
            for (int i = 0; i < names.size(); i++) {
                msg += names.get(i).split(Pattern.quote("."))[0];
                if (i != names.size() - 1) msg += ",";
                
            }
            msg += " Verified";
            Intent intent = new Intent(CameraOutActivity.this,CheckOutActivity.class);
            intent.putExtra("fverified","Berhasil");
            startActivity(intent);
            finish();
        }
        return msg;
    }

    /*---BItmap Resizing*/
    Bitmap drawResizeBitmap(final Bitmap src) {
        final Bitmap dst = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ARGB_8888);
        Display getOrient = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        Point point = new Point();
        getOrient.getSize(point);
        int screen_width = point.x;
        int screen_height = point.y;
        Log.d(TAG, String.format("screen size (%d,%d)", screen_width, screen_height));
        if (screen_width < screen_height) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        }

        final float minDim = Math.min(src.getWidth(), src.getHeight());
        final Matrix matrix = new Matrix();

        final float translateX = -Math.max(0, (src.getWidth() - minDim) / 2);
        final float translateY = -Math.max(0, (src.getHeight() - minDim) / 2);
        matrix.preTranslate(translateX, translateY);

        final float scaleFactor = dst.getHeight() / minDim;
        matrix.postScale(scaleFactor, scaleFactor);

        final Canvas canvas = new Canvas(dst);
        canvas.drawBitmap(src, matrix, null);
        return dst;
    }

    /*---Recognize Detect*/
    private class detectAsync extends AsyncTask<Bitmap, Void, String> {
        ProgressDialog dialog = new ProgressDialog(CameraOutActivity.this);
        Bitmap sourceBitmap;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Mendeteksi Wajah...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(Bitmap... bp) {
            List<VisionDetRet> results;
            sourceBitmap = bp[0];

            startTimeLocally = System.currentTimeMillis();
            results = FaceRecognizer.getInstance().detect(sourceBitmap);
            String msg = null;
            if (results.size() == 0) {
                msg = "Wajah tidak terdeteksi atau terlalu kecil. Tolong pilih gambar yang berbeda";
                Intent intent = new Intent(CameraOutActivity.this,CheckOutActivity.class);
                intent.putExtra("fverified","Gagal");
                startActivity(intent);
                finish();
            } else if (results.size() > 1) {
                msg = "Lebih dari satu wajah terdeteksi, Tolong pilih gambar lainnya ";
                Intent intent = new Intent(CameraOutActivity.this,CheckOutActivity.class);
                intent.putExtra("fverified","Gagal");
                startActivity(intent);
                finish();
            } else {
                return null;
            }
            return msg;
        }

        protected void onPostExecute(String result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                ;
                if (result != null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(CameraOutActivity.this);
                    builder1.setMessage(result);
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    if (sourceBitmap != null) {
                        new recognizeAsync().execute(sourceBitmap);
                    }
                }
            }
        }
    }

    private class recognizeAsync extends AsyncTask<Bitmap, Void, ArrayList<String>> {
        ProgressDialog dialog = new ProgressDialog(CameraOutActivity.this);
        Handler handler = new Handler();
        private int mScreenRotation = 0;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Recognizing...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }


        protected ArrayList<String> doInBackground(Bitmap... bp) {
            if (bp[0] != null) {
                List<VisionDetRet> results;
                results = FaceRecognizer.getInstance().recognize(bp[0]);
                final long endTime = System.currentTimeMillis();
                Log.d(TAG, "Time Cost :" + (endTime - startTimeLocally) / 1000f + " sec");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Time Cost: " + (endTime - startTimeLocally) / 1000f + " sec", Toast.LENGTH_LONG).show();
                    }
                });

                ArrayList<String> names = new ArrayList<>();
                for (VisionDetRet n : results) {
                    String getLabelStr = n.getLabel();
                    getLabelStr = getLabelStr.replaceAll("[0-9]", "");
                    names.add(getLabelStr);
                }

                HashSet<String> hashSet = new HashSet<>();
                hashSet.addAll(names);
                names.clear();
                names.addAll(hashSet);

                return names;

            } else {
                Toast.makeText(getApplicationContext(), "Bitmap is null", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        protected void onPostExecute(ArrayList<String> names) {
            if (names != null) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(CameraOutActivity.this);
                    builder1.setMessage(getResultMessage(names));
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*-- Aksi Tombol Back --*/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,CheckOutActivity.class));
        finish();
    }
}

