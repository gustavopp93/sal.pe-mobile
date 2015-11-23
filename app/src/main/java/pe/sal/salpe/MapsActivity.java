package pe.sal.salpe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;


public class MapsActivity extends AppCompatActivity{

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = MapFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, mapFragment).commit();

    }


}
