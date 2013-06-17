package com.angrykings.cannons;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * Cannonball
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Cannonball extends PhysicalEntity {

	protected final TextureRegion ballTexture;
	protected final Body ballBody;
	protected final FixtureDef ballFixture;
	protected final Sprite ballSprite;

	public Cannonball(TextureRegion ballTexture, Vector2 position) {
		this(ballTexture, 5.5f, 0.1f, 0.9f, position);
	}

	public Cannonball(TextureRegion ballTexture, float density, float elasticity, float friction, Vector2 position) {
		GameContext gc = GameContext.getInstance();

		this.ballSprite = new Sprite(0, 0, ballTexture, gc.getVboManager());

		this.ballTexture = ballTexture;

		this.ballSprite.setPosition(position.x - this.ballTexture.getWidth() / 2, position.y - this.ballTexture.getHeight() / 2);

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
