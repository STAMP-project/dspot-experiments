/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.api.utils.text;


import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.DateUtils;

import static com.google.common.collect.ImmutableMap.of;


public class JsonWriterTest {
    private static final String EMPTY_STRING = "";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private StringWriter stringWriter = new StringWriter();

    private JsonWriter underTest = JsonWriter.of(stringWriter);

    @Test
    public void empty_object() {
        underTest.beginObject().endObject().close();
        expect("{}");
    }

    @Test
    public void empty_array() {
        underTest.beginArray().endArray().close();
        expect("[]");
    }

    @Test
    public void stop_while_streaming() {
        underTest.beginObject().name("foo").value("bar");
        // endObject() and close() are missing
        expect("{\"foo\":\"bar\"");
    }

    @Test
    public void objects_and_arrays() {
        underTest.beginObject().name("issues").beginArray().beginObject().prop("key", "ABC").endObject().beginObject().prop("key", "DEF").endObject().endArray().endObject().close();
        expect("{\"issues\":[{\"key\":\"ABC\"},{\"key\":\"DEF\"}]}");
    }

    @Test
    public void array_values() {
        underTest.beginArray().values(Arrays.asList("foo", "bar", "baz")).endArray().close();
        expect("[\"foo\",\"bar\",\"baz\"]");
    }

    @Test
    public void type_of_values() {
        Date date = DateUtils.parseDateTime("2010-05-18T15:50:45+0100");
        underTest.beginObject().prop("aBoolean", true).prop("aInt", 123).prop("aLong", 1000L).prop("aDouble", 3.14).prop("aNumber", new AtomicInteger(123456789)).prop("aString", "bar").propDate("aDate", date).endObject().close();
        expect("{\"aBoolean\":true,\"aInt\":123,\"aLong\":1000,\"aDouble\":3.14,\"aNumber\":123456789,\"aString\":\"bar\",\"aDate\":\"2010-05-18\"}");
    }

    @Test
    public void ignore_null_values_by_default() {
        underTest.beginObject().prop("nullNumber", ((Number) (null))).prop("nullString", ((String) (null))).name("nullNumber").value(((Number) (null))).name("nullString").value(((String) (null))).name("nullDate").valueDate(null).name("nullDateTime").valueDate(null).endObject().close();
        expect("{}");
    }

    @Test
    public void serialize_null_values() {
        underTest.setSerializeNulls(true);
        underTest.beginObject().prop("nullNumber", ((Number) (null))).prop("nullString", ((String) (null))).name("nullNumber").value(((Number) (null))).name("nullString").value(((String) (null))).name("nullDate").valueDate(null).name("nullDateTime").valueDate(null).endObject().close();
        expect("{\"nullNumber\":null,\"nullString\":null,\"nullNumber\":null,\"nullString\":null,\"nullDate\":null,\"nullDateTime\":null}");
    }

    @Test
    public void serialize_empty_strings_by_default() {
        underTest.beginObject().prop("emptyString", JsonWriterTest.EMPTY_STRING).name("emptyStringAsObject").valueObject(JsonWriterTest.EMPTY_STRING).endObject().close();
        expect(("{" + (("\"emptyString\":\"\"," + "\"emptyStringAsObject\":\"\"") + "}")));
    }

    @Test
    public void ignore_empty_strings_when_requested() {
        underTest.setSerializeEmptys(false).beginObject().prop("emptyString", JsonWriterTest.EMPTY_STRING).name("emptyStringAsObject").valueObject(JsonWriterTest.EMPTY_STRING).endObject().close();
        expect("{}");
    }

    @Test
    public void escape_values() {
        underTest.beginObject().prop("foo", "<hello \"world\">").endObject().close();
        expect("{\"foo\":\"<hello \\\"world\\\">\"}");
    }

    @Test
    public void valueObject() {
        underTest.beginObject().name("aString").valueObject("stringValue").name("aBoolean").valueObject(true).name("aInt").valueObject(42).name("aFloat").valueObject(3.14).name("aLong").valueObject(42L).name("aList").valueObject(Arrays.asList("one", 2, "three")).name("anEnum").valueObject(JsonWriterTest.ColorEnum.GREEN).name("aMap").valueObject(of("hello", "world", "good", "bye")).endObject().close();
        expect("{\"aString\":\"stringValue\",\"aBoolean\":true,\"aInt\":42,\"aFloat\":3.14,\"aLong\":42,\"aList\":[\"one\",2,\"three\"],\"anEnum\":\"GREEN\",\"aMap\":{\"hello\":\"world\",\"good\":\"bye\"}}");
    }

    @Test
    public void valueObject_recursive() {
        Map map = ImmutableMap.of("a", ImmutableMap.of("b", "c"));
        underTest.valueObject(map).close();
        expect("{\"a\":{\"b\":\"c\"}}");
    }

    @Test
    public void valueObject_unsupported_type() {
        try {
            underTest.beginObject().valueObject(new StringWriter()).endObject().close();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("class org.sonar.api.utils.text.JsonWriter does not support encoding of type: class java.io.StringWriter");
        }
    }

    @Test
    public void fail_on_NaN_value() {
        expectedException.expect(WriterException.class);
        underTest.beginObject().prop("foo", Double.NaN).endObject().close();
    }

    @Test
    public void fail_if_not_valid() {
        expectedException.expect(WriterException.class);
        underTest.beginObject().endArray().close();
    }

    @Test
    public void fail_to_begin_array() throws Exception {
        com.google.gson.stream.JsonWriter gson = Mockito.mock(com.google.gson.stream.JsonWriter.class);
        Mockito.when(gson.beginArray()).thenThrow(new IOException("the reason"));
        expectedException.expect(WriterException.class);
        expectedException.expectMessage("Fail to write JSON");
        new JsonWriter(gson).beginArray();
    }

    private enum ColorEnum {

        RED,
        GREEN;}
}

