package pe.sal.salpe.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import pe.sal.salpe.R;
import pe.sal.salpe.model.EventType;

public class EventTypeAdapter extends BaseAdapter {

    private ArrayList<EventType> data;
    private Context context;

    public EventTypeAdapter(Context context, int resource, ArrayList<EventType> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_event_type, null);
            ViewHolder holder = new ViewHolder();
            holder.tviEventTypeText = (TextView) convertView.findViewById(R.id.event_type_text);
            convertView.setTag(holder);
        }

        EventType entry = data.get(position);
        if (entry != null) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.tviEventTypeText.setText(entry.getName());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tviEventTypeText;
    }
}
