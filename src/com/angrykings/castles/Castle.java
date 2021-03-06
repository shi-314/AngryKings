package com.angrykings.castles;

import android.util.Log;

import com.angrykings.GameContext;
import com.angrykings.IJsonSerializable;
import com.angrykings.KeyframeData;
import com.angrykings.PhysicalEntity;
import com.angrykings.PhysicsManager;
import com.angrykings.ResourceManager;
import com.angrykings.maps.BasicMap;
import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Castle
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Castle implements IJsonSerializable {
    private float x;
    private float y;

    private TiledTextureRegion stoneTexture;
    private TextureRegion roofTexture;
    private TextureRegion woodTexture;

    private final ArrayList<PhysicalEntity> blocks;

    private final float initialHeight;

    public Castle(float x, float y) {

        this.x = x;
        this.y = y;

        ResourceManager rm = ResourceManager.getInstance();

        this.stoneTexture = rm.getStoneTexture();
        this.roofTexture = rm.getRoofTexture();
        this.woodTexture = rm.getWoodTexture();

        this.blocks = new ArrayList<PhysicalEntity>();

        this.build();

        this.initialHeight = this.getHeight();

    }

    private void addWood(float x, float y) {

        GameContext gc = GameContext.getInstance();

        Wood wood = new Wood(x, y);

        wood.registerPhysicsConnector();
        gc.getScene().attachChild(wood.getAreaShape());

        this.blocks.add(wood);
        PhysicsManager.getInstance().addPhysicalEntity(wood);

    }

    private void addStone(float x, float y) {

        GameContext gc = GameContext.getInstance();

        Stone stone = new Stone(x, y);
        stone.registerPhysicsConnector();
        gc.getScene().attachChild(stone.getAreaShape());

        this.blocks.add(stone);
        PhysicsManager.getInstance().addPhysicalEntity(stone);

    }

    private void addRoof(float x, float y) {

        GameContext gc = GameContext.getInstance();

        Roof roof = new Roof(x, y);
        roof.registerPhysicsConnector();
        gc.getScene().attachChild(roof.getAreaShape());

        this.blocks.add(roof);
        PhysicsManager.getInstance().addPhysicalEntity(roof);

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

        // bottom row
        float bottomStone1Y = y - stoneTexture.getHeight() / 2;
        float bottomStone1X = x + stoneTexture.getWidth() / 2;
        float bottomStone2X = bottomStone1X + woodTexture.getWidth() - stoneTexture.getWidth() / 2;
        float bottomStone3X = bottomStone2X + woodTexture.getWidth();
        float bottomStone4X = bottomStone3X + woodTexture.getWidth() - stoneTexture.getWidth() / 2;
        // second row x-Coordinates are known
        float row2Stone1Y = bottomStone1Y - stoneTexture.getHeight();
        // third row
        float row3Wood1Y = row2Stone1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
        float row3Wood1X = bottomStone1X + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
        float row3Wood2X = row3Wood1X + woodTexture.getWidth();
        float row3Wood3X = row3Wood2X + woodTexture.getWidth();
        // fourth row
        float row4Stone1Y = row3Wood1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
        float row4Stone1X = bottomStone1X + woodTexture.getWidth() / 2;
        float row4Stone2X = row4Stone1X + woodTexture.getWidth() - stoneTexture.getWidth() / 2;
        float row4Stone3X = row4Stone2X + woodTexture.getWidth() - stoneTexture.getWidth() / 2;
        // fifth row
        float row5Wood1Y = row4Stone1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
        float row5Wood1X = row4Stone1X + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
        float row5Wood2X = row5Wood1X + woodTexture.getWidth();
        // sixth row
        float row6Stone1Y = row5Wood1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
        float row6Stone1X = row4Stone1X + woodTexture.getWidth() / 2;
        float row6Stone2X = row6Stone1X + woodTexture.getWidth() - stoneTexture.getWidth();
        // seventh row
        float row7Wood1Y = row6Stone1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
        float row7Wood1X = row6Stone1X + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
        // eigth row
        float row8Stone1Y = row7Wood1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
        // ninth row
        float row9Stone1Y = row8Stone1Y - stoneTexture.getHeight();
        // tenth row
        float row10Wood1Y = row9Stone1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
        // eleventh row
        float row11Roof1Y = row10Wood1Y - woodTexture.getHeight() / 2 - roofTexture.getHeight() / 2;
        float row11Roof1X = row6Stone1X + roofTexture.getWidth() / 2;

        this.addStone(bottomStone1X, bottomStone1Y);
        this.addStone(bottomStone2X, bottomStone1Y);
        this.addStone(bottomStone3X, bottomStone1Y);
        this.addStone(bottomStone4X, bottomStone1Y);
        this.addStone(bottomStone1X, row2Stone1Y);
        this.addStone(bottomStone2X, row2Stone1Y);
        this.addStone(bottomStone3X, row2Stone1Y);
        this.addStone(bottomStone4X, row2Stone1Y);
        this.addWood(row3Wood1X, row3Wood1Y);
        this.addWood(row3Wood2X, row3Wood1Y);
        this.addWood(row3Wood3X, row3Wood1Y);
        this.addStone(row4Stone1X, row4Stone1Y);
        this.addStone(row4Stone2X, row4Stone1Y);
        this.addStone(row4Stone3X, row4Stone1Y);
        this.addWood(row5Wood1X, row5Wood1Y);
        this.addWood(row5Wood2X, row5Wood1Y);
        this.addStone(row6Stone1X, row6Stone1Y);
        this.addStone(row6Stone2X, row6Stone1Y);
        this.addWood(row7Wood1X, row7Wood1Y);
        this.addStone(row6Stone1X, row8Stone1Y);
        this.addStone(row6Stone2X, row8Stone1Y);
        this.addStone(row6Stone1X, row9Stone1Y);
        this.addStone(row6Stone2X, row9Stone1Y);
        this.addWood(row7Wood1X, row10Wood1Y);
        this.addRoof(row11Roof1X, row11Roof1Y);


//
//      kleinere Burg
//
//		float bottomStone1X = x + stoneTexture.getWidth() / 2;
//		float bottomStone1Y = y - stoneTexture.getHeight() / 2;
//		float bottomStone2X = bottomStone1X + woodTexture.getWidth() - stoneTexture.getWidth() / 2;
//		float bottomStone3X = bottomStone2X + woodTexture.getWidth() - stoneTexture.getWidth() / 2;
//		float row2Stone1Y = bottomStone1Y - stoneTexture.getHeight();
//		float row3Wood1X = bottomStone1X + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
//		float row3Wood1Y = row2Stone1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
//		float row3Wood2X = row3Wood1X + woodTexture.getWidth();
//		float row4Stone1X = bottomStone1X + woodTexture.getWidth() / 2;
//		float row4Stone1Y = row3Wood1Y - stoneTexture.getHeight() / 2 - woodTexture.getHeight() / 2;
//		float row4Stone2X = row4Stone1X + woodTexture.getWidth() - stoneTexture.getWidth();
//		float row5Wood1X = row4Stone1X + woodTexture.getWidth() / 2 - stoneTexture.getWidth() / 2;
//		float row5Wood1Y = row4Stone1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
//		float row6Stone1Y = row5Wood1Y - stoneTexture.getHeight() / 2 - woodTexture.getHeight() / 2;
//		float row7Wood1Y = row6Stone1Y - woodTexture.getHeight() / 2 - stoneTexture.getHeight() / 2;
//		float row8Stone1Y = row7Wood1Y - stoneTexture.getHeight() / 2 - woodTexture.getHeight() / 2;
//		float row9Stone1Y = row8Stone1Y - stoneTexture.getHeight();
//		float row10roof1Y = row9Stone1Y - roofTexture.getHeight();
//
//		this.addStone(bottomStone1X, bottomStone1Y);
//		this.addStone(bottomStone2X, bottomStone1Y);
//		this.addStone(bottomStone3X, bottomStone1Y);
//		this.addStone(bottomStone1X, row2Stone1Y);
//		this.addStone(bottomStone2X, row2Stone1Y);
//		this.addStone(bottomStone3X, row2Stone1Y);
//		this.addWood(row3Wood1X, row3Wood1Y);
//		this.addWood(row3Wood2X, row3Wood1Y);
//		this.addStone(row4Stone1X, row4Stone1Y);
//		this.addStone(row4Stone2X, row4Stone1Y);
//		this.addWood(row5Wood1X, row5Wood1Y);
//		this.addStone(row4Stone1X, row6Stone1Y);
//		this.addStone(row4Stone2X, row6Stone1Y);
//		this.addWood(row5Wood1X, row7Wood1Y);
//		this.addStone(row4Stone1X, row8Stone1Y);
//		this.addStone(row4Stone2X, row8Stone1Y);
//		this.addStone(row4Stone1X, row9Stone1Y);
//		this.addStone(row4Stone2X, row9Stone1Y);
//		this.addRoof(row4Stone1X, row10roof1Y);
//		this.addRoof(row4Stone2X, row10roof1Y);

    }

    private void setFreeze(boolean freeze) {

        Log.i(this.getClass().getName(), (freeze ? "" : "un") + "freeze castle");

        Iterator<PhysicalEntity> it = this.blocks.iterator();
        while (it.hasNext()) {
            PhysicalEntity entity = it.next();
            Body b = entity.getBody();

            b.setActive(!freeze);
        }

    }

    public void freeze() {
        this.setFreeze(true);
    }

    public void unfreeze() {
        this.setFreeze(false);
    }

    @Override
    public JSONObject toJson() throws JSONException {

        JSONObject json = new JSONObject();
        for (PhysicalEntity entity : blocks) {
            json.put(String.valueOf(entity.getId()), entity.getKeyframeData().toJson());
        }

        return json;

    }

    @Override
    public void fromJson(JSONObject json) throws JSONException {

        for (PhysicalEntity entity : blocks) {
            KeyframeData data = new KeyframeData();
            data.fromJson(json.getJSONObject(String.valueOf(entity.getId())));
            entity.setKeyframeData(data);
        }

    }

    public ArrayList<KeyframeData> getKeyframeData() {

        ArrayList<KeyframeData> data = new ArrayList<KeyframeData>();

        for (PhysicalEntity entity : this.blocks) {
            data.add(entity.getKeyframeData());
        }

        return data;

    }

    public void setKeyframeData(ArrayList<KeyframeData> data) {

        PhysicsManager pm = PhysicsManager.getInstance();
        for(KeyframeData d : data){
            pm.getEntityById(d.entityId).setKeyframeData(d);
        }

    }

    public ArrayList<PhysicalEntity> getBlocks() {
        return this.blocks;
    }
}
