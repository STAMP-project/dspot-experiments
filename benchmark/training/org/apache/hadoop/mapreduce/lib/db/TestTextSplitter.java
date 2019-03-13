/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.lib.db;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestTextSplitter {
    @Test
    public void testStringConvertEmpty() {
        TextSplitter splitter = new TextSplitter();
        BigDecimal emptyBigDec = splitter.stringToBigDecimal("");
        Assert.assertEquals(BigDecimal.ZERO, emptyBigDec);
    }

    @Test
    public void testBigDecConvertEmpty() {
        TextSplitter splitter = new TextSplitter();
        String emptyStr = splitter.bigDecimalToString(BigDecimal.ZERO);
        Assert.assertEquals("", emptyStr);
    }

    @Test
    public void testConvertA() {
        TextSplitter splitter = new TextSplitter();
        String out = splitter.bigDecimalToString(splitter.stringToBigDecimal("A"));
        Assert.assertEquals("A", out);
    }

    @Test
    public void testConvertZ() {
        TextSplitter splitter = new TextSplitter();
        String out = splitter.bigDecimalToString(splitter.stringToBigDecimal("Z"));
        Assert.assertEquals("Z", out);
    }

    @Test
    public void testConvertThreeChars() {
        TextSplitter splitter = new TextSplitter();
        String out = splitter.bigDecimalToString(splitter.stringToBigDecimal("abc"));
        Assert.assertEquals("abc", out);
    }

    @Test
    public void testConvertStr() {
        TextSplitter splitter = new TextSplitter();
        String out = splitter.bigDecimalToString(splitter.stringToBigDecimal("big str"));
        Assert.assertEquals("big str", out);
    }

    @Test
    public void testConvertChomped() {
        TextSplitter splitter = new TextSplitter();
        String out = splitter.bigDecimalToString(splitter.stringToBigDecimal("AVeryLongStringIndeed"));
        Assert.assertEquals("AVeryLon", out);
    }

    @Test
    public void testAlphabetSplit() throws SQLException {
        // This should give us 25 splits, one per letter.
        TextSplitter splitter = new TextSplitter();
        List<String> splits = splitter.split(25, "A", "Z", "");
        String[] expected = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
        assertArrayEquals(expected, splits.toArray(new String[0]));
    }

    @Test
    public void testCommonPrefix() throws SQLException {
        // Splits between 'Hand' and 'Hardy'
        TextSplitter splitter = new TextSplitter();
        List<String> splits = splitter.split(5, "nd", "rdy", "Ha");
        // Don't check for exact values in the middle, because the splitter generates some
        // ugly Unicode-isms. But do check that we get multiple splits and that it starts
        // and ends on the correct points.
        Assert.assertEquals("Hand", splits.get(0));
        Assert.assertEquals("Hardy", splits.get(((splits.size()) - 1)));
        Assert.assertEquals(6, splits.size());
    }
}

