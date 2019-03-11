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
import com.google.gson.common.TestTypes;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 * Functional tests that do not fall neatly into any of the existing classification.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class UncategorizedTest extends TestCase {
    private Gson gson = null;

    public void testInvalidJsonDeserializationFails() throws Exception {
        try {
            gson.fromJson("adfasdf1112,,,\":", TestTypes.BagOfPrimitives.class);
            TestCase.fail("Bad JSON should throw a ParseException");
        } catch (JsonParseException expected) {
        }
        try {
            gson.fromJson("{adfasdf1112,,,\":}", TestTypes.BagOfPrimitives.class);
            TestCase.fail("Bad JSON should throw a ParseException");
        } catch (JsonParseException expected) {
        }
    }

    public void testObjectEqualButNotSameSerialization() throws Exception {
        TestTypes.ClassOverridingEquals objA = new TestTypes.ClassOverridingEquals();
        TestTypes.ClassOverridingEquals objB = new TestTypes.ClassOverridingEquals();
        objB.ref = objA;
        String json = gson.toJson(objB);
        TestCase.assertEquals(objB.getExpectedJson(), json);
    }

    public void testStaticFieldsAreNotSerialized() {
        TestTypes.BagOfPrimitives target = new TestTypes.BagOfPrimitives();
        TestCase.assertFalse(gson.toJson(target).contains("DEFAULT_VALUE"));
    }

    public void testGsonInstanceReusableForSerializationAndDeserialization() {
        TestTypes.BagOfPrimitives bag = new TestTypes.BagOfPrimitives();
        String json = gson.toJson(bag);
        TestTypes.BagOfPrimitives deserialized = gson.fromJson(json, TestTypes.BagOfPrimitives.class);
        TestCase.assertEquals(bag, deserialized);
    }

    /**
     * This test ensures that a custom deserializer is able to return a derived class instance for a
     * base class object. For a motivation for this test, see Issue 37 and
     * http://groups.google.com/group/google-gson/browse_thread/thread/677d56e9976d7761
     */
    public void testReturningDerivedClassesDuringDeserialization() {
        Gson gson = new GsonBuilder().registerTypeAdapter(UncategorizedTest.Base.class, new UncategorizedTest.BaseTypeAdapter()).create();
        String json = "{\"opType\":\"OP1\"}";
        UncategorizedTest.Base base = gson.fromJson(json, UncategorizedTest.Base.class);
        TestCase.assertTrue((base instanceof UncategorizedTest.Derived1));
        TestCase.assertEquals(UncategorizedTest.OperationType.OP1, base.opType);
        json = "{\"opType\":\"OP2\"}";
        base = gson.fromJson(json, UncategorizedTest.Base.class);
        TestCase.assertTrue((base instanceof UncategorizedTest.Derived2));
        TestCase.assertEquals(UncategorizedTest.OperationType.OP2, base.opType);
    }

    /**
     * Test that trailing whitespace is ignored.
     * http://code.google.com/p/google-gson/issues/detail?id=302
     */
    public void testTrailingWhitespace() throws Exception {
        List<Integer> integers = gson.fromJson("[1,2,3]  \n\n  ", new TypeToken<List<Integer>>() {}.getType());
        TestCase.assertEquals(Arrays.asList(1, 2, 3), integers);
    }

    private enum OperationType {

        OP1,
        OP2;}

    private static class Base {
        UncategorizedTest.OperationType opType;
    }

    private static class Derived1 extends UncategorizedTest.Base {
        Derived1() {
            opType = UncategorizedTest.OperationType.OP1;
        }
    }

    private static class Derived2 extends UncategorizedTest.Base {
        Derived2() {
            opType = UncategorizedTest.OperationType.OP2;
        }
    }

    private static class BaseTypeAdapter implements JsonDeserializer<UncategorizedTest.Base> {
        @Override
        public UncategorizedTest.Base deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String opTypeStr = json.getAsJsonObject().get("opType").getAsString();
            UncategorizedTest.OperationType opType = UncategorizedTest.OperationType.valueOf(opTypeStr);
            switch (opType) {
                case OP1 :
                    return new UncategorizedTest.Derived1();
                case OP2 :
                    return new UncategorizedTest.Derived2();
            }
            throw new JsonParseException(("unknown type: " + json));
        }
    }
}

