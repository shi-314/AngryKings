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
import org.andengine.entity.modifier.*;
import org.andengine.entity.primitive.Line;
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

	private Cannon cannon;
	private Cannon enemyCannon;
	private Castle leftCastle, rightCastle;
	private King leftKing, rightKing;

    //
    // Game Objects Positions
    //

    private static int LEFTCANNONX = -250;
    private static int RIGHTCANNONX = 200;
    private static int LEFTCASTLEX = -475;
    private static int RIGHTCASTLEX = 275;
    private static int LEFTKINGX = -300;
    private static int RIGHTKINGX = 250;

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
	private int round;
	boolean turnSent;
	boolean receivedEndTurn;
	int aimX, aimY;
	String myName, enemyName;
	boolean wonTheGame = false;
	boolean isLeft;

	private class AngryKingsMessageHandler extends ServerConnection.OnMessageHandler {
		@Override
		public void onMessage(String payload) {
			try {
				JSONObject jObj = new JSONObject(payload);
				if (jObj.getInt("action") == Action.Server.TURN
						&& round % 2 == 1) {

					// partner has made his turn

					final int x = Integer.parseInt(jObj.getString("x"));
					final int y = Integer.parseInt(jObj.getString("y"));

					handlePartnerTurn(x, y);

				} else if (jObj.getInt("action") == Action.Server.YOU_WIN
						|| jObj.getInt("action") == Action.Server.PARTNER_LEFT) {

					// this client has won the game

					Intent intent = new Intent(OnlineGameActivity.this,
							EndGameActivity.class);
					intent.putExtra("hasWon", true);
					intent.putExtra("isLeft", OnlineGameActivity.this.isLeft);
					intent.putExtra("username", myName);
					intent.putExtra("partnername", enemyName);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
					wonTheGame = true;

				} else if (jObj.getInt("action") == Action.Server.END_TURN) {

					// partner has end his turn -> synchronize physics

					onPartnerTurnEnd(jObj);

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

		this.receivedEndTurn = true;

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
				this.receivedEndTurn = true;
				this.round = 0;
				amILeft = true;
				myX = LEFTCANNONX;
				myY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				enemyX = RIGHTCANNONX;
				enemyY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				leftPlayerName = extras.getString("username");
				rightPlayerName = extras.getString("partnername");
				this.myName = leftPlayerName;
				this.enemyName = rightPlayerName;
			} else {
				this.receivedEndTurn = false;
				this.round = 1;
				amILeft = false;
				enemyX = LEFTCANNONX;
				enemyY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				myX = RIGHTCANNONX;
				myY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();
				leftPlayerName = extras.getString("partnername");
				rightPlayerName = extras.getString("username");
				this.myName = rightPlayerName;
				this.enemyName = leftPlayerName;
			}
		}

		isLeft = amILeft;

		this.cannon = new Cannon(amILeft);
		this.cannon.setPosition(myX, myY);
		scene.attachChild(this.cannon);

		this.enemyCannon = new Cannon(!amILeft);
		this.enemyCannon.setPosition(enemyX, enemyY);
		scene.attachChild(this.enemyCannon);

		this.leftCastle = new Castle(LEFTCASTLEX, BasicMap.GROUND_Y);
		this.rightCastle = new Castle(RIGHTCASTLEX, BasicMap.GROUND_Y);

		this.rightKing = new King(rm.getKingTexture1(), RIGHTKINGX, BasicMap.GROUND_Y - rm.getKingTexture1().getHeight() / 2);
		scene.attachChild(this.rightKing);

		this.leftKing = new King(rm.getKingTexture2(), LEFTKINGX, BasicMap.GROUND_Y - rm.getKingTexture2().getHeight() / 2);
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
			this.cannon.showAimCircle();
		} else {
			hud.setStatus(this.getString(R.string.enemyTurn));
		}

		// TODO in HUD auslagern
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {

				float leftLife = leftCastle.getHeight() / initialLeftCastleHeight;
				float rightLife = rightCastle.getHeight() / initialRightCastleHeight;

				hud.getLeftLifeBar().setValue(1.0f - ((1.0f - leftLife) * 2.0f));
				hud.getRightLifeBar().setValue(1.0f - ((1.0f - rightLife) * 2.0f));

				if (left && leftLife < 0.5f || !left && rightLife < 0.5f) {
					gc.getHud().setStatus("Du hast verloren!");
					gc.getHud().setStatus(getString(R.string.hasLost));
					serverConnection.sendTextMessage(ServerMessage.lose());
					Intent intent = new Intent(OnlineGameActivity.this, EndGameActivity.class);
					intent.putExtra("hasWon", false);
					intent.putExtra("isLeft", OnlineGameActivity.this.isLeft);
					intent.putExtra("username", myName);
					intent.putExtra("partnername", enemyName);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
				}
			}

			@Override
			public void reset() {

			}
		});

		scene.registerUpdateHandler(pm.getPhysicsWorld());

		pm.setFreeze(true);

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
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

		double cannonDistanceX = pSceneTouchEvent.getX() - this.cannon.getX();
		double cannonDistanceY = pSceneTouchEvent.getY() - this.cannon.getY();
		double cannonDistanceR = Math.sqrt(cannonDistanceX*cannonDistanceX + cannonDistanceY*cannonDistanceY);

		Log.d(TAG, "Distance: " + cannonDistanceX + " " + cannonDistanceY);
		Log.d(TAG, "DistanceR: " + cannonDistanceR);

		// TODO: refactor constant
		//if (this.isAiming) {
		if (cannonDistanceR < rm.getAimCircleTexture().getHeight() &&
                ((isLeft && cannonDistanceX > 0) || (!isLeft && cannonDistanceX < 0))) {

			//
			// aim and fire
			//

			if (!PhysicsManager.getInstance().isReady()) {
				return true;
			}

			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();
			
			int iX = (int) x;
			int iY = (int) y;
			
			if (this.cannon.pointAt(iX, iY)) {
				this.aimX = iX;
				this.aimY = iY;
			}

			if (pSceneTouchEvent.isActionUp() && this.receivedEndTurn) {
				if (!turnSent && round % 2 == 0) {
					this.handleMyTurn();
				}
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

	private void onMyTurnEnd() {
		PhysicsManager.getInstance().setFreeze(true);

		//
		// send castle block positions
		//

		String jsonStr = ServerMessage.endTurn();
		this.serverConnection.sendTextMessage(ServerMessage.endTurn());

		Debug.d("send "
				+ PhysicsManager.getInstance().getPhysicalEntities().size()
				+ " entities");

		//
		// update own castle block position to avoid floating point precision
		// issues
		//

		try {
			JSONObject jObj = new JSONObject(jsonStr);
			JSONArray jsonEntities = jObj.getJSONArray("entities");

			if (jsonEntities != null) {
				PhysicsManager.getInstance().updateEntities(jsonEntities);
			}
		} catch (JSONException e) {
			Debug.d("JSONException: " + e);
		}

		this.cannon.hideAimCircle();

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

	private void onPartnerTurnEnd(JSONObject jObj) throws JSONException {
		PhysicsManager.getInstance().setFreeze(true);

		JSONArray jsonEntities = jObj.getJSONArray("entities");

		if (jsonEntities == null) {
			Debug.d("Warning: jsonEntities is null");
		} else {
			Debug.d("received end turn from server with " + jsonEntities.length() + " entities");

			PhysicsManager.getInstance().updateEntities(jsonEntities);
			receivedEndTurn = true;
			this.cannon.showAimCircle();

			if (!this.wonTheGame) {
				hud.setStatus(getString(R.string.yourTurn));
				if (isLeft) {
					leftKing.getSprite().setCurrentTileIndex(1);
					rightKing.getSprite().setCurrentTileIndex(0);
				} else {
					leftKing.getSprite().setCurrentTileIndex(0);
					rightKing.getSprite().setCurrentTileIndex(1);
				}
			}
		}

		if(this.isLeft)
			this.leftKing.jump();
		else
			this.rightKing.jump();
	}

	private void handleMyTurn() {
		this.turn();

		this.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				PhysicsManager.getInstance().setFreeze(false);

				final Cannonball ball = OnlineGameActivity.this.cannon .fire(GameConfig.CANNON_FORCE);

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

		this.serverConnection.sendTextMessage(ServerMessage.turn(this.aimX, this.aimY));
		this.turnSent = true;
	}

	private void handlePartnerTurn(final int x, final int y) {
		this.turn();

		this.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				receivedEndTurn = false;
				PhysicsManager.getInstance().setFreeze(false);
				enemyCannon.pointAt(x, y);

				final Cannonball ball = enemyCannon.fire(GameConfig.CANNON_FORCE);

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
						PhysicsManager.getInstance().setFreeze(true);
					}
				});
			}
		});

		turnSent = false;
	}

	private void turn() {
		this.round++;
	}
	
	@Override
	public synchronized void onPauseGame() {
		super.onPauseGame();
		if(BuildConfig.DEBUG) {
			Debug.d(this.getClass().getSimpleName() + ".onPauseGame lalala" + " @(Thread: '" + Thread.currentThread().getName() + "')");
		}
	}
	
}
