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
import com.github.zachuorice.brainfuh.InterpreterException.InterpreterError;
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
            // TODO: Use a hashtable here
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
        line_no = line;
        col_no = col;
        type = Type.match(instruction);
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

    // Variables for data tracking during jumps
    private boolean doing_zero_jmp;
    private boolean doing_nz_jmp;
    private Instruction jmp_origin;

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
    public void reset()
    {
        data = new byte[DATA_SIZE];
        data_pointer = 0;
        instruction_pointer = (ListIterator<Instruction>) 
                               instructions.iterator();
        doing_zero_jmp = false;
        doing_nz_jmp = false;
        jmp_origin = null;
    }

    /**
     * Discard all fed instructions.
     */
    public void clear()
    {
        instructions = new LinkedList<>();
        line_no = 1;
        col_no = 1;
    }

    public int line()
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

    public int col()
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
    public void feed(char data) 
    {
        // Increment line or column number information
        if(data == '\n' || data == '\r')
        {
            // This conditional handles CLRF newlines.
            if(col_no != 0)
                line_no += 1;
            col_no = 0;
        }
        else
            col_no += 1;

        if(!Instruction.Type.ignored(data))
            instructions.add(new Instruction(line_no, col_no, data));
    }

    public void feed(String data)
    {
        for(int index=0; index < data.length(); index++)
            feed(data.charAt(index));
    }

    /**
     * reset() the interpreter and execute the fed instructions from beginning
     * to end.
     */
    public void execute() throws InterpreterException
    {
        reset();
        while(!programDone())
            step();
    }

    public boolean programDone()
    {
        return instruction_pointer.hasNext();
    }

    public void step() throws InterpreterException
    {
        if(!instruction_pointer.hasNext())
            throw new InterpreterException(InterpreterError.IP_OVERFLOW, 
                                           line_no, col_no);
        Instruction instruction;
        if(doing_nz_jmp)
        {
            if(!instruction_pointer.hasPrevious())
                throw new InterpreterException(InterpreterError.IP_UNDERFLOW,
                                               jmp_origin.line(), 
                                               jmp_origin.col());
            instruction = instruction_pointer.previous();
        }
        else
            instruction = instruction_pointer.next();

        switch(instruction.type())
        {
            case INC_DP:
                break;
            case DEC_DP:
                break;
            case INC_DATA:
                break;
            case DEC_DATA:
                break;
            case OUT_DATA:
                break;
            case GET_DATA:
                break;
            case ZERO_JMP:
                break;
            case NZ_JMP:
                break;
        }
    }

    public Interpreter()
    {
        clear();
        reset();
    }
}
