package edu.uco.captainplanet.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import edu.uco.captainplanet.myapplication.Dtos.Favorite;

public class FavoritesActivity extends AppCompatActivity {

    private List<Favorite> favorites;
    private List<Favorite> possibleFavorites;
    private ListView lstFavorites;
    private ListView lstPossibleFavorites;
    private ArrayAdapter<Favorite> favoritesAdapter;
    private ArrayAdapter<Favorite> possibleFavoritesAdapter;

    private TextView txtFavorites;
    private TextView txtPossibleFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favorites = new ArrayList<>();
        possibleFavorites = new ArrayList<>();
        lstFavorites = (ListView)findViewById(R.id.lstFavorites);
        lstPossibleFavorites = (ListView)findViewById(R.id.lstPossibleFavorites);

        txtFavorites = (TextView)findViewById(R.id.txtFavorites);
        txtPossibleFavorites = (TextView)findViewById(R.id.txtPossibleFavorites);

        favoritesAdapter = new ArrayAdapter<Favorite>(getApplicationContext(), android.R.layout.simple_list_item_1, favorites){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }};

        possibleFavoritesAdapter = new ArrayAdapter<Favorite>(getApplicationContext(), android.R.layout.simple_list_item_1, possibleFavorites){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }};

        if(getUserFavorites())
        {
            if(getPossibleFavorites())
            {

            }
        }
        else
        {
            //display error, unable to pull JSON data

        }
    }

    public boolean getUserFavorites()
    {
        String username;
        username = UserInfoApplication.getInstance().getUsername();

        if(username == null || username.isEmpty())
            return false;

         /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("https://uco-edmond-bus.herokuapp.com/api/favoriteservice/favorites/"
                        + username
                        , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // Called when response HTTP status is "200 OK"
                        try {
                            setUserFavorites(response);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                        // Called when response HTTP status is "400"
                        txtPossibleFavorites.setText("Unable to pull data from server.");
                    }
                });
        return true;
    }

    public void setUserFavorites(JSONArray response)
    {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = new JSONObject(response.getString(i));
                Favorite newFavorite = new Favorite(item.getInt("id"), item.getInt("userId"), item.getInt("favoriteId"), item.getString("type"), item.getString("name"));
                favorites.add(newFavorite);
                // if(favorites.isEmpty())
                //display to user that they have no favorites
                // else
            }
            lstFavorites.setAdapter(favoritesAdapter);
            if(favorites.isEmpty())
                txtFavorites.setText("You currently have no favorites saved.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //get possible favorites
   /* public boolean getPossibleFavorites()
    {
        boolean temp = getBuses() && getBusStops();
        lstPossibleFavorites.setAdapter(possibleFavoritesAdapter);
        return temp;
    }*/

    public boolean getPossibleFavorites()
    {
        String username;
        username = UserInfoApplication.getInstance().getUsername();

        if(username == null || username.isEmpty())
            return false;

        /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("https://uco-edmond-bus.herokuapp.com/api/favoriteservice/favorites/possible/"
                        + username
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // Called when response HTTP status is "200 OK"
                        try {
                            setPossibleFavorites(response);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                        // Called when response HTTP status is "400"
                        txtPossibleFavorites.setText("Unable to pull data from server.");
                    }
                });
        return true;
    }

    public void setPossibleFavorites(JSONArray response)
    {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = new JSONObject(response.getString(i));
                Favorite newFavorite = new Favorite(item.getInt("id"), item.getString("type"), item.getString("name"));
                possibleFavorites.add(newFavorite);
                // if(possibleFavorites.isEmpty())
                //display to user that there are no buses registered
                // else
            }
            lstPossibleFavorites.setAdapter(possibleFavoritesAdapter);
            if(possibleFavorites.isEmpty())
                txtPossibleFavorites.setText("You currently have all possible favorites added to your favorites.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*public boolean getBuses()
    {
        /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
       /* AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("https://uco-edmond-bus.herokuapp.com/api/busservice/buses/"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // Called when response HTTP status is "200 OK"
                        try {
                            setBuses(response);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        return true;
    }

    public void setBuses(JSONArray response)
    {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = new JSONObject(response.getString(i));
                Favorite newFavorite = new Favorite(item.getInt("id"), "Bus", item.getString("name"));
                possibleFavorites.add(newFavorite);
                // if(possibleFavorites.isEmpty())
                //display to user that there are no buses registered
                // else
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean getBusStops()
    {
        /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        /*AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data
        client.get("https://uco-edmond-bus.herokuapp.com/api/busstopservice/stops/"
                , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // Called when response HTTP status is "200 OK"
                        try {
                            setBusStops(response);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        return true;
    }

    public void setBusStops(JSONArray response)
    {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = new JSONObject(response.getString(i));
                Favorite newFavorite = new Favorite(item.getInt("id"), "Bus Stop", item.getString("name"));
                possibleFavorites.add(newFavorite);

                // if(possibleFavorites.isEmpty())
                //display to user that there are no buses registered
                // else
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
}
