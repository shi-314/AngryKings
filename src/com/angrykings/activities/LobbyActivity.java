package com.angrykings.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.angrykings.Action;
import com.angrykings.gcm.GcmIntentService;
import com.angrykings.pregame.FacebookPlayer;
import com.angrykings.pregame.FacebookPlayerAdapter;
import com.angrykings.pregame.LobbyPlayer;
import com.angrykings.pregame.LobbyPlayerAdapter;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.utils.ServerJSONBuilder;
import com.angrykings.utils.ServerMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LobbyActivity extends Activity {

    private ListView lobbyList;
    private ListView facebookList;

    private String username;
    private List<LobbyPlayer> lobbyPlayers;
    private List<FacebookPlayer> facebookPlayers;

    private void challengePlayer(final LobbyPlayer partner) {
        new AlertDialog.Builder(LobbyActivity.this)
                .setTitle("Challenge")
                .setMessage(partner.name + " herausfordern?")
                .setPositiveButton("Klar!",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                Intent intent = new Intent(LobbyActivity.this, OnlineGameActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.putExtra("partnerId", String.valueOf(partner.id));
                                startActivity(intent);
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

    private void updateFacebookList(List<FacebookPlayer> user) {
        facebookList.setAdapter((new FacebookPlayerAdapter(this, R.layout.list_row_facebook, user)));
    }

    private void updateLobby(List<LobbyPlayer> user) {
        lobbyList.setAdapter(new LobbyPlayerAdapter(this, R.layout.list_row, user));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        Button zufallButton = (Button) findViewById(R.id.bZufall);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Rom_Ftl_Srif.ttf");
        zufallButton.setTypeface(font);
        lobbyList = (ListView) findViewById(R.id.lobbyList);
        facebookList = (ListView) findViewById(R.id.facebookList);

        Bundle extras = getIntent().getExtras();
        facebookPlayers = new ArrayList<FacebookPlayer>();
        lobbyPlayers = new ArrayList<LobbyPlayer>();
        if (extras != null) {
            this.username = extras.getString("username");
        }

        ServerConnection
                .getInstance()
                .sendTextMessage(ServerMessage.gotoLobby());

        facebookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CharSequence text = facebookPlayers.get(position).name;
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        lobbyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                challengePlayer(lobbyPlayers.get(position));
            }
        });

        zufallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LobbyPlayer partner = lobbyPlayers.get((int) (Math.random() * lobbyPlayers.size()));
                challengePlayer(partner);
            }
        });

        FacebookPlayer test1 = new FacebookPlayer("Michi", 123, "100", "50", "michi.wutte.1");
        FacebookPlayer test2 = new FacebookPlayer("Dustin", 456, "50", "100", "ohyaweird");

        facebookPlayers.add(test1);
        facebookPlayers.add(test2);
        updateFacebookList(facebookPlayers);
        updateLobby(lobbyPlayers);
        lobbyList.setTextFilterEnabled(true);

        ServerConnection.getInstance().setHandler(new ServerConnection.OnMessageHandler() {

            @Override
            public void onMessage(String payload) {
                try {
                    final JSONObject jObj = new JSONObject(payload);
                    if (jObj.getInt("action") == Action.Server.LOBBY_UPDATE) {
                        Log.d("AngryKings maan", "received lobby update: " + jObj.get("names"));

                        JSONArray userArray = new JSONArray(jObj.getString("names"));
                        lobbyPlayers.clear();

                        for (int i = 0; i < userArray.length(); i++) {
                            final JSONObject jsonObject = userArray.getJSONObject(i);

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