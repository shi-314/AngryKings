package com.angrykings.activities;

import com.angrykings.ServerConnection;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.angrykings.ServerConnection.OnMessageHandler;

public class LobbyActivity extends ListActivity {

	private String username;
	private String users;

	private void updateLobby(String users) {
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users
				.replace(username + ",", "").split(",")));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			users = extras.getString("users");
			this.username = extras.getString("username");
		}
		try {
			JSONObject userData = new JSONObject(users);
			users = userData.getString("names");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		displayLobby();
	}

	private void displayLobby() {
		updateLobby(users);
		getListView().setTextFilterEnabled(true);
		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
				try {
					JSONObject jObj = new JSONObject(payload);
					if (jObj.getString("action").equals("request")) {
						new AlertDialog.Builder(LobbyActivity.this).setTitle("Request")
								.setMessage(jObj.getString("partner") + " requested a match!")
								.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
										ServerConnection.getInstance().getConnection()
												.sendTextMessage("{\"action\":\"accept\"}");
										Intent intent = new Intent(LobbyActivity.this,
												PhysicsTest.class);
										intent.putExtra("myTurn", false);
										intent.putExtra("username", username);
										startActivity(intent);
									}
								}).setNegativeButton("Deny", new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
										ServerConnection.getInstance().getConnection()
												.sendTextMessage("{\"action\":\"deny\"}");
									}
								}).show();
					} else
						if (jObj.getString("action").equals("lobbyUpdate")) {
							users = jObj.getString("names");
							updateLobby(users);
						}
				} catch (JSONException e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Please Wait")
				.setMessage("Waiting for partner").show();

		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
				try {
					JSONObject jObj = new JSONObject(payload);
					if (jObj.getString("action").equals("denied")) {
						dialog.cancel();
						displayLobby();
					} else {
						Intent intent = new Intent(LobbyActivity.this, PhysicsTest.class);
						intent.putExtra("myTurn", true);
						intent.putExtra("username", username);
						startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		ServerConnection
				.getInstance()
				.getConnection()
				.sendTextMessage(
						"{\"action\":\"pair\",\"partner\":\""
								+ getListView().getItemAtPosition(position) + "\"}");

	}
}
