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


import Json.FALSE;
import Json.NULL;
import Json.TRUE;
import com.hazelcast.internal.json.JsonObject.HashIndexTable;
import com.hazelcast.internal.json.JsonObject.Member;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.StringReader;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static Json.FALSE;
import static Json.TRUE;


@Category(QuickTest.class)
public class JsonObject_Test {
    private JsonObject object;

    @Test
    public void copyConstructor_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "object is null", new Runnable() {
            public void run() {
                new JsonObject(null);
            }
        });
    }

    @Test
    public void copyConstructor_hasSameValues() {
        object.add("foo", 23);
        JsonObject copy = new JsonObject(object);
        Assert.assertEquals(object.names(), copy.names());
        Assert.assertSame(object.get("foo"), copy.get("foo"));
    }

    @Test
    public void copyConstructor_worksOnSafeCopy() {
        JsonObject copy = new JsonObject(object);
        object.add("foo", 23);
        Assert.assertTrue(copy.isEmpty());
    }

    @Test
    public void unmodifiableObject_hasSameValues() {
        object.add("foo", 23);
        JsonObject unmodifiableObject = JsonObject.unmodifiableObject(object);
        Assert.assertEquals(object.names(), unmodifiableObject.names());
        Assert.assertSame(object.get("foo"), unmodifiableObject.get("foo"));
    }

    @Test
    public void unmodifiableObject_reflectsChanges() {
        JsonObject unmodifiableObject = JsonObject.unmodifiableObject(object);
        object.add("foo", 23);
        Assert.assertEquals(object.names(), unmodifiableObject.names());
        Assert.assertSame(object.get("foo"), unmodifiableObject.get("foo"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unmodifiableObject_preventsModification() {
        JsonObject unmodifiableObject = JsonObject.unmodifiableObject(object);
        unmodifiableObject.add("foo", 23);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void readFrom_reader() throws IOException {
        Assert.assertEquals(new JsonObject(), JsonObject.readFrom(new StringReader("{}")));
        Assert.assertEquals(new JsonObject().add("a", 23), JsonObject.readFrom(new StringReader("{ \"a\": 23 }")));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void readFrom_string() {
        Assert.assertEquals(new JsonObject(), JsonObject.readFrom("{}"));
        Assert.assertEquals(new JsonObject().add("a", 23), JsonObject.readFrom("{ \"a\": 23 }"));
    }

    @Test(expected = ParseException.class)
    @SuppressWarnings("deprecation")
    public void readFrom_illegalJson() {
        JsonObject.readFrom("This is not JSON");
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("deprecation")
    public void readFrom_wrongJsonType() {
        JsonObject.readFrom("\"This is not a JSON object\"");
    }

    @Test
    public void isEmpty_trueAfterCreation() {
        Assert.assertTrue(object.isEmpty());
    }

    @Test
    public void isEmpty_falseAfterAdd() {
        object.add("a", true);
        Assert.assertFalse(object.isEmpty());
    }

    @Test
    public void size_zeroAfterCreation() {
        Assert.assertEquals(0, object.size());
    }

    @Test
    public void size_oneAfterAdd() {
        object.add("a", true);
        Assert.assertEquals(1, object.size());
    }

    @Test
    public void keyRepetition_allowsMultipleEntries() {
        object.add("a", true);
        object.add("a", "value");
        Assert.assertEquals(2, object.size());
    }

    @Test
    public void keyRepetition_getsLastEntry() {
        object.add("a", true);
        object.add("a", "value");
        Assert.assertEquals("value", object.getString("a", "missing"));
    }

    @Test
    public void keyRepetition_equalityConsidersRepetitions() {
        object.add("a", true);
        object.add("a", "value");
        JsonObject onlyFirstProperty = new JsonObject();
        onlyFirstProperty.add("a", true);
        Assert.assertNotEquals(onlyFirstProperty, object);
        JsonObject bothProperties = new JsonObject();
        bothProperties.add("a", true);
        bothProperties.add("a", "value");
        Assert.assertEquals(bothProperties, object);
    }

    @Test
    public void names_emptyAfterCreation() {
        Assert.assertTrue(object.names().isEmpty());
    }

    @Test
    public void names_containsNameAfterAdd() {
        object.add("foo", true);
        List<String> names = object.names();
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("foo", names.get(0));
    }

    @Test
    public void names_reflectsChanges() {
        List<String> names = object.names();
        object.add("foo", true);
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("foo", names.get(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void names_preventsModification() {
        List<String> names = object.names();
        names.add("foo");
    }

    @Test
    public void iterator_isEmptyAfterCreation() {
        Assert.assertFalse(object.iterator().hasNext());
    }

    @Test
    public void iterator_hasNextAfterAdd() {
        object.add("a", true);
        Iterator<Member> iterator = object.iterator();
        Assert.assertTrue(iterator.hasNext());
    }

    @Test
    public void iterator_nextReturnsActualValue() {
        object.add("a", true);
        Iterator<Member> iterator = object.iterator();
        Assert.assertEquals(new Member("a", TRUE), iterator.next());
    }

    @Test
    public void iterator_nextProgressesToNextValue() {
        object.add("a", true);
        object.add("b", false);
        Iterator<Member> iterator = object.iterator();
        iterator.next();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(new Member("b", FALSE), iterator.next());
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator_nextFailsAtEnd() {
        Iterator<Member> iterator = object.iterator();
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iterator_doesNotAllowModification() {
        object.add("a", 23);
        Iterator<Member> iterator = object.iterator();
        iterator.next();
        iterator.remove();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void iterator_detectsConcurrentModification() {
        Iterator<Member> iterator = object.iterator();
        object.add("a", 23);
        iterator.next();
    }

    @Test
    public void get_failsWithNullName() {
        TestUtil.assertException(NullPointerException.class, "name is null", new Runnable() {
            public void run() {
                object.get(null);
            }
        });
    }

    @Test
    public void get_returnsNullForNonExistingMember() {
        Assert.assertNull(object.get("foo"));
    }

    @Test
    public void get_returnsValueForName() {
        object.add("foo", true);
        Assert.assertEquals(TRUE, object.get("foo"));
    }

    @Test
    public void get_returnsLastValueForName() {
        object.add("foo", false).add("foo", true);
        Assert.assertEquals(TRUE, object.get("foo"));
    }

    @Test
    public void get_int_returnsValueFromMember() {
        object.add("foo", 23);
        Assert.assertEquals(23, object.getInt("foo", 42));
    }

    @Test
    public void get_int_returnsDefaultForMissingMember() {
        Assert.assertEquals(23, object.getInt("foo", 23));
    }

    @Test
    public void get_long_returnsValueFromMember() {
        object.add("foo", 23L);
        Assert.assertEquals(23L, object.getLong("foo", 42L));
    }

    @Test
    public void get_long_returnsDefaultForMissingMember() {
        Assert.assertEquals(23L, object.getLong("foo", 23L));
    }

    @Test
    public void get_float_returnsValueFromMember() {
        object.add("foo", 3.14F);
        Assert.assertEquals(3.14F, object.getFloat("foo", 1.41F), 0);
    }

    @Test
    public void get_float_returnsDefaultForMissingMember() {
        Assert.assertEquals(3.14F, object.getFloat("foo", 3.14F), 0);
    }

    @Test
    public void get_double_returnsValueFromMember() {
        object.add("foo", 3.14);
        Assert.assertEquals(3.14, object.getDouble("foo", 1.41), 0);
    }

    @Test
    public void get_double_returnsDefaultForMissingMember() {
        Assert.assertEquals(3.14, object.getDouble("foo", 3.14), 0);
    }

    @Test
    public void get_boolean_returnsValueFromMember() {
        object.add("foo", true);
        Assert.assertTrue(object.getBoolean("foo", false));
    }

    @Test
    public void get_boolean_returnsDefaultForMissingMember() {
        Assert.assertFalse(object.getBoolean("foo", false));
    }

    @Test
    public void get_string_returnsValueFromMember() {
        object.add("foo", "bar");
        Assert.assertEquals("bar", object.getString("foo", "default"));
    }

    @Test
    public void get_string_returnsDefaultForMissingMember() {
        Assert.assertEquals("default", object.getString("foo", "default"));
    }

    @Test
    public void add_failsWithNullName() {
        TestUtil.assertException(NullPointerException.class, "name is null", new Runnable() {
            public void run() {
                object.add(null, 23);
            }
        });
    }

    @Test
    public void add_int() {
        object.add("a", 23);
        Assert.assertEquals("{\"a\":23}", object.toString());
    }

    @Test
    public void add_int_enablesChaining() {
        Assert.assertSame(object, object.add("a", 23));
    }

    @Test
    public void add_long() {
        object.add("a", 23L);
        Assert.assertEquals("{\"a\":23}", object.toString());
    }

    @Test
    public void add_long_enablesChaining() {
        Assert.assertSame(object, object.add("a", 23L));
    }

    @Test
    public void add_float() {
        object.add("a", 3.14F);
        Assert.assertEquals("{\"a\":3.14}", object.toString());
    }

    @Test
    public void add_float_enablesChaining() {
        Assert.assertSame(object, object.add("a", 3.14F));
    }

    @Test
    public void add_double() {
        object.add("a", 3.14);
        Assert.assertEquals("{\"a\":3.14}", object.toString());
    }

    @Test
    public void add_double_enablesChaining() {
        Assert.assertSame(object, object.add("a", 3.14));
    }

    @Test
    public void add_boolean() {
        object.add("a", true);
        Assert.assertEquals("{\"a\":true}", object.toString());
    }

    @Test
    public void add_boolean_enablesChaining() {
        Assert.assertSame(object, object.add("a", true));
    }

    @Test
    public void add_string() {
        object.add("a", "foo");
        Assert.assertEquals("{\"a\":\"foo\"}", object.toString());
    }

    @Test
    public void add_string_toleratesNull() {
        object.add("a", ((String) (null)));
        Assert.assertEquals("{\"a\":null}", object.toString());
    }

    @Test
    public void add_string_enablesChaining() {
        Assert.assertSame(object, object.add("a", "foo"));
    }

    @Test
    public void add_jsonNull() {
        object.add("a", NULL);
        Assert.assertEquals("{\"a\":null}", object.toString());
    }

    @Test
    public void add_jsonArray() {
        object.add("a", new JsonArray());
        Assert.assertEquals("{\"a\":[]}", object.toString());
    }

    @Test
    public void add_jsonObject() {
        object.add("a", new JsonObject());
        Assert.assertEquals("{\"a\":{}}", object.toString());
    }

    @Test
    public void add_json_enablesChaining() {
        Assert.assertSame(object, object.add("a", NULL));
    }

    @Test
    public void add_json_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "value is null", new Runnable() {
            public void run() {
                object.add("a", ((JsonValue) (null)));
            }
        });
    }

    @Test
    public void add_json_nestedArray() {
        JsonArray innerArray = new JsonArray();
        innerArray.add(23);
        object.add("a", innerArray);
        Assert.assertEquals("{\"a\":[23]}", object.toString());
    }

    @Test
    public void add_json_nestedArray_modifiedAfterAdd() {
        JsonArray innerArray = new JsonArray();
        object.add("a", innerArray);
        innerArray.add(23);
        Assert.assertEquals("{\"a\":[23]}", object.toString());
    }

    @Test
    public void add_json_nestedObject() {
        JsonObject innerObject = new JsonObject();
        innerObject.add("a", 23);
        object.add("a", innerObject);
        Assert.assertEquals("{\"a\":{\"a\":23}}", object.toString());
    }

    @Test
    public void add_json_nestedObject_modifiedAfterAdd() {
        JsonObject innerObject = new JsonObject();
        object.add("a", innerObject);
        innerObject.add("a", 23);
        Assert.assertEquals("{\"a\":{\"a\":23}}", object.toString());
    }

    @Test
    public void set_int() {
        object.set("a", 23);
        Assert.assertEquals("{\"a\":23}", object.toString());
    }

    @Test
    public void set_int_enablesChaining() {
        Assert.assertSame(object, object.set("a", 23));
    }

    @Test
    public void set_long() {
        object.set("a", 23L);
        Assert.assertEquals("{\"a\":23}", object.toString());
    }

    @Test
    public void set_long_enablesChaining() {
        Assert.assertSame(object, object.set("a", 23L));
    }

    @Test
    public void set_float() {
        object.set("a", 3.14F);
        Assert.assertEquals("{\"a\":3.14}", object.toString());
    }

    @Test
    public void set_float_enablesChaining() {
        Assert.assertSame(object, object.set("a", 3.14F));
    }

    @Test
    public void set_double() {
        object.set("a", 3.14);
        Assert.assertEquals("{\"a\":3.14}", object.toString());
    }

    @Test
    public void set_double_enablesChaining() {
        Assert.assertSame(object, object.set("a", 3.14));
    }

    @Test
    public void set_boolean() {
        object.set("a", true);
        Assert.assertEquals("{\"a\":true}", object.toString());
    }

    @Test
    public void set_boolean_enablesChaining() {
        Assert.assertSame(object, object.set("a", true));
    }

    @Test
    public void set_string() {
        object.set("a", "foo");
        Assert.assertEquals("{\"a\":\"foo\"}", object.toString());
    }

    @Test
    public void set_string_enablesChaining() {
        Assert.assertSame(object, object.set("a", "foo"));
    }

    @Test
    public void set_jsonNull() {
        object.set("a", NULL);
        Assert.assertEquals("{\"a\":null}", object.toString());
    }

    @Test
    public void set_jsonArray() {
        object.set("a", new JsonArray());
        Assert.assertEquals("{\"a\":[]}", object.toString());
    }

    @Test
    public void set_jsonObject() {
        object.set("a", new JsonObject());
        Assert.assertEquals("{\"a\":{}}", object.toString());
    }

    @Test
    public void set_json_enablesChaining() {
        Assert.assertSame(object, object.set("a", NULL));
    }

    @Test
    public void set_addsElementIfMissing() {
        object.set("a", TRUE);
        Assert.assertEquals("{\"a\":true}", object.toString());
    }

    @Test
    public void set_modifiesElementIfExisting() {
        object.add("a", TRUE);
        object.set("a", FALSE);
        Assert.assertEquals("{\"a\":false}", object.toString());
    }

    @Test
    public void set_modifiesLastElementIfMultipleExisting() {
        object.add("a", 1);
        object.add("a", 2);
        object.set("a", TRUE);
        Assert.assertEquals("{\"a\":1,\"a\":true}", object.toString());
    }

    @Test
    public void remove_failsWithNullName() {
        TestUtil.assertException(NullPointerException.class, "name is null", new Runnable() {
            public void run() {
                object.remove(null);
            }
        });
    }

    @Test
    public void remove_removesMatchingMember() {
        object.add("a", 23);
        object.remove("a");
        Assert.assertEquals("{}", object.toString());
    }

    @Test
    public void remove_removesOnlyMatchingMember() {
        object.add("a", 23);
        object.add("b", 42);
        object.add("c", true);
        object.remove("b");
        Assert.assertEquals("{\"a\":23,\"c\":true}", object.toString());
    }

    @Test
    public void remove_removesOnlyLastMatchingMember() {
        object.add("a", 23);
        object.add("a", 42);
        object.remove("a");
        Assert.assertEquals("{\"a\":23}", object.toString());
    }

    @Test
    public void remove_removesOnlyLastMatchingMember_afterRemove() {
        object.add("a", 23);
        object.remove("a");
        object.add("a", 42);
        object.add("a", 47);
        object.remove("a");
        Assert.assertEquals("{\"a\":42}", object.toString());
    }

    @Test
    public void remove_doesNotModifyObjectWithoutMatchingMember() {
        object.add("a", 23);
        object.remove("b");
        Assert.assertEquals("{\"a\":23}", object.toString());
    }

    @Test
    public void merge_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "object is null", new Runnable() {
            public void run() {
                object.merge(null);
            }
        });
    }

    @Test
    public void merge_appendsMembers() {
        object.add("a", 1).add("b", 1);
        object.merge(Json.object().add("c", 2).add("d", 2));
        Assert.assertEquals(Json.object().add("a", 1).add("b", 1).add("c", 2).add("d", 2), object);
    }

    @Test
    public void merge_replacesMembers() {
        object.add("a", 1).add("b", 1).add("c", 1);
        object.merge(Json.object().add("b", 2).add("d", 2));
        Assert.assertEquals(Json.object().add("a", 1).add("b", 2).add("c", 1).add("d", 2), object);
    }

    @Test
    public void write_empty() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        object.write(writer);
        InOrder inOrder = Mockito.inOrder(writer);
        inOrder.verify(writer).writeObjectOpen();
        inOrder.verify(writer).writeObjectClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void write_withSingleValue() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        object.add("a", 23);
        object.write(writer);
        InOrder inOrder = Mockito.inOrder(writer);
        inOrder.verify(writer).writeObjectOpen();
        inOrder.verify(writer).writeMemberName("a");
        inOrder.verify(writer).writeMemberSeparator();
        inOrder.verify(writer).writeNumber("23");
        inOrder.verify(writer).writeObjectClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void write_withMultipleValues() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        object.add("a", 23);
        object.add("b", 3.14F);
        object.add("c", "foo");
        object.add("d", true);
        object.add("e", ((String) (null)));
        object.write(writer);
        InOrder inOrder = Mockito.inOrder(writer);
        inOrder.verify(writer).writeObjectOpen();
        inOrder.verify(writer).writeMemberName("a");
        inOrder.verify(writer).writeMemberSeparator();
        inOrder.verify(writer).writeNumber("23");
        inOrder.verify(writer).writeObjectSeparator();
        inOrder.verify(writer).writeMemberName("b");
        inOrder.verify(writer).writeMemberSeparator();
        inOrder.verify(writer).writeNumber("3.14");
        inOrder.verify(writer).writeObjectSeparator();
        inOrder.verify(writer).writeMemberName("c");
        inOrder.verify(writer).writeMemberSeparator();
        inOrder.verify(writer).writeString("foo");
        inOrder.verify(writer).writeObjectSeparator();
        inOrder.verify(writer).writeMemberName("d");
        inOrder.verify(writer).writeMemberSeparator();
        inOrder.verify(writer).writeLiteral("true");
        inOrder.verify(writer).writeObjectSeparator();
        inOrder.verify(writer).writeMemberName("e");
        inOrder.verify(writer).writeMemberSeparator();
        inOrder.verify(writer).writeLiteral("null");
        inOrder.verify(writer).writeObjectClose();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void isObject() {
        Assert.assertTrue(object.isObject());
    }

    @Test
    public void asObject() {
        Assert.assertSame(object, object.asObject());
    }

    @Test
    public void equals_trueForSameInstance() {
        Assert.assertTrue(object.equals(object));
    }

    @Test
    public void equals_trueForEqualObjects() {
        Assert.assertTrue(JsonObject_Test.object().equals(JsonObject_Test.object()));
        Assert.assertTrue(JsonObject_Test.object("a", "1", "b", "2").equals(JsonObject_Test.object("a", "1", "b", "2")));
    }

    @Test
    public void equals_falseForDifferentObjects() {
        Assert.assertFalse(JsonObject_Test.object("a", "1").equals(JsonObject_Test.object("a", "2")));
        Assert.assertFalse(JsonObject_Test.object("a", "1").equals(JsonObject_Test.object("b", "1")));
        Assert.assertFalse(JsonObject_Test.object("a", "1", "b", "2").equals(JsonObject_Test.object("b", "2", "a", "1")));
    }

    @Test
    public void equals_falseForNull() {
        Assert.assertFalse(new JsonObject().equals(null));
    }

    @Test
    public void equals_falseForSubclass() {
        JsonObject jsonObject = new JsonObject();
        Assert.assertFalse(jsonObject.equals(new JsonObject(jsonObject) {}));
    }

    @Test
    public void hashCode_equalsForEqualObjects() {
        Assert.assertTrue(((JsonObject_Test.object().hashCode()) == (JsonObject_Test.object().hashCode())));
        Assert.assertTrue(((JsonObject_Test.object("a", "1").hashCode()) == (JsonObject_Test.object("a", "1").hashCode())));
    }

    @Test
    public void hashCode_differsForDifferentObjects() {
        Assert.assertFalse(((JsonObject_Test.object().hashCode()) == (JsonObject_Test.object("a", "1").hashCode())));
        Assert.assertFalse(((JsonObject_Test.object("a", "1").hashCode()) == (JsonObject_Test.object("a", "2").hashCode())));
        Assert.assertFalse(((JsonObject_Test.object("a", "1").hashCode()) == (JsonObject_Test.object("b", "1").hashCode())));
    }

    @Test
    public void indexOf_returnsNoIndexIfEmpty() {
        Assert.assertEquals((-1), object.indexOf("a"));
    }

    @Test
    public void indexOf_returnsIndexOfMember() {
        object.add("a", true);
        Assert.assertEquals(0, object.indexOf("a"));
    }

    @Test
    public void indexOf_returnsIndexOfLastMember() {
        object.add("a", true);
        object.add("a", true);
        Assert.assertEquals(1, object.indexOf("a"));
    }

    @Test
    public void indexOf_returnsIndexOfLastMember_afterRemove() {
        object.add("a", true);
        object.add("a", true);
        object.remove("a");
        Assert.assertEquals(0, object.indexOf("a"));
    }

    @Test
    public void indexOf_returnsUpdatedIndexAfterRemove() {
        // See issue #16
        object.add("a", true);
        object.add("b", true);
        object.remove("a");
        Assert.assertEquals(0, object.indexOf("b"));
    }

    @Test
    public void indexOf_returnsIndexOfLastMember_forBigObject() {
        object.add("a", true);
        // for indexes above 255, the hash index table does not return a value
        for (int i = 0; i < 256; i++) {
            object.add(("x-" + i), 0);
        }
        object.add("a", true);
        Assert.assertEquals(257, object.indexOf("a"));
    }

    @Test
    public void hashIndexTable_copyConstructor() {
        HashIndexTable original = new HashIndexTable();
        original.add("name", 23);
        HashIndexTable copy = new HashIndexTable(original);
        Assert.assertEquals(23, copy.get("name"));
    }

    @Test
    public void hashIndexTable_add() {
        HashIndexTable indexTable = new HashIndexTable();
        indexTable.add("name-0", 0);
        indexTable.add("name-1", 1);
        indexTable.add("name-fe", 254);
        indexTable.add("name-ff", 255);
        Assert.assertEquals(0, indexTable.get("name-0"));
        Assert.assertEquals(1, indexTable.get("name-1"));
        Assert.assertEquals(254, indexTable.get("name-fe"));
        Assert.assertEquals((-1), indexTable.get("name-ff"));
    }

    @Test
    public void hashIndexTable_add_overwritesPreviousValue() {
        HashIndexTable indexTable = new HashIndexTable();
        indexTable.add("name", 23);
        indexTable.add("name", 42);
        Assert.assertEquals(42, indexTable.get("name"));
    }

    @Test
    public void hashIndexTable_add_clearsPreviousValueIfIndexExceeds0xff() {
        HashIndexTable indexTable = new HashIndexTable();
        indexTable.add("name", 23);
        indexTable.add("name", 300);
        Assert.assertEquals((-1), indexTable.get("name"));
    }

    @Test
    public void hashIndexTable_remove() {
        HashIndexTable indexTable = new HashIndexTable();
        indexTable.add("name", 23);
        indexTable.remove(23);
        Assert.assertEquals((-1), indexTable.get("name"));
    }

    @Test
    public void hashIndexTable_remove_updatesSubsequentElements() {
        HashIndexTable indexTable = new HashIndexTable();
        indexTable.add("foo", 23);
        indexTable.add("bar", 42);
        indexTable.remove(23);
        Assert.assertEquals(41, indexTable.get("bar"));
    }

    @Test
    public void hashIndexTable_remove_doesNotChangePrecedingElements() {
        HashIndexTable indexTable = new HashIndexTable();
        indexTable.add("foo", 23);
        indexTable.add("bar", 42);
        indexTable.remove(42);
        Assert.assertEquals(23, indexTable.get("foo"));
    }

    @Test
    public void canBeSerializedAndDeserialized() throws Exception {
        object.add("foo", 23).add("bar", new JsonObject().add("a", 3.14).add("b", true));
        Assert.assertEquals(object, TestUtil.serializeAndDeserialize(object));
    }

    @Test
    public void deserializedObjectCanBeAccessed() throws Exception {
        object.add("foo", 23);
        JsonObject deserializedObject = TestUtil.serializeAndDeserialize(object);
        Assert.assertEquals(23, deserializedObject.get("foo").asInt());
    }

    @Test
    public void member_returnsNameAndValue() {
        Member member = new Member("a", TRUE);
        Assert.assertEquals("a", member.getName());
        Assert.assertEquals(TRUE, member.getValue());
    }

    @Test
    public void member_equals_trueForSameInstance() {
        Member member = new Member("a", TRUE);
        Assert.assertTrue(member.equals(member));
    }

    @Test
    public void member_equals_trueForEqualObjects() {
        Member member = new Member("a", TRUE);
        Assert.assertTrue(member.equals(new Member("a", TRUE)));
    }

    @Test
    public void member_equals_falseForDifferingObjects() {
        Member member = new Member("a", TRUE);
        Assert.assertFalse(member.equals(new Member("b", TRUE)));
        Assert.assertFalse(member.equals(new Member("a", FALSE)));
    }

    @Test
    public void member_equals_falseForNull() {
        Member member = new Member("a", TRUE);
        Assert.assertFalse(member.equals(null));
    }

    @Test
    public void member_equals_falseForSubclass() {
        Member member = new Member("a", TRUE);
        Assert.assertFalse(member.equals(new Member("a", TRUE) {}));
    }

    @Test
    public void member_hashCode_equalsForEqualObjects() {
        Member member = new Member("a", TRUE);
        Assert.assertTrue(((member.hashCode()) == (new Member("a", TRUE).hashCode())));
    }

    @Test
    public void member_hashCode_differsForDifferingobjects() {
        Member member = new Member("a", TRUE);
        Assert.assertFalse(((member.hashCode()) == (new Member("b", TRUE).hashCode())));
        Assert.assertFalse(((member.hashCode()) == (new Member("a", FALSE).hashCode())));
    }
}

