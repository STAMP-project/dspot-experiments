/**
 * Copyright (C) 2014 Google Inc.
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
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 * Functional tests for the {@link com.google.gson.annotations.JsonAdapter} annotation on fields.
 */
public final class JsonAdapterAnnotationOnFieldsTest extends TestCase {
    public void testClassAnnotationAdapterTakesPrecedenceOverDefault() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.Computer(new JsonAdapterAnnotationOnFieldsTest.User("Inderjeet Singh")));
        TestCase.assertEquals("{\"user\":\"UserClassAnnotationAdapter\"}", json);
        JsonAdapterAnnotationOnFieldsTest.Computer computer = gson.fromJson("{'user':'Inderjeet Singh'}", JsonAdapterAnnotationOnFieldsTest.Computer.class);
        TestCase.assertEquals("UserClassAnnotationAdapter", computer.user.name);
    }

    public void testClassAnnotationAdapterFactoryTakesPrecedenceOverDefault() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.Gizmo(new JsonAdapterAnnotationOnFieldsTest.Part("Part")));
        TestCase.assertEquals("{\"part\":\"GizmoPartTypeAdapterFactory\"}", json);
        JsonAdapterAnnotationOnFieldsTest.Gizmo computer = gson.fromJson("{'part':'Part'}", JsonAdapterAnnotationOnFieldsTest.Gizmo.class);
        TestCase.assertEquals("GizmoPartTypeAdapterFactory", computer.part.name);
    }

    public void testRegisteredTypeAdapterTakesPrecedenceOverClassAnnotationAdapter() {
        Gson gson = new GsonBuilder().registerTypeAdapter(JsonAdapterAnnotationOnFieldsTest.User.class, new JsonAdapterAnnotationOnFieldsTest.RegisteredUserAdapter()).create();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.Computer(new JsonAdapterAnnotationOnFieldsTest.User("Inderjeet Singh")));
        TestCase.assertEquals("{\"user\":\"RegisteredUserAdapter\"}", json);
        JsonAdapterAnnotationOnFieldsTest.Computer computer = gson.fromJson("{'user':'Inderjeet Singh'}", JsonAdapterAnnotationOnFieldsTest.Computer.class);
        TestCase.assertEquals("RegisteredUserAdapter", computer.user.name);
    }

    public void testFieldAnnotationTakesPrecedenceOverRegisteredTypeAdapter() {
        Gson gson = new GsonBuilder().registerTypeAdapter(JsonAdapterAnnotationOnFieldsTest.Part.class, new TypeAdapter<JsonAdapterAnnotationOnFieldsTest.Part>() {
            @Override
            public void write(JsonWriter out, JsonAdapterAnnotationOnFieldsTest.Part part) throws IOException {
                throw new AssertionError();
            }

            @Override
            public JsonAdapterAnnotationOnFieldsTest.Part read(JsonReader in) throws IOException {
                throw new AssertionError();
            }
        }).create();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.Gadget(new JsonAdapterAnnotationOnFieldsTest.Part("screen")));
        TestCase.assertEquals("{\"part\":\"PartJsonFieldAnnotationAdapter\"}", json);
        JsonAdapterAnnotationOnFieldsTest.Gadget gadget = gson.fromJson("{'part':'screen'}", JsonAdapterAnnotationOnFieldsTest.Gadget.class);
        TestCase.assertEquals("PartJsonFieldAnnotationAdapter", gadget.part.name);
    }

    public void testFieldAnnotationTakesPrecedenceOverClassAnnotation() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.Computer2(new JsonAdapterAnnotationOnFieldsTest.User("Inderjeet Singh")));
        TestCase.assertEquals("{\"user\":\"UserFieldAnnotationAdapter\"}", json);
        JsonAdapterAnnotationOnFieldsTest.Computer2 target = gson.fromJson("{'user':'Interjeet Singh'}", JsonAdapterAnnotationOnFieldsTest.Computer2.class);
        TestCase.assertEquals("UserFieldAnnotationAdapter", target.user.name);
    }

    private static final class Gadget {
        @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.PartJsonFieldAnnotationAdapter.class)
        final JsonAdapterAnnotationOnFieldsTest.Part part;

        Gadget(JsonAdapterAnnotationOnFieldsTest.Part part) {
            this.part = part;
        }
    }

    private static final class Gizmo {
        @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.GizmoPartTypeAdapterFactory.class)
        final JsonAdapterAnnotationOnFieldsTest.Part part;

        Gizmo(JsonAdapterAnnotationOnFieldsTest.Part part) {
            this.part = part;
        }
    }

    private static final class Part {
        final String name;

        public Part(String name) {
            this.name = name;
        }
    }

    private static class PartJsonFieldAnnotationAdapter extends TypeAdapter<JsonAdapterAnnotationOnFieldsTest.Part> {
        @Override
        public void write(JsonWriter out, JsonAdapterAnnotationOnFieldsTest.Part part) throws IOException {
            out.value("PartJsonFieldAnnotationAdapter");
        }

        @Override
        public JsonAdapterAnnotationOnFieldsTest.Part read(JsonReader in) throws IOException {
            in.nextString();
            return new JsonAdapterAnnotationOnFieldsTest.Part("PartJsonFieldAnnotationAdapter");
        }
    }

    private static class GizmoPartTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    out.value("GizmoPartTypeAdapterFactory");
                }

                @SuppressWarnings("unchecked")
                @Override
                public T read(JsonReader in) throws IOException {
                    in.nextString();
                    return ((T) (new JsonAdapterAnnotationOnFieldsTest.Part("GizmoPartTypeAdapterFactory")));
                }
            };
        }
    }

    private static final class Computer {
        final JsonAdapterAnnotationOnFieldsTest.User user;

        Computer(JsonAdapterAnnotationOnFieldsTest.User user) {
            this.user = user;
        }
    }

    @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.UserClassAnnotationAdapter.class)
    private static class User {
        public final String name;

        private User(String name) {
            this.name = name;
        }
    }

    private static class UserClassAnnotationAdapter extends TypeAdapter<JsonAdapterAnnotationOnFieldsTest.User> {
        @Override
        public void write(JsonWriter out, JsonAdapterAnnotationOnFieldsTest.User user) throws IOException {
            out.value("UserClassAnnotationAdapter");
        }

        @Override
        public JsonAdapterAnnotationOnFieldsTest.User read(JsonReader in) throws IOException {
            in.nextString();
            return new JsonAdapterAnnotationOnFieldsTest.User("UserClassAnnotationAdapter");
        }
    }

    private static final class Computer2 {
        // overrides the JsonAdapter annotation of User with this
        @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.UserFieldAnnotationAdapter.class)
        final JsonAdapterAnnotationOnFieldsTest.User user;

        Computer2(JsonAdapterAnnotationOnFieldsTest.User user) {
            this.user = user;
        }
    }

    private static final class UserFieldAnnotationAdapter extends TypeAdapter<JsonAdapterAnnotationOnFieldsTest.User> {
        @Override
        public void write(JsonWriter out, JsonAdapterAnnotationOnFieldsTest.User user) throws IOException {
            out.value("UserFieldAnnotationAdapter");
        }

        @Override
        public JsonAdapterAnnotationOnFieldsTest.User read(JsonReader in) throws IOException {
            in.nextString();
            return new JsonAdapterAnnotationOnFieldsTest.User("UserFieldAnnotationAdapter");
        }
    }

    private static final class RegisteredUserAdapter extends TypeAdapter<JsonAdapterAnnotationOnFieldsTest.User> {
        @Override
        public void write(JsonWriter out, JsonAdapterAnnotationOnFieldsTest.User user) throws IOException {
            out.value("RegisteredUserAdapter");
        }

        @Override
        public JsonAdapterAnnotationOnFieldsTest.User read(JsonReader in) throws IOException {
            in.nextString();
            return new JsonAdapterAnnotationOnFieldsTest.User("RegisteredUserAdapter");
        }
    }

    public void testJsonAdapterInvokedOnlyForAnnotatedFields() {
        Gson gson = new Gson();
        String json = "{'part1':'name','part2':{'name':'name2'}}";
        JsonAdapterAnnotationOnFieldsTest.GadgetWithTwoParts gadget = gson.fromJson(json, JsonAdapterAnnotationOnFieldsTest.GadgetWithTwoParts.class);
        TestCase.assertEquals("PartJsonFieldAnnotationAdapter", gadget.part1.name);
        TestCase.assertEquals("name2", gadget.part2.name);
    }

    private static final class GadgetWithTwoParts {
        @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.PartJsonFieldAnnotationAdapter.class)
        final JsonAdapterAnnotationOnFieldsTest.Part part1;

        final JsonAdapterAnnotationOnFieldsTest.Part part2;// Doesn't have the JsonAdapter annotation


        @SuppressWarnings("unused")
        GadgetWithTwoParts(JsonAdapterAnnotationOnFieldsTest.Part part1, JsonAdapterAnnotationOnFieldsTest.Part part2) {
            this.part1 = part1;
            this.part2 = part2;
        }
    }

    public void testJsonAdapterWrappedInNullSafeAsRequested() {
        Gson gson = new Gson();
        String fromJson = "{'part':null}";
        JsonAdapterAnnotationOnFieldsTest.GadgetWithOptionalPart gadget = gson.fromJson(fromJson, JsonAdapterAnnotationOnFieldsTest.GadgetWithOptionalPart.class);
        TestCase.assertNull(gadget.part);
        String toJson = gson.toJson(gadget);
        TestCase.assertFalse(toJson.contains("PartJsonFieldAnnotationAdapter"));
    }

    private static final class GadgetWithOptionalPart {
        @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.PartJsonFieldAnnotationAdapter.class)
        final JsonAdapterAnnotationOnFieldsTest.Part part;

        private GadgetWithOptionalPart(JsonAdapterAnnotationOnFieldsTest.Part part) {
            this.part = part;
        }
    }

    /**
     * Regression test contributed through https://github.com/google/gson/issues/831
     */
    public void testNonPrimitiveFieldAnnotationTakesPrecedenceOverDefault() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.GadgetWithOptionalPart(new JsonAdapterAnnotationOnFieldsTest.Part("foo")));
        TestCase.assertEquals("{\"part\":\"PartJsonFieldAnnotationAdapter\"}", json);
        JsonAdapterAnnotationOnFieldsTest.GadgetWithOptionalPart gadget = gson.fromJson("{'part':'foo'}", JsonAdapterAnnotationOnFieldsTest.GadgetWithOptionalPart.class);
        TestCase.assertEquals("PartJsonFieldAnnotationAdapter", gadget.part.name);
    }

    /**
     * Regression test contributed through https://github.com/google/gson/issues/831
     */
    public void testPrimitiveFieldAnnotationTakesPrecedenceOverDefault() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.GadgetWithPrimitivePart(42));
        TestCase.assertEquals("{\"part\":\"42\"}", json);
        JsonAdapterAnnotationOnFieldsTest.GadgetWithPrimitivePart gadget = gson.fromJson(json, JsonAdapterAnnotationOnFieldsTest.GadgetWithPrimitivePart.class);
        TestCase.assertEquals(42, gadget.part);
    }

    private static final class GadgetWithPrimitivePart {
        @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.LongToStringTypeAdapterFactory.class)
        final long part;

        private GadgetWithPrimitivePart(long part) {
            this.part = part;
        }
    }

    private static final class LongToStringTypeAdapterFactory implements TypeAdapterFactory {
        static final TypeAdapter<Long> ADAPTER = new TypeAdapter<Long>() {
            @Override
            public void write(JsonWriter out, Long value) throws IOException {
                out.value(value.toString());
            }

            @Override
            public Long read(JsonReader in) throws IOException {
                return in.nextLong();
            }
        };

        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
            Class<?> cls = type.getRawType();
            if (Long.class.isAssignableFrom(cls)) {
                return ((TypeAdapter<T>) (JsonAdapterAnnotationOnFieldsTest.LongToStringTypeAdapterFactory.ADAPTER));
            } else
                if (long.class.isAssignableFrom(cls)) {
                    return ((TypeAdapter<T>) (JsonAdapterAnnotationOnFieldsTest.LongToStringTypeAdapterFactory.ADAPTER));
                }

            throw new IllegalStateException((("Non-long field of type " + type) + " annotated with @JsonAdapter(LongToStringTypeAdapterFactory.class)"));
        }
    }

    public void testFieldAnnotationWorksForParameterizedType() {
        Gson gson = new Gson();
        String json = gson.toJson(new JsonAdapterAnnotationOnFieldsTest.Gizmo2(Arrays.asList(new JsonAdapterAnnotationOnFieldsTest.Part("Part"))));
        TestCase.assertEquals("{\"part\":\"GizmoPartTypeAdapterFactory\"}", json);
        JsonAdapterAnnotationOnFieldsTest.Gizmo2 computer = gson.fromJson("{'part':'Part'}", JsonAdapterAnnotationOnFieldsTest.Gizmo2.class);
        TestCase.assertEquals("GizmoPartTypeAdapterFactory", computer.part.get(0).name);
    }

    private static final class Gizmo2 {
        @JsonAdapter(JsonAdapterAnnotationOnFieldsTest.Gizmo2PartTypeAdapterFactory.class)
        List<JsonAdapterAnnotationOnFieldsTest.Part> part;

        Gizmo2(List<JsonAdapterAnnotationOnFieldsTest.Part> part) {
            this.part = part;
        }
    }

    private static class Gizmo2PartTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    out.value("GizmoPartTypeAdapterFactory");
                }

                @SuppressWarnings("unchecked")
                @Override
                public T read(JsonReader in) throws IOException {
                    in.nextString();
                    return ((T) (Arrays.asList(new JsonAdapterAnnotationOnFieldsTest.Part("GizmoPartTypeAdapterFactory"))));
                }
            };
        }
    }
}

