package com.maper.a3ibetd.mapperv2.Objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class Tile {

    int x;
    int y;
    Bitmap image;
    boolean renderable;
    protected RectF collisionBox;




    protected int inventoryMaxCapacity=5;
    public Tile(int x, int y, Bitmap asset)
    {
        this.x=x;
        this.y=y;
        this.image=asset;
        renderable=false;
        this.collisionBox=new RectF(x,y,x+image.getWidth(),y+image.getHeight());
    }

    public void render(Canvas canvas) {

        if (!renderable)
            return;
        canvas.save();
        canvas.drawBitmap(image, x, y, new Paint());
        canvas.restore();

        Paint tPaint = new Paint();
        tPaint.setColor(Color.rgb(0, 255, 0));
        tPaint.setStyle(Paint.Style.STROKE);
    }
    public RectF getCollsionBox()
    {
        return collisionBox;
    }
    public void setRenderable(boolean renderable)
    {
        this.renderable=renderable;
    }

}
