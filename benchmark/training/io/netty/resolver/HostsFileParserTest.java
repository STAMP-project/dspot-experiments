/**
 * Copyright 2015 The Netty Project
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
package io.netty.resolver;


import CharsetUtil.ISO_8859_1;
import CharsetUtil.UTF_8;
import io.netty.util.internal.ResourcesUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class HostsFileParserTest {
    @Test
    public void testParse() throws IOException {
        String hostsString = // should be ignored since we have the uppercase host already
        // uppercase host, should match lowercase host
        // host mapped to a second address, must be ignored
        // multiple aliases
        // comment after hostname
        // comment in the middle of the line
        // comment at the beginning of the line
        // single hostname, separated with tabs
        // empty line
        // same as above, but IPv6
        // single hostname, separated with blanks
        new StringBuilder().append("127.0.0.1 host1").append("\n").append("::1 host1").append("\n").append("\n").append("192.168.0.1\thost2").append("\n").append("#comment").append("\n").append(" #comment  ").append("\n").append("192.168.0.2  host3  #comment").append("\n").append("192.168.0.3  host4  host5 host6").append("\n").append("192.168.0.4  host4").append("\n").append("192.168.0.5  HOST7").append("\n").append("192.168.0.6  host7").append("\n").toString();
        HostsFileEntries entries = HostsFileParser.parse(new BufferedReader(new StringReader(hostsString)));
        Map<String, Inet4Address> inet4Entries = entries.inet4Entries();
        Map<String, Inet6Address> inet6Entries = entries.inet6Entries();
        Assert.assertEquals("Expected 7 IPv4 entries", 7, inet4Entries.size());
        Assert.assertEquals("Expected 1 IPv6 entries", 1, inet6Entries.size());
        Assert.assertEquals("127.0.0.1", inet4Entries.get("host1").getHostAddress());
        Assert.assertEquals("192.168.0.1", inet4Entries.get("host2").getHostAddress());
        Assert.assertEquals("192.168.0.2", inet4Entries.get("host3").getHostAddress());
        Assert.assertEquals("192.168.0.3", inet4Entries.get("host4").getHostAddress());
        Assert.assertEquals("192.168.0.3", inet4Entries.get("host5").getHostAddress());
        Assert.assertEquals("192.168.0.3", inet4Entries.get("host6").getHostAddress());
        Assert.assertNotNull("uppercase host doesn't resolve", inet4Entries.get("host7"));
        Assert.assertEquals("192.168.0.5", inet4Entries.get("host7").getHostAddress());
        Assert.assertEquals("0:0:0:0:0:0:0:1", inet6Entries.get("host1").getHostAddress());
    }

    @Test
    public void testParseUnicode() throws IOException {
        final Charset unicodeCharset;
        try {
            unicodeCharset = Charset.forName("unicode");
        } catch (UnsupportedCharsetException e) {
            Assume.assumeNoException(e);
            return;
        }
        HostsFileParserTest.testParseFile(HostsFileParser.parse(ResourcesUtil.getFile(getClass(), "hosts-unicode"), unicodeCharset));
    }

    @Test
    public void testParseMultipleCharsets() throws IOException {
        final Charset unicodeCharset;
        try {
            unicodeCharset = Charset.forName("unicode");
        } catch (UnsupportedCharsetException e) {
            Assume.assumeNoException(e);
            return;
        }
        HostsFileParserTest.testParseFile(HostsFileParser.parse(ResourcesUtil.getFile(getClass(), "hosts-unicode"), UTF_8, ISO_8859_1, unicodeCharset));
    }
}

