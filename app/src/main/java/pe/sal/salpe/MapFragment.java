package pe.sal.salpe;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(-12.150498774159367,
                -76.99333564785161));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

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
                                LatLng place = new LatLng(tmpObj.getDouble("latitude"),
                                        tmpObj.getDouble("longitude"));

                                mMap.addMarker(new MarkerOptions()
                                        .position(place)
                                        .title(tmpObj.getString("name")));
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
