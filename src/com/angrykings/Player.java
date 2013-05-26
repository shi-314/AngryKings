package com.angrykings;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created with IntelliJ IDEA.
 * User: Shivan
 * Date: 22.05.13
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class Player {
	private Vector2 mPosition;

	private VertexBufferObjectManager mVboManager;
	private PhysicsWorld mPhysicsWorld;
	private TiledTextureRegion mTextureRegion;
	private Scene mScene;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(5.5f, 0.1f, 0.9f);

	public Player(Vector2 position, Scene scene, VertexBufferObjectManager vboManager,  TiledTextureRegion textureRegion, PhysicsWorld physicsWorld) {
		this.mPosition = position;
		this.mVboManager = vboManager;
		this.mPhysicsWorld = physicsWorld;
		this.mTextureRegion = textureRegion;
		this.mScene = scene;
	}

	public IEntity getTexture() {
		return new Rectangle(this.mPosition.x-5, this.mPosition.y-5, 10, 10, this.mVboManager);
	}

	public Body fire(Vector2 force) {
		final AnimatedSprite face;
		final Body body;

		float ballPosX =  this.mPosition.x - this.mTextureRegion.getWidth()/2;
		float ballPosY = this.mPosition.y - this.mTextureRegion.getHeight()/2;

		face = new AnimatedSprite(ballPosX, ballPosY, this.mTextureRegion, this.mVboManager);
		body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);

		//face.animate(500);

		this.mScene.attachChild(face);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));

		body.setLinearDamping(0.05f);
		body.setAngularDamping(0.9f);

		body.applyLinearImpulse(
				force.x,
				force.y,
				body.getPosition().x,
				body.getPosition().y
		);

		return body;
	}

	public Vector2 getPosition() {
		return this.mPosition;
	}
}
