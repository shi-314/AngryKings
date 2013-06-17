package com.angrykings;

import com.badlogic.gdx.physics.box2d.Body;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 14.06.13
 */
public abstract class PhysicalEntity extends Sprite {
	protected PhysicalEntity(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
	}

	public abstract Body getBody();

	public void registerPhysicsConnector() {
		GameContext gc = GameContext.getInstance();

		gc.getPhysicsWorld().registerPhysicsConnector(
				new PhysicsConnector(this, this.getBody(), true, true)
		);
	}
}
