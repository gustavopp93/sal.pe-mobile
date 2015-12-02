package pe.sal.salpe;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import pe.sal.salpe.model.Event;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    HashMap<Marker, Event> markers = new HashMap<Marker, Event>();

    private ImageView imgEventAvatar;
    private TextView txtEventName;
    private LinearLayout llaEventInfo;

    private Event currentEvent;


    private OnFragmentInteractionListener mListener;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        imgEventAvatar = (ImageView) view.findViewById(R.id.eventImage);
        txtEventName = (TextView) view.findViewById(R.id.eventInfo);
        llaEventInfo = (LinearLayout) view.findViewById(R.id.eventRedirect);

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    LocationManager mLocationManager;

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Location myLocation = getLastKnownLocation();

        if (myLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(-12.150498774159367,
                    -76.99333564785161));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentEvent = markers.get(marker);
                Picasso.with(getActivity())
                        .load(currentEvent.getAvatar())
                        .into(imgEventAvatar);
                txtEventName.setText(currentEvent.getName());

                final Activity context = getActivity();
                llaEventInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EventActivity.class);
                        intent.putExtra("EVENT", currentEvent);
                        context.startActivity(intent);
                    }
                });

                return true;
            }
        });



        setMarkers();

    }



    public void setMarkers(){
        mMap.clear();

        String url = "http://salpe-dev.elasticbeanstalk.com/mobile/events/";

        requestEvents(url);
    }

    public void setMarkers(int eventTypeId){
        mMap.clear();

        String url = "http://salpe-dev.elasticbeanstalk.com/mobile/events/?event_type=";
        url += Integer.toString(eventTypeId);

        requestEvents(url);
    }

    private void requestEvents(String url){
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for(int i=0; i < response.length(); i++){

                            try {
                                JSONObject tmpObj =  (JSONObject) response.get(i);

                                Event event = new Event();
                                event.setEventId(tmpObj.getInt("id"));
                                event.setName(tmpObj.getString("name"));
//                                event.setDescription(tmpObj.getString("description"));
//                                event.setExtraData(tmpObj.getString("extra_data"));
                                event.setAvatar(tmpObj.getString("avatar"));

                                LatLng place = new LatLng(tmpObj.getDouble("latitude"),
                                        tmpObj.getDouble("longitude"));

                                Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(place)
                                                    .title(tmpObj.getString("name")));
                                markers.put(marker, event);
                            } catch (Exception e){
                                //
                            }

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //

                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

}
