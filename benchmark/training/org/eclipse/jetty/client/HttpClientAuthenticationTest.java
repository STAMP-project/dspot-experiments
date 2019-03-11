/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.client;


import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;
import org.eclipse.jetty.client.api.Authentication.HeaderInfo;
import org.eclipse.jetty.client.api.ContentProvider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpClientAuthenticationTest extends AbstractHttpClientServerTest {
    private String realm = "TestRealm";

    private static class GeneratingContentProvider implements ContentProvider {
        private static final ByteBuffer DONE = ByteBuffer.allocate(0);

        private final IntFunction<ByteBuffer> generator;

        private GeneratingContentProvider(IntFunction<ByteBuffer> generator) {
            this.generator = generator;
        }

        @Override
        public long getLength() {
            return -1;
        }

        @Override
        public boolean isReproducible() {
            return true;
        }

        @Override
        public Iterator<ByteBuffer> iterator() {
            return new Iterator<ByteBuffer>() {
                private int index;

                public ByteBuffer current;

                @Override
                @SuppressWarnings("ReferenceEquality")
                public boolean hasNext() {
                    if ((current) == null) {
                        current = generator.apply(((index)++));
                        if ((current) == null)
                            current = HttpClientAuthenticationTest.GeneratingContentProvider.DONE;

                    }
                    return (current) != (HttpClientAuthenticationTest.GeneratingContentProvider.DONE);
                }

                @Override
                public ByteBuffer next() {
                    ByteBuffer result = current;
                    current = null;
                    if (result == null)
                        throw new NoSuchElementException();

                    return result;
                }
            };
        }
    }

    @Test
    public void testTestHeaderInfoParsing() {
        AuthenticationProtocolHandler aph = new WWWAuthenticationProtocolHandler(client);
        HeaderInfo headerInfo = aph.getHeaderInfo("Digest realm=\"thermostat\", qop=\"auth\", nonce=\"1523430383\"").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth", headerInfo.getParameter("qop"));
        Assertions.assertEquals("thermostat", headerInfo.getParameter("realm"));
        Assertions.assertEquals("1523430383", headerInfo.getParameter("nonce"));
        headerInfo = aph.getHeaderInfo("Digest qop=\"auth\", realm=\"thermostat\", nonce=\"1523430383\"").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth", headerInfo.getParameter("qop"));
        Assertions.assertEquals("thermostat", headerInfo.getParameter("realm"));
        Assertions.assertEquals("1523430383", headerInfo.getParameter("nonce"));
        headerInfo = aph.getHeaderInfo("Digest qop=\"auth\", nonce=\"1523430383\", realm=\"thermostat\"").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth", headerInfo.getParameter("qop"));
        Assertions.assertEquals("thermostat", headerInfo.getParameter("realm"));
        Assertions.assertEquals("1523430383", headerInfo.getParameter("nonce"));
        headerInfo = aph.getHeaderInfo("Digest qop=\"auth\", nonce=\"1523430383\"").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth", headerInfo.getParameter("qop"));
        Assertions.assertNull(headerInfo.getParameter("realm"));
        Assertions.assertEquals("1523430383", headerInfo.getParameter("nonce"));
        // test multiple authentications
        List<HeaderInfo> headerInfoList = aph.getHeaderInfo(("Digest qop=\"auth\", realm=\"thermostat\", nonce=\"1523430383\", " + (("Digest realm=\"thermostat2\", qop=\"auth2\", nonce=\"4522530354\", " + "Digest qop=\"auth3\", nonce=\"9523570528\", realm=\"thermostat3\", ") + "Digest qop=\"auth4\", nonce=\"3526435321\"")));
        Assertions.assertTrue(headerInfoList.get(0).getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth", headerInfoList.get(0).getParameter("qop"));
        Assertions.assertEquals("thermostat", headerInfoList.get(0).getParameter("realm"));
        Assertions.assertEquals("1523430383", headerInfoList.get(0).getParameter("nonce"));
        Assertions.assertTrue(headerInfoList.get(1).getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth2", headerInfoList.get(1).getParameter("qop"));
        Assertions.assertEquals("thermostat2", headerInfoList.get(1).getParameter("realm"));
        Assertions.assertEquals("4522530354", headerInfoList.get(1).getParameter("nonce"));
        Assertions.assertTrue(headerInfoList.get(2).getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth3", headerInfoList.get(2).getParameter("qop"));
        Assertions.assertEquals("thermostat3", headerInfoList.get(2).getParameter("realm"));
        Assertions.assertEquals("9523570528", headerInfoList.get(2).getParameter("nonce"));
        Assertions.assertTrue(headerInfoList.get(3).getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth4", headerInfoList.get(3).getParameter("qop"));
        Assertions.assertNull(headerInfoList.get(3).getParameter("realm"));
        Assertions.assertEquals("3526435321", headerInfoList.get(3).getParameter("nonce"));
        List<HeaderInfo> headerInfos = aph.getHeaderInfo("Newauth realm=\"apps\", type=1, title=\"Login to \\\"apps\\\"\", Basic realm=\"simple\"");
        Assertions.assertTrue(headerInfos.get(0).getType().equalsIgnoreCase("Newauth"));
        Assertions.assertEquals("apps", headerInfos.get(0).getParameter("realm"));
        Assertions.assertEquals("1", headerInfos.get(0).getParameter("type"));
        Assertions.assertEquals(headerInfos.get(0).getParameter("title"), "Login to \"apps\"");
        Assertions.assertTrue(headerInfos.get(1).getType().equalsIgnoreCase("Basic"));
        Assertions.assertEquals("simple", headerInfos.get(1).getParameter("realm"));
    }

    @Test
    public void testTestHeaderInfoParsingUnusualCases() {
        AuthenticationProtocolHandler aph = new WWWAuthenticationProtocolHandler(client);
        HeaderInfo headerInfo = aph.getHeaderInfo("Scheme").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Scheme"));
        Assertions.assertNull(headerInfo.getParameter("realm"));
        List<HeaderInfo> headerInfos = aph.getHeaderInfo("Scheme1    ,    Scheme2        ,      Scheme3");
        Assertions.assertEquals(3, headerInfos.size());
        Assertions.assertTrue(headerInfos.get(0).getType().equalsIgnoreCase("Scheme1"));
        Assertions.assertTrue(headerInfos.get(1).getType().equalsIgnoreCase("Scheme2"));
        Assertions.assertTrue(headerInfos.get(2).getType().equalsIgnoreCase("Scheme3"));
        headerInfo = aph.getHeaderInfo("Scheme name=\"value\", other=\"value2\"").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Scheme"));
        Assertions.assertEquals("value", headerInfo.getParameter("name"));
        Assertions.assertEquals("value2", headerInfo.getParameter("other"));
        headerInfo = aph.getHeaderInfo("Scheme   name   = value   , other   =  \"value2\"    ").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Scheme"));
        Assertions.assertEquals("value", headerInfo.getParameter("name"));
        Assertions.assertEquals("value2", headerInfo.getParameter("other"));
        headerInfos = aph.getHeaderInfo(", , , ,  ,,,Scheme name=value, ,,Scheme2   name=value2,,  ,,");
        Assertions.assertEquals(headerInfos.size(), 2);
        Assertions.assertTrue(headerInfos.get(0).getType().equalsIgnoreCase("Scheme"));
        Assertions.assertEquals("value", headerInfos.get(0).getParameter("nAmE"));
        Assertions.assertTrue(headerInfos.get(1).getType().equalsIgnoreCase("Scheme2"));
        headerInfos = aph.getHeaderInfo("Scheme name=value, Scheme2   name=value2");
        Assertions.assertEquals(headerInfos.size(), 2);
        Assertions.assertTrue(headerInfos.get(0).getType().equalsIgnoreCase("Scheme"));
        Assertions.assertEquals("value", headerInfos.get(0).getParameter("nAmE"));
        MatcherAssert.assertThat(headerInfos.get(1).getType(), Matchers.equalToIgnoringCase("Scheme2"));
        Assertions.assertEquals("value2", headerInfos.get(1).getParameter("nAmE"));
        headerInfos = aph.getHeaderInfo("Scheme ,   ,, ,, name=value, Scheme2 name=value2");
        Assertions.assertEquals(headerInfos.size(), 2);
        Assertions.assertTrue(headerInfos.get(0).getType().equalsIgnoreCase("Scheme"));
        Assertions.assertEquals("value", headerInfos.get(0).getParameter("name"));
        Assertions.assertTrue(headerInfos.get(1).getType().equalsIgnoreCase("Scheme2"));
        Assertions.assertEquals("value2", headerInfos.get(1).getParameter("name"));
        // Negotiate with base64 Content
        headerInfo = aph.getHeaderInfo("Negotiate TlRMTVNTUAABAAAAB4IIogAAAAAAAAAAAAAAAAAAAAAFAs4OAAAADw==").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Negotiate"));
        Assertions.assertEquals("TlRMTVNTUAABAAAAB4IIogAAAAAAAAAAAAAAAAAAAAAFAs4OAAAADw==", headerInfo.getBase64());
        headerInfos = aph.getHeaderInfo(("Negotiate TlRMTVNTUAABAAAAAAAAAFAs4OAAAADw==, " + "Negotiate YIIJvwYGKwYBBQUCoIIJszCCCa+gJDAi="));
        Assertions.assertTrue(headerInfos.get(0).getType().equalsIgnoreCase("Negotiate"));
        Assertions.assertEquals("TlRMTVNTUAABAAAAAAAAAFAs4OAAAADw==", headerInfos.get(0).getBase64());
        Assertions.assertTrue(headerInfos.get(1).getType().equalsIgnoreCase("Negotiate"));
        Assertions.assertEquals("YIIJvwYGKwYBBQUCoIIJszCCCa+gJDAi=", headerInfos.get(1).getBase64());
    }

    @Test
    public void testEqualsInParam() {
        AuthenticationProtocolHandler aph = new WWWAuthenticationProtocolHandler(client);
        HeaderInfo headerInfo;
        headerInfo = aph.getHeaderInfo("Digest realm=\"=the=rmo=stat=\", qop=\"=a=u=t=h=\", nonce=\"=1523430383=\"").get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("=a=u=t=h=", headerInfo.getParameter("qop"));
        Assertions.assertEquals("=the=rmo=stat=", headerInfo.getParameter("realm"));
        Assertions.assertEquals("=1523430383=", headerInfo.getParameter("nonce"));
        // test multiple authentications
        List<HeaderInfo> headerInfoList = aph.getHeaderInfo(("Digest qop=\"=au=th=\", realm=\"=ther=mostat=\", nonce=\"=152343=0383=\", " + ("Digest realm=\"=thermostat2\", qop=\"=auth2\", nonce=\"=4522530354\", " + "Digest qop=\"auth3=\", nonce=\"9523570528=\", realm=\"thermostat3=\", ")));
        Assertions.assertTrue(headerInfoList.get(0).getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("=au=th=", headerInfoList.get(0).getParameter("qop"));
        Assertions.assertEquals("=ther=mostat=", headerInfoList.get(0).getParameter("realm"));
        Assertions.assertEquals("=152343=0383=", headerInfoList.get(0).getParameter("nonce"));
        Assertions.assertTrue(headerInfoList.get(1).getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("=auth2", headerInfoList.get(1).getParameter("qop"));
        Assertions.assertEquals("=thermostat2", headerInfoList.get(1).getParameter("realm"));
        Assertions.assertEquals("=4522530354", headerInfoList.get(1).getParameter("nonce"));
        Assertions.assertTrue(headerInfoList.get(2).getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals("auth3=", headerInfoList.get(2).getParameter("qop"));
        Assertions.assertEquals("thermostat3=", headerInfoList.get(2).getParameter("realm"));
        Assertions.assertEquals("9523570528=", headerInfoList.get(2).getParameter("nonce"));
    }

    @Test
    public void testSingleChallengeLooksLikeMultipleChallenges() {
        AuthenticationProtocolHandler aph = new WWWAuthenticationProtocolHandler(client);
        List<HeaderInfo> headerInfoList = aph.getHeaderInfo("Digest param=\",f \"");
        Assertions.assertEquals(1, headerInfoList.size());
        headerInfoList = aph.getHeaderInfo("Digest realm=\"thermostat\", qop=\",Digest realm=hello\", nonce=\"1523430383=\"");
        Assertions.assertEquals(1, headerInfoList.size());
        HeaderInfo headerInfo = headerInfoList.get(0);
        Assertions.assertTrue(headerInfo.getType().equalsIgnoreCase("Digest"));
        Assertions.assertEquals(",Digest realm=hello", headerInfo.getParameter("qop"));
        Assertions.assertEquals("thermostat", headerInfo.getParameter("realm"));
        Assertions.assertEquals(headerInfo.getParameter("nonce"), "1523430383=");
    }
}

