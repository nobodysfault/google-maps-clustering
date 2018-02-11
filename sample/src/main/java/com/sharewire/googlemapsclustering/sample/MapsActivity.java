package com.sharewire.googlemapsclustering.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import net.sharewire.googlemapsclustering.Cluster;
import net.sharewire.googlemapsclustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final LatLngBounds NETHERLANDS = new LatLngBounds(
            new LatLng(50.77083, 3.57361), new LatLng(53.35917, 7.10833));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (savedInstanceState == null) {
            setupMapFragment();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(NETHERLANDS, 0));
            }
        });

        final ClusterManager<SampleClusterItem> clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setClusterClickCallback(new ClusterManager.ClusterClickCallback<SampleClusterItem>() {
            @Override
            public boolean onClick(@NonNull Cluster<SampleClusterItem> cluster) {
                Log.d(TAG, "onClusterClick");
                return false;
            }
        });
        clusterManager.setClusterItemClickCallback(new ClusterManager.ClusterItemClickCallback<SampleClusterItem>() {
            @Override
            public boolean onClick(@NonNull SampleClusterItem clusterItem) {
                Log.d(TAG, "onClusterItemClick");
                return false;
            }
        });
        googleMap.setOnCameraIdleListener(clusterManager);

        List<SampleClusterItem> clusterItems = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            clusterItems.add(new SampleClusterItem(
                    RandomLocationGenerator.generate(NETHERLANDS)));
        }
        clusterManager.setItems(clusterItems);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 19; i++) {
            executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Thread.sleep(500); // To emulate network loading latency
                    List<SampleClusterItem> clusterItems = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        clusterItems.add(new SampleClusterItem(
                                RandomLocationGenerator.generate(NETHERLANDS)));
                    }
                    clusterManager.addItems(clusterItems);
                    return null;
                }
            });
        }
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);
    }
}
