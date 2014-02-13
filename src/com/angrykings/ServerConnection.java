package com.angrykings;

import android.util.Log;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

public class ServerConnection {

	private static final String TAG = "com.angrykings.ServerConnection";

	public interface OnMessageHandler {
		void onMessage(String payload);
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
			WebSocketOptions webSocketOptions = new WebSocketOptions();
			webSocketOptions.setMaxMessagePayloadSize(GameConfig.WEBSOCKET_MAX_PAYLOAD_SIZE);
			webSocketOptions.setMaxFramePayloadSize(GameConfig.WEBSOCKET_MAX_FRAME_SIZE);

            Log.i(TAG, "connecting to " + GameConfig.WEBSERVICE_URI + " ...");

			connection.connect(GameConfig.WEBSERVICE_URI, new WebSocketHandler() {
				@Override
				public void onOpen() {
					Log.i(TAG, "Status: Connected");
					startHandler.onStart();
				}

				@Override
				public void onTextMessage(String payload) {
					int length = payload.length();

					if (length > 128)
						Log.i(TAG, "received " + length + " bytes: " + payload.substring(0, 128) + " ...");
					else
						Log.i(TAG, "received " + length + " bytes: " + payload);

					handler.onMessage(payload);
				}

				@Override
				public void onClose(int code, String reason) {
					Log.i(TAG, "Connection lost.");
				}
			}, webSocketOptions);
		} catch (WebSocketException e) {
			Log.i(TAG, e.toString());
		}
	}

	/**
	 * Sends a text message to the server.
	 * @param payload
	 */
	public void sendTextMessage(String payload) {

        int length = payload.length();

        if (length > 128)
            Log.i(TAG, "sent " + length + " bytes: " + payload.substring(0, 128) + " ...");
        else
            Log.i(TAG, "sent " + length + " bytes: " + payload);

        if(this.connection == null)
            Log.e(TAG, "connection is null :/");

		this.connection.sendTextMessage(payload);

	}

}