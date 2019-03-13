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
package com.github.pedrovgs.problem43;


import java.util.HashSet;
import java.util.Set;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class CombinationOfIntegersTest {
    private CombinationOfIntegers combinationOfIntegers;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullInstancesAsInput() {
        combinationOfIntegers.calculate(null);
    }

    @Test
    public void shouldJustContainEmptySetIfInputSetIsEmpty() {
        Set<Integer> input = new HashSet<Integer>();
        Set<Set<Integer>> result = combinationOfIntegers.calculate(input);
        assertSetContainsSet(result);
    }

    @Test
    public void shouldCalculateEveryCombination() {
        Set<Integer> input = new HashSet<Integer>();
        input.add(1);
        input.add(2);
        Set<Set<Integer>> result = combinationOfIntegers.calculate(input);
        assertSetContainsSet(result);
        assertSetContainsSet(result, 1);
        assertSetContainsSet(result, 2);
        assertSetContainsSet(result, 1, 2);
    }
}

