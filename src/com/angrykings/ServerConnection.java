package com.angrykings;

import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ServerConnection {

	private static final String TAG = "com.angrykings.ServerConnection";

	public static abstract class OnMessageHandler {
		public abstract void onMessage(String payload);
	}
	
	public static abstract class OnStartHandler{
		public abstract void onStart();
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

	public boolean isConnected() {
		return this.connection.isConnected();
	}

	public OnMessageHandler getHandler() {
		return handler;
	}

	// TODO implement so that activities don't have to override handlers
	public void setHandler(OnMessageHandler handler) {
		this.handler = handler;
	}

	/**
	 * Connects to our WebSocket Server. Only needs to be called ONCE in the whole app lifecycle.
	 * 
	 * @param startHandler
	 */
	//TODO As long the connection persists, do not restart
	public void start(final OnStartHandler startHandler) {

		try {
			connection.connect(GameConfig.WEBSERVICE_URI, new WebSocketHandler() {
				@Override
				public void onOpen() {
					Log.d(TAG, "Status: Connected");
					startHandler.onStart();
				}

				@Override
				public void onTextMessage(String payload) {
					Log.d(TAG, "received: "+payload);
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

	/**
	 * Sends a text message to the server.
	 * @param payload
	 */
	public void sendTextMessage(String payload) {
		Log.d(TAG, "sent: "+payload);
		this.connection.sendTextMessage(payload);
	}

}