/* A brainf*** interpreter in Java */
package com.github.zachuorice.brainfuh;

/* 
 * This object represents a single instruction 
 * in the Brainf*** instructon set.
 */
class Instruction
{
    public enum SymbolType
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

        SymbolType() {this.symbol = -1;}
        SymbolType(char symbol) {this.symbol = symbol;}

        private int symbol;
        public int symbol() {return this.symbol;}

        static SymbolType match(char symbol)
        {
            for(SymbolType sym : SymbolType.values())
                if(sym.symbol() == symbol)
                    return sym;
            return IGNORED;
        }
    }

    private int line_no;
    public int line() {return this.line_no;}

    private int col_no;
    public int col() {return this.col_no;}

    private SymbolType type;
    public SymbolType type() {return this.type;}

    Instruction(int line, int col, char instruction)
    {
        this.line_no = line;
        this.col_no = col;
        this.type = SymbolType.match(instruction);
    }
}

public class Interpreter
{

    void feed(char data) 
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void feed(String data)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void execute()
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
