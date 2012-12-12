/* Brainf*** interpreter frontend program code */
package com.github.zachuorice.brainfuh;
import java.io.File;
import java.lang.System;
import com.github.zachuorice.brainfuh.Brainfuh;

public class BrainfuhFrontend
{
    enum ExitStatus
    {
        NO_ARGS(1), BAD_FILENAME(2);
        
        ExitStatus(int error) {this.error = error;}
        private int error;
        public int errorCode() {return this.error;}
    }
    
    public static void main(String[] args)
    {
        if(args.length <= 0)
        {
            System.err.println("Args: code_filename");
            System.exit(ExitStatus.NO_ARGS.errorCode());
        }
        else
        {
            File code = new File(args[0]);
            if(!code.exists())
            {
                System.err.println("No file: " + args[0]);
                System.exit(ExitStatus.BAD_FILENAME.errorCode());
            }
            Brainfuh.executeFile(code);
        }
    }
}
