package com.angrykings.maps;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import com.angrykings.GameConfig;
import com.angrykings.GameContext;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.EntityBackground;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseActivity;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 07.06.13
 */
public class BasicMap extends Map {

	public static final float GROUND_X = -2500;
	public static final float GROUND_Y = 1000;
	public static final float GRASS_TILES = 150;
	public static final float GRASS_ENDING_HEIGHT = 4000;

	public BasicMap(TiledTextureRegion textureRegion, TiledTextureRegion skyTexture) {
		super(textureRegion);

		GameContext gc = GameContext.getInstance();

		//
		// Grass Texture
		//

		float grassWidth = textureRegion.getWidth();

		for(int i=0; i<BasicMap.GRASS_TILES; i++) {
			TiledSprite groundSprite = new TiledSprite(i*grassWidth + BasicMap.GROUND_X, BasicMap.GROUND_Y, this.textureRegion, gc.getVboManager());
			this.attachChild(groundSprite);
		}

		Rectangle groundEnding = new Rectangle(
				BasicMap.GROUND_X,
				BasicMap.GROUND_Y + textureRegion.getHeight(),
				BasicMap.GRASS_TILES*grassWidth,
				GRASS_ENDING_HEIGHT,
				gc.getVboManager()
		);

		groundEnding.setColor(0.63137f, 0.435294f, 0.250980f);

		this.attachChild(groundEnding);

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
