package com.angrykings.maps;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;

import com.angrykings.GameContext;
import com.angrykings.ResourceManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 07.06.13
 */
public class BasicMap extends Entity {

	public static final float GROUND_X = -1000;
	public static final float GROUND_Y = 1050;
	public static final float GRASS_TILES = 150;
    
    private Sprite ground;

	public BasicMap() {
		GameContext gc = GameContext.getInstance();
		ResourceManager rm = ResourceManager.getInstance();

		//
		// Grass Texture
		//

		float grassWidth = rm.getGrassTexture().getWidth();

        this.ground = new Sprite(BasicMap.GROUND_X, BasicMap.GROUND_Y-55, rm.getGroundTexture(), gc.getVboManager());
        this.attachChild(this.ground);

		//
		// Ground Body (Physics)
		//

		final Rectangle ground = new Rectangle(BasicMap.GROUND_X, BasicMap.GROUND_Y, BasicMap.GRASS_TILES*grassWidth, 2, gc.getVboManager());
		ground.setColor(1.0f, 0.0f, 0.0f);

		final FixtureDef wallFixture = PhysicsFactory.createFixtureDef(0, 0.1f, 1.0f);
		Body body = PhysicsFactory.createBoxBody(gc.getPhysicsWorld(), ground, BodyDef.BodyType.StaticBody, wallFixture);

		//this.attachChild(ground);
	}

}
