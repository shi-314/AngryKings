package com.angrykings.cannons;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.angrykings.ResourceManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;

/**
 * Cannonball
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Cannonball extends PhysicalEntity {

	private final Body ballBody;
	private final FixtureDef ballFixture;
	private final Sprite ballSprite;

	private static final float DEFAULT_LINEAR_DAMPING = 0.05f;
	private static final float DEFAULT_ANGULAR_DAMPING = 0.09f;

	private static final FixtureDef DEFAULT_FIXTURE_DEF = PhysicsFactory.createFixtureDef(5.5f, 0.1f, 0.9f);

	public Cannonball(float x, float y) {
		this(x, y, Cannonball.DEFAULT_FIXTURE_DEF, Cannonball.DEFAULT_LINEAR_DAMPING, Cannonball.DEFAULT_ANGULAR_DAMPING);
		this.autoRemove = true;
	}

	public Cannonball(float x, float y, FixtureDef fixtureDef, float linearDamping, float angularDamping) {
		GameContext gc = GameContext.getInstance();
		ResourceManager rm = ResourceManager.getInstance();

		TextureRegion ballTexture = rm.getBallTexture();

		this.ballSprite = new Sprite(
				x - ballTexture.getWidth() / 2,
				y - ballTexture.getHeight() / 2,
				ballTexture,
				gc.getVboManager()
		);

		this.ballFixture = fixtureDef;

		this.ballBody = PhysicsFactory.createCircleBody(
				gc.getPhysicsWorld(),
				this.ballSprite,
				BodyDef.BodyType.DynamicBody,
				this.ballFixture
		);

		this.ballBody.setLinearDamping(linearDamping);
		this.ballBody.setAngularDamping(angularDamping);
        this.ballBody.setBullet(true);
        this.ballBody.setUserData("Kugel");
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
