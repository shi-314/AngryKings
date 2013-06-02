package com.angrykings.activities;

import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.ServerConnection.OnMessageHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button nameSendButton = (Button) findViewById(R.id.button);
		final EditText nameText = (EditText) findViewById(R.id.nameInput);

		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
				Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
				intent.putExtra("users", payload);
				intent.putExtra("username", nameText.getText().toString());
				startActivity(intent);
			}
		});
		ServerConnection.getInstance().start();
		nameSendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ServerConnection
						.getInstance()
						.getConnection()
						.sendTextMessage(
								"{\"action\":\"name\",\"value\":\"" + nameText.getText().toString()
										+ "\"}");
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
