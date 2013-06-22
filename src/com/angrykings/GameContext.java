package com.angrykings;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * GameContext
 *
 * This singleton class holds global resources for our game.
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class GameContext {
	private static GameContext instance = null;

	public static GameContext getInstance() {
		if(instance == null)
			instance = new GameContext();

		return instance;
	}

	private Scene scene;
	private Camera camera;
	private PhysicsWorld physicsWorld;
	private VertexBufferObjectManager vboManager;

	private GameHUD hud;

	private GameContext() {

	}

	public void setContext(Scene scene, Camera camera, PhysicsWorld physicsWorld, VertexBufferObjectManager vboManager) {
		this.scene = scene;
		this.camera = camera;
		this.physicsWorld = physicsWorld;
		this.vboManager = vboManager;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void setPhysicsWorld(PhysicsWorld physicsWorld) {
		this.physicsWorld = physicsWorld;
	}

	public void setVboManager(VertexBufferObjectManager vboManager) {
		this.vboManager = vboManager;
	}

	public void setHud(GameHUD hud) {
		this.hud = hud;
	}

	public Scene getScene() {
		return scene;
	}

	public PhysicsWorld getPhysicsWorld() {
		return physicsWorld;
	}

	public VertexBufferObjectManager getVboManager() {
		return vboManager;
	}

	public Camera getCamera() {
		return camera;
	}

	public GameHUD getHud() {
		return hud;
	}
}
