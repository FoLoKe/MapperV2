package com.maper.a3ibetd.mapperv2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapWallPoint extends MapObject
{
    public HashMap<Integer, MapWallPoint> walls=new HashMap<>(100);
    public int myIndex;
    private String text;
    //int wallsCount[];
    public MapWallPoint()
    {
        activeElement=false;
    }
    public MapWallPoint(int colorRGB,float x,float y,float rotation,float sizeX,float sizeY,int index,HashMap<Integer, MapWallPoint> walls)
    {
        this.myIndex=index;
        this.walls=walls;
        this.paint=new Paint();
        this.location=new PointF(x,y);
        this.paint.setColor(colorRGB);
        this.paint.setTextSize(48f);
        this.paint.setStrokeWidth(10);
        //this.wallsCount=0;
        this.rotation=rotation;
        this.sizeX=sizeX;
        this.sizeY=sizeY;
        this.collisionRect=new RectF(location.x-sizeX/2,location.y-sizeY/2,location.x+sizeX/2,location.y+sizeY/2);
        this.text="W"+index;
        //if (this.walls.containsKey(myIndex))
        {
            //	this.walls.remove(myIndex);
        }
        this.activeElement=true;
    }

    public void draw(Canvas canvas)
    {
        if (activeElement)
        {

            canvas.save();
            //canvas.translate(location.x,location.y);
            canvas.drawRect(collisionRect,paint);
            int tempColor = paint.getColor();
            this.paint.setStyle(Paint.Style.STROKE);


            for(int i=0;i<walls.size();i++)
            {
                for(Map.Entry<Integer, MapWallPoint> entry : walls.entrySet())
                {
                    MapWallPoint valueTwo = entry.getValue();
                    canvas.drawLine(valueTwo.getWorldLocation().x,valueTwo.getWorldLocation().y,location.x,location.y,paint);
                }

            }

            this.paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(255,255,0));
            canvas.drawText(text,location.x,location.y-50,paint);
            paint.setColor(tempColor);
            canvas.restore();

        }
    }
    public void addWall(MapWallPoint newWall)
    {
        walls.put(newWall.myIndex,newWall);
    }
    public void removeWall(int index)
    {
        walls.remove(index);
    }
    @Override
    public void update()
    {
        // TODO: Implement this method
    }
    public void recombineOnLoad(HashMap<Integer, MapWallPoint> allWalls)
    {
        walls.clear();
        //for(int i=0;i<wallsCount.length;i++)
        //{
        //	walls.put(wallsCount[i],allWalls.get(wallsCount[i]));
        //}
    }
    public String save(BufferedWriter writer)
    {
        try{
            writer.newLine();
            writer.write(Integer.toString(myIndex));
            writer.newLine();
            writer.write(Float.toString(location.x));
            writer.newLine();
            writer.write(Float.toString(location.y));
            for(Map.Entry<Integer, MapWallPoint> entryTwo : walls.entrySet())
            {
                Integer keyTwo = entryTwo.getKey();
                //MapWallPoint valueTwo = entryTwo.getValue();
                writer.newLine();
                writer.write(Integer.toString(keyTwo));
            }

            writer.newLine();
            writer.write("***");
        }catch(IOException e)
        {return e.toString();}
        return "1";
    }
}
