package com.angrykings;

import org.andengine.entity.scene.background.AutoParallaxBackground;

/**
 * Created by Shivan on 24.01.14.
 */
public class AngryParallaxBackground extends AutoParallaxBackground {

    public AngryParallaxBackground(float pRed, float pGreen, float pBlue, float pParallaxChangePerSecond) {
        super(pRed, pGreen, pBlue, pParallaxChangePerSecond);
    }

    public float getParallaxValue() {
        return this.mParallaxValue;
    }
}
