/* Useful static methods for using the library quickly. */
package com.github.zachuorice.brainfuh;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.github.zachuorice.brainfuh.*;

public class Brainfuh
{
    static void executeFile(File code) throws IOException
    {
        if(!code.canRead() || !code.isFile())
            throw new IOException();
        FileReader code_reader = new FileReader(code);
        int data = code_reader.read();
        Interpreter interpreter = new Interpreter();
        while(data != -1)
            interpreter.feed(data);
        interpreter.execute();
    }

    static void executeString(String code)
    {
        Interpreter interpreter = new Interpreter();
        for(int index=0; index < code.length(); index++)
            interpreter.feed(code.charAt(index));
        interpreter.execute();
    }
}
