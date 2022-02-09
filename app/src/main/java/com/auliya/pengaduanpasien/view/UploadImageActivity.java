package com.auliya.pengaduanpasien.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.api.VolleyMultipartRequest;
import com.auliya.pengaduanpasien.model.UserModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UploadImageActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private VolleyMultipartRequest postPoto;
    private ProgressDialog dialog;
    private Uri imageUri;
    private final int kodeGallery = 1, kodeKamera = 0;
    private Bitmap bitmap = null;
    private ImageView image, btn_kembali;
    private TextView txt_Tile;
    private LinearLayout btn_upload, btn_gambarlain, btn_kirim, btn_ok;
    private ArrayList<UserModel> userModels;
    private boolean aduan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        setInit();
        setButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == kodeKamera && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(bitmap);
            PengaduanActivity.bitmap = bitmap;
            PengaduanActivity.mode = 1;
            Log.d("Respon", "Img Url: " + bitmap);
        } else if (requestCode == kodeGallery && resultCode == RESULT_OK && data != null) {
            try {
                imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageURI(imageUri);


                PengaduanActivity.imgUri = imageUri;
                PengaduanActivity.mode = 2;
                Log.d("Respon", "Img Url: " + bitmap);
            } catch (Exception e) {
                Log.d("Respon", "Error: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    private void uploadImage() {
        userModels = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();
        String id = String.valueOf(preferences.getInt("id_regis", 0));
        postPoto = new VolleyMultipartRequest(Request.Method.POST, URLServer.POSTGAMBARPFORILE, response -> {
            try {
                JSONObject object = new JSONObject(new String(response.data));
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    UserModel getuser = new UserModel();
                    getuser.setGambar(data.getString("gambar"));
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("gambar", data.getString("gambar"));
                    editor.apply();
                    showSuccess();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                showError(e.toString());
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            showError(error.getMessage());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id_regis", id);
                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long namaGambar = System.currentTimeMillis();
                params.put("gambar", new DataPart(namaGambar + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        postPoto.setRetryPolicy(new RetryPolicy() {
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
        queue.add(postPoto);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void setButton() {
        btn_gambarlain.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, kodeGallery);
        });

        btn_upload.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, kodeKamera);
            }
        });
        btn_kirim.setOnClickListener(v -> {
            uploadImage();
        });
        btn_kembali.setOnClickListener(v -> {
            startActivity(new Intent(this, PengaturanActivity.class));
            finish();
        });
        btn_ok.setOnClickListener(v ->{
            onBackPressed();
        });
    }

    private void setInit() {
        Intent intent = getIntent();
        String cek = intent.getStringExtra("aduan");

        if (cek!=null){
            aduan = true;
        }else {
            aduan = false;
        }


        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        image = findViewById(R.id.image);
        btn_upload = findViewById(R.id.btn_upload);
        btn_gambarlain = findViewById(R.id.btn_gambarlain);
        btn_kirim = findViewById(R.id.btn_kirim);
        btn_ok = findViewById(R.id.btn_ok);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_Tile = findViewById(R.id.txt_title);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);


        if (aduan){
            txt_Tile.setText("Tambah Foto Aduan");
            btn_kirim.setVisibility(View.GONE);
            btn_ok.setVisibility(View.VISIBLE);
        }else {
            txt_Tile.setText("Upload Gambar");
            btn_kirim.setVisibility(View.VISIBLE);
            btn_ok.setVisibility(View.GONE);
        }
    }

    private void showSuccess() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses")
                .show();
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Oops...")
                .setContentText(string)
                .show();
    }
}