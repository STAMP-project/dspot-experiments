/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.problem55;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class RewriteRowAndColumnsWithZerosTest {
    private RewriteRowAndColumnsWithZeros rewriteMatrix;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullMatrix() {
        rewriteMatrix.rewrite(null);
    }

    @Test
    public void shouldRewritePositionsEvenWithAOneDimensionMatrix() {
        int[][] matrix = new int[][]{ new int[]{ 1, 0 }, new int[]{  } };
        rewriteMatrix.rewrite(matrix);
        int[][] expectedMatrix = new int[][]{ new int[]{ 0, 0 }, new int[]{  } };
        Assert.assertArrayEquals(expectedMatrix, matrix);
    }

    @Test
    public void shouldNotRewritePositionsWithNonZeroElements() {
        int[][] matrix = new int[][]{ new int[]{ 1, 1 }, new int[]{ 1, 1 } };
        rewriteMatrix.rewrite(matrix);
        int[][] expectedMatrix = new int[][]{ new int[]{ 1, 1 }, new int[]{ 1, 1 } };
        Assert.assertArrayEquals(expectedMatrix, matrix);
    }

    @Test
    public void hsouldRewriteJustCenterRowAndCoulmnWithZeros() {
        int[][] matrix = new int[][]{ new int[]{ 1, 1, 1 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 1 } };
        rewriteMatrix.rewrite(matrix);
        int[][] expectedMatrix = new int[][]{ new int[]{ 1, 0, 1 }, new int[]{ 0, 0, 0 }, new int[]{ 1, 0, 1 } };
        Assert.assertArrayEquals(expectedMatrix, matrix);
    }

    @Test
    public void shouldRewriteRowAndColumnsWithZeros() {
        int[][] matrix = new int[][]{ new int[]{ 1, 1, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 1 } };
        rewriteMatrix.rewrite(matrix);
        int[][] expectedMatrix = new int[][]{ new int[]{ 0, 0, 0 }, new int[]{ 0, 0, 0 }, new int[]{ 1, 0, 0 } };
        Assert.assertArrayEquals(expectedMatrix, matrix);
    }
}

