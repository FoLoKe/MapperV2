package com.maper.a3ibetd.mapperv2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class MapCamera extends MapObject
{

    //private Bitmap image;
    //private float locationX;
    //private float locationY;
    //private PointF tempLocation;
    //private int rotation;
    //private int privotX;
    //private int privotY;
    //private float dX;
    //private float dY;
    //private boolean gameStarted=true;
    public MapCamera(float locationX,float locationY,float rotation,float zoom)
    {
        this.location=new PointF(locationX,locationY);
       // this.location.x=locationX;
       // this.location.y=locationY;
        this.rotation=rotation;
        //this.
        //this.image=resorce;
        //privotX=//image.getHeight()/2;
        //privotY=//image.getWidth()/2;
        //this.locationX+=privotX;
        //this.locationY+=privotY;
        //tempLocation=new PointF(0,0);
    }

    @Override
    public void draw(Canvas canvas)
    {
        // TODO: Implement this method
    }


    public void draw(Canvas canvas,boolean drawPrivot)
    {

      Paint paint= new Paint();
      paint.setColor(Color.rgb(255,0,255));
        //canvas.rotate(rotation,location.x,location.y);
        if(drawPrivot)
            canvas.drawRect(location.x-40,location.y-40,location.x+40,location.y+40,paint);
        canvas.translate(location.x,location.y);
       // canvas.
        //canvas.drawBitmap(image,locationX-privotX,locationY-privotY,paint);

       // rotation=0;

       // canvas.restore();

        // TODO: Implement this method
    }

    @Override
    public void update()
    {
        // TODO: Implement this method
        //locationX+=dX;
        //locationY+=dY;
        //tempLocation.x=dX;
        //tempLocation.y=dY;
        //dX=0;
        //dY=0;
    }

    public void update(PointF offsetXY)
    {
        location.x-=offsetXY.x;
        location.y-=offsetXY.y;
    }
    public void addRotation(int degree)
    {
        this.rotation+=degree-90;
        if (this.rotation>360)
            this.rotation-=360;
        if (this.rotation<0)
            this.rotation+=360;

    }
    public int findRotation(PointF point)
    {
        //double A=Math.atan2(locationY-point.y,locationX-point.x)/3.14*180;
        //return (int)A;
        return 0;
    }
    public void setLocation(PointF location)
    {
        PointF tempMoveLocation=new PointF(0,0);
        tempMoveLocation.x=this.location.x-location.x;
        tempMoveLocation.y=this.location.y-location.y;
        this.location.x+=tempMoveLocation.x;
        this.location.y+=tempMoveLocation.y;

    }
    public float getLocationX()
    {
        return location.x;
    }
    public float getLocationY()
    {
        return location.y;
    }

}
