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
package com.github.pedrovgs.problem40;


import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class MoveElementsToPositionsTest {
    private MoveElementsToPositions moveElementsToPosition;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArraysAsInput() {
        moveElementsToPosition.move(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptAnyNumberGreaterThanArrayLengthOrMinorThanOne() {
        int[] input = new int[]{ 1, 4 };
        moveElementsToPosition.move(input);
    }

    @Test
    public void shouldMoveElementsToTheCorrectPosition() {
        int[] input = new int[]{ 2, 3, 1, 0 };
        moveElementsToPosition.move(input);
        assertNumbersAreInTheCorrectPosition(input);
    }
}

