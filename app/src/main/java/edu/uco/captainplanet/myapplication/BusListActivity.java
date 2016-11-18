package edu.uco.captainplanet.myapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BusListActivity extends AppCompatActivity {

    private ArrayList<Bus> theBusesArray = new ArrayList<>();
    private Activity me;
    private ListView lview;
    private ListViewAdapter lviewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);

        lview = (ListView) findViewById(R.id.BusListView);

        me = this;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000);
        client.get("https://uco-edmond-bus.herokuapp.com/api/busservice/buses"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray theRoutes) {

                        setBusList(theRoutes);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }



                });
    }

    public void setBusList(JSONArray theBuses)
    {
        try {

            for(int x = 0; x < theBuses.length() ; x++)
            {
                Bus currentBus = new Bus();




                if(theBuses.getJSONObject(x).has("name"))
                {
                    currentBus.setName(theBuses.getJSONObject(x).getString("name"));
                }
                if(theBuses.getJSONObject(x).has("driver"))
                {
                    currentBus.setDriver(theBuses.getJSONObject(x).getString("driver"));
                }

                if(theBuses.getJSONObject(x).has("route"))
                {
                    currentBus.setRoute(theBuses.getJSONObject(x).getString("route"));
                }
                if(theBuses.getJSONObject(x).has("active"))
                {
                    currentBus.setActive(theBuses.getJSONObject(x).getBoolean("active"));
                }
                if(theBuses.getJSONObject(x).has("lastStop"))
                {
                    currentBus.setLastStop(theBuses.getJSONObject(x).getString("lastStop"));
                }
                if(theBuses.getJSONObject(x).has("lastLong"))
                {
                    currentBus.setLongi(theBuses.getJSONObject(x).getDouble("lastLong"));
                }
                if(theBuses.getJSONObject(x).has("lastLat")) {
                    currentBus.setLat(theBuses.getJSONObject(x).getDouble("lastLat"));
                }



                theBusesArray.add(currentBus);

                lviewAdapter = new ListViewAdapter(me, theBusesArray);
                lview.setAdapter(lviewAdapter);

            }

            //add buses to list


        } catch (JSONException ex) {

            ex.printStackTrace();

        }
    }

    private class ListViewAdapter extends BaseAdapter{
        Activity context;
        ArrayList<Bus> theBusesArray;

        public ListViewAdapter(Activity context, ArrayList<Bus> theBusesArray) {
            super();
            this.context = context;
            this.theBusesArray = theBusesArray;
        }

        @Override
        public int getCount() {
            return theBusesArray.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        private class ViewHolder {
            TextView txtViewTitle;
            TextView txtViewDescription;
        }


        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            LayoutInflater inflater =  context.getLayoutInflater();

            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.bus_row, null);
                holder = new ViewHolder();
                holder.txtViewTitle = (TextView) convertView.findViewById(R.id.BusName);
                holder.txtViewDescription = (TextView) convertView.findViewById(R.id.Active);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtViewTitle.setText(theBusesArray.get(i).getRoute());
            if(theBusesArray.get(i).isActive())
            {
                holder.txtViewDescription.setText(" : Active");
            }
            else
            {
                holder.txtViewDescription.setText(" : Not Active");
            }


            return convertView;
        }
    }
}
