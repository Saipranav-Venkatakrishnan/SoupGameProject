package com.example.soupgameproject;

import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Camera {

    // In game layout should follow this general template:
    // (-> means that the following is within the previous layout)
    // ScalingFrameLayout (Controls Zoom) -> GameContainerLayout (ConstraintLayout) (Controls Linear Movement)
    // -> BackgroundLayout (ConstraintLayout) -> CollisionLayout (ConstraintLayout) -> ForegroundLayout (ConstraintLayout)
    // UserInterfaceLayout (ConstraintLayout)...etc

    // The two layouts used to control camera movement
    private FrameLayout scalingLayout;
    private ConstraintLayout containerLayout;

    // Scale relates to zoom
    // x and y relate to the relative position of the camera,
    // NOT its actual position in our defined game coordinate system in DP
    private float scale, x, y;

    // When true, the camera will not move. This must be false in order for the camera to move.
    private boolean fixedPosition;

    public Camera(FrameLayout scalingLayout, ConstraintLayout containerLayout){
        this.scalingLayout = scalingLayout;
        this.containerLayout = containerLayout;

        scale = scalingLayout.getScaleX();
        x = containerLayout.getTranslationX();
        y = containerLayout.getTranslationY();

        this.fixedPosition = false;
    }

    // In general, the handler parameter will be used to start an action. To stop an action use: handler.removeCallbacks(action)
    // or handler.removeCallbacksAndMessages(null) which removes all callbacks from handler


    // The zoomSpeed parameter will control how fast the zoom occurs. The larger it is, the slower the zoom.
    // The smaller it is, the faster the zoom.
    // Use the following two function like so if the user is causing the zoom to occur:
    // Runnable action = yourCamera.zoomIn(handler, zoomSpeed) or yourCamera.zoomOut(handler, zoomSpeed)
    // handler.postDelayed(action, delay time for action to start);

    public Runnable zoomIn(Handler handler, float zoomSpeed){
        Runnable action = new Runnable() {
            @Override
            public void run() {
                if(!fixedPosition) {
                    scale = (scalingLayout.getScaleX() * zoomSpeed) / (zoomSpeed - 1);

                    scalingLayout.setScaleX(scale);
                    scalingLayout.setScaleY(scale);

                    Log.i("ScaleValue", "Scale: " + String.valueOf(scale));

                    handler.postDelayed(this, 1);
                }
            }
        };

        return action;
    }

    public Runnable zoomOut(Handler handler, float zoomSpeed){
        Runnable action = new Runnable() {
            @Override
            public void run() {
                if(!fixedPosition) {
                    scale = (scalingLayout.getScaleX() * (zoomSpeed - 1)) / (zoomSpeed);

                    scalingLayout.setScaleX(scale);
                    scalingLayout.setScaleY(scale);

                    Log.i("ScaleValue", "Scale: " + String.valueOf(scale));

                    handler.postDelayed(this, 1);
                }
            }
        };

        return action;
    }

    // Use this function to zoom to a desired scale. Zooming will start the moment the function is called.
    // zoomSpeed behaves the same as before.
    // the larger the desiredScale, the more zoomed in the view will be

    public void zoomTo(float desiredScale, float zoomSpeed){
        zoomTo(desiredScale, zoomSpeed, new CameraCallBack() {
            @Override
            public void onActionComplete(Camera camera) {
                Log.i("Camera", camera.toString());
            }
        });
    }

    public void zoomTo(float desiredScale, float zoomSpeed, CameraCallBack cameraCallBack){
        Handler handler = new Handler();

        Runnable action = new Runnable() {
            @Override
            public void run() {
                if(!fixedPosition) {
                    // this is the margin of error for how close the scale must be to the desired scale in order to stop zooming
                    float ME = scale / (zoomSpeed - 1);

                    if (desiredScale < scale + ME && desiredScale > scale - ME) {
                        // done zooming
                        scale = desiredScale;
                        scalingLayout.setScaleX(desiredScale);
                        scalingLayout.setScaleY(desiredScale);

                        cameraCallBack.onActionComplete(Camera.this);

                        return;
                    } else if (desiredScale > scale) {
                        // zoom in
                        scale = (scalingLayout.getScaleX() * zoomSpeed) / (zoomSpeed - 1);

                    } else if (desiredScale < scale) {
                        // zoom out
                        scale = (scalingLayout.getScaleX() * (zoomSpeed - 1)) / zoomSpeed;

                    }

                    scalingLayout.setScaleX(scale);
                    scalingLayout.setScaleY(scale);

                    Log.i("ScaleValue", "Scale: " + String.valueOf(scale));

                    handler.postDelayed(this, 1);
                }
            }
        };

        action.run();
    }

    // The movementSpeed parameter behaves as expected: the larger the movement speed, the faster the camera moves
    // the smaller the movement speed, the slower the camera moves.
    // Movement speed = movementSpeed (pixels)/1 millisecond
    // Like the zoomIn and zoomOut functions, define the action variable, then use it like so:
    // Runnable action = yourCamera.move[direction](handler, movementSpeed);
    // handler.postDelayed(action, delay time for action to start);

    // moveRight and moveLeft will stop moving if the camera domain goes outside of the layout's domain

    public Runnable moveRight(Handler handler, float movementSpeed){
        Runnable action = new Runnable() {
            @Override public void run() {
                if(!fixedPosition) {
                    x = containerLayout.getTranslationX() - movementSpeed;
                    containerLayout.setTranslationX(x);
                    Log.i("CameraMovement", "x: " + String.valueOf(x));
                    if(getRightXPosition() > TitleActivity.WIDTH / TitleActivity.DENSITY){
                        fixedPosition = true;
                    }
                    handler.postDelayed(this, 1);
                }
            }
        };

        return action;
    }

    public Runnable moveLeft(Handler handler, float movementSpeed){
        Runnable action = new Runnable() {
            @Override public void run() {
                if(!fixedPosition) {
                    x = containerLayout.getTranslationX() + movementSpeed;
                    containerLayout.setTranslationX(x);
                    Log.i("CameraMovement", "x: " + String.valueOf(x));
                    if(getLeftXPosition()<0){
                        fixedPosition = true;
                    }
                    handler.postDelayed(this, 1);
                }
            }
        };

        return action;
    }

    public Runnable moveUp(Handler handler, float movementSpeed){
        Runnable action = new Runnable() {
            @Override public void run() {
                if(!fixedPosition) {
                    y = containerLayout.getTranslationY() + movementSpeed;
                    containerLayout.setTranslationY(y);
                    Log.i("CameraMovement", "y: " + String.valueOf(y));
                    handler.postDelayed(this, 1);
                }
            }
        };

        return action;
    }

    public Runnable moveDown(Handler handler, float movementSpeed){
        Runnable action = new Runnable() {
            @Override public void run() {
                if(!fixedPosition) {
                    y = containerLayout.getTranslationY() - movementSpeed;
                    containerLayout.setTranslationY(y);
                    Log.i("CameraMovement", "y: " + String.valueOf(y));
                    handler.postDelayed(this, 1);
                }
            }
        };

        return action;
    }


    // Use this function to move to a desired location (xPosition, yPosition) on our defined game coordinate system, with units DP.
    // CameraMovement will start the moment the function is called
    // and movement speed will behave the same way as in the previous movement functions

    public void moveTo(float xPosition, float yPosition, float movementSpeed){
        moveTo(xPosition, yPosition, movementSpeed, new CameraCallBack() {
            @Override
            public void onActionComplete(Camera camera) {
                Log.i("Camera", camera.toString());
            }
        });
    }

    public void moveTo(float xPosition, float yPosition, float movementSpeed, CameraCallBack cameraCallBack){
        Handler handler = new Handler();

        // this is the margin of error for how close the camera must be to the desired position in order to stop moving
        // calculations are in px
        float ME = movementSpeed;
        float slope, xSign, ySign, xSpeed, ySpeed;

        slope = (yPosition-getYPosition())/(xPosition-getXPosition());

        // handle NaN issues
        if(xPosition - getXPosition() == 0){
            xSpeed = 0;
        }
        else{
            xSign = (xPosition-getXPosition())/Math.abs(xPosition-getXPosition());
            xSpeed = (float)(xSign * Math.abs(movementSpeed * (1/Math.sqrt(1+Math.pow(slope,2)))));
        }

        if(yPosition - getYPosition() == 0) {
            ySpeed = 0;
        }
        else if(xPosition - getXPosition() == 0){
            ySign = (yPosition - getYPosition()) / Math.abs(yPosition - getYPosition());
            ySpeed = ySign * movementSpeed;
        }
        else{
            ySign = (yPosition - getYPosition()) / Math.abs(yPosition - getYPosition());
            ySpeed = ySign * Math.abs(slope * xSpeed);
        }

        Runnable action = new Runnable() {
            @Override
            public void run() {
                if(!fixedPosition) {
                    float distance = (float) (Math.sqrt(Math.pow((xPosition - getXPosition()) * TitleActivity.DENSITY, 2)
                            + Math.pow((yPosition - getYPosition()) * TitleActivity.DENSITY, 2)));

                    Log.i("CameraMovement", "Distance: " + String.valueOf(distance));

                    if (distance < ME && distance > -ME) {
                        // done moving
                        containerLayout.setTranslationX(-((xPosition * TitleActivity.DENSITY) - TitleActivity.WIDTH / 2F));
                        containerLayout.setTranslationY((((yPosition * TitleActivity.DENSITY) - TitleActivity.HEIGHT / 2F)));


                        Log.i("CameraMovement", "Done Moving" + ", " + String.valueOf(containerLayout.getTranslationX()) + ", " +
                                String.valueOf(containerLayout.getTranslationY()));

                        cameraCallBack.onActionComplete(Camera.this);
                        return;
                    }

                    // x-movement

                    x = containerLayout.getTranslationX() - xSpeed;
                    containerLayout.setTranslationX(x);

                    //y-movement
                    y = containerLayout.getTranslationY() + ySpeed;
                    containerLayout.setTranslationY(y);
                    handler.postDelayed(this, 1);
                }
            }
        };

        action.run();
    }

    // get the x coordinate, using our defined game coordinate system, of where the camera is centered at in DP.
    public float getXPosition(){
        x = containerLayout.getTranslationX();
        return (TitleActivity.WIDTH/2F - x)/TitleActivity.DENSITY;
    }

    public void setXPosition(float xPosition){
        if(!fixedPosition) {
            containerLayout.setTranslationX(-((xPosition * TitleActivity.DENSITY) - TitleActivity.WIDTH / 2F));
        }
    }

    public void setLeftXPosition(float leftXPosition){
        if(!fixedPosition){
            containerLayout.setTranslationX(-(((leftXPosition - ((-TitleActivity.WIDTH/ (2 * TitleActivity.DENSITY))
                    + ((scale-1)/(2 * scale)) * (TitleActivity.WIDTH/TitleActivity.DENSITY)))
                    * TitleActivity.DENSITY) - TitleActivity.WIDTH / 2F));
        }
    }

    public void setRightXPosition(float rightXPosition){
        if(!fixedPosition){
            containerLayout.setTranslationX(-(((rightXPosition - ((TitleActivity.WIDTH/ (2 * TitleActivity.DENSITY))
                    - ((scale-1)/(2 * scale)) * (TitleActivity.WIDTH/TitleActivity.DENSITY)))
                    * TitleActivity.DENSITY) - TitleActivity.WIDTH / 2F));
        }
    }

    // get the y coordinate, using our defined game coordinate system, of where the camera is centered at in DP.
    public float getYPosition(){
        y = containerLayout.getTranslationY();
        return (TitleActivity.HEIGHT/2F + y)/TitleActivity.DENSITY;
    }

    public void setYPosition(float yPosition){
        if(!fixedPosition) {
            containerLayout.setTranslationY((((yPosition * TitleActivity.DENSITY) - TitleActivity.HEIGHT / 2F)));
        }
    }

    // use the following four functions to acquire the camera range/domain in terms of the defined coordinate system in DP.
    public float getLeftXPosition(){
        return (getXPosition()-TitleActivity.WIDTH/ (2 * TitleActivity.DENSITY))
                + ((scale-1)/(2 * scale)) * (TitleActivity.WIDTH/TitleActivity.DENSITY);
    }

    public float getRightXPosition(){
        return (getXPosition()+TitleActivity.WIDTH/ (2 * TitleActivity.DENSITY))
                - ((scale-1)/(2 * scale)) * (TitleActivity.WIDTH/TitleActivity.DENSITY);
    }

    public float getTopYPosition(){
        return (getYPosition()+TitleActivity.HEIGHT/ (2 * TitleActivity.DENSITY))
                - ((scale-1)/(2 * scale)) * (TitleActivity.HEIGHT/TitleActivity.DENSITY);
    }

    public float getBottomYPosition(){
        return (getYPosition()-TitleActivity.HEIGHT/ (2 * TitleActivity.DENSITY))
                + ((scale-1)/(2 * scale)) * (TitleActivity.HEIGHT/TitleActivity.DENSITY);
    }

    // Get/set the scale/zoom of the camera
    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        if(!fixedPosition) {
            this.scale = scale;
            scalingLayout.setScaleX(scale);
            scalingLayout.setScaleY(scale);
        }
    }

    // Get/set whether or not the camera is fixed
    public boolean isFixedPosition() {
        return fixedPosition;
    }

    public void setFixedPosition(boolean fixedPosition) {
        this.fixedPosition = fixedPosition;
    }

    public String toString(){
        return "Camera: (" + String.valueOf(getXPosition()) + ", " + String.valueOf(getYPosition()) +
                "), Zoom Scale = " + String.valueOf(scale) +
                ", Domain: (" + getLeftXPosition() + ", " + getRightXPosition() +
                "), Range: (" + getBottomYPosition() + ", " + getTopYPosition() + "), isFixed: "
                + String.valueOf(fixedPosition);
    }

    // interface to deal with asynchronous tasks
    public interface CameraCallBack{
        void onActionComplete(Camera camera);
    }
}

