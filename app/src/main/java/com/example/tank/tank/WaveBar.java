package com.example.tank.tank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.tank.R;

public class WaveBar extends View {
    private Paint paint;
    private Drawable svgDrawable;
    private int canvasWidth;
    private int canvasHeight;
    private Rect rect;
    private int porsentage = 50;

    public WaveBar(Context context) {
        super(context);
        init(context);
    }

    public WaveBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaveBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        svgDrawable = context.getDrawable(R.drawable.arrow_right);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasWidth = canvas.getWidth() - 30;
        canvasHeight = canvas.getHeight() ;
        int rectWidth = 12;


        if(porsentage<=0) porsentage = 0;
        if(porsentage>=100) porsentage =100;

        float left = canvasWidth - rectWidth;

        float height = (porsentage*(this.getHeight()/1.39f)) /  100;


        float top = canvasHeight -  height;
        float right = canvasWidth;
        float bottom = canvasHeight;

        rect = new Rect((int) left, (int) top, (int) right, (int) bottom);
        canvas.drawRect(rect, paint);


        if (svgDrawable != null) {
            int svgWidth = 40;
            int svgHeight = 40;


            int svgLeft = rect.left - svgWidth ;
            int svgTop = rect.top - svgHeight/2;
            int svgRight = svgLeft + svgWidth;
            int svgBottom = svgTop + svgHeight;

            svgDrawable.setBounds(svgLeft, svgTop, svgRight, svgBottom);
            svgDrawable.draw(canvas);
        }
    }

    public int getPorsentage() {
        return porsentage;
    }

    public void setPorsentage(int porsentage) {
        this.porsentage = porsentage;
        invalidate();
    }
}
