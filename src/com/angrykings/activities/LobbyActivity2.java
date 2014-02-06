package com.angrykings.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.angrykings.Action;
import com.angrykings.LobbyPlayer;
import com.angrykings.MyAdapter;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.utils.ServerJSONBuilder;
import com.angrykings.utils.ServerMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ray on 12.12.13.
 */
public class LobbyActivity2 extends Activity{

    private Button bZufall;
    private TextView tZufall;
    private ListView lobbyList;

    private String username;
    private List<String> users;
    private Map<String, LobbyPlayer> listItemToName = new HashMap<String, LobbyPlayer>();
    private List<LobbyPlayer> lobbyPlayers;

    private class Player{
        final String name;
        final String id;
        final String win;
        final String lose;

        Player(String name, String id, String win, String lose){
            this.name = name;
            this.id = id;
            this.win = win;
            this.lose = lose;
        }
    }

    public LobbyActivity2() {
    }

    private void updateLobby(List<LobbyPlayer> user){
        //lobbyList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, user));
        lobbyList.setAdapter(new MyAdapter(this, R.layout.list_row, user));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        bZufall = (Button) findViewById(R.id.bZufall);
        lobbyList = (ListView) findViewById(R.id.lobbyList);


        Bundle extras = getIntent().getExtras();
        users = new ArrayList<String>();
        lobbyPlayers = new ArrayList<LobbyPlayer>();
        if (extras != null) {
            this.username = extras.getString("username");
        }
        ServerConnection
                .getInstance()
                .sendTextMessage(ServerMessage.gotoLobby());

        lobbyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //final LobbyPlayer partner = listItemToName.get(
                  //      lobbyList.getItemAtPosition(
                    //            position).toString());
                final LobbyPlayer partner = lobbyPlayers.get(position);
                Log.d("zahl in Liste", lobbyList.getItemAtPosition(position).toString());

                final AlertDialog dialog = new AlertDialog.Builder(LobbyActivity2.this)
                        .setTitle("Please Wait").setMessage("Waiting for partner")
                        .show();

                ServerConnection.getInstance().setHandler(new ServerConnection.OnMessageHandler() {

                    @Override
                    public void onMessage(final String payload) {
                        try {
                            final JSONObject jObj = new JSONObject(payload);
                            // TODO revert logic: if confirmed
                            if (jObj.getInt("action") == Action.Server.DENIED) {
                                dialog.cancel();
                                displayLobby();
                            } else {
                                dialog.dismiss();
                                Intent intent = new Intent(LobbyActivity2.this, OnlineGameActivity.class);
                                intent.putExtra("myTurn", false)
                                        .putExtra("username", username)
                                        .putExtra("partnername", partner.name)
                                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                ServerConnection
                        .getInstance()
                        .sendTextMessage(ServerMessage.pair(partner.id));
            }
        });

        bZufall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int random = (int) (Math.random() * users.size());
                //Log.d("Random Nummer: ", "users: " + users.size() + random);
                //final LobbyPlayer partner = listItemToName.get(lobbyList.getItemAtPosition(random).toString());
                final LobbyPlayer partner = lobbyPlayers.get(random);
                final AlertDialog dialog = new AlertDialog.Builder(LobbyActivity2.this)
                        .setTitle("Please Wait").setMessage("Waiting for partner")
                        .show();
                ServerConnection.getInstance().setHandler(new ServerConnection.OnMessageHandler() {

                    @Override
                    public void onMessage(final String payload) {
                        try {
                            final JSONObject jObj = new JSONObject(payload);
                            // TODO revert logic: if confirmed
                            if (jObj.getInt("action") == Action.Server.DENIED) {
                                dialog.cancel();
                                displayLobby();
                            } else {
                                dialog.dismiss();
                                Intent intent = new Intent(LobbyActivity2.this, OnlineGameActivity.class);
                                intent.putExtra("myTurn", false)
                                        .putExtra("username", username)
                                        .putExtra("partnername", partner.name)
                                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                ServerConnection
                        .getInstance()
                        .sendTextMessage(ServerMessage.pair(partner.id));
            }
        });


        displayLobby();
    }


    public void displayLobby(){
        //updateLobby(users);
        updateLobby(lobbyPlayers);
        lobbyList.setTextFilterEnabled(true);
        ServerConnection.getInstance().setHandler(new ServerConnection.OnMessageHandler() {

            @Override
            public void onMessage(String payload) {
                try {
                    final JSONObject jObj = new JSONObject(payload);
                    if (jObj.getInt("action") == Action.Server.REQUEST) {
                        new AlertDialog.Builder(LobbyActivity2.this)
                                .setTitle("Request")
                                .setMessage(
                                        jObj.getString("partner")
                                                + " requested a match!")
                                .setPositiveButton("Okay",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                ServerConnection
                                                        .getInstance()
                                                        .sendTextMessage(ServerMessage.acceptChallenge());
                                                Intent intent = new Intent(
                                                        LobbyActivity2.this,
                                                        OnlineGameActivity.class);
                                                intent.putExtra("myTurn", true);
                                                intent.putExtra("username",
                                                        username);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                                                try {
                                                    intent.putExtra("partnername",
                                                            jObj.getString("partner"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    intent.putExtra("partnername","Partner");
                                                }
                                                startActivity(intent);
                                            }
                                        })
                                .setNegativeButton("Deny",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                ServerConnection
                                                        .getInstance()
                                                        .sendTextMessage(ServerMessage.denyChallenge());
                                            }
                                        }).show();
                    } else if (jObj.getInt("action") == Action.Server.LOBBY_UPDATE) {
                        Log.d("AngryKings", "received lobby update: " + jObj.get("names"));

                        JSONArray userArray = new JSONArray(jObj.getString("names"));
                        users.clear();
                        lobbyPlayers.clear();

                        for (int i = 0; i < userArray.length(); i++) {
                            final JSONObject jsonObject = userArray.getJSONObject(i);
                            String eingabe = jsonObject
                                    .getString("name")
                                    + "   Gewonnen: "
                                    + jsonObject.getString("won")
                                    + "   Verloren: "
                                    + jsonObject.getString("lost");

                            users.add(eingabe);
                            listItemToName.put(eingabe,
                                    new LobbyPlayer(jsonObject.getString("name"),
                                            jsonObject.getString("_id"),
                                            jsonObject.getString("won"),
                                            jsonObject.getString("lost")));

                            lobbyPlayers.add(new LobbyPlayer(jsonObject.getString("name"),
                                    jsonObject.getString("_id"),
                                    jsonObject.getString("won"),
                                    jsonObject.getString("lost")));
                        }

                        //updateLobby(users);
                        updateLobby(lobbyPlayers);
                    }
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
            }
        });
}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ServerConnection
                    .getInstance()
                    .sendTextMessage(ServerMessage.leaveLobby());
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop(){
        ServerConnection
                .getInstance()
                .sendTextMessage(new ServerJSONBuilder().create(Action.Client.LEAVE_LOBBY).build());
        super.onStop();
    }

}