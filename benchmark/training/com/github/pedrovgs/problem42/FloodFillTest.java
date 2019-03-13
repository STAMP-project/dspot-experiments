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
package com.github.pedrovgs.problem42;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class FloodFillTest {
    private FloodFill floodFill;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullInstances() {
        floodFill.apply(null, 0, 1, 0, 0);
    }

    @Test
    public void shouldPaintSelectedZoneUsingIndicatedColor() {
        int[][] picture = new int[][]{ new int[]{ 1, 1, 1, 1 }, new int[]{ 1, 0, 0, 1 }, new int[]{ 1, 0, 0, 1 }, new int[]{ 1, 1, 1, 1 } };
        int colorToReplace = 0;
        int colorToPaint = 1;
        int x = 1;
        int y = 1;
        floodFill.apply(picture, colorToReplace, colorToPaint, x, y);
        int[][] expectedResult = new int[][]{ new int[]{ 1, 1, 1, 1 }, new int[]{ 1, 1, 1, 1 }, new int[]{ 1, 1, 1, 1 }, new int[]{ 1, 1, 1, 1 } };
        Assert.assertArrayEquals(expectedResult, picture);
    }

    @Test
    public void shouldNotModifyIfStartPaintingInOnePixelWithTheColorToPaint() {
        int[][] picture = new int[][]{ new int[]{ 1, 1, 1, 1 }, new int[]{ 1, 0, 0, 1 }, new int[]{ 1, 0, 0, 1 }, new int[]{ 1, 1, 1, 1 } };
        int colorToReplace = 0;
        int colorToPaint = 1;
        int x = 0;
        int y = 0;
        floodFill.apply(picture, colorToReplace, colorToPaint, x, y);
        int[][] expectedResult = new int[][]{ new int[]{ 1, 1, 1, 1 }, new int[]{ 1, 0, 0, 1 }, new int[]{ 1, 0, 0, 1 }, new int[]{ 1, 1, 1, 1 } };
        Assert.assertArrayEquals(expectedResult, picture);
    }
}

