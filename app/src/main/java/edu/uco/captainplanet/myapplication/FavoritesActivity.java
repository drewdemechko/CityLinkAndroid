package edu.uco.captainplanet.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        favorites = UserInfoApplication.getInstance().getFavorites();
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
        if(UserInfoApplication.getInstance().getFavorites().isEmpty()) {
            txtFavorites.setText("Unable to pull data from server.");
            return false;
        }
        setUserFavorites();
        return true;
    }

    public void setUserFavorites()
    {
        try {
            lstFavorites.setAdapter(favoritesAdapter);
            lstFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                               long id) {

                    String item = ((TextView)view).getText().toString();

                    String favoriteSelected = item.toString();
                    for(Favorite favorite : favorites)
                        if(favorite.toString().equals(favoriteSelected)) {
                            if(deleteFavorite(favorite)) {
                                favorites.remove(favorite);
                                possibleFavorites.add(favorite);
                                favoritesAdapter.notifyDataSetChanged();
                                possibleFavoritesAdapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(getApplicationContext(), "You currently have no favorites saved.", Toast.LENGTH_SHORT).show();
                            }
                        break;
                        }
                }
            });
            if(favorites.isEmpty())
                txtFavorites.setText("You currently have no favorites saved.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

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
                int id = UserInfoApplication.getInstance().getId();
                Favorite newFavorite = new Favorite(id,item.getInt("favoriteId"), item.getString("type"), item.getString("name"));
                possibleFavorites.add(newFavorite);
                // if(possibleFavorites.isEmpty())
                //display to user that there are no buses registered
                // else
            }
            lstPossibleFavorites.setAdapter(possibleFavoritesAdapter);
            lstPossibleFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    String item = ((TextView)view).getText().toString();

                    String favoriteSelected = item.toString();
                    for(Favorite favorite : possibleFavorites)
                        if(favorite.toString().equals(favoriteSelected)) {
                            if(addFavorite(favorite)) {
                                possibleFavorites.remove(favorite);
                                favorites.add(favorite);
                                favoritesAdapter.notifyDataSetChanged();
                                possibleFavoritesAdapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(getApplicationContext(), "Unable to add favorite, check your connection.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                }
            });
            if(possibleFavorites.isEmpty())
                txtPossibleFavorites.setText("You currently have all possible favorites added to your favorites.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean deleteFavorite(final Favorite favorite)
    {
         /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data

        if(favorite.getType().equals("Bus")) {
            client.get("https://uco-edmond-bus.herokuapp.com/api/favoriteservice/favorites/delete/bus/"
                            + favorite.getUserid() + "/" + favorite.getFavoriteId()
                    , new JsonHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                            // Called when response HTTP status is "400"

                            //revert changes
                            favorites.add(favorite);
                            possibleFavorites.remove(favorite);
                            favoritesAdapter.notifyDataSetChanged();
                            possibleFavoritesAdapter.notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(), "Unable to delete favorite, check your connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else if(favorite.getType().equals("Bus Stop")) {
            client.get("https://uco-edmond-bus.herokuapp.com/api/favoriteservice/favorites/delete/busstop/"
                            + favorite.getUserid() + "/" + favorite.getFavoriteId()
                    , new JsonHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                            // Called when response HTTP status is "400"

                            //revert changes
                            favorites.add(favorite);
                            possibleFavorites.remove(favorite);
                            favoritesAdapter.notifyDataSetChanged();
                            possibleFavoritesAdapter.notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(), "Unable to delete favorite, check your connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return true;
    }

    public boolean addFavorite(final Favorite favorite)
    {
        /*
         * Attempt to get JSON info
         * Reference: http://loopj.com/android-async-http/
        */
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000); // give enough time for client to get the JSON data

        if(favorite.getType().equals("Bus")) {
            client.get("https://uco-edmond-bus.herokuapp.com/api/favoriteservice/favorites/create/bus/"
                            + favorite.getUserid() + "/" + favorite.getFavoriteId()
                    , new JsonHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                            // Called when response HTTP status is "400"

                            //revert changes
                            possibleFavorites.add(favorite);
                            favorites.remove(favorite);
                            favoritesAdapter.notifyDataSetChanged();
                            possibleFavoritesAdapter.notifyDataSetChanged();

                            Toast.makeText(getApplicationContext(), "Unable to add favorite, check your connection.", Toast.LENGTH_SHORT).show();

                        }
                    });
        }else if(favorite.getType().equals("Bus Stop")) {
            client.get("https://uco-edmond-bus.herokuapp.com/api/favoriteservice/favorites/create/busstop/"
                            + favorite.getUserid() + "/" + favorite.getFavoriteId()
                    , new JsonHttpResponseHandler(){
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                            // Called when response HTTP status is "400"

                            //revert changes
                            possibleFavorites.add(favorite);
                            favorites.remove(favorite);
                            favoritesAdapter.notifyDataSetChanged();
                            possibleFavoritesAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Unable to add favorite, check your connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        return true;
    }
}
