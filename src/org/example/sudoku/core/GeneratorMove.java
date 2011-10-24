/* Move created on 30.01.2006 */
package org.example.sudoku.core;

import java.util.Random;

public class GeneratorMove {
    
    private int y;
    private int x;
    private int valIndex;
    private int[] availabeMoves;
    private Random r;
    
    protected GeneratorMove(int x, int y, int[] availabeMoves, int valIndex) {
        this.y = y;
        this.x = x;
        this.availabeMoves = availabeMoves;
        this.valIndex = valIndex;
        r = new Random();
    }
    
    protected int getX() {
        return x;
    }
    
    protected int getY() {
        return y;
    }
    
    protected int[] getAvailabeMoves() {
        return availabeMoves;
    }
    
    protected int getValIndex() {
        return valIndex;
    }
    
    protected int getVal() {
        return availabeMoves[valIndex];
    }
    
    protected boolean setNextMove() {
        
        valIndex++;
        if(valIndex <= availabeMoves.length-1) {
            return true;
        }
        valIndex--;
        return false;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(28);
        sb.append("Move [x: ");
        sb.append(x);
        sb.append("; y: ");
        sb.append(y);
        sb.append("; val: ");
        sb.append(getVal());
        sb.append("]");
        return sb.toString();
    }
}
