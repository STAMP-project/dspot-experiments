/**
 * Copyright (C) 2010 The Android Open Source Project
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
package org.json;


import junit.framework.TestCase;


/**
 * These tests checks self use calls. For the most part we doesn't attempt to
 * cover self-use, except in those cases where our clean room implementation
 * does it.
 *
 * <p>This black box test was written without inspecting the non-free org.json
 * sourcecode.
 */
public class SelfUseTest extends TestCase {
    private int objectPutCalls = 0;

    private int objectGetCalls = 0;

    private int objectOptCalls = 0;

    private int objectOptTypeCalls = 0;

    private int arrayPutCalls = 0;

    private int arrayGetCalls = 0;

    private int arrayOptCalls = 0;

    private int arrayOptTypeCalls = 0;

    private int tokenerNextCalls = 0;

    private int tokenerNextValueCalls = 0;

    private final JSONObject object = new JSONObject() {
        @Override
        public JSONObject put(String name, Object value) throws JSONException {
            (objectPutCalls)++;
            return super.put(name, value);
        }

        @Override
        public Object get(String name) throws JSONException {
            (objectGetCalls)++;
            return super.get(name);
        }

        @Override
        public Object opt(String name) {
            (objectOptCalls)++;
            return super.opt(name);
        }

        @Override
        public boolean optBoolean(String key, boolean defaultValue) {
            (objectOptTypeCalls)++;
            return super.optBoolean(key, defaultValue);
        }

        @Override
        public double optDouble(String key, double defaultValue) {
            (objectOptTypeCalls)++;
            return super.optDouble(key, defaultValue);
        }

        @Override
        public int optInt(String key, int defaultValue) {
            (objectOptTypeCalls)++;
            return super.optInt(key, defaultValue);
        }

        @Override
        public long optLong(String key, long defaultValue) {
            (objectOptTypeCalls)++;
            return super.optLong(key, defaultValue);
        }

        @Override
        public String optString(String key, String defaultValue) {
            (objectOptTypeCalls)++;
            return super.optString(key, defaultValue);
        }
    };

    private final JSONArray array = new JSONArray() {
        @Override
        public JSONArray put(int index, Object value) throws JSONException {
            (arrayPutCalls)++;
            return super.put(index, value);
        }

        @Override
        public Object get(int index) throws JSONException {
            (arrayGetCalls)++;
            return super.get(index);
        }

        @Override
        public Object opt(int index) {
            (arrayOptCalls)++;
            return super.opt(index);
        }

        @Override
        public boolean optBoolean(int index, boolean fallback) {
            (arrayOptTypeCalls)++;
            return super.optBoolean(index, fallback);
        }

        @Override
        public double optDouble(int index, double fallback) {
            (arrayOptTypeCalls)++;
            return super.optDouble(index, fallback);
        }

        @Override
        public long optLong(int index, long fallback) {
            (arrayOptTypeCalls)++;
            return super.optLong(index, fallback);
        }

        @Override
        public String optString(int index, String fallback) {
            (arrayOptTypeCalls)++;
            return super.optString(index, fallback);
        }

        @Override
        public int optInt(int index, int fallback) {
            (arrayOptTypeCalls)++;
            return super.optInt(index, fallback);
        }
    };

    private final JSONTokener tokener = new JSONTokener("{\"foo\": [true]}") {
        @Override
        public char next() {
            (tokenerNextCalls)++;
            return super.next();
        }

        @Override
        public Object nextValue() throws JSONException {
            (tokenerNextValueCalls)++;
            return super.nextValue();
        }
    };

    public void testObjectPut() throws JSONException {
        object.putOpt("foo", "bar");
        TestCase.assertEquals(1, objectPutCalls);
    }

    public void testObjectAccumulate() throws JSONException {
        object.accumulate("foo", "bar");
        TestCase.assertEquals(1, objectPutCalls);
    }

    public void testObjectGetBoolean() throws JSONException {
        object.put("foo", "true");
        object.getBoolean("foo");
        TestCase.assertEquals(1, objectGetCalls);
    }

    public void testObjectOptType() throws JSONException {
        object.optBoolean("foo");
        TestCase.assertEquals(1, objectOptCalls);
        TestCase.assertEquals(1, objectOptTypeCalls);
        object.optDouble("foo");
        TestCase.assertEquals(2, objectOptCalls);
        TestCase.assertEquals(2, objectOptTypeCalls);
        object.optInt("foo");
        TestCase.assertEquals(3, objectOptCalls);
        TestCase.assertEquals(3, objectOptTypeCalls);
        object.optLong("foo");
        TestCase.assertEquals(4, objectOptCalls);
        TestCase.assertEquals(4, objectOptTypeCalls);
        object.optString("foo");
        TestCase.assertEquals(5, objectOptCalls);
        TestCase.assertEquals(5, objectOptTypeCalls);
    }

    public void testToJSONArray() throws JSONException {
        object.put("foo", 5);
        object.put("bar", 10);
        array.put("foo");
        array.put("baz");
        array.put("bar");
        object.toJSONArray(array);
        TestCase.assertEquals(3, arrayOptCalls);
        TestCase.assertEquals(0, arrayOptTypeCalls);
        TestCase.assertEquals(3, objectOptCalls);
        TestCase.assertEquals(0, objectOptTypeCalls);
    }

    public void testPutAtIndex() throws JSONException {
        array.put(10, false);
        TestCase.assertEquals(1, arrayPutCalls);
    }

    public void testIsNull() {
        array.isNull(5);
        TestCase.assertEquals(1, arrayOptCalls);
    }

    public void testArrayGetType() throws JSONException {
        array.put(true);
        array.getBoolean(0);
        TestCase.assertEquals(1, arrayGetCalls);
    }

    public void testArrayOptType() throws JSONException {
        array.optBoolean(3);
        TestCase.assertEquals(1, arrayOptCalls);
        TestCase.assertEquals(1, arrayOptTypeCalls);
        array.optDouble(3);
        TestCase.assertEquals(2, arrayOptCalls);
        TestCase.assertEquals(2, arrayOptTypeCalls);
        array.optInt(3);
        TestCase.assertEquals(3, arrayOptCalls);
        TestCase.assertEquals(3, arrayOptTypeCalls);
        array.optLong(3);
        TestCase.assertEquals(4, arrayOptCalls);
        TestCase.assertEquals(4, arrayOptTypeCalls);
        array.optString(3);
        TestCase.assertEquals(5, arrayOptCalls);
        TestCase.assertEquals(5, arrayOptTypeCalls);
    }

    public void testToJSONObject() throws JSONException {
        array.put("foo");
        array.put("baz");
        array.put("bar");
        JSONArray values = new JSONArray();
        values.put(5.5);
        values.put(11.0);
        values.put(30);
        values.toJSONObject(array);
        TestCase.assertEquals(3, arrayOptCalls);
        TestCase.assertEquals(0, arrayOptTypeCalls);
    }

    public void testNextExpecting() throws JSONException {
        tokener.next('{');
        TestCase.assertEquals(1, tokenerNextCalls);
        tokener.next('\"');
        TestCase.assertEquals(2, tokenerNextCalls);
    }

    public void testNextValue() throws JSONException {
        tokener.nextValue();
        TestCase.assertEquals(4, tokenerNextValueCalls);
    }
}

