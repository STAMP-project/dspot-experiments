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
package com.github.pedrovgs.problem54;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class RotateMatrixTest {
    private RotateMatrix rotateMatrix;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullMatrix() {
        rotateMatrix.rotate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptMatrixWithDifferentHeightAndWidth() {
        int[][] matrix = new int[6][3];
        rotateMatrix.rotate(matrix);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptAnEmtpyMatrix() {
        int[][] matrix = new int[0][0];
        rotateMatrix.rotate(matrix);
    }

    @Test
    public void shouldRotateMatrix() {
        int[][] matrix = new int[][]{ new int[]{ 0, 1, 2 }, new int[]{ 3, 4, 5 }, new int[]{ 6, 7, 8 } };
        rotateMatrix.rotate(matrix);
        int[][] expectedResult = new int[][]{ new int[]{ 6, 3, 0 }, new int[]{ 7, 4, 1 }, new int[]{ 8, 5, 2 } };
        Assert.assertArrayEquals(expectedResult, matrix);
    }
}

