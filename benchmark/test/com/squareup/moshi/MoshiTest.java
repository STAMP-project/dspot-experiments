/**
 * Copyright (C) 2014 Square, Inc.
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
package com.squareup.moshi;


import JsonAdapter.Factory;
import JsonReader.Token.END_DOCUMENT;
import Moshi.Builder;
import StandardJsonAdapters.BOOLEAN_JSON_ADAPTER;
import Util.NO_ANNOTATIONS;
import android.util.Pair;
import com.squareup.moshi.internal.Util;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.crypto.KeyGenerator;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Test;

import static Moshi.BUILT_IN_FACTORIES;


@SuppressWarnings("CheckReturnValue")
public final class MoshiTest {
    @Test
    public void booleanAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Boolean> adapter = moshi.adapter(boolean.class).lenient();
        assertThat(adapter.fromJson("true")).isTrue();
        assertThat(adapter.fromJson("TRUE")).isTrue();
        assertThat(adapter.toJson(true)).isEqualTo("true");
        assertThat(adapter.fromJson("false")).isFalse();
        assertThat(adapter.fromJson("FALSE")).isFalse();
        assertThat(adapter.toJson(false)).isEqualTo("false");
        // Nulls not allowed for boolean.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a boolean but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void BooleanAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Boolean> adapter = moshi.adapter(Boolean.class).lenient();
        assertThat(adapter.fromJson("true")).isTrue();
        assertThat(adapter.toJson(true)).isEqualTo("true");
        assertThat(adapter.fromJson("false")).isFalse();
        assertThat(adapter.toJson(false)).isEqualTo("false");
        // Allow nulls for Boolean.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void byteAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Byte> adapter = moshi.adapter(byte.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(((byte) (1)));
        assertThat(adapter.toJson(((byte) (-2)))).isEqualTo("254");
        // Canonical byte representation is unsigned, but parse the whole range -128..255
        assertThat(adapter.fromJson("-128")).isEqualTo(((byte) (-128)));
        assertThat(adapter.fromJson("128")).isEqualTo(((byte) (-128)));
        assertThat(adapter.toJson(((byte) (-128)))).isEqualTo("128");
        assertThat(adapter.fromJson("255")).isEqualTo(((byte) (-1)));
        assertThat(adapter.toJson(((byte) (-1)))).isEqualTo("255");
        assertThat(adapter.fromJson("127")).isEqualTo(((byte) (127)));
        assertThat(adapter.toJson(((byte) (127)))).isEqualTo("127");
        try {
            adapter.fromJson("256");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a byte but was 256 at path $");
        }
        try {
            adapter.fromJson("-129");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a byte but was -129 at path $");
        }
        // Nulls not allowed for byte.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected an int but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void ByteAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Byte> adapter = moshi.adapter(Byte.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(((byte) (1)));
        assertThat(adapter.toJson(((byte) (-2)))).isEqualTo("254");
        // Allow nulls for Byte.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void charAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Character> adapter = moshi.adapter(char.class).lenient();
        assertThat(adapter.fromJson("\"a\"")).isEqualTo('a');
        assertThat(adapter.fromJson("'a'")).isEqualTo('a');
        assertThat(adapter.toJson('b')).isEqualTo("\"b\"");
        // Exhaustively test all valid characters.  Use an int to loop so we can check termination.
        for (int i = 0; i <= (Character.MAX_VALUE); ++i) {
            final char c = ((char) (i));
            String s;
            switch (c) {
                // TODO: make JsonWriter.REPLACEMENT_CHARS visible for testing?
                case '\"' :
                    s = "\\\"";
                    break;
                case '\\' :
                    s = "\\\\";
                    break;
                case '\t' :
                    s = "\\t";
                    break;
                case '\b' :
                    s = "\\b";
                    break;
                case '\n' :
                    s = "\\n";
                    break;
                case '\r' :
                    s = "\\r";
                    break;
                case '\f' :
                    s = "\\f";
                    break;
                case '\u2028' :
                    s = "\\u2028";
                    break;
                case '\u2029' :
                    s = "\\u2029";
                    break;
                default :
                    if (c <= 31) {
                        s = String.format("\\u%04x", ((int) (c)));
                    } else
                        if ((c >= (Character.MIN_SURROGATE)) && (c <= (Character.MAX_SURROGATE))) {
                            // TODO: not handled properly; do we need to?
                            continue;
                        } else {
                            s = String.valueOf(c);
                        }

                    break;
            }
            s = ('"' + s) + '"';
            assertThat(adapter.toJson(c)).isEqualTo(s);
            assertThat(adapter.fromJson(s)).isEqualTo(c);
        }
        try {
            // Only a single character is allowed.
            adapter.fromJson("'ab'");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a char but was \"ab\" at path $");
        }
        // Nulls not allowed for char.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a string but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void CharacterAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Character> adapter = moshi.adapter(Character.class).lenient();
        assertThat(adapter.fromJson("\"a\"")).isEqualTo('a');
        assertThat(adapter.fromJson("'a'")).isEqualTo('a');
        assertThat(adapter.toJson('b')).isEqualTo("\"b\"");
        try {
            // Only a single character is allowed.
            adapter.fromJson("'ab'");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a char but was \"ab\" at path $");
        }
        // Allow nulls for Character.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void doubleAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Double> adapter = moshi.adapter(double.class).lenient();
        assertThat(adapter.fromJson("1.0")).isEqualTo(1.0);
        assertThat(adapter.fromJson("1")).isEqualTo(1.0);
        assertThat(adapter.fromJson("1e0")).isEqualTo(1.0);
        assertThat(adapter.toJson((-2.0))).isEqualTo("-2.0");
        // Test min/max values.
        assertThat(adapter.fromJson("-1.7976931348623157E308")).isEqualTo((-(Double.MAX_VALUE)));
        assertThat(adapter.toJson((-(Double.MAX_VALUE)))).isEqualTo("-1.7976931348623157E308");
        assertThat(adapter.fromJson("1.7976931348623157E308")).isEqualTo(Double.MAX_VALUE);
        assertThat(adapter.toJson(Double.MAX_VALUE)).isEqualTo("1.7976931348623157E308");
        // Lenient reader converts too large values to infinities.
        assertThat(adapter.fromJson("1E309")).isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(adapter.fromJson("-1E309")).isEqualTo(Double.NEGATIVE_INFINITY);
        assertThat(adapter.fromJson("+Infinity")).isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(adapter.fromJson("Infinity")).isEqualTo(Double.POSITIVE_INFINITY);
        assertThat(adapter.fromJson("-Infinity")).isEqualTo(Double.NEGATIVE_INFINITY);
        // Nulls not allowed for double.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a double but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        // Non-lenient adapter won't allow values outside of range.
        adapter = moshi.adapter(double.class);
        JsonReader reader = TestUtil.newReader("[1E309]");
        reader.beginArray();
        try {
            adapter.fromJson(reader);
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected).hasMessage("JSON forbids NaN and infinities: Infinity at path $[0]");
        }
        reader = TestUtil.newReader("[-1E309]");
        reader.beginArray();
        try {
            adapter.fromJson(reader);
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected).hasMessage("JSON forbids NaN and infinities: -Infinity at path $[0]");
        }
    }

    @Test
    public void DoubleAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Double> adapter = moshi.adapter(Double.class).lenient();
        assertThat(adapter.fromJson("1.0")).isEqualTo(1.0);
        assertThat(adapter.fromJson("1")).isEqualTo(1.0);
        assertThat(adapter.fromJson("1e0")).isEqualTo(1.0);
        assertThat(adapter.toJson((-2.0))).isEqualTo("-2.0");
        // Allow nulls for Double.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void floatAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Float> adapter = moshi.adapter(float.class).lenient();
        assertThat(adapter.fromJson("1.0")).isEqualTo(1.0F);
        assertThat(adapter.fromJson("1")).isEqualTo(1.0F);
        assertThat(adapter.fromJson("1e0")).isEqualTo(1.0F);
        assertThat(adapter.toJson((-2.0F))).isEqualTo("-2.0");
        // Test min/max values.
        assertThat(adapter.fromJson("-3.4028235E38")).isEqualTo((-(Float.MAX_VALUE)));
        assertThat(adapter.toJson((-(Float.MAX_VALUE)))).isEqualTo("-3.4028235E38");
        assertThat(adapter.fromJson("3.4028235E38")).isEqualTo(Float.MAX_VALUE);
        assertThat(adapter.toJson(Float.MAX_VALUE)).isEqualTo("3.4028235E38");
        // Lenient reader converts too large values to infinities.
        assertThat(adapter.fromJson("1E39")).isEqualTo(Float.POSITIVE_INFINITY);
        assertThat(adapter.fromJson("-1E39")).isEqualTo(Float.NEGATIVE_INFINITY);
        assertThat(adapter.fromJson("+Infinity")).isEqualTo(Float.POSITIVE_INFINITY);
        assertThat(adapter.fromJson("Infinity")).isEqualTo(Float.POSITIVE_INFINITY);
        assertThat(adapter.fromJson("-Infinity")).isEqualTo(Float.NEGATIVE_INFINITY);
        // Nulls not allowed for float.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a double but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        // Non-lenient adapter won't allow values outside of range.
        adapter = moshi.adapter(float.class);
        JsonReader reader = TestUtil.newReader("[1E39]");
        reader.beginArray();
        try {
            adapter.fromJson(reader);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("JSON forbids NaN and infinities: Infinity at path $[1]");
        }
        reader = TestUtil.newReader("[-1E39]");
        reader.beginArray();
        try {
            adapter.fromJson(reader);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("JSON forbids NaN and infinities: -Infinity at path $[1]");
        }
    }

    @Test
    public void FloatAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Float> adapter = moshi.adapter(Float.class).lenient();
        assertThat(adapter.fromJson("1.0")).isEqualTo(1.0F);
        assertThat(adapter.fromJson("1")).isEqualTo(1.0F);
        assertThat(adapter.fromJson("1e0")).isEqualTo(1.0F);
        assertThat(adapter.toJson((-2.0F))).isEqualTo("-2.0");
        // Allow nulls for Float.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void intAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Integer> adapter = moshi.adapter(int.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(1);
        assertThat(adapter.toJson((-2))).isEqualTo("-2");
        // Test min/max values
        assertThat(adapter.fromJson("-2147483648")).isEqualTo(Integer.MIN_VALUE);
        assertThat(adapter.toJson(Integer.MIN_VALUE)).isEqualTo("-2147483648");
        assertThat(adapter.fromJson("2147483647")).isEqualTo(Integer.MAX_VALUE);
        assertThat(adapter.toJson(Integer.MAX_VALUE)).isEqualTo("2147483647");
        try {
            adapter.fromJson("2147483648");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected an int but was 2147483648 at path $");
        }
        try {
            adapter.fromJson("-2147483649");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected an int but was -2147483649 at path $");
        }
        // Nulls not allowed for int.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected an int but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void IntegerAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Integer> adapter = moshi.adapter(Integer.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(1);
        assertThat(adapter.toJson((-2))).isEqualTo("-2");
        // Allow nulls for Integer.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void longAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Long> adapter = moshi.adapter(long.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(1L);
        assertThat(adapter.toJson((-2L))).isEqualTo("-2");
        // Test min/max values
        assertThat(adapter.fromJson("-9223372036854775808")).isEqualTo(Long.MIN_VALUE);
        assertThat(adapter.toJson(Long.MIN_VALUE)).isEqualTo("-9223372036854775808");
        assertThat(adapter.fromJson("9223372036854775807")).isEqualTo(Long.MAX_VALUE);
        assertThat(adapter.toJson(Long.MAX_VALUE)).isEqualTo("9223372036854775807");
        try {
            adapter.fromJson("9223372036854775808");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a long but was 9223372036854775808 at path $");
        }
        try {
            adapter.fromJson("-9223372036854775809");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a long but was -9223372036854775809 at path $");
        }
        // Nulls not allowed for long.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a long but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void LongAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Long> adapter = moshi.adapter(Long.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(1L);
        assertThat(adapter.toJson((-2L))).isEqualTo("-2");
        // Allow nulls for Integer.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void shortAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Short> adapter = moshi.adapter(short.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(((short) (1)));
        assertThat(adapter.toJson(((short) (-2)))).isEqualTo("-2");
        // Test min/max values.
        assertThat(adapter.fromJson("-32768")).isEqualTo(Short.MIN_VALUE);
        assertThat(adapter.toJson(Short.MIN_VALUE)).isEqualTo("-32768");
        assertThat(adapter.fromJson("32767")).isEqualTo(Short.MAX_VALUE);
        assertThat(adapter.toJson(Short.MAX_VALUE)).isEqualTo("32767");
        try {
            adapter.fromJson("32768");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a short but was 32768 at path $");
        }
        try {
            adapter.fromJson("-32769");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected a short but was -32769 at path $");
        }
        // Nulls not allowed for short.class
        try {
            adapter.fromJson("null");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected an int but was NULL at path $");
        }
        try {
            adapter.toJson(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void ShortAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Short> adapter = moshi.adapter(Short.class).lenient();
        assertThat(adapter.fromJson("1")).isEqualTo(((short) (1)));
        assertThat(adapter.toJson(((short) (-2)))).isEqualTo("-2");
        // Allow nulls for Byte.class
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void stringAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<String> adapter = moshi.adapter(String.class).lenient();
        assertThat(adapter.fromJson("\"a\"")).isEqualTo("a");
        assertThat(adapter.toJson("b")).isEqualTo("\"b\"");
        assertThat(adapter.fromJson("null")).isEqualTo(null);
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void upperBoundedWildcardsAreNotHandled() {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(Types.subtypeOf(String.class));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("No JsonAdapter for ? extends java.lang.String (with no annotations)");
        }
    }

    @Test
    public void lowerBoundedWildcardsAreNotHandled() {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(Types.supertypeOf(String.class));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("No JsonAdapter for ? super java.lang.String (with no annotations)");
        }
    }

    @Test
    public void addNullFails() throws Exception {
        Type type = Object.class;
        Class<? extends Annotation> annotation = Annotation.class;
        Moshi.Builder builder = new Moshi.Builder();
        try {
            builder.add(null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("factory == null");
        }
        try {
            builder.add(((Object) (null)));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("adapter == null");
        }
        try {
            builder.add(null, null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("type == null");
        }
        try {
            builder.add(type, null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("jsonAdapter == null");
        }
        try {
            builder.add(null, null, null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("type == null");
        }
        try {
            builder.add(type, null, null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("annotation == null");
        }
        try {
            builder.add(type, annotation, null);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("jsonAdapter == null");
        }
    }

    @Test
    public void customJsonAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().add(MoshiTest.Pizza.class, new MoshiTest.PizzaAdapter()).build();
        JsonAdapter<MoshiTest.Pizza> jsonAdapter = moshi.adapter(MoshiTest.Pizza.class);
        assertThat(jsonAdapter.toJson(new MoshiTest.Pizza(15, true))).isEqualTo("{\"size\":15,\"extra cheese\":true}");
        assertThat(jsonAdapter.fromJson("{\"extra cheese\":true,\"size\":18}")).isEqualTo(new MoshiTest.Pizza(18, true));
    }

    @Test
    public void classAdapterToObjectAndFromObject() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        MoshiTest.Pizza pizza = new MoshiTest.Pizza(15, true);
        Map<String, Object> pizzaObject = new LinkedHashMap<>();
        pizzaObject.put("diameter", 15L);
        pizzaObject.put("extraCheese", true);
        JsonAdapter<MoshiTest.Pizza> jsonAdapter = moshi.adapter(MoshiTest.Pizza.class);
        assertThat(jsonAdapter.toJsonValue(pizza)).isEqualTo(pizzaObject);
        assertThat(jsonAdapter.fromJsonValue(pizzaObject)).isEqualTo(pizza);
    }

    @Test
    public void customJsonAdapterToObjectAndFromObject() throws Exception {
        Moshi moshi = new Moshi.Builder().add(MoshiTest.Pizza.class, new MoshiTest.PizzaAdapter()).build();
        MoshiTest.Pizza pizza = new MoshiTest.Pizza(15, true);
        Map<String, Object> pizzaObject = new LinkedHashMap<>();
        pizzaObject.put("size", 15L);
        pizzaObject.put("extra cheese", true);
        JsonAdapter<MoshiTest.Pizza> jsonAdapter = moshi.adapter(MoshiTest.Pizza.class);
        assertThat(jsonAdapter.toJsonValue(pizza)).isEqualTo(pizzaObject);
        assertThat(jsonAdapter.fromJsonValue(pizzaObject)).isEqualTo(pizza);
    }

    @Test
    public void indent() throws Exception {
        Moshi moshi = new Moshi.Builder().add(MoshiTest.Pizza.class, new MoshiTest.PizzaAdapter()).build();
        JsonAdapter<MoshiTest.Pizza> jsonAdapter = moshi.adapter(MoshiTest.Pizza.class);
        MoshiTest.Pizza pizza = new MoshiTest.Pizza(15, true);
        assertThat(jsonAdapter.indent("  ").toJson(pizza)).isEqualTo(("" + ((("{\n" + "  \"size\": 15,\n") + "  \"extra cheese\": true\n") + "}")));
    }

    @Test
    public void unindent() throws Exception {
        Moshi moshi = new Moshi.Builder().add(MoshiTest.Pizza.class, new MoshiTest.PizzaAdapter()).build();
        JsonAdapter<MoshiTest.Pizza> jsonAdapter = moshi.adapter(MoshiTest.Pizza.class);
        Buffer buffer = new Buffer();
        JsonWriter writer = JsonWriter.of(buffer);
        writer.setLenient(true);
        writer.setIndent("  ");
        MoshiTest.Pizza pizza = new MoshiTest.Pizza(15, true);
        // Calling JsonAdapter.indent("") can remove indentation.
        jsonAdapter.indent("").toJson(writer, pizza);
        assertThat(buffer.readUtf8()).isEqualTo("{\"size\":15,\"extra cheese\":true}");
        // Indentation changes only apply to their use.
        jsonAdapter.toJson(writer, pizza);
        assertThat(buffer.readUtf8()).isEqualTo(("" + ((("{\n" + "  \"size\": 15,\n") + "  \"extra cheese\": true\n") + "}")));
    }

    @Test
    public void composingJsonAdapterFactory() throws Exception {
        Moshi moshi = new Moshi.Builder().add(new MoshiTest.MealDealAdapterFactory()).add(MoshiTest.Pizza.class, new MoshiTest.PizzaAdapter()).build();
        JsonAdapter<MoshiTest.MealDeal> jsonAdapter = moshi.adapter(MoshiTest.MealDeal.class);
        assertThat(jsonAdapter.toJson(new MoshiTest.MealDeal(new MoshiTest.Pizza(15, true), "Pepsi"))).isEqualTo("[{\"size\":15,\"extra cheese\":true},\"Pepsi\"]");
        assertThat(jsonAdapter.fromJson("[{\"extra cheese\":true,\"size\":18},\"Coke\"]")).isEqualTo(new MoshiTest.MealDeal(new MoshiTest.Pizza(18, true), "Coke"));
    }

    static class Message {
        String speak;

        @MoshiTest.Uppercase
        String shout;
    }

    @Test
    public void registerJsonAdapterForAnnotatedType() throws Exception {
        JsonAdapter<String> uppercaseAdapter = new JsonAdapter<String>() {
            @Override
            public String fromJson(JsonReader reader) throws IOException {
                throw new AssertionError();
            }

            @Override
            public void toJson(JsonWriter writer, String value) throws IOException {
                writer.value(value.toUpperCase(Locale.US));
            }
        };
        Moshi moshi = new Moshi.Builder().add(String.class, MoshiTest.Uppercase.class, uppercaseAdapter).build();
        JsonAdapter<MoshiTest.Message> messageAdapter = moshi.adapter(MoshiTest.Message.class);
        MoshiTest.Message message = new MoshiTest.Message();
        message.speak = "Yo dog";
        message.shout = "What's up";
        assertThat(messageAdapter.toJson(message)).isEqualTo("{\"shout\":\"WHAT\'S UP\",\"speak\":\"Yo dog\"}");
    }

    @Test
    public void adapterLookupDisallowsNullType() {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(null, Collections.<Annotation>emptySet());
            Assert.fail();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("type == null");
        }
    }

    @Test
    public void adapterLookupDisallowsNullAnnotations() {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(String.class, ((Class<? extends Annotation>) (null)));
            Assert.fail();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("annotationType == null");
        }
        try {
            moshi.adapter(String.class, ((Set<? extends Annotation>) (null)));
            Assert.fail();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("annotations == null");
        }
    }

    @Test
    public void nextJsonAdapterDisallowsNullAnnotations() throws Exception {
        JsonAdapter.Factory badFactory = new JsonAdapter.Factory() {
            @Nullable
            @Override
            public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
                return moshi.nextAdapter(this, type, null);
            }
        };
        Moshi moshi = new Moshi.Builder().add(badFactory).build();
        try {
            moshi.adapter(Object.class);
            Assert.fail();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("annotations == null");
        }
    }

    @MoshiTest.Uppercase
    static String uppercaseString;

    @Test
    public void delegatingJsonAdapterFactory() throws Exception {
        Moshi moshi = new Moshi.Builder().add(new MoshiTest.UppercaseAdapterFactory()).build();
        Field uppercaseString = MoshiTest.class.getDeclaredField("uppercaseString");
        Set<? extends Annotation> annotations = Util.jsonAnnotations(uppercaseString);
        JsonAdapter<String> adapter = moshi.<String>adapter(String.class, annotations).lenient();
        assertThat(adapter.toJson("a")).isEqualTo("\"A\"");
        assertThat(adapter.fromJson("\"b\"")).isEqualTo("B");
    }

    @Test
    public void listJsonAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<List<String>> adapter = moshi.adapter(Types.newParameterizedType(List.class, String.class));
        assertThat(adapter.toJson(Arrays.asList("a", "b"))).isEqualTo("[\"a\",\"b\"]");
        assertThat(adapter.fromJson("[\"a\",\"b\"]")).isEqualTo(Arrays.asList("a", "b"));
    }

    @Test
    public void setJsonAdapter() throws Exception {
        Set<String> set = new LinkedHashSet<>();
        set.add("a");
        set.add("b");
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Set<String>> adapter = moshi.adapter(Types.newParameterizedType(Set.class, String.class));
        assertThat(adapter.toJson(set)).isEqualTo("[\"a\",\"b\"]");
        assertThat(adapter.fromJson("[\"a\",\"b\"]")).isEqualTo(set);
    }

    @Test
    public void collectionJsonAdapter() throws Exception {
        Collection<String> collection = new ArrayDeque<>();
        collection.add("a");
        collection.add("b");
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Collection<String>> adapter = moshi.adapter(Types.newParameterizedType(Collection.class, String.class));
        assertThat(adapter.toJson(collection)).isEqualTo("[\"a\",\"b\"]");
        assertThat(adapter.fromJson("[\"a\",\"b\"]")).containsExactly("a", "b");
    }

    @MoshiTest.Uppercase
    static List<String> uppercaseStrings;

    @Test
    public void collectionsDoNotKeepAnnotations() throws Exception {
        Moshi moshi = new Moshi.Builder().add(new MoshiTest.UppercaseAdapterFactory()).build();
        Field uppercaseStringsField = MoshiTest.class.getDeclaredField("uppercaseStrings");
        try {
            moshi.adapter(uppercaseStringsField.getGenericType(), Util.jsonAnnotations(uppercaseStringsField));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("No JsonAdapter for java.util.List<java.lang.String> " + "annotated [@com.squareup.moshi.MoshiTest$Uppercase()]"));
        }
    }

    @Test
    public void noTypeAdapterForQualifiedPlatformType() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        Field uppercaseStringField = MoshiTest.class.getDeclaredField("uppercaseString");
        try {
            moshi.adapter(uppercaseStringField.getGenericType(), Util.jsonAnnotations(uppercaseStringField));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage(("No JsonAdapter for class java.lang.String " + "annotated [@com.squareup.moshi.MoshiTest$Uppercase()]"));
        }
    }

    @Test
    public void objectArray() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<String[]> adapter = moshi.adapter(String[].class);
        assertThat(adapter.toJson(new String[]{ "a", "b" })).isEqualTo("[\"a\",\"b\"]");
        assertThat(adapter.fromJson("[\"a\",\"b\"]")).containsExactly("a", "b");
    }

    @Test
    public void primitiveArray() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<int[]> adapter = moshi.adapter(int[].class);
        assertThat(adapter.toJson(new int[]{ 1, 2 })).isEqualTo("[1,2]");
        assertThat(adapter.fromJson("[2,3]")).containsExactly(2, 3);
    }

    @Test
    public void enumAdapter() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Roshambo> adapter = moshi.adapter(MoshiTest.Roshambo.class).lenient();
        assertThat(adapter.fromJson("\"ROCK\"")).isEqualTo(MoshiTest.Roshambo.ROCK);
        assertThat(adapter.toJson(MoshiTest.Roshambo.PAPER)).isEqualTo("\"PAPER\"");
    }

    @Test
    public void annotatedEnum() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Roshambo> adapter = moshi.adapter(MoshiTest.Roshambo.class).lenient();
        assertThat(adapter.fromJson("\"scr\"")).isEqualTo(MoshiTest.Roshambo.SCISSORS);
        assertThat(adapter.toJson(MoshiTest.Roshambo.SCISSORS)).isEqualTo("\"scr\"");
    }

    @Test
    public void invalidEnum() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Roshambo> adapter = moshi.adapter(MoshiTest.Roshambo.class);
        try {
            adapter.fromJson("\"SPOCK\"");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected one of [ROCK, PAPER, scr] but was SPOCK at path $");
        }
    }

    @Test
    public void invalidEnumHasCorrectPathInExceptionMessage() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Roshambo> adapter = moshi.adapter(MoshiTest.Roshambo.class);
        JsonReader reader = JsonReader.of(new Buffer().writeUtf8("[\"SPOCK\"]"));
        reader.beginArray();
        try {
            adapter.fromJson(reader);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Expected one of [ROCK, PAPER, scr] but was SPOCK at path $[0]");
        }
        reader.endArray();
        assertThat(reader.peek()).isEqualTo(END_DOCUMENT);
    }

    @Test
    public void nullEnum() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Roshambo> adapter = moshi.adapter(MoshiTest.Roshambo.class).lenient();
        assertThat(adapter.fromJson("null")).isNull();
        assertThat(adapter.toJson(null)).isEqualTo("null");
    }

    @Test
    public void byDefaultUnknownFieldsAreIgnored() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Pizza> adapter = moshi.adapter(MoshiTest.Pizza.class);
        MoshiTest.Pizza pizza = adapter.fromJson("{\"diameter\":5,\"crust\":\"thick\",\"extraCheese\":true}");
        assertThat(pizza.diameter).isEqualTo(5);
        assertThat(pizza.extraCheese).isEqualTo(true);
    }

    @Test
    public void failOnUnknownThrowsOnUnknownFields() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Pizza> adapter = moshi.adapter(MoshiTest.Pizza.class).failOnUnknown();
        try {
            adapter.fromJson("{\"diameter\":5,\"crust\":\"thick\",\"extraCheese\":true}");
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Cannot skip unexpected NAME at $.diameter");
        }
    }

    @Test
    public void platformTypeThrows() throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(File.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Platform class java.io.File requires explicit JsonAdapter to be registered");
        }
        try {
            moshi.adapter(KeyGenerator.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(("Platform class javax.crypto.KeyGenerator requires explicit " + "JsonAdapter to be registered"));
        }
        try {
            moshi.adapter(Pair.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Platform class android.util.Pair requires explicit JsonAdapter to be registered");
        }
    }

    static final class HasPlatformType {
        ArrayList<String> strings;

        static final class Wrapper {
            MoshiTest.HasPlatformType hasPlatformType;
        }

        static final class ListWrapper {
            List<MoshiTest.HasPlatformType> platformTypes;
        }
    }

    @Test
    public void reentrantFieldErrorMessagesTopLevelMap() {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(Types.newParameterizedType(Map.class, String.class, MoshiTest.HasPlatformType.class));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(("Platform java.util.ArrayList<java.lang.String> requires explicit " + (((("JsonAdapter to be registered" + "\nfor java.util.ArrayList<java.lang.String> strings") + "\nfor class com.squareup.moshi.MoshiTest$HasPlatformType") + "\nfor java.util.Map<java.lang.String, ") + "com.squareup.moshi.MoshiTest$HasPlatformType>")));
            assertThat(e).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause()).hasMessage(("Platform java.util.ArrayList<java.lang.String> " + "requires explicit JsonAdapter to be registered"));
        }
    }

    @Test
    public void reentrantFieldErrorMessagesWrapper() {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(MoshiTest.HasPlatformType.Wrapper.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(("Platform java.util.ArrayList<java.lang.String> requires explicit " + ((("JsonAdapter to be registered" + "\nfor java.util.ArrayList<java.lang.String> strings") + "\nfor class com.squareup.moshi.MoshiTest$HasPlatformType hasPlatformType") + "\nfor class com.squareup.moshi.MoshiTest$HasPlatformType$Wrapper")));
            assertThat(e).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause()).hasMessage(("Platform java.util.ArrayList<java.lang.String> " + "requires explicit JsonAdapter to be registered"));
        }
    }

    @Test
    public void reentrantFieldErrorMessagesListWrapper() {
        Moshi moshi = new Moshi.Builder().build();
        try {
            moshi.adapter(MoshiTest.HasPlatformType.ListWrapper.class);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(("Platform java.util.ArrayList<java.lang.String> requires explicit " + (((("JsonAdapter to be registered" + "\nfor java.util.ArrayList<java.lang.String> strings") + "\nfor class com.squareup.moshi.MoshiTest$HasPlatformType") + "\nfor java.util.List<com.squareup.moshi.MoshiTest$HasPlatformType> platformTypes") + "\nfor class com.squareup.moshi.MoshiTest$HasPlatformType$ListWrapper")));
            assertThat(e).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause()).hasMessage(("Platform java.util.ArrayList<java.lang.String> " + "requires explicit JsonAdapter to be registered"));
        }
    }

    @Test
    public void qualifierWithElementsMayNotBeDirectlyRegistered() throws IOException {
        try {
            new Moshi.Builder().add(Boolean.class, MoshiTest.Localized.class, BOOLEAN_JSON_ADAPTER);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Use JsonAdapter.Factory for annotations with elements");
        }
    }

    @Test
    public void qualifierWithElements() throws IOException {
        Moshi moshi = new Moshi.Builder().add(MoshiTest.LocalizedBooleanAdapter.FACTORY).build();
        MoshiTest.Baguette baguette = new MoshiTest.Baguette();
        baguette.avecBeurre = true;
        baguette.withButter = true;
        JsonAdapter<MoshiTest.Baguette> adapter = moshi.adapter(MoshiTest.Baguette.class);
        assertThat(adapter.toJson(baguette)).isEqualTo("{\"avecBeurre\":\"oui\",\"withButter\":\"yes\"}");
        MoshiTest.Baguette decoded = adapter.fromJson("{\"avecBeurre\":\"oui\",\"withButter\":\"yes\"}");
        assertThat(decoded.avecBeurre).isTrue();
        assertThat(decoded.withButter).isTrue();
    }

    /**
     * Note that this is the opposite of Gson's behavior, where later adapters are preferred.
     */
    @Test
    public void adaptersRegisteredInOrderOfPrecedence() throws Exception {
        JsonAdapter<String> adapter1 = new JsonAdapter<String>() {
            @Override
            public String fromJson(JsonReader reader) throws IOException {
                throw new AssertionError();
            }

            @Override
            public void toJson(JsonWriter writer, String value) throws IOException {
                writer.value("one!");
            }
        };
        JsonAdapter<String> adapter2 = new JsonAdapter<String>() {
            @Override
            public String fromJson(JsonReader reader) throws IOException {
                throw new AssertionError();
            }

            @Override
            public void toJson(JsonWriter writer, String value) throws IOException {
                writer.value("two!");
            }
        };
        Moshi moshi = new Moshi.Builder().add(String.class, adapter1).add(String.class, adapter2).build();
        JsonAdapter<String> adapter = moshi.adapter(String.class).lenient();
        assertThat(adapter.toJson("a")).isEqualTo("\"one!\"");
    }

    @Test
    public void cachingJsonAdapters() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.MealDeal> adapter1 = moshi.adapter(MoshiTest.MealDeal.class);
        JsonAdapter<MoshiTest.MealDeal> adapter2 = moshi.adapter(MoshiTest.MealDeal.class);
        assertThat(adapter1).isSameAs(adapter2);
    }

    @Test
    public void newBuilder() throws Exception {
        Moshi moshi = new Moshi.Builder().add(MoshiTest.Pizza.class, new MoshiTest.PizzaAdapter()).build();
        Moshi.Builder newBuilder = moshi.newBuilder();
        for (JsonAdapter.Factory factory : BUILT_IN_FACTORIES) {
            assertThat(factory).isNotIn(newBuilder.factories);
        }
    }

    @Test
    public void referenceCyclesOnArrays() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", map);
        try {
            moshi.adapter(Object.class).toJson(map);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage((("Nesting too deep at $" + (TestUtil.repeat(".a", 255))) + ": circular reference?"));
        }
    }

    @Test
    public void referenceCyclesOnObjects() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        List<Object> list = new ArrayList<>();
        list.add(list);
        try {
            moshi.adapter(Object.class).toJson(list);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage((("Nesting too deep at $" + (TestUtil.repeat("[0]", 255))) + ": circular reference?"));
        }
    }

    @Test
    public void referenceCyclesOnMixedTypes() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        List<Object> list = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<>();
        list.add(map);
        map.put("a", list);
        try {
            moshi.adapter(Object.class).toJson(list);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage((("Nesting too deep at $[0]" + (TestUtil.repeat(".a[0]", 127))) + ": circular reference?"));
        }
    }

    @Test
    public void duplicateKeyDisallowedInObjectType() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Object> adapter = moshi.adapter(Object.class);
        String json = "{\"diameter\":5,\"diameter\":5,\"extraCheese\":true}";
        try {
            adapter.fromJson(json);
            Assert.fail();
        } catch (JsonDataException expected) {
            assertThat(expected).hasMessage("Map key 'diameter' has multiple values at path $.diameter: 5.0 and 5.0");
        }
    }

    @Test
    public void duplicateKeysAllowedInCustomType() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<MoshiTest.Pizza> adapter = moshi.adapter(MoshiTest.Pizza.class);
        String json = "{\"diameter\":5,\"diameter\":5,\"extraCheese\":true}";
        assertThat(adapter.fromJson(json)).isEqualTo(new MoshiTest.Pizza(5, true));
    }

    static class Pizza {
        final int diameter;

        final boolean extraCheese;

        Pizza(int diameter, boolean extraCheese) {
            this.diameter = diameter;
            this.extraCheese = extraCheese;
        }

        @Override
        public boolean equals(Object o) {
            return ((o instanceof MoshiTest.Pizza) && ((((MoshiTest.Pizza) (o)).diameter) == (diameter))) && ((((MoshiTest.Pizza) (o)).extraCheese) == (extraCheese));
        }

        @Override
        public int hashCode() {
            return (diameter) * (extraCheese ? 31 : 1);
        }
    }

    static class MealDeal {
        final MoshiTest.Pizza pizza;

        final String drink;

        MealDeal(MoshiTest.Pizza pizza, String drink) {
            this.pizza = pizza;
            this.drink = drink;
        }

        @Override
        public boolean equals(Object o) {
            return ((o instanceof MoshiTest.MealDeal) && (((MoshiTest.MealDeal) (o)).pizza.equals(pizza))) && (((MoshiTest.MealDeal) (o)).drink.equals(drink));
        }

        @Override
        public int hashCode() {
            return (pizza.hashCode()) + (31 * (drink.hashCode()));
        }
    }

    static class PizzaAdapter extends JsonAdapter<MoshiTest.Pizza> {
        @Override
        public MoshiTest.Pizza fromJson(JsonReader reader) throws IOException {
            int diameter = 13;
            boolean extraCheese = false;
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("size")) {
                    diameter = reader.nextInt();
                } else
                    if (name.equals("extra cheese")) {
                        extraCheese = reader.nextBoolean();
                    } else {
                        reader.skipValue();
                    }

            } 
            reader.endObject();
            return new MoshiTest.Pizza(diameter, extraCheese);
        }

        @Override
        public void toJson(JsonWriter writer, MoshiTest.Pizza value) throws IOException {
            writer.beginObject();
            writer.name("size").value(value.diameter);
            writer.name("extra cheese").value(value.extraCheese);
            writer.endObject();
        }
    }

    static class MealDealAdapterFactory implements JsonAdapter.Factory {
        @Override
        public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
            if (!(type.equals(MoshiTest.MealDeal.class)))
                return null;

            final JsonAdapter<MoshiTest.Pizza> pizzaAdapter = moshi.adapter(MoshiTest.Pizza.class);
            final JsonAdapter<String> drinkAdapter = moshi.adapter(String.class);
            return new JsonAdapter<MoshiTest.MealDeal>() {
                @Override
                public MoshiTest.MealDeal fromJson(JsonReader reader) throws IOException {
                    reader.beginArray();
                    MoshiTest.Pizza pizza = pizzaAdapter.fromJson(reader);
                    String drink = drinkAdapter.fromJson(reader);
                    reader.endArray();
                    return new MoshiTest.MealDeal(pizza, drink);
                }

                @Override
                public void toJson(JsonWriter writer, MoshiTest.MealDeal value) throws IOException {
                    writer.beginArray();
                    pizzaAdapter.toJson(writer, value.pizza);
                    drinkAdapter.toJson(writer, value.drink);
                    writer.endArray();
                }
            };
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    public @interface Uppercase {}

    static class UppercaseAdapterFactory implements JsonAdapter.Factory {
        @Override
        public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
            if (!(type.equals(String.class)))
                return null;

            if (!(Util.isAnnotationPresent(annotations, MoshiTest.Uppercase.class)))
                return null;

            final JsonAdapter<String> stringAdapter = moshi.nextAdapter(this, String.class, NO_ANNOTATIONS);
            return new JsonAdapter<String>() {
                @Override
                public String fromJson(JsonReader reader) throws IOException {
                    String s = stringAdapter.fromJson(reader);
                    return s.toUpperCase(Locale.US);
                }

                @Override
                public void toJson(JsonWriter writer, String value) throws IOException {
                    stringAdapter.toJson(writer, value.toUpperCase());
                }
            };
        }
    }

    enum Roshambo {

        ROCK,
        PAPER,
        @Json(name = "scr")
        SCISSORS;}

    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    @interface Localized {
        String value();
    }

    static class Baguette {
        @MoshiTest.Localized("en")
        boolean withButter;

        @MoshiTest.Localized("fr")
        boolean avecBeurre;
    }

    static class LocalizedBooleanAdapter extends JsonAdapter<Boolean> {
        private static final Factory FACTORY = new JsonAdapter.Factory() {
            @Override
            public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
                if (type == (boolean.class)) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof MoshiTest.Localized) {
                            return new MoshiTest.LocalizedBooleanAdapter(((MoshiTest.Localized) (annotation)).value());
                        }
                    }
                }
                return null;
            }
        };

        private final String trueString;

        private final String falseString;

        public LocalizedBooleanAdapter(String language) {
            if (language.equals("fr")) {
                trueString = "oui";
                falseString = "non";
            } else {
                trueString = "yes";
                falseString = "no";
            }
        }

        @Override
        public Boolean fromJson(JsonReader reader) throws IOException {
            return reader.nextString().equals(trueString);
        }

        @Override
        public void toJson(JsonWriter writer, Boolean value) throws IOException {
            writer.value((value ? trueString : falseString));
        }
    }
}

