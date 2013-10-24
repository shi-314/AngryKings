package com.angrykings.unused;

import android.app.Dialog;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import com.angrykings.*;
import com.angrykings.cannons.Cannon;
import com.angrykings.castles.Castle;
import com.angrykings.maps.BasicMap;
import com.badlogic.gdx.math.Vector2;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * MapTest
 * 
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 06.06.13
 */
public class LocalTestActivity extends BaseGameActivity implements
		IOnSceneTouchListener, IScrollDetectorListener,
		IPinchZoomDetectorListener {

	private GameContext gc;

	//
	// Textures
	//

	private TiledTextureRegion grassTexture;
	private TextureRegion cannonTexture;
	private TextureRegion wheelTexture;
	private TextureRegion ballTexture;
	private TiledTextureRegion skyTexture;
	private TiledTextureRegion aimButtonTexture;
	private TiledTextureRegion whiteFlagButtonTexture;
	private TiledTextureRegion stoneTexture;
	private TextureRegion roofTexture;
	private TextureRegion woodTexture;
	private Font statusFont;
	private Font playerNameFont;

	//
	// Game Objects
	//

	private Cannon cannon;
	private Cannon enemyCannon;
	private RepeatingSpriteBackground skySprite;
	private Castle castle;

	//
	// Navigation Attributes
	//

	private SurfaceScrollDetector scrollDetector;
	private PinchZoomDetector pinchZoomDetector;
	private float pinchZoomStartedCameraZoomFactor;
	boolean isAiming = true;

	private Handler handler;

	@Override
	public EngineOptions onCreateEngineOptions() {

		handler = new Handler();

		gc = GameContext.getInstance();

		ZoomCamera camera = new ZoomCamera(0, 0, GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT);

		camera.setZoomFactor(GameConfig.CAMERA_STARTUP_ZOOM);
		camera.setBounds(GameConfig.CAMERA_MIN_X, GameConfig.CAMERA_MIN_Y,
				GameConfig.CAMERA_MAX_X, GameConfig.CAMERA_MAX_Y);
		camera.setBoundsEnabled(true);

		gc.setCamera(camera);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(GameConfig.CAMERA_WIDTH,
						GameConfig.CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		//
		// map textures
		//

		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 50, 393, TextureOptions.BILINEAR);
		this.grassTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, this, "grass.png", 0, 0, 1,
						1); // 32x32
		textureAtlas.load();

		this.skySprite = new RepeatingSpriteBackground(GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT, this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(),
						"gfx/sky.png"), this.getVertexBufferObjectManager());

		//
		// cannon textures
		//

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256,
				72, TextureOptions.BILINEAR);
		this.cannonTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, this, "cannon.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128,
				128, TextureOptions.BILINEAR);
		this.wheelTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, this, "wheel.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 44, 44,
				TextureOptions.BILINEAR);
		this.ballTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, this, "ball.png", 0, 0);
		textureAtlas.load();

		//
		// hud textures
		//

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128,
				64, TextureOptions.BILINEAR);
		this.aimButtonTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, this, "aim_button.png", 0,
						0, 2, 1);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 64,
				TextureOptions.BILINEAR);
		this.whiteFlagButtonTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, this,
						"white_flag_button.png", 0, 0, 1, 1);
		textureAtlas.load();

		FontFactory.setAssetBasePath("font/");

		final ITexture statusFontTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.statusFont = FontFactory.createFromAsset(this.getFontManager(),
				statusFontTexture, this.getAssets(), "Plok.ttf", 22.0f, true,
				Color.BLACK);
		this.statusFont.load();

		final ITexture playerNameFontTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.playerNameFont = FontFactory.createFromAsset(
				this.getFontManager(), playerNameFontTexture, this.getAssets(),
				"Plok.ttf", 16.0f, true, Color.BLACK);
		this.playerNameFont.load();

		//
		// castle textures
		//

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 384,
				128, TextureOptions.BILINEAR);
		this.stoneTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, this, "stones.png", 0, 0,
						3, 1);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128,
				128, TextureOptions.BILINEAR);
		this.roofTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, this, "roof.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 409,
				50, TextureOptions.BILINEAR);
		this.woodTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, this, "wood.png", 0, 0);
		textureAtlas.load();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {

		gc = GameContext.getInstance();

		gc.setVboManager(this.getVertexBufferObjectManager());

		if (GameConfig.LOG_FPS)
			this.mEngine.registerUpdateHandler(new FPSLogger());

		//
		// initialize the scene
		//

		Scene scene = new Scene();
		scene.setBackground(this.skySprite);
		scene.setOnSceneTouchListener(this);

		gc.setScene(scene);

		//
		// initialize the physics engine
		//

		PhysicsWorld physicsWorld = new FixedStepPhysicsWorld(30, new Vector2(
				0, SensorManager.GRAVITY_EARTH), true, 3, 2);
		physicsWorld.setAutoClearForces(true);
		gc.setPhysicsWorld(physicsWorld);

		//
		// initialize the entities
		//

		BasicMap map = new BasicMap();
		scene.attachChild(map);

		Boolean amILeft = false;

		int myX = 1500;
		int myY = 890;
		int enemyX = -400;
		int enemyY = 890;

		this.cannon = new Cannon(amILeft);
		this.cannon.setPosition(myX, myY);
		scene.attachChild(this.cannon);

		this.enemyCannon = new Cannon(!amILeft);
		this.enemyCannon.setPosition(enemyX, enemyY);
		scene.attachChild(this.enemyCannon);

		castle = new Castle(400, BasicMap.GROUND_Y);

		// castle = new Castle(100, BasicMap.GROUND_Y, this.stoneTexture,
		// this.roofTexture, this.woodTexture);
		// castle = new Castle(300, BasicMap.GROUND_Y - 500, this.stoneTexture,
		// this.roofTexture, this.woodTexture);

		//
		// initialize navigation
		//

		this.scrollDetector = new SurfaceScrollDetector(this);
		this.pinchZoomDetector = new PinchZoomDetector(this);

		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		final GameHUD hud = new GameHUD();
		hud.setOnAimTouched(new Runnable() {
			@Override
			public void run() {
				isAiming = !isAiming;
			}
		});
		hud.setOnWhiteFlagTouched(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						final Dialog dialog = new Dialog(LocalTestActivity.this);
						dialog.setContentView(R.layout.quit_dialog);
						dialog.setTitle("Test");
						dialog.setCancelable(true);

						
						Button button = (Button) dialog
								.findViewById(R.id.bCancel);
						Button button2 = (Button) dialog
								.findViewById(R.id.bResign);

						button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						button2.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();
							}
						});
						dialog.show();
					}
				});
			}
		});

		gc.setHud(hud);
		gc.getCamera().setHUD(hud);

		final float initialCastleHeight = castle.getInitialHeight();

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				hud.getLeftLifeBar().setValue(
						castle.getHeight() / initialCastleHeight);
			}

			@Override
			public void reset() {

			}
		});

		hud.setLeftPlayerName("Shivan");
		hud.setRightPlayerName("Ray");
		hud.setStatus("Du bist dran!");
		scene.registerUpdateHandler(physicsWorld);
		scene.registerUpdateHandler(PhysicsManager.getInstance());

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		gc = GameContext.getInstance();

		if (gc.getPhysicsWorld() == null)
			return false;

		if (this.isAiming) {
			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();

			this.cannon.pointAt((int) x, (int) y);

			if (pSceneTouchEvent.isActionUp()) {
				this.cannon.fire(400);
			}
		} else {
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
}
