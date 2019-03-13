/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
/**
 * -*
 * Copyright ? 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class DictionaryEntryLineParserTest {
    private DictionaryEntryLineParser parser = new DictionaryEntryLineParser();

    @Test
    public void testTrivial() {
        Assert.assertArrayEquals(new String[]{ "??????", "?? ?? ??", "??? ???? ????", "??????" }, parser.parseLine("??????,?? ?? ??,??? ???? ????,??????"));
    }

    @Test
    public void testQuotes() {
        Assert.assertArrayEquals(new String[]{ "Java Platform, Standard Edition", "Java Platform, Standard Edition", "Java Platform, Standard Edition", "??????" }, parser.parseLine("\"Java Platform, Standard Edition\",\"Java Platform, Standard Edition\",\"Java Platform, Standard Edition\",\u30ab\u30b9\u30bf\u30e0\u540d\u8a5e"));
    }

    @Test
    public void testQuotedQuotes() {
        Assert.assertArrayEquals(new String[]{ "Java \"Platform\"", "Java \"Platform\"", "Java \"Platform\"", "??????" }, parser.parseLine("\"Java \"\"Platform\"\"\",\"Java \"\"Platform\"\"\",\"Java \"\"Platform\"\"\",\u30ab\u30b9\u30bf\u30e0\u540d\u8a5e"));
    }

    @Test
    public void testEmptyQuotedQuotes() {
        Assert.assertArrayEquals(new String[]{ "\"", "\"", "quote", "punctuation" }, parser.parseLine("\"\"\"\",\"\"\"\",quote,punctuation"));
    }

    @Test
    public void testCSharp() {
        Assert.assertArrayEquals(new String[]{ "C#", "C #", "??????", "?????????" }, parser.parseLine("\"C#\",\"C #\",\u30b7\u30fc\u30b7\u30e3\u30fc\u30d7,\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e"));
    }

    @Test
    public void testTab() {
        Assert.assertArrayEquals(new String[]{ "A\tB", "A B", "A B", "tab" }, parser.parseLine("A\tB,A B,A B,tab"));
    }

    @Test
    public void testFrancoisWhiteBuffaloBota() {
        Assert.assertArrayEquals(new String[]{ "\u30d5\u30e9\u30f3\u30bd\u30ef\"\u30b6\u30db\u30ef\u30a4\u30c8\u30d0\u30c3\u30d5\u30a1\u30ed\u30fc\"\u30dc\u30bf", "\u30d5\u30e9\u30f3\u30bd\u30ef\"\u30b6\u30db\u30ef\u30a4\u30c8\u30d0\u30c3\u30d5\u30a1\u30ed\u30fc\"\u30dc\u30bf", "\u30d5\u30e9\u30f3\u30bd\u30ef\"\u30b6\u30db\u30ef\u30a4\u30c8\u30d0\u30c3\u30d5\u30a1\u30ed\u30fc\"\u30dc\u30bf", "??" }, parser.parseLine("\"\u30d5\u30e9\u30f3\u30bd\u30ef\"\"\u30b6\u30db\u30ef\u30a4\u30c8\u30d0\u30c3\u30d5\u30a1\u30ed\u30fc\"\"\u30dc\u30bf\",\"\u30d5\u30e9\u30f3\u30bd\u30ef\"\"\u30b6\u30db\u30ef\u30a4\u30c8\u30d0\u30c3\u30d5\u30a1\u30ed\u30fc\"\"\u30dc\u30bf\",\"\u30d5\u30e9\u30f3\u30bd\u30ef\"\"\u30b6\u30db\u30ef\u30a4\u30c8\u30d0\u30c3\u30d5\u30a1\u30ed\u30fc\"\"\u30dc\u30bf\",\u540d\u8a5e"));
    }

    @Test(expected = RuntimeException.class)
    public void testSingleQuote() {
        parser.parseLine("this is an entry with \"unmatched quote");
    }

    @Test(expected = RuntimeException.class)
    public void testUnmatchedQuote() {
        parser.parseLine("this is an entry with \"\"\"unmatched quote");
    }

    @Test
    public void testEscapeRoundTrip() {
        String original = "3,\"14";
        Assert.assertEquals("\"3,\"\"14\"", DictionaryEntryLineParser.escape(original));
        Assert.assertEquals(original, DictionaryEntryLineParser.unescape(DictionaryEntryLineParser.escape(original)));
    }

    @Test
    public void testUnescape() {
        Assert.assertEquals("A", DictionaryEntryLineParser.unescape("\"A\""));
        Assert.assertEquals("\"A\"", DictionaryEntryLineParser.unescape("\"\"\"A\"\"\""));
        Assert.assertEquals("\"", DictionaryEntryLineParser.unescape("\"\"\"\""));
        Assert.assertEquals("\"\"", DictionaryEntryLineParser.unescape("\"\"\"\"\"\""));
        Assert.assertEquals("\"\"\"", DictionaryEntryLineParser.unescape("\"\"\"\"\"\"\"\""));
        Assert.assertEquals("\"\"\"\"\"", DictionaryEntryLineParser.unescape("\"\"\"\"\"\"\"\"\"\"\"\""));
    }

    // TODO: these tests should be checked, right now they are documenting what is happening.
    @Test
    public void testParseInputString() throws Exception {
        String input = "??????,1292,1292,4980,??,????,??,*,*,*,??????,???????????,???????????";
        String expected = Arrays.deepToString(new String[]{ "??????", "1292", "1292", "4980", "??", "????", "??", "*", "*", "*", "??????", "???????????", "???????????" });
        Assert.assertEquals(expected, given(input));
    }

    @Test
    public void testParseInputStringWithQuotes() throws Exception {
        String input = "\u65e5\u672c\u7d4c\u6e08\u65b0\u805e,1292,1292,4980,\u540d\u8a5e,\u56fa\u6709\u540d\u8a5e,\u7d44\u7e54,*,*,\"1,0\",\u65e5\u672c\u7d4c\u6e08\u65b0\u805e,\u30cb\u30db\u30f3\u30b1\u30a4\u30b6\u30a4\u30b7\u30f3\u30d6\u30f3,\u30cb\u30db\u30f3\u30b1\u30a4\u30b6\u30a4\u30b7\u30f3\u30d6\u30f3";
        String expected = Arrays.deepToString(new String[]{ "??????", "1292", "1292", "4980", "??", "????", "??", "*", "*", "1,0", "??????", "???????????", "???????????" });
        Assert.assertEquals(expected, given(input));
    }

    @Test
    public void testQuoteEscape() throws Exception {
        String input = "\u65e5\u672c\u7d4c\u6e08\u65b0\u805e,1292,1292,4980,\u540d\u8a5e,\u56fa\u6709\u540d\u8a5e,\u7d44\u7e54,*,*,\"1,0\",\u65e5\u672c\u7d4c\u6e08\u65b0\u805e,\u30cb\u30db\u30f3\u30b1\u30a4\u30b6\u30a4\u30b7\u30f3\u30d6\u30f3,\u30cb\u30db\u30f3\u30b1\u30a4\u30b6\u30a4\u30b7\u30f3\u30d6\u30f3";
        String expected = "\"\u65e5\u672c\u7d4c\u6e08\u65b0\u805e,1292,1292,4980,\u540d\u8a5e,\u56fa\u6709\u540d\u8a5e,\u7d44\u7e54,*,*,\"\"1,0\"\",\u65e5\u672c\u7d4c\u6e08\u65b0\u805e,\u30cb\u30db\u30f3\u30b1\u30a4\u30b6\u30a4\u30b7\u30f3\u30d6\u30f3,\u30cb\u30db\u30f3\u30b1\u30a4\u30b6\u30a4\u30b7\u30f3\u30d6\u30f3\"";
        Assert.assertEquals(expected, parser.escape(input));
    }
}

