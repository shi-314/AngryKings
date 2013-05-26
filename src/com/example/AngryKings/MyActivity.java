package com.example.AngryKings;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
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


public class MyActivity extends BaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {
	private static final int CAMERA_WIDTH = 960;
	private static final int CAMERA_HEIGHT = 540;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.9f, 0.1f, 0.8f);

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mCircleFaceTextureRegion;
	private TiledTextureRegion mTriangleFaceTextureRegion;
	private TiledTextureRegion mHexagonFaceTextureRegion;

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;
	private int mFaceCount = 0;

	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Touch me :D", Toast.LENGTH_LONG).show();

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

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

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
				this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX()*2, pAccelerationData.getY()*2);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void addFace(final float pX, final float pY) {
		this.mFaceCount++;
		Debug.d("Faces: " + this.mFaceCount);

		final AnimatedSprite face;
		final Body body;

		if(this.mFaceCount % 4 == 0) {
			face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
			face.setScale(2.0f);
			body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
		} else if (this.mFaceCount % 4 == 1) {
			face = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
			body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
		} else if (this.mFaceCount % 4 == 2) {
			face = new AnimatedSprite(pX, pY, this.mTriangleFaceTextureRegion, this.getVertexBufferObjectManager());
			body = MyActivity.createTriangleBody(this.mPhysicsWorld, face, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
		} else {
			face = new AnimatedSprite(pX, pY, this.mHexagonFaceTextureRegion, this.getVertexBufferObjectManager());
			body = MyActivity.createHexagonBody(this.mPhysicsWorld, face, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
		}

		face.animate(500);

		this.mScene.attachChild(face);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));

		body.applyLinearImpulse(10, 0, 5, 5);
	}

	/**
	 * Creates a {@link Body} based on a {@link com.badlogic.gdx.physics.box2d.PolygonShape} in the form of a triangle:
	 * <pre>
	 *  /\
	 * /__\
	 * </pre>
	 */
	private static Body createTriangleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyDef.BodyType pBodyType, final FixtureDef pFixtureDef) {
		/* Remember that the vertices are relative to the center-coordinates of the Shape. */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		final float top = -halfHeight;
		final float bottom = halfHeight;
		final float left = -halfHeight;
		final float centerX = 0;
		final float right = halfWidth;

		final Vector2[] vertices = {
				new Vector2(centerX, top),
				new Vector2(right, bottom),
				new Vector2(left, bottom)
		};

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	/**
	 * Creates a {@link Body} based on a {@link com.badlogic.gdx.physics.box2d.PolygonShape} in the form of a hexagon:
	 * <pre>
	 *  /\
	 * /  \
	 * |  |
	 * |  |
	 * \  /
	 *  \/
	 * </pre>
	 */
	private static Body createHexagonBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyDef.BodyType pBodyType, final FixtureDef pFixtureDef) {
		/* Remember that the vertices are relative to the center-coordinates of the Shape. */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		/* The top and bottom vertex of the hexagon are on the bottom and top of hexagon-sprite. */
		final float top = -halfHeight;
		final float bottom = halfHeight;

		final float centerX = 0;

		/* The left and right vertices of the heaxgon are not on the edge of the hexagon-sprite, so we need to inset them a little. */
		final float left = -halfWidth + 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float right = halfWidth - 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float higher = top + 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float lower = bottom - 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;

		final Vector2[] vertices = {
				new Vector2(centerX, top),
				new Vector2(right, higher),
				new Vector2(right, lower),
				new Vector2(centerX, bottom),
				new Vector2(left, lower),
				new Vector2(left, higher)
		};

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
