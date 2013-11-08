package com.angrykings.castles;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.angrykings.ResourceManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Wood extends PhysicalEntity {

	protected final TextureRegion woodTexture;
	protected final Sprite woodSprite;
	protected final Body woodBody;

	public final static FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.3f, 0.05f, 0.8f);

	private final static float LINEAR_DAMPING = 0.1f;
	private final static float ANGULAR_DAMPING = 0.1f;

	public Wood(float x, float y) {
		ResourceManager rm = ResourceManager.getInstance();

		this.woodTexture = rm.getWoodTexture();

		GameContext gc = GameContext.getInstance();

		this.woodSprite = new Sprite(
				x - this.woodTexture.getWidth() / 2,
				y - this.woodTexture.getHeight() / 2,
				this.woodTexture,
				gc.getVboManager()
		);

		this.woodBody = PhysicsFactory.createBoxBody(
				gc.getPhysicsWorld(), this.woodSprite,
				BodyDef.BodyType.DynamicBody,
				Wood.FIXTURE_DEF
		);

		this.woodBody.setLinearDamping(Wood.LINEAR_DAMPING);
		this.woodBody.setAngularDamping(Wood.ANGULAR_DAMPING);
	}

	@Override
	public Body getBody() {
		return woodBody;
	}

	@Override
	public IAreaShape getAreaShape() {
		return this.woodSprite;
	}

}
