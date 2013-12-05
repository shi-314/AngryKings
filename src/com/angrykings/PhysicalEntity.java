package com.angrykings;

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
public abstract class PhysicalEntity implements IJsonSerializable {

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

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("x", this.getAreaShape().getX());
		json.put("y", this.getAreaShape().getY());
		json.put("rotation", this.getBody().getAngle());
		json.put("linearVelocityX", this.getBody().getLinearVelocity().x);
		json.put("linearVelocityY", this.getBody().getLinearVelocity().y);
		json.put("angularVelocity", this.getBody().getAngularVelocity());

		return json;
	}

	@Override
	public void fromJson(JSONObject json) throws JSONException {
		final float x = (float) json.getDouble("x");
		final float y = (float) json.getDouble("y");

		final float rotation = (float) json.getDouble("rotation");

		final float linearVelocityY = (float) json.getDouble("linearVelocityY");
		final float linearVelocityX = (float) json.getDouble("linearVelocityX");

		final float angularVelocity = (float) json.getDouble("angularVelocity");

		final float widthD2 = this.getAreaShape().getWidth() / 2;
		final float heightD2 = this.getAreaShape().getHeight() / 2;
		final Vector2 v2 = new Vector2(
				(x + widthD2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
				(y + heightD2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT
		);

		this.getBody().setTransform(v2, rotation);

		this.getBody().setLinearVelocity(linearVelocityX, linearVelocityY);
		this.getBody().setAngularVelocity(angularVelocity);
	}

}
