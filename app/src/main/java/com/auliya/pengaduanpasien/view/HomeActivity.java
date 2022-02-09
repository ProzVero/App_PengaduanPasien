package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
import com.auliya.pengaduanpasien.model.PengaduanModel;
import com.auliya.pengaduanpasien.presentasi.PengaduanAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressLint("MissingPermission")
public class HomeActivity extends AppCompatActivity {

    private CardView btn_pengaduan, btn_informasi, btn_rm, btn_pengaturan;
    private TextView txt_nama;
    private ImageView img_user, div_img;
    private SharedPreferences preferences;
    private StringRequest getPengaduan;
    private LinearLayout notif;
    private int id;
    private String jawaban;

    private ArrayList<PengaduanModel> dataPengaduan;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        setButton();
    }

    private void setButton() {

        btn_informasi.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), InformasiActivity.class));
        });

        btn_pengaduan.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PengaduanActivity.class));
        });

        btn_pengaturan.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PengaturanActivity.class));
        });

        btn_rm.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RekamMedikActivity.class));
        });
        img_user.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Profile.class));
        });
        txt_nama.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Profile.class));
        });
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_informasi = findViewById(R.id.btn_informasi);
        btn_pengaduan = findViewById(R.id.btn_pengaduan);
        btn_rm = findViewById(R.id.btn_rm);
        btn_pengaturan = findViewById(R.id.btn_pengaturan);
        txt_nama = findViewById(R.id.txt_nama);
        img_user = findViewById(R.id.img_user);
        notif = findViewById(R.id.notif);

        String gambar = preferences.getString("gambar", "");
        if (gambar.isEmpty()) {
            img_user.setVisibility(View.GONE);
        } else {
            img_user.setVisibility(View.VISIBLE);
        }

        id = preferences.getInt("id_regis", 0);

        txt_nama.setText(preferences.getString("nama", ""));
    }

    private void getUser() {
        getPengaduan = new StringRequest(Request.Method.GET, URLServer.GETUSER + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    Glide.with(this)
                            .load(URLServer.URL_IMAGE + data.getString("gambar"))
                            .centerCrop()
                            .apply(RequestOptions.circleCropTransform())
                            .into(img_user);
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getPengaduan);
    }

    public void setGetPengaduan() {
        dataPengaduan = new ArrayList<>();
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
                            getDataPengaduan.setGrup_id(getData.getInt("grup_id"));
                            getDataPengaduan.setSaran(getData.getString("saran"));
                            getDataPengaduan.setJawaban_saran(getData.getString("jawaban_saran"));
                            getDataPengaduan.setCreated_at(getData.getString("created_at"));
                            dataPengaduan.add(getDataPengaduan);
                        }
                        jawaban = dataPengaduan.get(0).getJawaban_saran();
                        Log.d("Response", "Jawaban: " + jawaban);
                        if (jawaban.equals("null")) {
                            notif.setVisibility(View.GONE);
                        } else {
                            notif.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showError(object.getString("message"));
                    }
                } catch (JSONException e) {
                    showError(e.toString());
                }
            } else {
                showError(null);
            }
        }, error -> {
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


    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    @Override
    protected void onResume() {
        getUser();
        setGetPengaduan();
        super.onResume();
    }

}