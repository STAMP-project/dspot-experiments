/**
 * Copyright (C) 2016 Google Inc.
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
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Functional tests for the {@link JsonAdapter} annotation on fields where the value is of
 * type {@link JsonSerializer} or {@link JsonDeserializer}.
 */
public final class JsonAdapterSerializerDeserializerTest extends TestCase {
    public void testJsonSerializerDeserializerBasedJsonAdapterOnFields() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterSerializerDeserializerTest.Computer(new JsonAdapterSerializerDeserializerTest.User("Inderjeet Singh"), null, new JsonAdapterSerializerDeserializerTest.User("Jesse Wilson")));
        TestCase.assertEquals("{\"user1\":\"UserSerializer\",\"user3\":\"UserSerializerDeserializer\"}", json);
        JsonAdapterSerializerDeserializerTest.Computer computer = gson.fromJson("{'user2':'Jesse Wilson','user3':'Jake Wharton'}", JsonAdapterSerializerDeserializerTest.Computer.class);
        TestCase.assertEquals("UserSerializer", computer.user2.name);
        TestCase.assertEquals("UserSerializerDeserializer", computer.user3.name);
    }

    private static final class Computer {
        @JsonAdapter(JsonAdapterSerializerDeserializerTest.UserSerializer.class)
        final JsonAdapterSerializerDeserializerTest.User user1;

        @JsonAdapter(JsonAdapterSerializerDeserializerTest.UserDeserializer.class)
        final JsonAdapterSerializerDeserializerTest.User user2;

        @JsonAdapter(JsonAdapterSerializerDeserializerTest.UserSerializerDeserializer.class)
        final JsonAdapterSerializerDeserializerTest.User user3;

        Computer(JsonAdapterSerializerDeserializerTest.User user1, JsonAdapterSerializerDeserializerTest.User user2, JsonAdapterSerializerDeserializerTest.User user3) {
            this.user1 = user1;
            this.user2 = user2;
            this.user3 = user3;
        }
    }

    private static final class User {
        public final String name;

        private User(String name) {
            this.name = name;
        }
    }

    private static final class UserSerializer implements JsonSerializer<JsonAdapterSerializerDeserializerTest.User> {
        @Override
        public JsonElement serialize(JsonAdapterSerializerDeserializerTest.User src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("UserSerializer");
        }
    }

    private static final class UserDeserializer implements JsonDeserializer<JsonAdapterSerializerDeserializerTest.User> {
        @Override
        public JsonAdapterSerializerDeserializerTest.User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new JsonAdapterSerializerDeserializerTest.User("UserSerializer");
        }
    }

    private static final class UserSerializerDeserializer implements JsonDeserializer<JsonAdapterSerializerDeserializerTest.User> , JsonSerializer<JsonAdapterSerializerDeserializerTest.User> {
        @Override
        public JsonElement serialize(JsonAdapterSerializerDeserializerTest.User src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("UserSerializerDeserializer");
        }

        @Override
        public JsonAdapterSerializerDeserializerTest.User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new JsonAdapterSerializerDeserializerTest.User("UserSerializerDeserializer");
        }
    }

    public void testJsonSerializerDeserializerBasedJsonAdapterOnClass() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterSerializerDeserializerTest.Computer2(new JsonAdapterSerializerDeserializerTest.User2("Inderjeet Singh")));
        TestCase.assertEquals("{\"user\":\"UserSerializerDeserializer2\"}", json);
        JsonAdapterSerializerDeserializerTest.Computer2 computer = gson.fromJson("{'user':'Inderjeet Singh'}", JsonAdapterSerializerDeserializerTest.Computer2.class);
        TestCase.assertEquals("UserSerializerDeserializer2", computer.user.name);
    }

    private static final class Computer2 {
        final JsonAdapterSerializerDeserializerTest.User2 user;

        Computer2(JsonAdapterSerializerDeserializerTest.User2 user) {
            this.user = user;
        }
    }

    @JsonAdapter(JsonAdapterSerializerDeserializerTest.UserSerializerDeserializer2.class)
    private static final class User2 {
        public final String name;

        private User2(String name) {
            this.name = name;
        }
    }

    private static final class UserSerializerDeserializer2 implements JsonDeserializer<JsonAdapterSerializerDeserializerTest.User2> , JsonSerializer<JsonAdapterSerializerDeserializerTest.User2> {
        @Override
        public JsonElement serialize(JsonAdapterSerializerDeserializerTest.User2 src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("UserSerializerDeserializer2");
        }

        @Override
        public JsonAdapterSerializerDeserializerTest.User2 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new JsonAdapterSerializerDeserializerTest.User2("UserSerializerDeserializer2");
        }
    }

    public void testDifferentJsonAdaptersForGenericFieldsOfSameRawType() {
        JsonAdapterSerializerDeserializerTest.Container c = new JsonAdapterSerializerDeserializerTest.Container("Foo", 10);
        Gson gson = new Gson();
        String json = gson.toJson(c);
        TestCase.assertTrue(json.contains("\"a\":\"BaseStringAdapter\""));
        TestCase.assertTrue(json.contains("\"b\":\"BaseIntegerAdapter\""));
    }

    private static final class Container {
        @JsonAdapter(JsonAdapterSerializerDeserializerTest.BaseStringAdapter.class)
        JsonAdapterSerializerDeserializerTest.Base<String> a;

        @JsonAdapter(JsonAdapterSerializerDeserializerTest.BaseIntegerAdapter.class)
        JsonAdapterSerializerDeserializerTest.Base<Integer> b;

        Container(String a, int b) {
            this.a = new JsonAdapterSerializerDeserializerTest.Base<String>(a);
            this.b = new JsonAdapterSerializerDeserializerTest.Base<Integer>(b);
        }
    }

    private static final class Base<T> {
        @SuppressWarnings("unused")
        T value;

        Base(T value) {
            this.value = value;
        }
    }

    private static final class BaseStringAdapter implements JsonSerializer<JsonAdapterSerializerDeserializerTest.Base<String>> {
        @Override
        public JsonElement serialize(JsonAdapterSerializerDeserializerTest.Base<String> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("BaseStringAdapter");
        }
    }

    private static final class BaseIntegerAdapter implements JsonSerializer<JsonAdapterSerializerDeserializerTest.Base<Integer>> {
        @Override
        public JsonElement serialize(JsonAdapterSerializerDeserializerTest.Base<Integer> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("BaseIntegerAdapter");
        }
    }
}

