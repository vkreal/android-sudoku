package org.example.sudoku;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.example.sudoku.core.*;
import org.example.sudoku.core.SudokuGenerator.*;
import org.example.sudoku.ui.ToggleButton;
import org.json.*;

import android.R.color;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends GameOptions {

	private static final String PENCIL_ISChecked = "pencil_isChecked";
	private static final String PREF_PUZZLE = Game.class + ".puzzle";
	private static final String ORIGINAL_CONTENT = "origContent";
	private static final String GAME_PAUSE = "GAME_PAUSE";
	private static final String GAME_SECONDS = "GAME_SECONDS";
	private static final String GAME_SOLVED = "GAME_SOLVED";
	private static final String GAME_DIFFICULTY = "GAME_DIFFICULTY";
	
	private static final String CURRENT_GAME_FILE = "CURRENT_GAME_FILE";
	
	private static final int ACTIVITY_SELECT = 0;
	protected static final String KEY_DIFFICULTY = "difficulty";
	protected static final int DIFFICULTY_CONTINUE = -1;
	protected static final int DIFFICULTY_EASY = 0;
	protected static final int DIFFICULTY_MEDIUM = 1;
	protected static final int DIFFICULTY_HARD = 2;
	protected static final int DIFFICULTY_VERYHARD = 3;
	private NumDistributuon nD = NumDistributuon.evenlyFilled3x3Square3;
	protected SudokuGrid sudGrid;
	private ProgressDialog pd;
	private boolean isPause = false;
	protected int current_diff_mode = SudokuGenerator.DIFF_EASY;
	private final Handler timerHandler = new Handler();
	private boolean isTimerActive;
	private int secondsOffset = 0;
	static int timerSeconds = 0;
	// from http://www.sudocue.net/lite.php
	private PuzzleView puzzleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		Log.d(TAG, "onCreate");	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.game);
		current_diff_mode = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
		
		// resume pause game
		if( savedInstanceState != null )
		{
			current_diff_mode = DIFFICULTY_CONTINUE;
			if ( savedInstanceState.getString(GAME_PAUSE) != null )
				isPause = Boolean.parseBoolean(savedInstanceState.getString(GAME_PAUSE));
		}
	//	Log.d(TAG, "current_diff_mode: " + String.valueOf(current_diff_mode));	
		sudGrid = new SudokuGrid();
		puzzleView = new PuzzleView(this, sudGrid);
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
		
		frame.addView(puzzleView, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels; 
		
		int newWidth = screenWidth/9;
		LinearLayout buttonContainer = (LinearLayout)findViewById(R.id.buttons);
		for(int index = 0; index < 9; index++)
		{
			ToggleButton button = new ToggleButton(this);
			button.setText(String.valueOf(index + 1));
			buttonContainer.addView(button, newWidth, 45);
			button.setToggleButton( false );
			button.setId(index + 1);
			button.setOnClickListener(new OnClickListener()  
			{
				public void onClick(View v) {
					ToggleButton button = (ToggleButton)v;
					doNumber(button.getId());
				}
			});
			
		}
		setupControlStates(savedInstanceState);
		pd = ProgressDialog.show(Game.this,     
                "Please wait...", "Generating Sudoku", true);
		Handler handler = new Handler(); 
		handler.post(start);
	}
	public void doNumber(int number)
	{
		if( isPencilMode() )
		{
			// do pencil here
			puzzleView.setNote(number);
		}
		else
		{
			puzzleView.clearNotes();
			puzzleView.setSelectedTile(number);
		}
	}
	private void setupControlStates ( Bundle savedInstanceState )
	{
		if( savedInstanceState != null )
		{
			((ToggleButton)findViewById(R.id.pencil)).setIsChecked( Boolean.parseBoolean(savedInstanceState.getString(PENCIL_ISChecked)) );
		}
	}
	public boolean isPencilMode()
	{
		return ((ToggleButton)findViewById(R.id.pencil)).isChecked();
	}
	Thread start = new Thread()
    {
           public void run()
           {
        	   	
        	   	//when done
        	   	setGameStates();
       			puzzleView.requestFocus();
       			puzzleView.setEnabled(true);
       			pd.dismiss(); 
       			createPuzzle( current_diff_mode );
           }

    }; 
	private void setGameStates()
	{
		Button btnPause = (Button)findViewById(R.id.pause);
		if( !isPause )
		{
			btnPause.setText(R.string.pause);
			startTimer();
		}
		btnPause.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				Intent intent = new Intent(Game.this, Sudoku.class);
				startActivity(intent);
				stopTimer();
				
			}
		});
		findViewById(R.id.solve).setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				gameSolved();
			}
		});	
		findViewById(R.id.clear).setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				puzzleView.clearCell();
				//startTimer();
			}
		});	
		findViewById(R.id.pencil).setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				
			}
		});	
		findViewById(R.id.hint).setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				puzzleView.setHint();
			}
		});	
	}
	public void gameSolved()
	{
		puzzleView.solve();
		stopTimer();
		togglePlayButtons(false);
		Button button = (Button)findViewById(R.id.solve);
		if( sudGrid.getSolved() )
		{
			if(button.getText() == getString(R.string.new_label))
				openNewGameDialog();
			else
				Toast.makeText(Game.this, R.string.game_solved, Toast.LENGTH_SHORT).show();
			button.setText(R.string.new_label);
		}
		else
			button.setText(R.string.solve);	
	}
	private void togglePlayButtons(boolean enabled)
	{
		for(int index = 0; index < 9; index++)
		{
			findViewById(index+1).setEnabled(enabled);
		}
		((ToggleButton)findViewById(R.id.pencil)).setEnabled(enabled);
		findViewById(R.id.clear).setEnabled(enabled);
		findViewById(R.id.pause).setEnabled(enabled);
		findViewById(R.id.hint).setEnabled(enabled);
		puzzleView.setEnabled(enabled);
	}
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
		Log.d(TAG, "onSaveInstanceState");
		outState.putString(GAME_PAUSE, String.valueOf(isPause));
		outState.putString(PENCIL_ISChecked, String.valueOf(((ToggleButton)findViewById(R.id.pencil)).isChecked()));
    }
	
	private void createPuzzle(int diff) {
		secondsOffset = 0;
		switch (diff) {
		case DIFFICULTY_CONTINUE:
			loadCurrentGame();
			break;
		case DIFFICULTY_VERYHARD:
			createGame(SudokuGenerator.DIFF_VERYHARD);		
			break;
		case DIFFICULTY_HARD:
			createGame(SudokuGenerator.DIFF_HARD);	
			break;
		case DIFFICULTY_MEDIUM:
			createGame(SudokuGenerator.DIFF_NORMAL);	
			break;
		case DIFFICULTY_EASY:
		default:
			createGame(SudokuGenerator.DIFF_EASY);	
			break;
		}
	}
	private void loadCurrentGame()
	{
		try 
		{   
			FileInputStream fIn = openFileInput(CURRENT_GAME_FILE);
			ObjectInputStream is = new ObjectInputStream(fIn);
			SerGrid res = (SerGrid)is.readObject();
	        is.close();
	        is = null; fIn = null;
	        sudGrid.setSerGrid(res);
	        current_diff_mode = res.difficulty;
	        secondsOffset = res.timerSeconds;
	        sudGrid.setSolved(res.solved);
	        togglePlayButtons(!res.solved);
	        if( res.solved )
	        {
	        	stopTimer();
	        	puzzleView.solve();
	        	((Button)findViewById(R.id.solve)).setText(R.string.new_label);
	        }
	        /*
	        SharedPreferences pref = getPreferences(MODE_PRIVATE);
			String puz = pref.getString(PREF_PUZZLE, "");
			if( puz != "")
			{
				JSONObject settings = new JSONObject(puz);
				secondsOffset = settings.getInt("timerSeconds");
			}
			*/
		} 
		catch (Exception ioe) 
		{
	        ioe.printStackTrace();
	        createGame(SudokuGenerator.DIFF_EASY);	
		}
	}
	@Override
	protected void saveCurrentGame()
	{
		try 
		{ // catches IOException below
         // ##### Write a file to the disk #####
	         /* We have to use the openFileOutput()-method
	          * the ActivityContext provides, to
	          * protect your file from others and
	          * This is done for security-reasons.
	          * We chose MODE_WORLD_READABLE, because
	          *  we have nothing to hide in our file */        
	         FileOutputStream fOut = openFileOutput(CURRENT_GAME_FILE,
	                                  MODE_WORLD_READABLE);
	         ObjectOutputStream osw = new ObjectOutputStream(fOut); 
	         SerGrid sg = sudGrid.getSerGrid();
	         sg.timerSeconds = timerSeconds;
	         sg.difficulty = current_diff_mode;
	         sg.solved = sudGrid.getSolved();
	         // Write the string to the file
	         osw.writeObject(sg);
	         /* ensure that everything is
	          * really written out and close */
	         osw.flush();
	         osw.close();
	         Log.d(TAG, "Saving current game successful!");
		} 
		catch (IOException ioe) {
	        ioe.printStackTrace();
		}
	}
	private void createGame(int diff)
	{
		new SudokuGenerator(sudGrid).generatePuzzle(diff, nD);	
	}
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		this.saveCurrentGame();
		/*
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		String puz = toPreferencesString();
		if(puz != null)
			pref.edit().putString(PREF_PUZZLE, puz).commit();
		*/
		super.onPause();
	}
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}
	protected String toPreferencesString() {
		try {
			JSONObject json = new JSONObject();
			json.put(GAME_SECONDS, timerSeconds);
			json.put(GAME_PAUSE, String.valueOf(isPause));
			return json.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void startTimer() {
	    isTimerActive = true;
	    new Thread(new Runnable() {
	      public void run() {
	        final long startTime = SystemClock.uptimeMillis();
	        while (isTimerActive) {
	        	timerHandler.post(new Runnable() {
	            public void run() {
            	 timerSeconds = ((int)(SystemClock.uptimeMillis() - startTime) / 1000) + secondsOffset;
            	 setGameTitle(timerSeconds);
	            }
	          });
	          SystemClock.sleep(100);
	        }
	      }
	    }).start();
	  }
	  public void stopTimer() {
		secondsOffset = timerSeconds;
	    isTimerActive = false;
	  }
	  private void setGameTitle(int currentSeconds)
	  {
		  int minutes = currentSeconds / 60;
          int seconds = currentSeconds % 60;
          setLevelSeconds(minutes, seconds);
	  }
	  public void setLevelSeconds(int minutes, int seconds) {
		  String level = getString(R.string.easy_label);
		  switch( current_diff_mode )
		  {
		  		case DIFFICULTY_EASY:
		  			level = getString(R.string.easy_label);
		  			break;
		  		case DIFFICULTY_MEDIUM:
		  			level = getString(R.string.medium_label);
		  			break;
		  		case DIFFICULTY_HARD:
		  			level = getString(R.string.hard_label);
		  			break;
		  		case DIFFICULTY_VERYHARD:
		  			level = getString(R.string.very_hard_label);
		  			break;
		  }
		  if (seconds < 10) {
			  this.setTitle(getString(R.string.app_name) + " - " + getString(R.string.time) + " " + minutes + ":0" + seconds + "   " + getString(R.string.level) + " " + level);
          } else {
        	  this.setTitle(getString(R.string.app_name) + " - " + getString(R.string.time) + " " + minutes + ":" + seconds + "   " + getString(R.string.level) + " " + level);            
          }
	  }
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){ 
	        super.onActivityResult(resultCode, resultCode, intent); 
	        
	      //  Bundle extras = intent.getExtras();
	        if (requestCode == ACTIVITY_SELECT && resultCode == RESULT_OK) {
				puzzleView.setSelectedTile(Integer.parseInt(intent.getAction()));
			}
	}
	protected void showKeypadOrError(int x, int y) {
		if(sudGrid.isDefault(x, y) == false)
		{
		
			int value = this.sudGrid.getVal(x,y);
			Log.d(TAG, "showKeypad: used=" + Integer.toString(value));
			
			
			
			/*
			int used[] = getUsedTiles(x, y);
			if (allUsed(used)) {
				Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT)
						.show();
			} else {
			
				Log.d(TAG, "showKeypad: used=" + Arrays.toString(used));*/
				Intent i = new Intent(this, Keypad.class);
				i.putExtra(Keypad.EXTRA_USED, value);
				startActivityForResult(i, ACTIVITY_SELECT);
			//}
		}
	}
	private MediaPlayer mp;
	@Override
	protected void onStart() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean playmusic = sharedPref.getBoolean(getString( R.string.playmusic ), false);
		if( playmusic )
		{
			try {
				if (mp != null) {
					mp.stop();
					mp.release();
				}
				mp = MediaPlayer.create(this, R.raw.yids_underground_level_2);
				mp.start();
			} catch (Throwable t) {
				Log.e(TAG, "MediaPlayer Failed", t);
			}
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (mp != null) {	
			mp.stop();
			mp.release();
			mp = null;
			
		}
		super.onStop();
	}
}
