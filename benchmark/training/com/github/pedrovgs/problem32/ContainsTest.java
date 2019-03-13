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
package com.github.pedrovgs.problem32;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class ContainsTest {
    private Contains contains;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInput() {
        contains.evaluate(null, null);
    }

    @Test
    public void shouldReturnTrueIfSecondStringContainsFirstString() {
        String word1 = "dro";
        String word2 = "Pedro";
        boolean result = contains.evaluate(word1, word2);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseIfSecondStringDoesNotContainFirstString() {
        String word1 = "ana";
        String word2 = "Pedro";
        boolean result = contains.evaluate(word1, word2);
        Assert.assertFalse(result);
    }

    @Test
    public void shouldBeCaseSensitive() {
        String word1 = "PE";
        String word2 = "Pedro";
        boolean result = contains.evaluate(word1, word2);
        Assert.assertFalse(result);
    }
}

