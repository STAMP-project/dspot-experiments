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
package com.github.pedrovgs.problem50;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class UniqueCharsTest {
    private UniqueChars uniqueChars;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInput() {
        uniqueChars.evaluate(null);
    }

    @Test
    public void shouldReturnFalseIfInputStringIsEmpty() {
        Assert.assertTrue(uniqueChars.evaluate(""));
    }

    @Test
    public void shouldReturnTrueIfInputStringContainsDuplicatedChars() {
        Assert.assertFalse(uniqueChars.evaluate("vicente"));
    }

    @Test
    public void shouldReturnFalseIfInputStringDoesNotContainDuplicatedChars() {
        Assert.assertTrue(uniqueChars.evaluate("pedro"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInput2() {
        uniqueChars.evaluate2(null);
    }

    @Test
    public void shouldReturnTrueIfInputStringIsEmpty2() {
        Assert.assertTrue(uniqueChars.evaluate2(""));
    }

    @Test
    public void shouldReturnFalseIfInputStringContainsDuplicatedChars2() {
        Assert.assertFalse(uniqueChars.evaluate2("vicente"));
    }

    @Test
    public void shouldReturnTrueIfInputStringDoesNotContainDuplicatedChars2() {
        Assert.assertTrue(uniqueChars.evaluate2("pedro"));
    }
}

