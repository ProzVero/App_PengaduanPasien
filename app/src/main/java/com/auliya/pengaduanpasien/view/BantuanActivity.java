package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.auliya.pengaduanpasien.R;

public class BantuanActivity extends AppCompatActivity {

    private ImageView btn_kembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan);
        init();
    }
    public void init(){
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}