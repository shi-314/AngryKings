package com.angrykings.cannons;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Cannonball
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Cannonball extends PhysicalEntity {

	private final TextureRegion ballTexture;
	private final Body ballBody;
	private final FixtureDef ballFixture;
	private final Sprite ballSprite;

	public Cannonball(TextureRegion ballTexture, float x, float y) {
		this(ballTexture, 5.5f, 0.1f, 0.9f, x, y);
		this.autoRemove = true;
	}

	public Cannonball(TextureRegion ballTexture, float density, float elasticity, float friction, float x, float y) {
		GameContext gc = GameContext.getInstance();

		this.ballTexture = ballTexture;

		this.ballSprite = new Sprite(
				x - this.ballTexture.getWidth() / 2,
				y - this.ballTexture.getHeight() / 2,
				ballTexture,
				gc.getVboManager()
		);

		this.ballFixture = PhysicsFactory.createFixtureDef(density, elasticity, friction);

		this.ballBody = PhysicsFactory.createCircleBody(
				gc.getPhysicsWorld(),
				this.ballSprite,
				BodyDef.BodyType.DynamicBody,
				this.ballFixture
		);

		this.ballBody.setLinearDamping(0.05f);
		this.ballBody.setAngularDamping(0.9f);
	}

	@Override
	public Body getBody() {
		return this.ballBody;
	}

	@Override
	public IAreaShape getAreaShape() {
		return this.ballSprite;
	}

}
