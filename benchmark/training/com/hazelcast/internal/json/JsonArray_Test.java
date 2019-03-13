/**
 * *****************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ****************************************************************************
 */
package com.hazelcast.internal.json;


import Json.NULL;
import Json.TRUE;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.StringReader;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InOrder;
import org.mockito.Mockito;


@Category(QuickTest.class)
public class JsonArray_Test {
    private JsonArray array;

    @Test
    public void copyConstructor_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "array is null", new Runnable() {
            public void run() {
                new JsonArray(null);
            }
        });
    }

    @Test
    public void copyConstructor_hasSameValues() {
        array.add(23);
        JsonArray copy = new JsonArray(array);
        Assert.assertEquals(array.values(), copy.values());
    }

    @Test
    public void copyConstructor_worksOnSafeCopy() {
        JsonArray copy = new JsonArray(array);
        array.add(23);
        Assert.assertTrue(copy.isEmpty());
    }

    @Test
    public void unmodifiableArray_hasSameValues() {
        array.add(23);
        JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);
        Assert.assertEquals(array.values(), unmodifiableArray.values());
    }

    @Test
    public void unmodifiableArray_reflectsChanges() {
        JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);
        array.add(23);
        Assert.assertEquals(array.values(), unmodifiableArray.values());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unmodifiableArray_preventsModification() {
        JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);
        unmodifiableArray.add(23);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void readFrom_reader() throws IOException {
        Assert.assertEquals(new JsonArray(), JsonArray.readFrom(new StringReader("[]")));
        Assert.assertEquals(new JsonArray().add("a").add(23), JsonArray.readFrom(new StringReader("[ \"a\", 23 ]")));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void readFrom_string() {
        Assert.assertEquals(new JsonArray(), JsonArray.readFrom("[]"));
        Assert.assertEquals(new JsonArray().add("a").add(23), JsonArray.readFrom("[ \"a\", 23 ]"));
    }

    @Test(expected = ParseException.class)
    @SuppressWarnings("deprecation")
    public void readFrom_illegalJson() {
        JsonArray.readFrom("This is not JSON");
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("deprecation")
    public void readFrom_wrongJsonType() {
        JsonArray.readFrom("\"This is not a JSON object\"");
    }

    @Test
    public void isEmpty_isTrueAfterCreation() {
        Assert.assertTrue(array.isEmpty());
    }

    @Test
    public void isEmpty_isFalseAfterAdd() {
        array.add(true);
        Assert.assertFalse(array.isEmpty());
    }

    @Test
    public void size_isZeroAfterCreation() {
        Assert.assertEquals(0, array.size());
    }

    @Test
    public void size_isOneAfterAdd() {
        array.add(true);
        Assert.assertEquals(1, array.size());
    }

    @Test
    public void iterator_isEmptyAfterCreation() {
        Assert.assertFalse(array.iterator().hasNext());
    }

    @Test
    public void iterator_hasNextAfterAdd() {
        array.add(true);
        Iterator<JsonValue> iterator = array.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(TRUE, iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iterator_doesNotAllowModification() {
        array.add(23);
        Iterator<JsonValue> iterator = array.iterator();
        iterator.next();
        iterator.remove();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void iterator_detectsConcurrentModification() {
        Iterator<JsonValue> iterator = array.iterator();
        array.add(23);
        iterator.next();
    }

    @Test
    public void values_isEmptyAfterCreation() {
        Assert.assertTrue(array.values().isEmpty());
    }

    @Test
    public void values_containsValueAfterAdd() {
        array.add(true);
        Assert.assertEquals(1, array.values().size());
        Assert.assertEquals(TRUE, array.values().get(0));
    }

    @Test
    public void values_reflectsChanges() {
        List<JsonValue> values = array.values();
        array.add(true);
        Assert.assertEquals(array.values(), values);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void values_preventsModification() {
        List<JsonValue> values = array.values();
        values.add(TRUE);
    }

    @Test
    public void get_returnsValue() {
        array.add(23);
        JsonValue value = array.get(0);
        Assert.assertEquals(Json.value(23), value);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_failsWithInvalidIndex() {
        array.get(0);
    }

    @Test
    public void add_int() {
        array.add(23);
        Assert.assertEquals("[23]", array.toString());
    }

    @Test
    public void add_int_enablesChaining() {
        Assert.assertSame(array, array.add(23));
    }

    @Test
    public void add_long() {
        array.add(23L);
        Assert.assertEquals("[23]", array.toString());
    }

    @Test
    public void add_long_enablesChaining() {
        Assert.assertSame(array, array.add(23L));
    }

    @Test
    public void add_float() {
        array.add(3.14F);
        Assert.assertEquals("[3.14]", array.toString());
    }

    @Test
    public void add_float_enablesChaining() {
        Assert.assertSame(array, array.add(3.14F));
    }

    @Test
    public void add_double() {
        array.add(3.14);
        Assert.assertEquals("[3.14]", array.toString());
    }

    @Test
    public void add_double_enablesChaining() {
        Assert.assertSame(array, array.add(3.14));
    }

    @Test
    public void add_boolean() {
        array.add(true);
        Assert.assertEquals("[true]", array.toString());
    }

    @Test
    public void add_boolean_enablesChaining() {
        Assert.assertSame(array, array.add(true));
    }

    @Test
    public void add_string() {
        array.add("foo");
        Assert.assertEquals("[\"foo\"]", array.toString());
    }

    @Test
    public void add_string_enablesChaining() {
        Assert.assertSame(array, array.add("foo"));
    }

    @Test
    public void add_string_toleratesNull() {
        array.add(((String) (null)));
        Assert.assertEquals("[null]", array.toString());
    }

    @Test
    public void add_jsonNull() {
        array.add(NULL);
        Assert.assertEquals("[null]", array.toString());
    }

    @Test
    public void add_jsonArray() {
        array.add(new JsonArray());
        Assert.assertEquals("[[]]", array.toString());
    }

    @Test
    public void add_jsonObject() {
        array.add(new JsonObject());
        Assert.assertEquals("[{}]", array.toString());
    }

    @Test
    public void add_json_enablesChaining() {
        Assert.assertSame(array, array.add(NULL));
    }

    @Test
    public void add_json_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "value is null", new Runnable() {
            public void run() {
                array.add(((JsonValue) (null)));
            }
        });
    }

    @Test
    public void add_json_nestedArray() {
        JsonArray innerArray = new JsonArray();
        innerArray.add(23);
        array.add(innerArray);
        Assert.assertEquals("[[23]]", array.toString());
    }

    @Test
    public void add_json_nestedArray_modifiedAfterAdd() {
        JsonArray innerArray = new JsonArray();
        array.add(innerArray);
        innerArray.add(23);
        Assert.assertEquals("[[23]]", array.toString());
    }

    @Test
    public void add_json_nestedObject() {
        JsonObject innerObject = new JsonObject();
        innerObject.add("a", 23);
        array.add(innerObject);
        Assert.assertEquals("[{\"a\":23}]", array.toString());
    }

    @Test
    public void add_json_nestedObject_modifiedAfterAdd() {
        JsonObject innerObject = new JsonObject();
        array.add(innerObject);
        innerObject.add("a", 23);
        Assert.assertEquals("[{\"a\":23}]", array.toString());
    }

    @Test
    public void set_int() {
        array.add(false);
        array.set(0, 23);
        Assert.assertEquals("[23]", array.toString());
    }

    @Test
    public void set_int_enablesChaining() {
        array.add(false);
        Assert.assertSame(array, array.set(0, 23));
    }

    @Test
    public void set_long() {
        array.add(false);
        array.set(0, 23L);
        Assert.assertEquals("[23]", array.toString());
    }

    @Test
    public void set_long_enablesChaining() {
        array.add(false);
        Assert.assertSame(array, array.set(0, 23L));
    }

    @Test
    public void set_float() {
        array.add(false);
        array.set(0, 3.14F);
        Assert.assertEquals("[3.14]", array.toString());
    }

    @Test
    public void set_float_enablesChaining() {
        array.add(false);
        Assert.assertSame(array, array.set(0, 3.14F));
    }

    @Test
    public void set_double() {
        array.add(false);
        array.set(0, 3.14);
        Assert.assertEquals("[3.14]", array.toString());
    }

    @Test
    public void set_double_enablesChaining() {
        array.add(false);
        Assert.assertSame(array, array.set(0, 3.14));
    }

    @Test
    public void set_boolean() {
        array.add(false);
        array.set(0, true);
        Assert.assertEquals("[true]", array.toString());
    }

    @Test
    public void set_boolean_enablesChaining() {
        array.add(false);
        Assert.assertSame(array, array.set(0, true));
    }

    @Test
    public void set_string() {
        array.add(false);
        array.set(0, "foo");
        Assert.assertEquals("[\"foo\"]", array.toString());
    }

    @Test
    public void set_string_enablesChaining() {
        array.add(false);
        Assert.assertSame(array, array.set(0, "foo"));
    }

    @Test
    public void set_jsonNull() {
        array.add(false);
        array.set(0, NULL);
        Assert.assertEquals("[null]", array.toString());
    }

    @Test
    public void set_jsonArray() {
        array.add(false);
        array.set(0, new JsonArray());
        Assert.assertEquals("[[]]", array.toString());
    }

    @Test
    public void set_jsonObject() {
        array.add(false);
        array.set(0, new JsonObject());
        Assert.assertEquals("[{}]", array.toString());
    }

    @Test
    public void set_json_failsWithNull() {
        array.add(false);
        TestUtil.assertException(NullPointerException.class, "value is null", new Runnable() {
            public void run() {
                array.set(0, ((JsonValue) (null)));
            }
        });
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void set_json_failsWithInvalidIndex() {
        array.set(0, NULL);
    }

    @Test
    public void set_json_enablesChaining() {
        array.add(false);
        Assert.assertSame(array, array.set(0, NULL));
    }

    @Test
    public void set_json_replacesDifferntArrayElements() {
        array.add(3).add(6).add(9);
        array.set(1, 4).set(2, 5);
        Assert.assertEquals("[3,4,5]", array.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void remove_failsWithInvalidIndex() {
        array.remove(0);
    }

    @Test
    public void remove_removesElement() {
        array.add(23);
        array.remove(0);
        Assert.assertEquals("[]", array.toString());
    }

    @Test
    public void remove_keepsOtherElements() {
        array.add("a").add("b").add("c");
        array.remove(1);
        Assert.assertEquals("[\"a\",\"c\"]", array.toString());
    }

    @Test
    public void write_empty() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        array.write(writer);
        InOrder inOrder = Mockito.inOrder(writer);
        inOrder.verify(writer).writeArrayOpen();
        inOrder.verify(writer).writeArrayClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void write_withSingleValue() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        array.add(23);
        array.write(writer);
        InOrder inOrder = Mockito.inOrder(writer);
        inOrder.verify(writer).writeArrayOpen();
        inOrder.verify(writer).writeNumber("23");
        inOrder.verify(writer).writeArrayClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void write_withMultipleValues() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        array.add(23).add("foo").add(false);
        array.write(writer);
        InOrder inOrder = Mockito.inOrder(writer);
        inOrder.verify(writer).writeArrayOpen();
        inOrder.verify(writer).writeNumber("23");
        inOrder.verify(writer).writeArraySeparator();
        inOrder.verify(writer).writeString("foo");
        inOrder.verify(writer).writeArraySeparator();
        inOrder.verify(writer).writeLiteral("false");
        inOrder.verify(writer).writeArrayClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void isArray() {
        Assert.assertTrue(array.isArray());
    }

    @Test
    public void asArray() {
        Assert.assertSame(array, array.asArray());
    }

    @Test
    public void equals_trueForSameInstance() {
        Assert.assertTrue(array.equals(array));
    }

    @Test
    public void equals_trueForEqualArrays() {
        Assert.assertTrue(JsonArray_Test.array().equals(JsonArray_Test.array()));
        Assert.assertTrue(JsonArray_Test.array("foo", "bar").equals(JsonArray_Test.array("foo", "bar")));
    }

    @Test
    public void equals_falseForDifferentArrays() {
        Assert.assertFalse(JsonArray_Test.array("foo", "bar").equals(JsonArray_Test.array("foo", "bar", "baz")));
        Assert.assertFalse(JsonArray_Test.array("foo", "bar").equals(JsonArray_Test.array("bar", "foo")));
    }

    @Test
    public void equals_falseForNull() {
        Assert.assertFalse(array.equals(null));
    }

    @Test
    public void equals_falseForSubclass() {
        Assert.assertFalse(array.equals(new JsonArray(array) {}));
    }

    @Test
    public void hashCode_equalsForEqualArrays() {
        Assert.assertTrue(((JsonArray_Test.array().hashCode()) == (JsonArray_Test.array().hashCode())));
        Assert.assertTrue(((JsonArray_Test.array("foo").hashCode()) == (JsonArray_Test.array("foo").hashCode())));
    }

    @Test
    public void hashCode_differsForDifferentArrays() {
        Assert.assertFalse(((JsonArray_Test.array().hashCode()) == (JsonArray_Test.array("bar").hashCode())));
        Assert.assertFalse(((JsonArray_Test.array("foo").hashCode()) == (JsonArray_Test.array("bar").hashCode())));
    }

    @Test
    public void canBeSerializedAndDeserialized() throws Exception {
        array.add(true).add(3.14).add(23).add("foo").add(new JsonArray().add(false));
        Assert.assertEquals(array, TestUtil.serializeAndDeserialize(array));
    }

    @Test
    public void deserializedArrayCanBeAccessed() throws Exception {
        array.add(23);
        JsonArray deserializedArray = TestUtil.serializeAndDeserialize(array);
        Assert.assertEquals(23, deserializedArray.get(0).asInt());
    }
}

