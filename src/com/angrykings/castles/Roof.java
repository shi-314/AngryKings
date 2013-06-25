package com.angrykings.castles;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Roof extends PhysicalEntity {

	protected final TextureRegion roofTexture;
	protected final Sprite roofSprite;
	protected final Body roofBody;

	public final static FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.7f, 0.2f, 0.5f);

	public Roof(TextureRegion roofTexture, float x, float y) {
		this.roofTexture = roofTexture;

		GameContext gc = GameContext.getInstance();

		this.roofSprite = new Sprite(x, y, this.roofTexture, gc.getVboManager());
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

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	@Override
	public IAreaShape getAreaShape() {
		return roofSprite;
	}

}
