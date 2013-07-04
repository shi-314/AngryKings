package com.angrykings.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angrykings.R;
import com.angrykings.ServerConnection;
import com.angrykings.ServerConnection.OnMessageHandler;

public class EndGameActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Boolean hasWon = false;
		Boolean isLeft = false;
		String username = "";
		String partnername = "";

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endgame);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			hasWon = extras.getBoolean("hasWon");
			isLeft = extras.getBoolean("isLeft");
			username = extras.getString("username");
			partnername = extras.getString("partnername");
		}

		final TextView winLoseText = (TextView) findViewById(R.id.winLoseText);
		final TextView leftNameText = (TextView) findViewById(R.id.leftNameText);
		final TextView rightNameText = (TextView) findViewById(R.id.rightNameText);
		final ImageView bigKingImageView = (ImageView) findViewById(R.id.bigKingImageView);

		if (hasWon) {
			winLoseText.setText(R.string.hasWon);
		} else {
			winLoseText.setText(R.string.hasLost);
		}

		if (isLeft) {
			leftNameText.setText(username);
			rightNameText.setText(partnername);
		} else {
			leftNameText.setText(partnername);
			rightNameText.setText(username);
		}

		if ((isLeft && hasWon) || (!isLeft && !hasWon)) {
			bigKingImageView.setImageResource(R.drawable.happy_big_king);
		}

		ServerConnection.getInstance().setHandler(new OnMessageHandler() {

			@Override
			public void onMessage(String payload) {
			}
		});

		final Button bLobby = (Button) findViewById(R.id.backToLobby);
		bLobby.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		});

	}
}
