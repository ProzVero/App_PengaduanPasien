package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.auliya.pengaduanpasien.R;

public class TentangActivity extends AppCompatActivity {
    private ImageView btn_kembali;
    private LinearLayout btn_facebook, btn_instagram, btn_whatsaap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
        init();
    }
    public void init(){
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_facebook  = findViewById(R.id.btn_facebook);
        btn_instagram = findViewById(R.id.btn_instagram);
        btn_whatsaap = findViewById(R.id.btn_whatsapp);

        btn_facebook.setOnClickListener(v -> {
            Intent fb = new Intent();
            fb.setAction(Intent.ACTION_VIEW);
            fb.addCategory(Intent.CATEGORY_BROWSABLE);
            fb.setData(Uri.parse("https://web.facebook.com/?_rdc=1&_rdr/profile.php?id=100054762002322"));
            startActivity(fb);
        });
        btn_whatsaap.setOnClickListener(v -> {
            Intent wa = new Intent();
            wa.setAction(Intent.ACTION_VIEW);
            wa.addCategory(Intent.CATEGORY_BROWSABLE);
            wa.setData(Uri.parse("https://api.whatsapp.com/send?phone=6282271067770"));
            startActivity(wa);
        });
        btn_instagram.setOnClickListener(v -> {
            Intent ig = new Intent();
            ig.setAction(Intent.ACTION_VIEW);
            ig.addCategory(Intent.CATEGORY_BROWSABLE);
            ig.setData(Uri.parse("https://www.instagram.com/auliyanurmala_?r=nametag"));
            startActivity(ig);
        });
        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}