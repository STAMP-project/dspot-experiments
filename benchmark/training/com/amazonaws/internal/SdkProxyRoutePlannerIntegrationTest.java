/**
 * Copyright 2016-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal;


import com.amazonaws.http.MockServerTestBase;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;
import org.junit.Test;


/**
 * This class starts a mock proxy server, and once a request is sent to this mock proxy server,
 * a 200 OK will be returned. We'll take advantage of this returned status code to test whether
 * a request to the given fake service host is passed through the proxy or not.
 */
public class SdkProxyRoutePlannerIntegrationTest extends MockServerTestBase {
    private static final String FOO_FAKE_SERVICE_HOST_PREFIX = UUID.randomUUID().toString();

    private static final String FOO_FAKE_SERVICE_HOST = (SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST_PREFIX) + ".com";

    private static final String BAR_FAKE_SERVICE_HOST = (UUID.randomUUID().toString()) + ".com";

    private static final String BAZ_FAKE_SERVICE_HOST = (UUID.randomUUID().toString()) + ".com";

    @Test
    public void nonProxyHostsNull_fakeHost() throws IOException {
        mockSuccessfulRequest(null, SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST);
    }

    @Test
    public void nonProxyHostEmpty_fakeHost() throws IOException {
        mockSuccessfulRequest("", SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST);
    }

    @Test(expected = UnknownHostException.class)
    public void nonProxyHostsNotNull_fakeHostDoesMatch() throws Exception {
        mockUnsuccessfulRequest(SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST, SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST);
    }

    @Test
    public void nonProxyHostsNotNull_fakeHostDoesNotMatch() throws IOException {
        mockSuccessfulRequest(SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST, SdkProxyRoutePlannerIntegrationTest.BAR_FAKE_SERVICE_HOST);
    }

    @Test(expected = UnknownHostException.class)
    public void nonProxyHostsWithWildcardPrefix_fakeHostDoesMatch() throws Exception {
        mockUnsuccessfulRequest("*.com", SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST);
    }

    @Test
    public void nonProxyHostsWithWildcardPrefix_fakeHostDoesNotMatch() throws IOException {
        mockSuccessfulRequest("*.org", SdkProxyRoutePlannerIntegrationTest.BAR_FAKE_SERVICE_HOST);
    }

    @Test(expected = UnknownHostException.class)
    public void nonProxyHostsWithWildcardSuffix_fakeHostDoesMatch() throws Exception {
        mockUnsuccessfulRequest(((SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST_PREFIX) + ".*"), SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST);
    }

    @Test
    public void nonProxyHostsWithWildcardSuffix_fakeHostDoesNotMatch() throws IOException {
        mockSuccessfulRequest(((SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST_PREFIX) + ".*"), SdkProxyRoutePlannerIntegrationTest.BAR_FAKE_SERVICE_HOST);
    }

    @Test(expected = UnknownHostException.class)
    public void nonProxyHostsWithOrSign_fakeHostDoesMatch() throws Exception {
        mockUnsuccessfulRequest((((SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST) + "|") + (SdkProxyRoutePlannerIntegrationTest.BAR_FAKE_SERVICE_HOST)), SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST);
    }

    @Test
    public void nonProxyHostsWithOrSign_fakeHostDoesNotMatch() throws IOException {
        mockSuccessfulRequest((((SdkProxyRoutePlannerIntegrationTest.FOO_FAKE_SERVICE_HOST) + "|") + (SdkProxyRoutePlannerIntegrationTest.BAZ_FAKE_SERVICE_HOST)), SdkProxyRoutePlannerIntegrationTest.BAR_FAKE_SERVICE_HOST);
    }
}

