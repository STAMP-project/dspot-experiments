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
import com.android.volley.mock.TestRequest;
import com.android.volley.toolbox.HttpClientStack.HttpPatch;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class HttpClientStackTest {
    @Test
    public void createDeprecatedGetRequest() throws Exception {
        TestRequest.DeprecatedGet request = new TestRequest.DeprecatedGet();
        Assert.assertEquals(getMethod(), DEPRECATED_GET_OR_POST);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpGet));
    }

    @Test
    public void createDeprecatedPostRequest() throws Exception {
        TestRequest.DeprecatedPost request = new TestRequest.DeprecatedPost();
        Assert.assertEquals(getMethod(), DEPRECATED_GET_OR_POST);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpPost));
    }

    @Test
    public void createGetRequest() throws Exception {
        TestRequest.Get request = new TestRequest.Get();
        Assert.assertEquals(getMethod(), GET);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpGet));
    }

    @Test
    public void createPostRequest() throws Exception {
        TestRequest.Post request = new TestRequest.Post();
        Assert.assertEquals(getMethod(), POST);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpPost));
    }

    @Test
    public void createPostRequestWithBody() throws Exception {
        TestRequest.PostWithBody request = new TestRequest.PostWithBody();
        Assert.assertEquals(getMethod(), POST);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpPost));
    }

    @Test
    public void createPutRequest() throws Exception {
        TestRequest.Put request = new TestRequest.Put();
        Assert.assertEquals(getMethod(), PUT);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpPut));
    }

    @Test
    public void createPutRequestWithBody() throws Exception {
        TestRequest.PutWithBody request = new TestRequest.PutWithBody();
        Assert.assertEquals(getMethod(), PUT);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpPut));
    }

    @Test
    public void createDeleteRequest() throws Exception {
        TestRequest.Delete request = new TestRequest.Delete();
        Assert.assertEquals(getMethod(), DELETE);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpDelete));
    }

    @Test
    public void createHeadRequest() throws Exception {
        TestRequest.Head request = new TestRequest.Head();
        Assert.assertEquals(getMethod(), HEAD);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpHead));
    }

    @Test
    public void createOptionsRequest() throws Exception {
        TestRequest.Options request = new TestRequest.Options();
        Assert.assertEquals(getMethod(), OPTIONS);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpOptions));
    }

    @Test
    public void createTraceRequest() throws Exception {
        TestRequest.Trace request = new TestRequest.Trace();
        Assert.assertEquals(getMethod(), TRACE);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpTrace));
    }

    @Test
    public void createPatchRequest() throws Exception {
        TestRequest.Patch request = new TestRequest.Patch();
        Assert.assertEquals(getMethod(), PATCH);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpPatch));
    }

    @Test
    public void createPatchRequestWithBody() throws Exception {
        TestRequest.PatchWithBody request = new TestRequest.PatchWithBody();
        Assert.assertEquals(getMethod(), PATCH);
        HttpUriRequest httpRequest = HttpClientStack.createHttpRequest(request, null);
        Assert.assertTrue((httpRequest instanceof HttpPatch));
    }
}

