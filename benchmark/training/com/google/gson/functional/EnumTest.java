/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.common.MoreAsserts;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Functional tests for Java 5.0 enums.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class EnumTest extends TestCase {
    private Gson gson;

    public void testTopLevelEnumSerialization() throws Exception {
        String result = gson.toJson(EnumTest.MyEnum.VALUE1);
        TestCase.assertEquals((('"' + (EnumTest.MyEnum.VALUE1.toString())) + '"'), result);
    }

    public void testTopLevelEnumDeserialization() throws Exception {
        EnumTest.MyEnum result = gson.fromJson((('"' + (EnumTest.MyEnum.VALUE1.toString())) + '"'), EnumTest.MyEnum.class);
        TestCase.assertEquals(EnumTest.MyEnum.VALUE1, result);
    }

    public void testCollectionOfEnumsSerialization() {
        Type type = new TypeToken<Collection<EnumTest.MyEnum>>() {}.getType();
        Collection<EnumTest.MyEnum> target = new ArrayList<EnumTest.MyEnum>();
        target.add(EnumTest.MyEnum.VALUE1);
        target.add(EnumTest.MyEnum.VALUE2);
        String expectedJson = "[\"VALUE1\",\"VALUE2\"]";
        String actualJson = gson.toJson(target);
        TestCase.assertEquals(expectedJson, actualJson);
        actualJson = gson.toJson(target, type);
        TestCase.assertEquals(expectedJson, actualJson);
    }

    public void testCollectionOfEnumsDeserialization() {
        Type type = new TypeToken<Collection<EnumTest.MyEnum>>() {}.getType();
        String json = "[\"VALUE1\",\"VALUE2\"]";
        Collection<EnumTest.MyEnum> target = gson.fromJson(json, type);
        MoreAsserts.assertContains(target, EnumTest.MyEnum.VALUE1);
        MoreAsserts.assertContains(target, EnumTest.MyEnum.VALUE2);
    }

    public void testClassWithEnumFieldSerialization() throws Exception {
        EnumTest.ClassWithEnumFields target = new EnumTest.ClassWithEnumFields();
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testClassWithEnumFieldDeserialization() throws Exception {
        String json = "{value1:'VALUE1',value2:'VALUE2'}";
        EnumTest.ClassWithEnumFields target = gson.fromJson(json, EnumTest.ClassWithEnumFields.class);
        TestCase.assertEquals(EnumTest.MyEnum.VALUE1, target.value1);
        TestCase.assertEquals(EnumTest.MyEnum.VALUE2, target.value2);
    }

    private static enum MyEnum {

        VALUE1,
        VALUE2;}

    private static class ClassWithEnumFields {
        private final EnumTest.MyEnum value1 = EnumTest.MyEnum.VALUE1;

        private final EnumTest.MyEnum value2 = EnumTest.MyEnum.VALUE2;

        public String getExpectedJson() {
            return ((("{\"value1\":\"" + (value1)) + "\",\"value2\":\"") + (value2)) + "\"}";
        }
    }

    /**
     * Test for issue 226.
     */
    public void testEnumSubclass() {
        TestCase.assertFalse(((EnumTest.Roshambo.class) == (EnumTest.Roshambo.ROCK.getClass())));
        TestCase.assertEquals("\"ROCK\"", gson.toJson(EnumTest.Roshambo.ROCK));
        TestCase.assertEquals("[\"ROCK\",\"PAPER\",\"SCISSORS\"]", gson.toJson(EnumSet.allOf(EnumTest.Roshambo.class)));
        TestCase.assertEquals(EnumTest.Roshambo.ROCK, gson.fromJson("\"ROCK\"", EnumTest.Roshambo.class));
        TestCase.assertEquals(EnumSet.allOf(EnumTest.Roshambo.class), gson.fromJson("[\"ROCK\",\"PAPER\",\"SCISSORS\"]", new TypeToken<Set<EnumTest.Roshambo>>() {}.getType()));
    }

    public void testEnumSubclassWithRegisteredTypeAdapter() {
        gson = new GsonBuilder().registerTypeHierarchyAdapter(EnumTest.Roshambo.class, new EnumTest.MyEnumTypeAdapter()).create();
        TestCase.assertFalse(((EnumTest.Roshambo.class) == (EnumTest.Roshambo.ROCK.getClass())));
        TestCase.assertEquals("\"123ROCK\"", gson.toJson(EnumTest.Roshambo.ROCK));
        TestCase.assertEquals("[\"123ROCK\",\"123PAPER\",\"123SCISSORS\"]", gson.toJson(EnumSet.allOf(EnumTest.Roshambo.class)));
        TestCase.assertEquals(EnumTest.Roshambo.ROCK, gson.fromJson("\"123ROCK\"", EnumTest.Roshambo.class));
        TestCase.assertEquals(EnumSet.allOf(EnumTest.Roshambo.class), gson.fromJson("[\"123ROCK\",\"123PAPER\",\"123SCISSORS\"]", new TypeToken<Set<EnumTest.Roshambo>>() {}.getType()));
    }

    public void testEnumSubclassAsParameterizedType() {
        Collection<EnumTest.Roshambo> list = new ArrayList<EnumTest.Roshambo>();
        list.add(EnumTest.Roshambo.ROCK);
        list.add(EnumTest.Roshambo.PAPER);
        String json = gson.toJson(list);
        TestCase.assertEquals("[\"ROCK\",\"PAPER\"]", json);
        Type collectionType = new TypeToken<Collection<EnumTest.Roshambo>>() {}.getType();
        Collection<EnumTest.Roshambo> actualJsonList = gson.fromJson(json, collectionType);
        MoreAsserts.assertContains(actualJsonList, EnumTest.Roshambo.ROCK);
        MoreAsserts.assertContains(actualJsonList, EnumTest.Roshambo.PAPER);
    }

    public void testEnumCaseMapping() {
        TestCase.assertEquals(EnumTest.Gender.MALE, gson.fromJson("\"boy\"", EnumTest.Gender.class));
        TestCase.assertEquals("\"boy\"", gson.toJson(EnumTest.Gender.MALE, EnumTest.Gender.class));
    }

    public void testEnumSet() {
        EnumSet<EnumTest.Roshambo> foo = EnumSet.of(EnumTest.Roshambo.ROCK, EnumTest.Roshambo.PAPER);
        String json = gson.toJson(foo);
        Type type = new TypeToken<EnumSet<EnumTest.Roshambo>>() {}.getType();
        EnumSet<EnumTest.Roshambo> bar = gson.fromJson(json, type);
        TestCase.assertTrue(bar.contains(EnumTest.Roshambo.ROCK));
        TestCase.assertTrue(bar.contains(EnumTest.Roshambo.PAPER));
        TestCase.assertFalse(bar.contains(EnumTest.Roshambo.SCISSORS));
    }

    public enum Roshambo {

        ROCK() {
            @Override
            EnumTest.Roshambo defeats() {
                return EnumTest.Roshambo.SCISSORS;
            }
        },
        PAPER() {
            @Override
            EnumTest.Roshambo defeats() {
                return EnumTest.Roshambo.ROCK;
            }
        },
        SCISSORS() {
            @Override
            EnumTest.Roshambo defeats() {
                return EnumTest.Roshambo.PAPER;
            }
        };
        abstract EnumTest.Roshambo defeats();
    }

    private static class MyEnumTypeAdapter implements JsonDeserializer<EnumTest.Roshambo> , JsonSerializer<EnumTest.Roshambo> {
        @Override
        public JsonElement serialize(EnumTest.Roshambo src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(("123" + (src.name())));
        }

        @Override
        public EnumTest.Roshambo deserialize(JsonElement json, Type classOfT, JsonDeserializationContext context) throws JsonParseException {
            return EnumTest.Roshambo.valueOf(json.getAsString().substring(3));
        }
    }

    public enum Gender {

        @SerializedName("boy")
        MALE,
        @SerializedName("girl")
        FEMALE;}

    public void testEnumClassWithFields() {
        TestCase.assertEquals("\"RED\"", gson.toJson(EnumTest.Color.RED));
        TestCase.assertEquals("red", gson.fromJson("RED", EnumTest.Color.class).value);
    }

    public enum Color {

        RED("red", 1),
        BLUE("blue", 2),
        GREEN("green", 3);
        String value;

        int index;

        private Color(String value, int index) {
            this.value = value;
            this.index = index;
        }
    }
}

