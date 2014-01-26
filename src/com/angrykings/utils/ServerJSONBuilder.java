package com.angrykings.utils;

import com.angrykings.PhysicalEntity;
import com.angrykings.PhysicsManager;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ServerJSONBuilder {

	private String json;

	public ServerJSONBuilder create(int action) {
		json = "{\"action\":" + action;
		return this;
	}

	public ServerJSONBuilder option(String key, String value) {
		json += ",\"" + key + "\":\"" + value + "\"";
		return this;
	}

    public ServerJSONBuilder integer(String key, int value) {
        json += ",\"" + key + "\":" + value;
        return this;
    }

	public String build() {
		return json + "}";
	}

	public ServerJSONBuilder entities() {
		ArrayList<PhysicalEntity> entities = PhysicsManager.getInstance().getPhysicalEntities();

		json += ", \"entities\": [";

		for (int i = 0; i < entities.size(); i++) {
			PhysicalEntity e = entities.get(i);

			json += "{";
			json += "\"id\": " + e.getId() + ", ";
			json += "\"x\": " + precision(2, e.getAreaShape().getX()) + ", ";
			json += "\"y\": " + precision(2, e.getAreaShape().getY()) + ", ";
			json += "\"rotation\": " + precision(3, e.getBody().getAngle()) + "";
			json += "}";

			if (i < entities.size() - 1)
				json += ", ";
		}

		json += "]";

		return this;
	}

	//
	// TODO: remove this function when the physics is perfectly synchronized?
	//

	public static Float precision(int decimalPlace, Float d) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

}
