/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.util.matcher;


import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Luke Taylor
 */
public class IpAddressMatcherTests {
    final IpAddressMatcher v6matcher = new IpAddressMatcher("fe80::21f:5bff:fe33:bd68");

    final IpAddressMatcher v4matcher = new IpAddressMatcher("192.168.1.104");

    MockHttpServletRequest ipv4Request = new MockHttpServletRequest();

    MockHttpServletRequest ipv6Request = new MockHttpServletRequest();

    @Test
    public void ipv6MatcherMatchesIpv6Address() {
        assertThat(v6matcher.matches(ipv6Request)).isTrue();
    }

    @Test
    public void ipv6MatcherDoesntMatchIpv4Address() {
        assertThat(v6matcher.matches(ipv4Request)).isFalse();
    }

    @Test
    public void ipv4MatcherMatchesIpv4Address() {
        assertThat(v4matcher.matches(ipv4Request)).isTrue();
    }

    @Test
    public void ipv4SubnetMatchesCorrectly() throws Exception {
        IpAddressMatcher matcher = new IpAddressMatcher("192.168.1.0/24");
        assertThat(matcher.matches(ipv4Request)).isTrue();
        matcher = new IpAddressMatcher("192.168.1.128/25");
        assertThat(matcher.matches(ipv4Request)).isFalse();
        ipv4Request.setRemoteAddr("192.168.1.159");// 159 = 0x9f

        assertThat(matcher.matches(ipv4Request)).isTrue();
    }

    @Test
    public void ipv6RangeMatches() throws Exception {
        IpAddressMatcher matcher = new IpAddressMatcher("2001:DB8::/48");
        assertThat(matcher.matches("2001:DB8:0:0:0:0:0:0")).isTrue();
        assertThat(matcher.matches("2001:DB8:0:0:0:0:0:1")).isTrue();
        assertThat(matcher.matches("2001:DB8:0:FFFF:FFFF:FFFF:FFFF:FFFF")).isTrue();
        assertThat(matcher.matches("2001:DB8:1:0:0:0:0:0")).isFalse();
    }

    // SEC-1733
    @Test
    public void zeroMaskMatchesAnything() throws Exception {
        IpAddressMatcher matcher = new IpAddressMatcher("0.0.0.0/0");
        assertThat(matcher.matches("123.4.5.6")).isTrue();
        assertThat(matcher.matches("192.168.0.159")).isTrue();
        matcher = new IpAddressMatcher("192.168.0.159/0");
        assertThat(matcher.matches("123.4.5.6")).isTrue();
        assertThat(matcher.matches("192.168.0.159")).isTrue();
    }
}

