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
package com.google.gson;


import com.google.gson.common.MoreAsserts;
import junit.framework.TestCase;


/**
 * Unit test for the {@link JsonObject} class.
 *
 * @author Joel Leitch
 */
public class JsonObjectTest extends TestCase {
    public void testAddingAndRemovingObjectProperties() throws Exception {
        JsonObject jsonObj = new JsonObject();
        String propertyName = "property";
        TestCase.assertFalse(jsonObj.has(propertyName));
        TestCase.assertNull(jsonObj.get(propertyName));
        JsonPrimitive value = new JsonPrimitive("blah");
        jsonObj.add(propertyName, value);
        TestCase.assertEquals(value, jsonObj.get(propertyName));
        JsonElement removedElement = jsonObj.remove(propertyName);
        TestCase.assertEquals(value, removedElement);
        TestCase.assertFalse(jsonObj.has(propertyName));
        TestCase.assertNull(jsonObj.get(propertyName));
    }

    public void testAddingNullPropertyValue() throws Exception {
        String propertyName = "property";
        JsonObject jsonObj = new JsonObject();
        jsonObj.add(propertyName, null);
        TestCase.assertTrue(jsonObj.has(propertyName));
        JsonElement jsonElement = jsonObj.get(propertyName);
        TestCase.assertNotNull(jsonElement);
        TestCase.assertTrue(jsonElement.isJsonNull());
    }

    public void testAddingNullOrEmptyPropertyName() throws Exception {
        JsonObject jsonObj = new JsonObject();
        try {
            jsonObj.add(null, JsonNull.INSTANCE);
            TestCase.fail("Should not allow null property names.");
        } catch (NullPointerException expected) {
        }
        jsonObj.add("", JsonNull.INSTANCE);
        jsonObj.add("   \t", JsonNull.INSTANCE);
    }

    public void testAddingBooleanProperties() throws Exception {
        String propertyName = "property";
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(propertyName, true);
        TestCase.assertTrue(jsonObj.has(propertyName));
        JsonElement jsonElement = jsonObj.get(propertyName);
        TestCase.assertNotNull(jsonElement);
        TestCase.assertTrue(jsonElement.getAsBoolean());
    }

    public void testAddingStringProperties() throws Exception {
        String propertyName = "property";
        String value = "blah";
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(propertyName, value);
        TestCase.assertTrue(jsonObj.has(propertyName));
        JsonElement jsonElement = jsonObj.get(propertyName);
        TestCase.assertNotNull(jsonElement);
        TestCase.assertEquals(value, jsonElement.getAsString());
    }

    public void testAddingCharacterProperties() throws Exception {
        String propertyName = "property";
        char value = 'a';
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(propertyName, value);
        TestCase.assertTrue(jsonObj.has(propertyName));
        JsonElement jsonElement = jsonObj.get(propertyName);
        TestCase.assertNotNull(jsonElement);
        TestCase.assertEquals(String.valueOf(value), jsonElement.getAsString());
        TestCase.assertEquals(value, jsonElement.getAsCharacter());
    }

    /**
     * From bug report http://code.google.com/p/google-gson/issues/detail?id=182
     */
    public void testPropertyWithQuotes() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("a\"b", new JsonPrimitive("c\"d"));
        String json = new Gson().toJson(jsonObj);
        TestCase.assertEquals("{\"a\\\"b\":\"c\\\"d\"}", json);
    }

    /**
     * From issue 227.
     */
    public void testWritePropertyWithEmptyStringName() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("", new JsonPrimitive(true));
        TestCase.assertEquals("{\"\":true}", new Gson().toJson(jsonObj));
    }

    public void testReadPropertyWithEmptyStringName() {
        JsonObject jsonObj = new JsonParser().parse("{\"\":true}").getAsJsonObject();
        TestCase.assertEquals(true, jsonObj.get("").getAsBoolean());
    }

    public void testEqualsOnEmptyObject() {
        MoreAsserts.assertEqualsAndHashCode(new JsonObject(), new JsonObject());
    }

    public void testEqualsNonEmptyObject() {
        JsonObject a = new JsonObject();
        JsonObject b = new JsonObject();
        TestCase.assertEquals(a, a);
        a.add("foo", new JsonObject());
        TestCase.assertFalse(a.equals(b));
        TestCase.assertFalse(b.equals(a));
        b.add("foo", new JsonObject());
        MoreAsserts.assertEqualsAndHashCode(a, b);
        a.add("bar", new JsonObject());
        TestCase.assertFalse(a.equals(b));
        TestCase.assertFalse(b.equals(a));
        b.add("bar", JsonNull.INSTANCE);
        TestCase.assertFalse(a.equals(b));
        TestCase.assertFalse(b.equals(a));
    }

    public void testSize() {
        JsonObject o = new JsonObject();
        TestCase.assertEquals(0, o.size());
        o.add("Hello", new JsonPrimitive(1));
        TestCase.assertEquals(1, o.size());
        o.add("Hi", new JsonPrimitive(1));
        TestCase.assertEquals(2, o.size());
        o.remove("Hello");
        TestCase.assertEquals(1, o.size());
    }

    public void testDeepCopy() {
        JsonObject original = new JsonObject();
        JsonArray firstEntry = new JsonArray();
        original.add("key", firstEntry);
        JsonObject copy = original.deepCopy();
        firstEntry.add(new JsonPrimitive("z"));
        TestCase.assertEquals(1, original.get("key").getAsJsonArray().size());
        TestCase.assertEquals(0, copy.get("key").getAsJsonArray().size());
    }

    /**
     * From issue 941
     */
    public void testKeySet() {
        JsonObject a = new JsonObject();
        a.add("foo", new JsonArray());
        a.add("bar", new JsonObject());
        TestCase.assertEquals(2, a.size());
        TestCase.assertEquals(2, keySet().size());
        TestCase.assertTrue(keySet().contains("foo"));
        TestCase.assertTrue(keySet().contains("bar"));
    }
}

