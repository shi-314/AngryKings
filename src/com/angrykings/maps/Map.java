package com.angrykings.maps;

import org.andengine.entity.Entity;
import org.andengine.opengl.texture.region.TiledTextureRegion;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public abstract class Map extends Entity{
	protected final TiledTextureRegion textureRegion;

	protected Map(TiledTextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}
}
