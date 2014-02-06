package com.angrykings.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.angrykings.Action;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.ServerConnection.OnMessageHandler;
import com.angrykings.utils.ServerMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final Button nameSendButton = (Button) findViewById(R.id.button);
		final EditText nameText = (EditText) findViewById(R.id.nameInput);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		ServerConnection.getInstance().setHandler(new OnMessageHandler() {
			
			@Override
			public void onMessage(String payload) {
				try {
					JSONObject jObj = new JSONObject(payload);
					if (jObj.getInt("action") == Action.Server.CONFIRM) {
						Intent intent = new Intent(getApplicationContext(),
								MainActivity.class);
						intent.putExtra("username", nameText.getText().toString());
						startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		nameSendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ServerConnection
						.getInstance()
						.sendTextMessage(ServerMessage.setName(nameText.getText().toString()));
                settings.edit().putString("username", nameText.getText().toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
