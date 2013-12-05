package com.angrykings;

import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Keyframe {
	private int timestampMilliSec;
	private JSONObject cannonball;
	private JSONObject castle;

	public Keyframe(JSONObject json) throws JSONException {
		this.timestampMilliSec = json.getInt("timestampMilliSec");
		this.cannonball = json.getJSONObject("cannonball");
		this.castle = json.getJSONObject("castle");
	}

	public Keyframe(int timestampMilliSec, Cannonball cannonball, Castle castle) throws JSONException {
		this.timestampMilliSec = timestampMilliSec;
		this.cannonball = cannonball.toJson();
		this.castle = castle.toJson();
	}
}
