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
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import java.util.ArrayList;

public class Player implements IUpdateHandler {

	// Attributes
	private static final int KEYFRAMES_PER_SECOND = 5;
	private boolean inTurn;
	private float timeElapsed;
	private float keyTime;

	// Game Objects Positions

	private static final int LEFT_CANNON_X = -375;
	private static final int RIGHT_CANNON_X = 300;

	private static final int LEFT_CASTLE_X = -1015;
	private static final int RIGHT_CASTLE_X = 375;

	private static final int LEFT_KING_X = -415;
	private static final int RIGHT_KING_X = 350;

	// Game Objects
	private final String name;
	private final King king;
	private final Castle castle;
	private final Cannon cannon;
	private final boolean isLeft;
	private Cannonball cannonball;

	// Callbacks
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

	public void handleTurn(final int aimX, final  int aimY, final ArrayList<Keyframe> keyframes) {

		Log.i(getClass().getName(), "["+this.name+"] handleTurn("+aimX+", "+aimY+")");

		final BaseGameActivity gameActivity = GameContext.getInstance().getGameActivity();

		gameActivity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {

				cannonball = cannon.fire(GameConfig.CANNON_FORCE);
                cannon.activateFollowCamera();

				gameActivity.getEngine().registerUpdateHandler(
						new TimerHandler(
								GameConfig.CANNONBALL_TIME_SEC,
								new ITimerCallback() {
									@Override
									public void onTimePassed(TimerHandler pTimerHandler) {
										cannonball.remove();
									}
								}));

				cannonball.setOnRemove(new Runnable() {
					@Override
					public void run() {
						endTurn();
						castle.freeze();
					}
				});

                if(playerTurnListener != null) {
                    playerTurnListener.onHandleTurn(aimX, aimY, keyframes);
                    playerTurnListener.onKeyframe(0f);
                }
			}
		});

		this.timeElapsed = 0;
		this.keyTime = 0;
		this.inTurn = true;

	}

	public void endTurn() {

		if(this.playerTurnListener != null) {
			this.playerTurnListener.onKeyframe(GameConfig.CANNONBALL_TIME_SEC);
		}

		Log.i(getClass().getName(), "["+this.name+"] onEndTurn()");

		this.inTurn = false;

		if(this.playerTurnListener != null) {
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

	public Cannonball getCannonball() {
		return cannonball;
	}

	public void setPlayerTurnListener(IPlayerTurnListener playerTurnListener) {
		this.playerTurnListener = playerTurnListener;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {

		if(this.playerTurnListener != null)
			this.playerTurnListener.onUpdate(pSecondsElapsed);

		if(inTurn) {
			this.timeElapsed += pSecondsElapsed;
			this.keyTime += pSecondsElapsed;

			if(this.keyTime > 1.0f / Player.KEYFRAMES_PER_SECOND) {
				if(this.playerTurnListener != null)
					this.playerTurnListener.onKeyframe(this.timeElapsed);

				this.keyTime = 0;
			}
		}

	}

	public boolean isInTurn() {
		return inTurn;
	}

	@Override
	public void reset() {

	}
}
