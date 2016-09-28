package edu.uco.captainplanet.myapplication.Dtos;

/**
 * Created by Drew Demechko on 9/28/2016.
 */

public class Favorite {

    private int id;
    private int userId;
    private int favoriteId;
    private String type;

    public Favorite(int id, int userId, int favoriteId, String type)
    {
        this.id = id;
        this.userId = userId;
        this.favoriteId = favoriteId;
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public int getUserid()
    {
        return userId;
    }

    public int getFavoriteId()
    {
        return favoriteId;
    }

    public String getType()
    {
        return type;
    }

    public String toString()
    {
        return type + " # " + favoriteId;
    }
}
