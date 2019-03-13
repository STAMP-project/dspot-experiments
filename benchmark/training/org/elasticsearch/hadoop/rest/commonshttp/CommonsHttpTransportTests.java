/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.rest.commonshttp;


import ConfigurationOptions.ES_HTTP_TIMEOUT;
import Request.Method;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.util.TestSettings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CommonsHttpTransportTests {
    /**
     * See <a href="https://tools.ietf.org/html/rfc5735">RFC 5735 Section 3:</a>
     *
     * 192.0.2.0/24 - This block is assigned as "TEST-NET-1" for use in
     * documentation and example code.  It is often used in conjunction with
     * domain names example.com or example.net in vendor and protocol
     * documentation.  As described in [RFC5737], addresses within this
     * block do not legitimately appear on the public Internet and can be
     * used without any coordination with IANA or an Internet registry.  See
     * [RFC1166].
     */
    public static final String TEST_NET_1 = "192.0.2.0";

    private Protocol original;

    @Test
    public void testProtocolReplacement() throws Exception {
        final ProtocolSocketFactory socketFactory = getSocketFactory();
        CommonsHttpTransport.replaceProtocol(socketFactory, "https", 443);
        Protocol protocol = Protocol.getProtocol("https");
        MatcherAssert.assertThat(protocol, Matchers.instanceOf(DelegatedProtocol.class));
        DelegatedProtocol delegatedProtocol = ((DelegatedProtocol) (protocol));
        MatcherAssert.assertThat(delegatedProtocol.getSocketFactory(), Matchers.sameInstance(socketFactory));
        MatcherAssert.assertThat(delegatedProtocol.getOriginal(), Matchers.sameInstance(original));
        // ensure we do not re-wrap a delegated protocol
        CommonsHttpTransport.replaceProtocol(socketFactory, "https", 443);
        protocol = Protocol.getProtocol("https");
        MatcherAssert.assertThat(protocol, Matchers.instanceOf(DelegatedProtocol.class));
        delegatedProtocol = ((DelegatedProtocol) (protocol));
        MatcherAssert.assertThat(delegatedProtocol.getSocketFactory(), Matchers.sameInstance(socketFactory));
        MatcherAssert.assertThat(delegatedProtocol.getOriginal(), Matchers.sameInstance(original));
    }

    @Test
    public void testTimeout() {
        Settings testSettings = new TestSettings();
        testSettings.setProperty(ES_HTTP_TIMEOUT, "3s");
        String garbageHost = (CommonsHttpTransportTests.TEST_NET_1) + ":80";
        long maxTime = 3000L + 1000L;// 5s plus some buffer

        long startTime = System.currentTimeMillis();
        try {
            CommonsHttpTransport transport = new CommonsHttpTransport(testSettings, garbageHost);
            transport.execute(new org.elasticsearch.hadoop.rest.SimpleRequest(Method.GET, null, "/"));
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long took = endTime - startTime;
            MatcherAssert.assertThat("Connection Timeout not respected", took, Matchers.lessThan(maxTime));
            return;
        }
        Assert.fail("Should not be able to connect to TEST_NET_1");
    }
}

