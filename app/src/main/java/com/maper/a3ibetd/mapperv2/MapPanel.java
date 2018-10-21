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
    private PointF pointOfTouch;
    private PointF screenOffset;
    private float x;
    private float y;
    protected float scaleFactor=1;
    float canvasW=1,canvasH=1;
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
    private RectF cursorRect;


    /////onTouchEvent
    double t1=0;
    double t2=0;
    float offsetX=0,offsetY=0,firstX=0,firstY=0;
    float MovementX=0,MovementY=0;
    /////
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
        cursorRect=new RectF(0,0,1,1);
        MapWallPoints=new HashMap<>();
        testRectangle=new MyPoint(Color.rgb(200,0,0),windowSize.x/2,windowSize.y/2,0,100,100);
        textMemUsage=new MapText(Color.rgb(255,255,0),200,200);
        textWidthXHeight=new MapText(Color.rgb(255,255,0),200,250);
        textFPS=new MapText(Color.rgb(0,255,0),200,150);
        textButtonPress=new MapText(Color.rgb(0,255,0),200,300);
        textButtonPress2=new MapText(Color.rgb(0,255,0),200,350);
        pointOfTouch=new PointF();
        stringButtonPress="just started and nothing";
        memInfo = new Debug.MemoryInfo();
        mapCamera = new MapCamera(0, 0, 1,new PointF(0,0));
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

        //screenPrivot.x=mapCamera.location.x;
        //screenPrivot.y=mapCamera.location.y;



        textFPS.update(stringFps);
        textWidthXHeight.update(stringWidthXHeight);
        textMemUsage.update(stringMemUsage);
        textButtonPress.update(mapCamera.getWorldLocation()+"");
        textButtonPress2.update(pointOfTouch+"");
        testRectangle.setWorldLocation(new PointF(x,y));

        mapCamera.tick(scaleFactor,mapCamera.getWorldLocation().x+screenOffset.x,mapCamera.getWorldLocation().y+screenOffset.y);
        screenOffset.x=0;
        screenOffset.y=0;

    }
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (gameStart) {
            canvasW=canvas.getWidth();
            canvasH=canvas.getHeight();
            movable=false;
            screenOffset=new PointF(0,0);
            screenPrivot=new PointF(0,0);
            windowSize.x = canvas.getWidth();
            windowSize.y = canvas.getHeight();
            x = windowSize.x / 2;
            y = windowSize.y / 2;
            stringWidthXHeight = windowSize.x + "x" + windowSize.y;
            testRectangle.setWorldLocation(windowSize.x / 2, windowSize.y / 2);

            gameStart = false;
        }
        //setTranslationX(1000);
        //setRotation(10);
        //this.setX(1000);
        //canvas.
        canvas.save();
        ///ИНТЕРФЕЙС И ЧАНКИ
        canvas.drawColor(Color.rgb(200, 200, 200));
       // testRectangle.draw(canvas);
        textMemUsage.draw(canvas);
        textWidthXHeight.draw(canvas);
        textFPS.draw(canvas);
        textButtonPress.draw(canvas);
        textButtonPress2.draw(canvas);
        Paint paint=new Paint();
        paint.setColor(Color.GREEN);

        ///СМЕЩЕНИЕ СЕТКИ
       // mapCamera.render(canvas);
        canvas.translate(-mapCamera.getWorldLocation().x+canvas.getWidth()/2,-mapCamera.getWorldLocation().y+canvas.getHeight()/2);
        canvas.scale(mapCamera.getScale(),mapCamera.getScale(),mapCamera.getWorldLocation().x,mapCamera.getWorldLocation().y);
        ///ОБЪЕКТЫ


       // for (int i = 0; i < MapWallPoints.size(); i++) {
        //    MapWallPoints.get(i).draw(canvas);
        //}
        canvas.drawRect(cursorRect,paint);
        for(Map.Entry<Integer, MapWallPoint> entry : MapWallPoints.entrySet())
        {
            //Integer key = entry.getKey();
            MapWallPoint value = entry.getValue();
            value.draw(canvas);
        }
       canvas.restore();

       // canvas.scale(scaleFactor,scaleFactor);
        //mapCamera.draw(canvas, true);
        //canvas.restore();

    }
        //

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO: Implement this method
       //x=event.getX();
       //y=event.getY();
        x=event.getX();
        y=event.getY();

        float tempX=event.getX(),tempY=event.getY();

        pointOfTouch.set((x-canvasW/2)/mapCamera.getScale()+mapCamera.getWorldLocation().x,(y-canvasH/2)/mapCamera.getScale()+mapCamera.getWorldLocation().y);
        cursorRect=new RectF(pointOfTouch.x,pointOfTouch.y,pointOfTouch.x+40,pointOfTouch.y+40);
        int action=event.getAction();
        switch(action)
            {
                case MotionEvent.ACTION_DOWN:
                    MovementX=event.getX();
                    MovementY=event.getY();
                    if (!movable) {
                        t1=System.currentTimeMillis();
                        firstX=event.getX();
                        firstY=event.getY();


                        screenPrivot.x=mapCamera.getWorldLocation().x;
                        screenPrivot.y=mapCamera.getWorldLocation().y;



                    }
                    stringButtonPress="pressed";
                    break;
                case MotionEvent.ACTION_MOVE:
                    stringButtonPress="moving";

                    tempX=event.getX();
                    tempY=event.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    t2=System.currentTimeMillis();
                    offsetX=firstX-event.getX();
                    offsetY=firstY-event.getY();
                    int tempKey=-1;
                    if(Math.sqrt(offsetX*offsetX+offsetY*offsetY)<60)
                        if((t2-t1)/1000>1) {
                            for (Map.Entry<Integer, MapWallPoint> entry : MapWallPoints.entrySet()) {
                                Integer key = entry.getKey();
                                MapWallPoint value = entry.getValue();
                                if (value.collision(cursorRect))
                                    tempKey = key;
                            }
                            if (tempKey != -1)
                            {
                                ((MainActivity)getContext()).PointFunction(tempKey);

                            }
                        }
                    break;
                default:
                    break;
            }
            //stringButtonPress=x+" "+y;

        stringButtonPress2=(mapCamera.getWorldLocation().x+tempX)+" "+(mapCamera.getWorldLocation().y+tempY);
        if (movable) {
            screenOffset.x += (MovementX - tempX);
            screenOffset.y += (MovementY - tempY);
        }
        MovementX=event.getX();
        MovementY=event.getY();
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
    public void addMapWallPoint(int index,float x,float y,HashMap<Integer,MapWallPoint> tempMapWallPoints)
    {
        while(MapWallPoints.containsKey(index))
            index++;
        MapWallPoints.put(index, new MapWallPoint(Color.rgb(0, 255, 0), x,y, 0, 50, 50, index,tempMapWallPoints));
    }
    public void putWallPoint(HashMap<Integer,MapWallPoint> tempMapWallPoints)
    {

        int index=0;
        while(MapWallPoints.containsKey(index))
            index++;
        MapWallPoints.put(index, new MapWallPoint(Color.rgb(0, 255, 0), pointOfTouch.x, pointOfTouch.y, 0, 50, 50, index,tempMapWallPoints));
    }
    public void changeWallPoint(int id, float x,float y, HashMap<Integer,MapWallPoint> tempMapWallPoints)
    {
        MapWallPoints.get(id).setWorldLocation(x,y);
        MapWallPoints.get(id).setNeigbours(tempMapWallPoints);
    }
    public void removePoint(int id)
    {
       removeWalls(id);
    }
}

