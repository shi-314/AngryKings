package com.angrykings.utils;

import com.angrykings.PhysicalEntity;
import com.angrykings.PhysicsManager;

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
			json += "\"x\": " + e.getAreaShape().getX() + ", ";
			json += "\"y\": " + e.getAreaShape().getY() + ", ";
			json += "\"rotation\": " + e.getBody().getAngle() + "";
			json += "}";

			if (i < entities.size() - 1)
				json += ", ";
		}

		json += "]";

		return this;
	}

}
