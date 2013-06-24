package com.angrykings;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;
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
	private final AnimatedSprite whiteFlagButton;

	private Runnable onAimTouched;
	private Runnable onWhiteFlagTouched;

	private final LifeBar leftLifeBar;
	private final LifeBar rightLifeBar;

	/**
	 * This entity represents a slider to indicate the height of the players castle.
	 */
	public class LifeBar extends Entity {
		private final Rectangle barFrame;
		private final Rectangle barFilled;

		public final static float BAR_HEIGHT = 200.0f;
		public final static float BAR_WIDTH = 8;

		private LifeBar(float x, float y) {
			this(x, y, new Color(0.5f, 0.5f, 0.5f, 0.5f), new Color(1, 0, 0, 0.5f));
		}

		private LifeBar(float x, float y, Color frameColor, Color lifeColor) {
			super(x, y);

			GameContext gc = GameContext.getInstance();

			this.barFrame = new Rectangle(0, 0, BAR_WIDTH, LifeBar.BAR_HEIGHT, gc.getVboManager());
			this.barFrame.setColor(frameColor);
			this.attachChild(this.barFrame);

			this.barFilled = new Rectangle(0, LifeBar.BAR_HEIGHT, BAR_WIDTH, -LifeBar.BAR_HEIGHT, gc.getVboManager());
			this.barFilled.setColor(lifeColor);
			this.attachChild(this.barFilled);
		}

		public void setValue(float value) {
			this.barFilled.setHeight(-LifeBar.BAR_HEIGHT*Math.min(value, 1.0f));
		}
	}

	public GameHUD(TiledTextureRegion aimButtonTexture, TiledTextureRegion whiteFlagButtonTexture) {
		super();

		GameContext gc = GameContext.getInstance();

		//
		// aim button
		//

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

		this.attachChild(this.aimButton);

		//
		// white flag button
		//

		this.whiteFlagButton = new AnimatedSprite(GameConfig.CAMERA_WIDTH-100, 20, whiteFlagButtonTexture, gc.getVboManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					if(onWhiteFlagTouched != null)
						onWhiteFlagTouched.run();
				}
				return true;
			}
		};

		this.attachChild(this.whiteFlagButton);

		//
		// player slider
		//

		this.leftLifeBar = new LifeBar(20, 180);
		this.leftLifeBar.setValue(0.5f);
		this.attachChild(this.leftLifeBar);

		this.rightLifeBar = new LifeBar(GameConfig.CAMERA_WIDTH - LifeBar.BAR_WIDTH - 20, 180);
		this.rightLifeBar.setValue(0.7f);
		this.attachChild(this.rightLifeBar);
	}

	public void setOnAimTouched(Runnable onAimTouched) {
		this.onAimTouched = onAimTouched;
		this.registerTouchArea(this.aimButton);
	}

	public void setOnWhiteFlagTouched(Runnable onWhiteFlagTouched) {
		this.onWhiteFlagTouched = onWhiteFlagTouched;
		this.registerTouchArea(this.whiteFlagButton);
	}

	public AnimatedSprite getAimButton() {
		return aimButton;
	}

	public AnimatedSprite getWhiteFlagButton() {
		return whiteFlagButton;
	}

	public LifeBar getRightLifeBar() {
		return rightLifeBar;
	}

	public LifeBar getLeftLifeBar() {
		return leftLifeBar;
	}
}
