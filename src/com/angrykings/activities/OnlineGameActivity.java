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

import com.angrykings.Action;
import com.angrykings.AngryParallaxBackground;
import com.angrykings.BuildConfig;
import com.angrykings.GameConfig;
import com.angrykings.GameContext;
import com.angrykings.GameHUD;
import com.angrykings.GameStatus;
import com.angrykings.IPlayerTurnListener;
import com.angrykings.Keyframe;
import com.angrykings.KeyframeData;
import com.angrykings.PhysicalEntity;
import com.angrykings.PhysicsManager;
import com.angrykings.Player;
import com.angrykings.R;
import com.angrykings.ResourceManager;
import com.angrykings.ServerConnection;
import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;
import com.angrykings.maps.BasicMap;
import com.angrykings.utils.ServerMessage;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.sprite.Sprite;
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

import java.util.ArrayList;

/**
 * OnlineGameActivity
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 06.06.13
 */
public class OnlineGameActivity extends BaseGameActivity implements
		IOnSceneTouchListener, IScrollDetectorListener,
		IPinchZoomDetectorListener {

	private GameContext gc;
	private Handler handler;
	private GameHUD hud;
	private ResourceManager rm;
    private boolean myTurn;

	//
	// Game Objects
	//

	private Player me;
	private Player partner;

	//
	// Navigation Attributes
	//

	private SurfaceScrollDetector scrollDetector;
	private PinchZoomDetector pinchZoomDetector;
	private float pinchZoomStartedCameraZoomFactor;
	boolean isAiming = true;

    //
    // Camera Positions
    //

    private static final int MIDDLE = 0;
    private static final int OWNCANNONBALL = 1;
    private static final int ENEMYCANNONBALL = 2;
    private static final int ENEMYCANNON = 3;
    private static final int OFF = 4;
    private int followCamera = OFF;

	//
	// Network
	//

	private ServerConnection serverConnection;
	int aimX, aimY;
	boolean isLeft;

	GameStatus status;
    private BasicMap map;
    private AngryParallaxBackground parallaxBackground;

    private class AngryKingsMessageHandler extends ServerConnection.OnMessageHandler {
		@Override
		public void onMessage(String payload) {
			try {
				JSONObject jObj = new JSONObject(payload);
				if (jObj.getInt("action") == Action.Server.TURN) {

					turn();

				}else if (jObj.getInt("action") == Action.Server.END_TURN) {

					final int x = Integer.parseInt(jObj.getString("x"));
					final int y = Integer.parseInt(jObj.getString("y"));

					ArrayList<Keyframe> keyframes = null;

					if(jObj.has("keyframes")) {
						JSONArray jsonKeyframes = jObj.getJSONArray("keyframes");
						keyframes = new ArrayList<Keyframe>();

						for(int i = 0; i < jsonKeyframes.length(); ++i) {
							keyframes.add(new Keyframe(jsonKeyframes.getJSONObject(i)));
						}

						Log.i(getClass().getName(), "received "+keyframes.size()+" keyframes");
					} else {
						Log.i(getClass().getName(), "received 0 keyframes");
					}

					partner.handleTurn(x, y, keyframes);

				} else if (jObj.getInt("action") == Action.Server.YOU_WIN || jObj.getInt("action") == Action.Server.PARTNER_LEFT) {

					won();

				}
			} catch (JSONException e) {

				Log.w(getClass().getName(), "JSONException: " + e);

			}
		}
	}

	private class MyTurnListener implements IPlayerTurnListener {
		private ArrayList<Keyframe> keyframes;

		public MyTurnListener() {
			this.keyframes = new ArrayList<Keyframe>();
		}

		@Override
		public void onHandleTurn(int x, int y, ArrayList<Keyframe> keyframes) {
			this.keyframes.clear();
			status = GameStatus.PARTNER_TURN;
			me.getCannon().hideAimCircle();
			partner.getCastle().unfreeze();
            //followCamera = OWNCANNONBALL;
		}

		@Override
		public void onEndTurn() {
			serverConnection.sendTextMessage(ServerMessage.endTurn(aimX, aimY, this.keyframes));

            partner.getCastle().freeze();
			hud.setStatus(getString(R.string.enemyTurn));

			me.getKing().getSprite().setCurrentTileIndex(0);
			partner.getKing().getSprite().setCurrentTileIndex(1);

			partner.getKing().jump();
            //followCamera = ENEMYCANNON;
            myTurn = false;
		}

		@Override
		public void onKeyframe(float time) {
			try {
				Keyframe k = new Keyframe(time, me.getCannonball(), partner.getCastle());
				this.keyframes.add(k);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpdate(float dt) {

		}
	}

	private class PartnerTurnListener implements IPlayerTurnListener {
		private ArrayList<Keyframe> keyframes;
		private int keyframeIndex;
		private float timeElapsed;
		private float timeElapsedSinceKeyframe;
        private boolean keyframeInterlpolationDone;

		@Override
		public void onHandleTurn(int x, int y, ArrayList<Keyframe> keyframes) {
			partner.getCannon().pointAt(x, y);
			//me.getCastle().unfreeze();

            partner.getCannonball().getBody().setActive(false);

			this.keyframes = keyframes;
			this.keyframeIndex = -1; // because onKeyframe is called at t=0
			this.timeElapsed = 0;
			this.timeElapsedSinceKeyframe = 0;
            this.keyframeInterlpolationDone = false;

            //followCamera = ENEMYCANNONBALL;
		}

		@Override
		public void onEndTurn() {
			this.keyframes = null;
			me.getCastle().freeze();

			me.getKing().getSprite().setCurrentTileIndex(0);
			partner.getKing().getSprite().setCurrentTileIndex(1);

			me.getKing().jump();
            //followCamera = MIDDLE;
            myTurn = true;

			if(status != GameStatus.LOST)
				serverConnection.sendTextMessage(ServerMessage.ready());
		}

		@Override
		public void onKeyframe(float time) {
            if(this.keyframeIndex == this.keyframes.size() - 1) {
                return;
            }

            this.keyframeIndex++;
            this.timeElapsedSinceKeyframe = 0;

            if(this.keyframeIndex == this.keyframes.size() - 1) {
                this.keyframeInterlpolationDone = true;
            }
        }

		@Override
		public void onUpdate(float dt) {
			if(this.keyframes == null || this.keyframeInterlpolationDone)
				return;

			this.timeElapsed += dt;
			this.timeElapsedSinceKeyframe += dt;

            Keyframe currentKeyframe = this.keyframes.get(this.keyframeIndex);
            Keyframe nextKeyframe = this.keyframes.get(this.keyframeIndex + 1);
            Cannonball cannonball = partner.getCannonball();

            float deltaT = (float) (nextKeyframe.getTimestampSec() - currentKeyframe.getTimestampSec());
            float t = this.timeElapsedSinceKeyframe / deltaT;

            KeyframeData interpolated = currentKeyframe.getCannonballKeyframeData()
                                        .interpolate(
                                                nextKeyframe.getCannonballKeyframeData(),
                                                t
                                        );

            cannonball.setKeyframeData(interpolated);

            ArrayList<KeyframeData> currentCastleData = currentKeyframe.getCastleKeyframeData();
            ArrayList<KeyframeData> nextCastleData = nextKeyframe.getCastleKeyframeData();

            for(int i = 0; i < currentCastleData.size(); i++) {

                KeyframeData currentKeyframeData = currentCastleData.get(i);

                KeyframeData nextKeyframeData = nextCastleData.get(i);
                KeyframeData interpolatedKeyframeData = currentKeyframeData.interpolate(nextKeyframeData, t);

                PhysicalEntity block = PhysicsManager.getInstance().getEntityById(currentKeyframeData.entityId);

                block.setKeyframeData(interpolatedKeyframeData);

            }
		}

	}

	@Override
	public EngineOptions onCreateEngineOptions() {
        GameContext.clear();
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

        parallaxBackground = new AngryParallaxBackground(0f, 0f, 0f, 1f);
        parallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0f, rm.getBackgroundSprite()));

        parallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(20f, new Sprite(0, 0, rm.getParallax2(), gc.getVboManager())));
        parallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(10f, new Sprite(0, 100, rm.getParallax1(), gc.getVboManager())));


        scene.setBackground(parallaxBackground);

		scene.setOnSceneTouchListener(this);

		gc.setScene(scene);

		//
		// initialize the physics engine
		//

        PhysicsManager.clear();
		PhysicsManager pm = PhysicsManager.getInstance();
		pm.clearEntities();

		//
		// initialize the entities
		//

        map = new BasicMap();
		scene.attachChild(map);

		String myName = "";
		String partnerName = "";

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			Boolean amILeft = extras.getBoolean("myTurn");
			this.isLeft = amILeft;

			myName = extras.getString("username");
			partnerName = extras.getString("partnername");

			Log.i(getClass().getName(), "this client is " + (amILeft ? "left" : "right"));
		}

		//
		// This is important because the entity ids are incremented in the order in which we
		// create the entities :(
		//

		if(isLeft) {
			this.me = new Player(myName, isLeft);
			this.partner = new Player(partnerName, !isLeft);
		} else {
			this.partner = new Player(partnerName, !isLeft);
			this.me = new Player(myName, isLeft);
		}

		this.me.setPlayerTurnListener(new MyTurnListener());
		this.partner.setPlayerTurnListener(new PartnerTurnListener());

		//
		// initialize navigation
		//

		this.scrollDetector = new SurfaceScrollDetector(this);
		this.pinchZoomDetector = new PinchZoomDetector(this);

		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		hud = new GameHUD();

		hud.setOnWhiteFlagTouched(new Runnable() {
			@Override
			public void run() {
				resign();
			}
		});

		gc.setHud(hud);
		gc.getCamera().setHUD(hud);

		final Castle leftCastle = isLeft ? me.getCastle() : partner.getCastle();
		final Castle rightCastle = !isLeft ? me.getCastle() : partner.getCastle();

		final float initialLeftCastleHeight = leftCastle.getInitialHeight();
		final float initialRightCastleHeight = rightCastle.getInitialHeight();

		final boolean left = isLeft;

		hud.setLeftPlayerName(isLeft ? myName : partnerName);
		hud.setRightPlayerName(!isLeft ? myName : partnerName);

        myTurn = true;

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
                if(followCamera == OWNCANNONBALL){
                    me.getCannon().activateFollowCamera();
                }else if(followCamera == ENEMYCANNONBALL){
                    partner.getCannon().activateFollowCamera();
                }else if(followCamera == MIDDLE){
                    deactivateFollowCamera("mitte");
                }else if(followCamera == ENEMYCANNON){
                    deactivateFollowCamera("gegner");
                }
			}

			@Override
			public void reset() {

			}
		});

		scene.registerUpdateHandler(pm.getPhysicsWorld());

		this.me.getCastle().freeze();
		this.partner.getCastle().freeze();

		scene.registerUpdateHandler(this.me);
		scene.registerUpdateHandler(this.partner);

		pOnCreateSceneCallback.onCreateSceneFinished(scene);

		this.serverConnection.sendTextMessage(ServerMessage.ready());
		hud.setStatus(this.getString(R.string.yourTurn));
	}

    private void deactivateFollowCamera(String s) {
        ZoomCamera camera = (ZoomCamera) gc.getCamera();
        camera.setChaseEntity(null);
        float cameraX = camera.getCenterX();
        float cameraY = camera.getCenterY();
        float difX;
        float difY;
        if(s.equals("mitte")){
            difX = cameraX - (GameConfig.CAMERA_X + GameConfig.CAMERA_WIDTH/2);
            difY = cameraY - (GameConfig.CAMERA_Y + GameConfig.CAMERA_HEIGHT/2);
        }else{
            difX = cameraX - (partner.getCannon().getX());
            difY = cameraY - (partner.getCannon().getY());
        }
        boolean rightPositionX = false;
        boolean rightPositionY = false;
        if(difX < -10){
            cameraX += Math.abs(difX)/5;
            camera.setCenter(cameraX, cameraY);
        }else if(difX > 10){
            cameraX -= Math.abs(difX)/5;
            camera.setCenter(cameraX, cameraY);
        }else{
            rightPositionX = true;
        }
        if(difY < -10){
            cameraY += Math.abs(difY)/5;
            camera.setCenter(cameraX, cameraY);
        }else if(difY > 10){
            cameraY -= Math.abs(difY)/5;
            camera.setCenter(cameraX, cameraY);
        }else{
            rightPositionY = true;
        }
        if(rightPositionX && rightPositionY && s.equals("mitte")){
            camera.setCenter(GameConfig.CAMERA_X + GameConfig.CAMERA_WIDTH/2, GameConfig.CAMERA_Y + GameConfig.CAMERA_HEIGHT/2);
            camera.setZoomFactor(GameConfig.CAMERA_STARTUP_ZOOM);
            followCamera = OFF;
        }else if(rightPositionX && rightPositionY && s.equals("gegner")){
            camera.setCenter(partner.getCannon().getX(), partner.getCannon().getY());
            camera.setZoomFactor(GameConfig.CAMERA_STARTUP_ZOOM);
            followCamera = OFF;
        }
    }

    @Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (gc.getPhysicsWorld() == null)
			return false;

		double cannonDistanceX = pSceneTouchEvent.getX() - this.me.getCannon().getX();
		double cannonDistanceY = pSceneTouchEvent.getY() - this.me.getCannon().getY();
		double cannonDistanceR = Math.sqrt(cannonDistanceX*cannonDistanceX + cannonDistanceY*cannonDistanceY);

		// TODO: refactor constant

		if (cannonDistanceR < rm.getAimCircleTexture().getHeight() &&
                ((isLeft && cannonDistanceX > 0) || (!isLeft && cannonDistanceX < 0))) {

			//
			// aim and fire
			//

			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();

			int iX = (int) x;
			int iY = (int) y;

			if (this.me.getCannon().pointAt(iX, iY)) {
				this.aimX = iX;
				this.aimY = iY;
			}

			if (pSceneTouchEvent.isActionUp() && this.status == GameStatus.MY_TURN) {
				this.me.handleTurn(this.aimX, this.aimY, null);
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
		ZoomCamera camera = (ZoomCamera) gc.getCamera();
		this.pinchZoomStartedCameraZoomFactor = camera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector,
							TouchEvent pTouchEvent, float pZoomFactor) {
		ZoomCamera camera = (ZoomCamera) gc.getCamera();

		float factor = this.pinchZoomStartedCameraZoomFactor * pZoomFactor;
		if (factor > GameConfig.CAMERA_ZOOM_MIN
				&& factor < GameConfig.CAMERA_ZOOM_MAX)
			camera.setZoomFactor(factor);
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector,
									TouchEvent pTouchEvent, float pZoomFactor) {
		ZoomCamera camera = (ZoomCamera) gc.getCamera();

		float factor = this.pinchZoomStartedCameraZoomFactor * pZoomFactor;
		if (factor > GameConfig.CAMERA_ZOOM_MIN
				&& factor < GameConfig.CAMERA_ZOOM_MAX)
			camera.setZoomFactor(factor);
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
								float pDistanceX, float pDistanceY) {
		ZoomCamera camera = (ZoomCamera) gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
						 float pDistanceX, float pDistanceY) {
		ZoomCamera camera = (ZoomCamera) gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

        //this.parallaxBackground.setParallaxValue(this.parallaxBackground.getParallaxValue() + camera.getCenterX() / 100);

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
								 float pDistanceX, float pDistanceY) {
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
						intent.putExtra("username", me.getName());
						intent.putExtra("partnername", partner.getName());
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

	private void turn() {

		Log.i(getClass().getName(), "turn()");

		this.hud.setStatus(getString(R.string.yourTurn));
		this.status = GameStatus.MY_TURN;

		this.me.getCannon().showAimCircle();

	}

	private void won() {

		Log.i(getClass().getName(), "won()");

		this.status = GameStatus.WON;

		Intent intent = new Intent(OnlineGameActivity.this, EndGameActivity.class);
		intent.putExtra("hasWon", true);
		intent.putExtra("isLeft", OnlineGameActivity.this.isLeft);
		intent.putExtra("username", me.getName());
		intent.putExtra("partnername", partner.getName());
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
		intent.putExtra("username", me.getName());
		intent.putExtra("partnername", partner.getName());
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
