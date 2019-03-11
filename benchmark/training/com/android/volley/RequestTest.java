/**
 * Copyright (C) 2011 The Android Open Source Project
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
package com.android.volley;


import Request.Method.GET;
import com.android.volley.Request.Priority;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class RequestTest {
    @Test
    public void compareTo() {
        int sequence = 0;
        RequestTest.TestRequest low = new RequestTest.TestRequest(Priority.LOW);
        setSequence((sequence++));
        RequestTest.TestRequest low2 = new RequestTest.TestRequest(Priority.LOW);
        setSequence((sequence++));
        RequestTest.TestRequest high = new RequestTest.TestRequest(Priority.HIGH);
        setSequence((sequence++));
        RequestTest.TestRequest immediate = new RequestTest.TestRequest(Priority.IMMEDIATE);
        setSequence((sequence++));
        // "Low" should sort higher because it's really processing order.
        Assert.assertTrue(((low.compareTo(high)) > 0));
        Assert.assertTrue(((high.compareTo(low)) < 0));
        Assert.assertTrue(((low.compareTo(low2)) < 0));
        Assert.assertTrue(((low.compareTo(immediate)) > 0));
        Assert.assertTrue(((immediate.compareTo(high)) < 0));
    }

    private class TestRequest extends Request<Object> {
        private Priority mPriority = Priority.NORMAL;

        public TestRequest(Priority priority) {
            super(GET, "", null);
            mPriority = priority;
        }

        @Override
        public Priority getPriority() {
            return mPriority;
        }

        @Override
        protected void deliverResponse(Object response) {
        }

        @Override
        protected Response<Object> parseNetworkResponse(NetworkResponse response) {
            return null;
        }
    }

    @Test
    public void urlParsing() {
        RequestTest.UrlParseRequest nullUrl = new RequestTest.UrlParseRequest(null);
        Assert.assertEquals(0, getTrafficStatsTag());
        RequestTest.UrlParseRequest emptyUrl = new RequestTest.UrlParseRequest("");
        Assert.assertEquals(0, getTrafficStatsTag());
        RequestTest.UrlParseRequest noHost = new RequestTest.UrlParseRequest("http:///");
        Assert.assertEquals(0, getTrafficStatsTag());
        RequestTest.UrlParseRequest badProtocol = new RequestTest.UrlParseRequest("bad:http://foo");
        Assert.assertEquals(0, getTrafficStatsTag());
        RequestTest.UrlParseRequest goodProtocol = new RequestTest.UrlParseRequest("http://foo");
        Assert.assertFalse((0 == (getTrafficStatsTag())));
    }

    private class UrlParseRequest extends Request<Object> {
        public UrlParseRequest(String url) {
            super(GET, url, null);
        }

        @Override
        protected void deliverResponse(Object response) {
        }

        @Override
        protected Response<Object> parseNetworkResponse(NetworkResponse response) {
            return null;
        }
    }
}

