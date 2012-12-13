/* Useful static methods for using the library quickly. */
package com.github.zachuorice.brainfuh;
import com.github.zachuorice.brainfuh.*;
import java.io.File;
import java.io.IOException;

public class Brainfuh
{
    static void executeFile(File code) throws IOException
    {
        if(!code.canRead() || !code.isFile())
        {
            throw new IOException();
        }
    }
}
