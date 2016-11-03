package edu.uco.captainplanet.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RouteArrayAdapter extends ArrayAdapter<ListRowItem> {
    Context context;

    public RouteArrayAdapter(Context context, List<ListRowItem> busRoutes) {
        super(context, R.layout.item_route, busRoutes);
        this.context = context;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtBus;
        TextView txtRoute;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ListRowItem rowItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_route, parent, false);
            viewHolder.txtBus = (TextView) convertView.findViewById(R.id.tvBus);
            viewHolder.txtRoute = (TextView) convertView.findViewById(R.id.tvRoute);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.txtBus.setText(rowItem.getBus());
        viewHolder.txtRoute.setText(rowItem.getRoute());
        // Return the completed view to render on screen
        return convertView;
    }
}