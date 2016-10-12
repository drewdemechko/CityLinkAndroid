package edu.uco.captainplanet.myapplication.Dtos;

/**
 * Created by Drew Demechko on 9/28/2016.
 */

public class Favorite {

    private int id;
    private int userId;
    private int favoriteId;
    private String type;
    private String name;

    public Favorite(int id, int userId, int favoriteId, String type, String name)
    {
        this.id = id;
        this.userId = userId;
        this.favoriteId = favoriteId;
        this.type = type;
        this.name = name;
    }

    public Favorite(int userId, int favoriteId, String type, String name)
    {
        this.userId = userId;
        this.favoriteId = favoriteId;
        this.type = type;
        this.name = name;
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
        return name + " - " + type;
    }

    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Favorite))
            return false; //not a Favorite Object

        if(this.getType() == ((Favorite)other).getType() && this.getFavoriteId() == ((Favorite)other).getFavoriteId())
            return true; //type and id match
        return false;
    }
}
