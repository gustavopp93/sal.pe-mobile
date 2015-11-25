package pe.sal.salpe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pe.sal.salpe.model.EventType;
import pe.sal.salpe.view.adapter.EventTypeAdapter;


public class MapsActivity extends AppCompatActivity{

    private ListView mDrawerList;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setEventTypes();

        mapFragment = MapFragment.newInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, mapFragment).commit();

    }

    private void setEventTypes(){
        String url = "http://salpe-dev.elasticbeanstalk.com/mobile/event-types/";

        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        final ArrayList<EventType> eventTypes = new ArrayList<EventType>();

                        EventType defaultEventType = new EventType();
                        defaultEventType.set_default(true);
                        defaultEventType.setName("Todos");
                        eventTypes.add(defaultEventType);

                        for(int i=0; i < response.length(); i++){

                            try {
                                JSONObject tmpObj =  (JSONObject) response.get(i);
                                EventType eventType = new EventType();
                                eventType.setId(tmpObj.getInt("id"));
                                eventType.setName(tmpObj.getString("name"));

                                eventTypes.add(eventType);

                            } catch (Exception e){
                                //
                            }

                        }

                        EventTypeAdapter evenTypeAdapter = new EventTypeAdapter(MapsActivity.this, -1, eventTypes);
                        mDrawerList.setAdapter(evenTypeAdapter);
                        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                EventType currentEventType = eventTypes.get(i);
                                if (currentEventType.is_default()){
                                    mapFragment.setMarkers();
                                } else {
                                    mapFragment.setMarkers(currentEventType.getId());
                                }

                            }
                        });

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
