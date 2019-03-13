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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.common.TestTypes;
import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Functional Test exercising custom deserialization only. When test applies to both
 * serialization and deserialization then add it to CustomTypeAdapterTest.
 *
 * @author Joel Leitch
 */
public class CustomDeserializerTest extends TestCase {
    private static final String DEFAULT_VALUE = "test123";

    private static final String SUFFIX = "blah";

    private Gson gson;

    public void testDefaultConstructorNotCalledOnObject() throws Exception {
        CustomDeserializerTest.DataHolder data = new CustomDeserializerTest.DataHolder(CustomDeserializerTest.DEFAULT_VALUE);
        String json = gson.toJson(data);
        CustomDeserializerTest.DataHolder actual = gson.fromJson(json, CustomDeserializerTest.DataHolder.class);
        TestCase.assertEquals(((CustomDeserializerTest.DEFAULT_VALUE) + (CustomDeserializerTest.SUFFIX)), actual.getData());
    }

    public void testDefaultConstructorNotCalledOnField() throws Exception {
        CustomDeserializerTest.DataHolderWrapper dataWrapper = new CustomDeserializerTest.DataHolderWrapper(new CustomDeserializerTest.DataHolder(CustomDeserializerTest.DEFAULT_VALUE));
        String json = gson.toJson(dataWrapper);
        CustomDeserializerTest.DataHolderWrapper actual = gson.fromJson(json, CustomDeserializerTest.DataHolderWrapper.class);
        TestCase.assertEquals(((CustomDeserializerTest.DEFAULT_VALUE) + (CustomDeserializerTest.SUFFIX)), actual.getWrappedData().getData());
    }

    private static class DataHolder {
        private final String data;

        // For use by Gson
        @SuppressWarnings("unused")
        private DataHolder() {
            throw new IllegalStateException();
        }

        public DataHolder(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

    private static class DataHolderWrapper {
        private final CustomDeserializerTest.DataHolder wrappedData;

        // For use by Gson
        @SuppressWarnings("unused")
        private DataHolderWrapper() {
            this(new CustomDeserializerTest.DataHolder(CustomDeserializerTest.DEFAULT_VALUE));
        }

        public DataHolderWrapper(CustomDeserializerTest.DataHolder data) {
            this.wrappedData = data;
        }

        public CustomDeserializerTest.DataHolder getWrappedData() {
            return wrappedData;
        }
    }

    private static class DataHolderDeserializer implements JsonDeserializer<CustomDeserializerTest.DataHolder> {
        @Override
        public CustomDeserializerTest.DataHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            String dataString = jsonObj.get("data").getAsString();
            return new CustomDeserializerTest.DataHolder((dataString + (CustomDeserializerTest.SUFFIX)));
        }
    }

    public void testJsonTypeFieldBasedDeserialization() {
        String json = "{field1:'abc',field2:'def',__type__:'SUB_TYPE1'}";
        Gson gson = new GsonBuilder().registerTypeAdapter(CustomDeserializerTest.MyBase.class, new JsonDeserializer<CustomDeserializerTest.MyBase>() {
            @Override
            public CustomDeserializerTest.MyBase deserialize(JsonElement json, Type pojoType, JsonDeserializationContext context) throws JsonParseException {
                String type = json.getAsJsonObject().get(CustomDeserializerTest.MyBase.TYPE_ACCESS).getAsString();
                return context.deserialize(json, CustomDeserializerTest.SubTypes.valueOf(type).getSubclass());
            }
        }).create();
        CustomDeserializerTest.SubType1 target = ((CustomDeserializerTest.SubType1) (gson.fromJson(json, CustomDeserializerTest.MyBase.class)));
        TestCase.assertEquals("abc", target.field1);
    }

    private static class MyBase {
        static final String TYPE_ACCESS = "__type__";
    }

    private enum SubTypes {

        SUB_TYPE1(CustomDeserializerTest.SubType1.class),
        SUB_TYPE2(CustomDeserializerTest.SubType2.class);
        private final Type subClass;

        private SubTypes(Type subClass) {
            this.subClass = subClass;
        }

        public Type getSubclass() {
            return subClass;
        }
    }

    private static class SubType1 extends CustomDeserializerTest.MyBase {
        String field1;
    }

    private static class SubType2 extends CustomDeserializerTest.MyBase {
        @SuppressWarnings("unused")
        String field2;
    }

    public void testCustomDeserializerReturnsNullForTopLevelObject() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
            @Override
            public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return null;
            }
        }).create();
        String json = "{baseName:'Base',subName:'SubRevised'}";
        TestTypes.Base target = gson.fromJson(json, TestTypes.Base.class);
        TestCase.assertNull(target);
    }

    public void testCustomDeserializerReturnsNull() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
            @Override
            public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return null;
            }
        }).create();
        String json = "{base:{baseName:'Base',subName:'SubRevised'}}";
        TestTypes.ClassWithBaseField target = gson.fromJson(json, TestTypes.ClassWithBaseField.class);
        TestCase.assertNull(target.base);
    }

    public void testCustomDeserializerReturnsNullForArrayElements() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
            @Override
            public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return null;
            }
        }).create();
        String json = "[{baseName:'Base'},{baseName:'Base'}]";
        TestTypes.Base[] target = gson.fromJson(json, TestTypes.Base[].class);
        TestCase.assertNull(target[0]);
        TestCase.assertNull(target[1]);
    }

    public void testCustomDeserializerReturnsNullForArrayElementsForArrayField() {
        Gson gson = new GsonBuilder().registerTypeAdapter(TestTypes.Base.class, new JsonDeserializer<TestTypes.Base>() {
            @Override
            public TestTypes.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return null;
            }
        }).create();
        String json = "{bases:[{baseName:'Base'},{baseName:'Base'}]}";
        CustomDeserializerTest.ClassWithBaseArray target = gson.fromJson(json, CustomDeserializerTest.ClassWithBaseArray.class);
        TestCase.assertNull(target.bases[0]);
        TestCase.assertNull(target.bases[1]);
    }

    private static final class ClassWithBaseArray {
        TestTypes.Base[] bases;
    }
}

