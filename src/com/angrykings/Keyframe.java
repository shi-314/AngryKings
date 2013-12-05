package com.angrykings;

import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;

import org.json.JSONException;
import org.json.JSONObject;

public class Keyframe implements IJsonSerializable {

	private double timestampSec;
	private JSONObject cannonballJson;
	private JSONObject castleJson;

	public Keyframe(JSONObject json) throws JSONException {

		this.fromJson(json);

	}

	public Keyframe(double timestampSec, Cannonball cannonball, Castle castle) throws JSONException {

		this.timestampSec = timestampSec;
		this.cannonballJson = cannonball.toJson();
		this.castleJson = castle.toJson();

	}

	public double getTimestampSec() {
		return timestampSec;
	}

	public JSONObject getCannonballJson() {
		return cannonballJson;
	}

	public JSONObject getCastleJson() {
		return castleJson;
	}

	@Override
	public JSONObject toJson() throws JSONException {

		JSONObject json = new JSONObject();
		json.put("timestampSec", this.timestampSec);
		json.put("cannonball", this.cannonballJson);
		json.put("castle", this.castleJson);

		return json;

	}

	@Override
	public void fromJson(JSONObject json) throws JSONException {

		this.timestampSec = json.getDouble("timestampSec");
		this.cannonballJson = json.getJSONObject("cannonball");
		this.castleJson = json.getJSONObject("castle");

	}
}
