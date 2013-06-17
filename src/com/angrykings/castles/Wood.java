package com.angrykings.castles;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Wood extends PhysicalEntity{

	protected final TextureRegion woodTexture;
	protected final Sprite woodSprite;
	protected final Body woodBody;
	protected final FixtureDef woodFixture;
	
	public Wood(TextureRegion woodTexture, float x, float y){
		this.woodTexture = woodTexture;

		GameContext gc = GameContext.getInstance();

		this.woodSprite = new Sprite(x,y, this.woodTexture,
				gc.getVboManager());

		this.woodFixture = PhysicsFactory.createFixtureDef(0.9f, 0.1f, 0.9f);

		this.woodBody = PhysicsFactory
				.createBoxBody(gc.getPhysicsWorld(), this.woodSprite,
						BodyDef.BodyType.DynamicBody, this.woodFixture);


		//this.attachChild(woodSprite);
	}

	@Override
	public Body getBody() {
		// TODO Auto-generated method stub
		return woodBody;
	}

	@Override
	public IAreaShape getAreaShape() {
		// TODO Auto-generated method stub
		return this.woodSprite;
	}
	
}
