package com.angrykings;

/**
 * IPlayerTurnListener
 *
 * @author 	Shivan Taher <zn31415926535@gmail.com>
 * @date 	21.11.13
 */
public interface IPlayerTurnListener {

	void onHandleTurn(int x, int y);
	void onEndTurn();
	void onKeyframe(float time);

}
