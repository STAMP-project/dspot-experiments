/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.tests.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.util.StringUtil;


public class StringUtilTest {
    @Test
    public void testFromHexToHex() {
        Assert.assertEquals("abcdeffaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", StringUtil.toHex(StringUtil.fromHex("abcdeffaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")));
        Assert.assertEquals("", StringUtil.toHex(StringUtil.fromHex("")));
    }

    @Test(expected = Exception.class)
    public void testFromHexInvalid1() {
        StringUtil.toHex(StringUtil.fromHex("a"));
    }

    @Test(expected = Exception.class)
    public void testFromHexInvalid2() {
        StringUtil.toHex(StringUtil.fromHex("INVALID!"));
    }

    @Test(expected = Exception.class)
    public void testFromHexInvalid3() {
        StringUtil.toHex(StringUtil.fromHex("INVALID"));
    }

    @Test
    public void testStringJoin() {
        Assert.assertEquals("a;b;c", StringUtil.join(new String[]{ "a", "b", "c" }, ";"));
        Assert.assertEquals("a b c", StringUtil.join(Arrays.asList(new String[]{ "a", "b", "c" }), " "));
        Assert.assertEquals("1.9 + 2.8 + 3.7", StringUtil.join(new Double[]{ 1.911, 2.833, 3.744 }, " + ", new org.syncany.util.StringUtil.StringJoinListener<Double>() {
            @Override
            public String getString(Double number) {
                return String.format("%.1f", number);
            }
        }));
    }

    @Test
    public void testToBytesUTF8() {
        Assert.assertArrayEquals(new byte[]{ 0 }, StringUtil.toBytesUTF8("\u0000"));
        Assert.assertArrayEquals(new byte[]{ ((byte) (195)), ((byte) (164)), ((byte) (195)), ((byte) (182)), ((byte) (195)), ((byte) (188)) }, StringUtil.toBytesUTF8("???"));
        Assert.assertArrayEquals(new byte[]{ ((byte) (231)), ((byte) (172)), ((byte) (170)), ((byte) (233)), ((byte) (170)), ((byte) (143)) }, StringUtil.toBytesUTF8("??"));
    }

    @Test
    public void testToCamelCase() {
        Assert.assertEquals("HelloWorld", StringUtil.toCamelCase("hello world"));
        Assert.assertEquals("HelloWorld", StringUtil.toCamelCase("hello_world"));
        Assert.assertEquals("HelloWorld", StringUtil.toCamelCase("hello-world"));
        Assert.assertEquals("HelloWorld", StringUtil.toCamelCase("hello-World"));
    }

    @Test
    public void testToSnakeCase() {
        Assert.assertEquals("hello_world", StringUtil.toSnakeCase("hello world"));
        Assert.assertEquals("hello_world", StringUtil.toSnakeCase("HelloWorld"));
        Assert.assertEquals("hello_world", StringUtil.toSnakeCase("helloWorld"));
        Assert.assertEquals("hello_world", StringUtil.toSnakeCase("hello_world"));
        Assert.assertEquals("s3", StringUtil.toSnakeCase("s3"));
    }

    @Test
    public void testSubstrCount() {
        Assert.assertEquals(1, StringUtil.substrCount("some/path", "/"));
        Assert.assertEquals(2, StringUtil.substrCount("some/path/", "/"));
        Assert.assertEquals(1, StringUtil.substrCount("annanna", "anna"));
    }
}

