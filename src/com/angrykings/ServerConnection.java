package com.angrykings;

import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ServerConnection {

	private static final String TAG = "com.angrykings";

	public static abstract class OnMessageHandler {
		public abstract void onMessage(String payload);
	}

	private final WebSocketConnection connection = new WebSocketConnection();
	private static ServerConnection instance;
	private OnMessageHandler handler;

	private ServerConnection() {

	}

	public static ServerConnection getInstance() {
		if (instance == null) {
			instance = new ServerConnection();
		}
		return instance;
	}

	public WebSocketConnection getConnection() {
		return connection;
	}

	public OnMessageHandler getHandler() {
		return handler;
	}

	public void setHandler(OnMessageHandler handler) {
		this.handler = handler;
	}

	public void start() {
		final String wsuri = "ws://spaeti.pavo.uberspace.de:61224";

		try {
			connection.connect(wsuri, new WebSocketHandler() {

				@Override
				public void onOpen() {
					Log.d(TAG, "Status: Connected");
				}

				@Override
				public void onTextMessage(String payload) {
					handler.onMessage(payload);
				}

				@Override
				public void onClose(int code, String reason) {
					Log.d(TAG, "Connection lost.");
				}
			});
		} catch (WebSocketException e) {
			Log.d(TAG, e.toString());
		}
	}

}