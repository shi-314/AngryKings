package com.angrykings.kings;

import org.andengine.entity.Entity;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.angrykings.GameContext;


/**
 * King
 *
 * @author Shivan Taher <zn31415926535@gmail.com>	
 * @date 31.05.13
 */
public class King extends Entity{
	
	protected final TiledTextureRegion kingTexture;
	protected AnimatedSprite kingSprite;
	
	public King(TiledTextureRegion kingTexture, float x, float y){
		this.kingTexture = kingTexture;
		GameContext gc = GameContext.getInstance();
		
		this.kingSprite = new AnimatedSprite(
				x - this.kingTexture.getWidth() / 2,
				y - this.kingTexture.getHeight() / 2,
				this.kingTexture,
				gc.getVboManager()
		);
		
		//kingSprite.setCurrentTileIndex(1);
		
		this.attachChild(kingSprite);
	}
	
	public AnimatedSprite getSprite() {
		return this.kingSprite;
	}

}
