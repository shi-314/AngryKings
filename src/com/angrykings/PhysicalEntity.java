package com.angrykings;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.extension.physics.box2d.PhysicsConnector;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * AngryKings
 * 
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 14.06.13
 */
public abstract class PhysicalEntity {
	private Runnable onRemove = null;
	protected boolean autoRemove = false;

	public abstract Body getBody();

	public abstract IAreaShape getAreaShape();

	public void registerPhysicsConnector() {
		GameContext gc = GameContext.getInstance();

		gc.getPhysicsWorld().registerPhysicsConnector(
				new PhysicsConnector(this.getAreaShape(), this.getBody(), true,
						true));
	}

	public boolean isAutoRemoveEnabled() {
		return this.autoRemove;
	}

	public void setOnRemove(Runnable onRemove) {
		this.onRemove = onRemove;
	}

	public void remove() {
		if(this.onRemove != null)
			this.onRemove.run();

		// TODO this needs to be run in runOnUpdateThread(), dont know why it is not crashing right now
		GameContext gc = GameContext.getInstance();

		this.getAreaShape().setVisible(false);
		this.getAreaShape().detachSelf();
		this.getAreaShape().clearUpdateHandlers();
		gc.getPhysicsWorld().unregisterPhysicsConnector(
				gc.getPhysicsWorld().getPhysicsConnectorManager()
						.findPhysicsConnectorByShape(this.getAreaShape()));
		gc.getPhysicsWorld().destroyBody(this.getBody());
		this.getAreaShape().dispose();
	}
}
