package com.maper.a3ibetd.mapperv2;

import android.graphics.PointF;


import java.lang.annotation.Target;
import android.graphics.*;

public class MapCamera {
    private PointF location;
    private PointF pointOfLook;
    private float screenXcenter,screenYcenter;
    private float scale;
    private RectF screenRect;
    public MapCamera(float x,float y,float scale,PointF target)
    {
        this.pointOfLook=target;
        this.location=new PointF(x,y);
        this.scale=scale;
        this.screenRect=new RectF(0,0,1,1);
    }

    public void tick(float scale,float x,float y)
    {
        this.scale=scale;
        this.location.x= x;
        this.location.y= y;
        pointOfLook.x=x;
        pointOfLook.y=y;
    }

    public void render(Canvas canvas)
    {
        Paint tPaint=new Paint();
        tPaint.setStyle(Paint.Style.STROKE);
        tPaint.setColor(Color.rgb(0,255,0));
        canvas.drawRect(screenRect,tPaint);
        canvas.drawCircle((pointOfLook).x,(pointOfLook).y,canvas.getHeight()/(8*scale),tPaint);
        canvas.drawCircle((pointOfLook).x,(pointOfLook).y,canvas.getHeight()/(2*scale),tPaint);

    }

    public float getxOffset()
    {
        return location.x;
    }

    public float getyOffset()
    {
        return location.y;
    }

    public void setPointOfLook(PointF pointOfLook) {
        this.pointOfLook = pointOfLook;
    }

    public void setScreenXcenter(float screenXcenter) {
        this.screenXcenter = screenXcenter;
    }

    public void setScreenYcenter(float screenYcenter)
    {
        this.screenYcenter=screenYcenter;
    }

    public float getScale()
    {
        return scale;
    }

    public float getScreenXcenter() {
        return screenXcenter;
    }

    public float getScreenYcenter() {
        return screenYcenter;
    }

    public PointF getWorldLocation()
    {
        return location;
    }
    public void setScreenRect(float screenW,float screenH)
    {
        screenRect.set(pointOfLook.x-screenW/(2*scale),pointOfLook.y-screenH/(2*scale),pointOfLook.y+screenW/(2*scale),pointOfLook.y+screenH/(2*scale));
    }
    public RectF getScreenRect()
    {
        return screenRect;
    }
}
