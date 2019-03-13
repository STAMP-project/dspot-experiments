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
package com.github.pedrovgs.problem33;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class SimpleRegularExpressionTest {
    private SimpleRegularExpression simpleRegularExpression;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInput() {
        simpleRegularExpression.evaluate(null, null);
    }

    @Test
    public void shouldReturnFalseIfInputDoesNotMatchesAndDoesNotContainKeyElements() {
        String input = "aaa";
        String regularExpression = "a";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnTrueIfInputMatchesAndDoesNotContainKeyElements() {
        String input = "aaa";
        String regularExpression = "aaa";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueIfMatchesUsingAsterisk() {
        String input = "aaa";
        String regularExpression = "a*";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseIfDoesNotMatchUsingAsterisk() {
        String input = "aaa";
        String regularExpression = "b*";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnTrueIfMatchesUsingDot() {
        String input = "aa";
        String regularExpression = "a.";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseIfDoesNotMatchUsingDot() {
        String input = "aaa";
        String regularExpression = "a.";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnTrueIfMatchesUsingAsteriskAndDot() {
        String input = "aaa";
        String regularExpression = ".a*";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseIfDoesNotMatchUsingAsteriskAndDot() {
        String input = "aaa";
        String regularExpression = ".b*";
        boolean result = simpleRegularExpression.evaluate(input, regularExpression);
        Assert.assertFalse(result);
    }
}

