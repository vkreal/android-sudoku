/* SerGrid created on 12.07.2006 */
package org.example.sudoku.core;

import java.io.Serializable;

public class SerGrid implements Serializable {
    
    public int[][] grid;
    public int difficulty;
    public int timerSeconds;
    public boolean solved;
    public SerGrid() {
    	solved = false;
    	timerSeconds = 0;
        grid = new int[9][9];
    }

}
