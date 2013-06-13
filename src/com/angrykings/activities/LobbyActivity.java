package com.angrykings.activities;

import java.util.ArrayList;
import java.util.List;

import com.angrykings.Action;
import com.angrykings.ServerConnection;

import org.json.JSONArray;
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
import com.angrykings.utils.ServerJSONBuilder;

public class LobbyActivity extends ListActivity {

	private String username;
	private String users = "";
	private List<String> user;

	private void updateLobby(List<String> user) {
		
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, user));
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		user = new ArrayList<String>();
		if (extras != null) {
			this.username = extras.getString("username");
		}
		ServerConnection
				.getInstance()
				.getConnection()
				.sendTextMessage(
						new ServerJSONBuilder().create(
								Action.Client.GO_TO_LOBBY).build());
		displayLobby();
	}

	private void displayLobby() {
		updateLobby(user);
		getListView().setTextFilterEnabled(true);
		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
				try {
					JSONObject jObj = new JSONObject(payload);
					if (jObj.getInt("action") == Action.Server.REQUEST) {
						new AlertDialog.Builder(LobbyActivity.this)
								.setTitle("Request")
								.setMessage(
										jObj.getString("partner")
												+ " requested a match!")
								.setPositiveButton("Okay",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												ServerConnection
														.getInstance()
														.getConnection()
														.sendTextMessage(
																new ServerJSONBuilder()
																		.create(Action.Client.ACCEPT)
																		.build());
												Intent intent = new Intent(
														LobbyActivity.this,
														PhysicsTest.class);
												intent.putExtra("myTurn", false);
												intent.putExtra("username",
														username);
												startActivity(intent);
											}
										})
								.setNegativeButton("Deny",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												ServerConnection
														.getInstance()
														.getConnection()
														.sendTextMessage(
																new ServerJSONBuilder()
																		.create(Action.Client.DENY)
																		.build());
											}
										}).show();
					} else if (jObj.getInt("action") == Action.Server.LOBBY_UPDATE) {
						users = jObj.getString("names");
						JSONArray userArray = new JSONArray(users);
						for (int i = 0; i < userArray.length(); i++) {
							user.add(userArray.getString(i));
							Log.d("name", username);
						}
						updateLobby(user);
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

		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("Please Wait").setMessage("Waiting for partner")
				.show();

		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
				try {
					JSONObject jObj = new JSONObject(payload);
					if (jObj.getInt("action") == Action.Server.DENIED) {
						dialog.cancel();
						displayLobby();
					} else {
						Intent intent = new Intent(LobbyActivity.this,
								PhysicsTest.class);
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
						new ServerJSONBuilder()
								.create(Action.Client.PAIR)
								.option("partner",
										getListView().getItemAtPosition(
												position).toString()).build());

	}
}
