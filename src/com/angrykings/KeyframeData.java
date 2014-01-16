package com.angrykings;

import com.badlogic.gdx.math.Vector2;

import org.json.JSONException;
import org.json.JSONObject;

public class KeyframeData implements IJsonSerializable {

    public int entityId;
    public Vector2 position;
    public Vector2 linearVelocity;
    public float angle;
    public float angularVelocity;

    public KeyframeData() {

        this.position = new Vector2();
        this.linearVelocity = new Vector2();

    }

    private float lerp(float v0, float v1, float t) {
        return v0+(v1-v0)*t;
    }

    public KeyframeData interpolate(KeyframeData data, float t) {

        KeyframeData interpolated = new KeyframeData();

        interpolated.position.x = lerp(this.position.x, data.position.x, t);
        interpolated.position.y = lerp(this.position.y, data.position.y, t);

        interpolated.linearVelocity.x = lerp(this.linearVelocity.x, data.linearVelocity.x, t);
        interpolated.linearVelocity.y = lerp(this.linearVelocity.y, data.linearVelocity.y, t);

        interpolated.angle = lerp(this.angle, data.angle, t);
        interpolated.angularVelocity = lerp(this.angularVelocity, data.angularVelocity, t);

        return interpolated;

    }

    @Override
    public JSONObject toJson() throws JSONException {

        JSONObject json = new JSONObject();

        json.put("i", this.entityId);
        json.put("x", this.position.x);
        json.put("y", this.position.y);
        json.put("r", this.angle);

        return json;

    }

    @Override
    public void fromJson(JSONObject json) throws JSONException {

        try {
            this.entityId = json.getInt("i");
            this.position = new Vector2((float) json.getDouble("x"), (float) json.getDouble("y"));
            this.angle = (float) json.getDouble("r");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}