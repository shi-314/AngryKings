package com.angrykings.kings;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
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
		
		this.attachChild(kingSprite);
	}
	
	public AnimatedSprite getSprite() {
		return this.kingSprite;
	}

	public void jump() {
		this.registerEntityModifier(
				new LoopEntityModifier(
						new SequenceEntityModifier(
								new MoveByModifier(0.1f, 0, -42),
								new MoveByModifier(0.1f, 0, 42)
						),
						3
				)
		);
	}

}
