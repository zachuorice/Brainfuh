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
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ArrayDeque;
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
    private ArrayList<Instruction> instructions;
    private int instruction_pointer;

    // The 'frames' stack is used for jumps.
    private ArrayDeque<Integer> jmp_frames;

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
        instruction_pointer = 0;
        jmp_frames = new ArrayDeque<>();
    }

    /**
     * Discard all fed instructions.
     */
    public void clear()
    {
        instructions = new ArrayList<>();
        line_no = 1;
        col_no = 1;
    }

    public int line()
    {
        int line;
        try
        {
            line = instructions.get(instruction_pointer).line();
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
            col = instructions.get(instruction_pointer).col();
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
        return instruction_pointer >= instructions.size();
    }

    public void step() throws InterpreterException
    {
        if(programDone())
            throw new InterpreterException(InterpreterError.IP_OVERFLOW, 
                                           line_no, col_no);
        Instruction instruction = instructions.get(instruction_pointer);

        switch(instruction.type())
        {
            case INC_DP:
                data_pointer += 1;
                if(data_pointer >= DATA_SIZE)
                    throw new InterpreterException(InterpreterError.DP_OVERFLOW,
                                                   instruction.line(),
                                                   instruction.col());
                break;
            case DEC_DP:
                data_pointer -= 1;
                if(data_pointer < 0)
                    throw new InterpreterException(InterpreterError.DP_UNDERFLOW,
                                                   instruction.line(),
                                                   instruction.col());
                break;
            case INC_DATA:
                data[data_pointer] += 1;
                break;
            case DEC_DATA:
                data[data_pointer] -= 1;
                break;
            case OUT_DATA:
                System.out.print((char ) data[data_pointer]);
                break;
            case GET_DATA:
                try
                {
                    int input = System.in.read();
                    if(input != -1)
                        data[data_pointer] = (byte ) input;
                }
                catch(IOException e)
                {
                    throw new InterpreterException(InterpreterError.
                                                    INPUT_NOT_AVAILABLE,
                                                   instruction.line(),
                                                   instruction.col());
                }
                break;
            case ZERO_JMP:
                break;
            case NZ_JMP:
                break;
            default:
                break;
        }
        instruction_pointer += 1;
    }

    public Interpreter()
    {
        clear();
        reset();
    }
}
