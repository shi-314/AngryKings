package com.angrykings.castles;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.angrykings.ResourceManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * CastleBlock
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Stone extends PhysicalEntity {

	protected final TiledTextureRegion stoneTexture;
	protected AnimatedSprite stoneSprite;
	protected final Body stoneBody;

	public final static FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.2f, 0.1f, 0.9f);

	private final static float LINEAR_DAMPING = 0.1f;
	private final static float ANGULAR_DAMPING = 0.1f;

	public Stone(float x, float y) {
		ResourceManager rm = ResourceManager.getInstance();
		this.stoneTexture = rm.getStoneTexture();
		GameContext gc = GameContext.getInstance();

		this.stoneSprite = new AnimatedSprite(
				x - this.stoneTexture.getWidth() / 2,
				y - this.stoneTexture.getHeight() / 2,
				this.stoneTexture,
				gc.getVboManager()
		);

		stoneSprite.setCurrentTileIndex(0);

		this.stoneBody = PhysicsFactory.createBoxBody(
				gc.getPhysicsWorld(),
				this.stoneSprite,
				BodyDef.BodyType.DynamicBody,
				Stone.FIXTURE_DEF
		);

		this.stoneBody.setLinearDamping(Stone.LINEAR_DAMPING);
		this.stoneBody.setAngularDamping(Stone.ANGULAR_DAMPING);
        this.stoneBody.setUserData("Stein");
	}


	@Override
	public Body getBody() {
		return stoneBody;
	}

	@Override
	public IAreaShape getAreaShape() {
		return this.stoneSprite;
	}
}
