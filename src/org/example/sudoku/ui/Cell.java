package org.example.sudoku.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class Cell extends View {
	private Paint mValuePaint;
    private int mValue = 5;

	private static final String TAG = "CELL";
	private float width;
	private float height;
	public Cell(Context context) {
		super(context);
	}
	public Cell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public Cell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public void setValue(int value) {
		mValue = value;
	    requestLayout();
	    invalidate();
	}
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 3f;
		height = h / 3f;
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
    }
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint paint = new Paint();
	    paint.setColor(Color.argb(255, 230, 240, 255));
	    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
	      
	    Paint paint1 = new Paint();
	    paint1.setColor(Color.argb(100, 86, 100, 143));
	    Paint paint2 = new Paint();
	    paint2.setColor(Color.argb(255, 255, 255, 255));
	    Paint paint3 = new Paint();
	    paint3.setColor(Color.argb(100, 198, 212, 239));
	    for (int i = 0; i < 3; i++) {
	         canvas.drawLine(0, i * height, getWidth(), i * height, paint3);
	         canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, paint2);
	         canvas.drawLine(i * width, 0, i * width, getHeight(), paint3);
	         canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), paint2);
	    }
	    
	    if( mValue != -1 )
	    {
	    	Paint paint5 = new Paint(Paint.ANTI_ALIAS_FLAG);
		    paint5.setColor(Color.BLACK);
		    paint5.setStyle(Style.FILL);
		    paint5.setTextSize(height * 2.75f);
		    paint5.setTextScaleX(width / height);
		    paint5.setTextAlign(Paint.Align.CENTER);

		    FontMetrics fm = paint5.getFontMetrics();
		    // how to draw the text center on our square
		    // centering in X is easy... use alignment (and X at midpoint)
		    float x = width / 2;
		    // centering in Y, we need to measure ascent/descent first
		    float y = height / 2 - (fm.ascent + fm.descent) / 2;
		    
	    	canvas.drawText(String.valueOf(mValue), 1 * width + x, 1
	    			* height + y, paint5);
	    }
	    
	   /*
	    paint5.setTextSize(height * .75f);
	    fm = paint5.getFontMetrics();
	    x = width / 2;
	    // centering in Y, we need to measure ascent/descent first
	    y = height / 2 - (fm.ascent + fm.descent) / 2;
	    canvas.drawText(String.valueOf(3), 0 * width + x, 1
                * height + y, paint5);
	    */
	    
	}
}
