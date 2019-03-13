/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Markus Brenneis
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.neuralnetwork;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class MatrixTest {
    @Test
    public void matrixFromListTest() {
        String matrixString = "1 2\n3 4\n5 6";
        Matrix expectedMatrix = new Matrix(new float[][]{ new float[]{ 1, 2 }, new float[]{ 3, 4 }, new float[]{ 5, 6 } });
        Matrix matrix = new Matrix(Arrays.asList(matrixString.split("\n")));
        Assert.assertEquals(expectedMatrix, matrix);
    }

    // @Test
    // public void bigtest() throws IOException {
    // FileInputStream fileInputStream = new FileInputStream("/tmp/m");
    // long start = System.currentTimeMillis();
    // float m[][] = new float[52520][64];
    // Matrix matrix = new Matrix(fileInputStream);
    // long end = System.currentTimeMillis();
    // System.out.println((end - start)/1000.0);
    // }
    @Test
    public void matMulTest() {
        final Matrix a = new Matrix(new float[][]{ new float[]{ 1, 2 }, new float[]{ 3, 4 }, new float[]{ 5, 6 } });
        final Matrix b = new Matrix(new float[][]{ new float[]{ 1 }, new float[]{ 2 } });
        final Matrix c = new Matrix(new float[][]{ new float[]{ 5 }, new float[]{ 11 }, new float[]{ 17 } });
        Assert.assertEquals(c, a.mul(b));
    }

    @Test
    public void matAddTest() {
        Matrix a = new Matrix(new float[][]{ new float[]{ 1, 2 }, new float[]{ 3, 4 }, new float[]{ 5, 6 } });
        Matrix b = new Matrix(new float[][]{ new float[]{ 1, 2 }, new float[]{ 3, 4 }, new float[]{ 5, 7 } });
        Matrix c = new Matrix(new float[][]{ new float[]{ 2, 4 }, new float[]{ 6, 8 }, new float[]{ 10, 13 } });
        Assert.assertEquals(c, a.add(b));
    }

    @Test
    public void matReluTest() {
        Matrix a = new Matrix(new float[][]{ new float[]{ 1, 2 }, new float[]{ -3, 0 } });
        Matrix b = new Matrix(new float[][]{ new float[]{ 1, 2 }, new float[]{ 0, 0 } });
        Assert.assertEquals(b, a.relu());
    }
}

