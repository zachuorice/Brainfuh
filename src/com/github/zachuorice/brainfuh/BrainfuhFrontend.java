/* Brainf*** interpreter frontend program code */
package com.github.zachuorice.brainfuh;
import java.io.File;
import com.github.zachuorice.brainfuh.Brainfuh;

public class BrainfuhFrontend
{
    private enum ExitStatus
    {
        ARGS_EMPTY(1), BAD_FILENAME(2), NOT_A_FILE(3), NOT_READABLE(4);
        
        ExitStatus(int error) {this.error = error;}
        private int error;
        public int errorCode() {return this.error;}
    }
    
    private static void exitWithMessage(String message, ExitStatus err)
    {
        System.err.println(message);
        System.exit(err.errorCode());
    }
    
    public static void main(String[] args)
    {
        if(args.length <= 0)
            exitWithMessage("Args: code_filename", ExitStatus.ARGS_EMPTY);
        else
        {
            File code = new File(args[0]);
            try
            {
                Brainfuh.executeFile(code);
            }
            catch(java.io.IOException e)
            {
                if(!code.exists())
                    exitWithMessage("No file called: " + args[0],
                                    ExitStatus.BAD_FILENAME);
                else if(!code.isFile())
                    exitWithMessage("Not a file: " + args[0],
                                    ExitStatus.NOT_A_FILE);
                else if(!code.canRead())
                    exitWithMessage("Can't read file: " + args[0],
                                    ExitStatus.NOT_READABLE);
            }
        }
    }
}
