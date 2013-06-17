package com.angrykings.activities;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.angrykings.GameConfig;
import com.angrykings.GameContext;
import com.angrykings.PhysicsManager;
import com.angrykings.cannons.Cannon;
import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;
import com.angrykings.maps.BasicMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

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
	private TiledTextureRegion skyTexture;

	//
	// Game Objects
	//

	private Cannon cannon;
	private RepeatingSpriteBackground skySprite;
	private Castle castle;


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


		this.skySprite = new RepeatingSpriteBackground(
				GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT,
				this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(), "gfx/sky.png"),
				this.getVertexBufferObjectManager()
		);

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

		this.cannon = new Cannon(this.cannonTexture, this.wheelTexture, this.ballTexture, true);
		this.cannon.setPosition(-400, 890);
		scene.attachChild(this.cannon);
		
		castle = new Castle(this, 400, 890);
		castle.createTextures();
		castle.createSinglePieces();
		

		FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.9f, 0.1f, 0.9f);
		Rectangle box = new Rectangle(200, 0, 100, 100, getVertexBufferObjectManager());
		box.setColor(1,0,0,1);
		Body body = PhysicsFactory.createBoxBody(physicsWorld, box, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
		scene.attachChild(box);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(box, body, true, true));
		scene.registerUpdateHandler(PhysicsManager.getInstance());

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

		this.cannon.pointAt(x, y);

		if(pSceneTouchEvent.isActionUp()) {
			this.cannon.fire(200);
		}else if(pSceneTouchEvent.isActionMove()) {
			Debug.d("move : "+x+", "+y);
			gc.getCamera().setCenter(x, y);
		}

		return false;
	}
}
