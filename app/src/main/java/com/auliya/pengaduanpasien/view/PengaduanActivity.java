package com.auliya.pengaduanpasien.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.model.PengaduanModel;
import com.auliya.pengaduanpasien.presentasi.PengaduanAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.auliya.pengaduanpasien.fcm.Notifikasi.CHANNEL_1_ID;


public class PengaduanActivity extends AppCompatActivity {

    public ImageView btn_kembali, img_Foto;
    private RelativeLayout rl_1;
    private Button btn_kirim,btn_addfoto;
    private TextInputLayout  l_saran;
    private EditText  e_saran;
    public String saran, judul_saran, user_id, grup_id, nama;
    private ProgressDialog dialog;
    private StringRequest kirim;
    private ArrayList<PengaduanModel> dataPengaduan;
    private SharedPreferences preferences;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data = null;
    private RecyclerView.LayoutManager layoutManager;
    private StringRequest getPengaduan;
    private PengaduanAdapter adapter;
    Uri selectedImageUri;
    public static Bitmap bitmap;
    public static Uri imgUri;
    public static int mode = 1;
    Context context;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    String currentPhotoPath;
    public static final String CAMERA_PREF = "camera_pref";
    public static final String ALLOW_KEY = "ALLOWED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaduan);
        context = this;
        init();
        onResume();
        setButton();
        setGetPengaduan();
    }



    private void setButton() {
        sw_data.setOnRefreshListener(() -> {
            setGetPengaduan();
        });
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_addfoto = findViewById(R.id.btn_addfoto);
        btn_kirim = findViewById(R.id.btn_kirim);
        l_saran = findViewById(R.id.l_saran);
        e_saran = findViewById(R.id.e_saran);
        rc_data = findViewById(R.id.rc_data);
        sw_data = findViewById(R.id.sw_data);
        img_Foto = findViewById(R.id.img_foto);
        rl_1 = findViewById(R.id.rl_1);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        user_id = String.valueOf(preferences.getInt("id_regis", 0));
        grup_id = preferences.getString("user_id", "");
        nama = preferences.getString("nama", "");

        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });

        btn_addfoto.setOnClickListener(v -> {
            checkPermission(MY_PERMISSIONS_REQUEST_CAMERA);
        });

        btn_kirim.setOnClickListener(v -> {
            if (validasi()) {
                kirimData();
            }
        });

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);
    }

    private void checkPermission(int myPermissionsRequestCamera) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (getFromPref(this, ALLOW_KEY)) {
                showSettingsAlert();
            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)

                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    showAlert();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent i=new Intent(context,UploadImageActivity.class);
        i.putExtra("aduan", "adasayangada");
        context.startActivity(i);
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(PengaduanActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(PengaduanActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(PengaduanActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(PengaduanActivity.this);
                    }
                });

        alertDialog.show();
    }

    public static void startInstalledAppDetailsActivity(final Context context) {
        if (context == null) {
            return;
        }

        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];

                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean
                                showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                        this, permission);

                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(PengaduanActivity.this, ALLOW_KEY, true);
                        }
                    }else {
                        dispatchTakePictureIntent();
                    }
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    private void kirimData() {
        dataPengaduan = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();

        kirim = new StringRequest(Request.Method.POST, URLServer.KIRIM_ADUAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    showDialog();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showError(e.toString());
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
            showError(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", user_id);
                map.put("grup_id", grup_id);
                map.put("saran", saran);
                return map;
            }
        };
        kirim.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() == null) {
                    dialog.dismiss();
                    Looper.prepare();
                    String pesan = "Koneksi gagal!";
                    showError(pesan);
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirim);
    }

    public void setGetPengaduan() {
        dataPengaduan = new ArrayList<>();
        sw_data.setRefreshing(true);
        int id = preferences.getInt("id_regis", 0);
        getPengaduan = new StringRequest(Request.Method.GET, URLServer.GETPENGADUAN + id, response -> {
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject getData = data.getJSONObject(i);
                            PengaduanModel getDataPengaduan = new PengaduanModel();
                            getDataPengaduan.setId(getData.getInt("id"));
                            getDataPengaduan.setUser_id(getData.getInt("user_id"));
                            getDataPengaduan.setNama(getData.getString("nama"));
                            getDataPengaduan.setGrup_id(getData.getInt("grup_id"));
                            getDataPengaduan.setSaran(getData.getString("saran"));
                            getDataPengaduan.setCreated_at(getData.getString("created_at"));
                            dataPengaduan.add(getDataPengaduan);
                        }
                        adapter = new PengaduanAdapter(this, dataPengaduan);
                        rc_data.setAdapter(adapter);
                    } else {
                        showError(object.getString("message"));
                    }
                } catch (JSONException e) {
                    showError(e.toString());
                }
            } else {
                showError(null);
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("respon", "err: " + error.networkResponse);
        });
        getPengaduan.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                    showError("Koneksi gagal!");

                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getPengaduan);
    }

    private void showDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses!")
                .setConfirmText("Oke")
                .setConfirmClickListener(sweetAlertDialog -> {
                    startActivity(new Intent(this, PengaduanActivity.class));
                    finish();
                    sweetAlertDialog.dismissWithAnimation();
                })
                .show();
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rl_1.setVisibility(View.GONE);
        if (mode == 1){
            if (bitmap!=null){
                img_Foto.setImageBitmap(bitmap);

                rl_1.setVisibility(View.VISIBLE);
            }
        }else if (mode == 2){
            if (imgUri!=null){
                img_Foto.setImageURI(imgUri);
                rl_1.setVisibility(View.VISIBLE);
            }
        }
    }

    public void gettextinput() {
        saran = e_saran.getText().toString().trim();
    }

    private boolean validasi() {
        gettextinput();
        if (saran.isEmpty()) {
            l_saran.setError("Isi aduan tidak boleh kosong!");
            return true;
        }
        return true;
    }
}