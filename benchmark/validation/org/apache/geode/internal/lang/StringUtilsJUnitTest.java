/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.lang;


import StringUtils.LINE_SEPARATOR;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.geode.DataSerializer;
import org.apache.geode.internal.cache.CachedDeserializable;
import org.apache.geode.internal.cache.CachedDeserializableFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * The StringUtilsJUnitTest is a test suite containing test cases for testing the contract and
 * functionality of the StringUtils class.
 * <p/>
 *
 * @see org.apache.geode.internal.lang.StringUtils
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since GemFire 7.0
 */
@SuppressWarnings("null")
public class StringUtilsJUnitTest {
    @Test
    public void arrayToString() {
        assertThat(StringUtils.arrayToString(null)).isEqualTo("null");
        String[] array1 = new String[]{ "one", "two", "three" };
        assertThat(StringUtils.arrayToString(array1)).isEqualTo("one, two, three");
        String[] array2 = new String[]{ "one", null, "three" };
        assertThat(StringUtils.arrayToString(array2)).isEqualTo("one, null, three");
        String[] array3 = new String[]{ null };
        assertThat(StringUtils.arrayToString(array3)).isEqualTo("null");
    }

    @Test
    public void testGetDigitsOnly() {
        Assert.assertEquals("", StringUtils.getDigitsOnly(null));
        Assert.assertEquals("", StringUtils.getDigitsOnly(""));
        Assert.assertEquals("", StringUtils.getDigitsOnly(" "));
        Assert.assertEquals("", StringUtils.getDigitsOnly("abc"));
        Assert.assertEquals("", StringUtils.getDigitsOnly("abcOneTwoThree"));
        Assert.assertEquals("", StringUtils.getDigitsOnly("@$$!"));
        Assert.assertEquals("", StringUtils.getDigitsOnly("lOlOl"));
        Assert.assertEquals("111", StringUtils.getDigitsOnly("1O1O1"));
        Assert.assertEquals("7", StringUtils.getDigitsOnly("OO7"));
        Assert.assertEquals("007", StringUtils.getDigitsOnly("007"));
        Assert.assertEquals("123456789", StringUtils.getDigitsOnly("123,456.789"));
    }

    @Test
    public void testWrap() {
        final String line = "The line of text to split for testing purposes!";
        final String expectedLine = "The line of".concat(LINE_SEPARATOR).concat("text to split").concat(LINE_SEPARATOR).concat("for testing").concat(LINE_SEPARATOR).concat("purposes!");
        final String actualLine = StringUtils.wrap(line, 15, null);
        Assert.assertNotNull(actualLine);
        Assert.assertEquals(expectedLine, actualLine);
    }

    @Test
    public void testWrapWithIndent() {
        final String line = "The line of text to split for testing purposes!";
        final String expectedLine = "The line of".concat(LINE_SEPARATOR).concat("\t").concat("text to split").concat(LINE_SEPARATOR).concat("\t").concat("for testing").concat(LINE_SEPARATOR).concat("\t").concat("purposes!");
        final String actualLine = StringUtils.wrap(line, 15, "\t");
        Assert.assertNotNull(actualLine);
        Assert.assertEquals(expectedLine, actualLine);
    }

    @Test
    public void testForceToString() throws IOException {
        Assert.assertEquals("null", StringUtils.forceToString(null));
        Assert.assertEquals("Object[][]", StringUtils.forceToString(new Object[0][0]));
        Assert.assertEquals("byte[1, 2]", StringUtils.forceToString(new byte[]{ 1, 2 }));
        Assert.assertEquals("int[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16]", StringUtils.forceToString(new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }));
        Assert.assertEquals("long[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, and 1 more]", StringUtils.forceToString(new long[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }));
        Assert.assertEquals("short[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, and 2 more]", StringUtils.forceToString(new short[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 }));
        Assert.assertEquals("char[1, 2, 3]", StringUtils.forceToString(new char[]{ '1', '2', '3' }));
        Assert.assertEquals("boolean[true, false]", StringUtils.forceToString(new boolean[]{ true, false }));
        Assert.assertEquals("float[1.0]", StringUtils.forceToString(new float[]{ 1.0F }));
        Assert.assertEquals("double[1.0, 2.0]", StringUtils.forceToString(new double[]{ 1.0, 2.0 }));
        Assert.assertEquals("String[start, middle, end]", StringUtils.forceToString(new String[]{ "start", "middle", "end" }));
        // make sure CacheDeserializables do not get deserialized when getting their string form
        Object v = "value";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        DataSerializer.writeObject(v, dos);
        dos.flush();
        byte[] valueBytes = baos.toByteArray();
        CachedDeserializable cd = CachedDeserializableFactory.create(valueBytes, null);
        Assert.assertSame(valueBytes, cd.getValue());
        Assert.assertEquals("value", StringUtils.forceToString(cd));
        Assert.assertSame(valueBytes, cd.getValue());
    }
}

