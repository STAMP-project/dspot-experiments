/**
 * The MIT License
 *
 * Copyright 2017 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class StringUtilsTest {
    @Test
    public void testSplit() {
        List<String> list = StringUtils.split("", '/');
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("", list.get(0));
        list = StringUtils.split("//", '/');
        Assert.assertEquals(0, list.size());
        list = StringUtils.split("foo", '/');
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("foo", list.get(0));
        list = StringUtils.split("foo/", '/');
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("foo", list.get(0));
        list = StringUtils.split("/foo", '/');
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("foo", list.get(0));
        list = StringUtils.split("/foo/bar", '/');
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("foo", list.get(0));
        Assert.assertEquals("bar", list.get(1));
        list = StringUtils.split("|pi\\|pe|blah|", '|');
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("pi|pe", list.get(0));
        Assert.assertEquals("blah", list.get(1));
    }

    @Test
    public void testJoin() {
        String[] foo = new String[]{ "a", "b" };
        Assert.assertEquals("a,b", StringUtils.join(foo, ','));
        Assert.assertEquals("a,b", StringUtils.join(Arrays.asList(foo), ','));
    }
}

