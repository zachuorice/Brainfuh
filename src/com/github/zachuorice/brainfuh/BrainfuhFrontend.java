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

/* Brainf*** interpreter frontend program code */
package com.github.zachuorice.brainfuh;
import java.io.File;

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
            exitWithMessage("Args: filename_0 ... [filename_n]", ExitStatus.ARGS_EMPTY);
        else
        {
            for(int i=0; i < args.length; i++)
            {
                File code = new File(args[i]);
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
                catch(InterpreterException e)
                {
                    System.out.println(e.toString());
                }
            }
        }
    }
}
