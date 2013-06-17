package com.angrykings;

import com.badlogic.gdx.physics.box2d.Body;
import org.andengine.entity.Entity;
import org.andengine.entity.shape.IAreaShape;
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
public abstract class PhysicalEntity {
	public abstract Body getBody();
	public abstract IAreaShape getAreaShape();

	public void registerPhysicsConnector() {
		GameContext gc = GameContext.getInstance();

		gc.getPhysicsWorld().registerPhysicsConnector(
				new PhysicsConnector(this.getAreaShape(), this.getBody(), true, true)
		);
	}
}
