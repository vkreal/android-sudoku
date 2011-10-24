package org.example.sudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Sudoku extends ActivityWithOptions {
	private View continueButton;

	private View newButton;

	private View aboutButton;

	private View exitButton;
	private View optionsButton;
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.main);
		findViews();
		setListeners();
	}
	private void findViews() {
		continueButton = this.findViewById(R.id.continue_button);
		newButton = this.findViewById(R.id.new_button);
		aboutButton = this.findViewById(R.id.about_button);
		exitButton = this.findViewById(R.id.exit_button);
		optionsButton = this.findViewById(R.id.options_button);
	}

	private void setListeners() {
		continueButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startGame(Game.DIFFICULTY_CONTINUE);
			}
		});
		newButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				openNewGameDialog();
			}
		});
		aboutButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(Sudoku.this, About.class);
				startActivity(i);
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		optionsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(Sudoku.this, EditPreferences.class));  
			}
		});
	}

	private MediaPlayer mp;

	@Override
	protected void onResume() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean playmusic = sharedPref.getBoolean(getString( R.string.playmusic ), false);
		if( playmusic )
		{
			try {
				if (mp != null) {
					mp.stop();
					mp.release();
				}
				mp = MediaPlayer.create(this, R.raw.yids_level_stats_screen);
				mp.start();
			} catch (Throwable t) {
				Log.e(TAG, "MediaPlayer Failed", t);
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
		super.onPause();
	}
}
