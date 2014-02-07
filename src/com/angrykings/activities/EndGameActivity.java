package com.angrykings.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.angrykings.Action;
import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.ServerConnection.OnMessageHandler;
import com.angrykings.utils.ServerJSONBuilder;
import com.angrykings.utils.ServerMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class EndGameActivity extends Activity {
	
	private Button revengeButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Boolean hasWon = false;
		Boolean isLeft = false;
		final String username;
		final String partnername;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endgame);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			hasWon = extras.getBoolean("hasWon");
			isLeft = extras.getBoolean("isLeft");
			username = extras.getString("username");
			partnername = extras.getString("partnername");
		}else{
			username = "";
			partnername = "";
		}

		final TextView winLoseText = (TextView) findViewById(R.id.winLoseText);
        Typeface type = Typeface.createFromAsset(getAssets(), "font/Rom_Ftl_Srif.ttf");
        winLoseText.setTypeface(type);

		if (hasWon) {
			winLoseText.setText(R.string.hasWon);
		} else {
			winLoseText.setText(R.string.hasLost);
		}

		final AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("Please Wait").setMessage("Waiting for partner").create();
		
//		ServerConnection.getInstance().setHandler(new OnMessageHandler() {
//
//			@Override
//			public void onMessage(String payload) {
//				try {
//					final JSONObject jObj = new JSONObject(payload);
//					if (jObj.getInt("action") == Action.Server.DENIED) {
//						dialog.cancel();
//					} else if (jObj.getInt("action") == Action.Server.START){
//						dialog.dismiss();
//						Intent intent = new Intent(EndGameActivity.this, OnlineGameActivity.class);
//						intent.putExtra("myTurn", false)
//						.putExtra("username", username)
//						.putExtra("partnername", partnername)
//						.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//						startActivity(intent);
//					}else if (jObj.getInt("action") == Action.Server.REQUEST) {
//						new AlertDialog.Builder(EndGameActivity.this)
//								.setTitle("Request")
//								.setMessage(
//										jObj.getString("partner")
//												+ " requested a match!")
//								.setPositiveButton("Okay",
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												ServerConnection
//														.getInstance()
//														.sendTextMessage(ServerMessage.acceptChallenge());
//												Intent intent = new Intent(
//														EndGameActivity.this,
//														OnlineGameActivity.class);
//												intent.putExtra("myTurn", true);
//												intent.putExtra("username",
//														username);
//												intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//												try {
//													intent.putExtra("partnername",
//															jObj.getString("partner"));
//												} catch (JSONException e) {
//													e.printStackTrace();
//													intent.putExtra("partnername","Partner");
//												}
//												startActivity(intent);
//											}
//										})
//								.setNegativeButton("Deny",
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												ServerConnection
//														.getInstance()
//														.sendTextMessage(ServerMessage.denyChallenge());
//											}
//										}).show();
//					}else if (jObj.getInt("action") == Action.Server.PARTNER_LEFT_GAME_OVER) {
//						revengeButton.setEnabled(false);
//                        revengeButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
//					}
//				} catch (final JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		});

		final Button backToLobbyButton = (Button) findViewById(R.id.backToLobby);
		backToLobbyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		});
		
		revengeButton = (Button) findViewById(R.id.revengeButton);
		revengeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.show();
				ServerConnection
				.getInstance()
				.sendTextMessage(new ServerJSONBuilder().create(Action.Client.ENTER_GAME).build());
			}

		});
	}
}
