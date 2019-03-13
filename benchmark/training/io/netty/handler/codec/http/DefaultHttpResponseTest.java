/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import org.junit.Assert;
import org.junit.Test;

import static HttpResponseStatus.NOT_FOUND;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;


public class DefaultHttpResponseTest {
    @Test
    public void testNotEquals() {
        HttpResponse ok = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpResponse notFound = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);
        Assert.assertNotEquals(ok, notFound);
        Assert.assertNotEquals(ok.hashCode(), notFound.hashCode());
    }

    @Test
    public void testEquals() {
        HttpResponse ok = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpResponse ok2 = new DefaultHttpResponse(HTTP_1_1, OK);
        Assert.assertEquals(ok, ok2);
        Assert.assertEquals(ok.hashCode(), ok2.hashCode());
    }
}

