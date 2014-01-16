package com.angrykings;

import org.andengine.entity.Entity;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.ui.activity.BaseGameActivity;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * AngryKings
 * 
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 14.06.13
 */
public abstract class PhysicalEntity {

	public static int CURRENT_ID = 0;

	public final int id;
	private Runnable onRemove = null;
	protected boolean autoRemove = false;

	private PhysicsConnector connector = null;

	protected PhysicalEntity() {
		this.id = PhysicalEntity.CURRENT_ID++;
	}

	public abstract Body getBody();

	public abstract IAreaShape getAreaShape();

	
	public void registerPhysicsConnector() {
		GameContext gc = GameContext.getInstance();

		this.connector = new PhysicsConnector(this.getAreaShape(), this.getBody(), true, true);

		gc.getPhysicsWorld().registerPhysicsConnector(this.connector);
	}

	public void unregisterPhysicsConnector() {
		GameContext gc = GameContext.getInstance();
		gc.getPhysicsWorld().unregisterPhysicsConnector(this.connector);
	}

	public PhysicsConnector getPhysicsConnector() {
		return connector;
	}

	public boolean isAutoRemoveEnabled() {
		return this.autoRemove;
	}

	public void setOnRemove(Runnable onRemove) {
		this.onRemove = onRemove;
	}

	public void remove() {
		BaseGameActivity gameActivity = GameContext.getInstance().getGameActivity();

		gameActivity.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				if(PhysicalEntity.this.onRemove != null)
					PhysicalEntity.this.onRemove.run();

				// TODO this needs to be run in runOnUpdateThread(), dont know why it is not crashing right now
				GameContext gc = GameContext.getInstance();

				PhysicalEntity.this.getAreaShape().setVisible(false);
				PhysicalEntity.this.getAreaShape().detachSelf();
				PhysicalEntity.this.getAreaShape().clearUpdateHandlers();
				gc.getPhysicsWorld().unregisterPhysicsConnector(
						gc.getPhysicsWorld().getPhysicsConnectorManager()
								.findPhysicsConnectorByShape(PhysicalEntity.this.getAreaShape()));
				gc.getPhysicsWorld().destroyBody(PhysicalEntity.this.getBody());
				PhysicalEntity.this.getAreaShape().dispose();
			}
		});
	}

	public int getId() {
		return this.id;
	}

    public KeyframeData getKeyframeData() {

        Body body = this.getBody();

        KeyframeData data = new KeyframeData();

        data.entityId = this.getId();
        data.position = new Vector2(body.getPosition());
        data.angle = body.getAngle();

        return data;

    }

    public void setKeyframeData(KeyframeData data) {

        this.getBody().setTransform(data.position, data.angle);

    }

}
