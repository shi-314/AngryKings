package com.angrykings.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import com.angrykings.*;
import com.angrykings.cannons.Cannon;
import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;
import com.angrykings.kings.King;
import com.angrykings.maps.BasicMap;
import com.angrykings.utils.ServerMessage;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * OnlineGameActivity
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 06.06.13
 */
public class OnlineGameActivity extends BaseGameActivity implements
		IOnSceneTouchListener, IScrollDetectorListener,
		IPinchZoomDetectorListener {

	private static final String TAG = "com.angrykings.OnlineGameActivity";
	private GameContext gc;
	private Handler handler;
	private GameHUD hud;
	private ResourceManager rm;

	//
	// Game Objects
	//

	private Cannon myCannon;
	private Cannon partnerCannon;
	private Castle leftCastle, rightCastle;
	private King leftKing, rightKing;

	//
	// Navigation Attributes
	//

	private SurfaceScrollDetector scrollDetector;
	private PinchZoomDetector pinchZoomDetector;
	private float pinchZoomStartedCameraZoomFactor;
	boolean isAiming = true;

	//
	// Network
	//

	private ServerConnection serverConnection;
	int aimX, aimY;
	String myName, enemyName;
	boolean isLeft;

	GameStatus status;

	private class AngryKingsMessageHandler extends ServerConnection.OnMessageHandler {
		@Override
		public void onMessage(String payload) {
			try {
				JSONObject jObj = new JSONObject(payload);
				if (jObj.getInt("action") == Action.Server.TURN) {

					turn();

				}else if (jObj.getInt("action") == Action.Server.END_TURN) {
					// partner has made his turn

					final int x = Integer.parseInt(jObj.getString("x"));
					final int y = Integer.parseInt(jObj.getString("y"));

					handlePartnerTurn(x, y);

				} else if (jObj.getInt("action") == Action.Server.YOU_WIN || jObj.getInt("action") == Action.Server.PARTNER_LEFT) {

					// this client has won the game

					won();


				} else if (jObj.getInt("action") == Action.Server.END_TURN) {

					// partner has end his turn -> synchronize physics

					// onPartnerTurnEnd(jObj);

				}
			} catch (JSONException e) {

				// TODO: Handle exceptions?

				Debug.d("JSONException: " + e);

			}
		}
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		gc = GameContext.getInstance();
		handler = new Handler();

		ZoomCamera camera = new ZoomCamera(GameConfig.CAMERA_X, GameConfig.CAMERA_Y, GameConfig.CAMERA_WIDTH, GameConfig.CAMERA_HEIGHT);

		camera.setZoomFactor(GameConfig.CAMERA_STARTUP_ZOOM);
		camera.setBounds(
				GameConfig.CAMERA_MIN_X, GameConfig.CAMERA_MIN_Y,
				GameConfig.CAMERA_MAX_X, GameConfig.CAMERA_MAX_Y
		);
		camera.setBoundsEnabled(true);

		gc.setCamera(camera);

		this.serverConnection = ServerConnection.getInstance();

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(GameConfig.CAMERA_WIDTH,
						GameConfig.CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		this.rm = ResourceManager.getInstance();

		this.rm.load(this);

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {

		gc = GameContext.getInstance();

		gc.setGameActivity(this);

		//
		// initialize network
		//

		ServerConnection.getInstance().setHandler(new AngryKingsMessageHandler());

		gc.setVboManager(this.getVertexBufferObjectManager());

		if (GameConfig.LOG_FPS)
			this.mEngine.registerUpdateHandler(new FPSLogger());

		//
		// initialize the scene
		//

		Scene scene = new Scene();
		scene.setBackground(rm.getSkySprite());
		scene.setOnSceneTouchListener(this);

		gc.setScene(scene);

		//
		// initialize the physics engine
		//

		PhysicsManager pm = PhysicsManager.getInstance();
		pm.clearEntities();

		//
		// initialize the entities
		//

		BasicMap map = new BasicMap();
		scene.attachChild(map);

		Boolean amILeft = false;

		String leftPlayerName = "";
		String rightPlayerName = "";
		int myX = 0;
		int myY = 0;
		int enemyX = 0;
		int enemyY = 0;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Boolean myTurn = extras.getBoolean("myTurn");
			if (myTurn) {
				amILeft = true;
				myX = -400;
				myY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				enemyX = 300;
				enemyY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				leftPlayerName = extras.getString("username");
				rightPlayerName = extras.getString("partnername");
				this.myName = leftPlayerName;
				this.enemyName = rightPlayerName;
			} else {
				amILeft = false;
				enemyX = -400;
				enemyY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				myX = 300;
				myY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				leftPlayerName = extras.getString("partnername");
				rightPlayerName = extras.getString("username");
				this.myName = rightPlayerName;
				this.enemyName = leftPlayerName;
			}
		}

		isLeft = amILeft;

		this.myCannon = new Cannon(amILeft);
		this.myCannon.setPosition(myX, myY);
		scene.attachChild(this.myCannon);

		this.partnerCannon = new Cannon(!amILeft);
		this.partnerCannon.setPosition(enemyX, enemyY);
		scene.attachChild(this.partnerCannon);

		this.leftCastle = new Castle(-800, BasicMap.GROUND_Y);
		this.rightCastle = new Castle(500, BasicMap.GROUND_Y);

		this.rightKing = new King(rm.getKingTexture1(), 400, BasicMap.GROUND_Y - rm.getKingTexture1().getHeight() / 2);
		scene.attachChild(this.rightKing);

		this.leftKing = new King(rm.getKingTexture2(), -550, BasicMap.GROUND_Y - rm.getKingTexture2().getHeight() / 2);
		scene.attachChild(this.leftKing);

		leftKing.getSprite().setCurrentTileIndex(1);
		rightKing.getSprite().setCurrentTileIndex(0);

		//
		// initialize navigation
		//

		this.scrollDetector = new SurfaceScrollDetector(this);
		this.pinchZoomDetector = new PinchZoomDetector(this);

		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		hud = new GameHUD();

		hud.setOnAimTouched(new Runnable() {
			@Override
			public void run() {
				isAiming = !isAiming;
			}
		});

		hud.setOnWhiteFlagTouched(new Runnable() {
			@Override
			public void run() {
				resign();
			}
		});

		gc.setHud(hud);
		gc.getCamera().setHUD(hud);

		final float initialLeftCastleHeight = leftCastle.getInitialHeight();
		final float initialRightCastleHeight = rightCastle.getInitialHeight();
		final boolean left = amILeft;

		hud.setLeftPlayerName(leftPlayerName);
		hud.setRightPlayerName(rightPlayerName);

		Debug.d("left player name: " + leftPlayerName);
		Debug.d("right player name: " + rightPlayerName);

		if (amILeft) {
			hud.setStatus(this.getString(R.string.yourTurn));
		} else {
			hud.setStatus(this.getString(R.string.enemyTurn));
		}

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {

				float leftLife = leftCastle.getHeight() / initialLeftCastleHeight;
				float rightLife = rightCastle.getHeight() / initialRightCastleHeight;

				hud.getLeftLifeBar().setValue(1.0f - ((1.0f - leftLife) * 2.0f));
				hud.getRightLifeBar().setValue(1.0f - ((1.0f - rightLife) * 2.0f));

				if ((left && leftLife < 0.5f || !left && rightLife < 0.5f) && status != GameStatus.LOST) {
					lost();
				}

			}

			@Override
			public void reset() {

			}
		});

		scene.registerUpdateHandler(pm.getPhysicsWorld());

		this.leftCastle.freeze();
		this.rightCastle.freeze();

		pOnCreateSceneCallback.onCreateSceneFinished(scene);

		this.serverConnection.sendTextMessage(ServerMessage.ready());
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		gc = GameContext.getInstance();

		if (gc.getPhysicsWorld() == null)
			return false;

		double cannonDistanceX = pSceneTouchEvent.getX() - this.myCannon.getX();
		double cannonDistanceY = pSceneTouchEvent.getY() - this.myCannon.getY();
		double cannonDistanceR = Math.sqrt(cannonDistanceX*cannonDistanceX + cannonDistanceY*cannonDistanceY);
		
		// Log.d(TAG, "Distance: " + cannonDistanceX + " " + cannonDistanceY);
		// Log.d(TAG, "DistanceR: " + cannonDistanceR);

		// TODO: refactor constant
		//if (this.isAiming) {
		if (cannonDistanceR < 1200) {

			//
			// aim and fire
			//

			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();
			
			int iX = (int) x;
			int iY = (int) y;
			
			if (this.myCannon.pointAt(iX, iY)) {
				this.aimX = iX;
				this.aimY = iY;
			}

			if (pSceneTouchEvent.isActionUp() && this.status == GameStatus.MY_TURN) {
				this.handleMyTurn(this.aimX, this.aimY);
			}

		} else {

			//
			// pinch and zoom
			//
			
			if (pSceneTouchEvent.isActionDown()) {
				this.scrollDetector.setEnabled(true);
			}

			this.pinchZoomDetector.onTouchEvent(pSceneTouchEvent);

			if (this.pinchZoomDetector.isZooming()) {
				this.scrollDetector.setEnabled(false);
			} else {
				this.scrollDetector.onTouchEvent(pSceneTouchEvent);
			}
			
		}

		return true;
	}

	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector,
								   TouchEvent pSceneTouchEvent) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera) gc.getCamera();
		this.pinchZoomStartedCameraZoomFactor = camera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector,
							TouchEvent pTouchEvent, float pZoomFactor) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera) gc.getCamera();

		float factor = this.pinchZoomStartedCameraZoomFactor * pZoomFactor;
		if (factor > GameConfig.CAMERA_ZOOM_MIN
				&& factor < GameConfig.CAMERA_ZOOM_MAX)
			camera.setZoomFactor(factor);
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector,
									TouchEvent pTouchEvent, float pZoomFactor) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera) gc.getCamera();

		float factor = this.pinchZoomStartedCameraZoomFactor * pZoomFactor;
		if (factor > GameConfig.CAMERA_ZOOM_MIN
				&& factor < GameConfig.CAMERA_ZOOM_MAX)
			camera.setZoomFactor(factor);
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
								float pDistanceX, float pDistanceY) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera) gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
						 float pDistanceX, float pDistanceY) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera) gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
								 float pDistanceX, float pDistanceY) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera) gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	private void resign() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				final Dialog dialog = new Dialog(OnlineGameActivity.this);
				dialog.setContentView(R.layout.quit_dialog);
				dialog.setCancelable(true);
				dialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));

				Button bCancel = (Button) dialog.findViewById(R.id.bCancel);
				Button bResign = (Button) dialog.findViewById(R.id.bResign);

				bCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				bResign.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hud.setStatus(getString(R.string.youResigned));
						serverConnection.sendTextMessage(ServerMessage.lose());
						dialog.dismiss();
						Intent intent = new Intent(OnlineGameActivity.this, EndGameActivity.class);
						intent.putExtra("hasWon", false);
						intent.putExtra("isLeft", OnlineGameActivity.this.isLeft);
						intent.putExtra("username", myName);
						intent.putExtra("partnername", enemyName);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(intent);
					}
				});
				dialog.show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			resign();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void handleMyTurn(final int x, final int y) {

		Log.i(getClass().getName(), "handleMyTurn()");

		this.status = GameStatus.PARTNER_TURN;
		this.myCannon.hideAimCircle();

		this.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {

				if(isLeft)
					rightCastle.unfreeze();
				else
					leftCastle.unfreeze();

				final Cannonball ball = OnlineGameActivity.this.myCannon.fire(GameConfig.CANNON_FORCE);

				getEngine().registerUpdateHandler(
						new TimerHandler(
								GameConfig.CANNONBALL_TIME_SEC,
								new ITimerCallback() {
									@Override
									public void onTimePassed(TimerHandler pTimerHandler) {
										ball.remove();
									}
								}));

				ball.setOnRemove(new Runnable() {
					@Override
					public void run() {
						onMyTurnEnd();
					}
				});

			}
		});

	}

	private void handlePartnerTurn(final int x, final int y) {

		Log.i(getClass().getName(), "handlePartnerTurn()");

		this.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {

				if(isLeft)
					leftCastle.unfreeze();
				else
					rightCastle.unfreeze();

				partnerCannon.pointAt(x, y);

				final Cannonball ball = partnerCannon.fire(GameConfig.CANNON_FORCE);

				getEngine().registerUpdateHandler(
						new TimerHandler(GameConfig.CANNONBALL_TIME_SEC,
								new ITimerCallback() {
									@Override
									public void onTimePassed(TimerHandler pTimerHandler) {
										ball.remove();
									}
								}));

				ball.setOnRemove(new Runnable() {
					@Override
					public void run() {
						if(isLeft)
							leftCastle.freeze();
						else
							rightCastle.freeze();

						onPartnerTurnEnd(new JSONObject()); //TODO: pass keyframes
					}
				});
			}
		});

	}

	private void onMyTurnEnd() {

		Log.i(getClass().getName(), "onMyTurnEnd()");

		if(this.isLeft)
			this.rightCastle.freeze();
		else
			this.leftCastle.freeze();

		//
		// send castle block positions
		//

		this.serverConnection.sendTextMessage(ServerMessage.endTurn(this.aimX, this.aimY));

		// TODO: send keyframes

		this.myCannon.hideAimCircle();

		this.hud.setStatus(getString(R.string.enemyTurn));

		if (isLeft) {
			leftKing.getSprite().setCurrentTileIndex(0);
			rightKing.getSprite().setCurrentTileIndex(1);
		} else {
			leftKing.getSprite().setCurrentTileIndex(1);
			rightKing.getSprite().setCurrentTileIndex(0);
		}

		if(this.isLeft)
			this.rightKing.jump();
		else
			this.leftKing.jump();
	}

	private void onPartnerTurnEnd(JSONObject jObj) {

		Log.i(getClass().getName(), "onPartnerTurnEnd()");

		if(this.isLeft)
			this.leftCastle.freeze();
		else
			this.rightCastle.freeze();


		JSONArray jsonEntities = null;
		try {
			jsonEntities = jObj.getJSONArray("entities");
		} catch (JSONException e) {
			Log.e(getClass().getName(), e.toString());
		}

		if (jsonEntities == null) {
			Log.w(getClass().getName(), "Warning: jsonEntities is null");
		} else {
			Log.i(getClass().getName(), "received end turn from server with " + jsonEntities.length() + " entities");

			PhysicsManager.getInstance().updateEntities(jsonEntities);
		}

		hud.setStatus(getString(R.string.yourTurn));
		if (isLeft) {
			leftKing.getSprite().setCurrentTileIndex(1);
			rightKing.getSprite().setCurrentTileIndex(0);
		} else {
			leftKing.getSprite().setCurrentTileIndex(0);
			rightKing.getSprite().setCurrentTileIndex(1);
		}

		if(this.isLeft)
			this.leftKing.jump();
		else
			this.rightKing.jump();

		if(this.status != GameStatus.LOST)
			this.serverConnection.sendTextMessage(ServerMessage.ready());

	}

	private void turn() {

		Log.i(getClass().getName(), "turn()");

		this.status = GameStatus.MY_TURN;

		this.myCannon.showAimCircle();

	}

	private void won() {

		Log.i(getClass().getName(), "won()");

		this.status = GameStatus.WON;

		Intent intent = new Intent(OnlineGameActivity.this, EndGameActivity.class);
		intent.putExtra("hasWon", true);
		intent.putExtra("isLeft", OnlineGameActivity.this.isLeft);
		intent.putExtra("username", myName);
		intent.putExtra("partnername", enemyName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);

	}

	private void lost() {
		Log.i(getClass().getName(), "lost()");

		this.status = GameStatus.LOST;
		serverConnection.sendTextMessage(ServerMessage.lose());

		gc.getHud().setStatus("Du hast verloren!");
		gc.getHud().setStatus(getString(R.string.hasLost));

		Intent intent = new Intent(OnlineGameActivity.this, EndGameActivity.class);
		intent.putExtra("hasWon", false);
		intent.putExtra("isLeft", OnlineGameActivity.this.isLeft);
		intent.putExtra("username", myName);
		intent.putExtra("partnername", enemyName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);

	}
	
	@Override
	public synchronized void onPauseGame() {
		Log.i(getClass().getName(), "onPauseGame()");

		super.onPauseGame();
		if(BuildConfig.DEBUG) {
			Debug.d(this.getClass().getSimpleName() + ".onPauseGame lalala" + " @(Thread: '" + Thread.currentThread().getName() + "')");
		}
	}
	
}
