package com.angrykings.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.angrykings.Action;
import com.angrykings.GameConfig;
import com.angrykings.Installation;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.ServerConnection.OnMessageHandler;
import com.angrykings.ServerConnection.OnStartHandler;
import com.angrykings.utils.ServerMessage;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.prefs.Preferences;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

	private String username;
	private Button lobbyButton;
	private Button introButton;
    private Button settingsButton;
    private GoogleCloudMessaging gcm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

        registerInBackground();

		introButton = (Button) findViewById(R.id.introButton);

		lobbyButton = (Button) findViewById(R.id.lobbyButton);
		lobbyButton.setBackgroundResource(R.drawable.verbinde_button);

        settingsButton = (Button) findViewById(R.id.settingsButton);

		lobbyButton.setEnabled(false);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
				try {
					JSONObject jObj = new JSONObject(payload);
					if (jObj.getInt("action") == Action.Server.KNOWN_USER || jObj.getInt("action") == Action.Server.SEND_NAME) {
						username = jObj.getString("name");
                        settings.edit().putString("username", username);
						lobbyButton.setBackgroundResource(R.drawable.lobby_button);
						//bLobby.setText(getString(R.string.lobbyButton));
						lobbyButton.setEnabled(true);
					} else if (jObj.getInt("action") == Action.Server.UNKNOWN_USER) {
						Intent intent = new Intent(MainActivity.this,
								LogInActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		if(!ServerConnection.getInstance().isConnected()){
            final String id = Installation.id(this);
            Log.i(TAG, "The installation ID is " + id);

			ServerConnection.getInstance().start(new OnStartHandler() {
				
				@Override
				public void onStart() {
					ServerConnection
					.getInstance()
					.sendTextMessage(ServerMessage.setId(id));
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
				Intent intent = new Intent(getApplicationContext(),
						LobbyActivity2.class);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString("username", "");
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        Log.i("GCM", "registerInBackground");

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    String registrationId = gcm.register(GameConfig.GOOGLE_API_PROJECT_ID);

                    msg = "Device registered, registration ID=" + registrationId;

                    Log.i("GCM", msg);

                    //Preferences preferences = Preferences.getInstance();
                    //preferences.storeRegistrationId(registrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    //registerInBackground();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("GCM", "onPostExecute: "+msg);
                //sendRegistrationToBackend();
            }
        }.execute(null, null, null);
    }
}
