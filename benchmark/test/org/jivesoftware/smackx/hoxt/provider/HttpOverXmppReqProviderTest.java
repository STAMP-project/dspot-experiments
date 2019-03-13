/**
 * Copyright 2014 Andriy Tsykholyas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.hoxt.provider;


import HttpMethod.GET;
import HttpMethod.OPTIONS;
import org.jivesoftware.smackx.hoxt.packet.HttpOverXmppReq;
import org.junit.Assert;
import org.junit.Test;


public class HttpOverXmppReqProviderTest {
    @Test
    public void areAllReqAttributesCorrectlyParsed() throws Exception {
        String string = "<req xmlns='urn:xmpp:http' method='OPTIONS' resource='*' version='1.1'/>";
        HttpOverXmppReq req = HttpOverXmppReqProviderTest.parseReq(string);
        Assert.assertEquals(req.getVersion(), "1.1");
        Assert.assertEquals(req.getMethod(), OPTIONS);
        Assert.assertEquals(req.getResource(), "*");
    }

    @Test
    public void areGetRequestAttributesCorrectlyParsed() throws Exception {
        String string = "<req xmlns='urn:xmpp:http' method='GET' resource='/rdf/xep' version='1.1'/>";
        HttpOverXmppReq req = HttpOverXmppReqProviderTest.parseReq(string);
        Assert.assertEquals(req.getVersion(), "1.1");
        Assert.assertEquals(req.getMethod(), GET);
        Assert.assertEquals(req.getResource(), "/rdf/xep");
    }

    @Test
    public void getReqOptionAttributesCorrectlyParsed() throws Exception {
        String string = "<req xmlns='urn:xmpp:http' method='OPTIONS' resource='*' version='1.1' maxChunkSize='256' sipub='false' ibb='true' jingle='false'/>";
        HttpOverXmppReq req = HttpOverXmppReqProviderTest.parseReq(string);
        Assert.assertEquals(req.getMaxChunkSize(), 256);
        Assert.assertEquals(req.isSipub(), false);
        Assert.assertEquals(req.isIbb(), true);
        Assert.assertEquals(req.isJingle(), false);
    }

    @Test
    public void getReqOptionalAttributesDefaultValues() throws Exception {
        String string = "<req xmlns='urn:xmpp:http' method='OPTIONS' resource='*' version='1.1'/>";
        HttpOverXmppReq req = HttpOverXmppReqProviderTest.parseReq(string);
        Assert.assertEquals(req.isSipub(), true);
        Assert.assertEquals(req.isIbb(), true);
        Assert.assertEquals(req.isJingle(), true);
    }
}

