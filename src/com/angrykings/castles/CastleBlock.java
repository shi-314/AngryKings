package com.angrykings.castles;

import com.angrykings.GameContext;
import com.angrykings.PhysicalEntity;
import com.badlogic.gdx.physics.box2d.Body;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * CastleBlock
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class CastleBlock extends PhysicalEntity {
	public CastleBlock(TextureRegion blockTexture) {
		super(0, 0, blockTexture, GameContext.getInstance().getVboManager());
	}

	@Override
	public Body getBody() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
