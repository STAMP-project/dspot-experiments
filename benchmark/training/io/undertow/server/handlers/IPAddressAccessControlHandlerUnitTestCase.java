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


import ResponseCodeHandler.HANDLE_404;
import StatusCodes.FORBIDDEN;
import StatusCodes.NOT_FOUND;
import io.undertow.server.handlers.builder.HandlerParser;
import io.undertow.testutils.category.UnitTest;
import io.undertow.util.StatusCodes;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for peer security handler
 *
 * @author Stuart Douglas
 */
@Category(UnitTest.class)
public class IPAddressAccessControlHandlerUnitTestCase {
    @Test
    public void testIPv4ExactMatch() throws UnknownHostException {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler().setDefaultAllow(false).addAllow("127.0.0.1");
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.1")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("127.0.0.2")));
    }

    @Test
    public void testIPv6ExactMatch() throws UnknownHostException {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler().setDefaultAllow(false).addAllow("FE45:00:00:000:0:AAA:FFFF:0045");
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("FE45:0:0:0:0:AAA:FFFF:45")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("127.0.0.2")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("FE45:0:0:0:0:AAA:FFFF:46")));
    }

    @Test
    public void testIPv4WildcardMatch() throws UnknownHostException {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler().setDefaultAllow(true).addAllow("127.0.0.1").addDeny("127.0.*.*");
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.1")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("127.0.0.2")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.1.0.2")));
    }

    @Test
    public void testIPv6PrefixMatch() throws UnknownHostException {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler().setDefaultAllow(true).addAllow("FE45:00:00:000:0:AAA:FFFF:0045").addDeny("FE45:00:00:000:0:AAA:FFFF:*");
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("FE45:0:0:0:0:AAA:FFFF:45")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.2")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("FE45:0:0:0:0:AAA:FFFF:46")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("FE45:0:0:0:0:AAA:FFFb:46")));
    }

    @Test
    public void testIPv4SlashMatch() throws UnknownHostException {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler().setDefaultAllow(true).addAllow("127.0.0.1").addAllow("127.0.0.48/30").addDeny("127.0.0.0/16");
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.1")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("127.0.0.2")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("127.0.1.1")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.1.0.2")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("127.0.0.47")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.48")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.49")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.50")));
        Assert.assertTrue(handler.isAllowed(InetAddress.getByName("127.0.0.51")));
        Assert.assertFalse(handler.isAllowed(InetAddress.getByName("127.0.0.52")));
    }

    @Test
    public void testIPv6SlashMatch() throws UnknownHostException {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler().setDefaultAllow(true).addAllow("FE45:00:00:000:0:AAA:FFFF:0045").addAllow("FE45:00:00:000:0:AAA:FFFF:01F4/127").addDeny("FE45:00:00:000:0:AAA:FFFF:0/112");
        runIpv6SlashMAtchTest(handler);
    }

    @Test
    public void testParsedHandler() throws UnknownHostException {
        IPAddressAccessControlHandler handler = ((IPAddressAccessControlHandler) (HandlerParser.parse("ip-access-control[default-allow=true, acl={'FE45:00:00:000:0:AAA:FFFF:0045 allow', 'FE45:00:00:000:0:AAA:FFFF:01F4/127 allow', 'FE45:00:00:000:0:AAA:FFFF:0/112 deny'}]", getClass().getClassLoader()).wrap(HANDLE_404)));
        runIpv6SlashMAtchTest(handler);
    }

    @Test
    public void testDefaultDenyResponseCode() {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler();
        Assert.assertEquals(FORBIDDEN, handler.getDenyResponseCode());
    }

    @Test
    public void testDenyResponseCode() {
        IPAddressAccessControlHandler handler = new IPAddressAccessControlHandler(null, StatusCodes.NOT_FOUND);
        Assert.assertEquals(NOT_FOUND, handler.getDenyResponseCode());
    }
}

