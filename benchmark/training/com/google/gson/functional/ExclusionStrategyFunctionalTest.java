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


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import junit.framework.TestCase;


/**
 * Performs some functional tests when Gson is instantiated with some common user defined
 * {@link ExclusionStrategy} objects.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ExclusionStrategyFunctionalTest extends TestCase {
    private static final ExclusionStrategy EXCLUDE_SAMPLE_OBJECT_FOR_TEST = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return clazz == (ExclusionStrategyFunctionalTest.SampleObjectForTest.class);
        }
    };

    private ExclusionStrategyFunctionalTest.SampleObjectForTest src;

    public void testExclusionStrategySerialization() throws Exception {
        Gson gson = ExclusionStrategyFunctionalTest.createGson(new ExclusionStrategyFunctionalTest.MyExclusionStrategy(String.class), true);
        String json = gson.toJson(src);
        TestCase.assertFalse(json.contains("\"stringField\""));
        TestCase.assertFalse(json.contains("\"annotatedField\""));
        TestCase.assertTrue(json.contains("\"longField\""));
    }

    public void testExclusionStrategySerializationDoesNotImpactDeserialization() {
        String json = "{\"annotatedField\":1,\"stringField\":\"x\",\"longField\":2}";
        Gson gson = ExclusionStrategyFunctionalTest.createGson(new ExclusionStrategyFunctionalTest.MyExclusionStrategy(String.class), true);
        ExclusionStrategyFunctionalTest.SampleObjectForTest value = gson.fromJson(json, ExclusionStrategyFunctionalTest.SampleObjectForTest.class);
        TestCase.assertEquals(1, value.annotatedField);
        TestCase.assertEquals("x", value.stringField);
        TestCase.assertEquals(2, value.longField);
    }

    public void testExclusionStrategyDeserialization() throws Exception {
        Gson gson = ExclusionStrategyFunctionalTest.createGson(new ExclusionStrategyFunctionalTest.MyExclusionStrategy(String.class), false);
        JsonObject json = new JsonObject();
        json.add("annotatedField", new JsonPrimitive(((src.annotatedField) + 5)));
        json.add("stringField", new JsonPrimitive(((src.stringField) + "blah,blah")));
        json.add("longField", new JsonPrimitive(1212311L));
        ExclusionStrategyFunctionalTest.SampleObjectForTest target = gson.fromJson(json, ExclusionStrategyFunctionalTest.SampleObjectForTest.class);
        TestCase.assertEquals(1212311L, target.longField);
        // assert excluded fields are set to the defaults
        TestCase.assertEquals(src.annotatedField, target.annotatedField);
        TestCase.assertEquals(src.stringField, target.stringField);
    }

    public void testExclusionStrategySerializationDoesNotImpactSerialization() throws Exception {
        Gson gson = ExclusionStrategyFunctionalTest.createGson(new ExclusionStrategyFunctionalTest.MyExclusionStrategy(String.class), false);
        String json = gson.toJson(src);
        TestCase.assertTrue(json.contains("\"stringField\""));
        TestCase.assertTrue(json.contains("\"annotatedField\""));
        TestCase.assertTrue(json.contains("\"longField\""));
    }

    public void testExclusionStrategyWithMode() throws Exception {
        ExclusionStrategyFunctionalTest.SampleObjectForTest testObj = new ExclusionStrategyFunctionalTest.SampleObjectForTest(((src.annotatedField) + 5), ((src.stringField) + "blah,blah"), ((src.longField) + 655L));
        Gson gson = ExclusionStrategyFunctionalTest.createGson(new ExclusionStrategyFunctionalTest.MyExclusionStrategy(String.class), false);
        JsonObject json = gson.toJsonTree(testObj).getAsJsonObject();
        TestCase.assertEquals(testObj.annotatedField, json.get("annotatedField").getAsInt());
        TestCase.assertEquals(testObj.stringField, json.get("stringField").getAsString());
        TestCase.assertEquals(testObj.longField, json.get("longField").getAsLong());
        ExclusionStrategyFunctionalTest.SampleObjectForTest target = gson.fromJson(json, ExclusionStrategyFunctionalTest.SampleObjectForTest.class);
        TestCase.assertEquals(testObj.longField, target.longField);
        // assert excluded fields are set to the defaults
        TestCase.assertEquals(src.annotatedField, target.annotatedField);
        TestCase.assertEquals(src.stringField, target.stringField);
    }

    public void testExcludeTopLevelClassSerialization() {
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(ExclusionStrategyFunctionalTest.EXCLUDE_SAMPLE_OBJECT_FOR_TEST).create();
        TestCase.assertEquals("null", gson.toJson(new ExclusionStrategyFunctionalTest.SampleObjectForTest(), ExclusionStrategyFunctionalTest.SampleObjectForTest.class));
    }

    public void testExcludeTopLevelClassSerializationDoesNotImpactDeserialization() {
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(ExclusionStrategyFunctionalTest.EXCLUDE_SAMPLE_OBJECT_FOR_TEST).create();
        String json = "{\"annotatedField\":1,\"stringField\":\"x\",\"longField\":2}";
        ExclusionStrategyFunctionalTest.SampleObjectForTest value = gson.fromJson(json, ExclusionStrategyFunctionalTest.SampleObjectForTest.class);
        TestCase.assertEquals(1, value.annotatedField);
        TestCase.assertEquals("x", value.stringField);
        TestCase.assertEquals(2, value.longField);
    }

    public void testExcludeTopLevelClassDeserialization() {
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(ExclusionStrategyFunctionalTest.EXCLUDE_SAMPLE_OBJECT_FOR_TEST).create();
        String json = "{\"annotatedField\":1,\"stringField\":\"x\",\"longField\":2}";
        ExclusionStrategyFunctionalTest.SampleObjectForTest value = gson.fromJson(json, ExclusionStrategyFunctionalTest.SampleObjectForTest.class);
        TestCase.assertNull(value);
    }

    public void testExcludeTopLevelClassDeserializationDoesNotImpactSerialization() {
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(ExclusionStrategyFunctionalTest.EXCLUDE_SAMPLE_OBJECT_FOR_TEST).create();
        String json = gson.toJson(new ExclusionStrategyFunctionalTest.SampleObjectForTest(), ExclusionStrategyFunctionalTest.SampleObjectForTest.class);
        TestCase.assertTrue(json.contains("\"stringField\""));
        TestCase.assertTrue(json.contains("\"annotatedField\""));
        TestCase.assertTrue(json.contains("\"longField\""));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    private static @interface Foo {}

    private static class SampleObjectForTest {
        @ExclusionStrategyFunctionalTest.Foo
        private final int annotatedField;

        private final String stringField;

        private final long longField;

        public SampleObjectForTest() {
            this(5, "someDefaultValue", 12345L);
        }

        public SampleObjectForTest(int annotatedField, String stringField, long longField) {
            this.annotatedField = annotatedField;
            this.stringField = stringField;
            this.longField = longField;
        }
    }

    private static final class MyExclusionStrategy implements ExclusionStrategy {
        private final Class<?> typeToSkip;

        private MyExclusionStrategy(Class<?> typeToSkip) {
            this.typeToSkip = typeToSkip;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return clazz == (typeToSkip);
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return (f.getAnnotation(ExclusionStrategyFunctionalTest.Foo.class)) != null;
        }
    }
}

