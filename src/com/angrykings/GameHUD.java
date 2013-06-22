package com.angrykings;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

/**
 * GameHUD
 *
 * Manages the HUD (head up display)
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class GameHUD extends HUD {
	private final AnimatedSprite aimButton;
	private Runnable onAimTouched;

	public GameHUD(TiledTextureRegion aimButtonTexture) {
		super();

		GameContext gc = GameContext.getInstance();

		this.aimButton = new AnimatedSprite(20, 20, aimButtonTexture, gc.getVboManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					this.setCurrentTileIndex(this.getCurrentTileIndex() == 0 ? 1 : 0);
					if(onAimTouched != null)
						onAimTouched.run();
				}
				return true;
			}
		};

		this.attachChild(aimButton);
	}

	public void setOnAimTouched(Runnable onAimTouched) {
		this.onAimTouched = onAimTouched;
		this.registerTouchArea(aimButton);
	}

	public AnimatedSprite getAimButton() {
		return aimButton;
	}
}
