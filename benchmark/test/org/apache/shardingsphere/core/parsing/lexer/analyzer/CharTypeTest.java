/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.parsing.lexer.analyzer;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class CharTypeTest {
    @Test
    public void assertIsWhitespace() {
        for (int i = 0; i < 256; i++) {
            if (Character.isWhitespace(((char) (i)))) {
                Assert.assertThat(CharType.isWhitespace(((char) (i))), CoreMatchers.is(Character.isWhitespace(((char) (i)))));
            }
        }
    }

    @Test
    public void assertIsEndOfInput() {
        Assert.assertTrue(CharType.isEndOfInput(((char) (26))));
    }

    @Test
    public void assertIsAlphabet() {
        for (int i = 0; i < 256; i++) {
            if (CharType.isAlphabet(((char) (i)))) {
                Assert.assertThat(CharType.isAlphabet(((char) (i))), CoreMatchers.is(Character.isAlphabetic(((char) (i)))));
            }
        }
    }

    @Test
    public void assertIsDigit() {
        for (int i = 0; i < 256; i++) {
            Assert.assertThat(CharType.isDigital(((char) (i))), CoreMatchers.is(Character.isDigit(((char) (i)))));
        }
    }

    @Test
    public void assertIsSymbol() {
        Assert.assertTrue(CharType.isSymbol('?'));
        Assert.assertTrue(CharType.isSymbol('#'));
        Assert.assertTrue(CharType.isSymbol('('));
    }
}

