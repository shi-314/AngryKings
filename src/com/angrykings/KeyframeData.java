package com.angrykings;

import com.badlogic.gdx.math.Vector2;

import org.json.JSONException;
import org.json.JSONObject;

public class KeyframeData implements IJsonSerializable {

    public int entityId;
    public Vector2 position;
    public float angle;

    public KeyframeData() {

        this.position = new Vector2();

    }

    public KeyframeData(JSONObject json) throws JSONException {
        this();
        this.fromJson(json);
    }

    private float lerp(float v0, float v1, float t) {
        return v0+(v1-v0)*t;
    }

    public KeyframeData interpolate(KeyframeData data, float t) {

        KeyframeData interpolated = new KeyframeData();

        interpolated.position.x = lerp(this.position.x, data.position.x, t);
        interpolated.position.y = lerp(this.position.y, data.position.y, t);
        interpolated.angle = lerp(this.angle, data.angle, t);

        return interpolated;

    }

    @Override
    public JSONObject toJson() throws JSONException {

        JSONObject json = new JSONObject();

        json.put("i", this.entityId);
        json.put("x", this.position.x);
        json.put("y", this.position.y);
        json.put("a", this.angle);

        return json;

    }

    @Override
    public void fromJson(JSONObject json) throws JSONException {

        this.entityId = json.getInt("i");
        this.position = new Vector2((float) json.getDouble("x"), (float) json.getDouble("y"));
        this.angle = (float) json.getDouble("a");

    }
}