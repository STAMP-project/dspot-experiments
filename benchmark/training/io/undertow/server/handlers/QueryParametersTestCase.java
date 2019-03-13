/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers;


import UndertowOptions.URL_CHARSET;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.ParameterLimitException;
import io.undertow.util.URLUtils;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.OptionMap;


/**
 * Tests that query parameters are handled correctly.
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class QueryParametersTestCase {
    @Test
    public void testQueryParameters() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            runTest(client, "{unicode=>I?t?rn?ti?n?li??ti?n}", "/path?unicode=I?t?rn?ti?n?li??ti?n");
            runTest(client, "{a=>b,value=>bb bb}", "/path?a=b&value=bb%20bb");
            runTest(client, "{a=>b,value=>[bb,cc]}", "/path?a=b&value=bb&value=cc");
            runTest(client, "{a=>b,s =>,t =>,value=>[bb,cc]}", "/path?a=b&value=bb&value=cc&s%20&t%20");
            runTest(client, "{a=>b,s =>,t =>,value=>[bb,cc]}", "/path?a=b&value=bb&value=cc&s%20&t%20&");
            runTest(client, "{a=>b,s =>,t =>,u=>,value=>[bb,cc]}", "/path?a=b&value=bb&value=cc&s%20&t%20&u");
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    @ProxyIgnore
    public void testQueryParametersShiftJIS() throws IOException {
        OptionMap old = DefaultServer.getUndertowOptions();
        try {
            DefaultServer.setUndertowOptions(OptionMap.create(URL_CHARSET, "Shift_JIS"));
            TestHttpClient client = new TestHttpClient();
            try {
                runTest(client, "{unicode=>???}", "/path?unicode=%83e%83X%83g");
            } finally {
                client.getConnectionManager().shutdown();
            }
        } finally {
            DefaultServer.setUndertowOptions(old);
        }
    }

    @Test
    @ProxyIgnore
    public void testQueryParameterParsingIncorrectlyEncodedURI() throws ParameterLimitException, IOException {
        StringBuilder out = new StringBuilder();
        out.append(((char) (199)));
        out.append(((char) (209)));
        out.append(((char) (37)));
        out.append(((char) (50)));
        out.append(((char) (48)));
        out.append(((char) (177)));
        out.append(((char) (219)));
        String s = "p=" + (out.toString());
        HttpServerExchange exchange = new HttpServerExchange(null);
        URLUtils.parseQueryString(s, exchange, "MS949", true, 1000);
        Assert.assertEquals("? ?", exchange.getQueryParameters().get("p").getFirst());
    }
}

