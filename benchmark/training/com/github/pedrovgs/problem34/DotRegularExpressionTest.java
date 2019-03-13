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
package com.github.pedrovgs.problem34;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class DotRegularExpressionTest {
    private DotRegularExpression dotRegularExpression;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArrayOrPatternAsInput() {
        dotRegularExpression.evaluate(null, null);
    }

    @Test
    public void shouldReturnAnEmptyArrayIfTheArrayIsEmpty() {
        String pattern = "p.d";
        String[] words = new String[]{  };
        String[] result = dotRegularExpression.evaluate(words, pattern);
        Assert.assertEquals(0, result.length);
    }

    @Test
    public void shouldReturnAnEmptyArrayIfPatternDoesNotMatch() {
        String pattern = "p.d";
        String[] words = new String[]{ "ana", "test1", "test2" };
        String[] result = dotRegularExpression.evaluate(words, pattern);
        Assert.assertEquals(0, result.length);
    }

    @Test
    public void shouldReturnAnArrayWithMatches() {
        String pattern = "p.d";
        String[] words = new String[]{ "pod", "pid", "pat", "por", "pwd" };
        String[] result = dotRegularExpression.evaluate(words, pattern);
        String[] expectedResult = new String[]{ "pod", "pid", "pwd" };
        Assert.assertArrayEquals(expectedResult, result);
    }
}

