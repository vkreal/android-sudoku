/* Grid created on 30.01.2006 */
package org.example.sudoku.core;

import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.json.*;

public class SudokuGrid {
    
    public static final int MASK_X =           0xF0000000;
    public static final int MASK_Y =           0x0F000000;
    public static final int MASK_PUZZLE_VAL =  0x000000F0;
    public static final int MASK_GRID_VAL =    0x0000000F;
    public static final int MASK_NOTES =       0x0001FF00;
    public static final int MASK_IS_DEFAULT =  0x00100000;
    public static final int MASK_IS_EDITABLE = 0x00200000;
    public static final int MASK_IS_HINT =     0x00400000;
    
    private static final int HEX1FF = 0x1ff;
    
    private int[][] grid;
    
    private ArrayList<SudokuObserver> observers;
    private boolean hasChanged;
    private boolean solved = false;
    private int[] vertical;
    private int[] horizontal;
    private int[] square;
    
    private Random r;
    
    public SudokuGrid () {
        grid = new int[9][9];
        int val = 0;
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                val = j << 28; //X
                val += i << 24; //Y
                val += MASK_IS_EDITABLE; //IS EDITABLE
                grid[j][i] = val;
            }
        }
        
        observers = new ArrayList<SudokuObserver>(4);
        hasChanged = false;
        
        vertical = new int[9];
        horizontal = new int[9];
        square = new int[9];
        
        this.r = new Random();
    }
    public void setSolved(boolean solved)
    {
    	this.solved = solved;
    }
    public boolean getSolved()
    {
    	return this.solved;
    }
    public boolean hasNotes(int x, int y)
    {
    	for (int i = 0; i < 9; i++) {
    		if( this.getNote(x, y, i+1) )
    			return true;
    	}
    	return false;
    }
    public int getRealGridVal(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y];
    }
    public int[][] getGrid()
    {
    	 return grid;
    }
    public static int getX(int cell) {
        return (cell & SudokuGrid.MASK_X) >>> 28;
    }
    
    public static int getY(int cell) {
        return (cell & SudokuGrid.MASK_Y) >>> 24;
    }
    
    public int getGridVal(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return grid[x][y] & MASK_GRID_VAL;
    }
    
    public static int getGridVal(int SudokuRealGridVal) {
        
        return (SudokuRealGridVal & MASK_GRID_VAL);
    }
    
    public int getPuzzleVal(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        return ((grid[x][y] & MASK_PUZZLE_VAL) >>> 4);
    }
    
    public static int getPuzzleVal(int SudokuRealGridVal) {
        
        return ((SudokuRealGridVal & MASK_PUZZLE_VAL) >>> 4);
    }
    
    /**
     * 
     * @return gridVal if isDefault = true else puzzleVal
     */
    public int getVal(int x, int y) {
        if(isDefault(x,y)) {
            return getGridVal(x,y);
        } else {
            return getPuzzleVal(x,y);
        }
    }
    
    public static int getVal(int realGridVal) {
        if(isDefault(realGridVal)) {
            return getGridVal(realGridVal);
        } else {
            return getPuzzleVal(realGridVal);
        }
    }
    
    public boolean getNote(int x, int y, int note) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        if(note < 1 || note > 9) {
            throw new IllegalArgumentException("Illegal note position. Note position must be form 1 to 9(inclusive).");
        }
        
        note = grid[x][y] & (1 << (note-1+8));
        if(note != 0) {
            return true;
        }
        return false;
    }
    
    public static boolean getNote(int realGridVal, int note) {
        note = realGridVal & (1 << (note-1+8));
        if(note != 0) {
            return true;
        }
        return false;
    }
    
    public boolean isDefault(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        if((grid[x][y] & MASK_IS_DEFAULT) == MASK_IS_DEFAULT) {
            return true;
        }
        return false;
    }
    
    public static boolean isDefault(int realGridVal) {
        if((realGridVal & MASK_IS_DEFAULT) == MASK_IS_DEFAULT) {
            return true;
        }
        return false;
    }
    
    public boolean isEditable(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        if((grid[x][y] & MASK_IS_EDITABLE) == MASK_IS_EDITABLE) {
            return true;
        }
        return false;
    }
    
    public static boolean isEditable(int realGridVal) {
        if((realGridVal & MASK_IS_EDITABLE) == MASK_IS_EDITABLE) {
            return true;
        }
        return false;
    }
    
    public boolean isHint(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        if((grid[x][y] & MASK_IS_HINT) == MASK_IS_HINT) {
            return true;
        }
        return false;
    }
    
    public static boolean isHint(int realGridVal) {
        
        if((realGridVal & MASK_IS_HINT) == MASK_IS_HINT) {
            return true;
        }
        return false;
    }
    
    //OBSERVERS SET CHANGE METHODS BEGIN:
    public void setRealGridVal(int realGridVal) {
        int x = SudokuGrid.getX(realGridVal);
        int y = SudokuGrid.getY(realGridVal);
        grid[x][y] = realGridVal;
        setChanged();
        notifyObservers(realGridVal);
    }
    
    public void setGridVal(int x, int y, int val) { 
        if(!isEditable(x,y)) return;
        if(val < 0 || val > 9 ) {
            throw new IllegalArgumentException("Cell value is illegal.");
        }
        
        grid[x][y] &= ~MASK_GRID_VAL;
        grid[x][y] |= val;
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setPuzzleVal(int x, int y, int val) { 
        if(!isEditable(x,y)) return;
        if(val < 0 || val > 9 ) {
            throw new IllegalArgumentException("Cell value is illegal.");
        }
        
        if(getPuzzleVal(x,y) == val) {
            grid[x][y] &= ~MASK_PUZZLE_VAL;
        } else {
            grid[x][y] &= ~MASK_PUZZLE_VAL;
            grid[x][y] |= (val << 4);
        }
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setDefault(int x, int y, boolean b) {
        if(!isEditable(x,y)) return;
        
        if(b) {
            grid[x][y] |= MASK_IS_DEFAULT;
        } else {
            grid[x][y] &= ~MASK_IS_DEFAULT;
        }
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setEditable(int x, int y, boolean b) {
        if(b) {
            grid[x][y] |= MASK_IS_EDITABLE;
        } else {
            grid[x][y] &= ~MASK_IS_EDITABLE;
        }
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void setHint(int x, int y, boolean b)  {
        if(!isEditable(x,y)) return;
        
        if(b) {
            grid[x][y] |= MASK_IS_HINT;
        } else {
            grid[x][y] &= ~MASK_IS_HINT;
        }
        setChanged();
        notifyObservers(grid[x][y]);
    }

    public void setNote(int x, int y, int note) {
        if(!isEditable(x,y)) return;
        if(note < 1 || note > 9) {
            throw new IllegalArgumentException("Illegal Note. Note must be form 1 to 9(inclusive).");
        }
        grid[x][y] ^= (1 << (note-1+8));
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void deleteNote(int x, int y, int note) {
        if(!isEditable(x,y)) return;
        if(note < 1 || note > 9) {
            throw new IllegalArgumentException("Illegal note position. Note position must be form 1 to 9(inclusive).");
        }
        grid[x][y] &= ~(1 << (note-1+8));
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void deleteAllNotes(int x, int y) {
        if(!isEditable(x,y)) return;
        
        grid[x][y] &= ~MASK_NOTES;
        
        setChanged();
        notifyObservers(grid[x][y]);
    }
    
    public void resetCell(int x, int y, boolean resetDefaultCellsToo) {
        
        if(resetDefaultCellsToo) {
            grid[x][y] &= MASK_X + MASK_Y;
        } else {
            grid[x][y] &= ~(MASK_NOTES + MASK_PUZZLE_VAL);
        }
        grid[x][y] |= MASK_IS_EDITABLE;
        setChanged();
        notifyObservers(grid[x][y]);
    }
    //END OBSERVERS SET CHANGE METHODS

    public void clearNonDefaultCells() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                resetCell(j,i,false);
            }
        }
    }
    
    public void resetGrid() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                resetCell(j,i,true);
            }
        }
    }
    
    public boolean isGridValid() {
        return checkGrid(false, false, false);
    }
    
    public boolean isGridSolved() {
        return checkGrid(true, false, false);
    }

    public boolean isPuzzleSolved() {
        
        return checkGrid(true, false, true);
    }
    
    public boolean isPuzzleValid() {
        return checkGrid(false, false, true);
    }
    
    private boolean checkGrid(boolean toBeSolvedToo, boolean usedForFindAvMoves, boolean checkPuzzle) {
        int m = 0 , n = 0;
        
        //reset Arrays
        for (int i = 0; i < 9; i++) {
            vertical[i] = horizontal[i] = square[i] = 0;
        }
        
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                int trueVal1 = 0;
                int trueVal2 = 0;
                if(checkPuzzle) {
                    trueVal1 = getVal(j,i);
                    trueVal2 = getVal(i,j);
                } else {
                    trueVal1 = getGridVal(j,i);
                    trueVal2 = getGridVal(i,j);
                }
                int k1 = 0, k2 = 0;
                if(trueVal1 != 0) {
                    k1 =  1 << (trueVal1 - 1);
                }
                if(trueVal2 != 0) {
                    k2 =  1 << (trueVal2 - 1);
                } 
                
                //Square adr in n
                m = j / 3;
                n = i / 3;
                n = 3*m + n;
                

                if(((vertical[i] & k2) > 0 || 
                   (horizontal[i] & k1) > 0 || 
                   (square[n] & k1) > 0) &&
                   !usedForFindAvMoves) {
                    return false;
                } else {
                    vertical[i] |= k2; square[n] |= k1; horizontal[i] |= k1; 
                }
            }
        }
        
        if(toBeSolvedToo) {
            for (int k = 0; k < 9; k++) {
                if(vertical[k] == HEX1FF && 
                   horizontal[k] == HEX1FF && 
                   square[k] == HEX1FF) {
                    continue;
                } else {
                    return false;
                }
            }
        } 
        return true; 
    }
    public int[] getAvailabeNotes(int x, int y) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        int n = 3*(x/3)+(y/3);

        int k = 0;
        boolean[] b = new boolean[9];
        
        for(int i = 0; i < 9; i++) {
            
            if(this.getNote(x, y, i+1)) {
                b[i] = true;
                k++;
            }
        }

        int[] result = new int[k];
        
        //SORT RESULT
        k = 0;
        for(int i = 0; i < 9; i++) {
            if(b[i]) {
                result[k] = i+1;
                k++;
            }
        }
        b = null;
        return result;
    }
    
    public int[] getAvailabeValuesField(int x, int y, boolean sortIt) {
        if((y < 0) || (y > 8) || (x < 0) || (x > 8)) {
            throw new IllegalArgumentException("Invalid cell address.");
        }
        
        checkGrid(false, true, false);
        
        int n = 3*(x/3)+(y/3);

        int k = 0;
        boolean[] b = new boolean[9];
        
        for(int i = 0; i < 9; i++) {
            
            if((vertical[x] & (1 << i)) == 0 && 
               (horizontal[y] & (1 << i)) == 0 && 
               (square[n] & (1 << i)) == 0) {
                b[i] = true;
                k++;
            }
        }

        int[] result = new int[k];
        
        //SORT RESULT
        k = 0;
        for(int i = 0; i < 9; i++) {
            if(b[i]) {
                result[k] = i+1;
                k++;
            }
        }
        b = null;
        if(!sortIt) {
            randomizeArray(result);
        }
        return result;
    }
    
    protected GeneratorMove getFirstMove() {
        return getNextMove(-1,0);
    }
    
    protected GeneratorMove getNextMove(int x, int y) {
        do { //No default Fields
            if(x + 1 > 8) { //y mod 9;
                if(y + 1 > 8) {
                    return null;
                }
                x = 0;
                y += 1;
            } else {
                x += 1;
            }
        } while(isDefault(x,y));

        int[] moves = getAvailabeValuesField(x,y, false);
        if(moves.length > 0) {
            return new GeneratorMove(x,y,moves,0);
        }
        return null;
    }
    public void setSerGrid(SerGrid sg) {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                grid[j][i] = sg.grid[j][i] ;
                setChanged();
                notifyObservers(grid[j][i]);
            }
        }
    }
    public SerGrid getSerGrid() {
        SerGrid sg = new SerGrid();
        int countDiff = 0;
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                sg.grid[j][i] = grid[j][i];
                if(isDefault(grid[j][i])) {
                    countDiff++;
                }
            }
        }
 //       sg.difficulty = countDiff;
        return sg; 
    }
    //OBSERVERS MANEGMANT
    public void addObserver(SudokuObserver so) {
        observers.add(so);
    }
    
    public boolean hasChanged() {
        return hasChanged;
    }
    
    public void setChanged() {
        hasChanged = true;

    }
    public void notifyObservers(int cell) {
        if(hasChanged) {
            for (SudokuObserver so : observers) {
                so.updateCellChange(cell);
            }
            hasChanged = false;
        }
    }
    /**/
    public JSONArray getJSONArrayGrid() throws JSONException {
    	JSONArray jsonArray = new JSONArray();
        int countDiff = 0;
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
            	int val =  grid[j][i];
            	JSONObject json = new JSONObject();
            	json.put("value", val);
            	json.put("i", i);
            	json.put("j", j);
            	jsonArray.put(json);
			    if(isDefault(val)) {
                    countDiff++;
                }
            }
        }
        //JSONObject jsonGame = new JSONObject();
        //jsonGame.put("DIFFICULTY", countDiff);
        return jsonArray; 
    }
    public void setJSONArrayGrid(JSONArray jsonArray) throws JSONException {
    	for(int index = 0; index < jsonArray.length(); index++)
    	{
    		JSONObject json = jsonArray.getJSONObject(index);
    		int i = json.getInt("i");
    		int j = json.getInt("j");
    		int val =  json.getInt("value");
    		grid[j][i] = val;
    		setChanged();
            notifyObservers(grid[j][i]);
    	}
    }
    
    private void randomizeArray(int[] a) {
        int tmp = 0; int rV = 0;
        for(int i = 0; i < a.length; i++) {
            rV = r.nextInt(a.length-i);
            tmp = a[a.length-i-1];
            a[a.length-i-1] = a[rV];
            a[rV] = tmp;
        }
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                sb.append("|"); sb.append(getGridVal(j,i));
            }
            sb.append("|\n");
        }
        return sb.toString();
    }   
}
