package com.auliya.pengaduanpasien.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.auliya.pengaduanpasien.model.UserModel;
import com.auliya.pengaduanpasien.view.fragment.FragmentLogin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UbahProfilActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private Button btn_update;
    private TextInputLayout l_nama, l_email, l_alamat, l_nohp, l_nik, l_nojaminan, l_tgl_lahir, l_norekam;
    private EditText e_nama, e_email, e_alamat, e_nohp, e_nik, e_nojaminan, e_tgl_lahir, e_norekam;
    private String nama, email, alamat, nohp, kelamin, nik, nojaminan, tgl_lahir;
    private TextView txt_kelamin;
    private Spinner sp_kelamin;
    private ProgressDialog dialog;
    private StringRequest updateUser;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public String id_regis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);
        init();
        setButton();
    }

    private void setButton() {
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
        btn_update.setOnClickListener(v -> {
            if (validasi()) {
                updateProfil();
            }
        });
        sp_kelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt_kelamin.setText(sp_kelamin.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_update = findViewById(R.id.btn_update);
        l_nama = findViewById(R.id.l_nama);
        l_email = findViewById(R.id.l_email);
        l_alamat = findViewById(R.id.l_alamat);
        l_nohp = findViewById(R.id.l_no_telp);
        e_nama = findViewById(R.id.e_nama);
        e_email = findViewById(R.id.e_email);
        e_alamat = findViewById(R.id.e_alamat);
        e_nohp = findViewById(R.id.e_no_telp);
        txt_kelamin = findViewById(R.id.getkelamin);
        sp_kelamin = findViewById(R.id.kelamin);
        l_nik = findViewById(R.id.l_nik);
        l_nojaminan = findViewById(R.id.l_nojaminan);
        e_nik = findViewById(R.id.e_nik);
        e_nojaminan = findViewById(R.id.e_nojaminan);
        l_tgl_lahir = findViewById(R.id.l_tgl_lahir);
        e_tgl_lahir = findViewById(R.id.e_tgl_lahir);
        l_norekam = findViewById(R.id.l_norekam);
        e_norekam = findViewById(R.id.e_norekam);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        e_nama.setText(preferences.getString("nama", ""));
        e_email.setText(preferences.getString("email", ""));
        id_regis = String.valueOf(preferences.getInt("id_regis", 0));
        e_norekam.setText(preferences.getString("no_rekam", ""));
        cekvalidasi();
    }

    private void updateProfil() {
        dialog.setMessage("Menyimpan...");
        dialog.show();
        updateUser = new StringRequest(Request.Method.POST, URLServer.UPDATE_PROFILE, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    editor = preferences.edit();
                    editor.putString("no_hp", data.getString("no_hp"));
                    editor.putString("kelamin", data.getString("kelamin"));
                    editor.putString("alamat", data.getString("alamat"));
                    editor.putString("nik", data.getString("nik"));
                    editor.putString("no_jaminan", data.getString("no_jaminan"));
                    editor.putString("tgl_lahir", data.getString("tgl_lahir"));
                    editor.apply();
                    showDialog();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.toString());
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            showError(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id_regis", id_regis);
                map.put("nama", nama);
                map.put("email", email);
                map.put("no_hp", nohp);
                map.put("kelamin", kelamin);
                map.put("alamat", alamat);
                map.put("nik", nik);
                map.put("no_jaminan", nojaminan);
                map.put("tgl_lahir", tgl_lahir);
                return map;
            }
        };
        updateUser.setRetryPolicy(new RetryPolicy() {
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
        koneksi.add(updateUser);
    }

    private void showDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses!")
                .show();
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    public void cekvalidasi() {
        getinputtext();

        e_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nama.isEmpty()) {
                    l_nama.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.isEmpty()) {
                    l_email.setErrorEnabled(false);
                } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    l_email.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        e_nohp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nohp.isEmpty()) {
                    l_nohp.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        e_alamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (alamat.isEmpty()) {
                    l_alamat.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_nik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nik.isEmpty()) {
                    l_nik.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_nojaminan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nojaminan.isEmpty()) {
                    l_nojaminan.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_tgl_lahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tgl_lahir.isEmpty()) {
                    l_tgl_lahir.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validasi() {
        getinputtext();
        if (nama.isEmpty()) {
            l_nama.setErrorEnabled(true);
            l_nama.setError("Kolom nama tidak boleh kosong!");
            return false;
        }
        if (email.isEmpty()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Kolom email tidak boleh kosong!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Format email salah!. Contoh: gunakan @example.com");
            return false;
        }
        if (nohp.isEmpty()) {
            l_nohp.setErrorEnabled(true);
            l_nohp.setError("Kolom no. telp tidak boleh kosong!");
            return false;
        }
        if (alamat.isEmpty()) {
            l_alamat.setErrorEnabled(true);
            l_alamat.setError("Kolom alamat tidak boleh kosong!");
            return false;
        }
        if (nik.isEmpty()) {
            l_nik.setErrorEnabled(true);
            l_nik.setError("Kolom nik tidak boleh kosong!");
            return false;
        }
        if (nojaminan.isEmpty()) {
            l_nojaminan.setErrorEnabled(true);
            l_nojaminan.setError("Kolom no. jaminan tidak boleh kosong!");
            return false;
        }
        if (tgl_lahir.isEmpty()) {
            l_tgl_lahir.setErrorEnabled(true);
            l_tgl_lahir.setError("Kolom tanggal lahir tidak boleh kosong!");
            return false;
        }

        return true;
    }

    public void getinputtext() {
        nama = e_nama.getText().toString().trim();
        email = e_email.getText().toString().trim();
        nohp = e_nohp.getText().toString().trim();
        alamat = e_alamat.getText().toString().trim();
        kelamin = txt_kelamin.getText().toString().trim();
        nik = e_nik.getText().toString().trim();
        nojaminan = e_nojaminan.getText().toString().trim();
        tgl_lahir = e_tgl_lahir.getText().toString().trim();
    }
}