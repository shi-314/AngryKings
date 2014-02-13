package com.angrykings.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.angrykings.Action;
import com.angrykings.GameConfig;
import com.angrykings.Installation;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.ServerConnection.OnMessageHandler;
import com.angrykings.ServerConnection.OnStartHandler;
import com.angrykings.utils.ServerMessage;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

	private String username;
	private Button lobbyButton;
    private Button runningGamesButton;
	private Button introButton;
    private Button settingsButton;
    private GoogleCloudMessaging gcm;
    private SharedPreferences settings;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		introButton = (Button) findViewById(R.id.introButton);

		lobbyButton = (Button) findViewById(R.id.lobbyButton);
        lobbyButton.setText(getString(R.string.bConnecting));
        runningGamesButton = (Button) findViewById(R.id.runningGamesButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/Rom_Ftl_Srif.ttf");
        runningGamesButton.setTypeface(font);
        lobbyButton.setTypeface(font);

		lobbyButton.setEnabled(false);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //
        // init google registration id for our app
        //

        registerInBackground(new Runnable() {
            @Override
            public void run() {

                //
                // connect to our server
                //

                ServerConnection.getInstance().setHandler(new OnMessageHandler() {

                    @Override
                    public void onMessage(String payload) {
                        try {
                            JSONObject jObj = new JSONObject(payload);
                            if (jObj.getInt("action") == Action.Server.KNOWN_USER || jObj.getInt("action") == Action.Server.SEND_NAME) {
                                username = jObj.getString("name");
                                settings.edit().putString("username", username).commit();
                                //lobbyButton.setBackgroundResource(R.drawable.lobby_button);
                                //bLobby.setText(getString(R.string.lobbyButton));
                                lobbyButton.setText(getString(R.string.bNewGame));
                                lobbyButton.setEnabled(true);
                            } else if (jObj.getInt("action") == Action.Server.UNKNOWN_USER) {
                                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // look for facebook login

                Session.openActiveSession(MainActivity.this, false, new Session.StatusCallback() {
                    @Override
                    public void call(Session session, SessionState state, Exception exception) {
                        if (state == SessionState.OPENED) {
                            Log.i(TAG, "Facebook: already logged in");
                            onFacebookLogin(session);
                        }
                    }
                });

            }
        });

        //
        // TODO: This should happen when the gcm registration went well
        //

		if(!ServerConnection.getInstance().isConnected()){
            final String id = Installation.id(this);
            Log.i(TAG, "The installation ID is " + id);

            final String registrationId = settings.getString("registrationId", "");

			ServerConnection.getInstance().start(new OnStartHandler() {
				@Override
				public void onStart() {
					ServerConnection
					.getInstance()
					.sendTextMessage(ServerMessage.setId(id, registrationId));
				}
			});
		}else{
			ServerConnection
			.getInstance()
			.sendTextMessage(ServerMessage.getName());
		}


		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
		}

		lobbyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
				intent.putExtra("username", username);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}

		});

		introButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
			
		});

        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        runningGamesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RunningGamesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

	}

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Session session = Session.getActiveSession();
        session.addCallback(
                new Session.StatusCallback() {

                    @Override
                    public void call(Session session, SessionState state, Exception exception) {
                        if (state == SessionState.OPENED) {
                            Log.i(TAG, "Facebook: Logged in");
                            onFacebookLogin(session);
                        }
                    }
                });
        session.onActivityResult(this, requestCode, resultCode, data);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

    @Override
    protected void onResume() {
        super.onResume();
        username = settings.getString("username", "");
    }

    protected void onFacebookLogin(Session session) {
        Log.i(TAG, "onFacebookLogin");
        Request.newMeRequest(session, new Request.GraphUserCallback() {
            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    Log.i(TAG, "Facebook: Logged in with " + user.getFirstName());

                    String profilePicture = "http://graph.facebook.com/" + user.getId() + "/picture";

                    settings.edit()
                            .putString("profilePicture", profilePicture)
                            .putString("facebookId", user.getId())
                            .commit();

                    //
                    // TODO: Set player name to facebook name :D
                    //

                } else {
                    Log.w(TAG, "facebook login failed, response=" + response.toString());
                }
            }
        }).executeAsync();

        final String fqlQuery = "SELECT uid,name FROM user WHERE uid IN " +
                "(SELECT uid2 FROM friend WHERE uid1 = me())";

        final Bundle params = new Bundle();
        params.putString("q", fqlQuery);

        new Request(session,
                "/fql",
                params,
                HttpMethod.GET,
                new Request.Callback(){
                    public void onCompleted(Response response) {
                        Log.i(TAG, "Result: " + response.toString());

                        try{
                            GraphObject graphObject = response.getGraphObject();
                            JSONObject jsonObject = graphObject.getInnerJSONObject();

                            JSONArray array = jsonObject.getJSONArray("data");
                            HashSet<String> friends = new HashSet<String>();

                            for(int i=0;i<array.length();i++){
                                JSONObject f = array.getJSONObject(i);
                                //Log.d("uid",f.getString("uid"));
                                //Log.d("name", friend.getString("name"));
                                friends.add(f.getString("uid"));
                            }

                            Log.i(TAG, "Facebook: I have " + friends.size() + " friends");

                            settings.edit()
                                    .putStringSet("facebookFriends", friends)
                                    .commit();
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     *
     * Stores the registration ID in the application's
     * shared preferences.
     */
    private void registerInBackground(final Runnable onDone) {

        //
        // TODO: Google refreshes their registration ids periodically. Handle changing  IDs :(
        //

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String registrationId = settings.getString("registrationId", null);

                if(registrationId != null) {
                    Log.i("GCM", "Device already registered with ID: "+registrationId);
                    return registrationId;
                }

                String msg = "";

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    registrationId = gcm.register(GameConfig.GOOGLE_API_PROJECT_ID);

                    msg = "Registered, registration ID=" + registrationId;

                    Log.i("GCM", msg);
                    settings.edit().putString("registrationId", registrationId).commit();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                    Log.i("GCM", "Registration failed, try again in " + GameConfig.GOOGLE_API_REGISTRATION_DELAY_MILLISEC + "ms");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    registerInBackground(onDone);
                                }
                            }, GameConfig.GOOGLE_API_REGISTRATION_DELAY_MILLISEC);
                        }
                    });
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(onDone != null)
                    onDone.run();

            }
        }.execute(null, null, null);
    }
}
