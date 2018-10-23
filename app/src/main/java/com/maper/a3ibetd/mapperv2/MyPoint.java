package com.maper.a3ibetd.mapperv2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class MyPoint extends MapObject
{
    //private PointF offset;
    public MyPoint()
    {
        activeElement=false;
    }
    public MyPoint(int colorRGB,float x,float y,float rotation,float sizeX,float sizeY)
    {
        this.paint=new Paint();
        this.location=new PointF(x,y);
        this.paint.setColor(colorRGB);
        this.paint.setStrokeWidth(5);
        this.paint.setStyle(Paint.Style.STROKE);
        this.rotation=rotation;
        this.sizeX=sizeX;
        this.sizeY=sizeY;
        this.collisionRect=new RectF(location.x-sizeX/2,location.y-sizeY/2,location.x+sizeX/2,location.y+sizeY/2);
        this.activeElement=true;
    }

    public void draw(Canvas canvas)
    {
        if (activeElement)
        {
            canvas.save();
            //canvas.translate(location.x,location.y);
            canvas.drawRect(collisionRect,paint);
            canvas.restore();
        }
    }


    @Override
    public void update()
    {
        // TODO: Implement this method
    }

}
