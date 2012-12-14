/* 
 *  Copyright 2012 Zachary Richey <zr.public@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package com.github.zachuorice.brainfuh;

/**
 * Represents an exception during the execution of a Brainf*** program. 
 */
public class InterpreterException extends RuntimeException 
{
    public enum InterpreterError
    {
        IP_OVERFLOW,
        IP_UNDERFLOW,
        DP_OVERFLOW,
        DP_UNDERFLOW,
        UNMATCHED_BRACKET,
        INPUT_NOT_AVAILABLE;
    }

    private InterpreterError errorType;
    public InterpreterError errorType() {return this.errorType;}
    
    private int line_no;
    public int line() {return this.line_no;}

    private int col_no;
    public int col() {return this.col_no;}

    InterpreterException(InterpreterError errorType, int line_no, int col_no)
    {
        this.errorType = errorType;
        this.line_no = line_no;
        this.col_no = col_no;
    }

    public String toString()
    {
        return String.format("%s:%s: %s", line_no, col_no, errorType);
    }
}
