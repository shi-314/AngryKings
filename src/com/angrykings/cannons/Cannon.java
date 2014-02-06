package com.angrykings.cannons;

import com.angrykings.GameContext;
import com.angrykings.PhysicsManager;
import com.angrykings.ResourceManager;
import com.badlogic.gdx.math.Vector2;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;

/**
 * Cannon
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 31.05.13
 */
public class Cannon extends Entity {
	protected Sprite barrelSprite, wheelSprite, aimCircleSprite;

	protected final boolean isLeft;
	protected final float minAngle, maxAngle;
    protected Cannonball ball;

	public Cannon(boolean isLeft) {
		ResourceManager rm = ResourceManager.getInstance();

		this.isLeft = isLeft;

		GameContext gc = GameContext.getInstance();

		this.wheelSprite = new Sprite(-76, 0, rm.getWheelTexture(), gc.getVboManager());
		this.barrelSprite = new Sprite(0, 12, rm.getCannonTexture(), gc.getVboManager());
		this.barrelSprite.setRotationCenter(40.0f, 16.0f);
		this.aimCircleSprite = new Sprite(
				0,
				-rm.getAimCircleTexture().getHeight() + rm.getWheelTexture().getHeight(),
				rm.getAimCircleTexture(),
				gc.getVboManager()
		);

        this.aimCircleSprite.setAlpha(0.5f);
		
		this.attachChild(this.barrelSprite);
		this.attachChild(this.wheelSprite);
		

		if (!isLeft) {
			this.setScale(-1.0f, 1.0f);
			this.minAngle = 280;
			this.maxAngle = 360;
		} else {
			this.minAngle = -80;
			this.maxAngle = 0;
		}

		this.setPosition(25, 75);
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @return	Returns true if the angle was set and false if the angle is out of bounds
	 */
	public boolean pointAt(int x, int y) {
		double rotation = Math.atan2(y - this.getY() - this.barrelSprite.getHeight() / 2, x - this.getX());

		rotation = Math.toDegrees(rotation);

		if (!this.isLeft)
			rotation = -rotation + 180;

		if (rotation > this.minAngle && rotation < maxAngle) {
			this.barrelSprite.setRotation((float) rotation);
			return true;
		}

		return false;
	}

	public Vector2 getDirection() {
		float angle = (float) Math.toRadians((double) this.barrelSprite.getRotation());

		if (this.isLeft) {
			return new Vector2((float) Math.cos(angle), (float) Math.sin(angle)).nor();
		} else {
			return new Vector2(-(float) Math.cos(angle), (float) Math.sin(angle))
					.nor();
		}
	}

	private Vector2 getBarrelEndPosition() {
		if (this.isLeft) {
			return new Vector2(this.getX(), this.getY() + 25).add(this
					.getDirection().mul(105));
		} else {
			return new Vector2(this.getX(), this.getY() + 25).add(this
					.getDirection().mul(105));
		}
	}

	public Cannonball fire(float force) {
		GameContext gc = GameContext.getInstance();

		Vector2 ballPosition = this.getBarrelEndPosition();

		ball = new Cannonball(ballPosition.x, ballPosition.y);
		ball.registerPhysicsConnector();

		ball.getBody().applyLinearImpulse(this.getDirection().mul(force), ball.getBody().getPosition());
		ball.getAreaShape().setPosition(ballPosition.x, ballPosition.y);

		gc.getScene().attachChild(ball.getAreaShape());

		PhysicsManager.getInstance().addPhysicalEntity(ball);

		return ball;
	}

    public void activateFollowCamera(){
        GameContext gc = GameContext.getInstance();
        /*
        if(this.ball != null) {
            ZoomCamera camera = (ZoomCamera) gc.getCamera();
            float posX = camera.getCenterX();

            camera.setChaseEntity(this.ball.getAreaShape());
            float zoom = camera.getZoomFactor();
            if(zoom < 0.75f){
                zoom += 0.005f;
                camera.setZoomFactor(zoom);
            }
        }
        */

        if(this.ball != null){
            ZoomCamera camera = (ZoomCamera) gc.getCamera();
            float cameraX = camera.getCenterX();
            float cameraY = camera.getCenterY();
            float difX = cameraX - (this.ball.getAreaShape().getX());
            float difY = cameraY - (this.ball.getAreaShape().getY());

            boolean rightPositionX = false;
            boolean rightPositionY = false;
            if(difX < -20){
                cameraX += Math.abs(difX)/3;
                camera.setCenter(cameraX, cameraY);
            }else if(difX > 20){
                cameraX -= Math.abs(difX)/3;
                camera.setCenter(cameraX, cameraY);
            }else{
                rightPositionX = true;
            }
            if(difY < -50){
                cameraY += Math.abs(difY)/2;
                camera.setCenter(cameraX, cameraY);
            }else if(difY > 50){
                cameraY -= Math.abs(difY)/2;
                camera.setCenter(cameraX, cameraY);
            }else{
                rightPositionY = true;
            }

            if(rightPositionX && rightPositionY){
                camera.setChaseEntity(this.ball.getAreaShape());
                //camera.setZoomFactor(GameConfig.CAMERA_STARTUP_ZOOM);
            }
        }

    }
	
	public void showAimCircle(){
		this.attachChild(aimCircleSprite);
	}
	
	public void hideAimCircle(){
		this.detachChild(aimCircleSprite);
	}
}
