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
        this.location.x=locationX;
        this.location.y=locationY;
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

        //canvas.save();
        Paint paint= new Paint();
        paint.setColor(Color.rgb(255,0,0));
        canvas.rotate(rotation,location.x,location.y);
        //canvas.drawBitmap(image,locationX-privotX,locationY-privotY,paint);
        //if(drawPrivot)
        canvas.drawRect(location.x-4,location.y-4,location.x+4,location.y+4,paint);
        rotation=0;
        //canvas.restore();

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
    public void addForce(float dX,float dY,float force)
    {
        float tdX=0,tdY=0;
        if (dX<0)
            tdX=-dX;
        else
            tdX=dX;
        if (dY<0)
            tdY=-dY;
        else
            tdY=dY;
        float vectorPercentage=(tdX+tdY);
        //if(vectorPercentage!=0)
        {
            //this.dY=dY/vectorPercentage*force;//vectorPecentage;
            //this.dX=dX/vectorPercentage*force;
        }
        //else
        {
            //this.dX=this.dY=0;
        }

    }
    public float getLocationX()
    {
        return location.x;
    }
    public float getLocationY()
    {
        return location.y;
    }
    public float getDirectionX()
    {
        return 0;//tempLocation.x;
    }
    public float getDirectionY()
    {
        return 0;//tempLocation.y;
    }
}
