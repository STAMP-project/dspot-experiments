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
import com.google.gson.JsonObject;
import junit.framework.TestCase;


/**
 * Functional tests for print formatting.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class PrintFormattingTest extends TestCase {
    private Gson gson;

    public void testJsonObjectWithNullValues() {
        JsonObject obj = new JsonObject();
        obj.addProperty("field1", "value1");
        obj.addProperty("field2", ((String) (null)));
        String json = gson.toJson(obj);
        TestCase.assertTrue(json.contains("field1"));
        TestCase.assertFalse(json.contains("field2"));
    }

    public void testJsonObjectWithNullValuesSerialized() {
        gson = new GsonBuilder().serializeNulls().create();
        JsonObject obj = new JsonObject();
        obj.addProperty("field1", "value1");
        obj.addProperty("field2", ((String) (null)));
        String json = gson.toJson(obj);
        TestCase.assertTrue(json.contains("field1"));
        TestCase.assertTrue(json.contains("field2"));
    }
}

