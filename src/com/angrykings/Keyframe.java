package com.angrykings;

import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Keyframe implements IJsonSerializable {

	private double timestampSec;
	private KeyframeData cannonballData;
	private ArrayList<KeyframeData> castleBlocksData;

    public Keyframe() {

        this.castleBlocksData = new ArrayList<KeyframeData>();

    }

	public Keyframe(JSONObject json) throws JSONException {

        this();
		this.fromJson(json);

	}

	public Keyframe(double timestampSec, Cannonball cannonball, Castle castle) throws JSONException {

        this();
		this.timestampSec = timestampSec;
		this.cannonballData = cannonball.getKeyframeData();
		this.castleBlocksData = castle.getKeyframeData();

	}

	public double getTimestampSec() {
		return timestampSec;
	}

    public KeyframeData getCannonballKeyframeData() {

        return this.cannonballData;

    }

    public ArrayList<KeyframeData> getCastleKeyframeData() {

        return this.castleBlocksData;

    }

	@Override
	public JSONObject toJson() throws JSONException {

		JSONObject json = new JSONObject();
		json.put("t", this.timestampSec);
		json.put("ball", this.cannonballData.toJson());

        JSONArray blocksJson = new JSONArray();

        for(KeyframeData kd : this.castleBlocksData)
            blocksJson.put(kd.toJson());

		json.put("castle", blocksJson);

		return json;

	}

	@Override
	public void fromJson(JSONObject json) throws JSONException {

        try {
            this.timestampSec = json.getDouble("t");
            this.cannonballData = new KeyframeData(json.getJSONObject("ball"));

            this.castleBlocksData.clear();

            JSONArray blocks = json.getJSONArray("castle");
            for(int i = 0; i < blocks.length(); i++) {
                this.castleBlocksData.add(new KeyframeData(blocks.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

	}
}
