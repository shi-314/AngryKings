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
	protected final FixtureDef stoneFixture;
	
	public Stone(TiledTextureRegion stoneTexture, float x, float y) {
		
		this.stoneTexture = stoneTexture;
		GameContext gc = GameContext.getInstance();
		
		this.stoneSprite = new AnimatedSprite(x, y, this.stoneTexture, gc.getVboManager());
		stoneSprite.setCurrentTileIndex(2);
		
		this.stoneFixture = PhysicsFactory.createFixtureDef(0.9f, 0.1f, 0.9f);
		
		
		this.stoneBody = PhysicsFactory.createBoxBody(
				gc.getPhysicsWorld(),
				this.stoneSprite,
				//BodyDef.BodyType.DynamicBody,
				BodyDef.BodyType.DynamicBody,
				this.stoneFixture
		);

		
		//this.attachChild(stoneSprite);
	}
		
	
	
	@Override
	public Body getBody() {
		return stoneBody;  //To change body of implemented methods use File | Settings | File Templates.
	}



	@Override
	public IAreaShape getAreaShape() {
		// TODO Auto-generated method stub
		return this.stoneSprite;
	}
}
