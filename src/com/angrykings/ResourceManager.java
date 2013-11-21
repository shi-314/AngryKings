package com.angrykings;

import android.graphics.Color;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * AngryKings
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 24.10.13
 */
public class ResourceManager {
	private static ResourceManager instance = null;

	public static ResourceManager getInstance() {
		if (instance == null)
			instance = new ResourceManager();

		return instance;
	}

	//
	// Textures
	//

	private RepeatingSpriteBackground skySprite;
	private TiledTextureRegion grassTexture;
	private TextureRegion cannonTexture;
	private TextureRegion wheelTexture;
	private TextureRegion ballTexture;
	private TextureRegion aimCircleTexture;
	private TiledTextureRegion skyTexture;
	private TiledTextureRegion aimButtonTexture;
	private TiledTextureRegion whiteFlagButtonTexture;
	private TiledTextureRegion stoneTexture;
	private TiledTextureRegion kingTexture1;
	private TiledTextureRegion kingTexture2;
	private TextureRegion roofTexture;
	private TextureRegion woodTexture;
	private Font statusFont;
	private Font playerNameFont;

	private ResourceManager() {
	}

	public void load(BaseGameActivity gameActivity) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		//
		// map textures
		//

		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(
				gameActivity.getTextureManager(), 13, 99, TextureOptions.BILINEAR);
		this.grassTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "grass.png", 0, 0, 1,
						1); // 32x32
		textureAtlas.load();

		this.skySprite = new RepeatingSpriteBackground(GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT, gameActivity.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(gameActivity.getAssets(),
						"gfx/sky.png"), gameActivity.getVertexBufferObjectManager());

		//
		// cannon textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 64,
				18, TextureOptions.BILINEAR);
		this.cannonTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "cannon.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 32,
				32, TextureOptions.BILINEAR);
		this.wheelTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "wheel.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 11, 11,
				TextureOptions.BILINEAR);
		this.ballTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "ball.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 280, 302, TextureOptions.BILINEAR);
		this.aimCircleTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, gameActivity, "aimCircle.png", 0, 0);
		textureAtlas.load();

		//
		// hud textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 128,
				64, TextureOptions.BILINEAR);
		this.aimButtonTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "aim_button.png", 0,
						0, 2, 1);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 64, 64,
				TextureOptions.BILINEAR);
		this.whiteFlagButtonTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity,
						"white_flag_button.png", 0, 0, 1, 1);
		textureAtlas.load();

		FontFactory.setAssetBasePath("font/");

		//
		// fonts
		//

		final ITexture statusFontTexture = new BitmapTextureAtlas(
				gameActivity.getTextureManager(),
				256,
				256,
				TextureOptions.BILINEAR
		);

		this.statusFont = FontFactory.createStrokeFromAsset(
				gameActivity.getFontManager(),
				statusFontTexture,
				gameActivity.getAssets(),
				"CherrySwash-Bold.ttf",
				32.0f,
				true,
				Color.WHITE,
				2.0f,
				Color.BLACK
		);

		this.statusFont.load();

		final ITexture playerNameFontTexture = new BitmapTextureAtlas(
				gameActivity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.playerNameFont = FontFactory.createFromAsset(
				gameActivity.getFontManager(), playerNameFontTexture, gameActivity.getAssets(),
				"CherrySwash-Bold.ttf", 16.0f, true, Color.BLACK);
		this.playerNameFont.load();

		//
		// castle textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 63,
				21, TextureOptions.BILINEAR);
		this.stoneTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "stones.png", 0, 0,
						3, 1);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 21,
				21, TextureOptions.BILINEAR);
		this.roofTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "roof.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 68,
				9, TextureOptions.BILINEAR);
		this.woodTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "wood.png", 0, 0);
		textureAtlas.load();

		//
		// king textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 88,
				82, TextureOptions.BILINEAR);
		this.kingTexture1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "green_king.png", 0,
						0, 2, 1);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 120,
				82, TextureOptions.BILINEAR);
		this.kingTexture2 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "purple_king.png", 0,
						0, 2, 1);

		textureAtlas.load();
	}

	public RepeatingSpriteBackground getSkySprite() {
		return skySprite;
	}

	public TiledTextureRegion getGrassTexture() {
		return grassTexture;
	}

	public TextureRegion getCannonTexture() {
		return cannonTexture;
	}

	public TextureRegion getWheelTexture() {
		return wheelTexture;
	}

	public TextureRegion getBallTexture() {
		return ballTexture;
	}

	public TextureRegion getAimCircleTexture() {
		return aimCircleTexture;
	}

	public TiledTextureRegion getSkyTexture() {
		return skyTexture;
	}

	public TiledTextureRegion getAimButtonTexture() {
		return aimButtonTexture;
	}

	public TiledTextureRegion getWhiteFlagButtonTexture() {
		return whiteFlagButtonTexture;
	}

	public TiledTextureRegion getStoneTexture() {
		return stoneTexture;
	}

	public TiledTextureRegion getKingTexture1() {
		return kingTexture1;
	}

	public TiledTextureRegion getKingTexture2() {
		return kingTexture2;
	}

	public TextureRegion getRoofTexture() {
		return roofTexture;
	}

	public TextureRegion getWoodTexture() {
		return woodTexture;
	}

	public Font getStatusFont() {
		return statusFont;
	}

	public Font getPlayerNameFont() {
		return playerNameFont;
	}
}
