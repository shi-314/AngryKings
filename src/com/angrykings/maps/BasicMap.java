package com.angrykings.maps;

import com.angrykings.GameContext;
import com.angrykings.ResourceManager;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 07.06.13
 */
public class BasicMap extends Entity {

	public static final float GROUND_X = -1500;
	public static final float GROUND_Y = 1050;

    private Sprite ground;

	public BasicMap() {

		GameContext gc = GameContext.getInstance();
		ResourceManager rm = ResourceManager.getInstance();

		//
		// Grass Texture
		//

        this.ground = new Sprite(BasicMap.GROUND_X, BasicMap.GROUND_Y-65, rm.getGroundTexture(), gc.getVboManager());
        this.attachChild(this.ground);

		//
		// Ground Body (Physics)
		//

		final Rectangle ground = new Rectangle(BasicMap.GROUND_X, BasicMap.GROUND_Y, rm.getGroundTexture().getWidth(), 2, gc.getVboManager());
		ground.setColor(1.0f, 0.0f, 0.0f);

		final FixtureDef wallFixture = PhysicsFactory.createFixtureDef(0, 0.1f, 1.0f);
		PhysicsFactory.createBoxBody(gc.getPhysicsWorld(), ground, BodyDef.BodyType.StaticBody, wallFixture);

	}

}
