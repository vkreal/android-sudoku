package org.example.sudoku.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ToggleButton extends Button {
     // ===========================================================
     // Fields
     // ===========================================================

     protected boolean isChecked = false;
     protected boolean isToggleButton = true;
     // ===========================================================
     // Constructors
     // ===========================================================
     public ToggleButton(Context context) {
          super(context);
     }
     
    public ToggleButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

     // ===========================================================
     // Getter & Setter
     // ===========================================================
     public boolean isChecked() {
          return this.isChecked;
     }
     public void setIsChecked(boolean isChecked) {
         this.isChecked = isChecked;
         this.postInvalidate();
     }
     public void setToggleButton(boolean isToggleButton) {
         this.isToggleButton = isToggleButton;
         this.postInvalidate();
     }
     public boolean isToggleButton() {
         return this.isToggleButton;
    }
     // ===========================================================
     // Methods
     // ===========================================================

     @Override
     public boolean performClick() {
          this.isChecked = !this.isChecked;
          return super.performClick();
     }

     /** Return an array of resource IDs of
      * the Drawable states representing the
      * current state of the view. */
     @Override
     public int[] onCreateDrawableState(int extraSpace) {
          if( !this.isToggleButton || !this.isEnabled() )
        	  return super.onCreateDrawableState(extraSpace);
    	  int[] states;
          if (this.isChecked()) {
               // Checked
               states = Button.ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;//Button.PRESSED_STATE_SET;
          } else {
               // Unchecked
               if (super.hasFocus()) {
                    /* Unchecked && Focus
                     * System highlights the Button */
                    states = super.onCreateDrawableState(extraSpace);
               } else {
                    // Unchecked && noFocus
                    states = Button.ENABLED_STATE_SET;//Button.LAST_STATE_SET;
               }
          }
          return states;
     }
}