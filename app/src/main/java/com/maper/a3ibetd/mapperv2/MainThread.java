package com.maper.a3ibetd.mapperv2;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

//import android.


public class MainThread extends Thread
{
    private int FPS =120;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private MapPanel mapPanel;
    private boolean running;
    private static Canvas canvas;

    public MainThread(SurfaceHolder surfaceHolder,MapPanel mapPanel)
    {
        super();
        this.surfaceHolder=surfaceHolder;
        this.mapPanel=mapPanel;
    }

    @Override
    public void run()
    {
        // TODO: Implement this method
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime =0;
        int frameCount =0;
        long targetTime =1000/FPS;

        while(running)
        {
            startTime=System.nanoTime();
            canvas = null;

            try
            {
                canvas=this.surfaceHolder.lockCanvas();
                synchronized(surfaceHolder)
                {
                    this.mapPanel.update();
                    this.mapPanel.draw(canvas);
                }
            }
            catch(Exception e){}

            finally{if(canvas!=null)
                try{
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }catch(Exception e){e.printStackTrace();}

            }
            timeMillis=(System.nanoTime()-startTime)/1000000;
            waitTime=targetTime-timeMillis;

            try{
                this.sleep(waitTime);
            }catch(Exception e){}

            totalTime += System.nanoTime()-startTime;
            frameCount++;
            if(frameCount==FPS)
            {
                averageFPS= 1000/((totalTime/frameCount)/1000000);
                frameCount=0;
                totalTime=0;

                mapPanel.stringFps=Double.toString(averageFPS);
            }
        }
    }
    public void setRunning(boolean b)
    {
        running=b;
    }
}
