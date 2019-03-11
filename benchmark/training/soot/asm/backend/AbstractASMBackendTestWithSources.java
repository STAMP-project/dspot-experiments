/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.asm.backend;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;


/**
 * Abstract base class for tests for the ASM backend that work with source code
 * files
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 */
public abstract class AbstractASMBackendTestWithSources implements Opcodes {
    private final StringWriter sw = new StringWriter();

    private final PrintWriter pw = new PrintWriter(sw);

    private final TraceClassVisitor visitor = new TraceClassVisitor(pw);

    private String comparisonOutput;

    /**
     * Compares the generated test output with Soot's output for the tested
     * class line by line
     *
     * @throws FileNotFoundException
     * 		if either the file for comparison could not be created or the
     * 		soot output could not be opened
     */
    @Test
    public void compareOutput() throws FileNotFoundException {
        /* Print output for comparison to file for debugging purposes. */
        PrintWriter ow = new PrintWriter(new File((("sootOutput/" + (getTargetClass())) + ".asm.compare")));
        ow.print(comparisonOutput);
        ow.flush();
        ow.close();
        Scanner sootOutput = new Scanner(new File((("sootOutput/" + (getTargetClass())) + ".asm")));
        Scanner compareOutput = new Scanner(comparisonOutput);
        int line = 1;
        while (compareOutput.hasNextLine()) {
            // Soot-output must have as much lines as the compared output.
            Assert.assertTrue("Too few lines in Soot-output!", sootOutput.hasNextLine());
            // Get both lines
            String compare = compareOutput.nextLine();
            String output = sootOutput.nextLine();
            // Compare lines
            Assert.assertTrue(((((("Expected line " + (compare.trim())) + " but got ") + (output.trim())) + " in line ") + line), compare.equals(output));
            ++line;
        } 
        Assert.assertFalse("Too many lines in Soot-output!", sootOutput.hasNextLine());
        sootOutput.close();
        compareOutput.close();
    }
}

