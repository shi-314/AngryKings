package com.angrykings.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.VideoView;

import com.angrykings.R;

public class IntroActivity extends Activity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		VideoView videoView = (VideoView) findViewById(R.id.videoView1);
		String uri = "android.resource://" + getPackageName() + "/" + R.raw.intro;
		videoView.setVideoURI(Uri.parse(uri));
		
		videoView.start();
		videoView.setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer arg0) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
			}
			
		});
	}
	
}
