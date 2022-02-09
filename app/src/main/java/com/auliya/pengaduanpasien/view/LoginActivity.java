package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.view.fragment.FragmentLogin;

public class LoginActivity extends AppCompatActivity {
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frm_login, new FragmentLogin()).commit();
    }
}