/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.greenrobot.essentials;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilsTest {
    private static final String LINES = "Line 1\nLine 2\n\nLine 4\r\nLine 5\r\n\r\nLine 7";

    @Test
    public void testSplitLines() {
        String[] lines = StringUtils.splitLines(StringUtilsTest.LINES, false);
        Assert.assertEquals(7, lines.length);
        Assert.assertEquals("Line 1", lines[0]);
        Assert.assertEquals("Line 2", lines[1]);
        Assert.assertEquals("", lines[2]);
        Assert.assertEquals("Line 4", lines[3]);
        Assert.assertEquals("Line 5", lines[4]);
        Assert.assertEquals("", lines[5]);
        Assert.assertEquals("Line 7", lines[6]);
    }

    @Test
    public void testSplitLinesSkipEmptyLines() {
        String[] lines = StringUtils.splitLines(StringUtilsTest.LINES, true);
        Assert.assertEquals(5, lines.length);
        Assert.assertEquals("Line 1", lines[0]);
        Assert.assertEquals("Line 2", lines[1]);
        Assert.assertEquals("Line 4", lines[2]);
        Assert.assertEquals("Line 5", lines[3]);
        Assert.assertEquals("Line 7", lines[4]);
    }

    @Test
    public void testSplit() throws Exception {
        Assert.assertArrayEquals(ss("John", "Doe"), StringUtils.split("John Doe", ' '));
        Assert.assertArrayEquals(ss("John", "", "Doe", ""), StringUtils.split("John  Doe ", ' '));
        Assert.assertArrayEquals(ss("", "John", "Doe", ""), StringUtils.split(" John Doe ", ' '));
        Assert.assertArrayEquals(ss("John", "Christoph", "Doe"), StringUtils.split("John Christoph Doe", ' '));
        Assert.assertArrayEquals(ss("John", "", "", "Doe"), StringUtils.split("John,,,Doe", ','));
        Assert.assertArrayEquals(ss("John", "Doe", ""), StringUtils.split("John Doe ", ' '));
        Assert.assertArrayEquals(ss("John", "", "", ""), StringUtils.split("John,,,", ','));
    }

    @Test
    public void testFindLinesContaining() {
        String text = "LiXXXne 1\nLine 2\n\nLXXXine 4\r\nLine 5\r\nXXX\r\nLine 7";
        List<String> lines = StringUtils.findLinesContaining(text, "XXX");
        Assert.assertEquals(3, lines.size());
        Assert.assertEquals("LiXXXne 1", lines.get(0));
        Assert.assertEquals("LXXXine 4", lines.get(1));
        Assert.assertEquals("XXX", lines.get(2));
    }

    @Test
    public void testConcatLines() {
        String[] lines = StringUtils.splitLines(StringUtilsTest.LINES, false);
        ArrayList<String> list = new ArrayList<String>();
        for (String line : lines) {
            list.add(line);
        }
        String concated = StringUtils.join(list, "\n");
        Assert.assertEquals("Line 1\nLine 2\n\nLine 4\nLine 5\n\nLine 7", concated);
    }

    @Test
    public void testJoinIterable() {
        Assert.assertEquals("", StringUtils.join(((Iterable) (null)), "blub"));
        List<String> fooBarList = Arrays.asList("foo", "bar");
        Assert.assertEquals("foo,bar", StringUtils.join(fooBarList, ","));
        Assert.assertEquals("foo, bar", StringUtils.join(fooBarList, ", "));
    }

    @Test
    public void testJoinIntArray() {
        Assert.assertEquals("", StringUtils.join(((int[]) (null)), "blub"));
        int[] ints = new int[]{ 42, 23 };
        Assert.assertEquals("42,23", StringUtils.join(ints, ","));
        Assert.assertEquals("42, 23", StringUtils.join(ints, ", "));
    }

    @Test
    public void testJoinStringArray() {
        Assert.assertEquals("", StringUtils.join(((String[]) (null)), "blub"));
        String[] fooBar = new String[]{ "foo", "bar" };
        Assert.assertEquals("foo,bar", StringUtils.join(fooBar, ","));
        Assert.assertEquals("foo, bar", StringUtils.join(fooBar, ", "));
    }

    @Test
    public void testEllipsize() {
        Assert.assertEquals("He...", StringUtils.ellipsize("Hello world", 5));
        Assert.assertEquals("Hell>", StringUtils.ellipsize("Hello world", 5, ">"));
    }

    @Test
    public void testHex() {
        Assert.assertArrayEquals(new byte[]{ 0, 102, -1 }, StringUtils.parseHex("0066FF"));
    }
}

