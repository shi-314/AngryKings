package com.angrykings.castles;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
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

	public final static FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1.0f, 0.1f, 0.9f);

	public Stone(TiledTextureRegion stoneTexture, float x, float y) {

		this.stoneTexture = stoneTexture;
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
