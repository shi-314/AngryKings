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

	public BasicMap(TiledTextureRegion textureRegion, TiledTextureRegion skyTexture) {
		super(textureRegion);

		GameContext gc = GameContext.getInstance();

		// TODO: Remove constants

		//
		// Background
		//

//		RepeatingSpriteBackground sky = new RepeatingSpriteBackground(GameConfig.CAMERA_WIDTH, GameConfig.CAMERA_HEIGHT, )
//
//		TiledSprite sky = new TiledSprite(0, 0, skyTexture, gc.getVboManager());
//		gc.getScene().setBackground(new EntityBackground(sky));




		//
		// Grass Texture
		//

		for(int i=0; i<128; i++) {
			TiledSprite groundSprite = new TiledSprite(i*32 - 1500, 1000, this.textureRegion, gc.getVboManager());
			this.attachChild(groundSprite);
		}

		//
		// Ground Body (Physics)
		//

		final Rectangle ground = new Rectangle(-1500, 1000, 4200, 2, gc.getVboManager());
		ground.setColor(1.0f, 0.0f, 0.0f);

		final FixtureDef wallFixture = PhysicsFactory.createFixtureDef(0, 0.1f, 1.0f);
		Body body = PhysicsFactory.createBoxBody(gc.getPhysicsWorld(), ground, BodyDef.BodyType.StaticBody, wallFixture);

		//this.attachChild(ground);
	}

}
