package org.example.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public abstract class BaseView extends Activity {

   protected static final String TAG = "Sudoku";
   
   protected void startGame(int i) {
		Intent intent = new Intent(this, Game.class);
		intent.putExtra(Game.KEY_DIFFICULTY, i);
		startActivity(intent);
	}
   protected void openNewGameDialog() {
		new AlertDialog.Builder(this).setTitle(R.string.difficulty)
		.setItems(R.array.difficulty,
				new DialogInterface.OnClickListener() {

					public void onClick(
							DialogInterface dialoginterface, int i) {
						Log.d(TAG, "clicked on " + i);

						startGame(i);
					}
		}).show();
	}
}
