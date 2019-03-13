/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.util;


import org.junit.Assert;
import org.junit.Test;


// see http://unicode-table.com for a graphical representation
public class BytesUtilsTest {
    @Test
    public void testByteCounting() throws Exception {
        // char   encoding   size
        // 1    = $          1
        // 2    = \u00A2     2
        // 3    = \u20AC     3
        // this falls outside Unicode BMP so we need to special handling
        // this takes two chars - high and low surrogate so
        // \u10348
        // 4    = high s     2
        // 5    = low  s     2
        // 6    = $          1
        // 7    = \u00A2     2
        // 8    = a          1
        // maybe it's LE or BE :)
        char[] chars = Character.toChars(66376);
        // thus
        // offset of each char in bytes (based on the offset of the previous char)
        // 0 + 1 + 2 + 3 + (2 + 2) + 1 + 2 + 1
        String unicode = (("$\u00a2\u20ac" + (chars[0])) + (chars[1])) + "$\u00a2a";
        // 7 unicode points but the non-BMP one takes 1 extra
        Assert.assertEquals(8, unicode.length());
        int[] bytePosition = BytesUtils.charToBytePosition(new BytesArray(unicode), 0, 2, 3, 1, 6, 3, 2, 5, 4);
        Assert.assertEquals(0, bytePosition[0]);
        Assert.assertEquals(((0 + 1) + 2), bytePosition[1]);
        Assert.assertEquals((((0 + 1) + 2) + 3), bytePosition[2]);
        Assert.assertEquals((0 + 1), bytePosition[3]);
        Assert.assertEquals((((((0 + 1) + 2) + 3) + (2 + 2)) + 1), bytePosition[4]);
        Assert.assertEquals((((0 + 1) + 2) + 3), bytePosition[5]);
        Assert.assertEquals(((0 + 1) + 2), bytePosition[6]);
        Assert.assertEquals(((((0 + 1) + 2) + 3) + (2 + 2)), bytePosition[7]);
        Assert.assertEquals(((((0 + 1) + 2) + 3) + 2), bytePosition[8]);
    }

    @Test
    public void testAnotherByteCounting() throws Exception {
        // 1 + 2 + 1
        String utf8 = "W\u00fcr";
        int[] bytePosition = BytesUtils.charToBytePosition(new BytesArray(utf8), 0, 1, 2);
        Assert.assertEquals(0, bytePosition[0]);
        Assert.assertEquals(1, bytePosition[1]);
        Assert.assertEquals((1 + 2), bytePosition[2]);
        // symbol G
        char[] chars = Character.toChars(119070);
        // 3 + 3 + (2 + 2) + 2 + 1
        utf8 = (("\ufef2\ufee6" + (chars[0])) + (chars[1])) + "\u00a2@";
        // 5 code points but 1 takes 2 chars in UTF-16
        Assert.assertEquals((5 + 1), utf8.length());
        bytePosition = BytesUtils.charToBytePosition(new BytesArray(utf8), 0, 1, 2, 3, 4, 5);
        Assert.assertEquals(0, bytePosition[0]);
        Assert.assertEquals((0 + 3), bytePosition[1]);
        Assert.assertEquals(((0 + 3) + 3), bytePosition[2]);
        Assert.assertEquals((((0 + 3) + 3) + 2), bytePosition[3]);
        Assert.assertEquals((((0 + 3) + 3) + (2 + 2)), bytePosition[4]);
        Assert.assertEquals(((((0 + 3) + 3) + (2 + 2)) + 2), bytePosition[5]);
    }

    @Test
    public void testStreamSize() throws Exception {
        BytesArray input = IOUtils.asBytes(getClass().getResourceAsStream("escape-size.txt"));
        Assert.assertEquals((5 * 2), input.size);
    }

    @Test
    public void testByteToChar() throws Exception {
        BytesArray input = IOUtils.asBytes(getClass().getResourceAsStream("escaped-chars.txt"));
        int[] chars = new int[]{ 9, 14, 0 };
        int[] bytePositions = BytesUtils.charToBytePosition(input, chars);
        Assert.assertEquals(10, bytePositions[0]);
        // LF/CR
        Assert.assertEquals((TestUtils.isWindows() ? 19 : 20), bytePositions[1]);
        Assert.assertEquals(0, bytePositions[2]);
    }
}

