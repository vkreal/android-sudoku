package org.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public abstract class GameOptions extends BaseView {
	
   protected static final String TAG = "Sudoku";
   protected static final int MENU_NEW_ID = Menu.FIRST;
   protected static final int MENU_SAVE_ID = MENU_NEW_ID + 1;
   protected static final int MENU_OPTIONS_ID = MENU_SAVE_ID + 1;
   protected static final int MENU_ABOUT_ID = MENU_OPTIONS_ID + 1;
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      
      menu.add(0, MENU_NEW_ID, 0, R.string.new_game_label)
      .setShortcut('0', 'n')
      .setIcon(android.R.drawable.ic_menu_add);
      
      menu.add(0, MENU_SAVE_ID, 0, R.string.save_game_label)
      .setShortcut('0', 's')
      .setIcon(android.R.drawable.ic_menu_save);
          /*
      menu.add(0, MENU_OPTIONS_ID, 0, R.string.options_label)
      .setShortcut('0', 'o')
      .setIcon(android.R.drawable.ic_menu_preferences);
  
      menu.add(0, MENU_ABOUT_ID, 0, R.string.about_label)
      .setShortcut('0', 'a')
      .setIcon(android.R.drawable.ic_menu_info_details);
		*/
      return true;
   }
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       // Handle all of the possible menu actions.
       switch (item.getItemId()) {
       case MENU_ABOUT_ID:
    	   Intent i = new Intent(this, About.class);
    	   startActivity(i);
    	   break;
       case MENU_NEW_ID:
    	   this.openNewGameDialog();
    	   break;
       case MENU_SAVE_ID:
    	   this.saveCurrentGame();
    	   Toast.makeText(this, R.string.game_saved_label, Toast.LENGTH_SHORT).show();
           break;
  //     case MENU_OPTIONS_ID:
  //  	   startActivity(new Intent(this, EditPreferences.class));  
  //  	   break;
       }
       return super.onOptionsItemSelected(item);
   }
   protected void saveCurrentGame() {
	// TODO Auto-generated method stub
	
   }
}
