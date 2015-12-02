package pe.sal.salpe;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import pe.sal.salpe.model.Event;

public class EventActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBarObj = getSupportActionBar();

        event = (Event) getIntent().getExtras().get("EVENT");

        ImageView imgEvent = (ImageView)findViewById(R.id.imageEvent);

        Picasso.with(EventActivity.this)
                .load(event.getAvatar())
                .into(imgEvent);


        actionBarObj.setTitle(event.getName());
        actionBarObj.setDisplayHomeAsUpEnabled(true);
        actionBarObj.setHomeButtonEnabled(true);

        String url = "http://salpe-dev.elasticbeanstalk.com/mobile/event/";
        url += Integer.toString(event.getEventId());

        RequestQueue queue = Volley.newRequestQueue(EventActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            event.setDescription(response.getString("description"));
                            event.setExtraData(response.getString("extra_data"));
                            event.setEventType(response.getString("event_type"));
                            event.setOrganization(response.getString("organization"));
                            event.setStartDate(response.getString("start_date"));


                            TextView eventName = (TextView)findViewById(R.id.eventName);
                            eventName.setText(event.getName());


                            TextView eventDescription = (TextView)findViewById(R.id.eventDescription);
                            eventDescription.setText(event.getDescription());

                            TextView eventExtraData = (TextView)findViewById(R.id.eventExtraData);
                            eventExtraData.setText(event.getExtraData());

                            TextView eventType = (TextView)findViewById(R.id.eventType);
                            eventType.setText(event.getEventType());

                            TextView eventOrganization = (TextView)findViewById(R.id.eventOrganization);
                            eventOrganization.setText(event.getOrganization());

                            TextView eventStartDate = (TextView)findViewById(R.id.eventStartDate);
                            eventStartDate.setText(event.getStartDate());

                        } catch (Exception e) {
                            //
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
