package com.maper.a3ibetd.mapperv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class scalebleBackGround
{
    private Bitmap image;
    public scalebleBackGround(Bitmap resource)
    {
        this.image=resource;

    }
    public void draw(Canvas canvas)
    {
        Paint paint=new Paint();

        int HEIGHT =canvas.getHeight();
        int WIDTH= canvas.getWidth();
        for(int wI=0;wI<=WIDTH;wI+=72)
        {
            for(int hI=0;hI<=HEIGHT;hI+=72)
                canvas.drawBitmap(image,wI,hI,paint);

        }

    }
    public void update()
    {

    }
}
