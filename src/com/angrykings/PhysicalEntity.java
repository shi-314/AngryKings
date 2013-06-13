package com.angrykings;

import com.badlogic.gdx.physics.box2d.Body;
import org.andengine.entity.Entity;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 14.06.13
 */
public abstract class PhysicalEntity extends Entity{
	public abstract Body getBody();
}
