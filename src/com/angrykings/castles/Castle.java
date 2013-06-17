package com.angrykings.castles;

import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.angrykings.GameContext;
import com.angrykings.activities.MapTest;

/**
 * Castle
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Castle {
	private GameContext gc;
	MapTest map;
	BitmapTextureAtlas textureAtlas;
	
	private TiledTextureRegion stoneTexture;
	
	
	private Stone stone;
	private int x;
	private int y;
	private TextureRegion roofTexture;
	private Roof roof;
	private TextureRegion woodTexture;
	private Wood wood;
	private Stone stone2;
	private Stone stone3;
	private Stone stone4;
	private Wood wood2;
	
	public Castle(MapTest map, int x, int y){
		this.map = map;
		this.x = x;
		this.y = y;
		gc = GameContext.getInstance();
		gc.setVboManager(map.getVertexBufferObjectManager());
		
	}
	
	public void createTextures(){
		textureAtlas = new BitmapTextureAtlas(map.getTextureManager(), 384, 128, TextureOptions.BILINEAR);
		this.stoneTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, map, "stones.png", 0, 0, 3, 1);
		textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(map.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.roofTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, map, "roof.png", 0, 0);
		textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(map.getTextureManager(), 409, 50, TextureOptions.BILINEAR);
		this.woodTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, map, "wood.png", 0, 0);
		textureAtlas.load();
	}
	
	public void createSinglePieces(){
		this.stone = new Stone(this.stoneTexture, x, y);
		stone.registerPhysicsConnector();
		gc.getScene().attachChild(this.stone.getAreaShape());
		
		this.stone2 = new Stone(this.stoneTexture, stone.getAreaShape().getX() + woodTexture.getWidth() - stoneTexture.getWidth(), stone.getAreaShape().getY());
		stone2.registerPhysicsConnector();
		gc.getScene().attachChild(stone2.getAreaShape());
		
		this.wood = new Wood(this.woodTexture, stone.getAreaShape().getX(), stone.getAreaShape().getY() - woodTexture.getHeight());
		wood.registerPhysicsConnector();
		gc.getScene().attachChild(this.wood.getAreaShape());
		
		
		
		this.stone3 = new Stone(this.stoneTexture, stone.getAreaShape().getX(), wood.getAreaShape().getY() - stoneTexture.getHeight());
		stone3.registerPhysicsConnector();
		gc.getScene().attachChild(stone3.getAreaShape());
		
		this.stone4 = new Stone(this.stoneTexture, stone.getAreaShape().getX() + woodTexture.getWidth() - stoneTexture.getWidth(), wood.getAreaShape().getY() - stoneTexture.getHeight());
		stone4.registerPhysicsConnector();
		gc.getScene().attachChild(stone4.getAreaShape());
		
		this.wood2 = new Wood(this.woodTexture, stone.getAreaShape().getX(), stone3.getAreaShape().getY() - woodTexture.getHeight());
		wood2.registerPhysicsConnector();
		gc.getScene().attachChild(this.wood2.getAreaShape());
		
		this.roof = new Roof(this.roofTexture, stone.getAreaShape().getX() + woodTexture.getWidth()/2 - roofTexture.getWidth()/2, wood2.getAreaShape().getY() - roofTexture.getHeight());
		roof.registerPhysicsConnector();
		gc.getScene().attachChild(this.roof.getAreaShape());
	}
	
}
