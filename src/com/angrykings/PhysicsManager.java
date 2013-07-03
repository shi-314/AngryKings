package com.angrykings;

import com.angrykings.maps.BasicMap;
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
	private boolean ready;
	private boolean freezed;

	public static PhysicsManager getInstance() {
		if(instance == null)
			instance = new PhysicsManager();

		return instance;
	}

	private static final float MIN_LINEAR_VELOCITY = 1e-2f;
	private static final float MIN_ANGULAR_VELOCITY = 1e-1f;
	private ArrayList<PhysicalEntity> physicalEntities;

	public PhysicsManager() {
		this.ready = true;
		this.physicalEntities = new ArrayList<PhysicalEntity>();
	}

	public void addPhysicalEntity(PhysicalEntity entity) {
		this.physicalEntities.add(entity);
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		this.ready = true;

		Iterator<PhysicalEntity> it = this.physicalEntities.iterator();
		while(it.hasNext()) {
			PhysicalEntity entity = it.next();
			Body b = entity.getBody();

			if(!entity.isAutoRemoveEnabled())
				continue;

			float linearVelocity = b.getLinearVelocity().len();
			float angularVelocity = b.getAngularVelocity();

			if(linearVelocity > 0 && linearVelocity < 2.5 && angularVelocity < 3){
				b.setAngularVelocity(0.0f);
			}

			if(linearVelocity < PhysicsManager.MIN_LINEAR_VELOCITY && angularVelocity < PhysicsManager.MIN_ANGULAR_VELOCITY) {
				Debug.d("remove physical entity: lin: " + b.getLinearVelocity().len() + " angular: " + b.getAngularVelocity());
				entity.remove();
				it.remove();
			}else{
				Debug.d("not ready: lin="+linearVelocity+", ang="+angularVelocity);
				this.ready = false;
			}

			if(entity.getAreaShape().getY() > BasicMap.GROUND_Y + 100) {
				Debug.d("remove physical entity (seems to have fallen down of the 'ground'): y " + entity.getAreaShape().getY());
				entity.remove();
				it.remove();
			}
		}
	}

	public void setFreeze(boolean freeze) {
		Debug.d((freeze ? "" : "un") + "freeze");

		Iterator<PhysicalEntity> it = this.physicalEntities.iterator();
		while(it.hasNext()) {
			PhysicalEntity entity = it.next();
			Body b = entity.getBody();

			// ignore auto removable entities like cannon balls -> just freeze the castle blocks

			if(entity.isAutoRemoveEnabled())
				continue;

			b.setActive(!freeze);
		}

		GameContext.getInstance().getPhysicsWorld().clearForces();

		this.freezed = freeze;
	}

	public boolean isReady() {
		return this.ready && (GameConfig.USE_FIXED_CANNONBALL_TIME ? this.freezed : true);
	}

	public void setEntitiesActive(boolean status) {
		Iterator<PhysicalEntity> it = this.physicalEntities.iterator();
		while(it.hasNext()) {
			PhysicalEntity entity = it.next();
			entity.getBody().setActive(status);
		}
	}

	@Override
	public void reset() {
	}
}
