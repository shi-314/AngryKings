package com.angrykings;

import android.util.Log;
import com.angrykings.cannons.Cannon;
import com.angrykings.cannons.Cannonball;
import com.angrykings.castles.Castle;
import com.angrykings.kings.King;
import com.angrykings.maps.BasicMap;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.view.ConfigChooser;
import org.andengine.ui.activity.BaseGameActivity;

/**
 * Player
 *
 * @author 	Shivan Taher <zn31415926535@gmail.com>
 * @date 	21.11.13
 */
public class Player implements IUpdateHandler {

	//
	// Attributes
	//

	private static final int KEYFRAMES_PER_SECOND = 1;
	private boolean inTurn;
	private float timeElapsed;
	private float keyTime;

	//
	// Game Objects Positions
	//

	private static final int LEFT_CANNON_X = -250;
	private static final int RIGHT_CANNON_X = 200;

	private static final int LEFT_CASTLE_X = -475;
	private static final int RIGHT_CASTLE_X = 275;

	private static final int LEFT_KING_X = -300;
	private static final int RIGHT_KING_X = 250;

	//
	// Game Objects
	//

	private final String name;
	private final King king;
	private final Castle castle;
	private final Cannon cannon;
	private final boolean isLeft;

	//
	// Callbacks
	//

	private IPlayerTurnListener playerTurnListener;

	public Player(String name, boolean isLeft) {

		ResourceManager rm = ResourceManager.getInstance();
		GameContext gc = GameContext.getInstance();

		this.name = name;
		this.isLeft = isLeft;

		float cannonX = LEFT_CANNON_X;
		float cannonY = (int) BasicMap.GROUND_Y - (int) rm.getWheelTexture().getHeight();

		float kingX = LEFT_KING_X;
		float kingY = BasicMap.GROUND_Y - rm.getKingTexture2().getHeight() / 2;

		float castleX = LEFT_CASTLE_X;
		float castleY = BasicMap.GROUND_Y;

		TiledTextureRegion kingTexture = rm.getKingTexture2();

		if(!this.isLeft) {
			cannonX = RIGHT_CANNON_X;
			kingX = RIGHT_KING_X;
			castleX = RIGHT_CASTLE_X;
			kingTexture = rm.getKingTexture1();
		}

		this.cannon = new Cannon(this.isLeft);

		this.cannon.setPosition(cannonX, cannonY);
		this.king = new King(kingTexture, kingX, kingY);
		this.castle = new Castle(castleX, castleY);

		gc.getScene().attachChild(this.king);
		gc.getScene().attachChild(this.cannon);

	}

	public void handleTurn(final int aimX, final  int aimY) {

		Log.i(getClass().getName(), "["+this.name+"] handleTurn("+aimX+", "+aimY+")");

		if(this.playerTurnListener != null)
			this.playerTurnListener.onHandleTurn(aimX, aimY);

		final BaseGameActivity gameActivity = GameContext.getInstance().getGameActivity();

		gameActivity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {

				final Cannonball ball = cannon.fire(GameConfig.CANNON_FORCE);
                cannon.activateFollowCamera();

				gameActivity.getEngine().registerUpdateHandler(
						new TimerHandler(
								GameConfig.CANNONBALL_TIME_SEC,
								new ITimerCallback() {
									@Override
									public void onTimePassed(TimerHandler pTimerHandler) {
										ball.remove();
									}
								}));

				ball.setOnRemove(new Runnable() {
					@Override
					public void run() {
						endTurn();
						castle.freeze();
					}
				});

			}
		});

		this.timeElapsed = 0;
		this.keyTime = 0;
		this.inTurn = true;

	}

	public void endTurn() {

		Log.i(getClass().getName(), "["+this.name+"] onEndTurn()");

		this.inTurn = false;

		if(this.playerTurnListener != null) {
			this.playerTurnListener.onKeyframe(GameConfig.CANNONBALL_TIME_SEC);
			this.playerTurnListener.onEndTurn();
		}

	}

	public String getName() {
		return name;
	}

	public King getKing() {
		return king;
	}

	public Castle getCastle() {
		return castle;
	}

	public Cannon getCannon() {
		return cannon;
	}

	public void setPlayerTurnListener(IPlayerTurnListener playerTurnListener) {
		this.playerTurnListener = playerTurnListener;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {

		if(inTurn) {
			this.timeElapsed += pSecondsElapsed;
			this.keyTime += pSecondsElapsed;

			if(this.keyTime > Player.KEYFRAMES_PER_SECOND) {
				if(this.playerTurnListener != null)
					this.playerTurnListener.onKeyframe(this.timeElapsed);

				this.keyTime = 0;
			}
		}

	}

	@Override
	public void reset() {

	}
}
