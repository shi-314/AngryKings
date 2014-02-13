package com.angrykings.pregame;


public class FacebookPlayer extends LobbyPlayer {

    public String fbID;

    public FacebookPlayer(String name, int id, String win, String lose, String fbID){
        super(name, id, win, lose);
        this.fbID = fbID;
    }

}
