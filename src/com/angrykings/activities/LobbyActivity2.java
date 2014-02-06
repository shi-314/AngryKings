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

import com.angrykings.Action;
import com.angrykings.LobbyPlayer;
import com.angrykings.LobbyPlayerAdapter;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.utils.ServerJSONBuilder;
import com.angrykings.utils.ServerMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity2 extends Activity {

    private ListView lobbyList;

    private String username;
    private List<String> users;
    private List<LobbyPlayer> lobbyPlayers;

    private void challengePlayer(final LobbyPlayer partner) {
        new AlertDialog.Builder(LobbyActivity2.this)
                .setTitle("Challenge")
                .setMessage(partner.name + " herausfordern?")
                .setPositiveButton("Klar!",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                ServerConnection
                                        .getInstance()
                                        .sendTextMessage(ServerMessage.enterGame(partner.id));

                                ServerConnection.getInstance().setHandler(new ServerConnection.OnMessageHandler() {
                                    @Override
                                    public void onMessage(String payload) {
                                        try {
                                            final JSONObject jObj = new JSONObject(payload);
                                            if (jObj.getInt("action") == Action.Server.NEW_GAME) {
                                                dialog.dismiss();

                                                Intent intent = new Intent(LobbyActivity2.this, OnlineGameActivity.class);
                                                intent.putExtra("existingGame", false);
                                                intent.putExtra("left", false);
                                                intent.putExtra("username", username);
                                                intent.putExtra("partnername", partner.name);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                startActivity(intent);
                                            } else if (jObj.getInt("action") == Action.Server.EXISTING_GAME) {
                                                dialog.dismiss();

                                                Intent intent = new Intent(LobbyActivity2.this, OnlineGameActivity.class);
                                                intent.putExtra("existingGame", true);
                                                intent.putExtra("left", jObj.getJSONObject("you").getBoolean("left"));
                                                intent.putExtra("username", username);
                                                intent.putExtra("partnername", partner.name);
                                                Log.d("fuer shivan you", jObj.getJSONObject("you").getJSONObject("data").toString());
                                                Log.d("fuer shivan opponent", jObj.getJSONObject("opponent").getJSONObject("data").toString());
                                                intent.putExtra("data_you", jObj.getJSONObject("you").getJSONObject("data").toString());
                                                intent.putExtra("data_opponent", jObj.getJSONObject("opponent").getJSONObject("data").toString());
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                startActivity(intent);
                                            }
                                        } catch (JSONException e) {
                                            Log.e("JSON Parser", "Error parsing data " + e.toString());
                                        }
                                    }
                                });
                            }
                        })
                .setNegativeButton("Lieber nicht",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
    }


    private void updateLobby(List<LobbyPlayer> user) {
        lobbyList.setAdapter(new LobbyPlayerAdapter(this, R.layout.list_row, user));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        Button zufallButton = (Button) findViewById(R.id.bZufall);
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
                challengePlayer(lobbyPlayers.get(position));
            }
        });

        zufallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LobbyPlayer partner = lobbyPlayers.get((int) (Math.random() * users.size()));
                challengePlayer(partner);

            }
        });

        updateLobby(lobbyPlayers);
        lobbyList.setTextFilterEnabled(true);

        ServerConnection.getInstance().setHandler(new ServerConnection.OnMessageHandler() {

            @Override
            public void onMessage(String payload) {
                try {
                    final JSONObject jObj = new JSONObject(payload);
                    if (jObj.getInt("action") == Action.Server.LOBBY_UPDATE) {
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

                            lobbyPlayers.add(new LobbyPlayer(jsonObject.getString("name"),
                                    jsonObject.getInt("public_id"),
                                    jsonObject.getString("won"),
                                    jsonObject.getString("lost")));
                        }

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
    protected void onStop() {
        ServerConnection
                .getInstance()
                .sendTextMessage(new ServerJSONBuilder().create(Action.Client.LEAVE_LOBBY).build());
        super.onStop();
    }

}