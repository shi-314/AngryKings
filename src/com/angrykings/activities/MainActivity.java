package com.angrykings.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.angrykings.Action;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.ServerConnection.OnMessageHandler;
import com.angrykings.ServerConnection.OnStartHandler;
import com.angrykings.utils.ServerJSONBuilder;

public class MainActivity extends Activity {

	private String username;
	private Button lobbyButton;
	private Button introButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		introButton = (Button) findViewById(R.id.introButton);

		lobbyButton = (Button) findViewById(R.id.lobbyButton);
		lobbyButton.setBackgroundResource(R.drawable.verbinde_button);

		lobbyButton.setEnabled(false);

		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
				try {
					JSONObject jObj = new JSONObject(payload);
					if (jObj.getInt("action") == Action.Server.KNOWN_USER || jObj.getInt("action") == Action.Server.SEND_NAME) {
						username = jObj.getString("name");
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

		if(!ServerConnection.getInstance().getConnection().isConnected()){
			ServerConnection.getInstance().start(new OnStartHandler() {
				
				@Override
				public void onStart() {
					ServerConnection
					.getInstance()
					.getConnection()
					.sendTextMessage(new ServerJSONBuilder().create(Action.Client.SET_ID).option("id", getImei()).build());				
				}
			});
		}else{
			ServerConnection
			.getInstance()
			.getConnection()
			.sendTextMessage(new ServerJSONBuilder().create(Action.Client.GET_NAME).build());				
		}


		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
		}

		lobbyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						LobbyActivity.class);
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
	}

	private String getImei() {
		getApplicationContext();
		// IMEI
		TelephonyManager tManager = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tManager.getDeviceId();
		// Ende IMEI
		return imei;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
