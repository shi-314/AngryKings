package com.angrykings.activities;

import android.os.Handler;

import com.angrykings.AngryParallaxBackground;
import com.angrykings.GameConfig;
import com.angrykings.GameContext;
import com.angrykings.GameHUD;
import com.angrykings.GameStatus;
import com.angrykings.Player;
import com.angrykings.ResourceManager;
import com.angrykings.ServerConnection;
import com.angrykings.maps.BasicMap;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.ui.IGameInterface;
import org.andengine.ui.activity.BaseGameActivity;

public class GameActivity extends BaseGameActivity implements
        IOnSceneTouchListener, ScrollDetector.IScrollDetectorListener,
        PinchZoomDetector.IPinchZoomDetectorListener  {

    //
    // Core
    //

    protected GameContext gc;
    protected GameHUD hud;
    protected ResourceManager rm;

    //
    // Game Objects
    //

    protected GameStatus status;
    protected Player me;
    protected Player partner;
    protected BasicMap map;
    protected AngryParallaxBackground parallaxBackground;

    //
    // Navigation Attributes
    //

    protected SurfaceScrollDetector scrollDetector;
    protected PinchZoomDetector pinchZoomDetector;
    protected float pinchZoomStartedCameraZoomFactor;
    protected boolean isAiming = true;





    @Override
    public EngineOptions onCreateEngineOptions() {
        GameContext.clear();
        gc = GameContext.getInstance();

        ZoomCamera camera = new ZoomCamera(GameConfig.CAMERA_X, GameConfig.CAMERA_Y, GameConfig.CAMERA_WIDTH, GameConfig.CAMERA_HEIGHT);

        camera.setZoomFactor(GameConfig.CAMERA_STARTUP_ZOOM);
        camera.setBounds(
                GameConfig.CAMERA_MIN_X, GameConfig.CAMERA_MIN_Y,
                GameConfig.CAMERA_MAX_X, GameConfig.CAMERA_MAX_Y
        );
        camera.setBoundsEnabled(true);

        gc.setCamera(camera);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(GameConfig.CAMERA_WIDTH,
                        GameConfig.CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {

    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {

    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {

    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return false;
    }

    @Override
    public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector, TouchEvent pSceneTouchEvent) {

    }

    @Override
    public void onPinchZoom(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {

    }

    @Override
    public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector, TouchEvent pTouchEvent, float pZoomFactor) {

    }

    @Override
    public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

    }

    @Override
    public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

    }

    @Override
    public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

    }

}
