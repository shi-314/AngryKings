package com.angrykings;

/**
 * GameConfig
 *
 * This class has public static final attributes that declare constants for the game.
 *
 * @author Shivan Taher <zn31415926535@gmail.com>
 * @date 06.06.13
 */

public final class GameConfig {
	public static final int CAMERA_WIDTH = 960;
	public static final int CAMERA_HEIGHT = 540;

	public static final float CAMERA_STARTUP_ZOOM = 0.2f;
	public static final float CAMERA_ZOOM_MIN = 0.2f;
	public static final float CAMERA_ZOOM_MAX = 1.2f;

	public static final float CAMERA_MIN_X = -2500;
	public static final float CAMERA_MAX_X = 3600;
	public static final float CAMERA_MIN_Y = -1300;
	public static final float CAMERA_MAX_Y = 1400;

	public static final boolean LOG_FPS = false;

	public static final int PHYSICS_STEPS_PER_SEC = 30;
	public static final int PHYSICS_VELOCITY_ITERATION = 3;
	public static final int PHYSICS_POSITION_ITERATION = 3;

	public static final float CANNON_FORCE = 400;
	public static final float CANNONBALL_TIME_SEC = 10.0f;
}
