package com.angrykings.utils;

import com.angrykings.Action;
import com.angrykings.Keyframe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class provides static methods to build quickly Json Messages for our game.
 */
public class ServerMessage {
	private ServerMessage() { }

	public static String endTurn(int x, int y, ArrayList<Keyframe> keyframes) {
		JSONArray keyframesJson = new JSONArray();

		for(Keyframe k : keyframes) {
			try {
				keyframesJson.put(k.toJson());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		JSONObject msg = new JSONObject();

		try {
			msg.put("action", Action.Client.END_TURN);
			msg.put("x", String.valueOf(x));
			msg.put("y", String.valueOf(y));
			msg.put("keyframes", keyframesJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return msg.toString();
	}

	public static String setId(String id, String registrationId) {
		return new ServerJSONBuilder().create(Action.Client.SET_ID)
                .option("id", id)
                .option("registration_id", registrationId)
                .build();
	}

	public static String getName() {
		return new ServerJSONBuilder().create(Action.Client.GET_NAME).build();
	}

	public static String setName(String name) {
		return new ServerJSONBuilder().create(Action.Client.SET_NAME).option("name", name).build();
	}

	public static String gotoLobby() {
		return new ServerJSONBuilder().create(Action.Client.GO_TO_LOBBY).build();
	}

	public static String leaveLobby() {
		return new ServerJSONBuilder().create(Action.Client.LEAVE_LOBBY).build();
	}

	public static String enterGame(int partnerId) {
		return new ServerJSONBuilder().create(Action.Client.ENTER_GAME).integer("partner", partnerId).build();
	}

    public static String leaveGame() {
        return new ServerJSONBuilder().create(Action.Client.LEAVE_GAME).build();
    }

    public static String lose() {
        return  new ServerJSONBuilder().create(Action.Client.LOSE).build();
    }

    public static String getRunningGames() {
        return new ServerJSONBuilder().create(Action.Client.GET_RUNNING_GAMES).build();
    }

}
