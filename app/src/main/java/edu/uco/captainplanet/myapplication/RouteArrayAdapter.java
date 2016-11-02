package edu.uco.captainplanet.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RouteArrayAdapter extends ArrayAdapter<Route> {
    /*
    private final Context context;
    private final String[] values;

    public RouteArrayAdapter(Context context, String[] values) {
        super(context, R.layout.activity_routes, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_routes, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(values[position]);

        return rowView;
    }
    */

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView home;
    }

    public RouteArrayAdapter(Context context, ArrayList<Route> routes) {
        super(context, R.layout.item_route, routes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Route route = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_route, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
            //viewHolder.home = (TextView) convertView.findViewById(R.id.tvHometown);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(route.getName());
        //viewHolder.home.setText(route.getId());
        // Return the completed view to render on screen
        return convertView;
    }
}