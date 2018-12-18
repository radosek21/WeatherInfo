package net.vanhara.radek.weatherinfo;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Button goButton;
    Marker myLocation;
    public String myLocationFile = "myLocationFile.dat";
    public Context myContext;
    LatLng myPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myContext = this;
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString(MainActivity.EXTRA_MESSAGE)!= null)
        {
            String []locations = bundle.getString(MainActivity.EXTRA_MESSAGE).split(",");
            myPosition = new LatLng(Double.parseDouble(locations[0]), Double.parseDouble(locations[1]));
        }
        else
        {
            myPosition = new LatLng(49.33, 17.58);
        }
        setContentView(R.layout.activity_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        goButton = findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LocationActivity", myLocation.getPosition().toString());

                try {
                    FileOutputStream fos = myContext.openFileOutput(myLocationFile, Context.MODE_PRIVATE);
                    fos.write(myLocation.getPosition().toString().getBytes());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        myLocation = mMap.addMarker(new MarkerOptions().position(myPosition).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                myLocation.setPosition(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }
}
