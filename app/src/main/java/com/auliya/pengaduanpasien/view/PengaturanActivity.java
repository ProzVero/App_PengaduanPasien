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
import android.widget.LinearLayout;
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

public class PengaturanActivity extends AppCompatActivity {
    private LinearLayout btn_pengaturan, btn_bantuan, btn_tentang, btn_keluar;
    private StringRequest prosesLogout;
    private ImageView btn_kembali;
    private SharedPreferences preferences;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);
        init();
        setButton();
    }

    private void setButton() {

        btn_keluar.setOnClickListener(v -> {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setCancelButton("No", SweetAlertDialog::dismissWithAnimation)
                    .setConfirmText("Yes")
                    .setConfirmClickListener(sDialog -> {
                        logout();
                        sDialog.dismissWithAnimation();
                    }).show();
        });


        btn_pengaturan.setOnClickListener(v -> {
            startActivity(new Intent(this, UbahPasswordActivity.class));
        });

        btn_bantuan.setOnClickListener(v -> {
            startActivity(new Intent(this, BantuanActivity.class));
        });

        btn_tentang.setOnClickListener(v -> {
            startActivity(new Intent(this, TentangActivity.class));
        });
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    @SuppressLint( "SetTextI18n" )
    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_pengaturan = findViewById(R.id.btn_pengaturan);
        btn_bantuan = findViewById(R.id.btn_bantuan);
        btn_tentang = findViewById(R.id.btn_tentang);
        btn_keluar = findViewById(R.id.btn_logout);
        btn_kembali = findViewById(R.id.btn_kembali);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
    }

    private void logout() {
        dialog.setMessage("Loading...");
        dialog.show();
        prosesLogout = new StringRequest(Request.Method.GET, URLServer.LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(preferences.getString("token", ""));
                    editor.remove(String.valueOf(preferences.getInt("id_regis", 0)));
                    editor.remove(preferences.getString("nama", ""));
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                showError(e.toString());
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            showError(error.toString());
        });
        prosesLogout.setRetryPolicy(new RetryPolicy() {
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
        queue.add(prosesLogout);
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }
}