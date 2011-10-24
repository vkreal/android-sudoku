package org.example.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Keypad extends Activity {

	public static final String EXTRA_USED = Keypad.class + ".used";

	protected static final String TAG = "Sudoku";

	private final View keypad[] = new View[9];

	//private int useds[];

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.keypad);
		findViews();
		int used = getIntent().getIntExtra(EXTRA_USED, 0) - 1;
		
		if(used > -1)
		{
			Button button = (Button)keypad[used];
			button.setText("X");
		}
		
	//	useds = Game.fromPuzzleString(extra);
		/*for (int element : useds) {
			if (element != 0)
				keypad[element - 1].setVisibility(View.INVISIBLE);
		}
		*/
		setListeners();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int tile = 0;
		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE:
			tile = 0;
			break;
		case KeyEvent.KEYCODE_1:
			tile = 1;
			break;
		case KeyEvent.KEYCODE_2:
			tile = 2;
			break;
		case KeyEvent.KEYCODE_3:
			tile = 3;
			break;
		case KeyEvent.KEYCODE_4:
			tile = 4;
			break;
		case KeyEvent.KEYCODE_5:
			tile = 5;
			break;
		case KeyEvent.KEYCODE_6:
			tile = 6;
			break;
		case KeyEvent.KEYCODE_7:
			tile = 7;
			break;
		case KeyEvent.KEYCODE_8:
			tile = 8;
			break;
		case KeyEvent.KEYCODE_9:
			tile = 9;
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		if (tile == 0 /*|| useds[tile - 1] == 0*/) {
			setResult( RESULT_OK, (new Intent()).setAction(String.valueOf(tile)) );
			// setResult(RESULT_OK, String.valueOf(tile));
			finish();
		}
		return true;

	}

	private void findViews() {
		keypad[0] = findViewById(R.id.keypad_1);
		keypad[1] = findViewById(R.id.keypad_2);
		keypad[2] = findViewById(R.id.keypad_3);
		keypad[3] = findViewById(R.id.keypad_4);
		keypad[4] = findViewById(R.id.keypad_5);
		keypad[5] = findViewById(R.id.keypad_6);
		keypad[6] = findViewById(R.id.keypad_7);
		keypad[7] = findViewById(R.id.keypad_8);
		keypad[8] = findViewById(R.id.keypad_9);
		
		for (int i = 0; i < keypad.length; i++) {
			final int t = i + 1;
			Button button = (Button)keypad[i];
			button.setText(Integer.toString(t));
		}
	}

	private void setListeners() {
		for (int i = 0; i < keypad.length; i++) {
			final int t = i + 1;
			keypad[i].setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					setResult( RESULT_OK, (new Intent()).setAction(String.valueOf(t)) );
					finish();
				}
			});
		}
	}

}
