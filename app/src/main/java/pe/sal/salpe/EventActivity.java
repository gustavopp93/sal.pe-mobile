package pe.sal.salpe;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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




        Event event = (Event) getIntent().getExtras().get("EVENT");

        ImageView imgEvent = (ImageView)findViewById(R.id.imageEvent);

        Picasso.with(EventActivity.this)
                .load(event.getAvatar())
                .into(imgEvent);

        TextView eventName = (TextView)findViewById(R.id.eventName);
        eventName.setText(event.getName());


        actionBarObj.setTitle(event.getName());
        actionBarObj.setDisplayHomeAsUpEnabled(true);
        actionBarObj.setHomeButtonEnabled(true);

        TextView eventDescription = (TextView)findViewById(R.id.eventDescription);
        eventDescription.setText(event.getDescription());

        TextView eventExtraData = (TextView)findViewById(R.id.eventExtraData);
        eventExtraData.setText(event.getExtraData());

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
