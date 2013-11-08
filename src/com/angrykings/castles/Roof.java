package com.angrykings.castles;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.angrykings.ResourceManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

public class Roof extends PhysicalEntity {

	protected final TextureRegion roofTexture;
	protected final Sprite roofSprite;
	protected final Body roofBody;

	public final static FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.4f, 0.2f, 0.5f);

	private final static float LINEAR_DAMPING = 0.1f;
	private final static float ANGULAR_DAMPING = 0.1f;

	public Roof(float x, float y) {
		ResourceManager rm = ResourceManager.getInstance();

		this.roofTexture = rm.getRoofTexture();

		GameContext gc = GameContext.getInstance();

		this.roofSprite = new Sprite(
				x - this.roofTexture.getWidth() / 2,
				y - this.roofTexture.getHeight() / 2,
				this.roofTexture, gc.getVboManager()
		);

		this.roofBody = createTriangleBody(gc.getPhysicsWorld(), roofSprite, BodyDef.BodyType.DynamicBody, Roof.FIXTURE_DEF);
	}

	@Override
	public Body getBody() {
		return roofBody;
	}

	/**
	 * Creates a Body based on a PolygonShape in the form of a triangle:
	 */
	private static Body createTriangleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
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

		Body body = PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);

		body.setLinearDamping(Roof.LINEAR_DAMPING);
		body.setAngularDamping(Roof.ANGULAR_DAMPING);

		return  body;
	}

	@Override
	public IAreaShape getAreaShape() {
		return roofSprite;
	}

}
