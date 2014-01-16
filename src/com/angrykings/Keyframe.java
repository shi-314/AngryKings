package com.angrykings;

import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.JarOutputStream;

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

    public KeyframeData getCannonballKeyframeData() {

        KeyframeData data = new KeyframeData();

        try {
            data.fromJson(this.cannonballJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;

    }

    public ArrayList<KeyframeData> getCastleKeyframeData() {

        ArrayList<KeyframeData> dataList = new ArrayList<KeyframeData>();

        Iterator<String> it = this.castleJson.keys();
        while(it.hasNext()) {
            String k = it.next();

            KeyframeData keyframeData = new KeyframeData();

            try {
                keyframeData.fromJson(this.castleJson.getJSONObject(k));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataList.add(keyframeData);
        }

        return dataList;

    }

	@Override
	public JSONObject toJson() throws JSONException {

		JSONObject json = new JSONObject();
		json.put("t", this.timestampSec);
		json.put("ball", this.cannonballJson);
		json.put("castle", this.castleJson);

		return json;

	}

	@Override
	public void fromJson(JSONObject json) throws JSONException {

		this.timestampSec = json.getDouble("t");
		this.cannonballJson = json.getJSONObject("ball");
		this.castleJson = json.getJSONObject("castle");

	}
}
