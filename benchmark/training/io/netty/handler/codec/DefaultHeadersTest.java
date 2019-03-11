/**
 * Copyright 2014 The Netty Project
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


import CharSequenceValueConverter.INSTANCE;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link DefaultHeaders}.
 */
public class DefaultHeadersTest {
    private static final class TestDefaultHeaders extends DefaultHeaders<CharSequence, CharSequence, DefaultHeadersTest.TestDefaultHeaders> {
        TestDefaultHeaders() {
            this(INSTANCE);
        }

        TestDefaultHeaders(ValueConverter<CharSequence> converter) {
            super(converter);
        }
    }

    @Test
    public void addShouldIncreaseAndRemoveShouldDecreaseTheSize() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        Assert.assertEquals(0, size());
        headers.add(of("name1"), of("value1"), of("value2"));
        Assert.assertEquals(2, size());
        headers.add(of("name2"), of("value3"), of("value4"));
        Assert.assertEquals(4, size());
        headers.add(of("name3"), of("value5"));
        Assert.assertEquals(5, size());
        headers.remove(of("name3"));
        Assert.assertEquals(4, size());
        headers.remove(of("name1"));
        Assert.assertEquals(2, size());
        headers.remove(of("name2"));
        Assert.assertEquals(0, size());
        Assert.assertTrue(isEmpty());
    }

    @Test
    public void afterClearHeadersShouldBeEmpty() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"));
        headers.add(of("name2"), of("value2"));
        Assert.assertEquals(2, size());
        clear();
        Assert.assertEquals(0, size());
        Assert.assertTrue(isEmpty());
        Assert.assertFalse(headers.contains(of("name1")));
        Assert.assertFalse(headers.contains(of("name2")));
    }

    @Test
    public void removingANameForASecondTimeShouldReturnFalse() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"));
        headers.add(of("name2"), of("value2"));
        Assert.assertTrue(headers.remove(of("name2")));
        Assert.assertFalse(headers.remove(of("name2")));
    }

    @Test
    public void multipleValuesPerNameShouldBeAllowed() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name"), of("value1"));
        headers.add(of("name"), of("value2"));
        headers.add(of("name"), of("value3"));
        Assert.assertEquals(3, size());
        List<CharSequence> values = headers.getAll(of("name"));
        Assert.assertEquals(3, values.size());
        Assert.assertTrue(values.containsAll(Arrays.asList(of("value1"), of("value2"), of("value3"))));
    }

    @Test
    public void multipleValuesPerNameIterator() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name"), of("value1"));
        headers.add(of("name"), of("value2"));
        headers.add(of("name"), of("value3"));
        Assert.assertEquals(3, size());
        List<CharSequence> values = new ArrayList<CharSequence>();
        Iterator<CharSequence> itr = headers.valueIterator(of("name"));
        while (itr.hasNext()) {
            values.add(itr.next());
        } 
        Assert.assertEquals(3, values.size());
        Assert.assertTrue(values.containsAll(Arrays.asList(of("value1"), of("value2"), of("value3"))));
    }

    @Test
    public void multipleValuesPerNameIteratorEmpty() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        List<CharSequence> values = new ArrayList<CharSequence>();
        Iterator<CharSequence> itr = headers.valueIterator(of("name"));
        while (itr.hasNext()) {
            values.add(itr.next());
        } 
        Assert.assertEquals(0, values.size());
        try {
            itr.next();
            Assert.fail();
        } catch (NoSuchElementException ignored) {
            // ignored
        }
    }

    @Test
    public void testContains() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.addBoolean(of("boolean"), true);
        Assert.assertTrue(headers.containsBoolean(of("boolean"), true));
        Assert.assertFalse(headers.containsBoolean(of("boolean"), false));
        headers.addLong(of("long"), Long.MAX_VALUE);
        Assert.assertTrue(headers.containsLong(of("long"), Long.MAX_VALUE));
        Assert.assertFalse(headers.containsLong(of("long"), Long.MIN_VALUE));
        headers.addInt(of("int"), Integer.MIN_VALUE);
        Assert.assertTrue(headers.containsInt(of("int"), Integer.MIN_VALUE));
        Assert.assertFalse(headers.containsInt(of("int"), Integer.MAX_VALUE));
        headers.addShort(of("short"), Short.MAX_VALUE);
        Assert.assertTrue(headers.containsShort(of("short"), Short.MAX_VALUE));
        Assert.assertFalse(headers.containsShort(of("short"), Short.MIN_VALUE));
        headers.addChar(of("char"), Character.MAX_VALUE);
        Assert.assertTrue(headers.containsChar(of("char"), Character.MAX_VALUE));
        Assert.assertFalse(headers.containsChar(of("char"), Character.MIN_VALUE));
        headers.addByte(of("byte"), Byte.MAX_VALUE);
        Assert.assertTrue(headers.containsByte(of("byte"), Byte.MAX_VALUE));
        Assert.assertFalse(headers.containsLong(of("byte"), Byte.MIN_VALUE));
        headers.addDouble(of("double"), Double.MAX_VALUE);
        Assert.assertTrue(headers.containsDouble(of("double"), Double.MAX_VALUE));
        Assert.assertFalse(headers.containsDouble(of("double"), Double.MIN_VALUE));
        headers.addFloat(of("float"), Float.MAX_VALUE);
        Assert.assertTrue(headers.containsFloat(of("float"), Float.MAX_VALUE));
        Assert.assertFalse(headers.containsFloat(of("float"), Float.MIN_VALUE));
        long millis = System.currentTimeMillis();
        headers.addTimeMillis(of("millis"), millis);
        Assert.assertTrue(headers.containsTimeMillis(of("millis"), millis));
        // This test doesn't work on midnight, January 1, 1970 UTC
        Assert.assertFalse(headers.containsTimeMillis(of("millis"), 0));
        headers.addObject(of("object"), "Hello World");
        Assert.assertTrue(headers.containsObject(of("object"), "Hello World"));
        Assert.assertFalse(headers.containsObject(of("object"), ""));
        headers.add(of("name"), of("value"));
        Assert.assertTrue(headers.contains(of("name"), of("value")));
        Assert.assertFalse(headers.contains(of("name"), of("value1")));
    }

    @Test
    public void testCopy() throws Exception {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.addBoolean(of("boolean"), true);
        headers.addLong(of("long"), Long.MAX_VALUE);
        headers.addInt(of("int"), Integer.MIN_VALUE);
        headers.addShort(of("short"), Short.MAX_VALUE);
        headers.addChar(of("char"), Character.MAX_VALUE);
        headers.addByte(of("byte"), Byte.MAX_VALUE);
        headers.addDouble(of("double"), Double.MAX_VALUE);
        headers.addFloat(of("float"), Float.MAX_VALUE);
        long millis = System.currentTimeMillis();
        headers.addTimeMillis(of("millis"), millis);
        headers.addObject(of("object"), "Hello World");
        headers.add(of("name"), of("value"));
        headers = DefaultHeadersTest.newInstance().add(headers);
        Assert.assertTrue(headers.containsBoolean(of("boolean"), true));
        Assert.assertFalse(headers.containsBoolean(of("boolean"), false));
        Assert.assertTrue(headers.containsLong(of("long"), Long.MAX_VALUE));
        Assert.assertFalse(headers.containsLong(of("long"), Long.MIN_VALUE));
        Assert.assertTrue(headers.containsInt(of("int"), Integer.MIN_VALUE));
        Assert.assertFalse(headers.containsInt(of("int"), Integer.MAX_VALUE));
        Assert.assertTrue(headers.containsShort(of("short"), Short.MAX_VALUE));
        Assert.assertFalse(headers.containsShort(of("short"), Short.MIN_VALUE));
        Assert.assertTrue(headers.containsChar(of("char"), Character.MAX_VALUE));
        Assert.assertFalse(headers.containsChar(of("char"), Character.MIN_VALUE));
        Assert.assertTrue(headers.containsByte(of("byte"), Byte.MAX_VALUE));
        Assert.assertFalse(headers.containsLong(of("byte"), Byte.MIN_VALUE));
        Assert.assertTrue(headers.containsDouble(of("double"), Double.MAX_VALUE));
        Assert.assertFalse(headers.containsDouble(of("double"), Double.MIN_VALUE));
        Assert.assertTrue(headers.containsFloat(of("float"), Float.MAX_VALUE));
        Assert.assertFalse(headers.containsFloat(of("float"), Float.MIN_VALUE));
        Assert.assertTrue(headers.containsTimeMillis(of("millis"), millis));
        // This test doesn't work on midnight, January 1, 1970 UTC
        Assert.assertFalse(headers.containsTimeMillis(of("millis"), 0));
        Assert.assertTrue(headers.containsObject(of("object"), "Hello World"));
        Assert.assertFalse(headers.containsObject(of("object"), ""));
        Assert.assertTrue(headers.contains(of("name"), of("value")));
        Assert.assertFalse(headers.contains(of("name"), of("value1")));
    }

    @Test
    public void canMixConvertedAndNormalValues() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name"), of("value"));
        headers.addInt(of("name"), 100);
        headers.addBoolean(of("name"), false);
        Assert.assertEquals(3, size());
        Assert.assertTrue(headers.contains(of("name")));
        Assert.assertTrue(headers.contains(of("name"), of("value")));
        Assert.assertTrue(headers.containsInt(of("name"), 100));
        Assert.assertTrue(headers.containsBoolean(of("name"), false));
    }

    @Test
    public void testGetAndRemove() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"));
        headers.add(of("name2"), of("value2"), of("value3"));
        headers.add(of("name3"), of("value4"), of("value5"), of("value6"));
        Assert.assertEquals(of("value1"), headers.getAndRemove(of("name1"), of("defaultvalue")));
        Assert.assertEquals(of("value2"), headers.getAndRemove(of("name2")));
        Assert.assertNull(headers.getAndRemove(of("name2")));
        Assert.assertEquals(Arrays.asList(of("value4"), of("value5"), of("value6")), headers.getAllAndRemove(of("name3")));
        Assert.assertEquals(0, size());
        Assert.assertNull(headers.getAndRemove(of("noname")));
        Assert.assertEquals(of("defaultvalue"), headers.getAndRemove(of("noname"), of("defaultvalue")));
    }

    @Test
    public void whenNameContainsMultipleValuesGetShouldReturnTheFirst() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"), of("value2"));
        Assert.assertEquals(of("value1"), headers.get(of("name1")));
    }

    @Test
    public void getWithDefaultValueWorks() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"));
        Assert.assertEquals(of("value1"), headers.get(of("name1"), of("defaultvalue")));
        Assert.assertEquals(of("defaultvalue"), headers.get(of("noname"), of("defaultvalue")));
    }

    @Test
    public void setShouldOverWritePreviousValue() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.set(of("name"), of("value1"));
        headers.set(of("name"), of("value2"));
        Assert.assertEquals(1, size());
        Assert.assertEquals(1, size());
        Assert.assertEquals(of("value2"), headers.getAll(of("name")).get(0));
        Assert.assertEquals(of("value2"), headers.get(of("name")));
    }

    @Test
    public void setAllShouldOverwriteSomeAndLeaveOthersUntouched() {
        DefaultHeadersTest.TestDefaultHeaders h1 = DefaultHeadersTest.newInstance();
        h1.add(of("name1"), of("value1"));
        h1.add(of("name2"), of("value2"));
        h1.add(of("name2"), of("value3"));
        h1.add(of("name3"), of("value4"));
        DefaultHeadersTest.TestDefaultHeaders h2 = DefaultHeadersTest.newInstance();
        h2.add(of("name1"), of("value5"));
        h2.add(of("name2"), of("value6"));
        h2.add(of("name1"), of("value7"));
        DefaultHeadersTest.TestDefaultHeaders expected = DefaultHeadersTest.newInstance();
        expected.add(of("name1"), of("value5"));
        expected.add(of("name2"), of("value6"));
        expected.add(of("name1"), of("value7"));
        expected.add(of("name3"), of("value4"));
        setAll(h2);
        Assert.assertEquals(expected, h1);
    }

    @Test
    public void headersWithSameNamesAndValuesShouldBeEquivalent() {
        DefaultHeadersTest.TestDefaultHeaders headers1 = DefaultHeadersTest.newInstance();
        headers1.add(of("name1"), of("value1"));
        headers1.add(of("name2"), of("value2"));
        headers1.add(of("name2"), of("value3"));
        DefaultHeadersTest.TestDefaultHeaders headers2 = DefaultHeadersTest.newInstance();
        headers2.add(of("name1"), of("value1"));
        headers2.add(of("name2"), of("value2"));
        headers2.add(of("name2"), of("value3"));
        Assert.assertEquals(headers1, headers2);
        Assert.assertEquals(headers2, headers1);
        Assert.assertEquals(headers1, headers1);
        Assert.assertEquals(headers2, headers2);
        Assert.assertEquals(headers1.hashCode(), headers2.hashCode());
        Assert.assertEquals(headers1.hashCode(), headers1.hashCode());
        Assert.assertEquals(headers2.hashCode(), headers2.hashCode());
    }

    @Test
    public void emptyHeadersShouldBeEqual() {
        DefaultHeadersTest.TestDefaultHeaders headers1 = DefaultHeadersTest.newInstance();
        DefaultHeadersTest.TestDefaultHeaders headers2 = DefaultHeadersTest.newInstance();
        Assert.assertNotSame(headers1, headers2);
        Assert.assertEquals(headers1, headers2);
        Assert.assertEquals(headers1.hashCode(), headers2.hashCode());
    }

    @Test
    public void headersWithSameNamesButDifferentValuesShouldNotBeEquivalent() {
        DefaultHeadersTest.TestDefaultHeaders headers1 = DefaultHeadersTest.newInstance();
        headers1.add(of("name1"), of("value1"));
        DefaultHeadersTest.TestDefaultHeaders headers2 = DefaultHeadersTest.newInstance();
        headers1.add(of("name1"), of("value2"));
        Assert.assertNotEquals(headers1, headers2);
    }

    @Test
    public void subsetOfHeadersShouldNotBeEquivalent() {
        DefaultHeadersTest.TestDefaultHeaders headers1 = DefaultHeadersTest.newInstance();
        headers1.add(of("name1"), of("value1"));
        headers1.add(of("name2"), of("value2"));
        DefaultHeadersTest.TestDefaultHeaders headers2 = DefaultHeadersTest.newInstance();
        headers1.add(of("name1"), of("value1"));
        Assert.assertNotEquals(headers1, headers2);
    }

    @Test
    public void headersWithDifferentNamesAndValuesShouldNotBeEquivalent() {
        DefaultHeadersTest.TestDefaultHeaders h1 = DefaultHeadersTest.newInstance();
        h1.set(of("name1"), of("value1"));
        DefaultHeadersTest.TestDefaultHeaders h2 = DefaultHeadersTest.newInstance();
        h2.set(of("name2"), of("value2"));
        Assert.assertNotEquals(h1, h2);
        Assert.assertNotEquals(h2, h1);
        Assert.assertEquals(h1, h1);
        Assert.assertEquals(h2, h2);
    }

    @Test(expected = NoSuchElementException.class)
    public void iterateEmptyHeadersShouldThrow() {
        Iterator<Map.Entry<CharSequence, CharSequence>> iterator = iterator();
        Assert.assertFalse(iterator.hasNext());
        iterator.next();
    }

    @Test
    public void iteratorShouldReturnAllNameValuePairs() {
        DefaultHeadersTest.TestDefaultHeaders headers1 = DefaultHeadersTest.newInstance();
        headers1.add(of("name1"), of("value1"), of("value2"));
        headers1.add(of("name2"), of("value3"));
        headers1.add(of("name3"), of("value4"), of("value5"), of("value6"));
        headers1.add(of("name1"), of("value7"), of("value8"));
        Assert.assertEquals(8, size());
        DefaultHeadersTest.TestDefaultHeaders headers2 = DefaultHeadersTest.newInstance();
        for (Map.Entry<CharSequence, CharSequence> entry : headers1) {
            headers2.add(entry.getKey(), entry.getValue());
        }
        Assert.assertEquals(headers1, headers2);
    }

    @Test
    public void iteratorSetValueShouldChangeHeaderValue() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"), of("value2"), of("value3"));
        headers.add(of("name2"), of("value4"));
        Assert.assertEquals(4, size());
        Iterator<Map.Entry<CharSequence, CharSequence>> iter = iterator();
        while (iter.hasNext()) {
            Map.Entry<CharSequence, CharSequence> header = iter.next();
            if ((of("name1").equals(header.getKey())) && (of("value2").equals(header.getValue()))) {
                header.setValue(of("updatedvalue2"));
                Assert.assertEquals(of("updatedvalue2"), header.getValue());
            }
            if ((of("name1").equals(header.getKey())) && (of("value3").equals(header.getValue()))) {
                header.setValue(of("updatedvalue3"));
                Assert.assertEquals(of("updatedvalue3"), header.getValue());
            }
        } 
        Assert.assertEquals(4, size());
        Assert.assertTrue(headers.contains(of("name1"), of("updatedvalue2")));
        Assert.assertFalse(headers.contains(of("name1"), of("value2")));
        Assert.assertTrue(headers.contains(of("name1"), of("updatedvalue3")));
        Assert.assertFalse(headers.contains(of("name1"), of("value3")));
    }

    @Test
    public void testEntryEquals() {
        Map.Entry<CharSequence, CharSequence> same1 = add("name", "value").iterator().next();
        Map.Entry<CharSequence, CharSequence> same2 = add("name", "value").iterator().next();
        Assert.assertEquals(same1, same2);
        Assert.assertEquals(same1.hashCode(), same2.hashCode());
        Map.Entry<CharSequence, CharSequence> nameDifferent1 = add("name1", "value").iterator().next();
        Map.Entry<CharSequence, CharSequence> nameDifferent2 = add("name2", "value").iterator().next();
        Assert.assertNotEquals(nameDifferent1, nameDifferent2);
        Assert.assertNotEquals(nameDifferent1.hashCode(), nameDifferent2.hashCode());
        Map.Entry<CharSequence, CharSequence> valueDifferent1 = add("name", "value1").iterator().next();
        Map.Entry<CharSequence, CharSequence> valueDifferent2 = add("name", "value2").iterator().next();
        Assert.assertNotEquals(valueDifferent1, valueDifferent2);
        Assert.assertNotEquals(valueDifferent1.hashCode(), valueDifferent2.hashCode());
    }

    @Test
    public void getAllReturnsEmptyListForUnknownName() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        Assert.assertEquals(0, size());
    }

    @Test
    public void setHeadersShouldClearAndOverwrite() {
        DefaultHeadersTest.TestDefaultHeaders headers1 = DefaultHeadersTest.newInstance();
        headers1.add(of("name"), of("value"));
        DefaultHeadersTest.TestDefaultHeaders headers2 = DefaultHeadersTest.newInstance();
        headers2.add(of("name"), of("newvalue"));
        headers2.add(of("name1"), of("value1"));
        headers1.set(headers2);
        Assert.assertEquals(headers1, headers2);
    }

    @Test
    public void setAllHeadersShouldOnlyOverwriteHeaders() {
        DefaultHeadersTest.TestDefaultHeaders headers1 = DefaultHeadersTest.newInstance();
        headers1.add(of("name"), of("value"));
        headers1.add(of("name1"), of("value1"));
        DefaultHeadersTest.TestDefaultHeaders headers2 = DefaultHeadersTest.newInstance();
        headers2.add(of("name"), of("newvalue"));
        headers2.add(of("name2"), of("value2"));
        DefaultHeadersTest.TestDefaultHeaders expected = DefaultHeadersTest.newInstance();
        expected.add(of("name"), of("newvalue"));
        expected.add(of("name1"), of("value1"));
        expected.add(of("name2"), of("value2"));
        setAll(headers2);
        Assert.assertEquals(headers1, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSelf() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(headers);
    }

    @Test
    public void testSetSelfIsNoOp() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        add("name", "value");
        headers.set(headers);
        Assert.assertEquals(1, size());
    }

    @Test
    public void testToString() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"));
        headers.add(of("name1"), of("value2"));
        headers.add(of("name2"), of("value3"));
        Assert.assertEquals("TestDefaultHeaders[name1: value1, name1: value2, name2: value3]", headers.toString());
        headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"));
        headers.add(of("name2"), of("value2"));
        headers.add(of("name3"), of("value3"));
        Assert.assertEquals("TestDefaultHeaders[name1: value1, name2: value2, name3: value3]", headers.toString());
        headers = DefaultHeadersTest.newInstance();
        headers.add(of("name1"), of("value1"));
        Assert.assertEquals("TestDefaultHeaders[name1: value1]", headers.toString());
        headers = DefaultHeadersTest.newInstance();
        Assert.assertEquals("TestDefaultHeaders[]", headers.toString());
    }

    @Test
    public void testNotThrowWhenConvertFails() {
        DefaultHeadersTest.TestDefaultHeaders headers = new DefaultHeadersTest.TestDefaultHeaders(new ValueConverter<CharSequence>() {
            @Override
            public CharSequence convertObject(Object value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertBoolean(boolean value) {
                throw new IllegalArgumentException();
            }

            @Override
            public boolean convertToBoolean(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertByte(byte value) {
                throw new IllegalArgumentException();
            }

            @Override
            public byte convertToByte(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertChar(char value) {
                throw new IllegalArgumentException();
            }

            @Override
            public char convertToChar(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertShort(short value) {
                throw new IllegalArgumentException();
            }

            @Override
            public short convertToShort(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertInt(int value) {
                throw new IllegalArgumentException();
            }

            @Override
            public int convertToInt(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertLong(long value) {
                throw new IllegalArgumentException();
            }

            @Override
            public long convertToLong(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertTimeMillis(long value) {
                throw new IllegalArgumentException();
            }

            @Override
            public long convertToTimeMillis(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertFloat(float value) {
                throw new IllegalArgumentException();
            }

            @Override
            public float convertToFloat(CharSequence value) {
                throw new IllegalArgumentException();
            }

            @Override
            public CharSequence convertDouble(double value) {
                throw new IllegalArgumentException();
            }

            @Override
            public double convertToDouble(CharSequence value) {
                throw new IllegalArgumentException();
            }
        });
        headers.set("name1", "");
        Assert.assertNull(headers.getInt("name1"));
        Assert.assertEquals(1, getInt("name1", 1));
        Assert.assertNull(headers.getBoolean(""));
        Assert.assertFalse(getBoolean("name1", false));
        Assert.assertNull(headers.getByte("name1"));
        Assert.assertEquals(1, getByte("name1", ((byte) (1))));
        Assert.assertNull(headers.getChar("name"));
        Assert.assertEquals('n', getChar("name1", 'n'));
        Assert.assertNull(headers.getDouble("name"));
        Assert.assertEquals(1, getDouble("name1", 1), 0);
        Assert.assertNull(headers.getFloat("name"));
        Assert.assertEquals(Float.MAX_VALUE, getFloat("name1", Float.MAX_VALUE), 0);
        Assert.assertNull(headers.getLong("name"));
        Assert.assertEquals(Long.MAX_VALUE, getLong("name1", Long.MAX_VALUE));
        Assert.assertNull(headers.getShort("name"));
        Assert.assertEquals(Short.MAX_VALUE, getShort("name1", Short.MAX_VALUE));
        Assert.assertNull(headers.getTimeMillis("name"));
        Assert.assertEquals(Long.MAX_VALUE, getTimeMillis("name1", Long.MAX_VALUE));
    }

    @Test
    public void testGetBooleanInvalidValue() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.set("name1", "invalid");
        headers.set("name2", new AsciiString("invalid"));
        set("name3", new StringBuilder("invalid"));
        Assert.assertFalse(getBoolean("name1", false));
        Assert.assertFalse(getBoolean("name2", false));
        Assert.assertFalse(getBoolean("name3", false));
    }

    @Test
    public void testGetBooleanFalseValue() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.set("name1", "false");
        headers.set("name2", new AsciiString("false"));
        set("name3", new StringBuilder("false"));
        Assert.assertFalse(getBoolean("name1", true));
        Assert.assertFalse(getBoolean("name2", true));
        Assert.assertFalse(getBoolean("name3", true));
    }

    @Test
    public void testGetBooleanTrueValue() {
        DefaultHeadersTest.TestDefaultHeaders headers = DefaultHeadersTest.newInstance();
        headers.set("name1", "true");
        headers.set("name2", new AsciiString("true"));
        set("name3", new StringBuilder("true"));
        Assert.assertTrue(getBoolean("name1", false));
        Assert.assertTrue(getBoolean("name2", false));
        Assert.assertTrue(getBoolean("name3", false));
    }
}

