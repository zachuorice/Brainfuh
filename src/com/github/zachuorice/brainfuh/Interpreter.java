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

/* A brainf*** interpreter in Java */
package com.github.zachuorice.brainfuh;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/* 
 * This object represents a single instruction 
 * in the Brainf*** instructon set.
 */
class Instruction
{
    public enum Type
    {
        IGNORED,
        INC_DP('>'), 
        DEC_DP('<'),
        INC_DATA('+'),
        DEC_DATA('-'),
        OUT_DATA('.'),
        GET_DATA(','),
        ZERO_JMP('['),
        NZ_JMP(']');

        Type() {this.symbol = -1;}
        Type(char symbol) {this.symbol = symbol;}

        private int symbol;
        public int symbol() {return this.symbol;}

        static Type match(char symbol)
        {
            for(Type sym : Type.values())
                if(sym.symbol() == symbol)
                    return sym;
            return IGNORED;
        }

        static boolean ignored(char symbol)
        {
            return Type.match(symbol) == Type.IGNORED;
        }
    }

    private int line_no;
    public int line() {return this.line_no;}

    private int col_no;
    public int col() {return this.col_no;}

    private Type type;
    public Type type() {return this.type;}

    Instruction(int line, int col, char instruction)
    {
        this.line_no = line;
        this.col_no = col;
        this.type = Type.match(instruction);
    }
}

/**
 * An interpreter for Brainf*** commands. 
 */
public final class Interpreter
{
    /**
     * The size of the data segment, and thus how much memory
     * a brainf*** program can address.
     */
    public static final int DATA_SIZE = 30000;

    // Instruction segment variables
    private LinkedList<Instruction> instructions;
    private ListIterator<Instruction> instruction_pointer;

    // Data segment variables
    private byte[] data;
    private int data_pointer;

    // Line and column number tracking variables, note
    // that CURRENT line and column number information
    // can be found in the instruction pointer not here.
    private int line_no;
    private int col_no;

    /**
     * Reset the data segment and instruction pointer.
     */
    void reset()
    {
        this.data = new byte[DATA_SIZE];
        this.data_pointer = 0;
        this.instruction_pointer = (ListIterator<Instruction>) 
                                    this.instructions.iterator();
    }

    /**
     * Discard all fed instructions.
     */
    void clear()
    {
        this.instructions = new LinkedList<>();
        this.line_no = 1;
        this.col_no = 1;
    }

    int line()
    {
        int line;
        try
        {
            line = instruction_pointer.next().line();
            instruction_pointer.previous();
        }
        catch(NoSuchElementException e)
        {
            line = -1;
        }
        return line;
    }

    int col()
    {
        int col;
        try
        {
            col = instruction_pointer.next().col();
            instruction_pointer.previous();
        }
        catch(NoSuchElementException e)
        {
            col = -1;
        }
        return col;
    }

    /** 
     * Feed a single instruction to the Interpreter, which will be stored
     * in the instruction segment and executed when execute() is called.
     */
    void feed(char data) 
    {
        // Increment line or column number information
        if(data == '\n' || data == '\r')
        {
            // This conditional handles CLRF newlines.
            if(this.col_no != 0)
                this.line_no += 1;
            this.col_no = 0;
        }
        else
            this.col_no += 1;

        if(!Instruction.Type.ignored(data))
            instructions.add(new Instruction(this.line_no, this.col_no, data));
    }

    void feed(String data)
    {
        for(int index=0; index < data.length(); index++)
            feed(data.charAt(index));
    }

    /**
     * reset() the interpreter and execute the fed instructions from beginning
     * to end.
     */
    void execute() throws DataOverflowException, DataUnderflowException
    {
        reset();
        while(instruction_pointer.hasNext())
            step();
    }

    void step() throws ProgramDoneException
    {
        throw new UnsupportedOperationException("Not implemented.");
    }

    Interpreter()
    {
        clear();
        reset();
    }
}
