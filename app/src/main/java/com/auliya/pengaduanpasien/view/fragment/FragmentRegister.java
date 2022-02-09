package com.auliya.pengaduanpasien.view.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentRegister extends Fragment {

    private View view;
    private ImageView btn_kembali;
    private CardView btn_register;
    private TextView txt_getToken;
    private EditText e_nama, e_username, e_email, e_password, e_konfir_pass;
    private TextInputLayout l_nama, l_username, l_email, l_password, l_konfir_pass;

    private ProgressDialog dialog;
    private StringRequest registerUser;
    public String nama, username, email, password, konf_pass, getToken;

    public FragmentRegister() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        init();
        return view;
    }

    private void init() {
        btn_kembali = view.findViewById(R.id.btn_kembali);
        btn_register = view.findViewById(R.id.btn_regis);
        l_username = view.findViewById(R.id.l_username);
        l_nama = view.findViewById(R.id.l_nama);
        l_password = view.findViewById(R.id.l_password);
        l_konfir_pass = view.findViewById(R.id.l_konfir_pass);
        l_email = view.findViewById(R.id.l_email);
        e_username = view.findViewById(R.id.e_username);
        e_nama = view.findViewById(R.id.e_nama);
        e_password = view.findViewById(R.id.e_password);
        e_konfir_pass = view.findViewById(R.id.e_konfir_pass);
        e_email = view.findViewById(R.id.e_email);
        txt_getToken = view.findViewById(R.id.getTokenRegister);

        txt_getToken.setText(FirebaseInstanceId.getInstance().getToken());

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        btn_kembali.setOnClickListener(v -> {
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            manager.beginTransaction()
                    .replace(R.id.frm_login, new FragmentLogin())
                    .commit();
        });

        btn_register.setOnClickListener(v -> {
            if (validasi()) {
                register();
            }
        });
        cekvalidasi();
    }

    private void register() {
        dialog.setMessage("Loading...");
        dialog.show();

        registerUser = new StringRequest(Request.Method.POST, URLServer.REGISTER, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    UserModel postUser = new UserModel();
                    postUser.setNama(data.getString("nama"));
                    postUser.setUsername(data.getString("username"));
                    postUser.setEmail(data.getString("email"));
                    postUser.setPassword(data.getString("password"));
                    postUser.setToken_id(data.getString("token_id"));

                    showDialog();
                    FragmentManager manager = getFragmentManager();
                    assert manager != null;
                    manager.beginTransaction().replace(R.id.frm_login, new FragmentLogin())
                            .commit();
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
                map.put("nama", nama);
                map.put("email", email);
                map.put("username", username);
                map.put("password", password);
                map.put("token_id", getToken);
                return map;
            }
        };
        registerUser.setRetryPolicy(new RetryPolicy() {
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
                    showError("Koneksi gagal");
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(registerUser);
    }

    private void showDialog() {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses")
                .show();
    }

    private void showError(String string) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    public void getinputText() {
        nama = e_nama.getText().toString().trim();
        username = e_username.getText().toString().trim();
        email = e_email.getText().toString().trim();
        password = e_password.getText().toString().trim();
        konf_pass = e_konfir_pass.getText().toString().trim();
        getToken = txt_getToken.getText().toString().trim();
    }

    public void cekvalidasi() {
        getinputText();

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
                if (konf_pass.isEmpty()) {
                    l_konfir_pass.setErrorEnabled(false);
                } else if (konf_pass.length() > 7) {
                    l_konfir_pass.setErrorEnabled(false);
                } else if (konf_pass.matches(password)) {
                    l_konfir_pass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validasi() {
        getinputText();

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

        if (konf_pass.isEmpty()) {
            l_konfir_pass.setErrorEnabled(true);
            l_konfir_pass.setError("Kolom konfirmasi password tidak boleh kosong!");
            return false;
        } else if (konf_pass.length() < 6) {
            l_konfir_pass.setErrorEnabled(true);
            l_konfir_pass.setError("Konfirmasi password tidak boleh kurang dari 6 karakter!");
            return false;
        } else if (!konf_pass.matches(password)) {
            l_konfir_pass.setErrorEnabled(true);
            l_konfir_pass.setError("Konfirmasi password tidak sama dengan password!");
            return false;
        }
        return true;
    }
}
