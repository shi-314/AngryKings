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
import com.badlogic.gdx.math.Vector2;

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
		//wood.getAreaShape().setRotation(90f);
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
		

//		float leftBottomStoneX = x + stoneTexture.getWidth() / 2;
//		float leftBottomStoneY = y - stoneTexture.getHeight() / 2;
//		float rightBottomStoneX = leftBottomStoneX + woodTexture.getWidth() - stoneTexture.getWidth();
//		float middleWoodX = leftBottomStoneX + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
//		float middleWoodY = leftBottomStoneY - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
//		
//		float topStoneY = middleWoodY - stoneTexture.getHeight() / 2 - woodTexture.getHeight() / 2;
//		float topWoodY = topStoneY - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
//		float roofX = leftBottomStoneX + woodTexture.getWidth() / 2 - roofTexture.getWidth() / 2;
//		float roofY = topWoodY - roofTexture.getHeight() / 2  - woodTexture.getHeight() / 2;
//
//		this.addStone(leftBottomStoneX, leftBottomStoneY);
//		this.addStone(rightBottomStoneX, leftBottomStoneY);
//		this.addWood(middleWoodX, middleWoodY);
//		this.addStone(leftBottomStoneX, topStoneY);
//		this.addStone(rightBottomStoneX, topStoneY);
//		this.addWood(middleWoodX, topWoodY);
//		this.addRoof(roofX, roofY);
		
		float bottomStone1X = x + stoneTexture.getWidth() / 2;
		float bottomStone1Y = y - stoneTexture.getHeight() / 2;
		float bottomStone2X = bottomStone1X + woodTexture.getWidth() - stoneTexture.getWidth() / 2; 
		float bottomStone3X = bottomStone2X + woodTexture.getWidth() - stoneTexture.getWidth() / 2;
		float row2Stone1Y = bottomStone1Y - stoneTexture.getHeight();
		float row3Wood1X = bottomStone1X + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
		float row3Wood1Y = row2Stone1Y - woodTexture.getHeight();
		float row3Wood2X = row3Wood1X + woodTexture.getWidth();
		float row4Stone1X = bottomStone1X + woodTexture.getWidth() / 2;
		float row4Stone1Y = row3Wood1Y - stoneTexture.getHeight();
		float row4Stone2X = row4Stone1X + woodTexture.getWidth() - stoneTexture.getWidth();
		float row5Wood1X = row4Stone1X + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
		float row5Wood1Y = row4Stone1Y - woodTexture.getHeight();
		float row6Stone1Y = row5Wood1Y - stoneTexture.getHeight();
		float row7Wood1Y = row6Stone1Y - woodTexture.getHeight();
		float row8Stone1Y = row7Wood1Y - stoneTexture.getHeight();
		float row9Stone1Y = row8Stone1Y - stoneTexture.getHeight();
		float row10roof1Y = row9Stone1Y - roofTexture.getHeight();
		
		this.addStone(bottomStone1X, bottomStone1Y);
		this.addStone(bottomStone2X, bottomStone1Y);
		this.addStone(bottomStone3X, bottomStone1Y);
		this.addStone(bottomStone1X, row2Stone1Y);
		this.addStone(bottomStone2X, row2Stone1Y);
		this.addStone(bottomStone3X, row2Stone1Y);
		this.addWood(row3Wood1X, row3Wood1Y);
		this.addWood(row3Wood2X, row3Wood1Y);
		this.addStone(row4Stone1X, row4Stone1Y);
		this.addStone(row4Stone2X, row4Stone1Y);
		this.addWood(row5Wood1X, row5Wood1Y);
		this.addStone(row4Stone1X, row6Stone1Y);
		this.addStone(row4Stone2X, row6Stone1Y);
		this.addWood(row5Wood1X, row7Wood1Y);
		this.addStone(row4Stone1X, row8Stone1Y);
		this.addStone(row4Stone2X, row8Stone1Y);
		this.addStone(row4Stone1X, row9Stone1Y);
		this.addStone(row4Stone2X, row9Stone1Y);
		this.addRoof(row4Stone1X, row10roof1Y);
		this.addRoof(row4Stone2X, row10roof1Y);
		
	}

	public ArrayList<PhysicalEntity> getBlocks() {
		return blocks;
	}

}
