package com.maper.a3ibetd.mapperv2.Objects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.maper.a3ibetd.mapperv2.Objects.MapObject;

public class MapText extends MapObject
{
    private int color;
    private String text;
    private Paint paint;
    public MapText()
    {
        activeElement=false;
    }

    public MapText(int color,float x,float y)
    {
        this.paint=new Paint();
        this.location= new PointF(x,y);
        this.paint.setTextSize(48f);
        this.color=color;
        this.paint.setColor(this.color);
        this.text="";
        this.activeElement=true;
    }

    @Override
    public void draw(Canvas canvas)
    {
        // TODO: Implement this method
        if(activeElement)
        {
            canvas.drawText(text,location.x,location.y,paint);
        }
    }

    @Override
    public void update()
    {
        // TODO: Implement this method
    }



    public void update(String text)
    {
        // TODO: Implement this method
        this.text=text;
    }

    public void setColor(int color)
    {
        this.color=color;
    }

}
