package com.burton.arlen.inboundbeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.findButton)Button findButton;
    @Bind(R.id.noBeacon)TextView noBeacon;
    @Bind(R.id.foundBeacon)TextView foundBeacon;
    private BeaconManager beaconManager;
    private Region region;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        findButton.setOnClickListener(this);
        foundBeacon.setVisibility(View.GONE);


        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    noBeacon.setVisibility(View.GONE);
                    foundBeacon.setVisibility(View.VISIBLE);
                }
            }
        });
        region = new Region("ranged region", UUID.fromString(Constants.BEACON_UUID), null, null);
    }


    public void beaconFinder() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback(){
            @Override
            public void onServiceReady(){
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause(){
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    public void onClick(View v){
        if (v == findButton){
            beaconFinder();
        }
    }
}
//        beaconManager = new BeaconManager(this);
//        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
//            @Override
//            public void onBeaconsDiscovered(BeaconRegion region, List<com.estimote.coresdk.recognition.packets.Beacon> list) {
//                if (!list.isEmpty()) {
//                    com.estimote.coresdk.recognition.packets.Beacon nearestBeacon = list.get(0);
//                    List<String> places = placesNearBeacon(nearestBeacon);
//                    noBeacon.setVisibility(View.GONE);
//                    foundBeacon.setVisibility(View.VISIBLE);
//                    Log.d("Airport", "Nearest places: " + places);
//                }
//            }
//        });
//        region = new BeaconRegion("ranged region", UUID.fromString(Constants.BEACON_UUID), null, null);
//
//    }
