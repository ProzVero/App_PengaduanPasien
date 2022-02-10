package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailPengaduan extends AppCompatActivity {

    private ImageView btn_kembali, imgAduan;
    private String id_pengaduan;
    private TextView txt_saran, txt_email, txt_nama, txt_alamat,
            txt_tanggal, txt_balasan, txt_tanggal2;
    private SharedPreferences preferences;
    private StringRequest getIdPengaduan;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengaduan);
        context = this;
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_alamat = findViewById(R.id.alamat);
        txt_nama = findViewById(R.id.nama);
        txt_email = findViewById(R.id.email);
        txt_saran = findViewById(R.id.saran);
        txt_tanggal = findViewById(R.id.tanggal);
        txt_balasan = findViewById(R.id.jawaban);
        txt_tanggal2 = findViewById(R.id.tanggal2);
        imgAduan = findViewById(R.id.img_aduan);

        id_pengaduan = getIntent().getStringExtra("id_pengaduan");

        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    public void setGetIdPengaduan() {
        getIdPengaduan = new StringRequest(Request.Method.GET, URLServer.GETPENGADUANID + id_pengaduan, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    txt_nama.setText(data.getString("nama"));
                    txt_email.setText(data.getString("email"));
                    txt_alamat.setText(data.getString("alamat"));
                    txt_saran.setText(data.getString("saran"));
                    txt_tanggal.setText(data.getString("created_at"));
                    txt_balasan.setText(data.getString("jawaban_saran"));
                    txt_tanggal2.setText(data.getString("created_at"));
                    Glide.with(context)
                            .load(URLServer.URL_IMAGE_ADUAN + id_pengaduan+".png")
                            .fitCenter()
                            .placeholder(ContextCompat.getDrawable(context,R.drawable.ic_image_24))
                            .apply(RequestOptions.fitCenterTransform())
                            .into(imgAduan);
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.toString());
            }
        }, error -> {
            showError(error.toString());
        });
        getIdPengaduan.setRetryPolicy(new RetryPolicy() {
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
                    showError("Koneksi gagal");
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getIdPengaduan);
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetIdPengaduan();
    }
}