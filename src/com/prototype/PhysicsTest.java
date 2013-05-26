package com.prototype;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import com.angrykings.Player;
import com.prototype.johann_hofmann.connectMe.KingServerConnection;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import android.hardware.SensorManager;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhysicsTest extends BaseGameActivity implements IOnSceneTouchListener {
	private static final int CAMERA_WIDTH = 960;
	private static final int CAMERA_HEIGHT = 540;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.9f, 0.1f, 0.9f);

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mCircleFaceTextureRegion;
	private TiledTextureRegion mTriangleFaceTextureRegion;
	private TiledTextureRegion mHexagonFaceTextureRegion;

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;
	private int mFaceCount = 0;

	private Player mPlayer1, mPlayer2;
	float mTouchDownX, mTouchDownY;
	float mTouchUpX, mTouchUpY;

	private Line mDragLine;

	private ArrayList<Body> mCannonballs;

	private int mRound;

	boolean mTurnSent = true;

	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Touch me :D", Toast.LENGTH_SHORT).show();

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, TextureOptions.BILINEAR);
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 2, 1); // 64x32
		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
		this.mTriangleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_triangle_tiled.png", 0, 64, 2, 1); // 64x32
		this.mHexagonFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_hexagon_tiled.png", 0, 96, 2, 1); // 64x32
		this.mBitmapTextureAtlas.load();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		ground.setColor(0.5f, 0.5f, 0.5f);
		roof.setColor(0.5f, 0.5f, 0.5f);
		left.setColor(0.5f, 0.5f, 0.5f);
		right.setColor(0.5f, 0.5f, 0.5f);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.1f, 1.0f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		KingServerConnection.getInstance().setHandler(new KingServerConnection.OnMessageHandler() {
			@Override
			public void onMessage(String payload) {
				try{
				JSONObject jObj = new JSONObject(payload);
					if (jObj.getString("action").equals("turn")) {
						Debug.d("message: "+payload);
						if(mRound % 2 == 1) {
							Body b = mPlayer2.fire(new Vector2(-45, -25));
							mCannonballs.add(b);
							mRound++;
							Debug.d("round: "+mRound);
						}
					}
				}catch (JSONException e) {

				}

			}
		});

		this.mScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				boolean ready = physicsReady();

//				if(ready && mRound % 2 == 1) {
//					Body b = mPlayer2.fire(new Vector2(-45, -25));
//					mCannonballs.add(b);
//					mRound++;
//					Debug.d("round: "+mRound);
//				}else
				if(ready && mRound % 2 == 0){
					if(!mTurnSent) {
						mRound++;
						KingServerConnection.getInstance().getmConnection().sendTextMessage("{\"action\":\"turn\"}");
						mTurnSent = true;
					}
				}
			}

			@Override
			public void reset() {
			}
		});

		final Vector2 gravity = Vector2Pool.obtain(0, 9.81f);
		this.mPhysicsWorld.setGravity(gravity);

		this.mDragLine = new Line(0, 0, 0, 0, 2, vertexBufferObjectManager);
		this.mDragLine.setColor(0.0f, 1.0f, 0.5f);

		this.mCannonballs = new ArrayList<Body>();

		Vector2 player1Pos = new Vector2(0,0);
		Vector2 player2Pos = new Vector2(0,0);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Boolean myTurn = extras.getBoolean("myTurn");
			if(myTurn){
				this.mRound = 0;
				player1Pos = new Vector2(200, 400);
				player2Pos = new Vector2(750, 400);
			}else{
				this.mRound = 1;
				player2Pos = new Vector2(200, 400);
				player1Pos = new Vector2(750, 400);
			}
		}

		this.mPlayer1 = new Player(player1Pos, this.mScene, vertexBufferObjectManager, this.mCircleFaceTextureRegion, this.mPhysicsWorld);
		this.mScene.attachChild(this.mPlayer1.getTexture());

		this.mPlayer2 = new Player(player2Pos, this.mScene, vertexBufferObjectManager, this.mCircleFaceTextureRegion, this.mPhysicsWorld);
		this.mScene.attachChild(this.mPlayer2.getTexture());

		this.addFace(450, 100);
		this.addFace(450, 200);

		pOnCreateSceneCallback.onCreateSceneFinished(this.mScene);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
				this.mTouchDownX = pSceneTouchEvent.getX();
				this.mTouchDownY = pSceneTouchEvent.getY();

				this.mDragLine.setPosition(this.mTouchDownX, this.mTouchDownY, this.mTouchDownX, this.mTouchDownY);

				this.mScene.attachChild(this.mDragLine);
			}else if(pSceneTouchEvent.isActionUp()) {
				this.mTouchUpX = pSceneTouchEvent.getX();
				this.mTouchUpY = pSceneTouchEvent.getY();

				boolean ready = this.physicsReady();
				Debug.d("physics ready: "+ready);

				if(ready) {
					mTurnSent = false;
					if(this.mRound % 2 == 0) {
						Vector2 force = new Vector2(
								(this.mTouchDownX-this.mTouchUpX),
								(this.mTouchDownY-this.mTouchUpY)
						);

						if(force.len() > 10) {
							mTurnSent = false;
							Body b = this.mPlayer1.fire(force);
							this.mCannonballs.add(b);

							Debug.d("round: "+mRound);
						}
					}else{
						Handler h = new Handler(getApplicationContext().getMainLooper());
						h.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "wait for player 2...", Toast.LENGTH_SHORT).show();
							}
						});
					}
				} else {
					Handler h = new Handler(getApplicationContext().getMainLooper());
					h.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "wait for physics...", Toast.LENGTH_SHORT).show();
						}
					});
				}

				this.mScene.detachChild(this.mDragLine);
			}else if(pSceneTouchEvent.isActionMove()) {
				this.mDragLine.setPosition(this.mTouchDownX, this.mTouchDownY, pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			}
		}
		return false;
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	private boolean physicsReady() {
		for(Body b : this.mCannonballs) {

			float linearVelocity = b.getLinearVelocity().len();
			float angularVelocity = b.getAngularVelocity();
//			b.get
//
//			if(linearVelocity > 0.5 && linearVelocity < 1 && angularVelocity < 1){
//				b.setLinearDamping(1);
//				b.setAngularDamping(1);
//			}
//
//			Debug.d("ang: "+angularVelocity);
			if(linearVelocity > 0 && linearVelocity < 2.5 && angularVelocity < 3){
				b.setAngularVelocity(0.0f);
			}else{
//				Debug.d("lin: "+b.getLinearVelocity().len()+" angular: "+b.getAngularVelocity());
			}

			if(linearVelocity > 0.01)
				return false;
		}

		return true;
	}

	private void addFace(final float pX, final float pY) {
		this.mFaceCount++;
		Debug.d("Faces: " + this.mFaceCount);

		final AnimatedSprite face;
		final Body body;

		face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
		face.setScale(2.0f);
		body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);


		this.mScene.attachChild(face);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
	}
}
