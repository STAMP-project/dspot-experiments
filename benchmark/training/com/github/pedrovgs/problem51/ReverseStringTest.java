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
package com.github.pedrovgs.problem51;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class ReverseStringTest {
    private ReverseString reverseString;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsIterative() {
        reverseString.reverseIterative(null);
    }

    @Test
    public void shouldReturnAnEmptyStringIfTheInputIsEmptyIterative() {
        String result = reverseString.reverseIterative("");
        Assert.assertEquals("", result);
    }

    @Test
    public void shouldReturnTheSameStringIfTheInputStringContainsJustOneCharIterative() {
        String result = reverseString.reverseIterative("a");
        Assert.assertEquals("a", result);
    }

    @Test
    public void shouldReverseStringIterative() {
        String result = reverseString.reverseIterative("Pedro");
        Assert.assertEquals("ordeP", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsRecursive() {
        reverseString.reverseRecursive(null);
    }

    @Test
    public void shouldReturnAnEmptyStringIfTheInputIsEmptyRecursive() {
        String result = reverseString.reverseRecursive("");
        Assert.assertEquals("", result);
    }

    @Test
    public void shouldReturnTheSameStringIfTheInputStringContainsJustOneCharRecursive() {
        String result = reverseString.reverseRecursive("a");
        Assert.assertEquals("a", result);
    }

    @Test
    public void shouldReverseStringRecursive() {
        String result = reverseString.reverseRecursive("Pedro");
        Assert.assertEquals("ordeP", result);
    }
}

