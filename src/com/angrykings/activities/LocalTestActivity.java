package com.angrykings.activities;

import com.angrykings.*;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;

import com.angrykings.cannons.Cannon;
import com.angrykings.castles.Castle;
import com.angrykings.maps.BasicMap;
import com.badlogic.gdx.math.Vector2;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;

/**
 * MapTest
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 06.06.13
 */
public class LocalTestActivity extends BaseGameActivity implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener {

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

	@Override
	public EngineOptions onCreateEngineOptions() {
		gc = GameContext.getInstance();

		ZoomCamera camera = new ZoomCamera(0, 0, GameConfig.CAMERA_WIDTH, GameConfig.CAMERA_HEIGHT);
		camera.setZoomFactor(0.3f);

		gc.setCamera(camera);

		return new EngineOptions(
				true,
				ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(GameConfig.CAMERA_WIDTH, GameConfig.CAMERA_HEIGHT),
				camera
		);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		//
		// tile set for the map
		//

		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);

		this.grassTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, this, "grass.png", 0, 0, 1, 1); // 32x32
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256, 72, TextureOptions.BILINEAR);
		this.cannonTexture = BitmapTextureAtlasTextureRegionFactory .createFromAsset(textureAtlas, this, "cannon.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.wheelTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this, "wheel.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 44, 44, TextureOptions.BILINEAR);
		this.ballTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this, "ball.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 160, 80, TextureOptions.BILINEAR);
		this.aimButtonTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, this, "aim_button.png", 0, 0, 2, 1);
		textureAtlas.load();

		this.skySprite = new RepeatingSpriteBackground(
				GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT, this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(), "gfx/sky.png"),
				this.getVertexBufferObjectManager()
		);

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

		PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		physicsWorld.setAutoClearForces(true);
		gc.setPhysicsWorld(physicsWorld);

		scene.registerUpdateHandler(physicsWorld);

		//
		// initialize the entities
		//

		BasicMap map = new BasicMap(this.grassTexture, this.skyTexture);
		scene.attachChild(map);

		Boolean amILeft = false;

		int myX = 1500;
		int myY = 890;
		int enemyX = -400;
		int enemyY = 890;

		this.cannon = new Cannon(this.cannonTexture, this.wheelTexture, this.ballTexture, amILeft);
		this.cannon.setPosition(myX, myY);
		scene.attachChild(this.cannon);

		this.enemyCannon = new Cannon(this.cannonTexture, this.wheelTexture, this.ballTexture, !amILeft);
		this.enemyCannon.setPosition(enemyX, enemyY);
		scene.attachChild(this.enemyCannon);

		scene.registerUpdateHandler(PhysicsManager.getInstance());

		//
		// initialize navigation
		//

		this.scrollDetector = new SurfaceScrollDetector(this);
		this.pinchZoomDetector = new PinchZoomDetector(this);

		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		final GameHUD hud = new GameHUD(this.aimButtonTexture);

		gc.setHud(hud);

		hud.setOnAimTouched(new Runnable() {
			@Override
			public void run() {
				isAiming = !isAiming;
			}
		});

		gc.getCamera().setHUD(hud);

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

		if(this.isAiming) {
			float x = pSceneTouchEvent.getX();
			float y = pSceneTouchEvent.getY();

			this.cannon.pointAt(x, y);

			if (pSceneTouchEvent.isActionUp()) {
				this.cannon.fire(200);
			}
		}else{
			if(pSceneTouchEvent.isActionDown()) {
				this.scrollDetector.setEnabled(true);
			}

			this.pinchZoomDetector.onTouchEvent(pSceneTouchEvent);

			if(this.pinchZoomDetector.isZooming()) {
				this.scrollDetector.setEnabled(false);
			}else{
				this.scrollDetector.onTouchEvent(pSceneTouchEvent);
			}
		}

		return true;
	}

	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector, TouchEvent pSceneTouchEvent) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera)gc.getCamera();
		this.pinchZoomStartedCameraZoomFactor = camera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera)gc.getCamera();
		camera.setZoomFactor(this.pinchZoomStartedCameraZoomFactor * pZoomFactor);
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera)gc.getCamera();
		camera.setZoomFactor(this.pinchZoomStartedCameraZoomFactor * pZoomFactor);
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera)gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera)gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		GameContext gc = GameContext.getInstance();
		ZoomCamera camera = (ZoomCamera)gc.getCamera();
		final float zoomFactor = camera.getZoomFactor();

		camera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}
}
