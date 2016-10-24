package edu.uco.captainplanet.myapplication;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import edu.uco.captainplanet.myapplication.Dtos.Favorite;

public class UserInfoApplication {
    private static UserInfoApplication instance;

    private String username;
    private String password;
    private boolean loggedIn;
    private int id;
    private List<Favorite> myFavorites;

    public static synchronized UserInfoApplication getInstance() {
        if (instance == null) {
            instance = new UserInfoApplication();
        }
        return instance;
    }

    private UserInfoApplication() {
        setUsername("");
        setPassword("");
        setLoggedIn(false);
        myFavorites = new ArrayList<>();
    }

    public int getId(){ return id; }

    public void setId(int id){this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() { return loggedIn; }

    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }

    public List<Favorite> getFavorites(){ return myFavorites; }

    public void getUserFavorites()
    {
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
                });
    }

    private void setUserFavorites(JSONArray response)
    {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = new JSONObject(response.getString(i));
                Favorite newFavorite = new Favorite(item.getInt("id"), item.getInt("userId"), item.getInt("favoriteId"), item.getString("type"), item.getString("name"));
                getFavorites().add(newFavorite);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
