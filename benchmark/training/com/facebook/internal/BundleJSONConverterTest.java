/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.internal;


import android.os.Bundle;
import com.facebook.FacebookTestCase;
import com.facebook.TestUtils;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


public class BundleJSONConverterTest extends FacebookTestCase {
    @Test
    public void testSimpleValues() throws JSONException {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("1st");
        arrayList.add("2nd");
        arrayList.add("third");
        Bundle innerBundle1 = new Bundle();
        innerBundle1.putInt("inner", 1);
        Bundle innerBundle2 = new Bundle();
        innerBundle2.putString("inner", "2");
        innerBundle2.putStringArray("deep list", new String[]{ "7", "8" });
        innerBundle1.putBundle("nested bundle", innerBundle2);
        Bundle b = new Bundle();
        b.putBoolean("boolValue", true);
        b.putInt("intValue", 7);
        b.putLong("longValue", 5000000000L);
        b.putDouble("doubleValue", 3.14);
        b.putString("stringValue", "hello world");
        b.putStringArray("stringArrayValue", new String[]{ "first", "second" });
        b.putStringArrayList("stringArrayListValue", arrayList);
        b.putBundle("nested", innerBundle1);
        JSONObject json = BundleJSONConverter.convertToJSON(b);
        Assert.assertNotNull(json);
        Assert.assertEquals(true, json.getBoolean("boolValue"));
        Assert.assertEquals(7, json.getInt("intValue"));
        Assert.assertEquals(5000000000L, json.getLong("longValue"));
        Assert.assertEquals(3.14, json.getDouble("doubleValue"), TestUtils.DOUBLE_EQUALS_DELTA);
        Assert.assertEquals("hello world", json.getString("stringValue"));
        JSONArray jsonArray = json.getJSONArray("stringArrayValue");
        Assert.assertEquals(2, jsonArray.length());
        Assert.assertEquals("first", jsonArray.getString(0));
        Assert.assertEquals("second", jsonArray.getString(1));
        jsonArray = json.getJSONArray("stringArrayListValue");
        Assert.assertEquals(3, jsonArray.length());
        Assert.assertEquals("1st", jsonArray.getString(0));
        Assert.assertEquals("2nd", jsonArray.getString(1));
        Assert.assertEquals("third", jsonArray.getString(2));
        JSONObject innerJson = json.getJSONObject("nested");
        Assert.assertEquals(1, innerJson.getInt("inner"));
        innerJson = innerJson.getJSONObject("nested bundle");
        Assert.assertEquals("2", innerJson.getString("inner"));
        jsonArray = innerJson.getJSONArray("deep list");
        Assert.assertEquals(2, jsonArray.length());
        Assert.assertEquals("7", jsonArray.getString(0));
        Assert.assertEquals("8", jsonArray.getString(1));
        Bundle finalBundle = BundleJSONConverter.convertToBundle(json);
        Assert.assertNotNull(finalBundle);
        Assert.assertEquals(true, finalBundle.getBoolean("boolValue"));
        Assert.assertEquals(7, finalBundle.getInt("intValue"));
        Assert.assertEquals(5000000000L, finalBundle.getLong("longValue"));
        Assert.assertEquals(3.14, finalBundle.getDouble("doubleValue"), TestUtils.DOUBLE_EQUALS_DELTA);
        Assert.assertEquals("hello world", finalBundle.getString("stringValue"));
        List<String> stringList = finalBundle.getStringArrayList("stringArrayValue");
        Assert.assertEquals(2, stringList.size());
        Assert.assertEquals("first", stringList.get(0));
        Assert.assertEquals("second", stringList.get(1));
        stringList = finalBundle.getStringArrayList("stringArrayListValue");
        Assert.assertEquals(3, stringList.size());
        Assert.assertEquals("1st", stringList.get(0));
        Assert.assertEquals("2nd", stringList.get(1));
        Assert.assertEquals("third", stringList.get(2));
        Bundle finalInnerBundle = finalBundle.getBundle("nested");
        Assert.assertEquals(1, finalInnerBundle.getInt("inner"));
        finalBundle = finalInnerBundle.getBundle("nested bundle");
        Assert.assertEquals("2", finalBundle.getString("inner"));
        stringList = finalBundle.getStringArrayList("deep list");
        Assert.assertEquals(2, stringList.size());
        Assert.assertEquals("7", stringList.get(0));
        Assert.assertEquals("8", stringList.get(1));
    }

    @Test
    public void testUnsupportedValues() throws JSONException {
        Bundle b = new Bundle();
        b.putShort("shortValue", ((short) (7)));
        boolean exceptionCaught = false;
        try {
            BundleJSONConverter.convertToJSON(b);
        } catch (IllegalArgumentException a) {
            exceptionCaught = true;
        }
        Assert.assertTrue(exceptionCaught);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(10);
        JSONObject json = new JSONObject();
        json.put("arrayValue", jsonArray);
        exceptionCaught = false;
        try {
            BundleJSONConverter.convertToBundle(json);
        } catch (IllegalArgumentException a) {
            exceptionCaught = true;
        }
        Assert.assertTrue(exceptionCaught);
    }
}

