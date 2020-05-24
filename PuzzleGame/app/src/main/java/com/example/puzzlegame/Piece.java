package com.example.puzzlegame;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.ImageView;

public class Piece extends androidx.appcompat.widget.AppCompatImageView {
    boolean isDown;
    boolean wasMatch;
    PointF curLoc;
    PointF rightLoc;
    PointF preLoc;


    public Piece(Context context, PointF rightLoc) {
        super(context);
        this.curLoc = new PointF();

        this.rightLoc = new PointF();
        this.rightLoc.x = rightLoc.x;
        this.rightLoc.y = rightLoc.y;

        this.preLoc = new PointF();
    }

    public PointF getPreviousLocation() {return preLoc;}

    public void setPreviousLocation(float x, float y) {
        preLoc.x = x;
        preLoc.y = y;
    }

    public PointF getCurrentLocation() {return curLoc;}

    public void setCurrentLocation(float x, float y) {
        curLoc.x = x;
        curLoc.y = y;
    }

    public boolean isMatch() {
        return curLoc.x == rightLoc.x && curLoc.y == rightLoc.y;
    }

    public void setWasMatch(boolean wasMatch) {
        this.wasMatch = wasMatch;
    }

    public boolean getWasMatch() {
        return wasMatch;
    }
}
