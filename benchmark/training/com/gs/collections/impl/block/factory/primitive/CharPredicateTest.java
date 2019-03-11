/**
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.block.factory.primitive;


import CharLists.mutable;
import CharPredicate.IS_DIGIT;
import CharPredicate.IS_DIGIT_OR_DOT;
import CharPredicate.IS_LETTER;
import CharPredicate.IS_LETTER_OR_DIGIT;
import CharPredicate.IS_LOWERCASE;
import CharPredicate.IS_UNDEFINED;
import CharPredicate.IS_UPPERCASE;
import CharPredicate.IS_WHITESPACE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Junit test for {@link CharPredicate}.
 *
 * @deprecated in 6.0
 */
@Deprecated
public class CharPredicateTest {
    @Test
    public void isUpperCase() {
        CharPredicateTest.assertTrue(mutable.of('A', 'B', 'C'), IS_UPPERCASE);
        CharPredicateTest.assertFalse(mutable.of('a', 'b', 'c', '1', '.'), IS_UPPERCASE);
    }

    @Test
    public void isLowerCase() {
        CharPredicateTest.assertTrue(mutable.of('a', 'b', 'c'), IS_LOWERCASE);
        CharPredicateTest.assertFalse(mutable.of('A', 'B', 'C', '1', '.'), IS_LOWERCASE);
    }

    @Test
    public void isDigit() {
        CharPredicateTest.assertTrue(mutable.of('0', '1', '2', '3'), IS_DIGIT);
        CharPredicateTest.assertFalse(mutable.of('A', 'B', 'C', '.'), IS_DIGIT);
        CharPredicateTest.assertFalse(mutable.of('a', 'b', 'c', '.'), IS_DIGIT);
    }

    @Test
    public void isDigitOrDot() {
        CharPredicateTest.assertTrue(mutable.of('0', '1', '2', '3', '.'), IS_DIGIT_OR_DOT);
        CharPredicateTest.assertFalse(mutable.of('A', 'B', 'C'), IS_DIGIT_OR_DOT);
        CharPredicateTest.assertFalse(mutable.of('a', 'b', 'c'), IS_DIGIT_OR_DOT);
    }

    @Test
    public void isLetter() {
        CharPredicateTest.assertTrue(mutable.of('A', 'B', 'C'), IS_LETTER);
        CharPredicateTest.assertTrue(mutable.of('a', 'b', 'c'), IS_LETTER);
        CharPredicateTest.assertFalse(mutable.of('0', '1', '2', '3', '.'), IS_LETTER);
    }

    @Test
    public void isLetterOrDigit() {
        CharPredicateTest.assertTrue(mutable.of('A', 'B', 'C', '0', '1', '2', '3'), IS_LETTER_OR_DIGIT);
        CharPredicateTest.assertTrue(mutable.of('a', 'b', 'c', '0', '1', '2', '3'), IS_LETTER_OR_DIGIT);
        CharPredicateTest.assertFalse(mutable.of('.', '$', '*'), IS_LETTER_OR_DIGIT);
    }

    @Test
    public void isWhitespace() {
        CharPredicateTest.assertTrue(mutable.of(' '), IS_WHITESPACE);
        CharPredicateTest.assertFalse(mutable.of('A', 'B', 'C', '0', '1', '2', '3'), IS_WHITESPACE);
        CharPredicateTest.assertFalse(mutable.of('a', 'b', 'c', '0', '1', '2', '3'), IS_WHITESPACE);
        CharPredicateTest.assertFalse(mutable.of('.', '$', '*'), IS_WHITESPACE);
    }

    @Test
    public void isUndefined() {
        Assert.assertTrue(CharPredicates.isUndefined().accept(((char) (888))));
        CharPredicateTest.assertFalse(mutable.of('A', 'B', 'C', '0', '1', '2', '3'), IS_UNDEFINED);
        CharPredicateTest.assertFalse(mutable.of('a', 'b', 'c', '0', '1', '2', '3'), IS_UNDEFINED);
        CharPredicateTest.assertFalse(mutable.of('.', '$', '*'), IS_UNDEFINED);
    }
}

