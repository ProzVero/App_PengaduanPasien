package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Profile extends AppCompatActivity {
    private ImageView btn_kembali, btn_upload;
    private TextView txt_nama, txt_email, txt_no_hp, txt_kelamin, txt_tgl, txt_alamat;
    private Button btn_edit;
    private SharedPreferences preferences;
    private int id;
    private ProgressDialog dialog;
    private StringRequest uploadGambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setinit();
        setButton();
        getUser();
    }

    @SuppressLint("SetTextI18n")
    private void setinit() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_nama = findViewById(R.id.txt_nama);
        txt_email = findViewById(R.id.txt_email);
        txt_no_hp = findViewById(R.id.txt_no_telp);
        btn_edit = findViewById(R.id.btn_edit);
        txt_kelamin = findViewById(R.id.txt_kelamin);
        btn_upload = findViewById(R.id.btn_upload);
        txt_tgl = findViewById(R.id.txt_tgl);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_nama.setText(": " + preferences.getString("nama", ""));
        txt_email.setText(": " + preferences.getString("email", ""));
        txt_no_hp.setText(": " + preferences.getString("no_hp", ""));
        txt_kelamin.setText(": " + preferences.getString("kelamin", ""));
        txt_tgl.setText(": " + preferences.getString("tgl_lahir", ""));
        txt_alamat.setText(": " + preferences.getString("alamat", ""));
        id = preferences.getInt("id_regis", 0);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
    }

    private void setButton() {
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
        btn_edit.setOnClickListener(v -> {
            startActivity(new Intent(this, UbahProfilActivity.class));
        });
        btn_upload.setOnClickListener(v -> {
            startActivity(new Intent(this, UploadImageActivity.class));
            finish();
        });
    }

    private void getUser() {
        uploadGambar = new StringRequest(Request.Method.GET, URLServer.GETUSER + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");

                    Picasso.get()
                            .load(URLServer.URL_IMAGE + data.getString("gambar"))
                            .into(btn_upload);
                    Log.d("Respon", "Url Image: " + data.getString("gambar"));
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.toString());
            }
        }, error -> {
            showError(error.toString());
        });
        uploadGambar.setRetryPolicy(new RetryPolicy() {
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
                    showError("Koneksi gagal!");
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(uploadGambar);
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }
}