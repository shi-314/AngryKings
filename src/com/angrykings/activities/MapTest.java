package com.angrykings.activities;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.angrykings.GameConfig;
import com.angrykings.GameContext;
import com.angrykings.cannons.Cannon;
import com.angrykings.cannons.Cannonball;
import com.angrykings.maps.BasicMap;
import com.badlogic.gdx.math.Vector2;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * MapTest
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 06.06.13
 */
public class MapTest extends BaseGameActivity implements IOnSceneTouchListener {

	private GameContext gc;

	//
	// Textures
	//

	private TiledTextureRegion grassTexture;
	private TextureRegion cannonTexture;
	private TextureRegion wheelTexture;
	private TextureRegion ballTexture;

	//
	// Game Objects
	//
	private Cannon cannon;


	@Override
	public EngineOptions onCreateEngineOptions() {
		gc = GameContext.getInstance();

		Camera camera = new Camera(0, 0, GameConfig.CAMERA_WIDTH, GameConfig.CAMERA_HEIGHT);

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

		this.grassTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				textureAtlas,
				this,
				"grass.png",
				0, 0,
				1, 1
		); // 32x32
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256, 72, TextureOptions.BILINEAR);
		this.cannonTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this, "cannon.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.wheelTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this, "wheel.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 44, 44, TextureOptions.BILINEAR);
		this.ballTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this, "ball.png", 0, 0);
		textureAtlas.load();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		gc = GameContext.getInstance();

		gc.setVboManager(this.getVertexBufferObjectManager());

		if(GameConfig.LOG_FPS)
			this.mEngine.registerUpdateHandler(new FPSLogger());

		//
		// initialize the scene
		//

		Scene scene = new Scene();
		scene.setBackground(new Background(0, 0, 0));
		scene.setOnSceneTouchListener(this);

		gc.setScene(scene);

		//
		// initialize the physics engine
		//

		PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), true);

		gc.setPhysicsWorld(physicsWorld);

		scene.registerUpdateHandler(physicsWorld);

		//
		// initialize the entities
		//

		scene.setBackground(new Background(0.7f, 0.7f, 1.0f));

		BasicMap map = new BasicMap(this.grassTexture);
		scene.attachChild(map);

		this.cannon = new Cannon(this.cannonTexture, this.wheelTexture, true);
		this.cannon.setPosition(300, 300);
		scene.attachChild(this.cannon);

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		gc = GameContext.getInstance();

		if(gc.getPhysicsWorld() == null)
			return false;

		float x = pSceneTouchEvent.getX();
		float y = pSceneTouchEvent.getY();

		if(pSceneTouchEvent.isActionUp()) {
			this.cannon.pointAt(x, y);
			Vector2 dir = this.cannon.getDirection();
			Cannonball ball = new Cannonball(this.ballTexture);
			ball.setPosition(200, 200);

			Vector2 force = this.cannon.getDirection().mul(10);
			Vector2 forcePosition = new Vector2(ball.getX()+10, ball.getY()+10);
			ball.getBody().applyForce(force, forcePosition);

			gc.getScene().attachChild(ball);
		}

		return false;
	}
}
