package com.angrykings;

import com.badlogic.gdx.physics.box2d.Body;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * PhysicsManager
 *
 * Manages all physically simulated game objects.
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class PhysicsManager implements IUpdateHandler {
	private static PhysicsManager instance = null;

	public static PhysicsManager getInstance() {
		if(instance == null)
			instance = new PhysicsManager();

		return instance;
	}

	private static final float MIN_LINEAR_VELOCITY = 1e-5f;
	private static final float MIN_ANGULAR_VELOCITY = 1e-3f;
	private ArrayList<PhysicalEntity> physicalEntities;

	public PhysicsManager() {
		this.physicalEntities = new ArrayList<PhysicalEntity>();
	}

	public void addPhysicalEntity(PhysicalEntity entity) {
		this.physicalEntities.add(entity);
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		GameContext gc = GameContext.getInstance();

		Iterator<PhysicalEntity> it = this.physicalEntities.iterator();
		while(it.hasNext()) {
			PhysicalEntity entity = it.next();
			Body b = entity.getBody();

			float linearVelocity = b.getLinearVelocity().len();
			float angularVelocity = b.getAngularVelocity();

			if(linearVelocity > 0 && linearVelocity < 2.5 && angularVelocity < 3){
				b.setAngularVelocity(0.0f);
			}

			if(linearVelocity < PhysicsManager.MIN_LINEAR_VELOCITY && angularVelocity < PhysicsManager.MIN_ANGULAR_VELOCITY) {
				Debug.d("remove physical entity: lin: " + b.getLinearVelocity().len() + " angular: " + b.getAngularVelocity());
				gc.getScene().detachChild(entity.getAreaShape());
				gc.getPhysicsWorld().destroyBody(b);
				it.remove();
			}
		}
	}

	@Override
	public void reset() {
	}
}
