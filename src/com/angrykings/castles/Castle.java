package com.angrykings.castles;

import com.angrykings.PhysicalEntity;
import com.angrykings.maps.BasicMap;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.angrykings.GameContext;
import com.angrykings.activities.MapTest;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

/**
 * Castle
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Castle {
	private float x;
	private float y;

	private TiledTextureRegion stoneTexture;
	private TextureRegion roofTexture;
	private TextureRegion woodTexture;

	private final ArrayList<PhysicalEntity> blocks;

	private final float initialHeight;

	public Castle(float x, float y, TiledTextureRegion stoneTexture, TextureRegion roofTexture, TextureRegion woodTexture) {
		this.x = x;
		this.y = y;

		this.stoneTexture = stoneTexture;
		this.roofTexture = roofTexture;
		this.woodTexture = woodTexture;

		this.blocks = new ArrayList<PhysicalEntity>();

		this.build();

		this.initialHeight = this.getHeight();
	}

	private void addWood(float x, float y) {
		GameContext gc = GameContext.getInstance();

		Wood wood = new Wood(this.woodTexture, x, y);
		wood.registerPhysicsConnector();
		gc.getScene().attachChild(wood.getAreaShape());

		this.blocks.add(wood);
	}

	private void addStone(float x, float y) {
		GameContext gc = GameContext.getInstance();

		Stone stone = new Stone(this.stoneTexture, x, y);
		stone.registerPhysicsConnector();
		gc.getScene().attachChild(stone.getAreaShape());

		this.blocks.add(stone);
	}

	private void addRoof(float x, float y) {
		GameContext gc = GameContext.getInstance();

		Roof roof = new Roof(this.roofTexture, x, y);
		roof.registerPhysicsConnector();
		gc.getScene().attachChild(roof.getAreaShape());

		this.blocks.add(roof);
	}

	public float getHeight() {
		float highest = Float.MAX_VALUE;
		for (PhysicalEntity e : this.blocks) {
			float y = e.getAreaShape().getY();
			if (y < highest)
				highest = y;
		}

		return BasicMap.GROUND_Y - highest - this.stoneTexture.getHeight();
	}

	public float getInitialHeight() {
		return initialHeight;
	}

	private void build() {

		GameContext gc = GameContext.getInstance();

		float leftBottomStoneX = x + stoneTexture.getWidth() / 2;
		float leftBottomStoneY = y - stoneTexture.getHeight() / 2;
		float rightBottomStoneX = leftBottomStoneX + woodTexture.getWidth() - stoneTexture.getWidth();
		float middleWoodX = leftBottomStoneX + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
		float middleWoodY = leftBottomStoneY - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
		float topStoneY = middleWoodY - stoneTexture.getHeight() / 2 - woodTexture.getHeight() / 2;
		float topWoodY = topStoneY - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
		float roofX = leftBottomStoneX + woodTexture.getWidth() / 2 - roofTexture.getWidth() / 2;
		float roofY = topWoodY - roofTexture.getHeight() / 2  - woodTexture.getHeight() / 2;

		this.addStone(leftBottomStoneX, leftBottomStoneY);
		this.addStone(rightBottomStoneX, leftBottomStoneY);
		this.addWood(middleWoodX, middleWoodY);
		this.addStone(leftBottomStoneX, topStoneY);
		this.addStone(rightBottomStoneX, topStoneY);
		this.addWood(middleWoodX, topWoodY);
		this.addRoof(roofX, roofY);

	}

}
