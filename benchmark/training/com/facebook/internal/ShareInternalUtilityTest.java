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


import com.facebook.FacebookTestCase;
import com.facebook.share.internal.ShareInternalUtility;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class ShareInternalUtilityTest extends FacebookTestCase {
    @Test
    public void testRemoveNamespaceFromNullOGJsonObject() {
        Assert.assertNull(ShareInternalUtility.removeNamespacesFromOGJsonObject(null, false));
    }

    @Test
    public void testRemoveNamespaceFromComplexOGJsonObject() {
        try {
            JSONObject testObject = ShareInternalUtilityTest.getJsonOGActionTestObject();
            testObject = ShareInternalUtility.removeNamespacesFromOGJsonObject(testObject, false);
            JSONObject expectedResult = ShareInternalUtilityTest.getJsonOGActionTestObjectWithoutNamespace();
            if (!(simpleJsonObjComparer(testObject, expectedResult))) {
                Assert.fail(String.format(Locale.ROOT, "Actual: %s\nExpected: %s", testObject.toString(), expectedResult.toString()));
            }
        } catch (JSONException ex) {
            // Fail
            Assert.assertNotNull(ex);
        }
    }

    @Test
    public void testJsonSerializationOfOpenGraph() {
        String placeId = "1";
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder().setAction(new ShareOpenGraphAction.Builder().putStringArrayList("tags", new ArrayList<String>() {
            {
                add("2");
                add("4");
            }
        }).build()).setPeopleIds(new ArrayList<String>() {
            {
                add("1");
                add("1");
                add("2");
                add("3");
            }
        }).setPlaceId(placeId).build();
        try {
            JSONObject object = ShareInternalUtility.toJSONObjectForCall(null, content);
            List<String> peopleIds = Utility.jsonArrayToStringList(object.getJSONArray("tags"));
            Assert.assertEquals(4, peopleIds.size());
            for (int i = 1; i < 5; ++i) {
                Assert.assertTrue(peopleIds.contains(Integer.valueOf(i).toString()));
            }
            Assert.assertEquals(placeId, object.getString("place"));
        } catch (JSONException ex) {
            // Fail
            Assert.assertNotNull(ex);
            return;
        }
    }

    @Test
    public void testJsonSerializationOfOpenGraphExistingPlace() {
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder().setAction(new ShareOpenGraphAction.Builder().putString("place", "1").build()).setPlaceId("2").build();
        try {
            JSONObject object = ShareInternalUtility.toJSONObjectForCall(null, content);
            Assert.assertEquals("1", object.getString("place"));
        } catch (JSONException ex) {
            // Fail
            Assert.assertNotNull(ex);
            return;
        }
    }
}

