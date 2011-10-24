package org.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public abstract class ActivityWithOptions extends BaseView {

   protected static final String TAG = "Sudoku";
   protected static final int MENU_OPTIONS_ID = Menu.FIRST;

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      /*
      menu.add(0, MENU_OPTIONS_ID, 0, R.string.options_label)
      .setShortcut('0', 'o')
      .setIcon(android.R.drawable.ic_menu_preferences);
      */
      return true;
   }
   private void openOptionsDialog() {
	   /*
      View view = getViewInflate().inflate(R.layout.options, null, null);
      new AlertDialog.Builder(this).setTitle(R.string.options_title)
         .setView(view)
         .setPositiveButton(R.string.ok_label,
               new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialoginterface, int i) {
                  }
               })
         .setNegativeButton(R.string.cancel_label,
               new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialoginterface, int i) {
                  }
               })
         .show();
         */
   }

}
