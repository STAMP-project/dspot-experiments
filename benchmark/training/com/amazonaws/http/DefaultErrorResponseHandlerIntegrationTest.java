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
package com.amazonaws.http;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.util.LogCaptor;
import java.util.ArrayList;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import utils.http.WireMockTestBase;


public class DefaultErrorResponseHandlerIntegrationTest extends WireMockTestBase {
    private static final String RESOURCE = "/some-path";

    private LogCaptor logCaptor = new LogCaptor.DefaultLogCaptor(Level.DEBUG);

    private final AmazonHttpClient client = new AmazonHttpClient(new ClientConfiguration());

    private final DefaultErrorResponseHandler sut = new DefaultErrorResponseHandler(new ArrayList<com.amazonaws.transform.Unmarshaller<com.amazonaws.AmazonServiceException, org.w3c.dom.Node>>());

    @Test
    public void invocationIdIsCapturedInTheLog() throws Exception {
        stubFor(get(urlPathEqualTo(DefaultErrorResponseHandlerIntegrationTest.RESOURCE)).willReturn(aResponse().withStatus(418)));
        executeRequest();
        Matcher<Iterable<? super LoggingEvent>> matcher = Matchers.hasItem(hasEventWithContent("Invocation Id"));
        Assert.assertThat(debugEvents(), matcher);
    }

    @Test
    public void invalidXmlLogsXmlContentToDebug() throws Exception {
        String content = RandomStringUtils.randomAlphanumeric(10);
        stubFor(get(urlPathEqualTo(DefaultErrorResponseHandlerIntegrationTest.RESOURCE)).willReturn(aResponse().withStatus(418).withBody(content)));
        executeRequest();
        Matcher<Iterable<? super LoggingEvent>> matcher = Matchers.hasItem(hasEventWithContent(content));
        Assert.assertThat(debugEvents(), matcher);
    }

    @Test
    public void requestIdIsLoggedWithDebugIfInTheHeader() throws Exception {
        String requestId = RandomStringUtils.randomAlphanumeric(10);
        stubFor(get(urlPathEqualTo(DefaultErrorResponseHandlerIntegrationTest.RESOURCE)).willReturn(aResponse().withStatus(418).withHeader(HttpResponseHandler.X_AMZN_REQUEST_ID_HEADER, requestId)));
        executeRequest();
        Matcher<Iterable<? super LoggingEvent>> matcher = Matchers.hasItem(hasEventWithContent(requestId));
        Assert.assertThat(debugEvents(), matcher);
    }
}

