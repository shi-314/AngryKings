package com.angrykings.cannons;

import com.angrykings.GameContext;
import com.angrykings.PhysicsManager;
import com.badlogic.gdx.math.Vector2;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

/**
 * Cannon
 * 
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Cannon extends Entity {
	protected final TextureRegion cannonTexture;
	protected final TextureRegion wheelTexture;
	protected final TextureRegion cannonballTexture;

	protected Sprite barrelSprite, wheelSprite;

	protected final boolean isLeft;
	protected final float minAngle, maxAngle;

	public Cannon(TextureRegion cannonTexture, TextureRegion wheelTexture,
			TextureRegion cannonballTexture, boolean isLeft) {
		this.cannonTexture = cannonTexture;
		this.wheelTexture = wheelTexture;
		this.cannonballTexture = cannonballTexture;
		this.isLeft = isLeft;

		GameContext gc = GameContext.getInstance();

		this.wheelSprite = new Sprite(0, 0, this.wheelTexture,
				gc.getVboManager());
		this.barrelSprite = new Sprite(0, 0, this.cannonTexture,
				gc.getVboManager());
		this.barrelSprite.setRotationCenter(60.0f, 72.0f);

		this.attachChild(this.barrelSprite);
		this.attachChild(this.wheelSprite);

		if (!isLeft) {
			this.setScale(-1.0f, 1.0f);
			this.minAngle = 280;
			this.maxAngle = 360;
		} else {
			this.minAngle = -80;
			this.maxAngle = 0;
		}

		this.setPosition(100, 300);
	}

	public void pointAt(float x, float y) {
		double rotation = Math.atan2(
				y - this.getY() - this.barrelSprite.getHeight() / 2,
				x - this.getX());

		rotation = Math.toDegrees(rotation);

		if (!this.isLeft)
			rotation = -rotation + 180;

		if (rotation > this.minAngle && rotation < maxAngle)
			this.barrelSprite.setRotation((float) rotation);
	}

	public Vector2 getDirection() {
		float angle = (float) Math.toRadians((double) this.barrelSprite
				.getRotation());
		if (this.isLeft) {
			return new Vector2((float) Math.cos(angle), (float) Math.sin(angle))
					.nor();
		} else {
			return new Vector2( - (float) Math.cos(angle), (float) Math.sin(angle))
			.nor();
		}
	}

	private Vector2 getBarrelEndPosition() {
		if (this.isLeft) {
			return new Vector2(this.getX(), this.getY() + 36).add(this
					.getDirection().mul(256));
		} else {
			return new Vector2(this.getX(), this.getY() + 36).add(this
					.getDirection().mul(256));
		}
	}

	public Cannonball fire(float force) {
		GameContext gc = GameContext.getInstance();

		Vector2 ballPosition = this.getBarrelEndPosition();

		Cannonball ball = new Cannonball(this.cannonballTexture, ballPosition.x, ballPosition.y);
		ball.registerPhysicsConnector();

		ball.getBody().applyLinearImpulse(this.getDirection().mul(force),
				ball.getBody().getPosition());
		ball.getAreaShape().setPosition(ballPosition.x, ballPosition.y);

		gc.getScene().attachChild(ball.getAreaShape());

		PhysicsManager.getInstance().addPhysicalEntity(ball);

		return ball;
	}
}
