/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class EmptyHeadersTest {
    private static final EmptyHeadersTest.TestEmptyHeaders HEADERS = new EmptyHeadersTest.TestEmptyHeaders();

    @Test(expected = UnsupportedOperationException.class)
    public void testAddStringValue() {
        EmptyHeadersTest.HEADERS.add("name", "value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddStringValues() {
        EmptyHeadersTest.HEADERS.add("name", "value1", "value2");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddStringValuesIterable() {
        add("name", Arrays.asList("value1", "value2"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddBoolean() {
        addBoolean("name", true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddByte() {
        addByte("name", ((byte) (1)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddChar() {
        addChar("name", 'a');
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddDouble() {
        addDouble("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddFloat() {
        addFloat("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddInt() {
        addInt("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddLong() {
        addLong("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddShort() {
        addShort("name", ((short) (0)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddTimeMillis() {
        addTimeMillis("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetStringValue() {
        EmptyHeadersTest.HEADERS.set("name", "value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetStringValues() {
        EmptyHeadersTest.HEADERS.set("name", "value1", "value2");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetStringValuesIterable() {
        EmptyHeadersTest.HEADERS.set("name", Arrays.asList("value1", "value2"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetBoolean() {
        setBoolean("name", true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetByte() {
        setByte("name", ((byte) (1)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetChar() {
        setChar("name", 'a');
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetDouble() {
        setDouble("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetFloat() {
        setFloat("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetInt() {
        setInt("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetLong() {
        setLong("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetShort() {
        setShort("name", ((short) (0)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetTimeMillis() {
        setTimeMillis("name", 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetAll() {
        setAll(new EmptyHeadersTest.TestEmptyHeaders());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSet() {
        set(new EmptyHeadersTest.TestEmptyHeaders());
    }

    @Test
    public void testGet() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.get("name1"));
    }

    @Test
    public void testGetDefault() {
        Assert.assertEquals("default", get("name1", "default"));
    }

    @Test
    public void testGetAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getAndRemove("name1"));
    }

    @Test
    public void testGetAndRemoveDefault() {
        Assert.assertEquals("default", getAndRemove("name1", "default"));
    }

    @Test
    public void testGetAll() {
        Assert.assertEquals(Collections.emptyList(), getAll("name1"));
    }

    @Test
    public void testGetAllAndRemove() {
        Assert.assertEquals(Collections.emptyList(), getAllAndRemove("name1"));
    }

    @Test
    public void testGetBoolean() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getBoolean("name1"));
    }

    @Test
    public void testGetBooleanDefault() {
        Assert.assertTrue(getBoolean("name1", true));
    }

    @Test
    public void testGetBooleanAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getBooleanAndRemove("name1"));
    }

    @Test
    public void testGetBooleanAndRemoveDefault() {
        Assert.assertTrue(getBooleanAndRemove("name1", true));
    }

    @Test
    public void testGetByte() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getByte("name1"));
    }

    @Test
    public void testGetByteDefault() {
        Assert.assertEquals(((byte) (0)), getByte("name1", ((byte) (0))));
    }

    @Test
    public void testGetByteAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getByteAndRemove("name1"));
    }

    @Test
    public void testGetByteAndRemoveDefault() {
        Assert.assertEquals(((byte) (0)), getByteAndRemove("name1", ((byte) (0))));
    }

    @Test
    public void testGetChar() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getChar("name1"));
    }

    @Test
    public void testGetCharDefault() {
        Assert.assertEquals('x', getChar("name1", 'x'));
    }

    @Test
    public void testGetCharAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getCharAndRemove("name1"));
    }

    @Test
    public void testGetCharAndRemoveDefault() {
        Assert.assertEquals('x', getCharAndRemove("name1", 'x'));
    }

    @Test
    public void testGetDouble() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getDouble("name1"));
    }

    @Test
    public void testGetDoubleDefault() {
        Assert.assertEquals(1, getDouble("name1", 1), 0);
    }

    @Test
    public void testGetDoubleAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getDoubleAndRemove("name1"));
    }

    @Test
    public void testGetDoubleAndRemoveDefault() {
        Assert.assertEquals(1, getDoubleAndRemove("name1", 1), 0);
    }

    @Test
    public void testGetFloat() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getFloat("name1"));
    }

    @Test
    public void testGetFloatDefault() {
        Assert.assertEquals(1, getFloat("name1", 1), 0);
    }

    @Test
    public void testGetFloatAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getFloatAndRemove("name1"));
    }

    @Test
    public void testGetFloatAndRemoveDefault() {
        Assert.assertEquals(1, getFloatAndRemove("name1", 1), 0);
    }

    @Test
    public void testGetInt() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getInt("name1"));
    }

    @Test
    public void testGetIntDefault() {
        Assert.assertEquals(1, getInt("name1", 1));
    }

    @Test
    public void testGetIntAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getIntAndRemove("name1"));
    }

    @Test
    public void testGetIntAndRemoveDefault() {
        Assert.assertEquals(1, getIntAndRemove("name1", 1));
    }

    @Test
    public void testGetLong() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getLong("name1"));
    }

    @Test
    public void testGetLongDefault() {
        Assert.assertEquals(1, getLong("name1", 1));
    }

    @Test
    public void testGetLongAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getLongAndRemove("name1"));
    }

    @Test
    public void testGetLongAndRemoveDefault() {
        Assert.assertEquals(1, getLongAndRemove("name1", 1));
    }

    @Test
    public void testGetShort() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getShort("name1"));
    }

    @Test
    public void testGetShortDefault() {
        Assert.assertEquals(1, getShort("name1", ((short) (1))));
    }

    @Test
    public void testGetShortAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getShortAndRemove("name1"));
    }

    @Test
    public void testGetShortAndRemoveDefault() {
        Assert.assertEquals(1, getShortAndRemove("name1", ((short) (1))));
    }

    @Test
    public void testGetTimeMillis() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getTimeMillis("name1"));
    }

    @Test
    public void testGetTimeMillisDefault() {
        Assert.assertEquals(1, getTimeMillis("name1", 1));
    }

    @Test
    public void testGetTimeMillisAndRemove() {
        Assert.assertNull(EmptyHeadersTest.HEADERS.getTimeMillisAndRemove("name1"));
    }

    @Test
    public void testGetTimeMillisAndRemoveDefault() {
        Assert.assertEquals(1, getTimeMillisAndRemove("name1", 1));
    }

    @Test
    public void testContains() {
        Assert.assertFalse(EmptyHeadersTest.HEADERS.contains("name1"));
    }

    @Test
    public void testContainsWithValue() {
        Assert.assertFalse(contains("name1", "value1"));
    }

    @Test
    public void testContainsBoolean() {
        Assert.assertFalse(containsBoolean("name1", false));
    }

    @Test
    public void testContainsByte() {
        Assert.assertFalse(containsByte("name1", ((byte) ('x'))));
    }

    @Test
    public void testContainsChar() {
        Assert.assertFalse(containsChar("name1", 'x'));
    }

    @Test
    public void testContainsDouble() {
        Assert.assertFalse(containsDouble("name1", 1));
    }

    @Test
    public void testContainsFloat() {
        Assert.assertFalse(containsFloat("name1", 1));
    }

    @Test
    public void testContainsInt() {
        Assert.assertFalse(containsInt("name1", 1));
    }

    @Test
    public void testContainsLong() {
        Assert.assertFalse(containsLong("name1", 1));
    }

    @Test
    public void testContainsShort() {
        Assert.assertFalse(containsShort("name1", ((short) (1))));
    }

    @Test
    public void testContainsTimeMillis() {
        Assert.assertFalse(containsTimeMillis("name1", 1));
    }

    @Test
    public void testContainsObject() {
        Assert.assertFalse(containsObject("name1", ""));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(isEmpty());
    }

    @Test
    public void testClear() {
        Assert.assertSame(EmptyHeadersTest.HEADERS, clear());
    }

    @Test
    public void testSize() {
        Assert.assertEquals(0, size());
    }

    @Test
    public void testValueIterator() {
        Assert.assertFalse(valueIterator("name1").hasNext());
    }

    private static final class TestEmptyHeaders extends EmptyHeaders<String, String, EmptyHeadersTest.TestEmptyHeaders> {}
}

