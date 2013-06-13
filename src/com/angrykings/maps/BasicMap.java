package com.angrykings.maps;

import com.angrykings.GameContext;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 07.06.13
 */
public class BasicMap extends Map {

	public BasicMap(TiledTextureRegion textureRegion) {
		super(textureRegion);

		GameContext gc = GameContext.getInstance();

		for(int i=0; i<32; i++) {
			TiledSprite ground = new TiledSprite(i*32, 400, this.textureRegion, gc.getVboManager());
			this.attachChild(ground);
		}
	}

}
