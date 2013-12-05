package com.angrykings;

import org.json.JSONException;
import org.json.JSONObject;

public interface IJsonSerializable {

	public abstract JSONObject toJson() throws JSONException;
	public abstract void updateFromJson(JSONObject json) throws JSONException;

}
