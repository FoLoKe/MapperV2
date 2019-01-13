package com.maper.a3ibetd.mapperv2.Objects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class MapObject
{
    protected PointF location;
    protected float rotation;
    protected PointF targetLocation;
    protected float targetRotation;
    protected Paint paint;
    protected RectF collisionRect;
    protected float sizeX;
    protected float sizeY;
    protected boolean activeElement=false;
    public MapObject()
    {
        activeElement=false;
    }
    public void draw(Canvas canvas)
    {
    }
    public void update()
    {
    }
    public void setWorldLocation(PointF newLocation)
    {
        location=newLocation;
        collisionRect.set(location.x-sizeX/2,location.y-sizeY/2,location.x+sizeX/2,location.y+sizeY/2);
    }
    public void setWorldRotation(float newRotation)
    {
        rotation=newRotation;
    }
    public void addWorldRotation(float addRotation)
    {
        targetRotation=rotation+addRotation;
    }
    public void addWorldLocation(PointF addLocation)
    {
        targetLocation.x=location.x+addLocation.x;
        targetLocation.y=location.y+addLocation.y;
    }
    public void setWorldLocation(float x,float y)
    {
        this.location.x=x;
        this.location.y=y;
        collisionRect.set(location.x-sizeX/2,location.y-sizeY/2,location.x+sizeX/2,location.y+sizeY/2);
    }
    public void addWorldLocation(float dx,float dy)
    {
        targetLocation.x=location.x+dx;
        targetLocation.y=location.y+dy;
    }
    public PointF getWorldLocation()
    {
        return location;
    }
}
