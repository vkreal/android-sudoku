package org.example.sudoku.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class Board extends TableLayout{

	public Board(Context context) {
		super(context);		
		
		 TableRow row = new TableRow(this.getContext());
	      
	      Cell cell = new Cell(this.getContext());
			
	      row.addView(cell);
			
			cell = new Cell(this.getContext());
			row.addView(cell);
	}
	 protected void onDraw(Canvas canvas) {
	      super.onDraw(canvas);
	 
	     
	 }
}
