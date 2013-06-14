package com.angrykings.cannons;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
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
	protected final Sprite ballSprite;
	protected final Body ballBody;
	protected final FixtureDef ballFixture;

	public Cannonball(TextureRegion ballTexture) {
		this(ballTexture, 5.5f, 0.1f, 0.9f);
	}

	public Cannonball(TextureRegion ballTexture, float density, float elasticity, float friction) {
		this.ballTexture = ballTexture;

		GameContext gc = GameContext.getInstance();

		this.ballSprite = new Sprite(-this.ballTexture.getWidth()/2, -this.ballTexture.getHeight()/2, this.ballTexture, gc.getVboManager());

		this.ballFixture = PhysicsFactory.createFixtureDef(density, elasticity, friction);

		this.ballBody = PhysicsFactory.createCircleBody(
				gc.getPhysicsWorld(),
				this.ballSprite,
				BodyDef.BodyType.DynamicBody,
				this.ballFixture
		);

		gc.getPhysicsWorld().registerPhysicsConnector(
				new PhysicsConnector(this.ballSprite, this.ballBody, true, true)
		);

		this.attachChild(ballSprite);
	}

	@Override
	public Body getBody() {
		return this.ballBody;
	}

}
