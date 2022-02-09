package com.auliya.pengaduanpasien.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.auliya.pengaduanpasien.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;


public class InformasiActivity extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    private ImageView btn_kembali;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;

    public InformasiActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_informasi);
        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        setInit();
    }

    private void setInit() {
        btn_kembali = findViewById(R.id.btn_kembali);

        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        InformasiActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationPlugin(style);
                hoveringMarker = new ImageView(getApplication());
                hoveringMarker.setImageResource(R.drawable.ic_marker);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
                );

                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings( {"MissingPermission"} )
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int res = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (res != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;

    @SuppressLint( "MissingSuperCall" )
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "User Lokasi", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, "User Lokasi tidak ditemukan", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressLint( "MissingPermission" )
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.NONE);
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
}