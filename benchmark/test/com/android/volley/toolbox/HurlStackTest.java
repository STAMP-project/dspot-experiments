/**
 * Copyright (C) 2012 The Android Open Source Project
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
package com.android.volley.toolbox;


import Method.DELETE;
import Method.DEPRECATED_GET_OR_POST;
import Method.GET;
import Method.HEAD;
import Method.OPTIONS;
import Method.PATCH;
import Method.POST;
import Method.PUT;
import Method.TRACE;
import com.android.volley.mock.MockHttpURLConnection;
import com.android.volley.mock.TestRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class HurlStackTest {
    private MockHttpURLConnection mMockConnection;

    @Test
    public void connectionForDeprecatedGetRequest() throws Exception {
        TestRequest.DeprecatedGet request = new TestRequest.DeprecatedGet();
        Assert.assertEquals(getMethod(), DEPRECATED_GET_OR_POST);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("GET", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForDeprecatedPostRequest() throws Exception {
        TestRequest.DeprecatedPost request = new TestRequest.DeprecatedPost();
        Assert.assertEquals(getMethod(), DEPRECATED_GET_OR_POST);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("POST", mMockConnection.getRequestMethod());
        Assert.assertTrue(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForGetRequest() throws Exception {
        TestRequest.Get request = new TestRequest.Get();
        Assert.assertEquals(getMethod(), GET);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("GET", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForPostRequest() throws Exception {
        TestRequest.Post request = new TestRequest.Post();
        Assert.assertEquals(getMethod(), POST);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("POST", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForPostWithBodyRequest() throws Exception {
        TestRequest.PostWithBody request = new TestRequest.PostWithBody();
        Assert.assertEquals(getMethod(), POST);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("POST", mMockConnection.getRequestMethod());
        Assert.assertTrue(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForPutRequest() throws Exception {
        TestRequest.Put request = new TestRequest.Put();
        Assert.assertEquals(getMethod(), PUT);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("PUT", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForPutWithBodyRequest() throws Exception {
        TestRequest.PutWithBody request = new TestRequest.PutWithBody();
        Assert.assertEquals(getMethod(), PUT);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("PUT", mMockConnection.getRequestMethod());
        Assert.assertTrue(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForDeleteRequest() throws Exception {
        TestRequest.Delete request = new TestRequest.Delete();
        Assert.assertEquals(getMethod(), DELETE);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("DELETE", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForHeadRequest() throws Exception {
        TestRequest.Head request = new TestRequest.Head();
        Assert.assertEquals(getMethod(), HEAD);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("HEAD", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForOptionsRequest() throws Exception {
        TestRequest.Options request = new TestRequest.Options();
        Assert.assertEquals(getMethod(), OPTIONS);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("OPTIONS", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForTraceRequest() throws Exception {
        TestRequest.Trace request = new TestRequest.Trace();
        Assert.assertEquals(getMethod(), TRACE);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("TRACE", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForPatchRequest() throws Exception {
        TestRequest.Patch request = new TestRequest.Patch();
        Assert.assertEquals(getMethod(), PATCH);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("PATCH", mMockConnection.getRequestMethod());
        Assert.assertFalse(mMockConnection.getDoOutput());
    }

    @Test
    public void connectionForPatchWithBodyRequest() throws Exception {
        TestRequest.PatchWithBody request = new TestRequest.PatchWithBody();
        Assert.assertEquals(getMethod(), PATCH);
        HurlStack.setConnectionParametersForRequest(mMockConnection, request);
        Assert.assertEquals("PATCH", mMockConnection.getRequestMethod());
        Assert.assertTrue(mMockConnection.getDoOutput());
    }
}

