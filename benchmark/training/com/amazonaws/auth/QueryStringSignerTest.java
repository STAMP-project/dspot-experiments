/**
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.auth;


import com.amazonaws.SignableRequest;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class QueryStringSignerTest {
    private static final QueryStringSigner signer = new QueryStringSigner();

    private static final AWSCredentials credentials = new BasicAWSCredentials("123456789", "123456789");

    private static final String EXPECTED_SIGNATURE = "VjYMhf9TWp08zAxXbKDAvUhW9GjJ56QjAuSj3LBsfjM=";

    static {
        Calendar c = new GregorianCalendar();
        c.clear();
        c.set(1981, 1, 16, 6, 30, 0);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        QueryStringSignerTest.signer.overrideDate(c.getTime());
    }

    @Test
    public void testRequestResourcePath() throws Exception {
        SignableRequest<?> request = MockRequestBuilder.create().withEndpoint("http://foo.amazon.com").withParameter("foo", "bar").withPath("foo/bar").build();
        QueryStringSignerTest.signer.sign(request, QueryStringSignerTest.credentials);
        assertSignature(QueryStringSignerTest.EXPECTED_SIGNATURE, request.getParameters());
    }

    @Test
    public void testRequestAndEndpointResourcePath() throws Exception {
        SignableRequest<?> request = MockRequestBuilder.create().withEndpoint("http://foo.amazon.com/foo").withParameter("foo", "bar").withPath("/bar").build();
        QueryStringSignerTest.signer.sign(request, QueryStringSignerTest.credentials);
        assertSignature(QueryStringSignerTest.EXPECTED_SIGNATURE, request.getParameters());
    }

    @Test
    public void testRequestAndEndpointResourcePathNoSlash() throws Exception {
        SignableRequest<?> request = MockRequestBuilder.create().withEndpoint("http://foo.amazon.com/foo").withParameter("foo", "bar").withPath("bar").build();
        QueryStringSignerTest.signer.sign(request, QueryStringSignerTest.credentials);
        assertSignature(QueryStringSignerTest.EXPECTED_SIGNATURE, request.getParameters());
    }

    @Test
    public void testAnonymous() throws Exception {
        SignableRequest<?> request = MockRequestBuilder.create().withEndpoint("http://foo.amazon.com").withParameter("foo", "bar").withPath("bar").build();
        QueryStringSignerTest.signer.sign(request, new AnonymousAWSCredentials());
        Assert.assertNull(request.getParameters().get("Signature"));
    }
}

