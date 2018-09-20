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

public class MapPanel extends SurfaceView implements SurfaceHolder.Callback
{
    private float startX;
    private float startY;
    private PointF pointOfLook;
    private float x;
    private float y;
    private float scaleFactor=1;

    private MainThread mainThread;
    //private scalebleBackGround Bg;
    private MapText textFPS;
    private MapText textWidthXHeight;
    private MapText textMemUsage;
    private MapText textButtonPress;

    private boolean checkMemory=true;

    private Debug.MemoryInfo memInfo;
    private MyPoint testRectangle;

    //private MyPoint[] WallPoints;

    //private MapCharacter player;

    public String stringFps="FPS unknown";
    public String stringWidthXHeight="WxH unknown";
    public String stringMemUsage="Mem unknown";
    public String stringButtonPress="Button unknown";



    private boolean gameStart=false;
    private PointF windowSize;
    private PointF windowOffset;
    private boolean actionPreformed=false;

    public int currentFloor=1;
    public HashMap<Integer,MapWallPoint> MapWallPoints;

    //private final String LOG_TAG = "myLogs";

   //private final String FILENAME = "file";
    private Context FileContext;

    public MapPanel(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        this.FileContext=context;
        getHolder().addCallback(this);
        mainThread= new MainThread(getHolder(),this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder p1)
    {
        // TODO: Implement this method

        windowSize=new PointF(0,0);
        pointOfLook= new PointF(0,0);
        MapWallPoints=new HashMap<>();
        testRectangle=new MyPoint(Color.rgb(200,0,0),windowSize.x/2,windowSize.y/2,0,100,100);
        textMemUsage=new MapText(Color.rgb(255,255,0),200,200);
        textWidthXHeight=new MapText(Color.rgb(255,255,0),200,250);
        textFPS=new MapText(Color.rgb(0,255,0),200,150);
        textButtonPress=new MapText(Color.rgb(0,255,0),200,300);

        stringButtonPress="just started and nothing";
        memInfo = new Debug.MemoryInfo();
        //Bg=new scalebleBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.map_desk_tile));
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
    public void update(Canvas canvas)
    {
        if(gameStart)
        {
            return;
        }
        //windowOffset.x=player.getLocationX()-windowSize.x/2;
        //windowOffset.y=player.getLocationY()-windowSize.y/2;

        if (checkMemory)
        {
            Debug.getMemoryInfo(memInfo);
            long res = memInfo.getTotalPrivateDirty();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                res += memInfo.getTotalPrivateClean();
            stringMemUsage=Long.toString(res);
        }


        textFPS.update(stringFps);
        textWidthXHeight.update(stringWidthXHeight);
        textMemUsage.update(stringMemUsage);
        textButtonPress.update(stringButtonPress);
    }
    public void draw(Canvas canvas)
    {
        if (gameStart)
        {
            //windowOffset= new PointF(0,0);
            windowSize.x=canvas.getWidth();
            windowSize.y=canvas.getHeight();

            stringWidthXHeight=windowSize.x+"x"+windowSize.y;
            testRectangle.setWorldLocation(windowSize.x/2,windowSize.y/2);
            gameStart=false;
        }
        canvas.drawColor(Color.rgb(200,200,200));
        textMemUsage.draw(canvas);
        textWidthXHeight.draw(canvas);
        textFPS.draw(canvas);
        textButtonPress.draw(canvas);

        for (int i=0;i<MapWallPoints.size();i++)
        {
            MapWallPoints.get(i).draw(canvas);
        }
        testRectangle.draw(canvas);
        //super.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO: Implement this method
        x=event.getX();
        y=event.getY();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                MapWallPoints.put(MapWallPoints.size(),new MapWallPoint(Color.rgb(0,255,0),x,y,0,50,50,MapWallPoints.size(),MapWallPoints));
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    public void clearMap()
    {
        MapWallPoints.clear();
    }
    public void addWalls(int index,MapWallPoint wallPoint)
    {

        MapWallPoints.put(index,wallPoint);
    }
    public int getWallPointsCount()
    {
        return MapWallPoints.size();
    }

}
