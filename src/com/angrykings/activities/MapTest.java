package com.angrykings.activities;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
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
import org.andengine.ui.activity.BaseGameActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.angrykings.Action;
import com.angrykings.GameConfig;
import com.angrykings.GameContext;
import com.angrykings.PhysicsManager;
import com.angrykings.ServerConnection;
import com.angrykings.cannons.Cannon;
import com.angrykings.castles.Castle;
import com.angrykings.maps.BasicMap;
import com.angrykings.utils.ServerJSONBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * MapTest
 * 
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 06.06.13
 */
public class MapTest extends BaseGameActivity implements IOnSceneTouchListener {

	private GameContext gc;

	private int mRound;

	boolean mTurnSent = false;

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
	private Cannon enemyCannon;
	private RepeatingSpriteBackground skySprite;
	private Castle castle;

	@Override
	public EngineOptions onCreateEngineOptions() {
		gc = GameContext.getInstance();

		ZoomCamera camera = new ZoomCamera(0, 0, GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT);
		camera.setZoomFactor(0.3f);

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
		// tile set for the map
		//

		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);

		this.grassTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, this, "grass.png", 0, 0, 1,
						1); // 32x32
		textureAtlas.load();

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

		this.skySprite = new RepeatingSpriteBackground(GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT, this.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(this.getAssets(),
						"gfx/sky.png"), this.getVertexBufferObjectManager());

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		ServerConnection.getInstance().setHandler(
				new ServerConnection.OnMessageHandler() {
					@Override
					public void onMessage(String payload) {
						try {
							JSONObject jObj = new JSONObject(payload);
							if (jObj.getInt("action") == Action.Server.TURN
									&& mRound % 2 == 1) {
								mRound++;
								MapTest.this.enemyCannon.pointAt(
										Float.parseFloat(jObj.getString("x")),
										Float.parseFloat(jObj.getString("y")));
								MapTest.this.enemyCannon.fire(200);
								mTurnSent = false;

							}else if(jObj.getInt("action") == Action.Server.PARTNER_LEFT){
								Intent intent = new Intent(
										MapTest.this,
										LobbyActivity.class);
								startActivity(intent);
							}
						} catch (JSONException e) {

						}

					}
				});

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

		PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_EARTH), false);

		physicsWorld.setAutoClearForces(true);
		gc.setPhysicsWorld(physicsWorld);

		scene.registerUpdateHandler(physicsWorld);

		//
		// initialize the entities
		//

		BasicMap map = new BasicMap(this.grassTexture, this.skyTexture);
		scene.attachChild(map);

		Boolean amILeft = false;

		int myX = 0;
		int myY = 0;
		int enemyX = 0;
		int enemyY = 0;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Boolean myTurn = extras.getBoolean("myTurn");
			if (myTurn) {
				this.mRound = 0;
				amILeft = true;
				myX = -400;
				myY = 890;
				enemyX = 1500;
				enemyY = 890;
			} else {
				this.mRound = 1;
				amILeft = false;
				enemyX = -400;
				enemyY = 890;
				myX = 1500;
				myY = 890;
			}
		}

		this.cannon = new Cannon(this.cannonTexture, this.wheelTexture,
				this.ballTexture, amILeft);
		this.cannon.setPosition(myX, myY);
		scene.attachChild(this.cannon);

		this.enemyCannon = new Cannon(this.cannonTexture, this.wheelTexture,
				this.ballTexture, !amILeft);
		this.enemyCannon.setPosition(enemyX, enemyY);
		scene.attachChild(this.enemyCannon);

		castle = new Castle(this, 400, 890);
		castle.createTextures();
		castle.createSinglePieces();

		FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.9f, 0.1f,
				0.9f);
		Rectangle box = new Rectangle(200, 0, 100, 100,
				getVertexBufferObjectManager());
		box.setColor(1, 0, 0, 1);
		Body body = PhysicsFactory.createBoxBody(physicsWorld, box,
				BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
		scene.attachChild(box);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(box, body,
				true, true));
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
		if (!mTurnSent && mRound % 2 == 0) {
			gc = GameContext.getInstance();

			if (gc.getPhysicsWorld() == null)
				return false;

			float x = Math.round(pSceneTouchEvent.getX());
			float y = Math.round(pSceneTouchEvent.getY());

			this.cannon.pointAt(x, y);

			if (pSceneTouchEvent.isActionUp()) {
				mRound++;
				this.cannon.fire(200);
				ServerConnection
						.getInstance()
						.getConnection()
						.sendTextMessage(
								new ServerJSONBuilder()
										.create(Action.Client.TURN)
										.option("x", String.valueOf(x))
										.option("y", String.valueOf(y)).build());
				mTurnSent = true;
			}
		}

		return false;
	}
}
