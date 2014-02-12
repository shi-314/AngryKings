package com.angrykings;

import android.graphics.Color;

import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.LinearGradientFillBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.shape.RectangleBitmapTextureAtlasSourceDecoratorShape;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
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
    private TextureRegion parallax1;
    private TextureRegion parallax2;

    public static ResourceManager getInstance() {
		if (instance == null)
			instance = new ResourceManager();

		return instance;
	}

	//
	// Textures
	//

	private RepeatingSpriteBackground skySprite;
    private TextureRegion groundTexture;
    private TextureRegion cloudTexture1, cloudTexture2, cloudTexture3;
	private TextureRegion cannonTexture;
	private TextureRegion wheelTexture;
	private TextureRegion ballTexture;
	private TextureRegion aimCircleTexture;
	private TiledTextureRegion skyTexture;
    private Sprite backgroundSprite;
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

		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 13, 99, TextureOptions.BILINEAR);


		this.skySprite = new RepeatingSpriteBackground(GameConfig.CAMERA_WIDTH,
				GameConfig.CAMERA_HEIGHT, gameActivity.getTextureManager(),
				AssetBitmapTextureAtlasSource.create(gameActivity.getAssets(),
						"gfx/sky.png"), gameActivity.getVertexBufferObjectManager());

        BitmapTextureAtlas backgroundGradientTexture = new BitmapTextureAtlas(gameActivity.getTextureManager(), 2, 512, TextureOptions.NEAREST);

        EmptyBitmapTextureAtlasSource bitmap = new EmptyBitmapTextureAtlasSource(2, 512);
        LinearGradientFillBitmapTextureAtlasSourceDecorator gradientSource = new LinearGradientFillBitmapTextureAtlasSourceDecorator(
                bitmap, new RectangleBitmapTextureAtlasSourceDecoratorShape(), Color.rgb(54, 168, 224), Color.rgb(255, 255, 255),
                LinearGradientFillBitmapTextureAtlasSourceDecorator.LinearGradientDirection.TOP_TO_BOTTOM);

        TextureRegion backgroundGradientTextureRegion = TextureRegionFactory.createFromSource(backgroundGradientTexture,
                gradientSource, 0, 0);
        backgroundGradientTexture.load();

        this.backgroundSprite = new Sprite(0, 0, GameConfig.CAMERA_WIDTH, GameConfig.CAMERA_HEIGHT,
                backgroundGradientTextureRegion,
                gameActivity.getVertexBufferObjectManager());



        textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 3001,  320, TextureOptions.NEAREST);
        this.groundTexture = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(textureAtlas, gameActivity, "ground copy.png", 0, 0);
        textureAtlas.load();

        textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 211,  91, TextureOptions.NEAREST);
        this.cloudTexture1 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(textureAtlas, gameActivity, "cloud1.png", 0, 0);
        textureAtlas.load();

        textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 242,  110, TextureOptions.NEAREST);
        this.cloudTexture2 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(textureAtlas, gameActivity, "cloud2.png", 0, 0);
        textureAtlas.load();

        textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 240,  90, TextureOptions.NEAREST);
        this.cloudTexture3 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(textureAtlas, gameActivity, "cloud3.png", 0, 0);
        textureAtlas.load();

        textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 1635,  190, TextureOptions.NEAREST);
        this.parallax1 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(textureAtlas, gameActivity, "parallax1.png", 0, 0);
        textureAtlas.load();

        textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 1743,  271, TextureOptions.NEAREST);
        this.parallax2 = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(textureAtlas, gameActivity, "parallax2.png", 0, 0);
        textureAtlas.load();

		//
		// cannon textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 120,
				19, TextureOptions.NEAREST);
		this.cannonTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "cannon.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 155,
				60, TextureOptions.NEAREST);
		this.wheelTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "wheel.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 11, 11,
				TextureOptions.NEAREST);
		this.ballTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "ball.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 294, 301, TextureOptions.NEAREST);

		this.aimCircleTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, gameActivity, "aim_area.png", 0, 0);
		textureAtlas.load();

		//
		// hud textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 64, 54,
				TextureOptions.NEAREST);
		this.whiteFlagButtonTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity,
                        "resign_button.png", 0, 0, 1, 1);
		textureAtlas.load();

		FontFactory.setAssetBasePath("font/");

		//
		// fonts
		//

		final ITexture statusFontTexture = new BitmapTextureAtlas(
				gameActivity.getTextureManager(),
				256,
				256,
				TextureOptions.NEAREST
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
				gameActivity.getTextureManager(), 256, 256, TextureOptions.NEAREST);
		this.playerNameFont = FontFactory.createFromAsset(
				gameActivity.getFontManager(), playerNameFontTexture, gameActivity.getAssets(),
				"CherrySwash-Bold.ttf", 16.0f, true, Color.BLACK);
		this.playerNameFont.load();

		//
		// castle textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 120,
				60, TextureOptions.NEAREST);
		this.stoneTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "stones03.png", 0, 0,
                        2, 1);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 120,
				66, TextureOptions.NEAREST);
		this.roofTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "roof.png", 0, 0);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 180,
				20, TextureOptions.NEAREST);
		this.woodTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, gameActivity, "wood.png", 0, 0);
		textureAtlas.load();

		//
		// king textures
		//

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 88,
				82, TextureOptions.NEAREST);
		this.kingTexture1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "green_king.png", 0,
                        0, 2, 1);
		textureAtlas.load();

		textureAtlas = new BitmapTextureAtlas(gameActivity.getTextureManager(), 120,
				82, TextureOptions.NEAREST);
		this.kingTexture2 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(textureAtlas, gameActivity, "purple_king.png", 0,
                        0, 2, 1);

		textureAtlas.load();
	}

	public RepeatingSpriteBackground getSkySprite() {
		return skySprite;
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

    public TextureRegion getGroundTexture() {
        return groundTexture;
    }

    public TextureRegion getCloudTexture1() {
        return cloudTexture1;
    }

    public TextureRegion getCloudTexture2() {
        return cloudTexture2;
    }

    public TextureRegion getCloudTexture3() {
        return cloudTexture3;
    }

    public Sprite getBackgroundSprite() {
        return backgroundSprite;
    }

    public TextureRegion getParallax1() {
        return parallax1;
    }

    public TextureRegion getParallax2() {
        return parallax2;
    }
}
