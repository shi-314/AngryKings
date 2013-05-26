package com.prototype.johann_hofmann.connectMe;

import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class KingServerConnection {

	private static final String TAG = "com.johann_hofmann.connectMe";

	public static abstract class OnMessageHandler {
		public abstract void onMessage(String payload);
	}

	private final WebSocketConnection mConnection = new WebSocketConnection();
	private static KingServerConnection instance;
	private OnMessageHandler handler;

	private KingServerConnection() {

	}

	public static KingServerConnection getInstance() {
		if (instance == null) {
			instance = new KingServerConnection();
		}
		return instance;
	}

	public WebSocketConnection getmConnection() {
		return mConnection;
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
			mConnection.connect(wsuri, new WebSocketHandler() {

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