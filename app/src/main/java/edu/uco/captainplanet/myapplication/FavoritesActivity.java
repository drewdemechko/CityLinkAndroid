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
    private ListView lstFavorites;
    private ArrayAdapter<Favorite> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favorites = new ArrayList<>();
        lstFavorites = (ListView)findViewById(R.id.lstFavorites);
        adapter = new ArrayAdapter<Favorite>(getApplicationContext(), android.R.layout.simple_list_item_1, favorites){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }};

        if(getFavorites())
        {

        }
        else
        {
            //display error, unable to pull JSON data
        }
    }

    public boolean getFavorites()
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
                            setResult(response);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        return true;
    }

    public void setResult(JSONArray response)
    {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = new JSONObject(response.getString(i));
                Favorite newFavorite = new Favorite(item.getInt("id"), item.getInt("userId"), item.getInt("favoriteId"), item.getString("type"));
                favorites.add(newFavorite);
               // if(favorites.isEmpty())
                //display to user that they have no favorites
               // else
                    lstFavorites.setAdapter(adapter);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
