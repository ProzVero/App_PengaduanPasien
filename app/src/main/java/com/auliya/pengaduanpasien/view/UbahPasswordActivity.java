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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UbahPasswordActivity extends AppCompatActivity {
    private ImageView btn_kembali;
    private TextInputLayout l_username, l_password, l_konfir_pass;
    private EditText e_username, e_password, e_konfir_pass;
    private Button btn_update;
    public String username, password, konfir_pass;
    public int id_regis;

    private ProgressDialog dialog;
    private StringRequest updatePassword;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_update = findViewById(R.id.btn_update);
        l_konfir_pass = findViewById(R.id.l_konfir_pass);
        l_password = findViewById(R.id.l_password);
        l_username = findViewById(R.id.l_username);
        e_konfir_pass = findViewById(R.id.e_konfir_pass);
        e_password = findViewById(R.id.e_password);
        e_username = findViewById(R.id.e_username);

        id_regis = preferences.getInt("id_regis", 0);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        btn_update.setOnClickListener(v -> {
            if (validasi()) {
                updatepassword();
            }
        });

        btn_kembali.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
        cekvalidasi();
    }

    private void updatepassword() {
        dialog.setMessage("Loading...");
        dialog.show();
        updatePassword = new StringRequest(Request.Method.POST, URLServer.UPDATE_PASSWORD, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(preferences.getString("token", ""));
                    editor.remove(String.valueOf(preferences.getInt("id_regis", 0)));
                    editor.remove(preferences.getString("nama", ""));
                    editor.clear();
                    editor.apply();

                    showDialog();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
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
            showError(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id_regis", String.valueOf(id_regis));
                map.put("username", username);
                map.put("password", password);
                return map;
            }
        };
        updatePassword.setRetryPolicy(new RetryPolicy() {
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
        koneksi.add(updatePassword);
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

    public void getinputtext() {
        username = e_username.getText().toString().trim();
        password = e_password.getText().toString().trim();
        konfir_pass = e_konfir_pass.getText().toString().trim();
    }

    public void cekvalidasi() {
        getinputtext();


        e_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (username.isEmpty()) {
                    l_username.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.isEmpty()) {
                    l_password.setErrorEnabled(false);
                } else if (password.length() > 7) {
                    l_password.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_konfir_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (konfir_pass.isEmpty()) {
                    l_konfir_pass.setErrorEnabled(false);
                } else if (konfir_pass.length() > 7) {
                    l_konfir_pass.setErrorEnabled(false);
                } else if (konfir_pass.matches(password)) {
                    l_konfir_pass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validasi() {
        getinputtext();

        if (username.isEmpty()) {
            l_username.setErrorEnabled(true);
            l_username.setError("Kolom username tidak boleh kosong!");
            return false;
        }
        if (password.isEmpty()) {
            l_password.setErrorEnabled(true);
            l_password.setError("Kolom password tidak boleh kosong!");
            return false;
        } else if (password.length() < 6) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kurang dari 6 karakter!");
            return false;
        }
        if (konfir_pass.isEmpty()) {
            l_konfir_pass.setErrorEnabled(true);
            l_konfir_pass.setError("Kolom konfirmasi password tidak boleh kosong!");
            return false;
        } else if (konfir_pass.length() < 6) {
            l_konfir_pass.setErrorEnabled(true);
            l_konfir_pass.setError("Konfirmasi password tidak boleh kurang dari 6 karakter!");
            return false;
        } else if (!konfir_pass.matches(password)) {
            l_konfir_pass.setErrorEnabled(true);
            l_konfir_pass.setError("Konfirmasi password tidak sama dengan password!");
            return false;
        }
        return true;
    }
}