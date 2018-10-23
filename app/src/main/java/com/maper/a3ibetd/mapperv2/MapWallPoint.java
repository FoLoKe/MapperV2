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
    private int myIndex;
    private int[] indexes;
    private String text;

    public MapWallPoint(int colorRGB,float x,float y,float rotation,float sizeX,float sizeY,int index,HashMap<Integer, MapWallPoint> walls)
    {
        this.myIndex=index;
        this.indexes=new int[walls.size()];
        //НА КРАЙ ДЕЛАТЬ ТАК
        int i=0;
        for(Map.Entry<Integer, MapWallPoint> entry : walls.entrySet())
        {
            this.indexes[i]=entry.getKey();
            i++;
            this.walls.put(entry.getKey(),entry.getValue());
        }
        //НЕ ДЕЛАТЬ ТАК (ССЫЛОЧНОЕ СОХРАНЕНИЕ) this.walls=walls;

        this.paint=new Paint();
        this.location=new PointF(x,y);
        this.paint.setColor(colorRGB);
        this.paint.setTextSize(48f);
        this.paint.setStrokeWidth(10);
        this.rotation=rotation;
        this.sizeX=sizeX;
        this.sizeY=sizeY;

        this.collisionRect=new RectF(location.x-sizeX/2,location.y-sizeY/2,location.x+sizeX/2,location.y+sizeY/2);
        this.text="W "+index;
        this.activeElement=true;
    }

    public void draw(Canvas canvas)
    {
        if (activeElement)
        {
            canvas.save();
            canvas.drawRect(collisionRect,paint);
            int tempColor = paint.getColor();
            this.paint.setStyle(Paint.Style.STROKE);
            for(Map.Entry<Integer,MapWallPoint> entry: walls.entrySet())
            {
                if (entry.getValue()!=null) {
                    canvas.drawLine(entry.getValue().getWorldLocation().x, entry.getValue().getWorldLocation().y, location.x, location.y, paint);
                }
                else
                {
                    walls.remove(entry.getKey());
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
                writer.newLine();
                writer.write(Integer.toString(keyTwo));
            }

            writer.newLine();
            writer.write("***");
        }catch(IOException e)
        {return "DATA WRITE ERROR: "+e.toString();}
        return "ALL OK";
    }

    public int getMyIndex()
    {
        return myIndex;
    }
    public boolean collision(RectF rect)
    {
        boolean debug=collisionRect.intersect(rect);
        if(debug)
            collisionRect.set(location.x-sizeX/2,location.y-sizeY/2,location.x+sizeX/2,location.y+sizeY/2);
       return debug;
    }
    public void setNeigbours(HashMap<Integer,MapWallPoint> tempWalls)
    {
        //////STOP WORKING WITH LOCALS!!!!
        //walls=tempWalls; <-WRONG
        walls.clear();
        for(Map.Entry<Integer,MapWallPoint> entry: tempWalls.entrySet())
        {
            walls.put(entry.getKey(),entry.getValue());
        }
    }

}
