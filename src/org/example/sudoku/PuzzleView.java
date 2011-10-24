package org.example.sudoku;

import org.example.sudoku.core.SudokuGrid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

class PuzzleView extends View {
   private static final String TAG = "Sudoku";
   private float width;
   private float height;
   private int selY = -1;
   private int selX = -1;
   private Rect selRect = new Rect();
   private Game game;
   private Boolean solveBoard = false;
   SudokuGrid sudGrid;
   public PuzzleView(Context context, SudokuGrid sudGrid) {
      super(context);
      this.sudGrid = sudGrid;
      this.game = (Game) context;
      setFocusable(true);
      setFocusableInTouchMode(true);
   }

   @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      width = w / 9f;
      height = h / 9f;
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
      for (int i = 0; i < 9; i++) {
         canvas.drawLine(0, i * height, getWidth(), i * height, paint3);
         canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, paint2);
         canvas.drawLine(i * width, 0, i * width, getHeight(), paint3);
         canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), paint2);
      }
      for (int i = 0; i < 9; i++) {
         if (i % 3 != 0)
            continue;
         canvas.drawLine(0, i * height, getWidth(), i * height, paint1);
         canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, paint2);
         canvas.drawLine(i * width, 0, i * width, getHeight(), paint1);
         canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), paint2);
      }
      Paint paint5 = new Paint(Paint.ANTI_ALIAS_FLAG);
      paint5.setColor(Color.BLACK);
      paint5.setStyle(Style.FILL);
      paint5.setTextSize(height * 0.75f);
      paint5.setTextScaleX(width / height);
      paint5.setTextAlign(Paint.Align.CENTER);

      FontMetrics fm = paint5.getFontMetrics();
      // how to draw the text center on our square
      // centering in X is easy... use alignment (and X at midpoint)
      float x = width / 2;
      // centering in Y, we need to measure ascent/descent first
      float y = height / 2 - (fm.ascent + fm.descent) / 2;
      
      SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(game);
      boolean showincorrect = sharedPref.getBoolean(game.getString( R.string.showincorrect ), false);
      
      
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
        	 
        	int location = j * 9 + i;
        	 
        	if(location == 0 || location == 1 || location == 2 
        			|| location == 9 || location == 10 || location == 11 
        			|| location == 18 || location == 19 || location == 20 ||
        			
        			location == 6 || location == 7 || location == 8
        			|| location == 15 || location == 16 || location == 17 || 
        			location == 24 || location == 25 || location == 26 ||		
        			
        			location == 30 || location == 31 || location == 32
        			|| location == 39 || location == 40 || location == 41 
        			|| location == 48 || location == 49 || location == 50 ||
        			
        			location == 54 || location == 55 || location == 56
        			|| location == 63 || location == 64 || location == 65 
        			|| location == 72 || location == 73 || location == 74 ||
        			
        			location == 60 || location == 61 || location == 62
        			|| location == 69 || location == 70 || location == 71 
        			|| location == 78 || location == 79 || location == 80)
        	{
        		paintAltStyle( canvas, i, j);
        	}
        	
        	int value = this.sudGrid.getVal(i,j);
        	// solve board
        	if( this.solveBoard )
        	{
        		if(sudGrid.isDefault(i, j) == false)
            	{
        			int solution = this.sudGrid.getGridVal(i,j);
        			if( value != 0 && solution != value )
        				paint5.setColor(Color.RED);
        			else
        				paint5.setColor(Color.BLUE);
        			
        			canvas.drawText(String.valueOf(solution), i * width + x, j
                            * height + y, paint5);
            	}
        		else
        		{
        			paint5.setColor(Color.BLACK);
            		if( value != 0 )
                		canvas.drawText(String.valueOf(value), i * width + x, j
                          * height + y, paint5);
        		}
        	}// playing board
        	else
        	{
        		boolean hasNotes = sudGrid.hasNotes(i, j);
        		boolean isDefault = sudGrid.isDefault(i, j);
        		if( isDefault == false )
            	{
        			if( showincorrect && value != this.sudGrid.getGridVal(i,j) && !hasNotes )
        				paint5.setColor(Color.RED);
        			else
        				paint5.setColor(Color.BLUE);
            	}
        		else
            		paint5.setColor(Color.BLACK);
        		
        		if( value != 0 && !hasNotes)
        		{ // do cell
        			paint5.setTextSize(height * 0.75f);
            		canvas.drawText(String.valueOf(value), i * width + x, j
                      * height + y, paint5);
        		}
        		else
        		{ // do notes
        			paint5.setTextSize(height * 0.30f);
        			String s = "";
        			
        			int[] available = sudGrid.getAvailabeNotes(i, j);
        			
        			for(int val : available)
        			{
        				float offsetX = width / 4;
          		      // centering in Y, we need to measure ascent/descent first
        				float offsetY = ( height / 7 - (fm.ascent + fm.descent) / 7);
        				
        				if(val==1)
        				{
        					offsetY = offsetY + 2;
        				}
        				if(val==2)
        				{
        					offsetY = offsetY + 2;
        					offsetX = offsetX + offsetX;
        				}
        				if(val==3)
        				{
        					offsetY = offsetY + 2;
        					offsetX = width - offsetX;
        				}
        				
        				if(val==4)
        				{
        					offsetY = offsetY + offsetY + 4;
        				}
        				if(val==5)
        				{
        					offsetX = offsetX + offsetX;
        					offsetY = offsetY + offsetY + 4;
        				}
        				if(val==6)
        				{
        					offsetY = offsetY + offsetY + 4;
        					offsetX = width - offsetX;
        				}
        				
        				if(val==7)
        				{
        					offsetY = height - offsetY + 3;
        				}
        				if(val==8)
        				{
        					offsetX = offsetX + offsetX;
        					offsetY = height - offsetY + 3;
        				}
        				if(val==9)
        				{
        					offsetX = width - offsetX;
        					offsetY = height - offsetY + 3;
        				}
        				canvas.drawText(String.valueOf(val), i * width + offsetX, j
      	                      * height + offsetY, paint5);
        			}
        			/*
        			for(int val : available)
        			{
        				s += String.valueOf(val);
        			}
            		canvas.drawText(String.valueOf(s), i * width + x, j
                      * height + y, paint5);*/
        		}
        	}
         }
      }
      Paint paint4 = new Paint();
      paint4.setColor(Color.argb(100, 255, 128, 0));
      paint4.setAlpha(30);
      canvas.drawRect(selRect, paint4);
      this.solveBoard = false;
   }
   public void clear()
   {
	   sudGrid.clearNonDefaultCells();
	   this.postInvalidate();
   }
   public void solve()
   {
	   this.solveBoard = true;
	   sudGrid.setSolved(true);
	   this.postInvalidate();
   }
   private void paintAltStyle(Canvas canvas, int i, int j)
   {
	   Rect rect = new Rect();
	   rect.set((int) (i * width + 3), (int) (j * height + 3), (int) (i
	            * width + width), (int) (j * height + height));
		
	   Paint p = new Paint();
	   p.setColor(Color.argb(240, 244, 252, 255));
	   p.setStyle(Style.FILL);
	   canvas.drawRect(rect, p);
   }
   
   @Override
   public boolean onTouchEvent(MotionEvent event) {
      if (event.getAction() != MotionEvent.ACTION_DOWN)
         return super.onTouchEvent(event);
      if(!this.isEnabled())
    	  return false;
      select((int) (event.getX() / width), (int) (event.getY() / height));
      
      if(!hasValidCell())
    	  return false;
      
   //   game.showKeypadOrError(selX, selY);
      Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
      return true;
   }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
      Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event=" + event);
      if(!hasValidCell())
    	  return false;
      switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_UP:
         select(selX, selY - 1);
         break;
      case KeyEvent.KEYCODE_DPAD_DOWN:
         select(selX, selY + 1);
         break;
      case KeyEvent.KEYCODE_DPAD_LEFT:
         select(selX - 1, selY);
         break;
      case KeyEvent.KEYCODE_DPAD_RIGHT:
         select(selX + 1, selY);
         break;
      case KeyEvent.KEYCODE_0:
      case KeyEvent.KEYCODE_SPACE:
         setSelectedTile(0);
         break;
      case KeyEvent.KEYCODE_1:
    	  this.game.doNumber(1);
         break;
      case KeyEvent.KEYCODE_2:
    	  this.game.doNumber(2);
         break;
      case KeyEvent.KEYCODE_3:
    	  this.game.doNumber(3);
         break;
      case KeyEvent.KEYCODE_4:
    	  this.game.doNumber(4);
         break;
      case KeyEvent.KEYCODE_5:
    	  this.game.doNumber(5);
         break;
      case KeyEvent.KEYCODE_6:
    	  this.game.doNumber(6);
         break;
      case KeyEvent.KEYCODE_7:
    	  this.game.doNumber(7);
         break;
      case KeyEvent.KEYCODE_8:
    	  this.game.doNumber(8);
         break;
      case KeyEvent.KEYCODE_9:
    	  this.game.doNumber(9);
         break;
  //    case KeyEvent.KEYCODE_NEWLINE:
  //    case KeyEvent.KEYCODE_DPAD_CENTER:
  //       game.showKeypadOrError(selX, selY);
      default:
         return super.onKeyDown(keyCode, event);
      }
      return true;
   }
   public void setNote(int note)
   {
	   if( !hasValidCell() )
	   {
		   Toast.makeText(this.game, R.string.message_click_on_cell, Toast.LENGTH_SHORT).show();
		   return;
	   }
	   sudGrid.setNote(selX, selY, note);
	   invalidate(selRect);	
   }
   public void clearNotes()
   {
	   if(!hasValidCell())
		   return;
	   sudGrid.deleteAllNotes(selX,selY);
	//   invalidate(selRect);	
   }
   public void clearCell()
   {
	   if( !hasValidCell() ){
		   Toast.makeText(this.game, R.string.message_click_on_cell_clear, Toast.LENGTH_SHORT).show();
	   }
	   else
	   {
		   sudGrid.resetCell(selX,selY,false);
		   invalidate(selRect);	
	   }
   }
   public boolean hasValidCell()
   {
	   return selX != -1 && selY != -1 && sudGrid.isDefault(selX, selY) != true && selRect != null;
   }
   public void setSelectedTile(int tile) {
	   /*
      if (game.setTileIfValid(selX, selY, tile)) {
         invalidate(selRect);
      } else {
         startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
         Log.d(TAG, "setSelectedTile: invalid: " + tile);
      }
      */
	  
	  if(!hasValidCell())
	  {
		  Toast.makeText(this.game, R.string.message_click_on_cell, Toast.LENGTH_SHORT).show();
	  }
	  else
	  {
		  sudGrid.setPuzzleVal(selX,selY,tile);
		  invalidate(selRect);	   
		  
		  if( sudGrid.isPuzzleSolved() )
		  {
			  game.gameSolved();
		  }
	  }
   }
   public void setHint()
   {
	   if(!hasValidCell())
	   {
		   Toast.makeText(this.game, R.string.message_click_on_cell_hint, Toast.LENGTH_SHORT).show();
		   return;
	   }
	   if( this.sudGrid.getVal(selX,selY) == sudGrid.getGridVal(selX, selY))
		   return;
	   sudGrid.deleteAllNotes(selX,selY);
	   setSelectedTile(sudGrid.getGridVal(selX, selY));
   }
   private void select(int x, int y) {
      invalidate(selRect);
      selX = Math.min(Math.max(x, 0), 8);
      selY = Math.min(Math.max(y, 0), 8);
      
      if(sudGrid.isDefault(selX, selY) == true)
    	  return;
      
      selRect.set((int) (selX * width + 1), (int) (selY * height + 1), (int) (selX
            * width + width), (int) (selY * height + height));
      invalidate(selRect);
   }

}