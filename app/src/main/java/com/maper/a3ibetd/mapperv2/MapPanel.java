package com.maper.a3ibetd.mapperv2;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.graphics.*;
import android.os.*;
import java.util.*;
import android.util.*;
import android.view.View;

public class MapPanel extends SurfaceView implements SurfaceHolder.Callback
{
    private PointF screenPrivot;

    private PointF screenOffset;
    private float x;
    private float y;
    private float scaleFactor=1;

    private MainThread mainThread;
    private MapText textFPS;
    private MapText textWidthXHeight;
    private MapText textMemUsage;
    private MapText textButtonPress;
    private MapText textButtonPress2;

    private boolean checkMemory=false;

    private Debug.MemoryInfo memInfo;
    private MyPoint testRectangle;

    public String stringFps="FPS unknown";
    public String stringWidthXHeight="WxH unknown";
    public String stringMemUsage="Mem unknown";
    public String stringButtonPress="Button unknown";
    public String stringButtonPress2="Button unknown";

    private boolean gameStart=false;
    private PointF windowSize;
    private MapCamera mapCamera;

    public int currentFloor=1;
    public HashMap<Integer,MapWallPoint> MapWallPoints;

    protected boolean movable;

    public MapPanel(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);

        getHolder().addCallback(this);
        mainThread= new MainThread(getHolder(),this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder p1)
    {
        // TODO: Implement this method

        windowSize=new PointF(0,0);
        //pointOfLook= new PointF(0,0);
        MapWallPoints=new HashMap<>();
        testRectangle=new MyPoint(Color.rgb(200,0,0),windowSize.x/2,windowSize.y/2,0,100,100);
        textMemUsage=new MapText(Color.rgb(255,255,0),200,200);
        textWidthXHeight=new MapText(Color.rgb(255,255,0),200,250);
        textFPS=new MapText(Color.rgb(0,255,0),200,150);
        textButtonPress=new MapText(Color.rgb(0,255,0),200,300);
        textButtonPress2=new MapText(Color.rgb(0,255,0),200,350);

        stringButtonPress="just started and nothing";
        memInfo = new Debug.MemoryInfo();

        mainThread.setRunning(true);
        mainThread.start();
        gameStart=true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder p1)
    {
        // TODO: Implement this method
        boolean retry = true;
        while(retry)
        {
            try{mainThread.setRunning(false);
                mainThread.join();
                retry=false;
            }catch(InterruptedException e)
            {e.printStackTrace();}
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
    {
        // TODO: Implement this method

    }
    public void update()
    {
        if(gameStart)
        {
            return;
        }
        if (checkMemory)
        {
            Debug.getMemoryInfo(memInfo);
            long res = memInfo.getTotalPrivateDirty();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                res += memInfo.getTotalPrivateClean();
            stringMemUsage=Long.toString(res);
        }

        screenPrivot.x=mapCamera.location.x;
        screenPrivot.y=mapCamera.location.y;

        mapCamera.update(screenOffset);
        screenOffset.x=0;
        screenOffset.y=0;
        textFPS.update(stringFps);
        textWidthXHeight.update(stringWidthXHeight);
        textMemUsage.update(stringMemUsage);
        textButtonPress.update(stringButtonPress);
        textButtonPress2.update(stringButtonPress2);
        testRectangle.setWorldLocation(new PointF(x,y));

    }
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (gameStart) {
            movable=false;
            screenOffset=new PointF(0,0);
            screenPrivot=new PointF(0,0);
            windowSize.x = canvas.getWidth();
            windowSize.y = canvas.getHeight();
            x = windowSize.x / 2;
            y = windowSize.y / 2;
            stringWidthXHeight = windowSize.x + "x" + windowSize.y;
            testRectangle.setWorldLocation(windowSize.x / 2, windowSize.y / 2);
            mapCamera = new MapCamera(windowSize.x / 2, windowSize.y / 2, 0, 1);
            gameStart = false;
        }
        canvas.save();
        ///ИНТЕРФЕЙС И ЧАНКИ
        canvas.drawColor(Color.rgb(200, 200, 200));
        testRectangle.draw(canvas);
        textMemUsage.draw(canvas);
        textWidthXHeight.draw(canvas);
        textFPS.draw(canvas);
        textButtonPress.draw(canvas);
        textButtonPress2.draw(canvas);

        ///СМЕЩЕНИЕ СЕТКИ
        mapCamera.draw(canvas, true);

        ///ОБЪЕКТЫ


       // for (int i = 0; i < MapWallPoints.size(); i++) {
        //    MapWallPoints.get(i).draw(canvas);
        //}

        for(Map.Entry<Integer, MapWallPoint> entry : MapWallPoints.entrySet())
        {
            //Integer key = entry.getKey();
            MapWallPoint value = entry.getValue();
            value.draw(canvas);
        }

        canvas.restore();
    }
        //

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO: Implement this method
       //x=event.getX();
       //y=event.getY();
        float tempX=event.getX(),tempY=event.getY();
        int action=event.getAction();
        switch(action)
            {
                case MotionEvent.ACTION_DOWN:
                    x=event.getX();
                    y=event.getY();
                    if (!movable) {
                        int tempKey=-1;
                        screenPrivot.x=mapCamera.location.x;
                        screenPrivot.y=mapCamera.location.y;
                        for(Map.Entry<Integer, MapWallPoint> entry : MapWallPoints.entrySet())
                        {
                            Integer key = entry.getKey();
                            MapWallPoint value = entry.getValue();
                            RectF tempRect=new RectF(x-10-screenPrivot.x,y-10-screenPrivot.y,x+10-screenPrivot.x,y+10-screenPrivot.y);

                            if(value.collision(tempRect))

                                tempKey=key;
                        }
                        if(tempKey!=-1)
                        removeWalls(tempKey);

                    }
                    stringButtonPress="pressed";
                    break;
                case MotionEvent.ACTION_MOVE:
                    stringButtonPress="moving";
                    tempX=event.getX();
                    tempY=event.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    x=event.getX();
                    y=event.getY();
                    break;
                default:
                    break;
            }
            stringButtonPress=x+" "+y;

        stringButtonPress2=(mapCamera.location.x+tempX)+" "+(mapCamera.location.y+tempY);
        if (movable) {
            screenOffset.x = (x - tempX);
            screenOffset.y = (y - tempY);
        }
        x=event.getX();
        y=event.getY();
        return true;
    }

    public void clearMap()
    {
        MapWallPoints.clear();
    }
    public void addWalls(int index,MapWallPoint wallPoint)
    {
        MapWallPoints.put(index,wallPoint);
    }
    public void removeWalls(int index)
    {
        for(Map.Entry<Integer, MapWallPoint> entry : MapWallPoints.entrySet())
        {

            Integer key = entry.getKey();
            MapWallPoint value = entry.getValue();
           // value.walls.;
            value.removeWall(index);
        }
        MapWallPoints.remove(index);
    }

    public void putWallPoint(HashMap<Integer,MapWallPoint> tempMapWallPoints)
    {

        MapWallPoints.put(MapWallPoints.size(), new MapWallPoint(Color.rgb(0, 255, 0), x-screenPrivot.x, y-screenPrivot.y, 0, 50, 50, MapWallPoints.size(),tempMapWallPoints));
    }
}

