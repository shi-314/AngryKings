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

import java.util.ArrayList;

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

    private ArrayList<Sprite> clouds;

	public BasicMap() {
        this.clouds = new ArrayList<Sprite>();

		GameContext gc = GameContext.getInstance();
		ResourceManager rm = ResourceManager.getInstance();

		//
		// Grass Texture
		//

		float grassWidth = rm.getGrassTexture().getWidth();

        this.ground = new Sprite(BasicMap.GROUND_X, BasicMap.GROUND_Y-65, rm.getGroundTexture(), gc.getVboManager());
        this.attachChild(this.ground);

		//
		// Ground Body (Physics)
		//

		final Rectangle ground = new Rectangle(BasicMap.GROUND_X, BasicMap.GROUND_Y, BasicMap.GRASS_TILES*grassWidth, 2, gc.getVboManager());
		ground.setColor(1.0f, 0.0f, 0.0f);

		final FixtureDef wallFixture = PhysicsFactory.createFixtureDef(0, 0.1f, 1.0f);
		Body body = PhysicsFactory.createBoxBody(gc.getPhysicsWorld(), ground, BodyDef.BodyType.StaticBody, wallFixture);

        //
        // Clouds
        //

        for(int i = 0; i < 10; i++) {
            if(i % 3 == 0) {
                final Sprite cloud1 = new Sprite(BasicMap.GROUND_X+200*i, BasicMap.GROUND_Y-500, rm.getCloudTexture1(), gc.getVboManager());
                this.attachChild(cloud1);
            }else if(i % 3 == 1) {
                final Sprite cloud2 = new Sprite(BasicMap.GROUND_X+260*i, BasicMap.GROUND_Y-600, rm.getCloudTexture2(), gc.getVboManager());
                this.attachChild(cloud2);
            }else if(i % 3 == 2) {
                final Sprite cloud3 = new Sprite(BasicMap.GROUND_X+200*i, BasicMap.GROUND_Y-700, rm.getCloudTexture3(), gc.getVboManager());
                this.attachChild(cloud3);
            }

        }

	}

}
