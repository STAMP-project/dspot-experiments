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
package com.github.pedrovgs.problem27;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class ReverseSentenceTest {
    private ReverseSentence reverseSentence;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullStringsAsInput() {
        reverseSentence.reverse(null);
    }

    @Test
    public void shouldReturnEmptyIfInputIsEmpty() {
        Assert.assertEquals("", reverseSentence.reverse(""));
    }

    @Test
    public void shouldReverseOneSentenceWithJustOneWord() {
        String input = "pedro";
        String result = reverseSentence.reverse(input);
        Assert.assertEquals("pedro", result);
    }

    @Test
    public void shouldReverseSentenceWithMoreThanOneWord() {
        String input = "pedro vicente g?mez";
        String result = reverseSentence.reverse(input);
        Assert.assertEquals("g?mez vicente pedro", result);
    }
}

