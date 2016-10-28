package edu.uco.captainplanet.myapplication.Dtos;

public class Notification {
    private String message;

    public Notification(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Notification))
            return false; //not a Notification Object

        if(this.getMessage().equals (((Notification)other).getMessage()))
            return true; //notification messages match
        return false;
    }
}
