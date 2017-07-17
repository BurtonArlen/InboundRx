package com.burton.arlen.inboundbeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.findButton)Button findButton;
    @Bind(R.id.noBeacon)TextView noBeacon;
    @Bind(R.id.foundBeacon)TextView foundBeacon;

    private static final Map<String, List<String>> PLACES_BY_BEACONS;
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("00000:00000", new ArrayList<String>() {{
            add("Thing 1");
            add("Object 2");
            add("Stuff 3");
        }});
        placesByBeacons.put("000:00", new ArrayList<String>() {{
            add("Thing 1");
            add("Object 2");
            add("Stuff 3");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private BeaconManager beaconManager;
    private BeaconRegion region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        findButton.setOnClickListener(this);
        foundBeacon.setVisibility(View.GONE);

    }
    public void beaconFinder() {
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<com.estimote.coresdk.recognition.packets.Beacon> list) {
                if (!list.isEmpty()) {
                    com.estimote.coresdk.recognition.packets.Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    noBeacon.setVisibility(View.GONE);
                    foundBeacon.setVisibility(View.VISIBLE);
                    Log.d("Airport", "Nearest places: " + places);
                }
            }
        });
        region = new BeaconRegion("ranged region", UUID.fromString(Constants.BEACON_UUID), null, null);
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

    private List<String> placesNearBeacon(com.estimote.coresdk.recognition.packets.Beacon beacon){
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)){
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }
    @Override
    public void onClick(View v){
        if (v == findButton){
            beaconFinder();
        }
    }
}
